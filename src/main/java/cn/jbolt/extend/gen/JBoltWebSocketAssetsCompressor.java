package cn.jbolt.extend.gen;

import cn.jbolt.core.gen.JBoltAbstractAssetsCompressor;

/** 
 * 资源在线压缩生成  - 主要是项目jbolt-websocket.js 的要替换
 * @ClassName:  JBoltWebSocketAssetsCompressor   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月15日 下午3:26:10   
 * 
 */
public class JBoltWebSocketAssetsCompressor extends JBoltAbstractAssetsCompressor{
	
	public static void main(String[] args) {
		JBoltWebSocketAssetsCompressor assetsCompressor=new JBoltWebSocketAssetsCompressor();
		//开始压缩jbolt-websocket.js
		//js源文件
		String jbolt_websocket_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-websocket.js";
		//js压缩后文件
		String jbolt_websocket_min_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-websocket.min.js";
		//执行JS压缩
		assetsCompressor.js(jbolt_websocket_js, jbolt_websocket_min_js);
		System.out.println("===提示：压缩后，请手动刷新项目工程目录,获取最新压缩文件===");
	}


	
}
