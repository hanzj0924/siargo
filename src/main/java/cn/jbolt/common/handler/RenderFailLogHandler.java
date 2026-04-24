package cn.jbolt.common.handler;

import com.jfinal.handler.Handler;
import com.jfinal.log.Log;

import cn.jbolt.core.consts.JBoltConst;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * renderFail 增强日志 Handler
 *
 * <p>通过包装 HttpServletResponse 的输出流，在响应内容写出时检测是否包含
 * renderFail 特征（JSON 中的 "state":false），从而实现对非异常触发的
 * renderFail 调用的日志记录。</p>
 *
 * <p>本方案可覆盖以下场景：
 * <ul>
 *   <li>JBoltOnlineUserGlobalInterceptor 会话过期直接调用 renderFail 并 return</li>
 *   <li>JBoltAdminAuthInterceptor 权限校验失败直接调用 renderFail 并 return</li>
 *   <li>业务 Controller 中主动调用 renderFail 的任何情形</li>
 * </ul>
 * </p>
 *
 * @author Siargo Team
 * @date 2026-04-24
 */
public class RenderFailLogHandler extends Handler {
    protected static final Log LOG = Log.getLog(RenderFailLogHandler.class);

    /** renderFail 输出的 JSON 特征字符串（JBolt 统一格式） */
    private static final String RENDER_FAIL_SIGNATURE = "\"state\":false";

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        // 静态资源直接放行，避免无谓包装
        if (isStaticResource(target)) {
            next.handle(target, request, response, isHandled);
            return;
        }

