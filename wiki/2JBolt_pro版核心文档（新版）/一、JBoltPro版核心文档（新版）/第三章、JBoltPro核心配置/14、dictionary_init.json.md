系统数据字典表初始化数据配置。
在系统初始化sql里 只创建数据库的表结构，没有任何数据。
1、系统首次链接数据源启动的时候，会进行自检操作，检查是否存在首个超级管理员用户，如果没有，自动添加初始化超管员 账号 admin 密码 123
2、有了用户之后再去初始化权限数据和字典数据 初始化字典数据就用到这个配置文件。
系统里既定的内置需要的字典数据已经都写在这里了。

包括，一些select radio checkbox所用的基础字典数据  性别 部门类型 岗位类型 通知类型 代办类型等字典数据
还有一些可视化代码生成器专用的 后面上线后就看到了

```
[{
	"name": "选项_Boolean值",
	"modeLevel": 1,
	"typeKey": "options_boolean",
	"dics":[
			{"name":"是","sn":"true"},
			{"name":"否","sn":"false"}
	]
},{
	"name": "选项_enable",
	"modeLevel": 1,
	"typeKey": "options_enable",
	"dics":[
		{"name":"启用","sn":"true"},
		{"name":"禁用","sn":"false"}
	]
},{
	"name": "性别",
	"modeLevel": 1,
	"typeKey": "sex",
	"dics":[
			{"name":"男","sn":"1"},
			{"name":"女","sn":"2"}
	]
},{
	"name": "商品单位",
	"modeLevel": 1,
	"typeKey": "goods_unit",
	"dics":[
			{"name":"个","sn":"1"},
			{"name":"套","sn":"2"},
			{"name":"瓶","sn":"3"},
			{"name":"箱","sn":"4"},
			{"name":"克","sn":"5"}
	]
},{
	"name": "机构类型",
	"modeLevel": 1,
	"typeKey": "dept_type",
	"dics":[
			{"name":"总部","sn":"1"},
			{"name":"省级公司","sn":"2"},
			{"name":"市级公司","sn":"3"},
			{"name":"区县级公司","sn":"4"},
			{"name":"办事处","sn":"5"},
			{"name":"部门","sn":"6"}
	]
},{
	"name": "岗位类型",
	"modeLevel": 1,
	"typeKey": "post_type",
	"dics":[
			{"name":"高管","sn":"1"},
			{"name":"中层","sn":"2"},
			{"name":"基层","sn":"3"},
			{"name":"其他","sn":"4"}
	]
},{
	"name": "系统通知_类型",
	"modeLevel": 1,
	"typeKey": "sys_notice_type",
	"dics":[
			{"name":"公告","sn":"1"},
			{"name":"新闻","sn":"2"},
			{"name":"会议","sn":"3"},
			{"name":"放假","sn":"4"},
			{"name":"其它","sn":"5"}
	]
},{
	"name": "系统通知_优先级",
	"modeLevel": 1,
	"typeKey": "sys_notice_priority_level",
	"dics":[
			{"name":"普通","sn":"1"},
			{"name":"一般","sn":"2"},
			{"name":"紧急","sn":"3"}
	]
},{
	"name": "系统通知_接收人类型",
	"modeLevel": 1,
	"typeKey": "sys_notice_receiver_type",
	"dics":[
			{"name":"全部","sn":"1"},
			{"name":"角色","sn":"2"},
			{"name":"部门","sn":"3"},
			{"name":"岗位","sn":"4"},
			{"name":"用户","sn":"5"}
	]
},{
	"name": "待办事项_优先级",
	"modeLevel": 1,
	"typeKey": "todo_priority_level",
	"dics":[
			{"name":"普通","sn":"1"},
			{"name":"一般","sn":"2"},
			{"name":"紧急","sn":"3"}
	]
},{
	"name": "待办事项_状态",
	"modeLevel": 1,
	"typeKey": "todo_state",
	"dics":[
			{"name":"未开始","sn":"1"},
			{"name":"进行中","sn":"2"},
			{"name":"已完成","sn":"3"},
			{"name":"已取消","sn":"4"},
			{"name":"未完成","sn":"5"}
	]
},{
	"name": "待办事项_类型",
	"modeLevel": 1,
	"typeKey": "todo_type",
	"dics":[
			{"name":"无链接无内容","sn":"1"},
			{"name":"无链接有内容","sn":"2"},
			{"name":"有链接无内容","sn":"3"},
			{"name":"有链接有内容","sn":"4"}
	]
},{
	"name": "时间查询列名",
	"modeLevel": 1,
	"typeKey": "todo_datetime_column",
	"dics":[
			{"name":"创建时间","sn":"1"},
			{"name":"更新时间","sn":"2"},
			{"name":"待完成时间","sn":"3"},
			{"name":"实际完成时间","sn":"4"},
			{"name":"取消时间","sn":"5"}
	]
},{
	"name": "七牛账号_类型",
	"modeLevel": 1,
	"typeKey": "qiniu_type",
	"dics":[
			{"name":"个人","sn":"1"},
			{"name":"企业","sn":"2"}
	]
},{
	"name": "七牛账号_地区",
	"modeLevel": 1,
	"typeKey": "qiniu_region",
	"dics":[
			{"name":"华东","sn":"z0"},
			{"name":"华北","sn":"z1"},
			{"name":"华南","sn":"z2"},
			{"name":"北美","sn":"na0"},
			{"name":"东南亚","sn":"as0"}
	]
},{
	"name": "主键生成策略",
	"modeLevel": 1,
	"typeKey": "code_gen_idgenmode",
	"dics":[
			{"name":"自增_auto_int","sn":"auto"},
			{"name":"自增_auto_long","sn":"auto_long"},
			{"name":"自增序列_sequence","sn":"sequence"},
			{"name":"自增序列_sequence_long","sn":"sequence_long"},
			{"name":"自增序列_pgsql_serial","sn":"serial"},
			{"name":"自增序列_pgsql_bigserial","sn":"bigserial"},
			{"name":"雪花_snowflake","sn":"snowflake"},
			{"name":"UUID","sn":"uuid"}
	]
},{
	"name": "表格列显示格式化",
	"modeLevel": 1,
	"typeKey": "code_gen_table_col_format",
	"dics":[
			{"name":"日期_yyyy-MM-dd","sn":"date_ymd"},
			{"name":"日期_yyyy-MM","sn":"date_ym"},
			{"name":"日期时间_yyyy-MM-dd HH:mm:ss","sn":"date_ymdhms"},
			{"name":"日期时间_yyyy-MM-dd HH:mm","sn":"date_ymdhm"},
			{"name":"日期_HH:mm:ss","sn":"date_hms"},
			{"name":"日期_HH:mm","sn":"date_hm"},
			{"name":"时间_HH:mm:ss","sn":"time_hms"},
			{"name":"时间_HH:mm","sn":"time_hm"},
			{"name":"图片URL_real_image","sn":"real_image"},
			{"name":"文件URL_real_url","sn":"real_url"},
			{"name":"时间美化_pretty_time","sn":"pretty_time"},
			{"name":"字符串转驼峰_str_camel","sn":"str_camel"},
			{"name":"性别转汉字_sex","sn":"sex"},
			{"name":"小数点保留_1位","sn":"tofixed_1"},
			{"name":"小数点保留_2位","sn":"tofixed_2"},
			{"name":"小数点保留_3位","sn":"tofixed_3"},
			{"name":"小数点保留_4位","sn":"tofixed_3"},
			{"name":"金额_保留1位","sn":"money_1"},
			{"name":"金额_保留2位","sn":"money_2"},
			{"name":"金额_保留3位","sn":"money_3"},
			{"name":"金额_保留4位","sn":"money_4"}
	]
},{
	"name": "组件数据类型",
	"modeLevel": 1,
	"typeKey": "code_gen_data_type",
	"dics":[
			{"name":"用户输入","sn":"user_input"},
			{"name":"系统_数据字典","sn":"sys_dictionary"},
			{"name":"自定义输入URL","sn":"url"},
			{"name":"自定义输入Action名称","sn":"action"},
			{"name":"自定义输入option","sn":"option"},
			{"name":"枚举类","sn":"enum"}
	]
},{
	"name": "codeGen_查询条件组件类型",
	"modeLevel": 1,
	"typeKey": "code_gen_condition_ui_type",
	"dics":[
			{"name":"单行输入框_input","sn":"input"},
			{"name":"密码输入框_password","sn":"password"},
			{"name":"数字输入框_number","sn":"number"},
			{"name":"数字输入框_带计算器","sn":"calculator"},
			{"name":"多行文本框_textarea","sn":"textarea"},
			{"name":"选择器_单选_select","sn":"select"},
			{"name":"选择器_多选_select","sn":"select_multi"},
			{"name":"选择器_单选_select2","sn":"select2"},
			{"name":"选择器_多选_select2","sn":"select2_multi"},
			{"name":"输入自动补全_autocomplete","sn":"autocomplete"},
			{"name":"单选_radio","sn":"radio"},
			{"name":"多选_checkbox","sn":"checkbox"},
			{"name":"jboltinput","sn":"jboltinput"},
			{"name":"jboltinput_jstree","sn":"jboltinput_jstree"},
			{"name":"jboltinput_table","sn":"jboltinput_table"},
			{"name":"原生_日期_date","sn":"input_date"},
			{"name":"原生_时间_time","sn":"input_time"},
			{"name":"原生_日期时间_datetime","sn":"input_datetime"},
			{"name":"原生_月份_month","sn":"input_month"},
			{"name":"原生_周_week","sn":"input_week"},
			{"name":"Laydate_日期_date","sn":"laydate_date"},
			{"name":"Laydate_时间_time","sn":"laydate_time"},
			{"name":"Laydate_日期时间_datetime","sn":"laydate_datetime"},
			{"name":"Laydate_年份_year","sn":"laydate_year"},
			{"name":"Laydate_月份_month","sn":"laydate_month"},
			{"name":"Laydate_范围_year","sn":"laydate_range_year"},
			{"name":"Laydate_范围_month","sn":"laydate_range_month"},
			{"name":"Laydate_范围_date","sn":"laydate_range_date"},
			{"name":"Laydate_范围_time","sn":"laydate_range_time"},
			{"name":"Laydate_范围_datetime","sn":"laydate_range_datetime"},
			{"name":"rangeslider组件","sn":"rangerSlider"},
			{"name":"rangeslider组件_double","sn":"rangerSlider_double"},
			{"name":"switch Button","sn":"switchbtn"}
	]
},{
	"name": "codeGen_可编辑表格组件类型",
	"modeLevel": 1,
	"typeKey": "code_gen_table_ui_type",
	"dics":[
			{"name":"单行输入框_input","sn":"input"},
			{"name":"密码输入框_password","sn":"password"},
			{"name":"数字输入框_number","sn":"number"},
			{"name":"数字输入框_带计算器","sn":"calculator"},
			{"name":"多行文本框_textarea","sn":"textarea"},
			{"name":"选择器_单选_select","sn":"select"},
			{"name":"选择器_多选_select","sn":"select_multi"},
			{"name":"输入自动补全_autocomplete","sn":"autocomplete"},
			{"name":"单checkbox","sn":"checkbox"},
			{"name":"jboltinput","sn":"jboltinput"},
			{"name":"jboltinput_jstree","sn":"jboltinput_jstree"},
			{"name":"jboltinput_table","sn":"jboltinput_table"},
			{"name":"原生_日期_date","sn":"input_date"},
			{"name":"原生_时间_time","sn":"input_time"},
			{"name":"原生_日期时间_datetime","sn":"input_datetime"},
			{"name":"原生_月份_month","sn":"input_month"},
			{"name":"原生_周_week","sn":"input_week"},
			{"name":"Laydate_日期_date","sn":"laydate_date"},
			{"name":"Laydate_时间_time","sn":"laydate_time"},
			{"name":"Laydate_日期时间_datetime","sn":"laydate_datetime"},
			{"name":"Laydate_年份_year","sn":"laydate_year"},
			{"name":"Laydate_月份_month","sn":"laydate_month"},
			{"name":"Laydate_范围_year","sn":"laydate_range_year"},
			{"name":"Laydate_范围_month","sn":"laydate_range_month"},
			{"name":"Laydate_范围_date","sn":"laydate_range_date"},
			{"name":"Laydate_范围_time","sn":"laydate_range_time"},
			{"name":"Laydate_范围_datetime","sn":"laydate_range_datetime"},
			{"name":"rangeslider组件","sn":"rangerSlider"},
			{"name":"rangeslider组件_double","sn":"rangerSlider_double"},
			{"name":"switch Button","sn":"switchbtn"}
	]
},{
	"name": "codeGen_表单组件类型",
	"modeLevel": 1,
	"typeKey": "code_gen_form_ui_type",
	"dics":[
			{"name":"单行输入框_input","sn":"input"},
			{"name":"密码输入框_password","sn":"password"},
			{"name":"数字输入框_number","sn":"number"},
			{"name":"数字输入框_带计算器","sn":"calculator"},
			{"name":"多行文本框_textarea","sn":"textarea"},
			{"name":"选择器_单选_select","sn":"select"},
			{"name":"选择器_多选_select","sn":"select_multi"},
			{"name":"选择器_单选_select2","sn":"select2"},
			{"name":"选择器_多选_select2","sn":"select2_multi"},
			{"name":"输入自动补全_autocomplete","sn":"autocomplete"},
			{"name":"单选_radio","sn":"radio"},
			{"name":"多选_checkbox","sn":"checkbox"},
			{"name":"jboltinput","sn":"jboltinput"},
			{"name":"jboltinput_jstree","sn":"jboltinput_jstree"},
			{"name":"jboltinput_table","sn":"jboltinput_table"},
			{"name":"富文本_summernote","sn":"summernote"},
			{"name":"富文本_summernote_精简","sn":"summernote_simple"},
			{"name":"富文本_neditor","sn":"neditor"},
			{"name":"富文本_neditor_精简","sn":"neditor_simple"},
			{"name":"原生_日期_date","sn":"input_date"},
			{"name":"原生_时间_time","sn":"input_time"},
			{"name":"原生_日期时间_datetime","sn":"input_datetime"},
			{"name":"原生_月份_month","sn":"input_month"},
			{"name":"原生_周_week","sn":"input_week"},
			{"name":"Laydate_日期_date","sn":"laydate_date"},
			{"name":"Laydate_时间_time","sn":"laydate_time"},
			{"name":"Laydate_日期时间_datetime","sn":"laydate_datetime"},
			{"name":"Laydate_年份_year","sn":"laydate_year"},
			{"name":"Laydate_月份_month","sn":"laydate_month"},
			{"name":"Laydate_范围_year","sn":"laydate_range_year"},
			{"name":"Laydate_范围_month","sn":"laydate_range_month"},
			{"name":"Laydate_范围_date","sn":"laydate_range_date"},
			{"name":"Laydate_范围_time","sn":"laydate_range_time"},
			{"name":"Laydate_范围_datetime","sn":"laydate_range_datetime"},
			{"name":"rangeslider组件","sn":"rangerSlider"},
			{"name":"rangeslider组件_double","sn":"rangerSlider_double"},
			{"name":"switch Button","sn":"switchbtn"},
			{"name":"单图上传_imguploader","sn":"imguploader"},
			{"name":"多图上传_imguploader","sn":"imguploader_multi"},
			{"name":"单文件上传_fileuploader","sn":"fileuploader"},
			{"name":"多文件上传_fileuploader","sn":"fileuploader_multi"},
			{"name":"树_jstree","sn":"jstree"}
	]
},{
	"name": "表单校验规则",
	"modeLevel": 1,
	"typeKey": "code_gen_form_data_rule",
	"dics":[
			{"name":"普通必填","sn":"required"},
			{"name":"select必选","sn":"select"},
			{"name":"radio必选","sn":"radio"},
			{"name":"checkbox必选","sn":"checkbox"},
			{"name":"数字","sn":"number"},
			{"name":"正数","sn":"pnumber"},
			{"name":"非负数","sn":"pznumber"},
			{"name":"整数","sn":"int"},
			{"name":"正整数","sn":"pint"},
			{"name":"自然数","sn":"pzint"},
			{"name":"电子邮件地址","sn":"email"},
			{"name":"磁盘文件地址","sn":"filepath"},
			{"name":"网址","sn":"url"},
			{"name":"网址_不带http","sn":"url_nohttp"},
			{"name":"日期时间(年-月-日 时:分:秒)","sn":"datetime"},
			{"name":"日期时间(年-月-日 时:分)","sn":"datetime_hm"},
			{"name":"日期(年-月-日)","sn":"date"},
			{"name":"时间(时:分:秒)","sn":"time"},
			{"name":"时间(时:分)","sn":"time_hm"},
			{"name":"金额_两位小数","sn":"money"},
			{"name":"金额_四位小数","sn":"money_4"},
			{"name":"手机号","sn":"phone"},
			{"name":"座机电话号","sn":"tel"},
			{"name":"中文汉字","sn":"zh_cn"},
			{"name":"IP地址","sn":"ip"},
			{"name":"邮政编码(6位)","sn":"postalcode"},
			{"name":"中国公民身份证号","sn":"idcardno"},
			{"name":"英文字母_数字_下划线","sn":"letter_num"},
			{"name":"英文字母","sn":"letter"},
			{"name":"密码(6-16位)","sn":"password"},
			{"name":"比例(2:10)","sn":"proportion"},
			{"name":"比例(2:10 或 2:5:6)","sn":"proportion_multi"}
	]
}]
```

### 那么问题来了？如果我二开系统有自己特殊需要的字典数据需要初始化怎么办？直接修改这个系统自带的配置文件吗？
不是的，JBolt给二开提供了专门的扩展字典json配置文件：
## extend_dictionary_init.json
![图片](../../image/57556090-a7f6-4840-adb3-a391446b9f78.png)
![图片](../../image/6686a3ce-0adf-4082-964a-801ad9aefa4a.png)


```
[{
	"name": "扩展数据字典Demo",
	"modeLevel": 1,
	"typeKey": "extend_dic_demo",
	"dics":[
			{"name":"字典项1","sn":"值1"},
			{"name":"字典项2","sn":"值2"}
	]
}]
```

是JSON数据，每组字典数据就是一个{}
每组数据里包含字典的类型和字典的数据项

name:字典数据类型名称
modelLevel 字典模式 是1级的还是多级的 现在只能配置一级的 就是1
typeKey 字典的key标识 唯一的
dics 字典类型下的具体字典项数据 是个数组 每个项是一个{} name是字典数据名字 sn是字典数据的编码值
