## JBolt_pro中使用的自动缓存规则，所有JBolt版本是通用的

### 平台内置了自动缓存处理机制。
这个机制可以极大降低手动操作缓存带来的风险，少写代码，自动处理，省心省力。

在说自动缓存机制之前，我们来看一下什么数据需要进缓存，什么时候需要操作缓存？
一般情况，系统里的用户基础信息，字典数据，全局配置信息，角色、用户、权限基础信息以及对应关系，导航菜单、商品分类、商品基础信息、组织架构信息、部门信息。再比如网站首页的一些轮播图啊、每个版块的前十条数据，热门文章前十条，等等这些吧。

### 举例：下图、左侧导航、右侧的字典表都使用了缓存。

![图片](../image/f5d3d925-92ab-4b52-b097-a6d9f895bdd0.png)

### 这里分析一下：
这些数据，有的是很基础的数据，使用缓存也很简单，只要ID对应Object作为KV键值对即可。
基础数据如果被其它模块中的数据引用了，表里用他们的ID作为外键关联的，比如一个商品属于笔记本这个类型。

那么笔记本分类的ID作为KEY，笔记本的Model作为Value存入缓存，商品中使用笔记本的类型ID作为外键关联。
当查询商品信息的时候，除了需要显示商品自身属性字段，还要显示这个商品是什么类型，这个类型商品只存了类型ID，要显示的是类型的名字-笔记本，这个时候就需要从缓存里通过这个外键ID去作为KEY得到实际笔记本类型这个Value数据了。

#### 当然，有些系统不用缓存就直接left join去查询了。

下面就来揭秘一下自动缓存机制的实现：
### 自动缓存机制提供了三种策略：
ID-Object策略、Key策略、ID-Object Key公用策略。

### 一、ID-Object策略
使用ID作为key，model数据作为value，只要有表中外键使用了这个数据的ID，就可以通过缓存获取数据，当指定ID的数据进行update和delete的时候，自动清除缓存，等待下次懒加载。

#### JBolt平台中具体使用的Model有哪些？
User.java,Dictionary.java等 都是使用ID就能查询到缓存数据的

#### 具体启动自动缓存ID-Object策略的写法：

![ID-Object策略启用](../image/3ccf31d1-3754-47f7-ab27-424ccd7bdf18.png)

只要在Model是增加注解@JBoltAutoCache，就声明启用了自动缓存处理，人工无需干预，而且没有任何参数的情况下，使用的就是默认策略-ID-Object策略。


### 二、Key策略
不使用ID作为键值对中的key，而是使用此model对应表中的一个特殊字符串字段作为KEY。

#### JBolt平台中具体使用的Model有哪些？
GlobalConfig.java 表中有config_key字段，标识为全局配置唯一的识别标识KEY，只要知道一个配置的全局标识KEY，就能从缓存中拿到数据。

#### 下图，是GlobalConfig全局配置的KEY定义。

![GlobalConfigKey](../image/05abff6e-666c-4640-9cd1-c92cec7856da.png)

#### key策略配置写法
![key策略配置写法](../image/7019b69e-5312-44f7-9743-a09a8e937045.png)

同样也是使用注解 @JBoltAutoCache 只不过这里需要关闭默认ID-Object策略：idCache=false,
然后启用KEY策略：keyCache=true，并且指定使用哪个特殊字段作为唯一KEY column="config_key"

简单的配置一下参数，自动根据KEY进行自动缓存处理，就完成了。

#### key策略中的bindColumn
当启用key策略的时候 指定了一个字段还不够 需要两个字段共同作为key才能确定唯一值 
例如:字典表的数据 需要通过编号作为key指定对应的数据字典的一个item是不够的 不同类型下的字典 它们的编号sn是可以重复的，所以需要增加绑定一个限制字段就是类型key
![图片](../image/0195419d-6556-46e7-bcaa-2c8af9924371.png)
如上图所示，sn+typeKey 就可以唯一确定一个字典数据了。


### 三、ID-Object策略与Key策略同时使用
这里比较特殊，在JBolt中权限资源定义Model上用了这个组合策略。
使用方式也很简单，跟上面KEY策略讲的一样，只要开启KEY策略keyCache=true，设置key字段column="permission_key"就行了，只要不设置idCache=false，默认就是开启的。

![组合策略](../image/4869a488-e544-4f99-953e-52cce996acf5.png)