        // 记录请求入口信息（Handler 层还没有 actionKey，用 URI 代替）
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        CapturingResponseWrapper wrapper = new CapturingResponseWrapper(response);
        try {
            next.handle(target, request, wrapper, isHandled);
        } finally {
            // 只有在响应内容含有 renderFail 特征时才记录日志
            if (wrapper.isRenderFailDetected()) {
                String rqType = (String) request.getAttribute(JBoltConst.RQKEY_JB_RQTYPE);
                String failMsg = wrapper.getDetectedFailMsg();
                LOG.error("【renderFail拦截】URI: " + requestUri
                        + ", Method: " + requestMethod
                        + ", RequestType: " + (rqType != null ? rqType : "unknown")
                        + ", FailMsg: " + failMsg);
            }
        }
    }

    /**
     * 判断是否为静态资源请求（CSS/JS/图片等），与 JBoltBaseHandler 逻辑保持一致
     */
    private boolean isStaticResource(String target) {
        if (target == null) return false;
        int index = target.lastIndexOf('.');
        if (index == -1) return false;
        String suffix = target.substring(index).toLowerCase();
        return suffix.equals(".js") || suffix.equals(".css") || suffix.equals(".png")
                || suffix.equals(".jpg") || suffix.equals(".jpeg") || suffix.equals(".gif")
                || suffix.equals(".ico") || suffix.equals(".svg") || suffix.equals(".woff")
                || suffix.equals(".woff2") || suffix.equals(".ttf") || suffix.equals(".eot")
                || suffix.equals(".map");
    }

    // -------------------------------------------------------------------------
    // 内部类：响应包装器
    // -------------------------------------------------------------------------

    /**
     * 捕获响应输出的包装器。
     * 代理 PrintWriter 和 ServletOutputStream，在首次写入时检测 renderFail 特征。
     * 检测完成后直接透传，不缓存全部响应体，性能开销最小。
     */
    static class CapturingResponseWrapper extends HttpServletResponseWrapper {
        private volatile boolean renderFailDetected = false;
        private volatile String detectedFailMsg = "";

        private PrintWriter wrappedWriter;
        private ServletOutputStream wrappedStream;

        CapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        boolean isRenderFailDetected() {
            return renderFailDetected;
        }

        String getDetectedFailMsg() {
            return detectedFailMsg;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (wrappedWriter == null) {
                wrappedWriter = new DetectingPrintWriter(super.getWriter(), this);
            }
            return wrappedWriter;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (wrappedStream == null) {
                wrappedStream = new DetectingServletOutputStream(super.getOutputStream(), this);
            }
            return wrappedStream;
        }

        /**
         * 处理检测到的内容片段，解析 renderFail 特征
         */
        void onContentWritten(String content) {
            if (renderFailDetected || content == null) return;
            if (content.contains(RENDER_FAIL_SIGNATURE)) {
                renderFailDetected = true;
                // 提取 msg 字段内容
                detectedFailMsg = extractMsg(content);
            }
        }

        /**
         * 简单解析 JSON 中的 msg 字段值
         */
        private static String extractMsg(String json) {
            // 匹配 "msg":"..." 或 "msg":null
            int msgIdx = json.indexOf("\"msg\":");
            if (msgIdx == -1) msgIdx = json.indexOf("\"message\":");
            if (msgIdx == -1) return "(msg not found)";
            int colonIdx = json.indexOf(':', msgIdx);
            if (colonIdx == -1) return "(msg not found)";
            int start = colonIdx + 1;
            while (start < json.length() && json.charAt(start) == ' ') start++;
            if (start >= json.length()) return "(msg not found)";
            if (json.charAt(start) == '"') {
                int end = json.indexOf('"', start + 1);
                if (end == -1) return json.substring(start);
                return json.substring(start + 1, end);
            }
            // null 或数字
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }

    // -------------------------------------------------------------------------
    // 代理 PrintWriter
    // -------------------------------------------------------------------------

    static class DetectingPrintWriter extends PrintWriter {
        private final CapturingResponseWrapper wrapper;
        private boolean detected = false;

        DetectingPrintWriter(Writer out, CapturingResponseWrapper wrapper) {
            super(out, true);
            this.wrapper = wrapper;
        }

        @Override
        public void write(String s) {
            checkAndNotify(s);
            super.write(s);
        }

        @Override
        public void write(String s, int off, int len) {
            if (!detected && s != null) {
                checkAndNotify(s.substring(off, Math.min(off + len, s.length())));
            }
            super.write(s, off, len);
        }

        @Override
        public void write(char[] buf, int off, int len) {
            if (!detected && buf != null) {
                checkAndNotify(new String(buf, off, len));
            }
            super.write(buf, off, len);
        }

        private void checkAndNotify(String s) {
            if (!detected) {
                wrapper.onContentWritten(s);
                if (wrapper.isRenderFailDetected()) {
                    detected = true;
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // 代理 ServletOutputStream
    // -------------------------------------------------------------------------

    static class DetectingServletOutputStream extends ServletOutputStream {
        private final CapturingResponseWrapper wrapper;
        private final ServletOutputStream delegate;
        private boolean detected = false;
        /** 用于收集首批字节以判断特征 */
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
        private boolean bufferFlushed = false;

        DetectingServletOutputStream(ServletOutputStream delegate, CapturingResponseWrapper wrapper) {
            this.delegate = delegate;
            this.wrapper = wrapper;
        }

        @Override
        public void write(int b) throws IOException {
            if (!bufferFlushed) {
                buffer.write(b);
                checkBuffer();
            }
            delegate.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (!bufferFlushed && !detected) {
                buffer.write(b, off, len);
                checkBuffer();
            }
            delegate.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            flushBuffer();
            delegate.flush();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            delegate.setWriteListener(writeListener);
        }

        private void checkBuffer() {
            // 缓存达到一定大小或内容足够时检测
            if (buffer.size() >= 64) {
                flushBuffer();
            }
        }

        private void flushBuffer() {
            if (!bufferFlushed && !detected) {
                String content;
                try { content = buffer.toString("UTF-8"); } catch (Exception ex) { content = buffer.toString(); }
                wrapper.onContentWritten(content);
                detected = wrapper.isRenderFailDetected();
                bufferFlushed = true;
            }
        }
    }
}
