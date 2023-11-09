
<p align="center">
    <img alt="有来技术" src="https://img.shields.io/badge/Java -17-brightgreen.svg"/>
    <img alt="有来技术" src="https://img.shields.io/badge/SpringBoot-3.1.5-green.svg"/>
     <a href="https://gitee.com/youlaitech/youlai-boot" target="_blank">
        <img alt="有来技术" src="https://gitee.com/youlaiorg/youlai-boot/badge/star.svg"/>
    </a>     
    <a href="https://github.com/haoxianrui" target="_blank">
        <img alt="有来技术" src="https://img.shields.io/github/stars/haoxianrui/youlai-boot.svg?style=social&label=Stars"/>
    </a>
    <br/>
    <img alt="有来技术" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg"/>
    <a href="https://gitee.com/youlaiorg" target="_blank">
        <img alt="有来技术" src="https://img.shields.io/badge/Author-有来开源组织-orange.svg"/>
    </a>
</p>

<p align="center">
   <a target="_blank" href="https://www.youlai.tech">有来技术官网</a> |
   <a target="_blank" href="https://youlai.blog.csdn.net">有来技术博客</a>|
   <a target="_blank" href="https://gitee.com/haoxr">Gitee</a>|
   <a target="_blank" href="https://github.com/haoxianrui">Github</a> 
</p>

# 项目简述

项目基于 JDK 17、SpringBoot3、SpringSecurity6 、 JWT 、 Redis 、 Mybatis-Plus 、 Knife4j 等技术栈搭建的前后端分离开源权限管理系统。

- Spring Boot 3.0 + Vue 3 + Element-Plus 前后端分离单体应用，适合快速开发；
- Spring Security 6 + JWT 认证鉴权方案；
- 基于 RBAC 模型的权限设计，细粒度接口方法、按钮级别权限控制。


# 项目结构
```
youlai-boot
├── sql                                 # SQL脚本
    ├── mysql5                          # MySQL5 脚本
    ├── mysql8                          # MySQL8 脚本
├── src                                 # 源码目录
    ├── common                          # 公共模块
    ├── config                          # 自动装配配置
        ├── CorsConfig                  # 跨域共享配置
        ├── RedisConfig                 # Redis 配置
        ├── SwaggerConfig               # API 接口文档配置
        ├── WebMvcConfig                # WebMvc 配置
    ├── controller                      # 控制层
    ├── converter                       # MapStruct转换器
    ├── core                            # 核心模块
        ├── mybatisplus                 # Mybatis-Plus 配置和插件
        ├── security                    # Spring Security 安全配置和扩展
    ├── filter                          # 过滤器
        ├── RequestLogFilter            # 请求日志过滤器
        ├── VerifyCodeFilter            # 验证码过滤器
    ├── model                           # 模型层
        ├── bo                          # 业务对象
        ├── dto                         # 数据传输对象
        ├── entity                      # 实体对象
        ├── form                        # 表单对象
        ├── query                       # 查询参数对象
        ├── vo                          # 视图对象
    ├── mapper                          # 数据库访问层
    ├── plugin                          # 插件(可选)
        ├── dupsubmit                   # 防重提交插件，用于防止表单重复提交
        ├── easyexcel                   # EasyExcel 插件，Excel 文件的读写
        ├── rabbitmq                    # RabbitMQ 插件，消息队列交互
        ├── websocket                   # WebSocket 插件，实时双向通信
        ├── xxljob                      # XXL-JOB 插件，分布式任务调度和执行
    ├── service                         # 业务逻辑层
└── end       
```

# 前端工程
| 项目名称 | Gitee | Github |
|------|-------|------|
| 前端   | [vue3-element-admin](https://gitee.com/youlaiorg/vue3-element-admin)  | [vue3-element-admin](https://github.com/youlaitech/vue3-element-admin)  |


# 接口文档

- `knife4j` 接口文档：[http://localhost:8989/doc.html](http://localhost:8989/doc.html)

- `swagger` 接口文档：[http://localhost:8989/swagger-ui/index.html](http://localhost:8989/swagger-ui/index.html)


# 项目启动

- 1. **数据库初始化**

执行 [youlai_boot.sql](sql/mysql8/youlai_boot.sql) 脚本完成数据库创建、表结构和基础数据的初始化。

- 2. **修改配置**

[application-dev.yml](src/main/resources/application-dev.yml) 修改MySQL、Redis连接配置；

- 3. **启动项目**

执行 [SystemApplication.java](src/main/java/com/youlai/system/SystemApplication.java) 的 main 方法完成后端项目启动；

访问接口文档地址 [http://localhost:8989/doc.html](http://localhost:8989/doc.html) 验证项目启动。


# 💖加交流群

> 关注公众号【有来技术】，获取交流群二维码，二维码过期请或不想关注公众号可加我微信(`haoxianrui`)备注“有来”，拉你进群。

| ![](https://s2.loli.net/2022/11/19/OGjum9wr8f6idLX.png) |
|---------------------------------------------------------|


