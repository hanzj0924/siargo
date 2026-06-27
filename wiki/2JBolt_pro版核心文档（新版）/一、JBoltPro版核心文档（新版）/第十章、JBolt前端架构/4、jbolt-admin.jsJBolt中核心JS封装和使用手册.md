# JBolt Admin JS 核心库使用指南

> JBolt Admin JS 版本：7.3.1  
> 作者：JBolt团队  
> 适用于：后台管理系统前端开发

## 简介

JBolt Admin JS 是一个功能丰富的前端JavaScript库，专为后台管理系统设计，集成了表格、表单、文件上传、树形控件、弹窗、图表等多种常用组件，提供了完整的前端解决方案。

## 目录结构

- [1. 核心工具类](#1-核心工具类)
- [2. 表单组件](#2-表单组件)
- [3. 表格组件](#3-表格组件)
- [4. 文件上传组件](#4-文件上传组件)
- [5. 弹窗和消息组件](#5-弹窗和消息组件)
- [6. 树形控件](#6-树形控件)
- [7. 图像组件](#7-图像组件)
- [8. 富文本编辑器](#8-富文本编辑器)
- [9. 日期时间组件](#9-日期时间组件)
- [10. 网络请求](#10-网络请求)
- [11. 插件管理](#11-插件管理)
- [12. 页面导航](#12-页面导航)
- [13. 实用工具](#13-实用工具)

## 1. 核心工具类

### 1.1 数据验证工具

```javascript
// 判断数据是否有效
isOk(obj)        // 判断对象是否有值
notOk(obj)       // 判断对象是否无值
isArray(obj)     // 判断是否为数组
```

**使用示例：**
```javascript
if (isOk(userData)) {
    console.log('用户数据有效');
}

if (isArray(list)) {
    list.forEach(item => console.log(item));
}
```

### 1.2 字符串处理工具 (StrUtil)

```javascript
// 字符串连接
StrUtil.join('a', 'b', 'c', ';')  // 返回: "a;b;c"

// 驼峰转下划线
StrUtil.underline('userRole', true)   // 返回: "USER_ROLE"
StrUtil.underline('userRole', false)  // 返回: "user_role"

// 下划线转驼峰
StrUtil.camel('USER_ROLE', true)   // 返回: "UserRole"
StrUtil.camel('USER_ROLE', false)  // 返回: "userRole"
```

### 1.3 数组操作工具 (JBoltArrayUtil)

```javascript
// 删除指定值
JBoltArrayUtil.remove(array, value)

// 按索引替换
JBoltArrayUtil.replace(array, index, newData)

// 首部插入
JBoltArrayUtil.prepend(array, data)

// 尾部添加
JBoltArrayUtil.append(array, data)

// 按索引插入
JBoltArrayUtil.insert(array, index, data)

// 数组去重
JBoltArrayUtil.unique(array)
```

## 2. 表单组件

### 2.1 表单验证 (FormChecker)

```javascript
// 检查表单
FormChecker.check(form)

// 检查单个元素
FormChecker.checkIt(element)

// 设置错误样式
showFormEleErrorStyle(element, message)
```

**HTML配置：**
```html
<input type="text" data-rule="required" data-tips="请输入用户名" />
<input type="email" data-rule="email" data-tips="请输入正确的邮箱" />
<input type="number" data-rule="number|min:1|max:100" />
```

### 2.2 下拉选择框 (SelectUtil)

```javascript
// 初始化
SelectUtil.init(parentElement)

// 手动设置选中值
SelectUtil.setSelectedValue(selectElement, value)
```

**HTML配置：**
```html
<select data-autoload data-url="api/options" data-value-attr="id" data-text-attr="name">
    <option value="">请选择</option>
</select>
```

### 2.3 单选框组件 (RadioUtil)

```javascript
// 初始化
RadioUtil.init(parentElement)

// 设置选中
RadioUtil.setChecked(element, name, value, text)

// 获取选中值
RadioUtil.getCheckedValue(name, parentElement)
```

**HTML配置：**
```html
<div data-radio data-name="gender" data-value="1,2" data-text="男,女">
    <label><input type="radio" name="gender" value="1"> 男</label>
    <label><input type="radio" name="gender" value="2"> 女</label>
</div>
```

### 2.4 复选框组件 (CheckboxUtil)

```javascript
// 初始化
CheckboxUtil.init(parentElement)

// 设置选中
CheckboxUtil.setChecked(element, name, values)

// 获取选中值
CheckboxUtil.getCheckedValueToString(name, separator, parentElement)
```

### 2.5 开关按钮 (SwitchBtnUtil)

```javascript
// 初始化
SwitchBtnUtil.init(parentElement)
```

**HTML配置：**
```html
<input type="checkbox" data-switchbtn data-url="api/toggle" data-size="small" />
```

### 2.6 自动完成组件 (AutocompleteUtil)

```javascript
// 初始化
AutocompleteUtil.init(parentElement)

// 刷新缓存
AutocompleteUtil.flushCache(inputElement)
```

**HTML配置：**
```html
<input type="text" 
       data-autocomplete 
       data-url="api/search" 
       data-hidden-input="hiddenId"
       data-text-attr="name"
       data-value-attr="id"
       data-column-attr="name,code"
       data-limit="20" />
```

### 2.7 JBolt输入组件 (JBoltInputUtil)

支持下拉选择、树形选择等复杂输入组件。

```javascript
// 初始化
JBoltInputUtil.init(parentElement)

// 设置值
JBoltInputUtil.setValue(element, text, value, jsonData)
```

**HTML配置：**
```html
<!-- 下拉表格选择 -->
<input type="text" 
       data-jboltinput 
       data-load-type="ajaxportal"
       data-url="api/options"
       data-hidden-input="hiddenId" />

<!-- 树形选择 -->
<input type="text" 
       data-jboltinput 
       data-load-type="jstree"
       data-url="api/tree"
       data-jstree-checkbox="true" />
```

## 3. 表格组件

### 3.1 JBoltTable 表格组件

强大的数据表格组件，支持分页、排序、编辑等功能。

```javascript
// 初始化
JBoltTableUtil.init(parentElement)

// 获取表格实例
var table = JBoltTableUtil.get(tableElement)

// 获取选中行ID
jboltTableGetCheckedIds(buttonElement)
```

**HTML配置：**
```html
<table data-jbolttable 
       data-url="api/list"
       data-page="true"
       data-page-size="20"
       data-checkbox="true">
    <thead>
        <tr>
            <th data-checkbox="true"></th>
            <th data-field="name">姓名</th>
            <th data-field="email">邮箱</th>
        </tr>
    </thead>
</table>
```

### 3.2 JBoltTreeTable 树形表格

```javascript
// 初始化
JBoltTreeTableUtil.init(parentElement)
```

## 4. 文件上传组件

### 4.1 图片上传 (ImgUploadUtil)

```javascript
// 初始化
ImgUploadUtil.init(parentElement)
```

**HTML配置：**
```html
<div class="j_img_uploder" 
     data-handler="uploadImg"
     data-url="api/upload"
     data-imgbox="imgBox"
     data-hidden-input="imgPath"
     data-placeholder="点击上传图片">
</div>
<ul id="imgBox" class="j_img_box"></ul>
```

### 4.2 文件上传 (FileUploadUtil)

```javascript
// 初始化
FileUploadUtil.init(parentElement)
```

**HTML配置：**
```html
<div class="j_upload_file_box"
     data-handler="uploadFile"
     data-url="api/upload"
     data-accept="pdf,doc,docx"
     data-maxsize="10"
     data-hidden-input="filePath">
</div>
```

### 4.3 多文件上传 (MultipleFileInputUtil)

支持拖拽上传、预览等功能。

```javascript
// 初始化
MultipleFileInputUtil.init(parentElement)
```

## 5. 弹窗和消息组件

### 5.1 Layer消息框 (LayerMsgBox)

```javascript
// 成功消息
LayerMsgBox.success("操作成功", 1000, callback)

// 错误消息
LayerMsgBox.error("操作失败", 2000)

// 确认对话框
LayerMsgBox.confirm("确认删除？", function() {
    // 确认回调
}, function() {
    // 取消回调
})

// 提示框
LayerMsgBox.alert("提示信息", 2)

// 加载中
LayerMsgBox.loading("加载中...", 5000)

// 输入框
LayerMsgBox.prompt("请输入", "默认值", function(index, text) {
    // 输入回调
})
```

### 5.2 弹窗工具 (DialogUtil)

```javascript
// 打开对话框
DialogUtil.open({
    title: "标题",
    url: "page.html",
    width: 800,
    height: 600,
    handler: callback
})

// 关闭对话框
DialogUtil.close()
```

**HTML配置：**
```html
<button data-openpage="dialog" 
        data-url="edit.html" 
        data-title="编辑"
        data-area="800,600"
        data-handler="refreshTable">编辑</button>
```

### 5.3 侧边抽屉 (JBoltLayerUtil)

从左侧或右侧滑出的抽屉组件。

```javascript
// 初始化
JBoltLayerUtil.init()

// 打开抽屉
JBoltLayerUtil.openByNav(url, options)
```

**HTML配置：**
```html
<button data-jboltlayertrigger 
        data-url="details.html"
        data-dir="right"
        data-width="600">查看详情</button>
```

## 6. 树形控件

### 6.1 JSTree组件 (JSTreeUtil)

功能强大的树形控件，支持复选框、拖拽、右键菜单等。

```javascript
// 初始化
JSTreeUtil.init(parentElement)

// 刷新树
JSTreeUtil.refresh(treeElement, selectId)

// 设置选中
JSTreeUtil.setChecked(treeElement, nodeIds)
```

**HTML配置：**
```html
<!-- 基础树 -->
<div data-jstree 
     data-read-url="api/tree"
     data-change-handler="onTreeChange">
</div>

<!-- 带复选框的树 -->
<div data-jstree 
     data-read-url="api/tree"
     data-checkbox="true"
     data-onlyleaf="true">
</div>

<!-- 可编辑树（增删改） -->
<div data-jstree 
     data-curd="true"
     data-target="dialog"
     data-add-url="api/add/"
     data-edit-url="api/edit/"
     data-delete-url="api/delete/">
</div>
```

## 7. 图像组件

### 7.1 图片查看器 (ImageViewerUtil)

```javascript
// 初始化
ImageViewerUtil.init(parentElement)
```

**HTML配置：**
```html
<!-- 单图查看 -->
<img data-imgviewer src="image.jpg" />

<!-- 相册查看 -->
<a data-imgviewer data-album="gallery" data-url="image1.jpg">图片1</a>
<a data-imgviewer data-album="gallery" data-url="image2.jpg">图片2</a>

<!-- 容器查看 -->
<div data-imgviewer>
    <img src="image1.jpg" />
    <img src="image2.jpg" />
</div>
```

## 8. 富文本编辑器

### 8.1 HTML编辑器 (HtmlEditorUtil)

支持Summernote和UEditor两种编辑器。

```javascript
// 初始化
HtmlEditorUtil.init(parentElement)
```

**HTML配置：**
```html
<!-- Summernote编辑器 -->
<textarea data-summernote 
          data-height="300"
          data-upload-url="api/upload">
</textarea>

<!-- UEditor编辑器 -->
<textarea data-neditor 
          data-width="100%"
          data-height="400">
</textarea>
```

## 9. 日期时间组件

### 9.1 日期选择器 (FormDate)

```javascript
// 初始化
FormDate.init(parentElement)

// 隐藏日期选择器
FormDate.hide()
```

**HTML配置：**
```html
<!-- 日期选择 -->
<input type="text" data-date data-format="yyyy-MM-dd" />

<!-- 时间选择 -->
<input type="text" data-time data-format="HH:mm:ss" />

<!-- 日期时间选择 -->
<input type="text" data-datetime data-format="yyyy-MM-dd HH:mm:ss" />

<!-- 日期范围选择 -->
<input type="text" data-daterange data-separator=" ~ " />
```

## 10. 网络请求

### 10.1 Ajax工具类

```javascript
// GET请求
Ajax.get("api/data", function(response) {
    console.log(response);
}, function(error) {
    console.error(error);
})

// POST请求
Ajax.post("api/save", {name: "张三"}, function(response) {
    console.log("保存成功");
})

// 表单提交
Ajax.formSubmit(formElement, function(response) {
    LayerMsgBox.success("提交成功");
})
```

### 10.2 下载工具 (DownloadUtil)

```javascript
// 初始化下载按钮
DownloadUtil.init()

// 手动下载
DownloadUtil.download({
    url: "api/export",
    fileName: "数据.xlsx",
    params: {id: 123}
})
```

**HTML配置：**
```html
<button data-downloadbtn 
        data-url="api/export"
        data-filename="数据.xlsx"
        data-form="searchForm">导出</button>
```

## 11. 插件管理

### 11.1 插件加载器

```javascript
// 加载单个插件
loadJBoltPlugin(['summernote'], function() {
    // 插件加载完成后的回调
})

// 加载多个插件
loadJBoltPlugin(['jbolttable', 'jstree'], function() {
    // 所有插件加载完成
})

// 异步加载CSS和JS
AssetsLazyLoad.js(['path/to/script.js'], function() {
    console.log('JS加载完成');
})

AssetsLazyLoad.css(['path/to/style.css'], function() {
    console.log('CSS加载完成');
})
```

## 12. 页面导航

### 12.1 PJAX导航 (JBoltPjaxUtil)

无刷新页面跳转。

```javascript
// 初始化PJAX
JBoltPjaxUtil.initAdminPjax()

// 跳转页面
JBoltPjaxUtil.toUrl(url, title)
```

### 12.2 Tab标签页 (JBoltTabUtil)

多标签页管理。

```javascript
// 初始化
JBoltTabUtil.init()

// 添加标签页
JBoltTabUtil.addJboltTab(url, title, closeable)

// 关闭标签页
JBoltTabUtil.closeJboltTab(tabId)
```

## 13. 实用工具

### 13.1 城市选择器 (CityPickerUtil)

```javascript
// 初始化
CityPickerUtil.init(parentElement)
```

**HTML配置：**
```html
<div data-citypicker 
     data-setvalueto="province:provinceName:name_last;city:cityName:name_last"
     data-level="3">
</div>
```

### 13.2 滑动条 (RangeSliderUtil)

```javascript
// 初始化
RangeSliderUtil.init(parentElement)

// 更新配置
RangeSliderUtil.update(element, options)
```

### 13.3 工具提示

```javascript
// 初始化提示
initToolTip(parentElement)

// 初始化弹出框
initPopover(parentElement)
```

**HTML配置：**
```html
<button tooltip data-title="这是提示信息">按钮</button>
<button data-bs-toggle="popover" data-content="弹出内容">弹出框</button>
```

### 13.4 全屏控制

```javascript
// 进入全屏
launchFullscreen(element)

// 退出全屏
exitFullscreen(element)

// 切换全屏
toggleFullScreen(element)
```

### 13.5 剪贴板工具 (JBoltClipboardUtil)

```javascript
// 初始化
JBoltClipboardUtil.init()
```

**HTML配置：**
```html
<button data-clipboard-text="要复制的文本">复制文本</button>
<button data-clipboard-target="#copyTarget">复制元素内容</button>
```

## 最佳实践

### 1. 组件初始化

推荐在页面加载完成后统一初始化所有组件：

```javascript
$(function() {
    // 初始化所有表单组件
    FormDate.init();
    SelectUtil.init();
    AutocompleteUtil.init();
    
    // 初始化表格
    JBoltTableUtil.init();
    
    // 初始化上传组件
    FileUploadUtil.init();
    ImgUploadUtil.init();
});
```

### 2. 错误处理

统一的错误处理机制：

```javascript
Ajax.get("api/data", function(response) {
    if (response.state === "ok") {
        // 处理成功响应
    } else {
        LayerMsgBox.error(response.msg || "操作失败");
    }
}, function(error) {
    LayerMsgBox.error("网络异常，请稍后重试");
});
```

### 3. 表单验证

合理使用表单验证规则：

```html
<form id="userForm">
    <input type="text" data-rule="required|minlength:2" data-tips="用户名不能少于2个字符" />
    <input type="email" data-rule="required|email" data-tips="请输入正确的邮箱地址" />
    <input type="password" data-rule="required|minlength:6" data-tips="密码不能少于6位" />
</form>

<script>
$('#userForm').on('submit', function(e) {
    e.preventDefault();
    if (FormChecker.check($(this))) {
        // 提交表单
        Ajax.formSubmit($(this));
    }
});
</script>
```

### 4. 组件配置

充分利用HTML5 data属性进行组件配置，保持JS代码的简洁：

```html
<!-- 推荐：声明式配置 -->
<table data-jbolttable 
       data-url="api/users"
       data-page="true"
       data-checkbox="true">
</table>

<!-- 不推荐：命令式配置 -->
<script>
$('#userTable').jboltTable({
    url: 'api/users',
    page: true,
    checkbox: true
});
</script>
```



## 技术支持

如需技术支持或遇到问题，请联系JBolt开发团队。

---

本文档涵盖了JBolt Admin JS库的主要功能和使用方法。建议开发者根据实际需求选择合适的组件，并参考示例代码进行开发。