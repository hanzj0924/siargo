config.properties里配置开启了ureport之后，系统启动会加载这两个文件，因为Ureport用到了spring，所以applicationContext.xml自然需要

然后：
![图片](../../image/d1cf44e8-f6c0-45c0-a2df-84383e6a301b.png)
ureport_config.properties里可以配置 设计好的报表存在文件位置，默认会存放到classpath下
自己可以转移配置到项目之外的地方。