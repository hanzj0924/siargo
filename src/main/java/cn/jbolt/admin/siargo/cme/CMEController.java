package cn.jbolt.admin.siargo.cme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;


/**
 * 注册计量师管理 Controller
 *
 * @ClassName: CMEController
 * @author: hanzj
 * @date: 2026-05-14 16:19
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/cme", viewPath = "/_view/admin/siargo/cme")
public class CMEController extends JBoltBaseController {

	/**
	 * 学习资料根目录（相对 webRoot）
	 */
	private static final String CME_BASE_DIR = "/_view/admin/siargo/cme/";

	/**
	 * 根目录固定展示顺序（目录不存在则跳过）
	 */
	private static final String[] ROOT_DIRS = {"法规", "实务", "案例", "补充知识", "计算器说明书"};

	/**
	 * 中文拼音排序器
	 */
	private static final Collator CHINA_COLLATOR = Collator.getInstance(Locale.CHINA);

	/**
	 * 首页
	 * URL路径: GET /admin/siargo/cme
	 */
	public void index() {
		render("index.html");
	}

	/**
	 * 学习界面（左侧目录树 + 右侧PDF/PNG查看器）
	 * URL路径: GET /admin/siargo/cme/learn
	 */
	public void learn() {
		render("learn.html");
	}

	/**
	 * 扫描cme目录，返回目录树JSON（供学习界面左侧目录树使用）
	 * 根目录按固定顺序：法规、实务、案例、补充知识、计算器说明书
	 * 只包含.pdf/.png文件，排除空目录；同级目录在前、文件在后，各自按中文拼音排序
	 * URL路径: GET /admin/siargo/cme/listFiles
	 */
	public void listFiles() {
		File baseDir = new File(PathKit.getWebRootPath() + CME_BASE_DIR);
		JSONArray tree = new JSONArray();
		for (String rootName : ROOT_DIRS) {
			File rootDir = new File(baseDir, rootName);
			if (!rootDir.exists() || !rootDir.isDirectory()) {
				continue;
			}
			JSONObject node = buildDirNode(rootDir, rootName);
			if (node != null) {
				tree.add(node);
			}
		}
		renderJsonData(tree);
	}

	/**
	 * 按相对路径提供PDF/PNG文件流（供embed/img内联查看）
	 * URL路径: GET /admin/siargo/cme/viewFile?path=相对cme目录的路径
	 */
	public void viewFile() {
		String path = get("path");
		if (StrKit.isBlank(path)) {
			renderJsonFail("参数path不能为空");
			return;
		}
		// 扩展名白名单校验
		String lowerPath = path.toLowerCase();
		String contentType;
		if (lowerPath.endsWith(".pdf")) {
			contentType = "application/pdf";
		} else if (lowerPath.endsWith(".png")) {
			contentType = "image/png";
		} else {
			renderJsonFail("不支持的文件类型");
			return;
		}
		File baseDir = new File(PathKit.getWebRootPath() + CME_BASE_DIR);
		File file = new File(baseDir, path);
		try {
			// 路径穿越检测：canonicalPath必须位于cme目录内（加分隔符防止同前缀兄弟目录绕过）
			String baseCanonical = baseDir.getCanonicalPath() + File.separator;
			String fileCanonical = file.getCanonicalPath();
			if (!fileCanonical.startsWith(baseCanonical)) {
				renderJsonFail("非法文件路径");
				return;
			}
		} catch (IOException e) {
			renderJsonFail("文件路径解析失败");
			return;
		}
		if (!file.exists() || !file.isFile()) {
			renderJsonFail("文件不存在");
			return;
		}
		serveFile(file, contentType);
	}

	/**
	 * 递归构建目录节点，无任何pdf/png后代时返回null（排除空目录）
	 * @param dir 当前目录
	 * @param name 目录名
	 * @return 目录节点JSON，空目录返回null
	 */
	private JSONObject buildDirNode(File dir, String name) {
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return null;
		}
		List<File> subDirs = new ArrayList<>();
		List<File> subFiles = new ArrayList<>();
		for (File f : files) {
			if (f.isDirectory()) {
				subDirs.add(f);
			} else if (isSupportedFile(f.getName())) {
				subFiles.add(f);
			}
		}
		// 同级排序：目录在前、文件在后，各自按中文拼音排序
		Comparator<File> byPinyin = (a, b) -> CHINA_COLLATOR.compare(a.getName(), b.getName());
		subDirs.sort(byPinyin);
		subFiles.sort(byPinyin);

		JSONArray children = new JSONArray();
		for (File sub : subDirs) {
			JSONObject childDir = buildDirNode(sub, sub.getName());
			if (childDir != null) {
				children.add(childDir);
			}
		}
		for (File f : subFiles) {
			children.add(buildFileNode(f));
		}
		if (children.isEmpty()) {
			return null;
		}
		JSONObject node = new JSONObject(true);
		node.put("name", name);
		node.put("type", "dir");
		node.put("children", children);
		return node;
	}

	/**
	 * 构建文件节点
	 * @param file 文件
	 * @return 文件节点JSON
	 */
	private JSONObject buildFileNode(File file) {
		JSONObject node = new JSONObject(true);
		node.put("name", file.getName());
		node.put("type", "file");
		node.put("fileType", file.getName().toLowerCase().endsWith(".pdf") ? "pdf" : "png");
		node.put("path", relativePath(file));
		return node;
	}

	/**
	 * 计算文件相对cme目录的路径（用/分隔）
	 * @param file 文件
	 * @return 相对路径
	 */
	private String relativePath(File file) {
		String basePath = new File(PathKit.getWebRootPath() + CME_BASE_DIR).getAbsolutePath();
		String filePath = file.getAbsolutePath();
		String relative = filePath.substring(basePath.length()).replace('\\', '/');
		if (relative.startsWith("/")) {
			relative = relative.substring(1);
		}
		return relative;
	}

	/**
	 * 判断是否为支持的文件类型（.pdf/.png，忽略大小写）
	 * @param fileName 文件名
	 * @return 是否支持
	 */
	private boolean isSupportedFile(String fileName) {
		String lower = fileName.toLowerCase();
		return lower.endsWith(".pdf") || lower.endsWith(".png");
	}

	/**
	 * 法规PDF
	 * URL路径: GET /admin/siargo/cme/fagui
	 */
	public void fagui() {
		render("1法规_viewer.html");
	}

	/**
	 * 法规PDF原始文件流（供embed使用）
	 * URL路径: GET /admin/siargo/cme/faguiPdfSrc
	 */
	public void faguiPdfSrc() {
		servePdf("1法规.pdf");
	}

	/**
	 * 实务PDF
	 * URL路径: GET /admin/siargo/cme/shiwu
	 */
	public void shiwu() {
		render("2实务_viewer.html");
	}

	/**
	 * 实务PDF原始文件流（供embed使用）
	 * URL路径: GET /admin/siargo/cme/shiwuPdfSrc
	 */
	public void shiwuPdfSrc() {
		servePdf("2实务.pdf");
	}

	/**
	 * 案例PDF
	 * URL路径: GET /admin/siargo/cme/anli
	 */
	public void anli() {
		render("3案例_viewer.html");
	}

	/**
	 * 案例PDF原始文件流（供embed使用）
	 * URL路径: GET /admin/siargo/cme/anliPdfSrc
	 */
	public void anliPdfSrc() {
		servePdf("3案例.pdf");
	}

	/**
	 * 法规思维导图
	 * URL路径: GET /admin/siargo/cme/faguiMindmap
	 */
	public void faguiMindmap() {
		render("1_法规_思维导图.html");
	}

	/**
	 * 实务思维导图
	 * URL路径: GET /admin/siargo/cme/shiwuMindmap
	 */
	public void shiwuMindmap() {
		render("2_实务_思维导图.html");
	}

	/**
	 * 案例思维导图
	 * URL路径: GET /admin/siargo/cme/anliMindmap
	 */
	public void anliMindmap() {
		render("3_案例_思维导图.html");
	}

	/**
	 * 以inline方式提供PDF文件，浏览器内直接查看
	 * @param fileName PDF文件名
	 */
	private void servePdf(String fileName) {
		File file = new File(PathKit.getWebRootPath() + CME_BASE_DIR + fileName);
		if (!file.exists()) {
			renderText("文件不存在: " + fileName);
			return;
		}
		serveFile(file, "application/pdf");
	}

	/**
	 * 以inline方式输出文件流，浏览器内直接查看
	 * @param file 文件
	 * @param contentType 响应Content-Type
	 */
	private void serveFile(File file, String contentType) {
		HttpServletResponse response = getResponse();
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "inline");
		response.setContentLength((int) file.length());
		try (FileInputStream fis = new FileInputStream(file);
			 OutputStream os = response.getOutputStream()) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}

}
