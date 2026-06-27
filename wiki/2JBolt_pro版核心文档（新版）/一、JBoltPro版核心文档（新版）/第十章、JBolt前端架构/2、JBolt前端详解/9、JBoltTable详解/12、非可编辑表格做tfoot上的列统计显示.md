这个分可编辑表格没法前端计算 可以后端查询数据 然后查询统计结果后 都发送给前端就行了。
用到了表格的extraData原理
：
案例：
![图片](../../../../image/0472b9e5-a125-4a41-a26e-03fb44370094.png)
![图片](../../../../image/74409399-3dc0-4e76-9ef9-e4d0686e7f93.png)
![图片](../../../../image/50889fc9-721e-4644-a7a1-d81afe92ddff.png)

具体后端代码截图：

![图片](../../../../image/018cf5b8-81a8-4832-8a02-42c8d74ef2cd.png)

### 使用专用方法 发送数据即可：
renderJBoltTableJsonData(pageData,extraData)

## 然后前端怎么找我们的extraData?：

![图片](../../../../image/3db9d0ea-2bd8-4217-a93c-a27921655c57.png)

只要在html里 你定义data-extradata属性 属性值设置为你后端定义的key即可自动赋值。
