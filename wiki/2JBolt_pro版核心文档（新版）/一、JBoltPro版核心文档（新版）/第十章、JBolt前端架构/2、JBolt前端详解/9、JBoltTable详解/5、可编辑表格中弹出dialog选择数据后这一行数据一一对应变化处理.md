在可编辑表格中，设置了使用dialog弹出选择数据，例如在第2节将enterHandler的时候，弹出Dialog选择数据。
![图片](../../../../image/3b615661-8a19-45e9-b1cf-c1d092eb8d66.png)
或者cols设置input带着dialog选择数据：
![图片](../../../../image/b453954e-8645-40c8-91bc-90e16354d78a.png)
界面效果如下：
![图片](../../../../image/c55b0327-eef0-4d2a-9ad9-a68d3df3f705.png)

点击弹出的选择器：
![图片](../../../../image/b7ad2479-ecf4-44d8-804c-c6c9096770bd.png)

选择数据后 替换当前行数据按钮点击，执行数据替换。

这个数据替换是在dialog加载的页面里实现的：

![图片](../../../../image/2d82692f-5697-43bd-8f9d-5863d668d5d0.png)
直接复制过去代码就可以使用，唯一需要自己设置的就是这里：
![图片](../../../../image/3d365a27-e406-4ae7-87ef-6eb23eb8bdca.png)
![图片](../../../../image/fdc1a917-9b91-45e4-b171-35f022bd90ee.png)
这个函数是获取当前table选中行的json数据，如果传了第二个参数 就是自定义数据结构了。
例如你拿到的数据json是{id:1,name:"xxx",age:2} 但是你想返回的数据格式是{title:"xxx",age:2}
那么这几个方法的第二个参数是个数组 就可以自定义他的属性换算结构：
[替换为什么属性:用什么属性替换,替换为什么属性:用什么属性替换]

{id:1,name:"xxx",age:2} 转 {title:"xxx",age:2} 怎么转？

![图片](../../../../image/938dac74-822b-4963-86c2-af10a8b0d12d.png)

["title:name","age:age"]

意思是title属性 使用选中的数据中的name属性值
age使用选中数据中的age属性值

这样最终datas就是{title:"xxx",age:2}这样的数据

可编辑表格里只要配置了列有title和age
点击下方按钮，就可以完成覆盖。


---


![图片](../../../../image/c3c6d755-13c6-41b3-9b6d-dd11f46d5206.png)

function chooseAndInsert(insertType){
	var datas=getJboltTableCheckedData("chooseDataTable",["title:name","age:age"]);
	if(datas){
		LayerMsgBox.confirm("确认选择此数据?",function(){
			var action=parent.DialogUtil.getCurrentTriggerEle();
			if(isOk(action)){
				var success=parent.jboltTableInsertRowsByDialogChooser(action,datas,insertType);
				if(success){
					parent.layer.closeAll();
				}
			}
		});
	}
}