这里权限为何使用组合策略呢，因为底层ID-Object是针对单个Permission的缓存进行处理的，但是针对一个用户角色上分配了N个资源，也就是一个角色对应了一个List<ID>集合 这里底层只用到ID-Object策略。

但是，其他模块里，JBolt提供了在页面上判断权限的指令，通过指定权限的KEY，去cache中查找对应的权限，判断当前用户是否有权访问某个页面元素，列，按钮等。
![通过key判断](../image/fde9137c-b5b3-4465-b36d-d72d65c9d144.png)






--------------------------------------------------------------
## 但是：
在JBolt_Pro中使用缓存的时候，不在强制依赖于一个CACHE.java工具类，而是将JBolt平台里内核中的基础数据，分成一个一个的缓存工具类去使用。

## 在cn.jbolt.core.cache包中：

![图片](../image/45fff966-15ae-47a3-9cd0-53778d2ed2cc.png)

在具体使用的时候与CACHE.java类似。

## 一、在Java代码里可以随处使用：
![图片](../image/8c59f672-2520-48ff-bd3d-e6cc15a8a9ad.png)
例如上图，在拦截器里使用Application的缓存。

![图片](../image/3d394ed2-49f4-4a6c-9260-6af4d4757220.png)
在其他表中关联使用User的cache 拿到用户姓名。

## 二、在enjoy模板中使用

![图片](../image/a1da15ed-726b-4385-9650-6aca3fe7b280.png)
在ProjectConfig.java中配置configEngine模板引擎的共享对象，把内置的缓存都配置上，就可以在html模板里使用了。


![图片](../image/3bb5f90a-0988-40bc-b3d2-953d4f2de1e0.png)

## 如何编写一个自己的工具类？

正常如果不是其他模块关联使用整个表，就不用写缓存工具类。

以现在一个项目中基础数据 年级管理中年级表为例
需要的缓存可以这样定义：

```
package com.boyaceping.cache;

import com.boyaceping.platform.grade.GradeService;
import com.boyaceping.platform.model.Grade;
import com.jfinal.aop.Aop;

import cn.jbolt.core.cache.JBoltCache;
/**
 * 年级缓存
 * @ClassName:  GradeCache   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2022年2月26日   
 */
public class GradeCache extends JBoltCache {
	public static final GradeCache me = new GradeCache();
	GradeService service = Aop.get(GradeService.class);

	/**
	 * 通过ID获得年级
	 * 
	 * @return
	 */
	public Grade get(Integer id) {
		return service.findById(id);
	}
	/**
	 * 获得年级名称
	 * 
	 * @param id
	 * @return
	 */
	public String getName(Integer id) {
		Grade grade = get(id);
		return grade == null ? "" : grade.getName();
	}
	
	private static final String TYPE_NAME ="grade";
	@Override
	public String getCacheTypeName() {
		return TYPE_NAME;
	}
}


```

## 特殊缓存怎么写？
这里就用到了JBoltCacheKit.java


```
/**
	 * 通过ID获得题目 api专用
	 * @return
	 */
	public Question getApiUseData(Long id) {
		if(notOk(id)) {return null;}
		return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey("apidata_", id), new IDataLoader() {
			@Override
			public Object load() {
				Question question = get(id);
				if(question==null) {return null;}
				return new  Question()._setAttrs(question).remove(Question.COLUMN_CREATE_TIME,Question.COLUMN_UPDATE_USER_ID,Question.COLUMN_CREATE_USER_ID,Question.COLUMN_UPDATE_USER_ID,"enable","analyzing");
			}
		});
	}
```

## 特殊缓存怎么删除？
如果缓存要在这个表数据的delete save update时需要清理掉
需要在model中复写方法：
![图片](../image/bdfb5dc1-a21d-423e-9979-0fe24a12e1ce.png)

这样在save update delete时候 自动触发 缓存清理


## 想用Redis缓存服务怎么办？
在配置文件config.properties中找到配置项
![图片](../image/4813f22e-6c14-4f9a-b7cb-a739cc333ca2.png)

前三个都支持 随便选一个

如果启用了redis缓存类型
需要外配置redis的配置文件：
![图片](../image/d73768e9-05ed-4573-aff6-c4d823b58af6.png)



### 使用JBoltCacheKit.java

![图片](../image/492980ff-c270-4284-a3c5-fd21f2c904bc.png)

![图片](../image/518753fd-e8fd-4994-b61c-d8ab84e60b4e.png)

