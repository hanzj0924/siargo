**Controller** 在JFinal MVC架构下，是用来承担接收用户请求（request）、响应（response）返回服务器数据、信息和UI的职责。

### JFinal controller层的文档 需要提前学习：
https://jfinal.com/doc/3-1

# 一、Controller里有什么
### 一个普通后台管理业务Controller的基本组成部分
1、继承JBoltBaseController
2、配置路由
3、权限注解 权限校验
4、业务注入
5、参数获取
6、业务调用与返回
7、render响应

![图片](../image/69a5e424-262a-431d-bc23-ded5c4976b97.png)

# 二、 这个Controller怎么能够访问到？
没有添加@path注解的 需要自己配置Routes路由映射 具体配置在
ProjectConfig中：
![图片](../image/fafdcb4f-c23c-4844-8d0d-3b7902a86e96.png)
找到路由配置configRoutes
![图片](../image/ce6a5fe5-cd33-4415-93d3-449b834a4490.png)
需要独立创建自己的routes类或者直接在这里me.add添加自己的controller映射
![图片](../image/16cf1245-f060-4901-aa69-f27d7789607d.png)

具体怎么写路由配置，这个是JFinal的基础知识，参考JFinal的文档即可：
https://jfinal.com/doc/2-3

### 这里需要注意的就是，每一个controller，执行一些action 匹配一组业务处理，可能对应了很多UI界面 html
### 这些Html存放在这个controller对应配置的viewPath中。
例如：
![图片](../image/f8c513a3-30dd-4498-83bd-0d687fa64808.png)

这个render("xxx.html")
文件存在放这个controller对应的viewPath里

### 系统默认的主view层根目录设置在：webapp/_view下
![图片](../image/9333870f-ed90-40af-84b8-4b6ee0b78b6b.png)

controller对应的viewPath在这个基础上增加自己的配置了：
![图片](../image/30e47311-cddd-4a25-9346-879974555c3a.png)
先设置了这个路由类的基础viewpath放在了/_view/_admin
![图片](../image/1c3137a4-c420-4bc8-bdef-e5e0e977f119.png)
这里viewPath设置到了基于baseViewPath目录 下的/user目录中
![图片](../image/8523cf02-2b5f-4bd4-868b-acc258fb0266.png)

这样，访问请求跳转到你定义的controller的action之后，render("index.html")
就去这个controller早就配置好的路由导航到指定位置了。

# 三、这样有点麻烦呀 有没有更省事儿的方式？
当然有，JFinal最近更新的新版里加入了Controller层注解@Path()可以在Controller上直接自己定义路由设置，而不用在一个地方集中配置routes类了。
这样好处就是可以在代码生成controller的时候直接生成可访问的路径和viewPath配置 启动就可用了。

![图片](../image/cefceece-7636-49e9-af4f-781621c0c7df.png)

这里需要注意的是：@Path中的viewPath是依赖配置扫描的baseViewPath的

只做上面的配置，你是访问不到这个Controller的，需要将controller所在的package包配置给扫描器：

因为这里他属于admin后台的内容，又因为之前开发的模块提前创建了Routes类，就在这里配置了：
![图片](../image/213640d4-37cf-4697-8895-7fe51d532396.png)

## 如果是新开的项目和模块，不想放在adminRoutes里怎么办？
那就需要在ProjectConfig的configRoutes里去配置这个扫描package了
![图片](../image/c430e184-eb14-48fe-aaaa-135cdb521ecc.png)

这样就没有了baseViewpath配置 就需要在Controller上自己指定全路径viewPath
![图片](../image/4d2f2c5d-45dc-47d8-b598-28f65ac07ea2.png)
这样才能找到index.html的位置。

# 四、如果要给controller设置拦截器呢？
### 1、就单独给这一个controller设置专门拦截器
直接加就行了
![图片](../image/1fbdf763-8adf-48cb-bce8-6cd4744a2acd.png)
### 2、如果这个模块好几个controller 都用同一个拦截器呢？
指定扫描的包 和给这个报下的controller 加上拦截器
![图片](../image/24707ac5-99fc-478b-a5a0-f15ffe34868d.png)



