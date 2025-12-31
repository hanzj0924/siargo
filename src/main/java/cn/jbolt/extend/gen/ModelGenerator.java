package cn.jbolt.extend.gen;

import cn.jbolt.core.base.JBoltIDGenMode;
import cn.jbolt.core.gen.JBoltColumnToBuildAttrNameFunction;
import cn.jbolt.core.gen.JBoltProjectGenConfig;
import cn.jbolt.core.gen.JFinalModelGenerator;

/**
 * JFinal model Generator 主要用来生成Model和BaseModel
 * @ClassName:  ModelGenerator
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年4月11日09:24:23
 */
public class ModelGenerator extends JFinalModelGenerator{
    
	public static void main(String[] args) {
		//要生成的代码是在哪个项目下面 设置项目跟路径
		String projectRootPath=System.getProperty("user.dir");
		//数据源配置名称 默认主数据源是main 其他的在extend_datasource.setting里配置的
		String configName="main";
		//指定本次运行直接生成的表名 忽略其它所有表 数组为空 表示忽略此强制设定 当需要单个指定生成时才需要这个
		String[] tableNames = new String[] {"siargo_product"/*,"siargo_qareport" ,"jb_user","jb_application" */};
		//哪些前缀名的要生成
		String[] tableNamesPrefixes = new String[] {/* "jb_","pl_" */};
		//是否包含数据库视图生成 默认不生
		boolean generateView = false;
		//生成的Model java类需要去掉的前缀 多个用逗号隔开 内置已经去掉了核心表的前缀jb_
		String removedTableNamePrefixes="siargo_";
		//默认ID生成模式
		String idGenMode=JBoltIDGenMode.SNOWFLAKE;//auto|snowflake|sequence
		//生成Model放在哪个包下
		String modelPackage="cn.jbolt.siargo.model";

		//是否生成Model和BaseModel 如果设置为false 则只判断是否生成字典文件 直接不进行model和baseModel的生成
		boolean genModel=true;
		//如果model已经存在是否强制覆盖重新生成model 默认不强制覆盖model 只强制覆盖baseModel
		boolean cover =  false;
		//是否生成html格式数据字典
		boolean genHtmlDataDictionary=false;
		//数据库字典文件版本号 自己定义
		String dataDictionaryVersion="1.0.0";
		//数据流字典文件的简介描述信息
		String dataDictionaryDescription="siargo"+(configName.equals("main")?"主数据源":"扩展数据源["+configName+"]")+"数据库字典";


		//是否开始启动缓存机制 开启后会在Model上生成@JBoltAutoCache注解
		boolean autoCacheEnable = false;
		//是否开启idCache策略
		boolean idCacheEnable = true;
		//是否开启KeyCache策略
		boolean keyCacheEnable = false;
		//KeyCache 使用哪个字段属性
		String keyCacheColumn = "";
		//KeyCache 还需额外绑定哪个属性
		String keyCacheBindColumn = "";

		//是否在baseModel中生成所有字段的静态常量 默认生成
		JBoltProjectGenConfig.genColConstant = true;
		//是否在baseModel中生成所有字段的静态常量 名称默认都要转大写 默认转大写
		JBoltProjectGenConfig.genColConstantToUpperCase = true;

		//下面这个默认是null就行 自定义的数据库字段转驼峰getter属性名的策略，
		//默认使用策略已经够用，如果你有特殊需求就在这里定义它
		JBoltColumnToBuildAttrNameFunction columnTobuildAttrNameFun=null;
//		JBoltColumnToBuildAttrNameFunction columnTobuildAttrNameFun = new JBoltColumnToBuildAttrNameFunction() {
//			@Override
//			public String build(String column) {
//				//这里column就是数据库里的一个字段 然后你通过处理返回一个应有的定制格式即可
//				return null;
//			}
//		};
		
		//初始化项目配置
		JBoltProjectGenConfig.init(projectRootPath,modelPackage,genModel,idGenMode,genHtmlDataDictionary,false,removedTableNamePrefixes,columnTobuildAttrNameFun,tableNames,tableNamesPrefixes,generateView,null,cover);
		//设置自动缓存机制
		JBoltProjectGenConfig.setModelAutoCache(autoCacheEnable,idCacheEnable,keyCacheEnable,keyCacheColumn,keyCacheBindColumn);
		//执行Model、BaseModel、数据字典Html的生成
		new ModelGenerator().generate(configName,dataDictionaryVersion,dataDictionaryDescription);
	}
}
