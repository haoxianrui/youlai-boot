/*
* youlai_boot 权限系统数据库(MySQL8.x)
*/

-- ----------------------------
-- 1. 创建数据库
-- ----------------------------
CREATE DATABASE IF NOT EXISTS youlai_boot DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_general_ci;


-- ----------------------------
-- 2. 创建表 && 数据初始化
-- ----------------------------
use youlai_boot;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
                             `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父节点id',
                             `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '父节点id路径',
                             `sort` int(0) NULL DEFAULT 0 COMMENT '显示顺序',
                             `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常;0:禁用)',
                             `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除标识(1:已删除;0:未删除)',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 171 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '有来技术', 0, '0', 1, 1, 0, NULL, NULL);
INSERT INTO `sys_dept` VALUES (2, '研发部门', 1, '0,1', 1, 1, 0, NULL, '2022-04-19 12:46:37');
INSERT INTO `sys_dept` VALUES (3, '测试部门', 1, '0,1', 1, 1, 0, NULL, '2022-04-19 12:46:37');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型编码',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项名称',
                             `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项值',
                             `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
                             `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态(1:正常;0:禁用)',
                             `defaulted` tinyint(0) NULL DEFAULT 0 COMMENT '是否默认(1:是;0:否)',
                             `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, 'gender', '男', '1', 1, 1, 0, NULL, '2019-05-05 13:07:52', '2022-06-12 23:20:39');
