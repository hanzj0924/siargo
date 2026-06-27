JBoltTable视频教程讲的很详细：
http://jfinalxueyuan.com/jiaocheng/jbolt/

**JBoltTable目前实现功能**：

1、普通表格循环遍历渲染数据
2、异步JSON数据源
3、固定头
4、固定列
5、列宽动态调整
6、高度fill填充 高度fill_box 填充父节点
7、宽度填充父节点 宽度auto
8、右键菜单
9、强大可编辑表格，单元格编辑与提交 整体提交 携带form等数据提交等
10、数据选择器 checkbox或者radio
11、工具条 header-box footer-box left-box right-box 
12、辅助录入
13、全键盘支持键盘事件 焦点跳转
14、输入与显示分开 支持格式化
15、多实例 可编辑多实例 多实例数据提交等
16、横向统计计算 纵向统计计算 extraData支持
17、还有一些隐藏特性 等等

JBoltTable配置属性：

属性| 取值举例 | 默认值|说明
:-----------: | :-----------: | :-----------: | :-----------: 
data-jbolttable |空|空|声明当前table是JBoltTable
data-autoload|false|true|设置是否默认自动执行ajax加载操作 默认是自动执行不用声明 仅当需要其他组件事件触发表格加载的时候需要设置为false
data-width|fill|fill|宽度设置 可以输入fill auto 百分比 数字
data-height|fill|fill|高度设置 可以输入fill fill_box 百分比 数字
data-ajax|true|false|设置表格数据源来自于指定data-url的地址返回json数据
data-url|admin/user/datas|空|设置json数据源
data-rowtpl|模板ID|空|设置异步json数据渲染tbody使用的js模板定义的ID
data-page|jboltTablePage|空|分页组件ID 声明后才有分页效果
data-pagesize|15|10|每页数量 默认10
data-pagesize-options|10,50,100,200,300|空|自定义表格分页组件可选分页pagesize的配置
data-fixed-columns-left|1,2|空|设置左侧固定列 多个用逗号 从1开始
data-fixed-columns-right|-1,-2|空|设置右侧固定列 多个用逗号 从-1开始
data-column-resize|true|false|开启列宽调整特性
data-bind-elements|"#jboltTableAddBtn"|空|绑定的元素 设置后绑定的元素 自动加上data-tableid属性 这些组件就可以调用jbolttable的方法了
data-conditions-form|jboltversion_JboltTableForm_ajax|空|设置表格使用的查询表单 绑定ID
data-sortable-columns|"version,publish_time,create_time"|空|设置可排序列
data-sort|crate_time:desc|空|设置排序方式
data-default-sort-column|create_time|空|默认排序列 用于控制首次加载UI后显示查询数据第一次的排序字段列的表头样式
 data-toolbar|jbolt_table_toolbar|空|设置表格的顶部工具条 指定ID
data-column-prepend|1:checkbox|空|设置指定列插入一列checkbox或者radio作为单选行和多选行
data-copy-to-excel|false|true|设置是否启用表格内容复制到excel功能 默认true
data-copy-from-excel|true|false|设置是否启用复制excel内容到表格里 默认false 可编辑表格才可以使用此属性
data-ajax-success-handler|xxxhandler|空|设置ajax加载完表格数据后执行的handler 回调

## data-column-prepend特殊说明
这个配置内容比较丰富 单独拿出来说明一下：
columnIndex:columnType:clickRowAndChecked

columnIndex：指定第一列出现checkbox或者radio组件 从1开始
columnType: 指定这一列出现什么类型组件 可选checkbox、radio
clickRowAndChecked：点击行是否触发这一行中这一列组件的选中状态 checkbox切换 radio只选中

举例：
1、data-column-prepend="1:checkbox" 第一列用checkbox渲染填充
2、data-column-prepend="1:checkbox:true" 第一列用checkbox渲染填充 点击行触发checkbox切换选中状态
