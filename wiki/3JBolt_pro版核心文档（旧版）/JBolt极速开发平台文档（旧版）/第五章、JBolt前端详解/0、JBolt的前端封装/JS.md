JBolt中JS部分主要封装了jbolt-admin.js jbolt-table.js jbolt-websocket.js

### jbolt-admin.js
封装了整个JBolt平台前端架构，从jbolt后台首页首次加载成功后开始进行初始化，启动自扫描，自动识别页面html中预先定义的各种组件声明，声明式的API检测到之后，会启动插件懒加载机制，声明了什么组件 ，什么插件，识别到之后采取自动异步加载对应组件、插件所需的依赖js css文件，加载后回调初始化方法，自动初始化UI和基础数据，以及默认和常用的各种事件监听。

### jbolt-table.js
封装了整个jbolttable组件使用的所有函数和事件处理，自动化响应等

### jbolt-websocket.js
封装了websocket 需要自己解开注释后重新压缩使用 配有压缩器