INSERT INTO `sys_dict` VALUES (2, 'gender', '女', '2', 2, 1, 0, NULL, '2019-04-19 11:33:00', '2019-07-02 14:23:05');
INSERT INTO `sys_dict` VALUES (3, 'gender', '未知', '0', 1, 1, 0, NULL, '2020-10-17 08:09:31', '2020-10-17 08:09:31');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
                                  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
                                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型名称',
                                  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型编码',
                                  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0:正常;1:禁用)',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `type_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 89 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '性别', 'gender', 1, NULL, '2019-12-06 19:03:32', '2022-06-12 16:21:28');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT,
                             `parent_id` bigint(0) NOT NULL COMMENT '父菜单ID',
                             `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
                             `type` tinyint(0) NULL DEFAULT NULL COMMENT '菜单类型(1:菜单；2:目录；3:外链；4:按钮)',
                             `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由路径(浏览器地址栏路径)',
                             `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径(vue页面完整路径，省略.vue后缀)',
                             `perm` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
                             `visible` tinyint(1) NOT NULL DEFAULT 1 COMMENT '显示状态(1-显示;0-隐藏)',
                             `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
                             `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单图标',
                             `redirect_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '外链路径',
                             `redirect` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转路径',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '系统管理', 2, '/system', 'Layout', NULL, 1, 1, 'system', '/system/user', '/system/user', '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (2, 1, '用户管理', 1, 'user', 'system/user/index', NULL, 1, 1, 'user', NULL, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (3, 1, '角色管理', 1, 'role', 'system/role/index', NULL, 1, 2, 'role', NULL, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (4, 1, '菜单管理', 1, 'cmenu', 'system/menu/index', NULL, 1, 3, 'menu', NULL, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (5, 1, '部门管理', 1, 'dept', 'system/dept/index', NULL, 1, 4, 'tree', NULL, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (6, 1, '字典管理', 1, 'dict', 'system/dict/index', NULL, 1, 5, 'dict', NULL, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (20, 0, '多级菜单', 2, '/multi-level-menu', 'Layout', NULL, 1, 9, 'multi_level', '/nested/level1/level2', '/nested/level1/level2', '2022-02-16 23:11:00', '2022-02-16 23:11:00');
INSERT INTO `sys_menu` VALUES (21, 20, '菜单一级', 2, 'nested_level1_index', 'nested/level1/index', NULL, 1, 1, '', '/nested/level1/level2', '/nested/level1/level2', '2022-02-16 23:13:38', '2022-02-16 23:13:38');
INSERT INTO `sys_menu` VALUES (22, 21, '菜单二级', 2, 'nested_level1_level2_index', 'nested/level1/level2/index', NULL, 1, 1, '', '/nested/level1/level2/level3', '/nested/level1/level2/level3', '2022-02-16 23:14:23', '2022-02-16 23:14:23');
INSERT INTO `sys_menu` VALUES (23, 22, '菜单三级-1', 1, 'nested_level1_level2_level3_index1', 'nested/level1/level2/level3/index1', NULL, 1, 1, '', '', '', '2022-02-16 23:14:51', '2022-02-16 23:14:51');
INSERT INTO `sys_menu` VALUES (24, 22, '菜单三级-2', 1, 'nested_level1_level2_level3_index2', 'nested/level1/level2/level3/index2', NULL, 1, 2, '', '', '', '2022-02-16 23:15:08', '2022-02-16 23:15:08');
INSERT INTO `sys_menu` VALUES (26, 0, '外部链接', 2, '/external-link', 'Layout', NULL, 1, 8, 'link', 'noredirect', 'noredirect', '2022-02-17 22:51:20', '2022-02-17 22:51:20');
INSERT INTO `sys_menu` VALUES (30, 26, 'document', 3, 'https://www.cnblogs.com/haoxianrui/p/16090029.html', '', NULL, 1, 1, 'document', '', '', '2022-02-18 00:01:40', '2022-02-18 00:01:40');
INSERT INTO `sys_menu` VALUES (31, 2, '用户新增', 4, '', NULL, 'sys:user:add', 1, 1, '', '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11');
INSERT INTO `sys_menu` VALUES (32, 2, '用户编辑', 4, '', NULL, 'sys:user:edit', 1, 2, '', '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11');
INSERT INTO `sys_menu` VALUES (33, 2, '用户删除', 4, '', NULL, 'sys:user:delete', 1, 3, '', '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11');
INSERT INTO `sys_menu` VALUES (36, 0, '组件封装', 2, '/demo', 'Layout', NULL, 1, 10, 'menu', '', '', '2022-10-31 09:18:44', '2022-10-31 09:18:47');
INSERT INTO `sys_menu` VALUES (37, 36, '富文本编辑器', 1, 'wangEditor', 'demo/wangEditor', NULL, 1, 1, '', '', '', NULL, NULL);
INSERT INTO `sys_menu` VALUES (38, 36, '上传组件', 1, 'uploader', 'demo/uploader', NULL, 1, 2, '', '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32');
INSERT INTO `sys_menu` VALUES (39, 36, '图标选择器', 1, 'IconSelector', 'demo/IconSelector', NULL, 1, 3, '', '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32');
INSERT INTO `sys_menu` VALUES (40, 0, '接口', 2, '/api', 'Layout', NULL, 1, 7, 'api', '', '', '2022-02-17 22:51:20', '2022-02-17 22:51:20');
INSERT INTO `sys_menu` VALUES (41, 40, '接口文档', 1, 'apidoc', 'demo/apidoc', NULL, 1, 1, 'api', '', '', '2022-02-17 22:51:20', '2022-02-17 22:51:20');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT,
                             `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
                             `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
                             `sort` int(0) NULL DEFAULT NULL COMMENT '显示顺序',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '角色状态(1-正常；0-停用)',
                             `data_scope` tinyint(0) NULL DEFAULT NULL COMMENT '数据权限(0-所有数据；1-部门及子部门数据；2-本部门数据；3-本人数据)',
                             `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             `update_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 128 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ROOT', 1, 1, 0, 0, '2021-05-21 14:56:51', '2018-12-23 16:00:00');
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'ADMIN', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (3, '访问游客', 'GUEST', 3, 1, 2, 0, '2021-05-26 15:49:05', '2019-05-05 16:00:00');
INSERT INTO `sys_role` VALUES (4, '系统管理员1', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (5, '系统管理员2', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (6, '系统管理员3', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (7, '系统管理员4', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (8, '系统管理员5', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (9, '系统管理员6', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (10, '系统管理员7', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (11, '系统管理员8', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (12, '系统管理员9', 'ADMIN1', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
                                  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
                                  `menu_id` bigint(0) NOT NULL COMMENT '菜单ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 5);
INSERT INTO `sys_role_menu` VALUES (2, 6);
INSERT INTO `sys_role_menu` VALUES (2, 11);
INSERT INTO `sys_role_menu` VALUES (2, 12);
INSERT INTO `sys_role_menu` VALUES (2, 19);
INSERT INTO `sys_role_menu` VALUES (2, 18);
INSERT INTO `sys_role_menu` VALUES (2, 17);
INSERT INTO `sys_role_menu` VALUES (2, 13);
INSERT INTO `sys_role_menu` VALUES (2, 14);
INSERT INTO `sys_role_menu` VALUES (2, 15);
INSERT INTO `sys_role_menu` VALUES (2, 16);
INSERT INTO `sys_role_menu` VALUES (2, 9);
INSERT INTO `sys_role_menu` VALUES (2, 10);
INSERT INTO `sys_role_menu` VALUES (2, 37);
INSERT INTO `sys_role_menu` VALUES (2, 20);
INSERT INTO `sys_role_menu` VALUES (2, 21);
INSERT INTO `sys_role_menu` VALUES (2, 22);
INSERT INTO `sys_role_menu` VALUES (2, 23);
INSERT INTO `sys_role_menu` VALUES (2, 24);
INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 39);
INSERT INTO `sys_role_menu` VALUES (2, 34);
INSERT INTO `sys_role_menu` VALUES (2, 26);
INSERT INTO `sys_role_menu` VALUES (2, 30);
INSERT INTO `sys_role_menu` VALUES (2, 31);
INSERT INTO `sys_role_menu` VALUES (2, 36);
INSERT INTO `sys_role_menu` VALUES (2, 38);
INSERT INTO `sys_role_menu` VALUES (2, 39);
INSERT INTO `sys_role_menu` VALUES (2, 40);
INSERT INTO `sys_role_menu` VALUES (2, 41);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` int(0) NOT NULL AUTO_INCREMENT,
                             `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                             `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                             `gender` tinyint(1) NULL DEFAULT 1 COMMENT '性别((1:男;2:女))',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                             `dept_id` int(0) NULL DEFAULT NULL COMMENT '部门ID',
                             `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户头像',
                             `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '用户状态((1:正常;0:禁用))',
                             `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
                             `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `login_name`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 287 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'root', '有来技术', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621590365', 1, 'youlaitech@163.com', 0, NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'admin', '系统管理员', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 2, 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210366', 1, '', 0, '2019-10-10 13:41:22', '2022-07-31 12:39:30');
INSERT INTO `sys_user` VALUES (3, 'test', '测试小用户', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 3, 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210366', 1, 'youlaitech@163.com', 0, '2021-06-05 01:31:29', '2021-06-05 01:31:29');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
                                  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
                                  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);

SET FOREIGN_KEY_CHECKS = 1;
