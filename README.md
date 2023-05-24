# 项目简述

项目基于 SpringBoot3、SpringSecurity6 、 JWT 、 Redis 、 Mybatis-Plus 、 Knife4j 等技术栈搭建的前后端分离开源权限管理系统。


## 项目预览
**在线预览地址**

[http://vue3.youlai.tech/](http://vue3.youlai.tech/)

**首页控制台**

| ![明亮模式](https://s2.loli.net/2023/03/26/oltnAHfFcbw18GL.png) |
|-------------------------------------------------------------|
| ![暗黑模式](https://s2.loli.net/2023/03/13/QvjY4zf3VCGteNF.png) |

**接口文档**

![接口文档](https://s2.loli.net/2023/03/13/bH4J3O6WRgCUpwt.png)

**权限管理系统**

| ![用户管理](https://s2.loli.net/2023/03/13/L9xgT5sSMVZukQj.png) | ![角色管理](https://s2.loli.net/2023/03/13/nQg6HmrtFUkPDYv.png) |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![菜单管理](https://s2.loli.net/2023/03/13/C4fDRJeTuUO7gPI.png) | ![字典管理](https://s2.loli.net/2023/03/13/BzqjHpa64wfeWhE.png) |



## 项目特色
- Spring Boot 3.0 + Vue3 前后端分离单体应用，适合快速开发；
- Spring Security + JWT 认证鉴权方案；
- 基于 RBAC 模型的权限设计，细粒度接口方法、按钮级别权限控制。

## 运行环境
- JDK 17
- IDEA Lombok 插件
- IDEA MapStruct Support 插件
- MySQL 8.x

## 项目地址

| 项目名称 | 项目地址    |
|------|------------------------------------------------|
| 后端工程 | https://gitee.com/youlaiorg/youlai-boot        |
| 前端工程 | https://gitee.com/youlaiorg/vue3-element-admin |



## 接口文档

- `knife4j` 接口文档：[http://localhost:8989/doc.html](http://localhost:8989/doc.html)

- `swagger` 接口文档：[http://localhost:8989/swagger-ui/index.html](http://localhost:8989/swagger-ui/index.html)

## 项目运行

### 1. 数据库创建

执行 [youlai_boot.sql](sql/youlai_boot.sql) 脚本完成数据库创建、表结构和基础数据的初始化。

### 2. 配置修改

[application-dev.yml](src/main/resources/application-dev.yml) 修改MySQL、Redis连接配置；

### 3. 后端启动
执行 [SystemApplication.java](src/main/java/com/youlai/system/SystemApplication.java) 的 main 方法完成后端项目启动；

访问接口文档地址 [http://localhost:8989/doc.html](http://localhost:8989/doc.html) 验证项目启动。

### 4. 前端启动

文档：[README.md](https://gitee.com/youlaiorg/vue3-element-admin#%E9%A1%B9%E7%9B%AE%E5%90%AF%E5%8A%A8)

## 开发规范

### 方法命名

以下命名涵盖了Controller、Service和Mapper层

|作用|示例|
|---|---|
|分页查询|getUserPage|
|列表查询|listUsers|
|单个查询|getUser/getUserDetail/getUserInfo ...|
|新增|saveUser|
|修改|updateUser|
|删除|deleteUser/removeUser|


### 实体命名

| 名称     | 定义               | 示例        |
|--------|------------------|-----------|
| entity | 映射数据库实体，字段属性完全对应 | SysUser   |
| bo     | 多表关联查询的业务实体      | UserBO    |
| query  | 查询传参，建议参数≥3使用    | UserQuery |
| form   | 表单对象             | UserForm  |
| dto    | RPC调用，可替代VO      | UserDTO   |
| vo     | 视图层对象            | UserVO    |

### API规范
在RESTFul架构中，每个URL代表一种资源，所以不能有动词，只能有名词，而且所用的名词往往与数据库的表格名对应。一般来说，数据库中的表都是同种记录的"集合"，所以API中的名词也应该使用复数。

**请求示例：**

|请求描述|请求方法|请求路径|
|---|---|---|
|获取所有用户信息|GET|/api/v1/users|
|获取标识为1用户信息|GET|/api/v1/users/1|
|删除标识为1用户信息|DELETE|/api/v1/users/1|
|新增用户|POST|/api/v1/users|
|修改标识为1用户信息|PUT|/api/v1/users/1|
|修改标识为1用户状态|PATCH|/api/v1/users/1/status|
|获取当前登录用户信息|GET|/api/v1/users/{me,current}|


## 请求状态码规范

参考 [阿里Java开发手册](https://developer.aliyun.com/topic/java2020?utm_content=g_1000113416)

## Git 提交规范


参考 ([Angular](https://github.com/conventional-changelog/conventional-changelog/tree/master/packages/conventional-changelog-angular)) 社区规范，建议 IDEA 安装 Git Commit Template 插件

- `feat` 增加新功能
- `fix` 修复问题/BUG
- `style` 代码风格相关无影响运行结果的
- `perf` 优化/性能提升
- `refactor` 重构
- `revert` 撤销修改
- `test` 测试相关
- `docs` 文档/注释
- `chore` 依赖更新/脚手架配置修改等
- `workflow` 工作流改进
- `ci` 持续集成

## 联系我们

> 欢迎添加开发者微信，备注「有来」进群

| ![郝先瑞](https://s2.loli.net/2022/04/06/yRx8uzj4emA5QVr.jpg) | ![张川](https://s2.loli.net/2022/04/06/cQihGv9uPsTjXk1.jpg) |
| --- | --- |
