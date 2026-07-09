package cn.jbolt.admin.siargo.changelog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;

/**
 * 更新日志 Controller
 * 从 classpath 读取 CHANGELOG.md（与 Controller 同包）并以 HTML 格式展示
 *
 * @ClassName: ChangelogController
 * @author: hanzj
 * @date: 2026-07-09
 */
@CheckPermission(PermissionKey.SIARGO_CHANGE_LOG)
@UnCheckIfSystemAdmin
@Path(value = "/admin/changelog", viewPath = "/_view/admin/siargo/changelog")
public class ChangelogController extends JBoltBaseController {

	/**
	 * 显示更新日志首页
	 * URL: GET /admin/changelog
	 */
	public void index() {
		String html = readAndConvertChangelog();
		set("changelogHtml", html);
		render("index.html");
	}

	/**
	 * 读取 CHANGELOG.md 并将 Markdown 转换为 HTML
	 * <p>转换规则：</p>
	 * <ul>
	 *   <li>## X → &lt;h2 class="cl-h2"&gt;X&lt;/h2&gt;</li>
	 *   <li>### X → &lt;h3 class="cl-h3"&gt;X&lt;/h3&gt;</li>
	 *   <li>- X → &lt;li&gt;X&lt;/li&gt;，包裹在 &lt;ul class="cl-list"&gt; 中</li>
	 *   <li>空行 → &lt;br&gt;</li>
	 * </ul>
	 *
	 * @return 转换后的 HTML 字符串
	 */
	private String readAndConvertChangelog() {
		try (InputStream is = getClass().getResourceAsStream("CHANGELOG.md")) {
			if (is == null) {
				return "<p class=\"text-muted\">更新日志文件不存在</p>";
			}
			String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return markdownToHtml(content);
		} catch (IOException e) {
			return "<p class=\"text-danger\">读取更新日志失败: " + e.getMessage() + "</p>";
		}
	}

	/**
	 * 简单的 Markdown → HTML 转换
	 * 支持：## / ### / - 列表项 / 空行
	 */
	private String markdownToHtml(String md) {
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"cl-wrap\">\n");

		String[] lines = md.split("\n");
		boolean inList = false;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			// 跳过文件开头的一级标题
			if (line.startsWith("# ") && !line.startsWith("## ")) {
				continue;
			}

			// 空行
			if (line.trim().isEmpty()) {
				if (inList) {
					html.append("</ul>\n");
					inList = false;
				}
				continue;
			}

			// ## 二级标题
			if (line.startsWith("## ")) {
				if (inList) {
					html.append("</ul>\n");
					inList = false;
				}
				String title = escapeHtml(line.substring(3).trim());
				html.append("<h2 class=\"cl-h2\">").append(title).append("</h2>\n");
				continue;
			}

			// ### 三级标题
			if (line.startsWith("### ")) {
				if (inList) {
					html.append("</ul>\n");
					inList = false;
				}
				String title = escapeHtml(line.substring(4).trim());
				html.append("<h3 class=\"cl-h3\">").append(title).append("</h3>\n");
				continue;
			}

			// - 列表项
			if (line.startsWith("- ")) {
				if (!inList) {
					html.append("<ul class=\"cl-list\">\n");
					inList = true;
				}
				String item = escapeHtml(line.substring(2).trim());
				// 处理 `` 包裹的内联代码
				item = convertInlineCode(item);
				html.append("<li>").append(item).append("</li>\n");
				continue;
			}

			// 其他行保持原样
			if (inList) {
				html.append("</ul>\n");
				inList = false;
			}
			html.append("<p>").append(escapeHtml(line)).append("</p>\n");
		}

		if (inList) {
			html.append("</ul>\n");
		}

		html.append("</div>");
		return html.toString();
	}

	/**
	 * 将反引号包裹的内联代码转为 HTML code 标签
	 */
	private String convertInlineCode(String text) {
		StringBuilder sb = new StringBuilder();
		boolean inCode = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '`') {
				sb.append(inCode ? "</code>" : "<code>");
				inCode = !inCode;
			} else {
				sb.append(c);
			}
		}
		if (inCode) {
			sb.append("</code>");
		}
		return sb.toString();
	}

	/**
	 * 基本的 HTML 转义
	 */
	private String escapeHtml(String text) {
		if (text == null) return "";
		return text
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;");
	}
}
