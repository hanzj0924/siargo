# JBolt Table 表格组件 data-xxx 属性配置文档

本文档详细说明了 JBolt Table 表格组件在 `<table>` 标签上可以配置的所有 `data-xxx` 属性。

## 基础配置属性

### data-jbolttable
- **说明**: 标识这是一个 JBolt Table 组件，必须设置此属性才能初始化表格
- **类型**: 无需值，仅作为标识
- **示例**: `<table data-jbolttable>`
- **必填**: 是

### data-editable
- **说明**: 设置表格是否可编辑
- **类型**: boolean
- **可选值**: `true` | `false`
- **默认值**: `false`
- **示例**: `<table data-jbolttable data-editable="true">`

### data-shortcutkey-disabled
- **说明**: 禁用表格的快捷键操作
- **类型**: boolean
- **可选值**: `true` | `false`
- **默认值**: `false`
- **示例**: `<table data-jbolttable data-shortcutkey-disabled="true">`

## 数据源配置

### data-ajax
- **说明**: 设置表格是否使用 Ajax 方式加载数据
- **类型**: boolean
- **可选值**: `true` | `false`
- **默认值**: `false`
- **示例**: `<table data-jbolttable data-ajax="true">`

### data-url
- **说明**: 设置 Ajax 请求的 URL 地址
- **类型**: string
- **示例**: `<table data-jbolttable data-ajax="true" data-url="/api/table/data">`

## 分页配置

### data-page
- **说明**: 设置表格分页容器的 ID 或启用分页
- **类型**: string | boolean
- **示例**: 
  - `<table data-jbolttable data-page="pageContainer">`
  - `<table data-jbolttable data-page="true">`

### data-pagesize
- **说明**: 设置每页显示的记录数
- **类型**: number
- **默认值**: 15
- **示例**: `<table data-jbolttable data-pagesize="20">`

## 表单关联配置

### data-conditions-form
- **说明**: 关联的查询条件表单 ID
- **类型**: string
- **示例**: `<table data-jbolttable data-conditions-form="searchForm">`

## 尺寸配置

### data-width
- **说明**: 设置表格宽度
- **类型**: string | number
- **示例**: `<table data-jbolttable data-width="800px">`
- **可选值** ：fill、fill_box、800px这样的具体数值

### data-height
- **说明**: 设置表格高度
- **类型**: string | number
- **示例**: `<table data-jbolttable data-height="400px">`
- **可选值** ：fill、fill_box、800px这样的具体数值

## 主题和样式配置

### data-theme
- **说明**: 设置表格主题
- **类型**: string
- **默认值**: `jbolttable`
- **示例**: `<table data-jbolttable data-theme="bootstrap">`
- **可选值** ：jbolttable、bootstrap

## 工具栏配置

### data-toolbar
- **说明**: 设置表格工具栏的 ID 或配置
- **类型**: string
- **示例**: `<table data-jbolttable data-toolbar="tableToolbar">`

## 扩展区域配置

### data-headbox
- **说明**: 设置表格头部扩展区域的 ID 或配置
- **类型**: string
- **示例**: `<table data-jbolttable data-headbox="tableHeadBox">`

### data-footbox
- **说明**: 设置表格底部扩展区域的 ID 或配置
- **类型**: string
- **示例**: `<table data-jbolttable data-footbox="tableFootBox">`

### data-leftbox
- **说明**: 设置表格左侧扩展区域的 ID 或配置
- **类型**: string
- **示例**: `<table data-jbolttable data-leftbox="tableLeftBox">`

### data-rightbox
- **说明**: 设置表格右侧扩展区域的 ID 或配置
- **类型**: string
- **示例**: `<table data-jbolttable data-rightbox="tableRightBox">`

## 排序配置

### data-sort
- **说明**: 设置默认排序规则
- **类型**: string
- **格式**: `column:type` (type 可以是 asc 或 desc)
- **示例**: `<table data-jbolttable data-sort="name:asc">`

### data-sort-column
- **说明**: 设置当前排序的列名
- **类型**: string
- **示例**: `<table data-jbolttable data-sort-column="createTime">`

### data-sort-type
- **说明**: 设置当前排序类型
- **类型**: string
- **可选值**: `asc` | `desc`
- **示例**: `<table data-jbolttable data-sort-type="desc">`

