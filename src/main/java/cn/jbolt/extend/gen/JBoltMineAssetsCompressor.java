package cn.jbolt.extend.gen;

import cn.jbolt.core.gen.JBoltAbstractAssetsCompressor;

/** 
 * 资源在线压缩生成  - 主要是项目特殊自己写的css js 的要替换
 * @ClassName:  AssetsCompressor   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月15日 下午3:26:10   
 * 
 */
public class JBoltMineAssetsCompressor extends JBoltAbstractAssetsCompressor{
	
	public static void main(String[] args) {
		JBoltMineAssetsCompressor assetsCompressor=new JBoltMineAssetsCompressor();
		//开始压缩jbolt-mine.js
		//js源文件
		String jbolt_mine_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-mine.js";
		//js压缩后文件
		String jbolt_mine_min_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-mine.min.js";
		//执行JS压缩
		assetsCompressor.js(jbolt_mine_js, jbolt_mine_min_js);
		
		//开始压缩jbolt-mine.css
		//css源文件
		String jbolt_mine_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-mine.css";
		//CSS、压缩后文件
		String jbolt_mine_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-mine.min.css";
		//执行css压缩
		assetsCompressor.css(jbolt_mine_css, jbolt_mine_min_css);
		
		System.out.println("===提示：压缩后，请手动刷新项目工程目录,获取最新压缩文件===");
	}


	
}
