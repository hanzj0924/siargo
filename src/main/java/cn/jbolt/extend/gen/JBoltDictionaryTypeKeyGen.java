package cn.jbolt.extend.gen;

import cn.hutool.core.io.FileUtil;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.model.DictionaryType;
import cn.jbolt.core.model.Permission;
import cn.jbolt.core.util.JBoltStringUtil;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 本系统中定义的字典类型typeKey生成
 * 快捷生成枚举到DictionaryTypeKey.java文件中，
 * 方便其他地方统一调用
 */
public class JBoltDictionaryTypeKeyGen {
	public final String SEPARATOR=File.separator;
	/**
	 * JBolt项目绝对路径 修改这个就行
	 */
	private final String PROJECT_PATH=System.getProperty("user.dir");
	/**
	 * DictionaryTypeKey.java的绝对路径
	 */
	private final String TARGET=PROJECT_PATH+SEPARATOR+"src"+SEPARATOR+"main"+SEPARATOR+"java"+SEPARATOR+"cn"+SEPARATOR+"jbolt"+SEPARATOR+"_admin"+SEPARATOR+"dictionary"+SEPARATOR+"DictionaryTypeKey.java";
	/**
	 * 模板绝对路径
	 */
	private final String TPL=PROJECT_PATH+SEPARATOR+"src"+SEPARATOR+"main"+SEPARATOR+"resources"+SEPARATOR+"gentpl"+SEPARATOR+"dictionary_type_key_template.jf";
	protected DruidPlugin druidPlugin;
	public DataSource getDataSource() {
		druidPlugin = JBoltConfig.createDruidPlugin();
		boolean success=druidPlugin.start();
		return success?druidPlugin.getDataSource():null;
	}

	public static void main(String[] args) {
		new JBoltDictionaryTypeKeyGen().gen();
	}

	public void gen() {
		DataSource dataSource=getDataSource();
		if(dataSource==null) {
			System.out.println("数据库连接失败");
			return;
		}
		ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(dataSource);
		JBoltConfig.setDialect(activeRecordPlugin);
		//设置不区分大小写
		activeRecordPlugin.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		activeRecordPlugin.addMapping("jb_dictionary_type", "id", DictionaryType.class);
		activeRecordPlugin.start();
		List<DictionaryType> types = new DictionaryType().dao().findAll();
		Engine engine = new Engine();
		engine.addSharedObject("JBoltStringUtil", new JBoltStringUtil());
		Template template=engine.getTemplate(TPL);
		BufferedWriter writer=FileUtil.getWriter(TARGET, "utf-8", false);
		try {
			writer.write(template.renderToString(Kv.by("types", types)));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				}

			activeRecordPlugin.stop();
			if(druidPlugin!=null) {
				druidPlugin.stop();
			}
		}
	
		
	}
}
