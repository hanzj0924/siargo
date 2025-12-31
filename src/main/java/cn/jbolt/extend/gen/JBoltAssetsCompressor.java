package cn.jbolt.extend.gen;

import cn.jbolt.core.gen.JBoltAbstractAssetsCompressor;

/**
 * 资源在线压缩生成
 * 
 * @ClassName: AssetsCompressor
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年4月15日 下午3:26:10
 * 
 */
public class JBoltAssetsCompressor extends JBoltAbstractAssetsCompressor {

	public static void main(String[] args) {
		JBoltAssetsCompressor assetsCompressor = new JBoltAssetsCompressor();

		// 开始压缩login.js
		// js源文件
		String login_js = JBOLT_PROJECT_PATH + "/src/main/webapp/assets/js/login.js";
		// js压缩后文件
		String login_min_js = JBOLT_PROJECT_PATH + "/src/main/webapp/assets/js/login.min.js";
		// 执行JS压缩
		assetsCompressor.js(login_js, login_min_js);

		// 开始压缩login.css
		// css源文件
		String login_css = JBOLT_PROJECT_PATH + "/src/main/webapp/assets/css/login.css";
		// css压缩后文件
		String login_min_css = JBOLT_PROJECT_PATH + "/src/main/webapp/assets/css/login.min.css";
		// 执行JS压缩
		assetsCompressor.css(login_css, login_min_css);

		System.out.println("===提示：压缩后，请手动刷新项目工程目录,获取最新压缩文件===");
	}

}
