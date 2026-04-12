---
description: 检查项目源代码注释完整性，识别缺少注释的代码并自动补充注释
args: <目标路径> - 必填，指定要检查的文件或文件夹路径
---
## 概述
源代码注释完善指令，扫描项目源文件检查注释完整性，识别缺少注释的代码位置，并自动补充符合规范的注释内容，提升代码可读性和可维护性。

## 使用方式

```
/comment <文件或文件夹路径>
```

### 参数说明
- **目标路径**（必填）：指定要检查注释的文件或文件夹的绝对路径
  - 指定文件：仅检查该单个文件的注释
  - 指定文件夹：递归检查该文件夹下所有符合检查范围的文件

### 使用示例
```bash
# 检查整个模块目录
/comment E:\eclipse-workspace\siargo\src\main\java\cn\jbolt\admin\siargo

# 检查单个 Java 文件
/comment E:\eclipse-workspace\siargo\src\main\java\cn\jbolt\admin\siargo\customer\CustomerAdminController.java

# 检查前端模板目录
/comment E:\eclipse-workspace\siargo\src\main\webapp\WEB-INF\views\admin\siargo

# 检查单个 HTML 文件
/comment E:\eclipse-workspace\siargo\src\main\webapp\WEB-INF\views\admin\siargo\customer\index.html
```

## 检查范围

> 根据用户指定的目标路径，自动识别路径下的文件类型并按以下规范进行检查。

1. **Java 源文件**
   - 路径：`src/main/java/` 下的所有 `.java` 文件
   - 重点：Controller、Service、Model、Handler 等业务代码

2. **前端模板文件**
   - 路径：`src/main/webapp/` 下的所有 `.html` 文件
   - 重点：页面模板、组件模板、布局模板

3. **配置文件**
   - `*.properties` 配置文件的关键配置项
   - `*.xml` 配置文件的关键节点
   - `pom.xml` 构建配置

4. **SQL 文件**
   - `sql/` 目录下的数据库脚本
   - 复杂查询和数据迁移脚本

## Java 注释规范

1. **类级别注释**
   - 每个类必须有 Javadoc 注释
   - 包含：类的用途说明、@author、@date
   - 示例：
     ```java
     /**
      * 用户管理控制器，处理用户相关的HTTP请求
      * @author developer
      * @date 2026-04-09
      */
     public class UserController extends Controller {
     ```

2. **方法级别注释**
   - public/protected 方法必须有 Javadoc 注释
   - 包含：功能描述、@param、@return、@throws（如有异常）
   - 示例：
     ```java
     /**
      * 根据用户ID查询用户信息
      * @param userId 用户ID
      * @return 用户信息对象，不存在则返回null
      */
     public User findById(Long userId) {
     ```

3. **字段级别注释**
   - 类的成员变量应有注释说明用途
   - 特别是 Model 类需注明对应数据库字段含义
   - 示例：
     ```java
     /** 用户名，对应用户表 username 字段 */
     private String username;
     ```

4. **关键逻辑注释**
   - 复杂业务逻辑需有行内注释说明处理思路
   - 算法实现需注释关键步骤
   - 条件分支需注释判断依据
   - 示例：
     ```java
     // 校验用户权限：只有管理员可以删除数据
     if (!currentUser.isAdmin()) {
         throw new BusinessException("无权限执行此操作");
     }
     ```

5. **Controller 类特殊要求**
   - 路由方法需注明对应的 URL 路径和 HTTP 方法
   - 示例：
     ```java
     /**
      * 用户列表页面
      * GET /admin/user/list
      */
     public void list() {
     ```

6. **Service 类特殊要求**
   - 业务方法需说明业务场景和处理流程
   - 涉及事务的方法需注明事务边界

7. **Model/Bean 类特殊要求**
   - 字段需注明对应数据库字段含义
   - 关联关系字段需说明关联逻辑

## 前端模板注释规范

1. **文件顶部注释**
   - HTML 模板文件顶部应有页面功能说明注释
   - 示例：
     ```html
     <!-- 用户管理列表页面：展示用户列表，支持搜索、新增、编辑、删除操作 -->
     ```

2. **Enjoy 模板指令注释**
   - 复杂的 #if、#for 等指令块应有注释说明
   - 嵌套结构需标注层级关系
   - 示例：
     ```html
     <!-- 遍历用户列表 -->
     #for(user in userList)
         <!-- 仅显示启用状态的用户 -->
         #if(user.status == 1)
         ...
         #end
     #end
     ```

3. **JavaScript 函数注释**
   - 页面内嵌 JS 函数应有功能注释
   - 示例：
     ```javascript
     /**
      * 删除用户确认
      * @param userId 用户ID
      */
     function deleteUser(userId) {
     ```

4. **DOM 结构注释**
   - 关键 DOM 结构区块应有注释标识
   - 大型区块使用 begin/end 标记
   - 示例：
     ```html
     <!-- 搜索表单区域 begin -->
     <form class="search-form">...</form>
     <!-- 搜索表单区域 end -->
     ```

## 执行流程

1. **扫描源文件**
   - 根据用户指定的目标路径扫描源文件
   - 排除自动生成代码和第三方库代码

2. **检查注释完整性**
   - 逐文件分析代码结构
   - 识别缺少注释的类、方法、字段、逻辑块

3. **输出缺失报告**
   - 按文件列出注释缺失位置
   - 标注缺失类型（类/方法/字段/逻辑）

4. **自动补充注释**
   - 根据代码语义生成注释内容
   - 保持与项目现有注释风格一致
   - 不修改已有注释内容

5. **输出修改汇总**
   - 列出所有新增注释的文件和位置
   - 统计注释完善数量

## 注释风格要求

1. **语言要求**
   - Java 注释统一使用中文编写
   - 专有名词、技术术语可保留英文

2. **简洁性**
   - 注释应简洁明了，避免冗余
   - 描述功能用途，非实现细节

3. **一致性**
   - 遵循项目现有注释风格
   - 保持同类代码注释格式统一

4. **准确性**
   - 自动生成的注释需准确反映代码实际功能
   - 涉及业务逻辑需结合上下文理解

5. **非侵入性**
   - 不修改已有注释内容
   - 仅补充缺失的注释

## 验证要点

### Java 代码注释
- [ ] 所有类有 Javadoc 类注释
- [ ] public/protected 方法有 Javadoc 方法注释
- [ ] 类成员变量有字段用途注释
- [ ] 复杂逻辑有行内注释说明
- [ ] Controller 方法有 URL 路径注释
- [ ] Service 方法有业务场景说明
- [ ] Model 字段有数据库字段对应说明

### 前端模板注释
- [ ] HTML 文件顶部有页面功能说明
- [ ] 复杂模板指令块有注释
- [ ] JavaScript 函数有功能注释
- [ ] 关键 DOM 结构有区块标识

### 配置与 SQL 注释
- [ ] 关键配置项有说明注释
- [ ] SQL 脚本有用途说明
- [ ] 复杂 SQL 有逻辑注释

### 注释质量
- [ ] 注释使用中文编写
- [ ] 注释简洁明了无冗余
- [ ] 注释风格与项目一致
- [ ] 注释内容准确反映功能
