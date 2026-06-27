JBoltJson使用的默认是fastJson，为了节省带宽，fastJson默认会将null值忽略，不进行序列化。
这种有利于使用js接受处理数据的前端，但是针对安卓等app不使用js开发的前端来说，不太友好了。

# 举例：
系统字典管理中，数据如果没有设置sn，那么默认拿到的json是这样的：
![图片](../image/eea77ed0-65c2-40ca-a36f-4503eb6365d5.png)

![图片](../image/b37579f0-23b6-481f-a483-b3c4dec170e3.png)

## 我们前端如果需要拿到sn怎么办？

# 解决方法：
JBoltI提供了开关API和自定义序列化解决这个问题：


还有单独处理一个的方式
![图片](../image/a82ea8dc-1567-4286-9ad9-be3d536a517f.png)

## 具体使用方式：
在JFinal的常量配置中 配置这个序列化特性即可。
![图片](../image/8a7b01fc-3e2f-43ab-a426-3b23427c33dd.png)
![图片](../image/b48b53c4-a30f-4e53-a6f8-89e7c43cec52.png)

## 再看效果

![图片](../image/11f002bf-dec1-4e0d-9da3-6eb2b1b540ff.png)

这里调用默认的是把null字符串改成了“

如果你就是需要保证是null的话，需要调用单独的另一个了
![图片](../image/4c854a0c-be09-402c-9b3a-5f629fb8a06c.png)


![图片](../image/f427754a-22f8-45b6-8cc9-6285b25abae4.png)