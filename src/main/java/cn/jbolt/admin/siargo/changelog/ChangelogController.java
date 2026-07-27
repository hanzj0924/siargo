package cn.jbolt.admin.siargo.changelog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;

/**
 * 更新日志 Controller
 * 从 webapp 目录读取 CHANGELOG.md（_view/admin/siargo/changelog/CHANGELOG.md）并以 HTML 格式展示
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
		// 文件位于 webapp/_view/admin/siargo/changelog/CHANGELOG.md
		java.nio.file.Path filePath = java.nio.file.Paths.get(PathKit.getWebRootPath(), "_view", "admin", "siargo", "changelog", "CHANGELOG.md");
		java.io.File file = filePath.toFile();
		if (!file.exists()) {
			return "<p class=\"text-muted\">更新日志文件不存在</p>";
		}
		try {
			String content = Files.readString(filePath, StandardCharsets.UTF_8);
			return markdownToHtml(content);
		} catch (IOException e) {
			return "<p class=\"text-danger\">读取更新日志失败: " + e.getMessage() + "</p>";
		}
	}

	/**
	 * Markdown → HTML 转换（两阶段解析）
	 * 第一阶段：扫描所有 ### 和 - 行，归入版本条目
	 * 第二阶段：按 MAJOR.MINOR 分组，每组 &lt;details&gt; 包裹，首个默认展开
	 */
	private String markdownToHtml(String md) {
		// 第一阶段：解析为结构化数据
		List<VersionEntry> entries = parseEntries(md);

		// 按 MAJOR.MINOR 分组（LinkedHashMap 保持插入顺序）
		Map<String, List<VersionEntry>> groups = new LinkedHashMap<>();
		for (VersionEntry e : entries) {
			groups.computeIfAbsent(e.majorMinor, k -> new ArrayList<>()).add(e);
		}

		StringBuilder html = new StringBuilder();
		html.append("<div class=\"cl-wrap\">\n");

		// 页面标题
		html.append("<h2 class=\"cl-h2\">更新日志</h2>\n");

		// 展开/折叠全部 工具栏
		html.append("<div class=\"cl-toolbar\">\n");
		html.append("<span class=\"cl-toggle\" data-expanded=\"0\" onclick=\"ChangelogToggleAll(this)\">全部展开</span>\n");
		html.append("</div>\n");

		int groupIdx = 0;
		for (Map.Entry<String, List<VersionEntry>> g : groups.entrySet()) {
			String mm = g.getKey();
			List<VersionEntry> vers = g.getValue();

			// 首个组默认展开
			html.append("<details class=\"cl-group\"");
			if (groupIdx == 0) {
				html.append(" open");
			}
			html.append(">\n");

			html.append("<summary class=\"cl-group-header\">\n");
			html.append("<span class=\"cl-group-label\">v").append(mm).append("</span>\n");
			html.append("<span class=\"cl-group-badge\">").append(vers.size()).append("</span>\n");
			html.append("</summary>\n");

			html.append("<div class=\"cl-group-body\">\n");
			for (VersionEntry ve : vers) {
				html.append("<h3 class=\"cl-h3\">").append(escapeHtml(ve.title)).append("</h3>\n");
				if (!ve.items.isEmpty()) {
					html.append("<ul class=\"cl-list\">\n");
					for (String item : ve.items) {
						String escaped = escapeHtml(item);
						escaped = convertInlineCode(escaped);
						html.append("<li>").append(escaped).append("</li>\n");
					}
					html.append("</ul>\n");
				}
			}
			html.append("</div>\n</details>\n");
			groupIdx++;
		}

		html.append("</div>");
		return html.toString();
	}

	/**
	 * 从版本标题提取 MAJOR.MINOR 作为分组键
	 * 如 "v2.7.15 (2026-07-24)" → "2.7"
	 */
	private String extractMajorMinor(String versionTitle) {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("v(\\d+)\\.(\\d+)\\.\\d+");
		java.util.regex.Matcher m = p.matcher(versionTitle);
		if (m.find()) {
			return m.group(1) + "." + m.group(2);
		}
		return versionTitle;
	}

	/**
	 * 解析 CHANGELOG.md 为版本条目列表
	 * 每个 ### 行开启一个条目，后续 - 行归入该条目的 items
	 */
	private List<VersionEntry> parseEntries(String md) {
		List<VersionEntry> entries = new ArrayList<>();
		VersionEntry current = null;
		String[] lines = md.split("\n");

		for (String line : lines) {
			if (line.startsWith("### ")) {
				String rawTitle = line.substring(4).trim();
				String majorMinor = extractMajorMinor(rawTitle);
				current = new VersionEntry(rawTitle, majorMinor);
				entries.add(current);
			} else if (line.startsWith("- ") && current != null) {
				current.items.add(line.substring(2).trim());
			}
		}

		return entries;
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
	 * 解析后的单条版本条目
	 */
	private static class VersionEntry {
		final String title;       // 如 "v2.7.15 (2026-07-24)"
		final String majorMinor;  // 如 "2.7"
		final List<String> items = new ArrayList<>();

		VersionEntry(String title, String majorMinor) {
			this.title = title;
			this.majorMinor = majorMinor;
		}
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