### data-default-sort-column
- **说明**: 设置默认排序列
- **类型**: string
- **示例**: `<table data-jbolttable data-default-sort-column="id">`

### data-sortable-columns
- **说明**: 设置可排序的列名列表
- **类型**: string (逗号分隔)
- **示例**: `<table data-jbolttable data-sortable-columns="name,age,createTime">`

## 固定列配置

### data-fixed-columns-left
- **说明**: 设置左侧固定列的列名列表
- **类型**: 序号数字 从1开始 (逗号分隔)
- **示例**: `<table data-jbolttable data-fixed-columns-left="1,2,3">`

### data-fixed-columns-right
- **说明**: 设置右侧固定列的列名列表
- **类型**: 需要数字 从右往左从-1开始 (逗号分隔)
- **示例**: `<table data-jbolttable data-fixed-columns-right="-3,-2,-1">`

## 列配置

### data-column-resize
- **说明**: 启用列宽调整功能
- **类型**: boolean
- **可选值**: `true` | `false`
- **默认值**: `false`
- **示例**: `<table data-jbolttable data-column-resize="true">`

### data-column-prepend
- **说明**: 列前置配置信息
- **类型**: string.   第几行:(checkbox还是radio):
- **示例**: `<table data-jbolttable data-column-prepend="1:checkbox:true">`

这个配置内容比较丰富 单独拿出来说明一下：
columnIndex:columnType:clickRowAndChecked

columnIndex：指定第一列出现checkbox或者radio组件 从1开始
columnType: 指定这一列出现什么类型组件 可选checkbox、radio
clickRowAndChecked：点击行是否触发这一行中这一列组件的选中状态 checkbox切换 radio只选中

举例：
1、data-column-prepend="1:checkbox" 第一列用checkbox渲染填充
2、data-column-prepend="1:checkbox:true" 第一列用checkbox渲染填充 点击行触发checkbox切换选中状态



## 菜单配置

### data-menu-option
- **说明**: 右键菜单配置函数
- **类型**: string (函数名)
- **示例**: `<table data-jbolttable data-menu-option="getTableMenuOptions">`


## 使用示例

### 基础表格
```html
<table data-jbolttable>
  <thead>
    <tr>
      <th data-column="name">姓名</th>
      <th data-column="age">年龄</th>
    </tr>
  </thead>
  <tbody>
    <!-- 表格内容 -->
  </tbody>
</table>
```

### Ajax 分页表格
```html
<table data-jbolttable 
       data-ajax="true" 
       data-url="/api/users" 
       data-page="true" 
       data-pagesize="20">
  <thead>
    <tr>
      <th data-column="name">姓名</th>
      <th data-column="email">邮箱</th>
    </tr>
  </thead>
  <tbody>
    <!-- Ajax 加载内容 -->
  </tbody>
</table>
```

### 可编辑表格
```html
<table data-jbolttable 
       data-editable="true" 
       data-conditions-form="editForm">
  <thead>
    <tr>
      <th data-column="name">姓名</th>
      <th data-column="age">年龄</th>
    </tr>
  </thead>
  <tbody>
    <!-- 可编辑内容 -->
  </tbody>
</table>
```

### 带固定列和排序的表格
```html
<table data-jbolttable 
       data-fixed-columns-left="1,2"
       data-fixed-columns-right="-1"
       data-sortable-columns="name,age,createTime"
       data-sort="createTime:desc">
  <thead>
    <tr>
      <th data-column="checkbox">选择</th>
      <th data-column="name">姓名</th>
      <th data-column="age">年龄</th>
      <th data-column="createTime">创建时间</th>
      <th data-column="action">操作</th>
    </tr>
  </thead>
  <tbody>
    <!-- 表格内容 -->
  </tbody>
</table>
```

## 注意事项

1. `data-jbolttable` 属性是必须的，没有此属性表格不会被初始化
2. 可编辑表格需要同时设置 `data-editable="true"`
3. Ajax 表格需要设置 `data-ajax="true"` 和 `data-url`
4. 分页功能需要设置 `data-page` 属性
5. 固定列功能需要配合相应的 CSS 样式
6. 排序功能需要在 `<th>` 标签上设置 `data-column` 属性
7. 所有的布尔类型属性值应该使用字符串 `"true"` 或 `"false"`


#  data-xxxx  各种handler


