# JBoltTable Handler 属性说明文档

JBoltTable 组件支持多种 `data-xxx-handler` 属性，用于处理表格的各种事件和回调。本文档详细说明了各种 handler 的用法和配置方式。

## 目录

- [表格生命周期 Handler](#表格生命周期-handler)
- [用户交互 Handler](#用户交互-handler)
- [可编辑表格 Handler](#可编辑表格-handler)
- [Ajax 相关 Handler](#ajax-相关-handler)
- [按钮操作 Handler](#按钮操作-handler)
- [汇总统计 Handler](#汇总统计-handler)
- [其他 Handler](#其他-handler)

## 表格生命周期 Handler

### data-init-handler
表格初始化完成后的回调处理器。

**用法：**
```html
<table data-jbolttable data-init-handler="myInitHandler">
```

**JavaScript 函数签名：**
```javascript
function myInitHandler(table) {
    // table: JBoltTable 实例
    console.log('表格初始化完成', table);
}
```

**应用场景：**
- 表格渲染完成后执行自定义逻辑
- 初始化表格相关的其他组件
- 设置表格的初始状态

### data-before-ajax-handler
Ajax 请求发送前的处理器。

**用法：**
```html
<table data-jbolttable data-before-ajax-handler="beforeAjaxHandler">
```

**JavaScript 函数签名：**
```javascript
function beforeAjaxHandler(table, requestData) {
    // table: JBoltTable 实例
    // requestData: 即将发送的请求数据
    // 可以修改 requestData 或返回 false 取消请求
    return true; // 返回 false 会取消 Ajax 请求
}
```

## 用户交互 Handler

### data-row-click-handler
行点击事件处理器。

**用法：**
```html
<table data-jbolttable data-row-click-handler="rowClickHandler" data-row-click-active="true">
```

**JavaScript 函数签名：**
```javascript
function rowClickHandler(table, tr, dataId, rowData) {
    // table: JBoltTable 实例
    // tr: 被点击的行 jQuery 对象
    // dataId: 行数据对象ID
    // rowData: 行数据对象
    console.log('行被点击', rowData);
}
```

### data-row-dblclick-handler
行双击事件处理器。

**用法：**
```html
<table data-jbolttable data-row-dblclick-handler="rowDblClickHandler" data-row-dblclick-active="true">
```

**JavaScript 函数签名：**
```javascript
function rowDblClickHandler(table, tr, dataId, rowData) {
    // table: JBoltTable 实例
    // tr: 被点击的行 jQuery 对象
    // dataId: 行数据对象ID
    // rowData: 行数据对象
    console.log('行被双击', rowData);
}
```

### data-checked-change-handler
选中状态改变事件处理器。

**用法：**
```html
<table data-jbolttable data-checked-change-handler="checkedChangeHandler">
```

**JavaScript 函数签名：**
```javascript
function checkedChangeHandler(isAll,table,tr,tdOrTh,ele,trJsonData) {
    // table: JBoltTable 实例
    // tr: 选中的行 jQuery 对象集合
    // tdOrTh: 单元格
   // ele: 具体这一行里的checkbox还是radio的对象
    //trJsonData. 这一行数据
    console.log('选中状态改变', trJsonData);
}
```



## Ajax 相关 Handler

### data-ajax-success-handler
Ajax 请求成功处理器。

**用法：**
```html
<table data-jbolttable data-ajax-success-handler="ajaxSuccessHandler">
```

**JavaScript 函数签名：**
```javascript
function ajaxSuccessHandler(table, result) {
    // table: JBoltTable 实例
    // result: 服务器返回的结果
    console.log('Ajax 请求成功', result);
}
```

### data-ajax-fail-handler
Ajax 请求失败处理器。

**用法：**
```html
<table data-jbolttable data-ajax-fail-handler="ajaxFailHandler">
```

**JavaScript 函数签名：**
```javascript
function ajaxFailHandler(table, result) {
        // table: JBoltTable 实例
    // result: 服务器返回的结果
    console.log('Ajax 请求失败', error);
}
```

### data-ajax-success-data-handler
Ajax 成功后数据处理器 渲染之前改变一些数据使用。

**用法：**
```html
<table data-jbolttable data-ajax-success-data-handler="ajaxDataHandler">
```

**JavaScript 函数签名：**
```javascript
function ajaxDataHandler(table, data) {
    // table: JBoltTable 实例
    // data: 服务器返回的数据
    // 可以对数据进行预处理
    return data; // 返回处理后的数据
}
```

## 按钮操作 Handler

### data-handler（通用按钮处理器）
用于各种按钮操作的通用处理器。

**常用值：**

#### refreshJBoltTable
刷新表格数据。
```html
<button data-ajaxbtn data-handler="refreshJBoltTable" data-url="/api/update">更新</button>
```

#### removeTr
删除当前行。
```html
<a data-ajaxbtn data-handler="removeTr" data-url="/api/delete/123">删除</a>
```

#### removeTrCascade
级联删除当前行（用于树形表格）。
```html
<a data-ajaxbtn data-handler="removeTrCascade" data-url="/api/delete/123">删除</a>
```

#### moveUp
上移当前行。
```html
<a data-ajaxbtn data-handler="moveUp" data-url="/api/moveUp/123">上移</a>
```

#### moveDown
下移当前行。
```html
<a data-ajaxbtn data-handler="moveDown" data-url="/api/moveDown/123">下移</a>
```

#### jboltTablePageToFirst
操作完成后跳转到第一页。
```html
<button data-dialogbtn data-handler="jboltTablePageToFirst" data-url="/api/add">新增</button>
```

#### jboltTablePageToLast
操作完成后跳转到最后一页。
```html
<button data-dialogbtn data-handler="jboltTablePageToLast" data-url="/api/add">新增</button>
```

#### refreshPortal
刷新指定的 Portal 区域。
```html
<button data-ajaxbtn data-handler="refreshPortal" data-portalid="myPortal" data-url="/api/refresh">刷新</button>
```

### data-check-handler
检查处理器，用于获取选中的数据。

**常用值：**

#### jboltTableGetCheckedId
获取选中的单个 ID。
```html
<button data-check-handler="jboltTableGetCheckedId" data-url="/api/edit/">编辑</button>
```

#### jboltTableGetCheckedIds
获取选中的多个 ID。
```html
<button data-check-handler="jboltTableGetCheckedIds" data-url="/api/batchDelete?ids=">批量删除</button>
```



## 配置示例

### 完整的表格配置示例
```html
<table 
    data-jbolttable
    data-url="/api/tableData"
    data-init-handler="tableInitHandler"
    data-ajax-success-handler="tableAjaxSuccessHandler"
    data-row-click-handler="tableRowClickHandler"
    data-row-click-active="true"
    data-checked-change-handler="tableCheckedChangeHandler"
    data-editable="true">
    <thead>
        <tr>
            <th data-column="name">姓名</th>
            <th data-column="age">年龄</th>
            <th data-column="status">状态</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody data-rowtpl="tableRowTpl">
    </tbody>
</table>

```

### JavaScript 处理器函数示例
```javascript
// 表格初始化处理器
function tableInitHandler(table) {
    console.log('表格初始化完成');
    // 可以在这里进行额外的初始化操作
}

// Ajax 成功处理器
function tableAjaxSuccessHandler(table, result) {
    console.log('数据加载成功', result);
    // 可以在这里处理加载完成后的逻辑
}

// 行点击处理器
function tableRowClickHandler(table, tr, rowData, rowIndex) {
    console.log('点击了第' + rowIndex + '行', rowData);
    // 可以在这里处理行点击逻辑
}

// 选中状态改变处理器
function tableCheckedChangeHandler(table, checkedTrs, checkedDatas) {
    console.log('选中了' + checkedDatas.length + '行数据');
    // 可以在这里处理选中状态改变的逻辑
}

// 单元格值改变处理器
function nameChangeHandler(table, td, text, value, jsonData) {
    console.log('姓名从 ' + jsonData.name + ' 改为 ' + value);
    // 可以在这里处理单元格值改变的逻辑
}
```