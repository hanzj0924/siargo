本文档详细列出了JBoltTable组件的所有API方法、参数和返回值。

## 目录

- [表格操作API](#表格操作api)
- [行操作API](#行操作api)
- [列操作API](#列操作api)
- [单元格操作API](#单元格操作api)
- [选择操作API](#选择操作api)
- [数据操作API](#数据操作api)
- [可编辑表格API](#可编辑表格api)
- [扩展功能API](#扩展功能api)
- [工具方法API](#工具方法api)

## 表格操作API

### jboltTableRefresh(tableEle, confirm, refreshEditableOptions)

刷新表格数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `confirm` {Boolean} - 是否显示确认提示，默认false
- `refreshEditableOptions` {Boolean} - 是否刷新可编辑配置，默认false

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 直接刷新
jboltTableRefresh('#myTable');

// 带确认提示刷新
jboltTableRefresh('#myTable', true);

// 刷新并重新加载可编辑配置
jboltTableRefresh('#myTable', false, true);
```

### jboltTableClear(tableEle)

清空表格所有数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableClear('#myTable');
```

### jboltTableSubmit(tableEle)

提交可编辑表格数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableSubmit('#editableTable');
```

### jboltTableMaximize(tableEle)

最大化/恢复表格显示。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableMaximize('#myTable');
```

### jboltTableReadByConditions(tableEle, conditions)

使用指定条件重新加载表格数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `conditions` {Object} - 查询条件对象

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableReadByConditions('#myTable', {
    name: '张三',
    age: 25,
    status: 1
});
```

## 行操作API

### jboltTableInsertRow(tableEle, data, keepId, dontProcessChange, forceTrChange)

插入新行数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `data` {Object|Array} - 要插入的数据，可以是单个对象或对象数组
- `keepId` {Boolean} - 是否保留数据中的ID，默认false
- `dontProcessChange` {Boolean} - 是否不处理变更标记，默认false
- `forceTrChange` {Boolean} - 是否强制标记行为已变更，默认false

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 插入单行
jboltTableInsertRow('#myTable', {
    name: '张三',
    age: 25,
    email: 'zhang@example.com'
});

// 插入多行
jboltTableInsertRow('#myTable', [
    { name: '张三', age: 25 },
    { name: '李四', age: 30 }
]);

// 保留ID插入
jboltTableInsertRow('#myTable', { id: 123, name: '张三' }, true);
```

### jboltTableAppendRow(tableEle, data, keepId, dontProcessChange, forceTrChange, theTr)

在表格末尾或指定行后添加新行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `data` {Object|Array} - 要添加的数据
- `keepId` {Boolean} - 是否保留数据中的ID
- `dontProcessChange` {Boolean} - 是否不处理变更标记
- `forceTrChange` {Boolean} - 是否强制标记行为已变更
- `theTr` {jQuery} - 指定在哪一行后插入，可选

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在末尾添加
jboltTableAppendRow('#myTable', { name: '新用户', age: 25 });

// 在指定行后添加
var targetTr = $('#myTable tbody tr:eq(2)');
jboltTableAppendRow('#myTable', { name: '新用户' }, false, false, false, targetTr);
```

### jboltTablePrependRow(tableEle, data, keepId, dontProcessChange, forceTrChange, theTr)

在表格开头或指定行前添加新行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `data` {Object|Array} - 要添加的数据
- `keepId` {Boolean} - 是否保留数据中的ID
- `dontProcessChange` {Boolean} - 是否不处理变更标记
- `forceTrChange` {Boolean} - 是否强制标记行为已变更
- `theTr` {jQuery} - 指定在哪一行前插入，可选

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在开头添加
jboltTablePrependRow('#myTable', { name: '新用户', age: 25 });
```

### jboltTableInsertRowAfterChecked(tableEle, data, keepId, dontProcessChange, forceTrChange)

在选中行后插入新行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `data` {Object|Array} - 要插入的数据
- `keepId` {Boolean} - 是否保留数据中的ID
- `dontProcessChange` {Boolean} - 是否不处理变更标记
- `forceTrChange` {Boolean} - 是否强制标记行为已变更

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableInsertRowAfterChecked('#myTable', { name: '新用户' });
```

### jboltTableInsertRowBeforeChecked(tableEle, data, keepId, dontProcessChange, forceTrChange)

在选中行前插入新行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `data` {Object|Array} - 要插入的数据
- `keepId` {Boolean} - 是否保留数据中的ID
- `dontProcessChange` {Boolean} - 是否不处理变更标记
- `forceTrChange` {Boolean} - 是否强制标记行为已变更

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableInsertRowBeforeChecked('#myTable', { name: '新用户' });
```

### jboltTableReplaceCheckedRow(tableEle, data, replaceAllData, keepId, dontProcessChange, forceTrChange, theTr)

替换选中行的数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `data` {Object} - 替换的数据
- `replaceAllData` {Boolean} - 是否完全替换（true）还是合并数据（false）
- `keepId` {Boolean} - 是否保留数据中的ID
- `dontProcessChange` {Boolean} - 是否不处理变更标记
- `forceTrChange` {Boolean} - 是否强制标记行为已变更
- `theTr` {jQuery} - 指定要替换的行，可选

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 完全替换选中行数据
jboltTableReplaceCheckedRow('#myTable', {
    name: '新姓名',
    age: 30,
    email: 'new@example.com'
}, true);

// 合并数据到选中行
jboltTableReplaceCheckedRow('#myTable', {
    age: 30
}, false);
```

### jboltTableRemoveRow(tableEle)

删除当前行（通常在行内按钮事件中调用）。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在删除按钮的点击事件中
function deleteCurrentRow(btn) {
    jboltTableRemoveRow(btn);
}
```

### jboltTableRemoveCheckedRow(tableEle, confirm, callback)

删除选中的行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `confirm` {Boolean} - 是否显示确认提示
- `callback` {Function} - 删除完成后的回调函数

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableRemoveCheckedRow('#myTable', true, function() {
    console.log('删除完成');
});
```

### jboltTableTrMoveUp(trEle, forceTrChange, jsonAttrName, column)

将指定行上移。

**参数：**
- `trEle` {jQuery|Element} - 要移动的行元素
- `forceTrChange` {Boolean} - 是否强制标记行为已变更
- `jsonAttrName` {String} - JSON数据中的属性名
- `column` {String} - 列名

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在上移按钮的点击事件中
function moveUp(btn) {
    var tr = $(btn).closest('tr');
    jboltTableTrMoveUp(tr, true);
}
```

### jboltTableTrMoveDown(trEle, forceTrChange, jsonAttrName, column)

将指定行下移。

**参数：**
- `trEle` {jQuery|Element} - 要移动的行元素
- `forceTrChange` {Boolean} - 是否强制标记行为已变更
- `jsonAttrName` {String} - JSON数据中的属性名
- `column` {String} - 列名

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在下移按钮的点击事件中
function moveDown(btn) {
    var tr = $(btn).closest('tr');
    jboltTableTrMoveDown(tr, true);
}
```

### jboltTableActivePrevTr(tableEle)

激活上一行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** 无

**示例：**
```javascript
jboltTableActivePrevTr('#myTable');
```

### jboltTableActiveNextTr(tableEle)

激活下一行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** 无

**示例：**
```javascript
jboltTableActiveNextTr('#myTable');
```

## 列操作API

### jboltTableHideColumn(tableEle, column)

隐藏指定列。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `column` {String} - 列名

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableHideColumn('#myTable', 'age');
```

### jboltTableShowColumn(tableEle, column)

显示指定列。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `column` {String} - 列名

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableShowColumn('#myTable', 'age');
```

### jboltTableHideColumnByIndex(tableEle, columnIndex)

按索引隐藏列。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `columnIndex` {Number} - 列索引

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableHideColumnByIndex('#myTable', 2);
```

### jboltTableShowColumnByIndex(tableEle, columnIndex)

按索引显示列。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `columnIndex` {Number} - 列索引

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableShowColumnByIndex('#myTable', 2);
```

### jboltTableBatchSetColumn(tableEle, column, text, value, dontExeValueChangeHandler)

批量设置列的值。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `column` {String} - 列名
- `text` {String} - 显示文本
- `value` {Any} - 实际值
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** 无

**示例：**
```javascript
jboltTableBatchSetColumn('#myTable', 'status', '启用', 1);
```

### jboltTableBatchSetColumns(tableEle, columnsJsonData, dontExeValueChangeHandler)

批量设置多列的值。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `columnsJsonData` {Array} - 列数据数组，格式：[{column: 'name', text: '文本', value: '值'}]
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** 无

**示例：**
```javascript
jboltTableBatchSetColumns('#myTable', [
    { column: 'status', text: '启用', value: 1 },
    { column: 'updateTime', text: '2023-01-01', value: '2023-01-01' }
]);
```

## 单元格操作API

### jboltTableSetCell(tableEle, tr, column, text, value, dontExeValueChangeHandler)

设置单元格的值。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `tr` {jQuery} - 行元素
- `column` {String} - 列名
- `text` {String} - 显示文本
- `value` {Any} - 实际值
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tr = $('#myTable tbody tr:first');
jboltTableSetCell('#myTable', tr, 'name', '张三', '张三');
```

### jboltTableSetCells(tableEle, tr, columnsJsonData, dontExeValueChangeHandler)

批量设置行中多个单元格的值。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `tr` {jQuery} - 行元素
- `columnsJsonData` {Array} - 列数据数组
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tr = $('#myTable tbody tr:first');
jboltTableSetCells('#myTable', tr, [
    { column: 'name', text: '张三', value: '张三' },
    { column: 'age', text: '25', value: 25 }
]);
```

### jboltTableSetCellEditable(tableEle, tr, column, editable, falseClear, clearValue, dontExeValueChangeHandler)

设置单元格是否可编辑。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `tr` {jQuery} - 行元素
- `column` {String} - 列名
- `editable` {Boolean} - 是否可编辑
- `falseClear` {Boolean} - 如果设为不可编辑，是否清空数据
- `clearValue` {Any} - 清空时使用的值
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tr = $('#myTable tbody tr:first');
// 设为不可编辑
jboltTableSetCellEditable('#myTable', tr, 'name', false);

// 设为不可编辑并清空
jboltTableSetCellEditable('#myTable', tr, 'name', false, true, '');
```

### jboltTableSetCellsEditable(tableEle, tr, columnsJsonData, dontExeValueChangeHandler)

批量设置多个单元格的可编辑状态。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `tr` {jQuery} - 行元素
- `columnsJsonData` {Array} - 列配置数组，格式：[{column: 'name', editable: true, falseClear: false, clearValue: ''}]
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tr = $('#myTable tbody tr:first');
jboltTableSetCellsEditable('#myTable', tr, [
    { column: 'name', editable: false },
    { column: 'age', editable: true }
]);
```

### jboltTableSetAttrValue(tableEle, tr, attr, value, dontExeValueChangeHandler)

设置行数据中不显示在表格中的属性值。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `tr` {jQuery} - 行元素
- `attr` {String} - 属性名
- `value` {Any} - 属性值
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tr = $('#myTable tbody tr:first');
jboltTableSetAttrValue('#myTable', tr, 'hiddenField', 'hiddenValue');
```

### jboltTableSetAttrsValue(tableEle, tr, attrValues, dontExeValueChangeHandler)

批量设置行数据中的多个属性值。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `tr` {jQuery} - 行元素
- `attrValues` {Array} - 属性数组，格式：[{attr: 'field1', value: 'value1'}]
- `dontExeValueChangeHandler` {Boolean} - 是否不触发值变化事件

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tr = $('#myTable tbody tr:first');
jboltTableSetAttrsValue('#myTable', tr, [
    { attr: 'field1', value: 'value1' },
    { attr: 'field2', value: 'value2' }
]);
```

## 选择操作API

### jboltTableGetCheckedId(tableEle, dontShowError)

获取选中行的ID（单选）。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {String|Number|Boolean} - 选中行的ID，未选中返回false

**示例：**
```javascript
var checkedId = jboltTableGetCheckedId('#myTable');
if (checkedId) {
    console.log('选中的ID:', checkedId);
}
```

### jboltTableGetCheckedIds(tableEle, dontShowError)

获取所有选中行的ID数组。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|Boolean} - 选中行的ID数组，未选中返回false

**示例：**
```javascript
var checkedIds = jboltTableGetCheckedIds('#myTable');
if (checkedIds && checkedIds.length > 0) {
    console.log('选中的IDs:', checkedIds);
}
```

### jboltTableGetCheckedData(tableEle, needAttrs, dontShowError)

获取选中行的数据（单选）。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `needAttrs` {Array} - 需要的字段数组，不传则返回所有字段
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Object|Boolean} - 选中行的数据对象，未选中返回false

**示例：**
```javascript
// 获取所有字段
var checkedData = jboltTableGetCheckedData('#myTable');

// 获取指定字段
var checkedData = jboltTableGetCheckedData('#myTable', ['name', 'age', 'email']);
```

### jboltTableGetCheckedDatas(tableEle, needAttrs, dontShowError)

获取所有选中行的数据数组。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `needAttrs` {Array} - 需要的字段数组
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|Boolean} - 选中行的数据数组，未选中返回false

**示例：**
```javascript
var checkedDatas = jboltTableGetCheckedDatas('#myTable', ['name', 'age']);
```

### jboltTableGetCheckedCols(tableEle, column, dontShowError)

获取选中行指定列的值数组。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `column` {String} - 列名
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|Boolean} - 指定列的值数组，未选中返回false

**示例：**
```javascript
var names = jboltTableGetCheckedCols('#myTable', 'name');
```

### jboltTableGetCheckedTexts(tableEle, dontShowError)

获取选中行的显示文本数组。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|Boolean} - 显示文本数组，未选中返回false

**示例：**
```javascript
var texts = jboltTableGetCheckedTexts('#myTable');
```

### jboltTableSetCheckedId(tableEle, id)

设置选中指定ID的行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `id` {String|Number} - 要选中的行ID

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableSetCheckedId('#myTable', '123');
```

### jboltTableSetCheckedIds(tableEle, ids)

设置选中指定ID数组的行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `ids` {Array} - 要选中的行ID数组

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableSetCheckedIds('#myTable', ['123', '456', '789']);
```

### jboltTableIsCheckedAll(tableEle)

检查是否全选。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 是否全选

**示例：**
```javascript
if (jboltTableIsCheckedAll('#myTable')) {
    console.log('已全选');
}
```

### jboltTableIsCheckedNone(tableEle)

检查是否一个都没选。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 是否一个都没选

**示例：**
```javascript
if (jboltTableIsCheckedNone('#myTable')) {
    console.log('没有选中任何行');
}
```

### jboltTableGetCheckedCount(tableEle)

获取选中行的数量。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Number} - 选中行的数量

**示例：**
```javascript
var count = jboltTableGetCheckedCount('#myTable');
console.log('选中了', count, '行');
```

### jboltTableCheckAll(tableEle)

全选所有行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** 无

**示例：**
```javascript
jboltTableCheckAll('#myTable');
```

### jboltTableUncheckAll(tableEle)

取消全选。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** 无

**示例：**
```javascript
jboltTableUncheckAll('#myTable');
```

### jboltTableConvertCheckAll(tableEle)

反选所有行。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** 无

**示例：**
```javascript
jboltTableConvertCheckAll('#myTable');
```

## 数据操作API

### jboltTableGetAllDatas(tableEle, needAttrs)

获取表格所有行的数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `needAttrs` {Array} - 需要的字段数组，不传则返回所有字段

**返回值：** {Array|Boolean} - 所有行的数据数组，失败返回false

**示例：**
```javascript
// 获取所有数据
var allData = jboltTableGetAllDatas('#myTable');

// 获取指定字段
var allData = jboltTableGetAllDatas('#myTable', ['name', 'age']);
```

### jboltTableGetOneColumnDatas(tableEle, attrName, withBlankDatas)

获取指定列的所有数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `attrName` {String} - 列名
- `withBlankDatas` {Boolean} - 是否包含空值，默认false

**返回值：** {Array|Boolean} - 指定列的数据数组，失败返回false

**示例：**
```javascript
// 获取姓名列的所有数据（不包含空值）
var names = jboltTableGetOneColumnDatas('#myTable', 'name');

// 获取姓名列的所有数据（包含空值）
var names = jboltTableGetOneColumnDatas('#myTable', 'name', true);
```

### jboltTableGetRowJsonData(tableEle, rowOrIndex, dontShowError)

获取指定行的数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `rowOrIndex` {jQuery|Number} - 行元素或行索引
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Object|null} - 行数据对象，失败返回null

**示例：**
```javascript
// 通过行元素获取
var tr = $('#myTable tbody tr:first');
var rowData = jboltTableGetRowJsonData('#myTable', tr);

// 通过索引获取
var rowData = jboltTableGetRowJsonData('#myTable', 0);
```

## 可编辑表格API

### jboltTableGetSubmitData(tableEle)

获取可编辑表格的提交数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Object|Boolean} - 提交数据对象，失败返回false

**返回数据结构：**
```javascript
{
    insertDatas: [], // 新增的数据
    updateDatas: [], // 修改的数据
    deleteDatas: [], // 删除的数据
    formDatas: {}    // 关联表单的数据
}
```

**示例：**
```javascript
var submitData = jboltTableGetSubmitData('#editableTable');
if (submitData) {
    console.log('新增:', submitData.insertDatas);
    console.log('修改:', submitData.updateDatas);
    console.log('删除:', submitData.deleteDatas);
}
```

### jboltTableGetSubmitDataMulti(tableEles)

获取多个可编辑表格的提交数据。

**参数：**
- `tableEles` {Array} - 表格元素数组

**返回值：** {Object|Boolean} - 提交数据对象，失败返回false

**示例：**
```javascript
var tables = ['#table1', '#table2', '#table3'];
var submitData = jboltTableGetSubmitDataMulti(tables);
```

### jboltTableSubmitMulti(arr, url, successCallback, failCallback)

提交多个可编辑表格的数据。

**参数：**
- `arr` {Array} - 表格元素数组
- `url` {String} - 提交URL
- `successCallback` {Function} - 成功回调
- `failCallback` {Function} - 失败回调

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
var tables = ['#table1', '#table2'];
jboltTableSubmitMulti(tables, '/api/save-multi', 
    function(result) {
        console.log('提交成功:', result);
    },
    function(error) {
        console.error('提交失败:', error);
    }
);
```

### finishEditingCells(tableEle, dontProcessExtraSomething)

完成当前正在编辑的单元格。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `dontProcessExtraSomething` {Boolean} - 是否不执行额外操作

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 完成编辑
finishEditingCells('#editableTable');

// 完成编辑但不执行额外操作
finishEditingCells('#editableTable', true);
```

### checkEditableCellRequired(tableEle, trs)

检查可编辑表格的必填字段。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `trs` {jQuery} - 限定检查的行范围，可选

**返回值：** {Boolean} - 验证是否通过

**示例：**
```javascript
// 检查所有行
if (checkEditableCellRequired('#editableTable')) {
    console.log('验证通过');
}

// 检查指定行
var specificTrs = $('#editableTable tbody tr:lt(5)');
if (checkEditableCellRequired('#editableTable', specificTrs)) {
    console.log('前5行验证通过');
}
```

### checkEditableCheckedTrCellRequired(tableEle)

检查选中行的必填字段。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 验证是否通过

**示例：**
```javascript
if (checkEditableCheckedTrCellRequired('#editableTable')) {
    console.log('选中行验证通过');
}
```

### changeJBoltTableEditableOptions(tableEle, newOptions)

更改表格的可编辑配置。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `newOptions` {Object} - 新的配置选项

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
changeJBoltTableEditableOptions('#editableTable', {
    trigger: 'dblclick',
    cols: {
        name: { type: 'text', required: true }
    }
});
```

## 扩展功能API

### jboltTableHideBox(tableEle, boxType)

隐藏表格扩展区域。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `boxType` {String} - 区域类型：'headbox', 'footbox', 'leftbox', 'rightbox'

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableHideBox('#myTable', 'headbox');
```

### jboltTableShowBox(tableEle, boxType)

显示表格扩展区域。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `boxType` {String} - 区域类型

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableShowBox('#myTable', 'headbox');
```

### jboltTableToggleBox(tableEle, boxType)

切换表格扩展区域的显示状态。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `boxType` {String} - 区域类型

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableToggleBox('#myTable', 'headbox');
```

### 便捷方法

以下是扩展区域操作的便捷方法：

```javascript
// 头部区域
jboltTableHideHeadbox(tableEle)
jboltTableShowHeadbox(tableEle)
jboltTableToggleHeadbox(tableEle)

// 底部区域
jboltTableHideFootbox(tableEle)
jboltTableShowFootbox(tableEle)
jboltTableToggleFootbox(tableEle)

// 左侧区域
jboltTableHideLeftbox(tableEle)
jboltTableShowLeftbox(tableEle)
jboltTableToggleLeftbox(tableEle)

// 右侧区域
jboltTableHideRightbox(tableEle)
jboltTableShowRightbox(tableEle)
jboltTableToggleRightbox(tableEle)
```

### jboltTableShowColumnConfigDialog(tableEle)

显示列配置对话框。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableShowColumnConfigDialog('#myTable');
```

### jboltTableExpandAll(tableEle)

展开树形表格的所有节点。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableExpandAll('#treeTable');
```

### jboltTableCollapseAll(tableEle)

折叠树形表格的所有节点。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableCollapseAll('#treeTable');
```

## 过滤和搜索API

### jboltTableMenuFilter(tableEle)

执行菜单过滤。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableMenuFilter('#myTable');
```

### jboltTableMenuFilterByKeywords(tableEle, keywords, include, pageSize)

按关键词过滤表格数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `keywords` {String} - 关键词
- `include` {Boolean} - 是否包含关键词
- `pageSize` {Number} - 每页条数

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 搜索包含"张三"的记录
jboltTableMenuFilterByKeywords('#myTable', '张三', true, 20);

// 搜索不包含"张三"的记录
jboltTableMenuFilterByKeywords('#myTable', '张三', false, 20);
```

### jboltTableMenuAddFilterItem(tableEle, column, comparison, value)

添加过滤条件。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `column` {String} - 列名
- `comparison` {String} - 比较操作符：'eq', 'ne', 'gt', 'ge', 'lt', 'le', 'like'
- `value` {Any} - 过滤值

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 添加年龄大于25的过滤条件
jboltTableMenuAddFilterItem('#myTable', 'age', 'gt', 25);

// 添加姓名包含"张"的过滤条件
jboltTableMenuAddFilterItem('#myTable', 'name', 'like', '张');
```

### jboltTableMenuClearFilter(tableEle)

清空过滤条件。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableMenuClearFilter('#myTable');
```

### jboltTableFilterByKeywords(tableEle, keywords, colIndexArr)

前端快速过滤（不请求服务器）。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `keywords` {String} - 关键词
- `colIndexArr` {Array} - 指定搜索的列索引数组，不传则搜索所有列

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在所有列中搜索
jboltTableFilterByKeywords('#myTable', '张三');

// 在指定列中搜索
jboltTableFilterByKeywords('#myTable', '张三', [0, 1, 2]);
```

### jboltTableSubmitConditionsForm(tableEle)

提交表格绑定的查询表单。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
jboltTableSubmitConditionsForm('#myTable');
```

## 工具方法API

### getJBoltTable(tableEle)

获取JBoltTable的jQuery对象。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {jQuery} - 表格的jQuery对象

**示例：**
```javascript
var table = getJBoltTable('#myTable');
```

### getJBoltTableInst(tableEle)

获取JBoltTable的实例对象。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Object|null} - 表格实例对象

**示例：**
```javascript
var tableInst = getJBoltTableInst('#myTable');
if (tableInst) {
    console.log('表格实例:', tableInst);
}
```

### jboltTableGetCellSelectText(td)

获取单元格中选中的文本。

**参数：**
- `td` {jQuery} - 单元格元素

**返回值：** {String|null} - 选中的文本

**示例：**
```javascript
$('#myTable').on('click', 'td', function() {
    var selectedText = jboltTableGetCellSelectText($(this));
    if (selectedText) {
        console.log('选中文本:', selectedText);
    }
});
```

### 保持选择相关API

#### jboltTableGetKeepSelectedDatas(tableEle, needAttrs, dontShowError)

获取保持选择的数据。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `needAttrs` {Array} - 需要的字段数组
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|null} - 保持选择的数据数组

#### jboltTableGetKeepSelectedIds(tableEle, dontShowError)

获取保持选择的ID数组。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|null} - 保持选择的ID数组

#### jboltTableGetKeepSelectedTexts(tableEle, dontShowError)

获取保持选择的文本数组。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** {Array|null} - 保持选择的文本数组

#### jboltTableRemoveKeepSelectedItem(tableEle, removeId, dontShowError)

移除指定的保持选择项。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器
- `removeId` {String|Number} - 要移除的ID
- `dontShowError` {Boolean} - 是否不显示错误信息

**返回值：** 无

## 弹窗选择相关API

### jboltTableChooseAndInsert(ele)

打开选择对话框并插入数据。

**参数：**
- `ele` {jQuery|Element} - 触发元素

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// HTML
<button data-jbolt-table-id="myTable" onclick="jboltTableChooseAndInsert(this)">选择插入</button>
```

### jboltTableInsertRowsByDialogChooser(action, datas, insertType, keepId, dontProcessChange, forceTrChange)

通过对话框选择器插入行数据。

**参数：**
- `action` {jQuery|Element} - 触发元素
- `datas` {Array|Object} - 选择的数据
- `insertType` {String} - 插入类型：'prepend', 'append', 'before', 'after', 'replace', 'merge'
- `keepId` {Boolean} - 是否保留ID
- `dontProcessChange` {Boolean} - 是否不处理变更标记
- `forceTrChange` {Boolean} - 是否强制标记为已变更

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在对话框的确认回调中调用
function onDialogConfirm(selectedDatas) {
    jboltTableInsertRowsByDialogChooser(
        $('#insertBtn'), 
        selectedDatas, 
        'append', 
        false, 
        false, 
        true
    );
}
```

### jboltTableProcessTdByDialogChooser(text, value, data)

处理对话框选择器选择的单元格数据。

**参数：**
- `text` {String} - 显示文本
- `value` {Any} - 实际值
- `data` {Object} - 完整数据对象

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在对话框选择回调中使用
function onCellDialogSelect(text, value, data) {
    jboltTableProcessTdByDialogChooser(text, value, data);
}
```

## 主从表格API

### masterTableTrTriggerShowSlave(ele, id, masterOtherParams, tableCallback, ajaxPortalCallback)

主表行点击触发从表显示。

**参数：**
- `ele` {jQuery|Element} - 触发的行元素
- `id` {String|Number} - 主表记录ID
- `masterOtherParams` {Object} - 其他参数
- `tableCallback` {Function} - 从表加载完成回调
- `ajaxPortalCallback` {Function} - Ajax门户加载完成回调

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
$('#masterTable').on('click', 'tbody tr', function() {
    var id = $(this).data('id');
    masterTableTrTriggerShowSlave(this, id, {}, function(slaveTable) {
        console.log('从表加载完成:', slaveTable);
    });
});
```

### resetJBolttableSlaveBox(masterTable)

重置从表区域。

**参数：**
- `masterTable` {jQuery} - 主表元素

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
resetJBolttableSlaveBox($('#masterTable'));
```

## 复制行相关API

### jboltTableCopyCheckedRowInsertBefore(tableEle)

复制选中行并在前面插入。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

### jboltTableCopyCheckedRowInsertAfter(tableEle)

复制选中行并在后面插入。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

### jboltTableCopyCheckedRowPrepend(tableEle)

复制选中行并插入到开头。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

### jboltTableCopyCheckedRowAppend(tableEle)

复制选中行并插入到末尾。

**参数：**
- `tableEle` {String|jQuery|Element} - 表格元素或选择器

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 复制选中行到末尾
jboltTableCopyCheckedRowAppend('#myTable');
```

## 表单辅助API

### jboltTableSaveFormToTableCurrentActiveTr(formEle, confirm, dontProcessIfNotExistActiveTr)

将表单数据保存到当前激活的表格行。

**参数：**
- `formEle` {jQuery|Element} - 表单元素
- `confirm` {Boolean} - 是否需要确认
- `dontProcessIfNotExistActiveTr` {Boolean} - 如果没有激活行是否不处理

**返回值：** {Boolean} - 操作是否成功

**示例：**
```javascript
// 在表单提交时调用
$('#myForm').on('submit', function(e) {
    e.preventDefault();
    jboltTableSaveFormToTableCurrentActiveTr(this, false);
});
```

## 键盘操作相关API

### activeCurrentFocusTdToEditor(table)

激活当前焦点单元格进入编辑状态。

**参数：**
- `table` {Object} - 表格实例对象

**返回值：** 无

### clearJBoltCurrentEditableAndKeyEventTable()

清空当前可编辑表格的键盘操作对象。

**返回值：** 无

### changeJBoltCurrentEditableAndKeyEventTable(table)

切换当前可编辑表格的键盘操作对象。

**参数：**
- `table` {Object} - 新的表格实例对象

**返回值：** 无

### getCurrentEditableAndKeyEventJBoltTable()

获取当前可编辑表格的键盘操作对象。

**返回值：** {Object|null} - 当前表格实例对象

### getCurrentEditableAndKeyEventJBoltTables()

获取当前所有可编辑表格的键盘操作对象数组。

**返回值：** {Array|Boolean} - 表格实例对象数组

## 错误处理

所有API方法在遇到错误时的处理方式：

1. **参数错误**：返回 `false` 并在控制台输出错误信息
2. **表格未找到**：显示 "表格配置异常，无法找到对应表格" 提示
3. **功能不支持**：显示相应的功能限制提示
4. **数据验证失败**：显示验证错误信息

## 返回值说明

- **Boolean**: `true` 表示操作成功，`false` 表示操作失败
- **Object**: 返回数据对象，失败时返回 `null` 或 `false`
- **Array**: 返回数据数组，失败时返回 `null` 或 `false`
- **String/Number**: 返回具体值，失败时返回 `null` 或 `false`

## 使用建议

1. **错误检查**：调用API后检查返回值，确保操作成功
2. **参数验证**：传入参数前进行有效性检查
3. **异步操作**：涉及服务器请求的操作使用回调函数处理结果
4. **性能考虑**：批量操作时使用对应的批量API方法

---

*本API参考手册涵盖了JBoltTable组件的所有公开方法，更多使用示例请参考主文档和示例代码。*