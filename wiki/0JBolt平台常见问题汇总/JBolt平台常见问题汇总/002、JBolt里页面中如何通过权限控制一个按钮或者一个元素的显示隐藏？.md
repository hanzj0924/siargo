在JBolt中提供了一个工具类 JBoltUserAuthKit.java
![图片](../image/64782ddd-e8a1-442a-8288-e97e6ceed6bf.png)
在Java代码和enjoy模板html代码里都是可以直接使用的。

### 在Java中如何使用？
 如下图所示，直接调用传参就好了。
![图片](../image/e2b26ac3-3e2e-4f9e-a4db-59915a715833.png)

### 在enjoy模板引擎的html里如何使用？
有两种方式：
一种就是判断有没有权限有就直接显示出来

![图片](../image/12b14e47-acff-426a-95ac-5d77d5b010b0.png)

使用这个指令：传入PermissionKey里定义的权限声明即可。


```
#permission(PermissionKey.DASHBOARD)
<a href="/admin/jboltversion/edit/1">审核</a>
#end
```

第二种是if else判断了
![图片](../image/d6aedc84-2776-4b92-92eb-7c854c04d530.png)
具体if需不需else 自己决定自己控制

```
#if(hasPermission(cn.jbolt.base.JBoltUserKit::getUserId(),true,PermissionKey.DEMO))
<a href="/admin/jboltversion/edit/1">审核</a>
#else
xxx
#end
```
