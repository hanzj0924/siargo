属性列表：
属性名 | 案例值 | 默认值 | 说明
:----------- | :-----------: |:-----------:|-----------
data-autocomplete | 无 | 无 | 声明input是自动Autocomplete组件
data-url | “admin/school/autocomplete” | 无 | 声明数据源地址
data-sync-ele  | inputId  | 空 | 设置选择数据后 可以将数据同步给指定组件 多个逗号隔开
data-column-attr | “id,name,sn” | 空 | 设置显示列
data-text-attr | “name” | 空 | 设置选中数据后填充到input里的属性值
data-delimiter | “~” | 空 | 设置text多属性后的分隔符
data-header | “ID,姓名,年龄” | 空 | 设置表头
data-limit | “10” | 100 | 设置pagesize
data-link-para-ele | “#xxxId” | 空 | 设置关联元素组件值
data-mustmatch | “false” | true | 设置是否数据必须匹配
data-width | 500 | 空 | 设置选择器宽度 默认和input一样宽
data-height | 300 | 300 | 设置选择器高度
data-handler | processAutocompleteHanler | 空 | 设置选择数据后的回调处理
function processAutocompleteHanler(input,hiddenInput,value,jsonData){

}
