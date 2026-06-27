## **JBoltCommonController**是JBolt controller层的基础封装了，有JBoltBaseController和JBoltApiBaseController两个实现子类

# 一、提供了什么？
## 1、便捷的参数获取
支持JFinal所有自身初级的参数获取方式，详情看JFinal文档即可
https://jfinal.com/doc/3-4
### 另外：
JBoltBaseController还提供了更多参数获取方式：
![图片](../../image/1a67e2a3-9f4c-491c-ac06-21aee56d41cb.png)

开发的时候根据需求轻松获取到前端提交的数据，支持各种方式和类型
get、post
rawData application/json uploadFile
Url挂的参数
header里带的参数
等等

GetJsonxxx系列 可以轻松获取到JSON参数数据，并且可以转为自己需要的各种数据类型。




## 2、action参数注入方式与万能参数获取器
参数注入 JFinal原生就支持 具体文档看这里
https://jfinal.com/doc/3-3

#### 那这里我们主要讲啥呢？
讲一讲JBolt内置万能参数获取器的注入方式。
不注入的时候万能转换器也能用
![图片](../../image/c53291b2-e8ae-4b71-822a-de670f708753.png)

### 在底层封装了getJBoltPara方法
![图片](../../image/4aeeb80a-f04c-4750-a6c7-d6383ee06659.png)

### 直接注入也是没有问题的：
![图片](../../image/06b10d8c-63d6-4dd3-95fb-f14e16fa15eb.png)

## 3、参数拿到之后你得校验合法性 有效性
JFinal提供了validator验证器 官方文档有，这里不讲了
这里讲一下JBolt里推荐使用的万能参数有效性校验器
isOk(param) 验证有效性 自动识别参数类型
notOk(param) 验证无效性 自动识别参数类型
hasNotOk(params)验证是否存在无效参数
hasOk()验证是否存在有效参数
isExcel(file) 验证文件是不是excel
isImage(file) 验证文件是不是图片
等等吧
![图片](../../image/6b4f8994-c09b-4305-91ec-b546cfef6de8.png)

### 咋用
![图片](../../image/87b4ee09-039c-49ca-8273-f068cb9d129c.png)
![图片](../../image/fe7600e6-3550-4a6f-9b34-b2eb24cad1d0.png)
![图片](../../image/9f7ae4ae-5d6f-4bc2-a342-ecee1ec5c39d.png)
![图片](../../image/046ffed4-50c2-450b-b347-72352a18697f.png)

在Controller service里都可以直接使用的


## 3、各种情况的render
响应客户端请求无非就是下载文件、返回数据、跳转网页等
在JFinal中有这方面的基础介绍：
https://jfinal.com/doc/3-7

### JBolt里提供了更多render
![图片](../../image/b322422c-fd32-4331-a2c5-8602136669a3.png)
可以render各种json数据

![图片](../../image/8990674e-d7c7-433b-88b2-ea10394d1ec3.png)
可以render几种验证码图片


![图片](../../image/66da88f6-ae88-46d1-bfc9-ac25ac167a83.png)
可以render各种bytes数据流 pdf excel 图片bytes都可以



