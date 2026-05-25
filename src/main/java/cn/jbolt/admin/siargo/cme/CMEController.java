package cn.jbolt.admin.siargo.cme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;


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
	 * 首页
	 * URL路径: GET /admin/siargo/cme
	 */
	public void index() {
		render("index.html");
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
		File file = new File(PathKit.getWebRootPath() + "/_view/admin/siargo/cme/" + fileName);
		if (!file.exists()) {
			renderText("文件不存在: " + fileName);
			return;
		}
		HttpServletResponse response = getResponse();
		response.setContentType("application/pdf");
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
