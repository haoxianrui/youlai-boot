# 2.7.1 (2024/4/18)
### 🐛 fix
- 修复用户名或者密码错误时，返回的错误信息不正确问题
### 🛠️ refactor
- JWT 解析和验证代码优化重构
- 优化代码结构和完善注释，提高代码可读性

# 2.7.0 (2024/4/13)
### ✨ feat
- 集成 Mybatis-Plus generator 代码生成器

# 2.6.0 (2024/3/6)

### ✨ feat
- 黑名单方式实现 JWT 主动注销过期
### 🛠️ refactor
- 角色权限重构


# 2.5.0 (2023/12/6)
### ✨ feat
- [集成 Spring Cache 和 Redis 缓存，路由缓存](https://blog.csdn.net/u013737132/article/details/134789862)
### 🛠️ refactor
- 权限判断逻辑调整，用户绑定权限调整为角色绑定权限
### fix
- [接口无请求权限，Spring Security 自定义异常无效问题修复](https://youlai.blog.csdn.net/article/details/134718249)


# 2.4.1 (2023/11/7)
### ✂️ refactor
- 项目目录结构优化
### ⬆️ chore
- 升级 SpringBoot 版本 `3.1.4` → `3.1.5`


# 2.2.1 (2023/5/25)

### 🐛 fix

- 修复多级路由的组件路径错误导致页面404问题

# 2.2.0 (2023/5/21)

### ✨ feat
- 菜单、角色、字典、部门添加接口权限控制

### 🐛 fix

- 用户登录权限缓存键值不一致导致获取用户数据权限错误问题修复

### ✂️ refactor

- 递归获取菜单、部门属性列表代码重构优化

### ⬆️ chore
- 升级 SpringBoot 版本 `3.0.6` → `3.1.0`

### 📝 docs
- SQL 脚本更新，sys_menu 新增 `tree_path` 字段  (升级需更新SQL脚本)

