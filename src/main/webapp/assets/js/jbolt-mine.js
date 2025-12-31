/****这个文件是用来提供个人扩展和编写js使用的文件****/
/**
 * 可在此自行扩展juicer注册函数 
 * 扩展方式参考jbolt-admin.js-initJuicer()方法
 * 此方法已经自动被jbolt-admin.js-initJuicer调用
 * @returns
 */
function initMineJuicer(){
	//格式 juicer.register("模板函数名定义（自己取名）",具体模板函数实现);
	//举例 juicer.register("date_ymdhm",date_ymdhm);
	//测试效果用例
	/*var jb_sum=function(a,b){
		return a+b;
	}
	juicer.register("jb_sum",jb_sum);
	alert(juicer("${a,b|jb_sum}",{a:1,b:1}));*/
	
	//下面写你自己的吧........
	
	
	
}

/**
 * 自行扩展前端表单校验规则 
 * 在jbolt-admin.js的ruleMap基础上加载这里的扩展
 * 此方法已经自动被jbolt-admin.js-ForkChecker.init()调用
 * @returns
 */
function initMineRuleMap(){
var mineFormCheckRuleMap=[
	//格式 {type:"具体规则名称",method:function(value){//具体校验实现 可以自行书写判断 也可用正则 return (!isNaN(value));}}
	//举例 {type:"int",method:function(value){return TestRgexp(/^-?[0-9]\d*$/, value);}},//整数校验
	//下面写你的吧
	
	
	];
	//将自己定义的Rule加入到JBolt自身Rule管理器中
	addRuleToJBoltRuleMap(mineFormCheckRuleMap);
}

/**
 * 添加和维护自己二开使用的其它官方未集成的js库 js插件 组件
 * @param plugins
 * @returns
 */
function initMinePlugins(){
	//在这里可以添加自己使用到的第三方组件的js 组件和插件
	var myPlugins={
			//"myplugin":{"js":['assets/plugins/myplugin/myplugin.js'],"css":['assets/plugins/myplugin/myplugin.css']}
	};
	
	//将自己定义的插件加入到JBolt自身插件管理器中
	addPluginsToJBoltPluginMgr(myPlugins);
}

//调用juicer模板初始化
initMineJuicer();
//调用注册二开使用的其它使用到的第三方插件
initMinePlugins();
//调用RuleMap增加自己的规则
initMineRuleMap();

$(function(){
//初始化调用加载
	
});