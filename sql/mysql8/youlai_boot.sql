/*
* youlai_boot 权限系统数据库(MySQL8.x)
* @author youlai
* @date 2024/06/24
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
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门名称',
                             `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门编号',
                             `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父节点id',
                             `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '父节点id路径',
                             `sort` smallint NULL DEFAULT 0 COMMENT '显示顺序',
                             `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1-正常 0-禁用)',
                             `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` bigint NULL DEFAULT NULL COMMENT '修改人ID',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(1-已删除 0-未删除)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE COMMENT '部门编号唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '有来技术', 'YOULAI', 0, '0', 1, 1, 1, NULL, 1, '2024-06-24 23:48:59', 0);
INSERT INTO `sys_dept` VALUES (2, '研发部门', 'RD001', 1, '0,1', 1, 1, 2, NULL, 2, '2022-04-19 12:46:37', 0);
INSERT INTO `sys_dept` VALUES (3, '测试部门', 'QA001', 1, '0,1', 1, 1, 2, NULL, 2, '2022-04-19 12:46:37', 0);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型名称',
                             `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型编码',
                             `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0:正常;1:禁用)',
                             `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除(1-删除，0-未删除)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 89 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, '性别', 'gender', 1, NULL, '2019-12-06 19:03:32', '2024-06-22 21:14:47', 0);

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `dict_id` bigint NULL DEFAULT NULL COMMENT '字典ID',
                                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项名称',
                                  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项值',
                                  `status` tinyint NULL DEFAULT 0 COMMENT '状态（1-正常，0-禁用）',
                                  `sort` int NULL DEFAULT 0 COMMENT '排序',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1, 1, '男', '1', 1, 1, NULL, '2019-05-05 13:07:52', '2022-06-12 23:20:39');
INSERT INTO `sys_dict_item` VALUES (2, 1, '女', '2', 1, 2, NULL, '2019-04-19 11:33:00', '2019-07-02 14:23:05');
INSERT INTO `sys_dict_item` VALUES (3, 1, '保密', '0', 1, 3, NULL, '2020-10-17 08:09:31', '2020-10-17 08:09:31');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `type` tinyint NULL DEFAULT NULL COMMENT '日志类型(1-操作日志 2-登录日志)',
                            `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志标题',
                            `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
                            `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '日志内容',
                            `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint NULL DEFAULT NULL COMMENT '修改人ID',
                            `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                            `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(1-已删除 0-未删除)',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `parent_id` bigint NOT NULL COMMENT '父菜单ID',
                             `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点ID路径',
                             `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
                             `type` tinyint NOT NULL COMMENT '菜单类型（1-菜单 2-目录 3-外链 4-按钮）',
                             `route_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由名称（Vue Router 中用于命名路由）',
                             `route_path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由路径（Vue Router 中定义的 URL 路径）',
                             `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径（组件页面完整路径，相对于 src/views/，缺省后缀 .vue）',
                             `perm` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '【按钮】权限标识',
                             `always_show` tinyint NULL DEFAULT NULL COMMENT '【目录】只有一个子路由是否始终显示（1-是 0-否）',
                             `keep_alive` tinyint NULL DEFAULT NULL COMMENT '【菜单】是否开启页面缓存（1-是 0-否）',
                             `visible` tinyint(1) NOT NULL DEFAULT 1 COMMENT '显示状态（1-显示 0-隐藏）',
                             `sort` int NULL DEFAULT 0 COMMENT '排序',
                             `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单图标',
                             `redirect` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转路径',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `params` json NULL COMMENT '路由参数',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '0', '系统管理', 2, '', '/system', 'Layout', NULL, NULL, NULL, 1, 1, 'system', '/system/user', '2021-08-28 09:12:21', '2024-06-24 23:49:04', NULL);
INSERT INTO `sys_menu` VALUES (2, 1, '0,1', '用户管理', 1, 'User', 'user', 'system/user/index', NULL, NULL, 1, 1, 1, 'user', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL);
INSERT INTO `sys_menu` VALUES (3, 1, '0,1', '角色管理', 1, 'Role', 'role', 'system/role/index', NULL, NULL, 1, 1, 2, 'role', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL);
INSERT INTO `sys_menu` VALUES (4, 1, '0,1', '菜单管理', 1, 'Menu', 'menu', 'system/menu/index', NULL, NULL, 1, 1, 3, 'menu', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL);
INSERT INTO `sys_menu` VALUES (5, 1, '0,1', '部门管理', 1, 'Dept', 'dept', 'system/dept/index', NULL, NULL, 1, 1, 4, 'tree', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL);
INSERT INTO `sys_menu` VALUES (6, 1, '0,1', '字典管理', 1, 'Dict', 'dict', 'system/dict/index', NULL, NULL, 1, 1, 5, 'dict', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL);
INSERT INTO `sys_menu` VALUES (20, 0, '0', '多级菜单', 2, NULL, '/multi-level', 'Layout', NULL, 1, NULL, 1, 9, 'cascader', '', '2022-02-16 23:11:00', '2022-02-16 23:11:00', NULL);
INSERT INTO `sys_menu` VALUES (21, 20, '0,20', '菜单一级', 1, NULL, 'multi-level1', 'demo/multi-level/level1', NULL, 1, NULL, 1, 1, '', '', '2022-02-16 23:13:38', '2022-02-16 23:13:38', NULL);
INSERT INTO `sys_menu` VALUES (22, 21, '0,20,21', '菜单二级', 1, NULL, 'multi-level2', 'demo/multi-level/children/level2', NULL, 0, NULL, 1, 1, '', NULL, '2022-02-16 23:14:23', '2022-02-16 23:14:23', NULL);
INSERT INTO `sys_menu` VALUES (23, 22, '0,20,21,22', '菜单三级-1', 1, NULL, 'multi-level3-1', 'demo/multi-level/children/children/level3-1', NULL, 0, 1, 1, 1, '', '', '2022-02-16 23:14:51', '2022-02-16 23:14:51', NULL);
INSERT INTO `sys_menu` VALUES (24, 22, '0,20,21,22', '菜单三级-2', 1, NULL, 'multi-level3-2', 'demo/multi-level/children/children/level3-2', NULL, 0, 1, 1, 2, '', '', '2022-02-16 23:15:08', '2022-02-16 23:15:08', NULL);
INSERT INTO `sys_menu` VALUES (26, 0, '0', '平台文档', 2, NULL, '/doc', 'Layout', NULL, NULL, NULL, 1, 8, 'document', 'https://juejin.cn/post/7228990409909108793', '2022-02-17 22:51:20', '2022-02-17 22:51:20', NULL);
INSERT INTO `sys_menu` VALUES (30, 26, '0,26', '平台文档(外链)', 3, NULL, 'https://juejin.cn/post/7228990409909108793', '', NULL, NULL, NULL, 1, 2, 'link', '', '2022-02-18 00:01:40', '2022-02-18 00:01:40', NULL);
INSERT INTO `sys_menu` VALUES (31, 2, '0,1,2', '用户新增', 4, NULL, '', NULL, 'sys:user:add', NULL, NULL, 1, 1, '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11', NULL);
INSERT INTO `sys_menu` VALUES (32, 2, '0,1,2', '用户编辑', 4, NULL, '', NULL, 'sys:user:edit', NULL, NULL, 1, 2, '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11', NULL);
INSERT INTO `sys_menu` VALUES (33, 2, '0,1,2', '用户删除', 4, NULL, '', NULL, 'sys:user:delete', NULL, NULL, 1, 3, '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11', NULL);
INSERT INTO `sys_menu` VALUES (36, 0, '0', '组件封装', 2, NULL, '/component', 'Layout', NULL, NULL, NULL, 1, 10, 'menu', '', '2022-10-31 09:18:44', '2022-10-31 09:18:47', NULL);
INSERT INTO `sys_menu` VALUES (37, 36, '0,36', '富文本编辑器', 1, NULL, 'wang-editor', 'demo/wang-editor', NULL, NULL, 1, 1, 2, '', '', NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (38, 36, '0,36', '图片上传', 1, NULL, 'upload', 'demo/upload', NULL, NULL, 1, 1, 3, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL);
INSERT INTO `sys_menu` VALUES (39, 36, '0,36', '图标选择器', 1, NULL, 'icon-selector', 'demo/icon-selector', NULL, NULL, 1, 1, 4, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL);
INSERT INTO `sys_menu` VALUES (40, 0, '0', '接口文档', 2, NULL, '/api', 'Layout', NULL, 1, NULL, 1, 7, 'api', '', '2022-02-17 22:51:20', '2022-02-17 22:51:20', NULL);
INSERT INTO `sys_menu` VALUES (41, 40, '0,40', 'Apifox', 1, NULL, 'apifox', 'demo/api/apifox', NULL, NULL, 1, 1, 1, 'api', '', '2022-02-17 22:51:20', '2022-02-17 22:51:20', NULL);
INSERT INTO `sys_menu` VALUES (70, 3, '0,1,3', '角色新增', 4, NULL, '', NULL, 'sys:role:add', NULL, NULL, 1, 1, '', NULL, '2023-05-20 23:39:09', '2023-05-20 23:39:09', NULL);
INSERT INTO `sys_menu` VALUES (71, 3, '0,1,3', '角色编辑', 4, NULL, '', NULL, 'sys:role:edit', NULL, NULL, 1, 2, '', NULL, '2023-05-20 23:40:31', '2023-05-20 23:40:31', NULL);
INSERT INTO `sys_menu` VALUES (72, 3, '0,1,3', '角色删除', 4, NULL, '', NULL, 'sys:role:delete', NULL, NULL, 1, 3, '', NULL, '2023-05-20 23:41:08', '2023-05-20 23:41:08', NULL);
INSERT INTO `sys_menu` VALUES (73, 4, '0,1,4', '菜单新增', 4, NULL, '', NULL, 'sys:menu:add', NULL, NULL, 1, 1, '', NULL, '2023-05-20 23:41:35', '2023-05-20 23:41:35', NULL);
INSERT INTO `sys_menu` VALUES (74, 4, '0,1,4', '菜单编辑', 4, NULL, '', NULL, 'sys:menu:edit', NULL, NULL, 1, 3, '', NULL, '2023-05-20 23:41:58', '2023-05-20 23:41:58', NULL);
INSERT INTO `sys_menu` VALUES (75, 4, '0,1,4', '菜单删除', 4, NULL, '', NULL, 'sys:menu:delete', NULL, NULL, 1, 3, '', NULL, '2023-05-20 23:44:18', '2023-05-20 23:44:18', NULL);
INSERT INTO `sys_menu` VALUES (76, 5, '0,1,5', '部门新增', 4, NULL, '', NULL, 'sys:dept:add', NULL, NULL, 1, 1, '', NULL, '2023-05-20 23:45:00', '2023-05-20 23:45:00', NULL);
INSERT INTO `sys_menu` VALUES (77, 5, '0,1,5', '部门编辑', 4, NULL, '', NULL, 'sys:dept:edit', NULL, NULL, 1, 2, '', NULL, '2023-05-20 23:46:16', '2023-05-20 23:46:16', NULL);
INSERT INTO `sys_menu` VALUES (78, 5, '0,1,5', '部门删除', 4, NULL, '', NULL, 'sys:dept:delete', NULL, NULL, 1, 3, '', NULL, '2023-05-20 23:46:36', '2023-05-20 23:46:36', NULL);
INSERT INTO `sys_menu` VALUES (79, 6, '0,1,6', '字典类型新增', 4, NULL, '', NULL, 'sys:dict_type:add', NULL, NULL, 1, 1, '', NULL, '2023-05-21 00:16:06', '2023-05-21 00:16:06', NULL);
INSERT INTO `sys_menu` VALUES (81, 6, '0,1,6', '字典类型编辑', 4, NULL, '', NULL, 'sys:dict_type:edit', NULL, NULL, 1, 2, '', NULL, '2023-05-21 00:27:37', '2023-05-21 00:27:37', NULL);
INSERT INTO `sys_menu` VALUES (84, 6, '0,1,6', '字典类型删除', 4, NULL, '', NULL, 'sys:dict_type:delete', NULL, NULL, 1, 3, '', NULL, '2023-05-21 00:29:39', '2023-05-21 00:29:39', NULL);
INSERT INTO `sys_menu` VALUES (85, 6, '0,1,6', '字典数据新增', 4, NULL, '', NULL, 'sys:dict:add', NULL, NULL, 1, 4, '', NULL, '2023-05-21 00:46:56', '2023-05-21 00:47:06', NULL);
INSERT INTO `sys_menu` VALUES (86, 6, '0,1,6', '字典数据编辑', 4, NULL, '', NULL, 'sys:dict:edit', NULL, NULL, 1, 5, '', NULL, '2023-05-21 00:47:36', '2023-05-21 00:47:36', NULL);
INSERT INTO `sys_menu` VALUES (87, 6, '0,1,6', '字典数据删除', 4, NULL, '', NULL, 'sys:dict:delete', NULL, NULL, 1, 6, '', NULL, '2023-05-21 00:48:10', '2023-05-21 00:48:20', NULL);
INSERT INTO `sys_menu` VALUES (88, 2, '0,1,2', '重置密码', 4, NULL, '', NULL, 'sys:user:password:reset', NULL, NULL, 1, 4, '', NULL, '2023-05-21 00:49:18', '2024-04-28 00:38:22', NULL);
INSERT INTO `sys_menu` VALUES (89, 0, '0', '功能演示', 2, NULL, '/function', 'Layout', NULL, NULL, NULL, 1, 12, 'menu', '', '2022-10-31 09:18:44', '2024-05-26 21:05:25', NULL);
INSERT INTO `sys_menu` VALUES (90, 89, '0,89', 'Websocket', 1, NULL, '/function/websocket', 'demo/websocket', NULL, NULL, 1, 1, 3, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL);
INSERT INTO `sys_menu` VALUES (91, 89, '0,89', '敬请期待...', 2, NULL, 'other/:id', 'demo/other', NULL, NULL, NULL, 1, 4, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL);
INSERT INTO `sys_menu` VALUES (95, 36, '0,36', '字典组件', 1, NULL, 'dict-demo', 'demo/dict', NULL, NULL, 1, 1, 4, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL);
INSERT INTO `sys_menu` VALUES (97, 89, '0,89', 'Icons', 1, NULL, 'icon-demo', 'demo/icons', NULL, NULL, 1, 1, 2, 'el-icon-Notification', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL);
INSERT INTO `sys_menu` VALUES (102, 26, '0,26', '平台文档(内嵌)', 3, NULL, 'internal-doc', 'demo/internal-doc', NULL, NULL, NULL, 1, 1, 'document', '', '2022-02-18 00:01:40', '2022-02-18 00:01:40', NULL);
INSERT INTO `sys_menu` VALUES (105, 2, '0,1,2', '用户查询', 4, NULL, '', NULL, 'sys:user:query', 0, 0, 1, 0, '', NULL, '2024-04-28 00:37:34', '2024-04-28 00:37:34', NULL);
INSERT INTO `sys_menu` VALUES (106, 2, '0,1,2', '用户导入', 4, NULL, '', NULL, 'sys:user:import', NULL, NULL, 1, 5, '', NULL, '2024-04-28 00:39:15', '2024-04-28 00:39:15', NULL);
INSERT INTO `sys_menu` VALUES (107, 2, '0,1,2', '用户导出', 4, NULL, '', NULL, 'sys:user:export', NULL, NULL, 1, 6, '', NULL, '2024-04-28 00:39:43', '2024-04-28 00:39:43', NULL);
INSERT INTO `sys_menu` VALUES (108, 36, '0,36', '增删改查', 1, NULL, 'curd', 'demo/curd/index', NULL, NULL, 1, 1, 0, '', '', NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (109, 36, '0,36', '列表选择器', 1, NULL, 'table-select', 'demo/table-select/index', NULL, NULL, 1, 1, 1, '', '', NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (110, 0, '0', '路由参数', 2, NULL, '/route-param', 'Layout', NULL, 1, 1, 1, 11, 'el-icon-ElementPlus', NULL, '2024-05-26 21:05:09', '2024-05-26 21:05:34', NULL);
INSERT INTO `sys_menu` VALUES (111, 110, '0,110', '参数(type=1)', 1, NULL, 'route-param-type1', 'demo/route-param', NULL, 0, 1, 1, 1, 'el-icon-Star', NULL, '2024-05-26 21:59:24', '2024-05-26 21:59:37', '{\"type\": \"1\"}');
INSERT INTO `sys_menu` VALUES (112, 110, '0,110', '参数(type=2)', 1, NULL, 'route-param-type2', 'demo/route-param', NULL, 0, 1, 1, 2, 'el-icon-StarFilled', NULL, '2024-05-26 21:46:55', '2024-05-26 21:59:45', '{\"type\": \"2\"}');
INSERT INTO `sys_menu` VALUES (117, 1, '0,1', '系统日志', 1, 'Log', 'log', 'system/log/index', NULL, 0, 1, 1, 6, 'document', NULL, '2024-06-28 07:43:16', '2024-06-28 07:43:16', NULL);

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` bigint NULL DEFAULT NULL COMMENT '修改人ID',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(1-已删除 0-未删除)',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
                             `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
                             `sort` int NULL DEFAULT NULL COMMENT '显示顺序',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '角色状态(1-正常 0-停用)',
                             `data_scope` tinyint NULL DEFAULT NULL COMMENT '数据权限(0-所有数据 1-部门及子部门数据 2-本部门数据3-本人数据)',
                             `create_by` bigint NULL DEFAULT NULL COMMENT '创建人 ID',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除 1-已删除)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE COMMENT '角色名称唯一索引',
                             UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE COMMENT '角色编码唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 128 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ROOT', 1, 1, 0, NULL, '2021-05-21 14:56:51', NULL, '2018-12-23 16:00:00', 0);
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'ADMIN', 2, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (3, '访问游客', 'GUEST', 3, 1, 2, NULL, '2021-05-26 15:49:05', NULL, '2019-05-05 16:00:00', 0);
INSERT INTO `sys_role` VALUES (4, '系统管理员1', 'ADMIN1', 4, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (5, '系统管理员2', 'ADMIN2', 5, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (6, '系统管理员3', 'ADMIN3', 6, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (7, '系统管理员4', 'ADMIN4', 7, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (8, '系统管理员5', 'ADMIN5', 8, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (9, '系统管理员6', 'ADMIN6', 9, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (10, '系统管理员7', 'ADMIN7', 10, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (11, '系统管理员8', 'ADMIN8', 11, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (12, '系统管理员9', 'ADMIN9', 12, 1, 1, NULL, '2021-03-25 12:39:54', NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
                                  `role_id` bigint NOT NULL COMMENT '角色ID',
                                  `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                  UNIQUE INDEX `uk_roleid_menuid`(`role_id` ASC, `menu_id` ASC) USING BTREE COMMENT '角色菜单唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 5);
INSERT INTO `sys_role_menu` VALUES (2, 6);
INSERT INTO `sys_role_menu` VALUES (2, 20);
INSERT INTO `sys_role_menu` VALUES (2, 21);
INSERT INTO `sys_role_menu` VALUES (2, 22);
INSERT INTO `sys_role_menu` VALUES (2, 23);
INSERT INTO `sys_role_menu` VALUES (2, 24);
INSERT INTO `sys_role_menu` VALUES (2, 26);
INSERT INTO `sys_role_menu` VALUES (2, 30);
INSERT INTO `sys_role_menu` VALUES (2, 31);
INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 36);
INSERT INTO `sys_role_menu` VALUES (2, 37);
INSERT INTO `sys_role_menu` VALUES (2, 38);
INSERT INTO `sys_role_menu` VALUES (2, 39);
INSERT INTO `sys_role_menu` VALUES (2, 40);
INSERT INTO `sys_role_menu` VALUES (2, 41);
INSERT INTO `sys_role_menu` VALUES (2, 70);
INSERT INTO `sys_role_menu` VALUES (2, 71);
INSERT INTO `sys_role_menu` VALUES (2, 72);
INSERT INTO `sys_role_menu` VALUES (2, 73);
INSERT INTO `sys_role_menu` VALUES (2, 74);
INSERT INTO `sys_role_menu` VALUES (2, 75);
INSERT INTO `sys_role_menu` VALUES (2, 76);
INSERT INTO `sys_role_menu` VALUES (2, 77);
INSERT INTO `sys_role_menu` VALUES (2, 78);
INSERT INTO `sys_role_menu` VALUES (2, 79);
INSERT INTO `sys_role_menu` VALUES (2, 81);
INSERT INTO `sys_role_menu` VALUES (2, 84);
INSERT INTO `sys_role_menu` VALUES (2, 85);
INSERT INTO `sys_role_menu` VALUES (2, 86);
INSERT INTO `sys_role_menu` VALUES (2, 87);
INSERT INTO `sys_role_menu` VALUES (2, 88);
INSERT INTO `sys_role_menu` VALUES (2, 89);
INSERT INTO `sys_role_menu` VALUES (2, 90);
INSERT INTO `sys_role_menu` VALUES (2, 91);
INSERT INTO `sys_role_menu` VALUES (2, 95);
INSERT INTO `sys_role_menu` VALUES (2, 97);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 117);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                             `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                             `gender` tinyint(1) NULL DEFAULT 1 COMMENT '性别((1-男 2-女 0-保密)',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                             `dept_id` int NULL DEFAULT NULL COMMENT '部门ID',
                             `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户头像',
                             `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
                             `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态((1-正常 0-禁用)',
                             `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `update_by` bigint NULL DEFAULT NULL COMMENT '修改人ID',
                             `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除 1-已删除)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `login_name`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 288 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'root', '有来技术', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://oss.youlai.tech/youlai-boot/2023/05/16/811270ef31f548af9cffc026dfc3777b.gif', '17621590365', 1, 'youlaitech@163.com', NULL, NULL, NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (2, 'admin', '系统管理员', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 1, 'https://oss.youlai.tech/youlai-boot/2023/05/16/811270ef31f548af9cffc026dfc3777b.gif', '17621210366', 1, '', '2019-10-10 13:41:22', NULL, '2022-07-31 12:39:30', NULL, 0);
INSERT INTO `sys_user` VALUES (3, 'test', '测试小用户', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 3, 'https://oss.youlai.tech/youlai-boot/2023/05/16/811270ef31f548af9cffc026dfc3777b.gif', '17621210366', 1, 'youlaitech@163.com', '2021-06-05 01:31:29', NULL, '2021-06-05 01:31:29', NULL, 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` bigint NOT NULL COMMENT '用户ID',
                                  `role_id` bigint NOT NULL COMMENT '角色ID',
                                  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
                                  UNIQUE INDEX `uk_userid_roleid`(`user_id` ASC, `role_id` ASC) USING BTREE COMMENT '用户角色唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);


-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `module` enum('LOGIN','USER','ROLE','DEPT','MENU','DICT') COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志模块',
                           `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志内容',
                           `request_uri` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求路径',
                           `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '方法名',
                           `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP地址',
                           `region` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
                           `execution_time` bigint DEFAULT NULL COMMENT '执行时间(ms)',
                           `browser` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '浏览器',
                           `os` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '终端系统',
                           `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标识(1-已删除 0-未删除)',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统日志表';


SET FOREIGN_KEY_CHECKS = 1;
