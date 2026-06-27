Service层，一切与业务、数据处理、事务相关的操作，都可以放在Service里处理，并且你可以在任意场景里使用Service。
1、Controller中注入Service
2、Model中Aop.get(ServiceClass)
3、拦截器中注入Service
4、Cache操作类中Aop.get(ServiceClass)

### 切记，Controller不是单例的，但是Service默认是无状态和单例模式。

JBolt中的Service层做了大量有用的封装，可以帮助开发者快速完成与数据库的交互，已经快速参数校验、sql创建、模板提取、数据转换等。