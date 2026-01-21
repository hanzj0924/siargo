/*
 Navicat Premium Dump SQL

 Source Server         : siargo
 Source Server Type    : MySQL
 Source Server Version : 80405 (8.4.5)
 Source Host           : localhost:3306
 Source Schema         : siargo

 Target Server Type    : MySQL
 Target Server Version : 80405 (8.4.5)
 File Encoding         : 65001

 Date: 20/01/2026 17:24:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for jb_application
-- ----------------------------
DROP TABLE IF EXISTS `jb_application`;
CREATE TABLE `jb_application`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `brief_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用简介',
  `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用ID',
  `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用密钥',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `type` int NOT NULL COMMENT 'app类型',
  `need_check_sign` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否需要接口校验SIGN',
  `is_inner` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否需要接口校验SIGN',
  `link_target_id` bigint NULL DEFAULT NULL COMMENT '关联目标ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API应用中心的应用APP' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_application
-- ----------------------------
INSERT INTO `jb_application` VALUES (1992880780549791744, '内置_平台自身', '开发平台内置应用', 'jbk8aj5tuygaolt', 'cXZqY3BsNDNjZ2RqcXN2bG0yaGtuc2s2cG1wanFiNWI=', '1', '2025-11-24 16:59:39', '2025-11-24 16:59:39', 1992880779681570816, 1992880779681570816, 101, '0', '1', NULL);

-- ----------------------------
-- Table structure for jb_code_gen
-- ----------------------------
DROP TABLE IF EXISTS `jb_code_gen`;
CREATE TABLE `jb_code_gen`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `pid` bigint NULL DEFAULT 0 COMMENT '父ID',
  `project_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目根路径',
  `is_sub_table` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为子表',
  `sort_rank` int NOT NULL DEFAULT 1 COMMENT '子表的顺序',
  `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模块类型',
  `main_table_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主表名',
  `datasource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源',
  `datasource_remark` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源说明',
  `database_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库类型',
  `is_main_datasource` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为主数据源',
  `main_table_pkey` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'id' COMMENT '主表主键',
  `table_remove_prefix` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据表删除前缀',
  `main_table_idgenmode` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主表主键策略',
  `version_sn` int NOT NULL COMMENT '版本序号',
  `main_table_remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表备注',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能作者',
  `style` int NOT NULL COMMENT '样式类型',
  `is_crud` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否CRUD',
  `is_editable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否可编辑表格',
  `is_tree_table` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为树表',
  `is_check_can_delete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '检测是否可以刪除数据',
  `is_check_can_toggle` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '检测是否可以toggle数据',
  `is_check_can_recover` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '检测是否可以recover数据',
  `editable_submit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可编辑提交方式',
  `state` int NOT NULL COMMENT '生成状态',
  `sub_table_count` int NULL DEFAULT NULL COMMENT '子表数',
  `topnav_id` bigint NULL DEFAULT NULL COMMENT '顶部导航',
  `permission_id` bigint NULL DEFAULT NULL COMMENT '关联权限',
  `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可访问角色',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NOT NULL COMMENT '更新人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `gen_user_id` bigint NULL DEFAULT NULL COMMENT '创建后执行生成人ID',
  `gen_time` datetime NULL DEFAULT NULL COMMENT '创建后执行生成时间',
  `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'modelName',
  `base_model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'baseModelName',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_auto_cache` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用autoCache',
  `is_id_cache` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用idCache',
  `is_key_cache` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用keyCache',
  `key_cache_column` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'keyCache指定Column',
  `key_cache_bind_column` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'keyCache指定bindColumn',
  `controller_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Controller Name',
  `controller_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'controller path',
  `main_java_package` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'java主包pacakge',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Service Name',
  `controller_package` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Controller代码包',
  `service_package` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Service代码包',
  `index_html_page_icon` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'index.html标题icon',
  `index_html_page_title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'index.html页面标题',
  `model_package` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'model 所属package',
  `html_view_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'html view path',
  `routes_scan_package` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由扫描包',
  `is_gen_model` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否需要生成Model',
  `is_table_use_record` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '表格是否使用record',
  `is_table_record_camel_case` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '表格列名用驼峰的attrName',
  `is_import_excel` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否支持Excel导入',
  `is_export_excel` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否支持Excel导出',
  `is_export_excel_by_checked_ids` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用 导出选中行功能',
  `is_export_excel_by_form` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用导出表单查询结果功能',
  `is_export_excel_all` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用导出所有数据',
  `is_copy_to_excel` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否支持表格复制到excel',
  `is_copy_from_excel` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否支持从excel复制到可编辑表格',
  `is_toolbar` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否使用toolbar模式',
  `is_headbox` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否使用headbox',
  `is_leftbox` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否使用leftBox',
  `is_rightbox` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否使用rightBox',
  `is_footbox` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否使用footbox',
  `is_paginate` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否分页查询',
  `is_table_sortable_move` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否开启移动排序功能',
  `leftbox_width` int NULL DEFAULT 220 COMMENT 'leftbox width',
  `rightbox_width` int NULL DEFAULT 220 COMMENT 'right width',
  `is_headbox_height_auto` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT 'headbox高度自动',
  `is_footbox_height_auto` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT 'footbox高度自动',
  `headbox_height` int NULL DEFAULT 60 COMMENT 'headbox height',
  `footbox_height` int NULL DEFAULT 220 COMMENT 'footbox height',
  `is_leftbox_footer` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用leftbox的footer',
  `is_rightbox_footer` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用rightbox的footer',
  `leftbox_footer_button_count` int NULL DEFAULT NULL COMMENT 'leftbox footer button count',
  `rightbox_footer_button_count` int NULL DEFAULT NULL COMMENT 'rightbox footer button count',
  `leftbox_title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'leftbox' COMMENT 'leftbox title',
  `leftbox_icon` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'fa fa-cog' COMMENT 'leftbox icon',
  `rightbox_title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'rightbox' COMMENT 'rightbox title',
  `rightbox_icon` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'fa fa-cog' COMMENT 'rightbox icon',
  `is_show_optcol_sort` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否开启操作列排序功能',
  `is_show_optcol_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否开启操作列显示编辑按钮',
  `is_show_optcol_del` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否开启操作列显示删除按钮',
  `is_show_optcol` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否显示操作列',
  `is_show_optcol_recover` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否显示操作列的恢复按钮',
  `default_sort_column` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认排序字段',
  `default_sort_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认排序方式',
  `table_optcol_width` int NOT NULL DEFAULT 80 COMMENT '长度',
  `is_table_column_resize` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格是否开启调整列宽功能',
  `is_table_prepend_column` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否增加填充列',
  `table_prepend_column_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格chechbox radio配置类型',
  `table_prepend_column_index` int NOT NULL DEFAULT 1 COMMENT '填充列到第几列',
  `is_table_prepend_column_linkparent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '填充列linkparent',
  `is_table_prepend_column_linkson` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '填充列linkson',
  `table_prepend_column_rowspan` int NOT NULL DEFAULT 1 COMMENT '填充列表头是几行rowspan',
  `is_table_prepend_column_click_active` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否点击行就切换列填充组件选中状态',
  `table_fixed_column_left` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '左侧固定列',
  `table_fixed_column_right` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '右侧固定列',
  `table_pagesize_options` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分页pagesize自定义设置',
  `table_width_assign` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格宽度自定义值',
  `table_width` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'fill' COMMENT '表格宽度',
  `table_height_assign` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格高度自定义值',
  `table_height` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'fill' COMMENT '表格高度',
  `table_default_sort_column` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'id' COMMENT '表格默认排序字段',
  `table_default_sort_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'desc' COMMENT '表格默认排序类型',
  `is_keywords_search` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否开启关键词查询',
  `keywords_match_columns` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关键词匹配列',
  `toolbar_extra_button_size` int NULL DEFAULT NULL COMMENT 'toolbar 额外预留按钮个数',
  `is_deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标识',
  `form_column_size` int NOT NULL DEFAULT 1 COMMENT '表单分几列',
  `is_form_group_row` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表单form-group风格 左右还是上下',
  `form_column_proportion` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单分多列 比例值',
  `form_column_direction` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'v' COMMENT '表单列排布方向 横向还是纵向',
  `form_group_proportion` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '2:10' COMMENT 'form-group row状态下的比例',
  `is_view_use_path` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用Path注解路由',
  `view_layout` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '使用布局器',
  `is_need_new_route` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否需要创建新的路由配置类',
  `routes_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '哪个路由配置类',
  `is_need_admin_interceptor` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否需要后台管理权限拦截器',
  `extra_interceptor_class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外配置的拦截器',
  `is_table_multi_conditions_mode` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '表格查询条件是否启用高级多条件模式',
  `is_table_multi_conditions_default_hide` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格查询高级模式 是否隐藏条件 默认隐藏',
  `is_table_multi_conditions_btn_show_title` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格高级查询条件切换按钮是否显示标题',
  `is_toolbar_add_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格toolbar上启用添加按钮',
  `is_toolbar_edit_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格toolbar上启用编辑按钮',
  `is_toolbar_del_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格toolbar上启用删除按钮',
  `is_toolbar_recover_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '表格toolbar上启用恢复按钮 当有is_deleted时',
  `is_toolbar_refresh_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '表格tolbar上启用刷新按钮',
  `is_page_title_add_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用pageTitle上的添加按钮',
  `is_page_title_refresh_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用pageTitle上的刷新按钮',
  `is_page_title_init_rank_btn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用pageTitle上的初始化顺序按钮',
  `is_project_system_log` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用systemLog日志',
  `project_system_log_target_type_text` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统日志text',
  `project_system_log_target_type_value` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统日志value值',
  `project_system_log_target_type_key_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统日志KeyName',
  `form_dialog_area` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '980,700' COMMENT 'form表单的dialog的area属性 长宽',
  `is_base_model_gen_col_constant` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否在baseModel中生成字段常量',
  `is_base_model_gen_col_constant_to_uppercase` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否在baseModel中生成的字段常量 名称转大写',
  `thead_bg_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表头背景色',
  `thead_fr_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表头文字颜色',
  `is_table_row_click_active` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否表格选中定色',
  `active_tr_bg_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选中行背景色',
  `active_tr_fr_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '选中行文字颜色',
  `is_gen_cache_util_class` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成缓存工具类',
  `cache_class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缓存工具类名',
  `cache_class_package` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缓存工具类包',
  `is_cache_get_name` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成getName',
  `is_cache_get_sn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成getSn',
  `is_cache_get_name_by_sn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成getNameBySn(sn)',
  `is_cache_get_by_sn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成getBySn(sn)',
  `model_title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型名称',
  `is_gen_options_action` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成options接口',
  `is_return_option_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否返回Option类型',
  `options_text_column` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'name' COMMENT 'options接口text用哪一列',
  `options_value_column` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'id' COMMENT 'options接口value用哪一列',
  `is_gen_autocomplete_action` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否生成Autocomplete接口',
  `autocomplete_limit` int NOT NULL DEFAULT 20 COMMENT 'autocomplete接口limit',
  `autocomplete_match_columns` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'autocomplete接口匹配字段',
  `table_opt_sort_column` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格排序操作用哪个column',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_code_gen
-- ----------------------------

-- ----------------------------
-- Table structure for jb_code_gen_model_attr
-- ----------------------------
DROP TABLE IF EXISTS `jb_code_gen_model_attr`;
CREATE TABLE `jb_code_gen_model_attr`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `code_gen_id` bigint NOT NULL COMMENT '所属codeGen',
  `col_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '列名',
  `attr_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性名',
  `java_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性类型',
  `attr_length` int NOT NULL DEFAULT 20 COMMENT '属性长度',
  `attr_fixed` int NULL DEFAULT 0 COMMENT '属性小数点',
  `attr_default_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `sort_rank` int NOT NULL DEFAULT 1 COMMENT '数据表内默认顺序',
  `sort_rank_intable` int NOT NULL DEFAULT 1 COMMENT '表格中的排序',
  `sort_rank_inform` int NOT NULL DEFAULT 1 COMMENT '表单中的排序',
  `sort_rank_insearch` int NOT NULL DEFAULT 1 COMMENT '查询条件中的顺序',
  `is_pkey` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否主键',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否必填',
  `is_search_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '作为查询条件是否必填',
  `data_rule_for_search` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询条件必填校验规则',
  `data_tips_for_search` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询条件不符合校验的提示信息',
  `form_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单单显示文本',
  `placeholder` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单placeholder',
  `table_label` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格中显示文本',
  `search_form_label` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询表单提示文本',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_keywords_column` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为关键词查询列',
  `is_form_ele` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否表单可编辑元素',
  `is_table_col` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否表格列',
  `is_table_switchbtn` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为表格switchbtn',
  `table_col_width` int NOT NULL DEFAULT 100 COMMENT '列宽',
  `is_need_fixed_width` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否固定宽度',
  `is_search_ele` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否检索条件',
  `is_search_hidden` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为检索隐藏条件',
  `col_format` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '格式化操作值',
  `search_ui_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询用ui 组件类型',
  `search_data_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询用组件数据源类型',
  `search_data_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询用组件数据值',
  `search_default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '查询用组件默认值',
  `is_single_line` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '独立新行',
  `need_data_handler` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否需要data_handler',
  `form_ui_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单组件类型',
  `form_jboltinput_filter_handler` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jboltinput filter handler',
  `is_form_jboltinput_jstree_checkbox` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT 'jboltinput jstree是否有checkbox',
  `is_form_jboltinput_jstree_only_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jboltinput jstree checkbox只选子节点',
  `form_data_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单组件数据源类型',
  `form_data_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单组件数据值',
  `form_default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单组件默认值',
  `data_rule_assign` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单校验规则 自定义',
  `data_rule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '校验规则',
  `data_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '校验提示信息',
  `is_import_col` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为导入列',
  `is_export_col` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '导出列',
  `is_sortable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否可排序',
  `table_ui_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可编辑表格显示组件类型',
  `table_data_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格组件数据库类型',
  `table_data_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格组件数据值',
  `form_ele_width` int NOT NULL DEFAULT 0 COMMENT '组件自定义宽度',
  `is_item_inline` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT 'radio checkbox等是否inline',
  `form_data_text_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-text-attr',
  `form_data_value_attr` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-value-attr',
  `form_data_column_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-column-attr',
  `search_data_text_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-text-attr',
  `search_data_value_attr` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-value-attr',
  `search_data_column_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-column-attr',
  `table_data_text_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-text-attr',
  `table_data_value_attr` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-value-attr',
  `table_data_column_attr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'data-column-attr',
  `is_need_translate` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否需要翻译',
  `translate_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '翻译类型',
  `translate_use_value` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '翻译用值',
  `translate_col_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '翻译后的列名',
  `is_upload_to_qiniu` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否上传到七牛',
  `form_upload_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传地址',
  `form_img_uploader_area` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '200,200' COMMENT '上传组件area',
  `form_maxsize` int NULL DEFAULT 200 COMMENT '上传尺寸限制',
  `qiniu_bucket_sn` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '七牛bucket sn',
  `qiniu_file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '[dateTime]/[randomId]/[filename]' COMMENT '七牛file key',
  `is_need_check_exists` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否检测数据重复字段',
  `is_fuzzy_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否模糊查询',
  `fuzzy_query_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'default' COMMENT '模糊查询类型',
  `form_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帮助信息',
  `form_tips_color` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'secondary' COMMENT '帮助信息的文字颜色',
  `data_min` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最小值',
  `data_max` decimal(10, 2) NOT NULL DEFAULT 100.00 COMMENT '最大值',
  `data_step` decimal(10, 2) NOT NULL DEFAULT 1.00 COMMENT '步长',
  `ig_prepend_html` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前缀html',
  `ig_append_html` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后缀html',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'CodeGen模型详细设计' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_code_gen_model_attr
-- ----------------------------

-- ----------------------------
-- Table structure for jb_datasource_filter
-- ----------------------------
DROP TABLE IF EXISTS `jb_datasource_filter`;
CREATE TABLE `jb_datasource_filter`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置名称',
  `table_name_filter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '不需要生成的表名',
  `table_name_contains` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '需要排除包含字符',
  `table_name_patterns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '需要排除符合正则的',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据源过滤配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_datasource_filter
-- ----------------------------
INSERT INTO `jb_datasource_filter` VALUES (1992880779480244224, 'main[主]', 'main', 'dept,emp,salgrade,bonus,dtproperties', 'sqlite_,_old_,jb_', 'jb_wechat_user_-?[1-9]\\d*', 0, 0, '2025-11-24 16:59:38', '2025-11-24 16:59:38');

-- ----------------------------
-- Table structure for jb_dept
-- ----------------------------
DROP TABLE IF EXISTS `jb_dept`;
CREATE TABLE `jb_dept`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `full_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '全称',
  `pid` bigint NULL DEFAULT NULL COMMENT '父级ID',
  `dept_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门路径',
  `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
  `leader` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系地址',
  `zipcode` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `sort_rank` int NULL DEFAULT NULL COMMENT '顺序',
  `sn` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '机构代码',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '启用/禁用',
  `update_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织机构' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_dept
-- ----------------------------
INSERT INTO `jb_dept` VALUES (2001545887328747520, '质管部', '质管部', 0, '2001545887328747520', '6', NULL, NULL, NULL, NULL, NULL, NULL, 2, '2', '1', '2025-12-24 16:55:27', '2025-12-18 14:51:41');
INSERT INTO `jb_dept` VALUES (2007080161399705600, '工艺部', '工艺部', 0, '2007080161399705600', '6', NULL, NULL, NULL, NULL, NULL, NULL, 2, '3', '1', '2026-01-05 17:11:51', '2026-01-02 21:22:55');

-- ----------------------------
-- Table structure for jb_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `jb_dictionary`;
CREATE TABLE `jb_dictionary`  (
  `id` bigint NOT NULL COMMENT '字典ID主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type_id` bigint NULL DEFAULT NULL COMMENT '字典类型ID',
  `pid` bigint NULL DEFAULT NULL COMMENT '父类ID',
  `sort_rank` int NULL DEFAULT NULL COMMENT '排序',
  `sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号编码',
  `type_key` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型KEY',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用',
  `is_build_in` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否内置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_dictionary
-- ----------------------------
INSERT INTO `jb_dictionary` VALUES (1993858755363016704, '是', 1993858755283324928, 0, NULL, 'true', 'options_boolean', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755363016705, '否', 1993858755283324928, 0, NULL, 'false', 'options_boolean', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755467874304, '启用', 1993858755451097088, 0, NULL, 'true', 'options_enable', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755488845824, '禁用', 1993858755451097088, 0, NULL, 'false', 'options_enable', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755543371776, '男', 1993858755497234432, 0, NULL, '1', 'sex', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755560148992, '女', 1993858755497234432, 0, NULL, '2', 'sex', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755606286336, '总部', 1993858755589509120, 0, NULL, '1', 'dept_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755623063552, '省级公司', 1993858755589509120, 0, NULL, '2', 'dept_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755639840768, '市级公司', 1993858755589509120, 0, NULL, '3', 'dept_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755644035072, '区县级公司', 1993858755589509120, 0, NULL, '4', 'dept_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755644035073, '办事处', 1993858755589509120, 0, NULL, '5', 'dept_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755660812288, '部门', 1993858755589509120, 0, NULL, '6', 'dept_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755702755328, '高管', 1993858755690172416, 0, NULL, '1', 'post_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755702755329, '中层', 1993858755690172416, 0, NULL, '2', 'post_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755702755330, '基层', 1993858755690172416, 0, NULL, '3', 'post_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755702755331, '其他', 1993858755690172416, 0, NULL, '4', 'post_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755799224320, '公告', 1993858755769864192, 0, NULL, '1', 'sys_notice_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755799224321, '新闻', 1993858755769864192, 0, NULL, '2', 'sys_notice_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755807612928, '会议', 1993858755769864192, 0, NULL, '3', 'sys_notice_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755807612929, '放假', 1993858755769864192, 0, NULL, '4', 'sys_notice_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755816001536, '其它', 1993858755769864192, 0, NULL, '5', 'sys_notice_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755870527488, '普通', 1993858755853750272, 0, NULL, '1', 'sys_notice_priority_level', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755887304704, '一般', 1993858755853750272, 0, NULL, '2', 'sys_notice_priority_level', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755895693312, '紧急', 1993858755853750272, 0, NULL, '3', 'sys_notice_priority_level', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755958607872, '全部', 1993858755933442048, 0, NULL, '1', 'sys_notice_receiver_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755966996480, '角色', 1993858755933442048, 0, NULL, '2', 'sys_notice_receiver_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755975385088, '部门', 1993858755933442048, 0, NULL, '3', 'sys_notice_receiver_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755987968000, '岗位', 1993858755933442048, 0, NULL, '4', 'sys_notice_receiver_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858755992162304, '用户', 1993858755933442048, 0, NULL, '5', 'sys_notice_receiver_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756055076864, '普通', 1993858756029911040, 0, NULL, '1', 'todo_priority_level', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756055076865, '一般', 1993858756029911040, 0, NULL, '2', 'todo_priority_level', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756055076866, '紧急', 1993858756029911040, 0, NULL, '3', 'todo_priority_level', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756122185728, '未开始', 1993858756097019904, 0, NULL, '1', 'todo_state', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756122185729, '进行中', 1993858756097019904, 0, NULL, '2', 'todo_state', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756122185730, '已完成', 1993858756097019904, 0, NULL, '3', 'todo_state', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756122185731, '已取消', 1993858756097019904, 0, NULL, '4', 'todo_state', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756122185732, '未完成', 1993858756097019904, 0, NULL, '5', 'todo_state', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756197683201, '无链接无内容', 1993858756197683200, 0, NULL, '1', 'todo_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756197683202, '无链接有内容', 1993858756197683200, 0, NULL, '2', 'todo_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756197683203, '有链接无内容', 1993858756197683200, 0, NULL, '3', 'todo_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756235431936, '有链接有内容', 1993858756197683200, 0, NULL, '4', 'todo_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756294152192, '创建时间', 1993858756264792064, 0, NULL, '1', 'todo_datetime_column', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756306735104, '更新时间', 1993858756264792064, 0, NULL, '2', 'todo_datetime_column', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756306735105, '待完成时间', 1993858756264792064, 0, NULL, '3', 'todo_datetime_column', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756315123712, '实际完成时间', 1993858756264792064, 0, NULL, '4', 'todo_datetime_column', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756323512320, '取消时间', 1993858756264792064, 0, NULL, '5', 'todo_datetime_column', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756378038272, '个人', 1993858756336095232, 0, NULL, '1', 'qiniu_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756378038273, '企业', 1993858756336095232, 0, NULL, '2', 'qiniu_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756449341440, '华东', 1993858756428369920, 0, NULL, 'z0', 'qiniu_region', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756457730048, '华北', 1993858756428369920, 0, NULL, 'z1', 'qiniu_region', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756466118656, '华南', 1993858756428369920, 0, NULL, 'z2', 'qiniu_region', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756474507264, '北美', 1993858756428369920, 0, NULL, 'na0', 'qiniu_region', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756478701568, '东南亚', 1993858756428369920, 0, NULL, 'as0', 'qiniu_region', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756537421824, '自增_auto_int', 1993858756478701569, 0, NULL, 'auto', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756545810432, '自增_auto_long', 1993858756478701569, 0, NULL, 'auto_long', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756554199040, '自增_auto_string', 1993858756478701569, 0, NULL, 'auto_string', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756558393344, '自增序列_sequence', 1993858756478701569, 0, NULL, 'sequence', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756566781952, '自增序列_sequence_long', 1993858756478701569, 0, NULL, 'sequence_long', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756570976256, '自增序列_pgsql_serial', 1993858756478701569, 0, NULL, 'serial', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756575170560, '自增序列_pgsql_bigserial', 1993858756478701569, 0, NULL, 'bigserial', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756583559168, '雪花_snowflake_long', 1993858756478701569, 0, NULL, 'snowflake', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756591947776, '雪花_snowflake_字符串', 1993858756478701569, 0, NULL, 'snowflake_string', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756596142080, 'UUID', 1993858756478701569, 0, NULL, 'uuid', 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756646473728, 'Boolean转文字', 1993858756625502208, 0, NULL, 'boolean_to_str', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756654862336, 'Boolean转对号[√]', 1993858756625502208, 0, NULL, 'boolean_to_check', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756663250944, 'Enable转文字', 1993858756625502208, 0, NULL, 'enable_to_str', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756667445248, 'Enable转对号[√]', 1993858756625502208, 0, NULL, 'enable_to_check', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756671639552, '日期_yyyy-MM-dd', 1993858756625502208, 0, NULL, 'date_ymd', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756684222464, '日期_yyyy-MM', 1993858756625502208, 0, NULL, 'date_ym', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756684222465, '日期时间_yyyy-MM-dd HH:mm:ss', 1993858756625502208, 0, NULL, 'date_ymdhms', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756684222466, '日期时间_yyyy-MM-dd HH:mm', 1993858756625502208, 0, NULL, 'date_ymdhm', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756684222467, '日期_HH:mm:ss', 1993858756625502208, 0, NULL, 'date_hms', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756684222468, '日期_HH:mm', 1993858756625502208, 0, NULL, 'date_hm', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756684222469, '时间_HH:mm:ss', 1993858756625502208, 0, NULL, 'time_hms', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756726165504, '时间_HH:mm', 1993858756625502208, 0, NULL, 'time_hm', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756726165505, '图片URL_real_image', 1993858756625502208, 0, NULL, 'real_image', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756738748416, '文件URL_real_url', 1993858756625502208, 0, NULL, 'real_url', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756747137024, '时间美化_pretty_time', 1993858756625502208, 0, NULL, 'pretty_time', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756751331328, '字符串转驼峰_str_camel', 1993858756625502208, 0, NULL, 'str_camel', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756763914240, '性别转汉字_sex', 1993858756625502208, 0, NULL, 'sex', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756768108544, '小数点保留_1位', 1993858756625502208, 0, NULL, 'tofixed_1', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756772302848, '小数点保留_2位', 1993858756625502208, 0, NULL, 'tofixed_2', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756780691456, '小数点保留_3位', 1993858756625502208, 0, NULL, 'tofixed_3', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756784885760, '小数点保留_4位', 1993858756625502208, 0, NULL, 'tofixed_4', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756789080064, '金额_保留1位', 1993858756625502208, 0, NULL, 'money_1', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756797468672, '金额_保留2位', 1993858756625502208, 0, NULL, 'money_2', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756801662976, '金额_保留3位', 1993858756625502208, 0, NULL, 'money_3', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756814245888, '金额_保留4位', 1993858756625502208, 0, NULL, 'money_4', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756818440192, '弹出查看静态内容Dialog按钮', 1993858756625502208, 0, NULL, 'static_view_content_dialog_btn', 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756872966144, '用户输入', 1993858756851994624, 0, NULL, 'user_input', 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756877160448, '系统_数据字典', 1993858756851994624, 0, NULL, 'sys_dictionary', 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756889743360, '自定义输入URL', 1993858756851994624, 0, NULL, 'url', 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756893937664, '自定义输入Action名称', 1993858756851994624, 0, NULL, 'action', 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756902326272, '自定义输入option', 1993858756851994624, 0, NULL, 'option', 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756902326273, '枚举类', 1993858756851994624, 0, NULL, 'enum', 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756965240832, '字典_ID', 1993858756935880704, 0, NULL, 'sys_dic_id', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756977823744, '字典_SN', 1993858756935880704, 0, NULL, 'sys_dic_sn', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756982018048, '系统用户_ID转UserName', 1993858756935880704, 0, NULL, 'sys_user_id_to_username', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756986212352, '系统用户_ID转Name', 1993858756935880704, 0, NULL, 'sys_user_id_to_name', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756990406656, '系统用户_用户名转Name', 1993858756935880704, 0, NULL, 'sys_user_username_to_name', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858756998795264, '缓存', 1993858756935880704, 0, NULL, 'cache', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757002989568, '枚举类', 1993858756935880704, 0, NULL, 'enum', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757007183872, '自定义静态 Method', 1993858756935880704, 0, NULL, 'static_method', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757015572480, '自定义Service Method', 1993858756935880704, 0, NULL, 'service_method', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757019766784, '自定义Key-Value数据', 1993858756935880704, 0, NULL, 'kv_data', 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757078487040, '单行输入框_input', 1993858757053321216, 0, NULL, 'input', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757082681344, '密码输入框_password', 1993858757053321216, 0, NULL, 'password', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757086875648, '数字输入框_number', 1993858757053321216, 0, NULL, 'number', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757095264256, '数字输入框_带计算器', 1993858757053321216, 0, NULL, 'calculator', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757099458560, '多行文本框_textarea', 1993858757053321216, 0, NULL, 'textarea', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757107847168, '选择器_单选_select', 1993858757053321216, 0, NULL, 'select', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757112041472, '选择器_多选_select', 1993858757053321216, 0, NULL, 'select_multi', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757116235776, '选择器_单选_select2', 1993858757053321216, 0, NULL, 'select2', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757124624384, '选择器_多选_select2', 1993858757053321216, 0, NULL, 'select2_multi', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757128818688, '输入自动补全_autocomplete', 1993858757053321216, 0, NULL, 'autocomplete', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757133012992, '单选_radio', 1993858757053321216, 0, NULL, 'radio', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757137207296, '多选_checkbox', 1993858757053321216, 0, NULL, 'checkbox', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757145595904, 'jboltinput', 1993858757053321216, 0, NULL, 'jboltinput', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757149790208, 'jboltinput_jstree', 1993858757053321216, 0, NULL, 'jboltinput_jstree', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757158178816, 'jboltinput_table', 1993858757053321216, 0, NULL, 'jboltinput_table', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757162373120, '原生_日期_date', 1993858757053321216, 0, NULL, 'input_date', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757170761728, '原生_时间_time', 1993858757053321216, 0, NULL, 'input_time', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757174956032, '原生_日期时间_datetime', 1993858757053321216, 0, NULL, 'input_datetime', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757179150336, '原生_月份_month', 1993858757053321216, 0, NULL, 'input_month', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757191733248, '原生_周_week', 1993858757053321216, 0, NULL, 'input_week', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757195927552, 'Laydate_日期_date', 1993858757053321216, 0, NULL, 'laydate_date', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757200121856, 'Laydate_时间_time', 1993858757053321216, 0, NULL, 'laydate_time', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757204316160, 'Laydate_日期时间_datetime', 1993858757053321216, 0, NULL, 'laydate_datetime', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757208510464, 'Laydate_年份_year', 1993858757053321216, 0, NULL, 'laydate_year', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757212704768, 'Laydate_月份_month', 1993858757053321216, 0, NULL, 'laydate_month', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757216899072, 'Laydate_范围_year', 1993858757053321216, 0, NULL, 'laydate_range_year', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757225287680, 'Laydate_范围_month', 1993858757053321216, 0, NULL, 'laydate_range_month', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757229481984, 'Laydate_范围_date', 1993858757053321216, 0, NULL, 'laydate_range_date', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757233676288, 'Laydate_范围_time', 1993858757053321216, 0, NULL, 'laydate_range_time', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757242064896, 'Laydate_范围_datetime', 1993858757053321216, 0, NULL, 'laydate_range_datetime', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757242064897, 'rangeslider组件', 1993858757053321216, 0, NULL, 'rangerSlider', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757242064898, 'rangeslider组件_double', 1993858757053321216, 0, NULL, 'rangerSlider_double', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757242064899, 'switch Button', 1993858757053321216, 0, NULL, 'switchbtn', 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757304979456, '单行输入框_input', 1993858757279813632, 0, NULL, 'input', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757313368064, '密码输入框_password', 1993858757279813632, 0, NULL, 'password', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757321756672, '数字输入框_number', 1993858757279813632, 0, NULL, 'number', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757325950976, '数字输入框_带计算器', 1993858757279813632, 0, NULL, 'calculator', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757330145280, '多行文本框_textarea', 1993858757279813632, 0, NULL, 'textarea', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757334339584, '选择器_单选_select', 1993858757279813632, 0, NULL, 'select', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757338533888, '选择器_多选_select', 1993858757279813632, 0, NULL, 'select_multi', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757346922496, '输入自动补全_autocomplete', 1993858757279813632, 0, NULL, 'autocomplete', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757351116800, '单checkbox', 1993858757279813632, 0, NULL, 'checkbox', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757355311104, 'jboltinput', 1993858757279813632, 0, NULL, 'jboltinput', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757359505408, 'jboltinput_jstree', 1993858757279813632, 0, NULL, 'jboltinput_jstree', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757363699712, 'jboltinput_table', 1993858757279813632, 0, NULL, 'jboltinput_table', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757367894016, '原生_日期_date', 1993858757279813632, 0, NULL, 'input_date', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757372088320, '原生_时间_time', 1993858757279813632, 0, NULL, 'input_time', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757376282624, '原生_日期时间_datetime', 1993858757279813632, 0, NULL, 'input_datetime', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757380476928, '原生_月份_month', 1993858757279813632, 0, NULL, 'input_month', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757388865536, '原生_周_week', 1993858757279813632, 0, NULL, 'input_week', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757393059840, 'Laydate_日期_date', 1993858757279813632, 0, NULL, 'laydate_date', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757401448448, 'Laydate_时间_time', 1993858757279813632, 0, NULL, 'laydate_time', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757405642752, 'Laydate_日期时间_datetime', 1993858757279813632, 0, NULL, 'laydate_datetime', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757409837056, 'Laydate_年份_year', 1993858757279813632, 0, NULL, 'laydate_year', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757414031360, 'Laydate_月份_month', 1993858757279813632, 0, NULL, 'laydate_month', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757418225664, 'Laydate_范围_year', 1993858757279813632, 0, NULL, 'laydate_range_year', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757422419968, 'Laydate_范围_month', 1993858757279813632, 0, NULL, 'laydate_range_month', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757426614272, 'Laydate_范围_date', 1993858757279813632, 0, NULL, 'laydate_range_date', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757430808576, 'Laydate_范围_time', 1993858757279813632, 0, NULL, 'laydate_range_time', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757435002880, 'Laydate_范围_datetime', 1993858757279813632, 0, NULL, 'laydate_range_datetime', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757439197184, 'rangeslider组件', 1993858757279813632, 0, NULL, 'rangerSlider', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757447585792, 'rangeslider组件_double', 1993858757279813632, 0, NULL, 'rangerSlider_double', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757451780096, 'switch Button', 1993858757279813632, 0, NULL, 'switchbtn', 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757502111744, '单行输入框_input', 1993858757485334528, 0, NULL, 'input', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757506306048, '密码输入框_password', 1993858757485334528, 0, NULL, 'password', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757514694656, '数字输入框_number', 1993858757485334528, 0, NULL, 'number', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757518888960, '数字输入框_带计算器', 1993858757485334528, 0, NULL, 'calculator', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757518888961, '多行文本框_textarea', 1993858757485334528, 0, NULL, 'textarea', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757527277568, '选择器_单选_select', 1993858757485334528, 0, NULL, 'select', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757531471872, '选择器_多选_select', 1993858757485334528, 0, NULL, 'select_multi', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757535666176, '选择器_单选_select2', 1993858757485334528, 0, NULL, 'select2', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757539860480, '选择器_多选_select2', 1993858757485334528, 0, NULL, 'select2_multi', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757548249088, '输入自动补全_autocomplete', 1993858757485334528, 0, NULL, 'autocomplete', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757552443392, '单选_radio', 1993858757485334528, 0, NULL, 'radio', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757556637696, '多选_checkbox', 1993858757485334528, 0, NULL, 'checkbox', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757560832000, 'jboltinput', 1993858757485334528, 0, NULL, 'jboltinput', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757565026304, 'jboltinput_jstree', 1993858757485334528, 0, NULL, 'jboltinput_jstree', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757569220608, 'jboltinput_table', 1993858757485334528, 0, NULL, 'jboltinput_table', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757577609216, '富文本_summernote', 1993858757485334528, 0, NULL, 'summernote', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757577609217, '富文本_summernote_精简', 1993858757485334528, 0, NULL, 'summernote_simple', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757581803520, '富文本_neditor', 1993858757485334528, 0, NULL, 'neditor', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757594386432, '富文本_neditor_精简', 1993858757485334528, 0, NULL, 'neditor_simple', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757598580736, '原生_日期_date', 1993858757485334528, 0, NULL, 'input_date', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757602775040, '原生_时间_time', 1993858757485334528, 0, NULL, 'input_time', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757606969344, '原生_日期时间_datetime', 1993858757485334528, 0, NULL, 'input_datetime', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757611163648, '原生_月份_month', 1993858757485334528, 0, NULL, 'input_month', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757615357952, '原生_周_week', 1993858757485334528, 0, NULL, 'input_week', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757619552256, 'Laydate_日期_date', 1993858757485334528, 0, NULL, 'laydate_date', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757623746560, 'Laydate_时间_time', 1993858757485334528, 0, NULL, 'laydate_time', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757627940864, 'Laydate_日期时间_datetime', 1993858757485334528, 0, NULL, 'laydate_datetime', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757632135168, 'Laydate_年份_year', 1993858757485334528, 0, NULL, 'laydate_year', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757636329472, 'Laydate_月份_month', 1993858757485334528, 0, NULL, 'laydate_month', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757644718080, 'Laydate_范围_year', 1993858757485334528, 0, NULL, 'laydate_range_year', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757648912384, 'Laydate_范围_month', 1993858757485334528, 0, NULL, 'laydate_range_month', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757653106688, 'Laydate_范围_date', 1993858757485334528, 0, NULL, 'laydate_range_date', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757657300992, 'Laydate_范围_time', 1993858757485334528, 0, NULL, 'laydate_range_time', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757665689600, 'Laydate_范围_datetime', 1993858757485334528, 0, NULL, 'laydate_range_datetime', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757669883904, 'rangeslider组件', 1993858757485334528, 0, NULL, 'rangerSlider', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757678272512, 'rangeslider组件_double', 1993858757485334528, 0, NULL, 'rangerSlider_double', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757682466816, 'switch Button', 1993858757485334528, 0, NULL, 'switchbtn', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757686661120, '单图上传_imguploader', 1993858757485334528, 0, NULL, 'imguploader', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757690855424, '多图上传_imguploader', 1993858757485334528, 0, NULL, 'imguploader_multi', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757699244032, '单文件上传_fileuploader', 1993858757485334528, 0, NULL, 'fileuploader', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757703438336, '多文件上传_fileuploader', 1993858757485334528, 0, NULL, 'fileuploader_multi', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757707632640, '树_jstree', 1993858757485334528, 0, NULL, 'jstree', 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757762158592, '普通必填', 1993858757745381376, 0, NULL, 'required', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757770547200, 'select必选', 1993858757745381376, 0, NULL, 'select', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757774741504, 'radio必选', 1993858757745381376, 0, NULL, 'radio', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757783130112, 'checkbox必选', 1993858757745381376, 0, NULL, 'checkbox', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757787324416, '数字', 1993858757745381376, 0, NULL, 'number', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757791518720, '正数', 1993858757745381376, 0, NULL, 'pnumber', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757795713024, '非负数', 1993858757745381376, 0, NULL, 'pznumber', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757799907328, '整数', 1993858757745381376, 0, NULL, 'int', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757799907329, '正整数', 1993858757745381376, 0, NULL, 'pint', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757799907330, '自然数', 1993858757745381376, 0, NULL, 'pzint', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757799907331, '电子邮件地址', 1993858757745381376, 0, NULL, 'email', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757825073152, '磁盘文件地址', 1993858757745381376, 0, NULL, 'filepath', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757833461760, '网址', 1993858757745381376, 0, NULL, 'url', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757833461761, '网址_不带http', 1993858757745381376, 0, NULL, 'url_nohttp', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757841850368, '日期时间(年-月-日 时:分:秒)', 1993858757745381376, 0, NULL, 'datetime', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757841850369, '日期时间(年-月-日 时:分)', 1993858757745381376, 0, NULL, 'datetime_hm', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757841850370, '日期(年-月-日)', 1993858757745381376, 0, NULL, 'date', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757841850371, '时间(时:分:秒)', 1993858757745381376, 0, NULL, 'time', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757871210496, '时间(时:分)', 1993858757745381376, 0, NULL, 'time_hm', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757875404800, '金额_两位小数', 1993858757745381376, 0, NULL, 'money', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757883793408, '金额_四位小数', 1993858757745381376, 0, NULL, 'money_4', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757887987712, '手机号', 1993858757745381376, 0, NULL, 'phone', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757892182016, '座机电话号', 1993858757745381376, 0, NULL, 'tel', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757900570624, '中文汉字', 1993858757745381376, 0, NULL, 'zh_cn', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757904764928, 'IP地址', 1993858757745381376, 0, NULL, 'ip', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757908959232, '邮政编码(6位)', 1993858757745381376, 0, NULL, 'postalcode', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757913153536, '中国公民身份证号', 1993858757745381376, 0, NULL, 'idcardno', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757917347840, '英文字母_数字_下划线', 1993858757745381376, 0, NULL, 'letter_num', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757921542144, '英文字母', 1993858757745381376, 0, NULL, 'letter', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757929930752, '密码(6-16位)', 1993858757745381376, 0, NULL, 'password', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757934125056, '比例(2:10)', 1993858757745381376, 0, NULL, 'proportion', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757938319360, '比例(2:10 或 2:5:6)', 1993858757745381376, 0, NULL, 'proportion_multi', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757980262400, '默认Like查询', 1993858757942513664, 0, NULL, 'default', 'code_gen_condition_fuzzy_query_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858757980262401, '左Like查询', 1993858757942513664, 0, NULL, 'left', 'code_gen_condition_fuzzy_query_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (1993858758009622528, '右Like查询', 1993858757942513664, 0, NULL, 'right', 'code_gen_condition_fuzzy_query_type', '1', '1');
INSERT INTO `jb_dictionary` VALUES (2009449215228366848, 'siargo_小数点保留_2位', 1993858757745381376, 0, 33, 'siargo_tofixed_2', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (2009453093114073088, 'siargo_小数点保留_3位', 1993858757745381376, 0, 34, 'siargo_tofixed_3', 'code_gen_form_data_rule', '1', '1');

-- ----------------------------
-- Table structure for jb_dictionary_type
-- ----------------------------
DROP TABLE IF EXISTS `jb_dictionary_type`;
CREATE TABLE `jb_dictionary_type`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mode_level` int NULL DEFAULT NULL COMMENT '模式层级',
  `type_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识KEY',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '是否启用',
  `is_build_in` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否内置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_dictionary_type
-- ----------------------------
INSERT INTO `jb_dictionary_type` VALUES (1993858755283324928, '选项_Boolean值', 1, 'options_boolean', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755451097088, '选项_enable', 1, 'options_enable', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755497234432, '性别', 1, 'sex', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755589509120, '机构类型', 1, 'dept_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755690172416, '岗位类型', 1, 'post_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755769864192, '系统通知_类型', 1, 'sys_notice_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755853750272, '系统通知_优先级', 1, 'sys_notice_priority_level', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858755933442048, '系统通知_接收人类型', 1, 'sys_notice_receiver_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756029911040, '待办事项_优先级', 1, 'todo_priority_level', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756097019904, '待办事项_状态', 1, 'todo_state', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756197683200, '待办事项_类型', 1, 'todo_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756264792064, '时间查询列名', 1, 'todo_datetime_column', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756336095232, '七牛账号_类型', 1, 'qiniu_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756428369920, '七牛账号_地区', 1, 'qiniu_region', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756478701569, '主键生成策略', 1, 'code_gen_idgenmode', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756625502208, '表格列显示格式化', 1, 'code_gen_table_col_format', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756851994624, '组件数据类型', 1, 'code_gen_data_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858756935880704, 'codeGen_属性翻译类型', 1, 'code_gen_translate_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858757053321216, 'codeGen_查询条件组件类型', 1, 'code_gen_condition_ui_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858757279813632, 'codeGen_可编辑表格组件类型', 1, 'code_gen_table_ui_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858757485334528, 'codeGen_表单组件类型', 1, 'code_gen_form_ui_type', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858757745381376, '表单校验规则', 1, 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary_type` VALUES (1993858757942513664, 'codeGen_模糊查询类型', 1, 'code_gen_condition_fuzzy_query_type', '1', '1');

-- ----------------------------
-- Table structure for jb_global_config
-- ----------------------------
DROP TABLE IF EXISTS `jb_global_config`;
CREATE TABLE `jb_global_config`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置KEY',
  `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置项值',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '创建用户ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `value_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '值类型',
  `type_key` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型key',
  `type_id` bigint NULL DEFAULT NULL COMMENT '类型ID',
  `built_in` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '内置参数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_global_config
-- ----------------------------
INSERT INTO `jb_global_config` VALUES (1992880779580907520, 'JBOLT_CHECK_JWT_API_USER_EFFECTIVE_ENABLE', 'false', '2025-11-24 16:59:38', NULL, '2025-11-27 09:45:43', 1992880779681570816, '是否启用JWT-APIUser-有效性检测机制', 'boolean', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880779639627776, 'JBOLT_CHECK_JWT_API_USER_EFFECTIVE_TOKEN', 'jbolt', '2025-11-24 16:59:38', NULL, '2025-11-27 09:45:43', 1992880779681570816, 'JWT-APIUser-有效性检测因子', 'string', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880780625289216, 'JBOLT_ACTION_REPORT_WRITER', 'sysout', '2025-11-24 16:59:39', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统 Action Report输出方式', 'string', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880780675620864, 'JBOLT_AUTO_CACHE_LOG', 'false', '2025-11-24 16:59:39', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统自动缓存Debug日志', 'boolean', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880780730146816, 'ASSETS_VERSION', '20251124165938715', '2025-11-24 16:59:39', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统静态资源版本号', 'string', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880780805644288, 'JBOLT_ACTION_REPORT_LINE_NUMBER_ENABLE', 'false', '2025-11-24 16:59:39', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统 Action Report输出准确行号', 'boolean', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880864842719232, 'JBOLT_ADMIN_LOGIN_HTML_FILE', 'login.html', '2025-11-24 16:59:59', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '登录页文件配置', 'string', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880864943382528, 'SYSTEM_NAME', '矽翔质管部管理系统', '2025-11-24 16:59:59', 1992880779681570816, '2026-01-04 13:10:15', 2002984611549220864, '系统名称', 'string', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880865035657216, 'JBOLT_LOGIN_BGIMG_BLUR', 'true', '2025-11-24 16:59:59', 1992880779681570816, '2025-11-28 14:01:35', 1992880779681570816, '系统登录页面背景图是否启用模糊风格', 'boolean', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880865123737600, 'JBOLT_LOGIN_BGIMG', 'assets/css/img/login_bg.jpg', '2025-11-24 16:59:59', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统登录页背景图', 'string', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880865203429376, 'JBOLT_DOMAIN_PUT_ON_RECORD_INFO', NULL, '2025-11-24 16:59:59', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统备案号', 'string', 'sys_put_on_record', 1993858739797954560, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880865270538240, 'SYSTEM_COPYRIGHT_COMPANY', '©hanzj', '2025-11-24 16:59:59', 1992880779681570816, '2025-11-27 09:47:16', 1992880779681570816, '系统版权所有人', 'string', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880865346035712, 'JBOLT_LOGIN_NEST', 'true', '2025-11-24 16:59:59', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统登录页是否开启线条特效', 'boolean', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880865480253440, 'JBOLT_LOGIN_CAPTURE_TYPE', 'number_calc', '2025-11-24 16:59:59', 1992880779681570816, '2025-12-24 16:41:58', 1992880779681570816, '系统登录页验证码类型', 'string', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880949651546112, 'JBOLT_ADMIN_USER_KEEPLOGIN_SECONDS', '604800', '2025-11-24 17:00:19', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '后台用户保持登录时Cookie时长(秒)', 'int', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880949739626496, 'JBOLT_ADMIN_USER_NOT_KEEPLOGIN_SECONDS', '28800', '2025-11-24 17:00:19', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '后台用户不保持登录时Cookie时长(秒)', 'int', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880949810929664, 'JBOLT_LOGIN_TERMINAL_ONLYONE', 'true', '2025-11-24 17:00:19', 1992880779681570816, '2025-12-24 13:46:43', 1992880779681570816, '同一账号不能多端登录', 'boolean', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954001039360, 'JBOLT_ADMIN_WITH_TABS', 'true', '2025-11-24 17:00:20', 1992880779681570816, '2025-12-12 16:49:53', 1992880779681570816, '系统Admin后台是否启用多选项卡', 'boolean', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954076536832, 'SYSTEM_ADMIN_H50', 'false', '2025-11-24 17:00:20', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统后台整体样式高度使用H50', 'boolean', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954177200128, 'JBOLT_AUTO_LOCKSCREEN_SECONDS', '1800', '2025-11-24 17:00:20', 1992880779681570816, '2025-12-24 13:53:23', 1992880779681570816, '用户多长时间(秒)无操作自动锁屏', 'int', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954240114688, 'JBOLT_TAB_KEEP_AFTER_RELOAD', 'true', '2025-11-24 17:00:20', 1992880779681570816, '2025-12-24 13:51:03', 1992880779681570816, '启用页面重载保持住原有选项卡', 'boolean', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954340777984, 'SYSTEM_ADMIN_LEFT_NAV_WIDTH', '220', '2025-11-24 17:00:20', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统后台左侧导航宽度', 'int', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954412081152, 'JBOLT_PASSWORD_CHANGE_NOTICE_DAYS', '0', '2025-11-24 17:00:20', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '多久没改密码就提醒', 'int', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954508550144, 'SYSTEM_ADMIN_GLOBAL_SEARCH_SHOW', 'false', '2025-11-24 17:00:20', 1992880779681570816, '2025-12-24 13:51:44', 1992880779681570816, '系统后台全局搜索输入框是否启用', 'boolean', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954563076096, 'JBOLT_ADMIN_STYLE', 'default', '2025-11-24 17:00:20', 1992880779681570816, '2025-11-27 09:45:42', 1992880779681570816, '系统Admin后台样式', 'string', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1992880954684710912, 'SYSTEM_ADMIN_NAV_MENU_DEFAULT_ICON', NULL, '2025-11-24 17:00:20', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统后台导航菜单默认图标', 'string', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858728305561600, 'JBOLT_DICTIONARY_DELETE_ENABLE', 'false', '2025-11-27 09:45:40', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '是否启用字典删除功能', 'boolean', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858740183830528, 'WECHAT_MP_SERVER_DOMAIN', 'http://localhost/wx/msg', '2025-11-27 09:45:42', 1992880779681570816, '2025-11-27 09:45:42', 1992880779681570816, '微信公众号_服务器配置_根URL', 'string', 'wechat_dev', 1993858740108333056, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858740188024832, 'WECHAT_WXA_SERVER_DOMAIN', 'http://localhost/wxa/msg', '2025-11-27 09:45:42', 1992880779681570816, '2025-11-27 09:45:42', 1992880779681570816, '微信小程序_客服消息推送配置_根URL', 'string', 'wechat_dev', 1993858740108333056, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858740229967872, 'WECHAT_ASSETS_SERVER_DOMAIN', 'http://localhost', '2025-11-27 09:45:42', 1992880779681570816, '2025-11-27 09:45:42', 1992880779681570816, '微信_静态资源_根URL', 'string', 'wechat_dev', 1993858740108333056, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858740343214080, 'SYSTEM_ADMIN_LOGO', 'assets/img/logoo.png', '2025-11-27 09:45:42', 1992880779681570816, '2025-11-28 14:03:10', 1992880779681570816, '系统后台主页LOGO', 'string', 'admin_ui', 1993858740037029888, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858740410322944, 'SYSTEM_COPYRIGHT_LINK', 'http://siargo.com.cn', '2025-11-27 09:45:42', 1992880779681570816, '2025-11-27 09:48:20', 1992880779681570816, '系统版权所有人的网址链接', 'string', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858740519374848, 'JBOLT_LOGIN_USE_CAPTURE', 'true', '2025-11-27 09:45:43', 1992880779681570816, '2025-12-24 16:41:52', 1992880779681570816, '系统登录页面是否启用验证码', 'boolean', 'admin_login', 1993858739932172288, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858741190463488, 'SYSTEM_DEPT_ENABLE', 'true', '2025-11-27 09:45:43', 1992880779681570816, '2025-11-27 09:48:34', 1992880779681570816, '启用部门', 'boolean', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858741211435008, 'SYSTEM_POST_ENABLE', 'true', '2025-11-27 09:45:43', 1992880779681570816, '2025-11-27 09:48:36', 1992880779681570816, '启用岗位', 'boolean', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858741236600832, 'SYSTEM_USER_EXTEND_ENABLE', 'true', '2025-11-27 09:45:43', 1992880779681570816, '2025-11-28 14:04:10', 1992880779681570816, '启用用户信息扩展', 'boolean', 'sys_config', 1993858739441438720, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858741307904000, 'JBOLT_LOCKSYSTEM_AFTER_AUTOLOGIN', 'true', '2025-11-27 09:45:43', 1992880779681570816, '2025-12-24 13:52:26', 1992880779681570816, '启用自动登录后进入锁屏状态', 'boolean', 'sys_safe_config', 1993858739588239360, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858741375012864, 'JBOLT_DOMAIN_PUT_ON_RECORD_LINK', 'http://beian.miit.gov.cn/', '2025-11-27 09:45:43', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统备案号链接', 'string', 'sys_put_on_record', 1993858739797954560, '1', NULL);
INSERT INTO `jb_global_config` VALUES (1993858741404372992, 'JBOLT_DEPT_MGR_TYPE', 'tree_table', '2025-11-27 09:45:43', 1992880779681570816, '2025-11-27 09:45:43', 1992880779681570816, '系统部门管理方式', 'string', 'admin_ui', 1993858740037029888, '1', NULL);

-- ----------------------------
-- Table structure for jb_global_config_type
-- ----------------------------
DROP TABLE IF EXISTS `jb_global_config_type`;
CREATE TABLE `jb_global_config_type`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型名称',
  `sort_rank` int NULL DEFAULT NULL COMMENT '序号',
  `type_key` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型KEY',
  `built_in` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '内置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局参数类型分组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_global_config_type
-- ----------------------------
INSERT INTO `jb_global_config_type` VALUES (1993858739441438720, '系统基础配置', 1, 'sys_config', '1');
INSERT INTO `jb_global_config_type` VALUES (1993858739588239360, '系统安全配置', 2, 'sys_safe_config', '1');
INSERT INTO `jb_global_config_type` VALUES (1993858739797954560, '系统备案相关', 3, 'sys_put_on_record', '1');
INSERT INTO `jb_global_config_type` VALUES (1993858739932172288, '后台登录', 4, 'admin_login', '1');
INSERT INTO `jb_global_config_type` VALUES (1993858740037029888, '后台样式', 5, 'admin_ui', '1');
INSERT INTO `jb_global_config_type` VALUES (1993858740108333056, '微信开发', 6, 'wechat_dev', '1');

-- ----------------------------
-- Table structure for jb_hiprint_tpl
-- ----------------------------
DROP TABLE IF EXISTS `jb_hiprint_tpl`;
CREATE TABLE `jb_hiprint_tpl`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `sn` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板编码',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '更新人',
  `test_api_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试API地址',
  `test_json_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '测试JSON数据',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'hiprint模版' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_hiprint_tpl
-- ----------------------------

-- ----------------------------
-- Table structure for jb_jbolt_file
-- ----------------------------
DROP TABLE IF EXISTS `jb_jbolt_file`;
CREATE TABLE `jb_jbolt_file`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `local_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '保存物理地址',
  `local_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '本地可访问URL地址',
  `cdn_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外部CDN地址',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_type` int NULL DEFAULT NULL COMMENT '文件类型 图片 附件 视频 音频',
  `file_size` int NULL DEFAULT 0 COMMENT '文件大小',
  `file_suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后缀名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_jbolt_file
-- ----------------------------
INSERT INTO `jb_jbolt_file` VALUES (2001550099865387008, 'E:/eclipse-workspace/siargo/src/main/webapp/upload/user/avatar/20251218/c8e7ebc6659943388f2942e6fef13ff9.jpg', 'upload/user/avatar/20251218/c8e7ebc6659943388f2942e6fef13ff9.jpg', NULL, '2025-12-18 15:08:25', 2001546811778514944, '1.jpg', 1, 70081, 'jpg');

-- ----------------------------
-- Table structure for jb_login_log
-- ----------------------------
DROP TABLE IF EXISTS `jb_login_log`;
CREATE TABLE `jb_login_log`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `login_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `login_state` int NOT NULL COMMENT '登录状态',
  `is_browser` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否是浏览器访问',
  `browser_version` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器版本号',
  `browser_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `login_city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录城市',
  `login_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录位置详情',
  `login_city_code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录城市代码',
  `login_province` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录省份',
  `login_country` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录国家',
  `is_mobile` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否移动端',
  `os_platform_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台名称',
  `browser_engine_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器引擎名',
  `browser_engine_version` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器引擎版本',
  `login_address_latitude` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录地横坐标',
  `login_address_longitude` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录地纵坐标',
  `is_remote_login` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为异地异常登录',
  `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_login_log
-- ----------------------------
INSERT INTO `jb_login_log` VALUES (2003000908903288832, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-22 15:13:25', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 15:13:25');
INSERT INTO `jb_login_log` VALUES (2003013166962249728, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-22 16:02:08', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:02:08');
INSERT INTO `jb_login_log` VALUES (2003019201454215168, NULL, 'hanzijin', '192.168.3.53', '2025-12-22 16:26:07', 3, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:26:07');
INSERT INTO `jb_login_log` VALUES (2003019231279910912, 2001546811778514944, 'hanzijin', '192.168.3.53', '2025-12-22 16:26:14', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:26:14');
INSERT INTO `jb_login_log` VALUES (2003019583471423488, 2001546811778514944, 'hanzijin', '192.168.3.22', '2025-12-22 16:27:38', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:27:38');
INSERT INTO `jb_login_log` VALUES (2003019642795659264, NULL, 'siargosiargo', '192.168.3.33', '2025-12-22 16:27:52', 3, '1', '97.0.4692.98', 'Chrome', 'Android', '内网IP', '0内网IP', NULL, NULL, NULL, '1', 'Android', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:27:52');
INSERT INTO `jb_login_log` VALUES (2003019739101073408, NULL, 'siargosiargo', '192.168.3.33', '2025-12-22 16:28:15', 3, '1', '97.0.4692.98', 'Chrome', 'Android', '内网IP', '0内网IP', NULL, NULL, NULL, '1', 'Android', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:28:15');
INSERT INTO `jb_login_log` VALUES (2003019799532605440, NULL, 'siargo siargo', '192.168.3.33', '2025-12-22 16:28:29', 3, '1', '97.0.4692.98', 'Chrome', 'Android', '内网IP', '0内网IP', NULL, NULL, NULL, '1', 'Android', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:28:29');
INSERT INTO `jb_login_log` VALUES (2003020923660603392, 2001548007356481536, 'luoxiongfei', '192.168.3.33', '2025-12-22 16:32:57', 1, '1', '97.0.4692.98', 'Chrome', 'Android', '内网IP', '0内网IP', NULL, NULL, NULL, '1', 'Android', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:32:57');
INSERT INTO `jb_login_log` VALUES (2003021428281511936, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2025-12-22 16:34:57', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 16:34:57');
INSERT INTO `jb_login_log` VALUES (2003021555591221248, NULL, 'siargo', '192.168.3.12', '2025-12-22 16:35:28', 3, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2025-12-22 16:35:28');
INSERT INTO `jb_login_log` VALUES (2003021658867568640, NULL, 'houlling', '192.168.3.12', '2025-12-22 16:35:52', 3, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2025-12-22 16:35:52');
INSERT INTO `jb_login_log` VALUES (2003021697463554048, NULL, 'houlliang', '192.168.3.12', '2025-12-22 16:36:02', 3, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2025-12-22 16:36:02');
INSERT INTO `jb_login_log` VALUES (2003021757433712640, NULL, 'houliang', '192.168.3.12', '2025-12-22 16:36:16', 3, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2025-12-22 16:36:16');
INSERT INTO `jb_login_log` VALUES (2003021854561210368, 2001548276093927424, 'houliang', '192.168.3.12', '2025-12-22 16:36:39', 1, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2025-12-22 16:36:39');
INSERT INTO `jb_login_log` VALUES (2003065548056236032, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-22 19:30:16', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 19:30:16');
INSERT INTO `jb_login_log` VALUES (2003067825420701696, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-22 19:39:19', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 19:39:19');
INSERT INTO `jb_login_log` VALUES (2003072054789083136, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-22 19:56:08', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 19:56:08');
INSERT INTO `jb_login_log` VALUES (2003079333756211200, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-22 20:25:03', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-22 20:25:03');
INSERT INTO `jb_login_log` VALUES (2003268327429312512, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-23 08:56:03', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-23 08:56:03');
INSERT INTO `jb_login_log` VALUES (2003669979583942656, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-24 11:32:04', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 11:32:04');
INSERT INTO `jb_login_log` VALUES (2003703678169976832, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-24 13:45:59', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 13:45:59');
INSERT INTO `jb_login_log` VALUES (2003704104453869568, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-24 13:47:40', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 13:47:40');
INSERT INTO `jb_login_log` VALUES (2003704504024240128, NULL, 'amdin', '127.0.0.1', '2025-12-24 13:49:15', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 13:49:15');
INSERT INTO `jb_login_log` VALUES (2003704560987082752, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-24 13:49:29', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 13:49:29');
INSERT INTO `jb_login_log` VALUES (2003748099037007872, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-24 16:42:29', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 16:42:29');
INSERT INTO `jb_login_log` VALUES (2003752792542875648, 2001546811778514944, 'hanzijin', '127.0.0.1', '2025-12-24 17:01:08', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 17:01:08');
INSERT INTO `jb_login_log` VALUES (2003756506779422720, 1992880779681570816, 'admin', '192.168.77.120', '2025-12-24 17:15:54', 1, '1', '142.0.0.0', 'Chrome', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-24 17:15:54');
INSERT INTO `jb_login_log` VALUES (2003992297019478016, 2001546811778514944, 'hanzijin', '127.0.0.1', '2025-12-25 08:52:51', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 08:52:51');
INSERT INTO `jb_login_log` VALUES (2003992370730176512, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-25 08:53:08', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 08:53:08');
INSERT INTO `jb_login_log` VALUES (2003995462347460608, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-25 09:05:25', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 09:05:25');
INSERT INTO `jb_login_log` VALUES (2003996316177731584, 2003992595003805696, 'wuyong', '192.168.9.11', '2025-12-25 09:08:49', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 09:08:49');
INSERT INTO `jb_login_log` VALUES (2004106698095529984, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2025-12-25 16:27:26', 1, '1', '7.0.20.1781', 'WindowsWechat', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 16:27:26');
INSERT INTO `jb_login_log` VALUES (2004140245640482816, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-25 18:40:44', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 18:40:44');
INSERT INTO `jb_login_log` VALUES (2004140304885026816, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-25 18:40:58', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-25 18:40:58');
INSERT INTO `jb_login_log` VALUES (2004364575024549888, 2001546811778514944, 'hanzijin', '192.168.3.31', '2025-12-26 09:32:09', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-26 09:32:09');
INSERT INTO `jb_login_log` VALUES (2004364634088738816, NULL, 'admin', '192.168.3.31', '2025-12-26 09:32:23', 3, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-26 09:32:23');
INSERT INTO `jb_login_log` VALUES (2004364670746955776, 1992880779681570816, 'admin', '192.168.3.31', '2025-12-26 09:32:31', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-26 09:32:31');
INSERT INTO `jb_login_log` VALUES (2004368334412779520, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-26 09:47:05', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-26 09:47:05');
INSERT INTO `jb_login_log` VALUES (2004398229129990144, 2003992595003805696, 'wuyong', '192.168.9.11', '2025-12-26 11:45:52', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-26 11:45:52');
INSERT INTO `jb_login_log` VALUES (2004433062103289856, 2003992595003805696, 'wuyong', '192.168.9.11', '2025-12-26 14:04:17', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-26 14:04:17');
INSERT INTO `jb_login_log` VALUES (2004717246101262336, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-27 08:53:32', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-27 08:53:32');
INSERT INTO `jb_login_log` VALUES (2004717614419873792, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-27 08:55:00', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-27 08:55:00');
INSERT INTO `jb_login_log` VALUES (2005443063311699968, 2002984611549220864, 'siargo', '127.0.0.1', '2025-12-29 08:57:40', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-29 08:57:40');
INSERT INTO `jb_login_log` VALUES (2006193533055602688, NULL, 'aadmin', '127.0.0.1', '2025-12-31 10:39:46', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-31 10:39:46');
INSERT INTO `jb_login_log` VALUES (2006193565951528960, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-31 10:39:54', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-31 10:39:54');
INSERT INTO `jb_login_log` VALUES (2006197252396617728, 1992880779681570816, 'admin', '127.0.0.1', '2025-12-31 10:54:33', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2025-12-31 10:54:33');
INSERT INTO `jb_login_log` VALUES (2006986262501036032, 1992880779681570816, 'admin', '127.0.0.1', '2026-01-02 15:09:48', 1, '1', '143.0.0.0', 'Chrome', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-02 15:09:48');
INSERT INTO `jb_login_log` VALUES (2007012075078324224, 1992880779681570816, 'admin', '127.0.0.1', '2026-01-02 16:52:22', 1, '1', '143.0.0.0', 'Chrome', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-02 16:52:22');
INSERT INTO `jb_login_log` VALUES (2007032782998900736, 1992880779681570816, 'admin', '127.0.0.1', '2026-01-02 18:14:39', 1, '1', '143.0.0.0', 'Chrome', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-02 18:14:39');
INSERT INTO `jb_login_log` VALUES (2007044101265330176, 1992880779681570816, 'admin', '127.0.0.1', '2026-01-02 18:59:37', 1, '1', '143.0.0.0', 'Chrome', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-02 18:59:37');
INSERT INTO `jb_login_log` VALUES (2007059625781137408, 1992880779681570816, 'admin', '127.0.0.1', '2026-01-02 20:01:19', 1, '1', '138.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-02 20:01:19');
INSERT INTO `jb_login_log` VALUES (2007672528036548608, 2002984611549220864, 'siargo', '127.0.0.1', '2026-01-04 12:36:46', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 12:36:46');
INSERT INTO `jb_login_log` VALUES (2007681228180672512, 2001547415494049792, 'fengying', '192.168.77.31', '2026-01-04 13:11:20', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 13:11:20');
INSERT INTO `jb_login_log` VALUES (2007685009941319680, 2002984611549220864, 'siargo', '192.168.3.30', '2026-01-04 13:26:22', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 13:26:22');
INSERT INTO `jb_login_log` VALUES (2007689553400156160, 2001548276093927424, 'houliang', '192.168.3.14', '2026-01-04 13:44:25', 1, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2026-01-04 13:44:25');
INSERT INTO `jb_login_log` VALUES (2007693541352787968, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-04 14:00:16', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 14:00:16');
INSERT INTO `jb_login_log` VALUES (2007695845200089088, 2002984611549220864, 'siargo', '127.0.0.1', '2026-01-04 14:09:25', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 14:09:25');
INSERT INTO `jb_login_log` VALUES (2007696729271291904, 2001546811778514944, 'hanzijin', '192.168.77.254', '2026-01-04 14:12:56', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 14:12:56');
INSERT INTO `jb_login_log` VALUES (2007723068275675136, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-04 15:57:36', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 15:57:36');
INSERT INTO `jb_login_log` VALUES (2007732877649629184, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-04 16:36:35', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 16:36:35');
INSERT INTO `jb_login_log` VALUES (2007742126794592256, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-04 17:13:20', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 17:13:20');
INSERT INTO `jb_login_log` VALUES (2007742166502068224, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-04 17:13:29', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-04 17:13:29');
INSERT INTO `jb_login_log` VALUES (2007990379054223360, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-05 09:39:48', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 09:39:48');
INSERT INTO `jb_login_log` VALUES (2008000063320477696, NULL, 'houliang', '192.168.3.11', '2026-01-05 10:18:17', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 10:18:17');
INSERT INTO `jb_login_log` VALUES (2008000104156221440, 2001548276093927424, 'houliang', '192.168.3.11', '2026-01-05 10:18:26', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 10:18:26');
INSERT INTO `jb_login_log` VALUES (2008016113185443840, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-05 11:22:03', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 11:22:03');
INSERT INTO `jb_login_log` VALUES (2008018295041413120, 2001548276093927424, 'houliang', '192.168.3.11', '2026-01-05 11:30:43', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 11:30:43');
INSERT INTO `jb_login_log` VALUES (2008051216653602816, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-05 13:41:32', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 13:41:32');
INSERT INTO `jb_login_log` VALUES (2008051272483983360, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-05 13:41:46', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 13:41:46');
INSERT INTO `jb_login_log` VALUES (2008069122275790848, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-05 14:52:42', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 14:52:42');
INSERT INTO `jb_login_log` VALUES (2008077020171194368, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-05 15:24:05', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 15:24:05');
INSERT INTO `jb_login_log` VALUES (2008078086803673088, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-05 15:28:19', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 15:28:19');
INSERT INTO `jb_login_log` VALUES (2008080174870810624, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-05 15:36:37', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 15:36:37');
INSERT INTO `jb_login_log` VALUES (2008099798752546816, 2001546811778514944, 'hanzijin', '192.168.3.65', '2026-01-05 16:54:35', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 16:54:35');
INSERT INTO `jb_login_log` VALUES (2008102994443096064, 2001546811778514944, 'hanzijin', '192.168.77.36', '2026-01-05 17:07:17', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 17:07:17');
INSERT INTO `jb_login_log` VALUES (2008103795391582208, NULL, 'siargo', '192.168.77.36', '2026-01-05 17:10:28', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 17:10:28');
INSERT INTO `jb_login_log` VALUES (2008103819391389696, 2002984611549220864, 'siargo', '192.168.77.36', '2026-01-05 17:10:34', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 17:10:34');
INSERT INTO `jb_login_log` VALUES (2008117718492762112, NULL, 'houl', '192.168.3.65', '2026-01-05 18:05:48', 2, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 18:05:48');
INSERT INTO `jb_login_log` VALUES (2008117764781101056, NULL, 'houl', '192.168.3.65', '2026-01-05 18:05:59', 3, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 18:05:59');
INSERT INTO `jb_login_log` VALUES (2008117831231459328, NULL, 'houlliang', '192.168.3.65', '2026-01-05 18:06:15', 3, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 18:06:15');
INSERT INTO `jb_login_log` VALUES (2008118044822196224, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-05 18:07:06', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-05 18:07:06');
INSERT INTO `jb_login_log` VALUES (2008348055118401536, 2002984611549220864, 'siargo', '127.0.0.1', '2026-01-06 09:21:04', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 09:21:04');
INSERT INTO `jb_login_log` VALUES (2008353040111620096, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-06 09:40:53', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 09:40:53');
INSERT INTO `jb_login_log` VALUES (2008363679416700928, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-06 10:23:09', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:23:09');
INSERT INTO `jb_login_log` VALUES (2008363892231491584, 2001546811778514944, 'hanzijin', '192.168.3.11', '2026-01-06 10:24:00', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:24:00');
INSERT INTO `jb_login_log` VALUES (2008365481675575296, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-06 10:30:19', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:30:19');
INSERT INTO `jb_login_log` VALUES (2008365668045279232, 2001546811778514944, 'hanzijin', '192.168.77.73', '2026-01-06 10:31:04', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:31:04');
INSERT INTO `jb_login_log` VALUES (2008366587889700864, 2001547605315665920, 'wuxiaoyu', '192.168.77.73', '2026-01-06 10:34:43', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:34:43');
INSERT INTO `jb_login_log` VALUES (2008368827970998272, NULL, 'hanzijin', '192.168.77.73', '2026-01-06 10:43:37', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:43:37');
INSERT INTO `jb_login_log` VALUES (2008368861915500544, 2001546811778514944, 'hanzijin', '192.168.77.73', '2026-01-06 10:43:45', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:43:45');
INSERT INTO `jb_login_log` VALUES (2008372169698824192, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-06 10:56:54', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 10:56:54');
INSERT INTO `jb_login_log` VALUES (2008405314347323392, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-06 13:08:36', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 13:08:36');
INSERT INTO `jb_login_log` VALUES (2008405435868893184, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-06 13:09:05', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 13:09:05');
INSERT INTO `jb_login_log` VALUES (2008511000243269632, 2001546811778514944, 'hanzijin', '127.0.0.1', '2026-01-06 20:08:33', 1, '1', '143.0.0.0', 'Chrome', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-06 20:08:33');
INSERT INTO `jb_login_log` VALUES (2008703784429932544, NULL, 'houliang', '192.168.3.65', '2026-01-07 08:54:37', 2, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 08:54:37');
INSERT INTO `jb_login_log` VALUES (2008703828835028992, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-07 08:54:47', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 08:54:47');
INSERT INTO `jb_login_log` VALUES (2008703967221895168, NULL, 'wuxaioyu', '192.168.77.81', '2026-01-07 08:55:20', 3, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 08:55:20');
INSERT INTO `jb_login_log` VALUES (2008704057638506496, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-07 08:55:42', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 08:55:42');
INSERT INTO `jb_login_log` VALUES (2008707543314845696, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-07 09:09:33', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 09:09:33');
INSERT INTO `jb_login_log` VALUES (2008718331198951424, 2001546811778514944, 'hanzijin', '192.168.3.16', '2026-01-07 09:52:25', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 09:52:25');
INSERT INTO `jb_login_log` VALUES (2008722542351470592, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-07 10:09:09', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 10:09:09');
INSERT INTO `jb_login_log` VALUES (2008728428281188352, 2002984611549220864, 'siargo', '192.168.3.11', '2026-01-07 10:32:32', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 10:32:32');
INSERT INTO `jb_login_log` VALUES (2008728937520025600, 2001546811778514944, 'hanzijin', '192.168.77.16', '2026-01-07 10:34:34', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 10:34:34');
INSERT INTO `jb_login_log` VALUES (2008733016954621952, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-07 10:50:46', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 10:50:46');
INSERT INTO `jb_login_log` VALUES (2008743583341727744, 2001546811778514944, 'hanzijin', '192.168.77.132', '2026-01-07 11:32:46', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 11:32:46');
INSERT INTO `jb_login_log` VALUES (2008745301076987904, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-07 11:39:35', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 11:39:35');
INSERT INTO `jb_login_log` VALUES (2008792276036079616, NULL, 'wuxiaoyu', '192.168.3.11', '2026-01-07 14:46:15', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 14:46:15');
INSERT INTO `jb_login_log` VALUES (2008792307501748224, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-07 14:46:22', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 14:46:22');
INSERT INTO `jb_login_log` VALUES (2008801649911255040, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-07 15:23:30', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 15:23:30');
INSERT INTO `jb_login_log` VALUES (2008822486789902336, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-07 16:46:18', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 16:46:18');
INSERT INTO `jb_login_log` VALUES (2008824520431751168, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-07 16:54:22', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 16:54:22');
INSERT INTO `jb_login_log` VALUES (2008828531373363200, NULL, 'luoxiongfei', '192.168.3.30', '2026-01-07 17:10:19', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 17:10:19');
INSERT INTO `jb_login_log` VALUES (2008828591901364224, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-07 17:10:33', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 17:10:33');
INSERT INTO `jb_login_log` VALUES (2008830557473853440, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-07 17:18:22', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-07 17:18:22');
INSERT INTO `jb_login_log` VALUES (2009069765631266816, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-08 09:08:53', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 09:08:53');
INSERT INTO `jb_login_log` VALUES (2009088081175367680, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-08 10:21:40', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 10:21:40');
INSERT INTO `jb_login_log` VALUES (2009089300908331008, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-08 10:26:31', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 10:26:31');
INSERT INTO `jb_login_log` VALUES (2009093003245637632, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-08 10:41:14', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 10:41:14');
INSERT INTO `jb_login_log` VALUES (2009105871051542528, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-08 11:32:22', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 11:32:22');
INSERT INTO `jb_login_log` VALUES (2009133106840064000, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-08 13:20:35', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 13:20:35');
INSERT INTO `jb_login_log` VALUES (2009142124966957056, 2002984611549220864, 'siargo', '127.0.0.1', '2026-01-08 13:56:25', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 13:56:25');
INSERT INTO `jb_login_log` VALUES (2009143769020878848, NULL, 'admin', '127.0.0.1', '2026-01-08 14:02:57', 3, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 14:02:57');
INSERT INTO `jb_login_log` VALUES (2009143828265422848, 2002984611549220864, 'siargo', '127.0.0.1', '2026-01-08 14:03:11', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 14:03:11');
INSERT INTO `jb_login_log` VALUES (2009149500126253056, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-08 14:25:44', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 14:25:44');
INSERT INTO `jb_login_log` VALUES (2009153022943154176, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-08 14:39:44', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 14:39:44');
INSERT INTO `jb_login_log` VALUES (2009155385980473344, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-08 14:49:07', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 14:49:07');
INSERT INTO `jb_login_log` VALUES (2009184969698430976, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-08 16:46:40', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 16:46:40');
INSERT INTO `jb_login_log` VALUES (2009189640366313472, NULL, 'hanzijin', '192.168.3.31', '2026-01-08 17:05:14', 3, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 17:05:14');
INSERT INTO `jb_login_log` VALUES (2009189666370998272, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-08 17:05:20', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-08 17:05:20');
INSERT INTO `jb_login_log` VALUES (2009430774921416704, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-09 09:03:25', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 09:03:25');
INSERT INTO `jb_login_log` VALUES (2009435860003311616, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-09 09:23:37', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 09:23:37');
INSERT INTO `jb_login_log` VALUES (2009447792571699200, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-09 10:11:02', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 10:11:02');
INSERT INTO `jb_login_log` VALUES (2009452461155012608, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-09 10:29:35', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 10:29:35');
INSERT INTO `jb_login_log` VALUES (2009453524222332928, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-09 10:33:49', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 10:33:49');
INSERT INTO `jb_login_log` VALUES (2009468033070649344, 2001546811778514944, 'hanzijin', '192.168.77.46', '2026-01-09 11:31:28', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 11:31:28');
INSERT INTO `jb_login_log` VALUES (2009497606508367872, NULL, 'wuxiaoyu', '192.168.77.81', '2026-01-09 13:28:59', 2, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 13:28:59');
INSERT INTO `jb_login_log` VALUES (2009505059476197376, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-09 13:58:36', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 13:58:36');
INSERT INTO `jb_login_log` VALUES (2009511314936680448, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-09 14:23:27', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 14:23:27');
INSERT INTO `jb_login_log` VALUES (2009549667937669120, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-09 16:55:51', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 16:55:51');
INSERT INTO `jb_login_log` VALUES (2009549882698616832, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-09 16:56:42', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 16:56:42');
INSERT INTO `jb_login_log` VALUES (2009552043566288896, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-09 17:05:18', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-09 17:05:18');
INSERT INTO `jb_login_log` VALUES (2009802131416928256, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-10 09:39:03', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-10 09:39:03');
INSERT INTO `jb_login_log` VALUES (2010535695519436800, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-12 10:13:58', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 10:13:58');
INSERT INTO `jb_login_log` VALUES (2010536224144347136, NULL, 'wuxiaoyu', '192.168.3.11', '2026-01-12 10:16:04', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 10:16:04');
INSERT INTO `jb_login_log` VALUES (2010536271200243712, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-12 10:16:16', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 10:16:16');
INSERT INTO `jb_login_log` VALUES (2010540090684461056, 2001547415494049792, 'fengying', '192.168.77.103', '2026-01-12 10:31:26', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 10:31:26');
INSERT INTO `jb_login_log` VALUES (2010583041498271744, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-12 13:22:07', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 13:22:07');
INSERT INTO `jb_login_log` VALUES (2010598028828594176, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-12 14:21:40', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 14:21:40');
INSERT INTO `jb_login_log` VALUES (2010619611899088896, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-12 15:47:26', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 15:47:26');
INSERT INTO `jb_login_log` VALUES (2010624855123021824, 2001546811778514944, 'hanzijin', '192.168.77.98', '2026-01-12 16:08:16', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 16:08:16');
INSERT INTO `jb_login_log` VALUES (2010633403785662464, NULL, 'wuxiaoyu', '192.168.3.11', '2026-01-12 16:42:14', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 16:42:14');
INSERT INTO `jb_login_log` VALUES (2010633435947585536, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-12 16:42:22', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 16:42:22');
INSERT INTO `jb_login_log` VALUES (2010642951267274752, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-12 17:20:10', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 17:20:10');
INSERT INTO `jb_login_log` VALUES (2010643426460946432, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-12 17:22:03', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-12 17:22:03');
INSERT INTO `jb_login_log` VALUES (2010881620683116544, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-13 09:08:33', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 09:08:33');
INSERT INTO `jb_login_log` VALUES (2010883681319505920, 2009493497545871360, 'admin', '127.0.0.1', '2026-01-13 09:16:45', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 09:16:45');
INSERT INTO `jb_login_log` VALUES (2010888432350253056, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-13 09:35:37', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 09:35:37');
INSERT INTO `jb_login_log` VALUES (2010904726017527808, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-13 10:40:22', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 10:40:22');
INSERT INTO `jb_login_log` VALUES (2010911170515292160, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-13 11:05:59', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 11:05:59');
INSERT INTO `jb_login_log` VALUES (2010918580676055040, 2001546811778514944, 'hanzijin', '192.168.77.78', '2026-01-13 11:35:25', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 11:35:25');
INSERT INTO `jb_login_log` VALUES (2010951887207911424, NULL, 'wuxiaoyu', '192.168.3.11', '2026-01-13 13:47:46', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 13:47:46');
INSERT INTO `jb_login_log` VALUES (2010951940802727936, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-13 13:47:59', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 13:47:59');
INSERT INTO `jb_login_log` VALUES (2010955222564589568, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-01-13 14:01:01', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-13 14:01:01');
INSERT INTO `jb_login_log` VALUES (2011243514623283200, 2009493497545871360, 'admin', '127.0.0.1', '2026-01-14 09:06:36', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 09:06:36');
INSERT INTO `jb_login_log` VALUES (2011246872100720640, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-14 09:19:56', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 09:19:56');
INSERT INTO `jb_login_log` VALUES (2011251255878209536, 2001546811778514944, 'hanzijin', '192.168.77.44', '2026-01-14 09:37:21', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 09:37:21');
INSERT INTO `jb_login_log` VALUES (2011259497127727104, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-14 10:10:06', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 10:10:06');
INSERT INTO `jb_login_log` VALUES (2011262838637449216, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-14 10:23:23', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 10:23:23');
INSERT INTO `jb_login_log` VALUES (2011271272229359616, NULL, 'wuxiaoyu', '192.168.77.81', '2026-01-14 10:56:54', 2, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 10:56:54');
INSERT INTO `jb_login_log` VALUES (2011271310552715264, NULL, 'wuxiaoyu', '192.168.77.81', '2026-01-14 10:57:03', 2, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 10:57:03');
INSERT INTO `jb_login_log` VALUES (2011271345797451776, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-14 10:57:11', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 10:57:11');
INSERT INTO `jb_login_log` VALUES (2011308502054457344, 2001546811778514944, 'HANZIJIN', '192.168.3.60', '2026-01-14 13:24:50', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 13:24:50');
INSERT INTO `jb_login_log` VALUES (2011338925472075776, NULL, 'wuxiaoyu', '192.168.3.11', '2026-01-14 15:25:43', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 15:25:43');
INSERT INTO `jb_login_log` VALUES (2011338949929062400, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-14 15:25:49', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 15:25:49');
INSERT INTO `jb_login_log` VALUES (2011356670989291520, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-14 16:36:14', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 16:36:14');
INSERT INTO `jb_login_log` VALUES (2011366430853681152, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-14 17:15:01', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 17:15:01');
INSERT INTO `jb_login_log` VALUES (2011366457122607104, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-14 17:15:07', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 17:15:07');
INSERT INTO `jb_login_log` VALUES (2011367770770231296, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-14 17:20:21', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 17:20:21');
INSERT INTO `jb_login_log` VALUES (2011368973457543168, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-14 17:25:07', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 17:25:07');
INSERT INTO `jb_login_log` VALUES (2011384918632681472, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-14 18:28:29', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-14 18:28:29');
INSERT INTO `jb_login_log` VALUES (2011614862780518400, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-15 09:42:12', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 09:42:12');
INSERT INTO `jb_login_log` VALUES (2011615055877885952, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-15 09:42:58', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 09:42:58');
INSERT INTO `jb_login_log` VALUES (2011615557013327872, 2001546811778514944, 'hanzijin', '192.168.77.106', '2026-01-15 09:44:57', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 09:44:57');
INSERT INTO `jb_login_log` VALUES (2011619419052953600, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-15 10:00:18', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 10:00:18');
INSERT INTO `jb_login_log` VALUES (2011620362054127616, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-15 10:04:03', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 10:04:03');
INSERT INTO `jb_login_log` VALUES (2011625745359425536, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-15 10:25:27', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 10:25:27');
INSERT INTO `jb_login_log` VALUES (2011703916863803392, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-15 15:36:04', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 15:36:04');
INSERT INTO `jb_login_log` VALUES (2011726542537609216, NULL, 'luoxongfei', '192.168.3.30', '2026-01-15 17:05:58', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 17:05:58');
INSERT INTO `jb_login_log` VALUES (2011726600666468352, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-15 17:06:12', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 17:06:12');
INSERT INTO `jb_login_log` VALUES (2011727505138765824, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-15 17:09:48', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-15 17:09:48');
INSERT INTO `jb_login_log` VALUES (2011969580673388544, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-16 09:11:43', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-16 09:11:43');
INSERT INTO `jb_login_log` VALUES (2011971572296699904, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-16 09:19:38', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-16 09:19:38');
INSERT INTO `jb_login_log` VALUES (2012046292622888960, NULL, 'luoxiongfei', '192.168.3.30', '2026-01-16 14:16:33', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-16 14:16:33');
INSERT INTO `jb_login_log` VALUES (2012046344284131328, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-16 14:16:45', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-16 14:16:45');
INSERT INTO `jb_login_log` VALUES (2012078510338592768, 2001546811778514944, 'hanzijin', '192.168.77.106', '2026-01-16 16:24:34', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-16 16:24:34');
INSERT INTO `jb_login_log` VALUES (2013070799324172288, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-19 10:07:34', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-19 10:07:34');
INSERT INTO `jb_login_log` VALUES (2013080343496478720, 2001547415494049792, 'fengying', '192.168.77.78', '2026-01-19 10:45:30', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-19 10:45:30');
INSERT INTO `jb_login_log` VALUES (2013083571051155456, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-19 10:58:19', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-19 10:58:19');
INSERT INTO `jb_login_log` VALUES (2013083879823233024, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-19 10:59:33', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-19 10:59:33');
INSERT INTO `jb_login_log` VALUES (2013138545961455616, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-19 14:36:46', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-19 14:36:46');
INSERT INTO `jb_login_log` VALUES (2013167609652826112, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-19 16:32:16', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-19 16:32:16');
INSERT INTO `jb_login_log` VALUES (2013427373108482048, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-20 09:44:28', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 09:44:28');
INSERT INTO `jb_login_log` VALUES (2013428920185901056, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-20 09:50:37', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 09:50:37');
INSERT INTO `jb_login_log` VALUES (2013429903448199168, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-20 09:54:31', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 09:54:31');
INSERT INTO `jb_login_log` VALUES (2013478469545873408, 2001546811778514944, 'hanzijin', '192.168.77.83', '2026-01-20 13:07:30', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 13:07:30');
INSERT INTO `jb_login_log` VALUES (2013517583452327936, NULL, 'wuxiaoyu', '192.168.77.81', '2026-01-20 15:42:56', 2, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 15:42:56');
INSERT INTO `jb_login_log` VALUES (2013517614590840832, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-20 15:43:03', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 15:43:03');
INSERT INTO `jb_login_log` VALUES (2013527535814299648, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-20 16:22:29', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 16:22:29');

-- ----------------------------
-- Table structure for jb_online_user
-- ----------------------------
DROP TABLE IF EXISTS `jb_online_user`;
CREATE TABLE `jb_online_user`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `login_log_id` bigint NOT NULL COMMENT '登录日志ID',
  `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `expiration_time` datetime NOT NULL COMMENT '过期时间',
  `screen_locked` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否锁屏',
  `online_state` int NOT NULL COMMENT '在线状态',
  `offline_time` datetime NULL DEFAULT NULL COMMENT '离线时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '在线用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_online_user
-- ----------------------------
INSERT INTO `jb_online_user` VALUES (2011243514870747136, '711045c452ab4a0186b893b1d0a947562011243514862358528', 2009493497545871360, 2011243514623283200, '2026-01-14 09:06:36', '2026-01-19 17:21:19', '2026-01-21 09:06:36', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2013080343530033152, 'e2b5e7e72313486dab537f9673cdd7f62010540090919342080', 2001547415494049792, 2013080343496478720, '2026-01-12 10:31:26', '2026-01-19 14:19:42', '2026-01-26 10:45:30', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2013428920294952961, '73bbbdbe508e43dda8fe737a733056172013428920294952960', 2001548276093927424, 2013428920185901056, '2026-01-20 09:50:37', '2026-01-20 16:25:49', '2026-01-20 17:50:37', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2013429903720828929, '4fca22a9de3d4361aa15f68463bf6e322013429903720828928', 2001548007356481536, 2013429903448199168, '2026-01-20 09:54:31', '2026-01-20 16:35:01', '2026-01-27 09:54:31', '1', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2013478469839474689, '8fa456fe938b40519216dc009c7a677d2013478469839474688', 2001546811778514944, 2013478469545873408, '2026-01-20 13:07:30', '2026-01-20 13:07:30', '2026-01-20 21:07:30', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2013527535944323073, '6f4dcd01b82b4702a619b535fa5c83402013527535944323072', 2001547605315665920, 2013527535814299648, '2026-01-20 16:22:29', '2026-01-20 17:02:04', '2026-01-27 16:22:29', '1', 1, NULL);

-- ----------------------------
-- Table structure for jb_permission
-- ----------------------------
DROP TABLE IF EXISTS `jb_permission`;
CREATE TABLE `jb_permission`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pid` bigint NOT NULL DEFAULT 0,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `icons` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_rank` int NULL DEFAULT NULL COMMENT '排序',
  `permission_level` int NULL DEFAULT NULL COMMENT '层级',
  `permission_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限资源KEY',
  `is_menu` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否是菜单',
  `is_target_blank` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否新窗口打开',
  `is_system_admin_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否系统超级管理员默认拥有的权限',
  `open_type` int NULL DEFAULT 1 COMMENT '打开类型 1 默认 2 iframe 3 dialog',
  `open_option` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件属性json',
  `of_module` int NOT NULL DEFAULT 1 COMMENT '哪个模块',
  `of_module_link` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '具体指向关联',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'function定义' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_permission
-- ----------------------------
INSERT INTO `jb_permission` VALUES (1992880779761262592, '数据分析', 0, 'admin/dashboard', 'jbicon jb-shujuhuizong', 1, 1, 'dashboard', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779778039808, '系统管理', 0, NULL, 'jbicon jb-shezhi3', 3, 1, 'systemmgr', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779794817024, '用户管理', 1992880779778039808, 'admin/user', NULL, 1, 2, 'user', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779811594240, '部门管理', 1992880779778039808, 'admin/dept', NULL, 2, 2, 'dept', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779832565760, '岗位管理', 1992880779778039808, 'admin/post', NULL, 3, 2, 'post', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779849342976, '权限配置', 1992880779778039808, NULL, NULL, 4, 2, 'role_permission_menu', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779866120192, '角色管理', 1992880779849342976, 'admin/role', NULL, 1, 3, 'role', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779878703104, '顶部导航', 1992880779849342976, 'admin/topnav', NULL, 2, 3, 'topnav', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779899674624, '权限资源', 1992880779849342976, 'admin/permission', NULL, 3, 3, 'permission', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779945811968, '字典参数', 1992880779778039808, NULL, NULL, 5, 2, 'dictionary_config', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779958394880, '数据字典', 1992880779945811968, 'admin/dictionary', NULL, 1, 3, 'dictionary', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880779979366400, '全局参数', 1992880779945811968, 'admin/globalconfig', NULL, 2, 3, 'globalconfig', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780004532224, '系统通知', 1992880779778039808, 'admin/sysnotice', NULL, 6, 2, 'sys_notice', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780025503744, '七牛配置', 1992880779778039808, NULL, NULL, 7, 2, 'qiniu_config', '1', '0', '0', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780050669568, '七牛账号', 1992880780025503744, 'admin/qiniu', NULL, 1, 3, 'qiniu', '1', '0', '0', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780067446784, '七牛Bucket', 1992880780025503744, 'admin/qiniu/bucket', NULL, 2, 3, 'qiniu_bucket', '1', '0', '0', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780080029696, '敏感词词库', 1992880779778039808, 'admin/sensitiveword', NULL, 8, 2, 'sensitive_word', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780096806912, '系统监控', 0, NULL, 'jbicon jb-ITjiankong', 4, 1, 'jbolt_monitor', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780109389824, 'Druid数据库监控', 1992880780096806912, 'admin/druid/monitor', NULL, 1, 2, 'druid_monitor', '1', '1', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780121972736, '服务器监控', 1992880780096806912, 'admin/servermonitor', NULL, 2, 2, 'jbolt_server_monitor', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780138749952, '日志监控', 1992880780096806912, NULL, NULL, 3, 2, 'jbolt_log_monitor', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780155527168, '登录日志', 1992880780138749952, 'admin/loginlog', NULL, 1, 3, 'jbolt_login_log', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780168110080, '关键操作日志', 1992880780138749952, 'admin/systemlog', NULL, 2, 3, 'systemlog', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780180692992, '在线用户', 1992880780096806912, 'admin/onlineuser', NULL, 4, 2, 'online_user', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780197470208, '开发平台', 0, NULL, 'jbicon jb-kaifarenwu', 5, 1, 'dev_platform', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780214247424, '应用中心', 1992880780197470208, 'admin/app', NULL, 1, 2, 'application', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780226830336, '微信公众平台', 1992880780197470208, 'admin/wechat/mpinfo', NULL, 2, 2, 'wechat_mpinfo', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780243607552, '基础配置', 1992880780226830336, NULL, NULL, 1, 3, 'wechat_config_basemgr', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780260384768, '菜单配置', 1992880780226830336, NULL, NULL, 2, 3, 'wechat_menu', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780277161984, '支付配置', 1992880780226830336, NULL, NULL, 3, 3, 'wechat_config_paymgr', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780298133504, '关注回复', 1992880780226830336, NULL, NULL, 4, 3, 'wechat_autoreply_subscribe', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780310716416, '关键词回复', 1992880780226830336, NULL, NULL, 5, 3, 'wechat_autoreply_keywords', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780323299328, '默认回复', 1992880780226830336, NULL, NULL, 6, 3, 'wechat_autoreply_default', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780340076544, '素材库', 1992880780226830336, NULL, NULL, 7, 3, 'wechat_media', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780352659456, '用户管理', 1992880780226830336, NULL, NULL, 8, 3, 'wechat_user', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780369436672, '其它配置', 1992880780226830336, NULL, NULL, 9, 3, 'wechat_config_extramgr', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780390408192, '报表设计器', 1992880780197470208, 'ureport/designer', NULL, 3, 2, 'ureport_designer', '1', '0', '1', 2, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780407185408, '打印设计器', 1992880780197470208, 'admin/hiprint', NULL, 4, 2, 'hiprint_design', '1', '0', '1', 2, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780428156928, '代码生成器', 1992880780197470208, 'admin/codegen', NULL, 5, 2, 'jbolt_code_gen', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780444934144, '开发文档', 1992880780197470208, NULL, NULL, 6, 2, 'admin_dev_doc', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780461711360, '数据库文档', 1992880780444934144, 'admin/devdoc/database', NULL, 1, 3, 'admin_dev_doc_database', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780478488576, '独立逻辑权限', 0, NULL, NULL, 6, 1, 'logic_permission', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1992880780495265792, 'Ureport报表查看权', 1992880780478488576, NULL, NULL, 1, 2, 'ureport_detail', '0', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1993873896657850368, '质管部', 0, NULL, 'jbicon  jb-data', 2, 1, 'siargo', '1', '0', '1', 1, NULL, 1, NULL);
INSERT INTO `jb_permission` VALUES (1993878316057563136, '报告单管理', 1993873896657850368, '/admin/siargo/qarep', NULL, 1, 2, 'qarep', '1', '0', '1', 1, NULL, 1, '1992880780549791744');
INSERT INTO `jb_permission` VALUES (1993879334514266112, '客户管理', 1993873896657850368, '/admin/siargo/customer', NULL, 2, 2, 'customer', '1', '0', '1', 1, NULL, 1, '1992880780549791744');

-- ----------------------------
-- Table structure for jb_post
-- ----------------------------
DROP TABLE IF EXISTS `jb_post`;
CREATE TABLE `jb_post`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位类型',
  `sort_rank` int NULL DEFAULT NULL COMMENT '顺序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `sn` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '启用/禁用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_post
-- ----------------------------
INSERT INTO `jb_post` VALUES (2001545737889890304, '经理', '1', 1, NULL, '1', '1');
INSERT INTO `jb_post` VALUES (2001545773755383808, '主管', '2', 2, NULL, '2', '1');
INSERT INTO `jb_post` VALUES (2001545808043819008, '检验员', '3', 3, NULL, '3', '1');

-- ----------------------------
-- Table structure for jb_private_message
-- ----------------------------
DROP TABLE IF EXISTS `jb_private_message`;
CREATE TABLE `jb_private_message`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '私信内容',
  `create_time` datetime NOT NULL COMMENT '发送时间',
  `from_user_id` bigint NOT NULL COMMENT '发信人',
  `to_user_id` bigint NOT NULL COMMENT '收信人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内部私信' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_private_message
-- ----------------------------

-- ----------------------------
-- Table structure for jb_qiniu
-- ----------------------------
DROP TABLE IF EXISTS `jb_qiniu`;
CREATE TABLE `jb_qiniu`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `sn` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号SN',
  `ak` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'AK',
  `sk` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'SK',
  `type` int NOT NULL COMMENT '账号类型',
  `bucket_count` int NOT NULL DEFAULT 0 COMMENT '空间个数',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否默认',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `update_user_id` bigint NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '七牛账号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_qiniu
-- ----------------------------

-- ----------------------------
-- Table structure for jb_qiniu_bucket
-- ----------------------------
DROP TABLE IF EXISTS `jb_qiniu_bucket`;
CREATE TABLE `jb_qiniu_bucket`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bucket名称',
  `sn` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `qiniu_id` bigint NOT NULL COMMENT '所属七牛账号',
  `domain_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定域名',
  `callback_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调地址',
  `callback_body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调body定义',
  `callback_body_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调Body类型',
  `expires` int NULL DEFAULT NULL COMMENT '有效期(秒)',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否默认',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` bigint NOT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `region` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地区',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '七牛bucket配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_qiniu_bucket
-- ----------------------------

-- ----------------------------
-- Table structure for jb_remote_login_log
-- ----------------------------
DROP TABLE IF EXISTS `jb_remote_login_log`;
CREATE TABLE `jb_remote_login_log`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `last_login_country` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近一次登录国家',
  `last_login_province` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近一次登录省份',
  `last_login_city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近一次登录城市',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最近一次登录时间',
  `login_country` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新登录国家',
  `login_province` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新登录省份',
  `login_city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新登录城市',
  `login_time` datetime NULL DEFAULT NULL COMMENT '新登录时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '登录用户ID',
  `username` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录用户名',
  `is_new` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为最新一次',
  `last_login_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近一次登录IP',
  `login_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新登录IP',
  `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '异地登录日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_remote_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for jb_role
-- ----------------------------
DROP TABLE IF EXISTS `jb_role`;
CREATE TABLE `jb_role`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `sn` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `pid` bigint NULL DEFAULT 0 COMMENT '父级角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_role
-- ----------------------------
INSERT INTO `jb_role` VALUES (2001546465781989376, '质检', '1', 0);
INSERT INTO `jb_role` VALUES (2002940469221724160, '管理员', '2', 0);

-- ----------------------------
-- Table structure for jb_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `jb_role_permission`;
CREATE TABLE `jb_role_permission`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色功能列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_role_permission
-- ----------------------------
INSERT INTO `jb_role_permission` VALUES (2008702786747617280, 2001546465781989376, 1992880779761262592);
INSERT INTO `jb_role_permission` VALUES (2008702786747617281, 2001546465781989376, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2008702786747617282, 2001546465781989376, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2008702786747617283, 2001546465781989376, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2008702786747617284, 2001546465781989376, 1992880780096806912);
INSERT INTO `jb_role_permission` VALUES (2008702786747617285, 2001546465781989376, 1992880780121972736);
INSERT INTO `jb_role_permission` VALUES (2008702786747617286, 2001546465781989376, 1992880780138749952);
INSERT INTO `jb_role_permission` VALUES (2008702786747617287, 2001546465781989376, 1992880780155527168);
INSERT INTO `jb_role_permission` VALUES (2008702786747617288, 2001546465781989376, 1992880780168110080);
INSERT INTO `jb_role_permission` VALUES (2008702786747617289, 2001546465781989376, 1992880780180692992);
INSERT INTO `jb_role_permission` VALUES (2008702808809656320, 2002940469221724160, 1992880779761262592);
INSERT INTO `jb_role_permission` VALUES (2008702808809656321, 2002940469221724160, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2008702808809656322, 2002940469221724160, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2008702808809656323, 2002940469221724160, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2008702808809656324, 2002940469221724160, 1992880779778039808);
INSERT INTO `jb_role_permission` VALUES (2008702808809656325, 2002940469221724160, 1992880779794817024);
INSERT INTO `jb_role_permission` VALUES (2008702808809656326, 2002940469221724160, 1992880779811594240);
INSERT INTO `jb_role_permission` VALUES (2008702808809656327, 2002940469221724160, 1992880779832565760);
INSERT INTO `jb_role_permission` VALUES (2008702808809656328, 2002940469221724160, 1992880779849342976);
INSERT INTO `jb_role_permission` VALUES (2008702808809656329, 2002940469221724160, 1992880779866120192);
INSERT INTO `jb_role_permission` VALUES (2008702808809656330, 2002940469221724160, 1992880779878703104);
INSERT INTO `jb_role_permission` VALUES (2008702808809656331, 2002940469221724160, 1992880779899674624);
INSERT INTO `jb_role_permission` VALUES (2008702808809656332, 2002940469221724160, 1992880779945811968);
INSERT INTO `jb_role_permission` VALUES (2008702808809656333, 2002940469221724160, 1992880779958394880);
INSERT INTO `jb_role_permission` VALUES (2008702808809656334, 2002940469221724160, 1992880779979366400);
INSERT INTO `jb_role_permission` VALUES (2008702808809656335, 2002940469221724160, 1992880780004532224);
INSERT INTO `jb_role_permission` VALUES (2008702808809656336, 2002940469221724160, 1992880780096806912);
INSERT INTO `jb_role_permission` VALUES (2008702808809656337, 2002940469221724160, 1992880780121972736);
INSERT INTO `jb_role_permission` VALUES (2008702808809656338, 2002940469221724160, 1992880780138749952);
INSERT INTO `jb_role_permission` VALUES (2008702808809656339, 2002940469221724160, 1992880780155527168);
INSERT INTO `jb_role_permission` VALUES (2008702808809656340, 2002940469221724160, 1992880780168110080);
INSERT INTO `jb_role_permission` VALUES (2008702808809656341, 2002940469221724160, 1992880780180692992);
INSERT INTO `jb_role_permission` VALUES (2008702808809656342, 2002940469221724160, 1992880780197470208);
INSERT INTO `jb_role_permission` VALUES (2008702808809656343, 2002940469221724160, 1992880780214247424);
INSERT INTO `jb_role_permission` VALUES (2008702808809656344, 2002940469221724160, 1992880780444934144);
INSERT INTO `jb_role_permission` VALUES (2008702808809656345, 2002940469221724160, 1992880780461711360);

-- ----------------------------
-- Table structure for jb_sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `jb_sensitive_word`;
CREATE TABLE `jb_sensitive_word`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `content` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '启用状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词词库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_sensitive_word
-- ----------------------------
INSERT INTO `jb_sensitive_word` VALUES (2003749800842301440, 'siargo', '1');

-- ----------------------------
-- Table structure for jb_sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `jb_sys_notice`;
CREATE TABLE `jb_sys_notice`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `type` int NOT NULL COMMENT '通知类型',
  `priority_level` int NOT NULL COMMENT '优先级',
  `read_count` int NULL DEFAULT 0 COMMENT '已读人数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人',
  `receiver_type` int NULL DEFAULT NULL COMMENT '接收人类型',
  `receiver_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接收人值',
  `files` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for jb_sys_notice_reader
-- ----------------------------
DROP TABLE IF EXISTS `jb_sys_notice_reader`;
CREATE TABLE `jb_sys_notice_reader`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `sys_notice_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知阅读用户关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_sys_notice_reader
-- ----------------------------

-- ----------------------------
-- Table structure for jb_system_log
-- ----------------------------
DROP TABLE IF EXISTS `jb_system_log`;
CREATE TABLE `jb_system_log`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `type` int NULL DEFAULT NULL COMMENT '操作类型 删除 更新 新增',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '连接对象详情地址',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `target_type` int NULL DEFAULT NULL COMMENT '操作对象类型',
  `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
  `target_id` bigint NULL DEFAULT NULL COMMENT '操作对象ID',
  `open_type` int NULL DEFAULT NULL COMMENT '打开类型URL还是Dialog',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_system_log
-- ----------------------------
INSERT INTO `jb_system_log` VALUES (2003703987441176576, '<span class=\'text-danger\'>[总管理(admin)]</span>更新全局参数配置<span class=\'text-danger\'>[null]</span>', 2, 'admin/globalconfig/show/1992880865480253440', 1992880779681570816, '总管理', 6, '2025-12-24 13:47:12', 1992880865480253440, 1);
INSERT INTO `jb_system_log` VALUES (2003704214072004608, '<span class=\'text-danger\'>[总管理(admin)]</span>更新全局参数配置<span class=\'text-danger\'>[null]</span>', 2, 'admin/globalconfig/show/1993858740519374848', 1992880779681570816, '总管理', 6, '2025-12-24 13:48:06', 1993858740519374848, 1);
INSERT INTO `jb_system_log` VALUES (2003704809176633344, '<span class=\'text-danger\'>[总管理(admin)]</span>更新全局参数配置<span class=\'text-danger\'>[null]</span>', 2, 'admin/globalconfig/show/1993858740519374848', 1992880779681570816, '总管理', 6, '2025-12-24 13:50:28', 1993858740519374848, 1);
INSERT INTO `jb_system_log` VALUES (2003705542013816832, '<span class=\'text-danger\'>[总管理(admin)]</span>更新全局参数配置<span class=\'text-danger\'>[null]</span>', 2, 'admin/globalconfig/show/1992880954177200128', 1992880779681570816, '总管理', 6, '2025-12-24 13:53:23', 1992880954177200128, 1);
INSERT INTO `jb_system_log` VALUES (2003705987704754176, '<span class=\'text-danger\'>[总管理(admin)]</span>更新部门<span class=\'text-danger\'>[质管部]</span>', 2, 'admin/admin/dept/show/2001545887328747520', 1992880779681570816, '总管理', 17, '2025-12-24 13:55:09', 2001545887328747520, 1);
INSERT INTO `jb_system_log` VALUES (2003747969395265536, '<span class=\'text-danger\'>[总管理(admin)]</span>更新全局参数配置<span class=\'text-danger\'>[null]</span>', 2, 'admin/globalconfig/show/1992880865480253440', 1992880779681570816, '总管理', 6, '2025-12-24 16:41:58', 1992880865480253440, 1);
INSERT INTO `jb_system_log` VALUES (2003748222173384705, '<span class=\'text-danger\'>[siargo(siargo)]</span>新增顶部导航<span class=\'text-danger\'>[1]</span>', 1, NULL, 2002984611549220864, 'siargo', 16, '2025-12-24 16:42:59', 2003748222173384704, 1);
INSERT INTO `jb_system_log` VALUES (2003748277336870913, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新顶部导航<span class=\'text-danger\'>[1]</span>下的菜单', 2, NULL, 2002984611549220864, 'siargo', 16, '2025-12-24 16:43:12', 2003748222173384704, 1);
INSERT INTO `jb_system_log` VALUES (2003748370865655808, '<span class=\'text-danger\'>[siargo(siargo)]</span>删除顶部导航<span class=\'text-danger\'>[1]</span>', 3, NULL, 2002984611549220864, 'siargo', 16, '2025-12-24 16:43:34', 2003748222173384704, 1);
INSERT INTO `jb_system_log` VALUES (2003749008538275840, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新角色<span class=\'text-danger\'>[管理员]</span>的可用权限设置', 2, 'admin/role/show/2002940469221724160', 2002984611549220864, 'siargo', 5, '2025-12-24 16:46:06', 2002940469221724160, 1);
INSERT INTO `jb_system_log` VALUES (2003750372286861312, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新角色<span class=\'text-danger\'>[管理员]</span>的可用权限设置', 2, 'admin/role/show/2002940469221724160', 2002984611549220864, 'siargo', 5, '2025-12-24 16:51:31', 2002940469221724160, 1);
INSERT INTO `jb_system_log` VALUES (2003751292974338048, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新部门<span class=\'text-danger\'>[质管部]</span>', 2, 'admin/admin/dept/show/2001545887328747520', 2002984611549220864, 'siargo', 17, '2025-12-24 16:55:11', 2001545887328747520, 1);
INSERT INTO `jb_system_log` VALUES (2003751330924400640, '<span class=\'text-danger\'>[siargo(siargo)]</span>新增部门<span class=\'text-danger\'>[管理员]</span>', 1, 'admin/admin/dept/show/2003751330882457600', 2002984611549220864, 'siargo', 17, '2025-12-24 16:55:20', 2003751330882457600, 1);
INSERT INTO `jb_system_log` VALUES (2003751455302291456, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[siargo]</span>', 2, 'admin/user/show/2002984611549220864', 2002984611549220864, 'siargo', 4, '2025-12-24 16:55:50', 2002984611549220864, 1);
INSERT INTO `jb_system_log` VALUES (2003751705110843392, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[siargo]</span>', 2, 'admin/user/show/2002984611549220864', 2002984611549220864, 'siargo', 4, '2025-12-24 16:56:49', 2002984611549220864, 1);
INSERT INTO `jb_system_log` VALUES (2003751781510090752, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[siargo]</span>', 2, 'admin/user/show/2002984611549220864', 2002984611549220864, 'siargo', 4, '2025-12-24 16:57:07', 2002984611549220864, 1);
INSERT INTO `jb_system_log` VALUES (2003751846022680576, '<span class=\'text-danger\'>[siargo(siargo)]</span>删除部门<span class=\'text-danger\'>[管理员]</span>', 3, NULL, 2002984611549220864, 'siargo', 17, '2025-12-24 16:57:23', 2003751330882457600, 1);
INSERT INTO `jb_system_log` VALUES (2003992595028971520, '<span class=\'text-danger\'>[siargo(siargo)]</span>新增用户<span class=\'text-danger\'>[吴勇]</span>', 1, 'admin/user/show/2003992595003805696', 2002984611549220864, 'siargo', 4, '2025-12-25 08:54:02', 2003992595003805696, 1);
INSERT INTO `jb_system_log` VALUES (2004366414138773504, '<span class=\'text-danger\'>[吴勇(wuyong)]</span>更新用户<span class=\'text-danger\'>[冯英]</span>', 2, 'admin/user/show/2001547415494049792', 2003992595003805696, '吴勇', 4, '2025-12-26 09:39:27', 2001547415494049792, 1);
INSERT INTO `jb_system_log` VALUES (2007080161429065728, '<span class=\'text-danger\'>[总管理(admin)]</span>新增部门<span class=\'text-danger\'>[工艺部]</span>', 1, 'admin/admin/dept/show/2007080161399705600', 1992880779681570816, '总管理', 17, '2026-01-02 21:22:55', 2007080161399705600, 1);
INSERT INTO `jb_system_log` VALUES (2007080219553730560, '<span class=\'text-danger\'>[总管理(admin)]</span>更新用户<span class=\'text-danger\'>[吴勇]</span>', 2, 'admin/user/show/2003992595003805696', 1992880779681570816, '总管理', 4, '2026-01-02 21:23:09', 2003992595003805696, 1);
INSERT INTO `jb_system_log` VALUES (2007680952606511104, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新全局参数配置<span class=\'text-danger\'>[null]</span>', 2, 'admin/globalconfig/show/1992880864943382528', 2002984611549220864, 'siargo', 6, '2026-01-04 13:10:15', 1992880864943382528, 1);
INSERT INTO `jb_system_log` VALUES (2007701726490906624, '<span class=\'text-danger\'>[吴小雨(wuxiaoyu)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[jbolt_style_5]]</span>', 2, NULL, 2001547605315665920, '吴小雨', 15, '2026-01-04 14:32:48', 2007701718077132800, 1);
INSERT INTO `jb_system_log` VALUES (2007701738859909120, '<span class=\'text-danger\'>[吴小雨(wuxiaoyu)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[default]]</span>', 2, NULL, 2001547605315665920, '吴小雨', 15, '2026-01-04 14:32:50', 2007701718077132800, 1);
INSERT INTO `jb_system_log` VALUES (2008103915910713344, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[吴小雨]</span>', 2, 'admin/user/show/2001547605315665920', 2002984611549220864, 'siargo', 4, '2026-01-05 17:10:57', 2001547605315665920, 1);
INSERT INTO `jb_system_log` VALUES (2008103954653499392, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[冯英]</span>', 2, 'admin/user/show/2001547415494049792', 2002984611549220864, 'siargo', 4, '2026-01-05 17:11:06', 2001547415494049792, 1);
INSERT INTO `jb_system_log` VALUES (2008104140725407744, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新部门<span class=\'text-danger\'>[工艺部]</span>', 2, 'admin/admin/dept/show/2007080161399705600', 2002984611549220864, 'siargo', 17, '2026-01-05 17:11:51', 2007080161399705600, 1);
INSERT INTO `jb_system_log` VALUES (2008702786856669184, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新角色<span class=\'text-danger\'>[质检]</span>的可用权限设置', 2, 'admin/role/show/2001546465781989376', 2002984611549220864, 'siargo', 5, '2026-01-07 08:50:39', 2001546465781989376, 1);
INSERT INTO `jb_system_log` VALUES (2008702808876765184, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新角色<span class=\'text-danger\'>[管理员]</span>的可用权限设置', 2, 'admin/role/show/2002940469221724160', 2002984611549220864, 'siargo', 5, '2026-01-07 08:50:44', 2002940469221724160, 1);
INSERT INTO `jb_system_log` VALUES (2009495312580595712, '<span class=\'text-danger\'>[siargo(siargo)]</span>删除字典数据分类<span class=\'text-danger\'>[扩展数据字典Demo]</span>', 3, NULL, 2002984611549220864, 'siargo', 1, '2026-01-09 13:19:52', 1993858758043176960, 1);
INSERT INTO `jb_system_log` VALUES (2010644890356928512, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[冯英]</span>', 2, 'admin/user/show/2001547415494049792', 2002984611549220864, 'siargo', 4, '2026-01-12 17:27:52', 2001547415494049792, 1);
INSERT INTO `jb_system_log` VALUES (2010644930144096256, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[吴小雨]</span>', 2, 'admin/user/show/2001547605315665920', 2002984611549220864, 'siargo', 4, '2026-01-12 17:28:02', 2001547605315665920, 1);
INSERT INTO `jb_system_log` VALUES (2010644974419169280, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[石珍云]</span>', 2, 'admin/user/show/2001548170967891968', 2002984611549220864, 'siargo', 4, '2026-01-12 17:28:13', 2001548170967891968, 1);
INSERT INTO `jb_system_log` VALUES (2010645023068901376, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[梁鹏]</span>', 2, 'admin/user/show/2001547886069792768', 2002984611549220864, 'siargo', 4, '2026-01-12 17:28:24', 2001547886069792768, 1);
INSERT INTO `jb_system_log` VALUES (2010883124651479040, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[韩子衿]</span>', 2, 'admin/user/show/2001546811778514944', 2002984611549220864, 'siargo', 4, '2026-01-13 09:14:32', 2001546811778514944, 1);
INSERT INTO `jb_system_log` VALUES (2010883185372418048, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[罗雄飞]</span>', 2, 'admin/user/show/2001548007356481536', 2002984611549220864, 'siargo', 4, '2026-01-13 09:14:46', 2001548007356481536, 1);
INSERT INTO `jb_system_log` VALUES (2010883234479329280, '<span class=\'text-danger\'>[siargo(siargo)]</span>更新用户<span class=\'text-danger\'>[侯亮]</span>', 2, 'admin/user/show/2001548276093927424', 2002984611549220864, 'siargo', 4, '2026-01-13 09:14:58', 2001548276093927424, 1);
INSERT INTO `jb_system_log` VALUES (2010883737812586496, '<span class=\'text-danger\'>[总管理(admin)]</span>删除用户<span class=\'text-danger\'>[siargo]</span>', 3, NULL, 2009493497545871360, '总管理', 4, '2026-01-13 09:16:58', 2002984611549220864, 1);
INSERT INTO `jb_system_log` VALUES (2010884113714499584, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[siargo]</span>', 2, 'admin/user/show/2009493497545871360', 2009493497545871360, 'siargo', 4, '2026-01-13 09:18:28', 2009493497545871360, 1);
INSERT INTO `jb_system_log` VALUES (2010918615119679488, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[jbolt_style_2]]</span>', 2, NULL, 2001546811778514944, '韩子衿', 15, '2026-01-13 11:35:34', 2008103332432695296, 1);
INSERT INTO `jb_system_log` VALUES (2010918634811936768, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[jbolt_style_4]]</span>', 2, NULL, 2001546811778514944, '韩子衿', 15, '2026-01-13 11:35:38', 2008103332432695296, 1);
INSERT INTO `jb_system_log` VALUES (2010918656521654272, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[jbolt_style_6]]</span>', 2, NULL, 2001546811778514944, '韩子衿', 15, '2026-01-13 11:35:43', 2008103332432695296, 1);
INSERT INTO `jb_system_log` VALUES (2010918682673139712, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[jbolt_style_5]]</span>', 2, NULL, 2001546811778514944, '韩子衿', 15, '2026-01-13 11:35:50', 2008103332432695296, 1);
INSERT INTO `jb_system_log` VALUES (2010918714965086208, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[jbolt_style_4]]</span>', 2, NULL, 2001546811778514944, '韩子衿', 15, '2026-01-13 11:35:57', 2008103332432695296, 1);
INSERT INTO `jb_system_log` VALUES (2010918737090039808, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户自身配置<span class=\'text-danger\'>[系统Admin后台样式为:[default]]</span>', 2, NULL, 2001546811778514944, '韩子衿', 15, '2026-01-13 11:36:03', 2008103332432695296, 1);

-- ----------------------------
-- Table structure for jb_todo
-- ----------------------------
DROP TABLE IF EXISTS `jb_todo`;
CREATE TABLE `jb_todo`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详情内容',
  `user_id` bigint NOT NULL COMMENT '所属用户',
  `state` int NOT NULL COMMENT '状态',
  `specified_finish_time` datetime NOT NULL COMMENT '规定完成时间',
  `type` int NOT NULL COMMENT '类型 链接还是内容 还是都有',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '链接',
  `priority_level` int NOT NULL COMMENT '优先级',
  `real_finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `source_msg_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方系统消息主键',
  `source_request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方系统流程主键',
  `source_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方url',
  `send_user_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送人第三方系统标识',
  `receive_user_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接收人第三方系统标识',
  `source_sys` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源系统',
  `is_readed` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '待办事项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_todo
-- ----------------------------

-- ----------------------------
-- Table structure for jb_topnav
-- ----------------------------
DROP TABLE IF EXISTS `jb_topnav`;
CREATE TABLE `jb_topnav`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `icon` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否启用',
  `sort_rank` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '顶部导航' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_topnav
-- ----------------------------

-- ----------------------------
-- Table structure for jb_topnav_menu
-- ----------------------------
DROP TABLE IF EXISTS `jb_topnav_menu`;
CREATE TABLE `jb_topnav_menu`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `topnav_id` bigint NULL DEFAULT NULL COMMENT '顶部导航ID',
  `permission_id` bigint NULL DEFAULT NULL COMMENT '菜单资源ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '顶部菜单对应左侧一级导航中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_topnav_menu
-- ----------------------------

-- ----------------------------
-- Table structure for jb_user
-- ----------------------------
DROP TABLE IF EXISTS `jb_user`;
CREATE TABLE `jb_user`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `username` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `sn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
  `phone` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '启用',
  `sex` int NULL DEFAULT 0 COMMENT '性别',
  `age` int NULL DEFAULT 0 COMMENT '年龄',
  `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拼音码',
  `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色 一对多',
  `is_system_admin` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否系统超级管理员',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `pwd_salt` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码盐值',
  `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `login_city` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录城市',
  `login_province` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录省份',
  `login_country` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录国家',
  `is_remote_login` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否异地登录',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `dept_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门路径',
  `posts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位IDS',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `last_pwd_update_time` datetime NULL DEFAULT NULL COMMENT '最近一次密码修改时间',
  `of_module` int NOT NULL DEFAULT 1 COMMENT '哪个模块',
  `of_module_link` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '具体指向关联',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户登录账户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_user
-- ----------------------------
INSERT INTO `jb_user` VALUES (2001546811778514944, 'hanzijin', 'adef7b7da197d6289ffa68e09b48d2c4222c8f2e4df956efffed841e71ff6c4b', '韩子衿', NULL, 'upload/user/avatar/20251218/c8e7ebc6659943388f2942e6fef13ff9.jpg', '2025-12-18 14:55:22', NULL, 'zhan@siargo.com.cn', '1', 1, NULL, 'hzj,hanzijin', '2001546465781989376', '0', 1992880779681570816, 'HLfgOmdKIMgr8Bgf9ujYqHaz5WrZY2Gw', '192.168.77.83', '2026-01-20 13:07:30', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-01-20 13:07:30', 2002984611549220864, '2026-01-13 09:14:32', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547415494049792, 'fengying', '05a9f06626bd38d6eb77f4785af0caf47dd5906b090b55b509ba4de88dcafc37', '冯英', NULL, 'assets/img/nv.png', '2025-12-18 14:57:45', NULL, 'yingfeng@siargo.com.cn', '1', 2, NULL, 'fy,fengying', '2001546465781989376,2002940469221724160', '0', 1992880779681570816, 'vjrc6bdLpItUduWxMX65qKFW0a9fDJSS', '192.168.77.103', '2026-01-12 10:31:26', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545737889890304', '2026-01-12 17:27:52', 2002984611549220864, '2026-01-12 17:27:52', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547605315665920, 'wuxiaoyu', '516e1cb55967ce54002ad3ba5f68709bbe41e7f92c20e7244de38e91a5710f7d', '吴小雨', NULL, 'assets/img/nv.png', '2025-12-18 14:58:31', NULL, 'xwu@siargo.com.cn', '1', 2, NULL, 'wxy,wuxiaoyu', '2001546465781989376,2002940469221724160', '0', 1992880779681570816, 'bL5FLrkc3nASgeR5M7tp1iy9xxjyQkZX', '192.168.3.11', '2026-01-20 16:22:29', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545773755383808', '2026-01-20 16:22:29', 2002984611549220864, '2026-01-12 17:28:02', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547886069792768, 'liangpeng', '77ee5afdcd4982257eaa2c84dd2712ad6ec0f1fd22990221b4adb1c5c592fd17', '梁鹏', NULL, 'assets/img/nan.png', '2025-12-18 14:59:38', NULL, 'pliang@siargo.com.cn', '1', 1, NULL, 'lp,liangpeng', '2001546465781989376', '0', 1992880779681570816, 'Cgm5O9cNmImm6cAs2ofNkQlNlys6MIXk', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-01-12 17:28:24', 2002984611549220864, '2026-01-12 17:28:24', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548007356481536, 'luoxiongfei', '0fa0eeac4e38f32dce622b91b78cbc462925030163f6af0c7714b2f04957c743', '罗雄飞', NULL, 'assets/img/nv.png', '2025-12-18 15:00:07', NULL, 'xluo@siargo.com.cn', '1', 2, NULL, 'lxf,luoxiongfei', '2001546465781989376', '0', 1992880779681570816, 'st4WHUfysHBHQGfWgoxCdFSppdM3ptx8', '192.168.3.30', '2026-01-20 09:54:31', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-01-20 09:54:31', 2002984611549220864, '2026-01-13 09:14:46', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548170967891968, 'shizhenyun', '696883428e592b11c7d3368e27ffe7cd2bf7613463e67663f3362c0c842b88db', '石珍云', NULL, 'assets/img/nv.png', '2025-12-18 15:00:46', NULL, 'zshi@siargo.com.cn', '1', 2, NULL, 'szy,shizhenyun', '2001546465781989376', '0', 1992880779681570816, '7RRLBDgyDQIafysHBRyTbkKMUraBaBcW', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-01-12 17:28:13', 2002984611549220864, '2026-01-12 17:28:13', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548276093927424, 'houliang', '4929e03616218c998ceae379dc15c92251a4c878b816027f1db147d13df55c6e', '侯亮', NULL, 'assets/img/nan.png', '2025-12-18 15:01:11', NULL, 'hou@siargo.com.cn', '1', 1, NULL, 'hl,houliang', '2001546465781989376', '0', 1992880779681570816, 'K0mhl2XI7cnGYRhH4G3UJr5svTgu3DTw', '192.168.3.65', '2026-01-20 09:50:37', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-01-20 09:50:37', 2002984611549220864, '2026-01-13 09:14:58', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2003992595003805696, 'wuyong', '69138627d70227a9f3f00e791558a31550823e2adc35c5f8462fb751fe8ab136', '吴勇', NULL, 'assets/img/nan.png', '2025-12-25 08:54:02', NULL, NULL, '1', 1, NULL, 'wy,wuyong', '2002940469221724160', '0', 2002984611549220864, 'MLec0kYhZVJhwbGE7CJqEmrkBcnXMvJv', '192.168.9.11', '2025-12-26 14:04:17', '内网IP', NULL, NULL, '0', 2007080161399705600, '2007080161399705600', NULL, '2026-01-02 21:23:09', 1992880779681570816, '2026-01-02 21:23:09', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2009493497545871360, 'admin', '0456810c087f40503d0a9b09933dab5b169182967a55dcc6edde6fc7d5cb1bb5', 'siargo', NULL, 'assets/img/nan.png', '2026-01-09 13:12:39', NULL, NULL, '1', 0, 0, '', '2002940469221724160', '1', 0, 'iPFW2GiLnayMLebIuCtiGAlfnkbw73T2', '127.0.0.1', '2026-01-14 09:06:36', NULL, NULL, NULL, '0', NULL, NULL, NULL, '2026-01-14 09:06:36', 2009493497545871360, '2026-01-13 09:18:28', 1, NULL);

-- ----------------------------
-- Table structure for jb_user_config
-- ----------------------------
DROP TABLE IF EXISTS `jb_user_config`;
CREATE TABLE `jb_user_config`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置名',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置KEY',
  `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置值',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `value_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取值类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户系统样式自定义设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_user_config
-- ----------------------------
INSERT INTO `jb_user_config` VALUES (2007701718077132800, '系统Admin后台样式', 'JBOLT_ADMIN_STYLE', 'default', 2001547605315665920, '2026-01-04 14:32:46', '2026-01-04 14:32:50', 'string');
INSERT INTO `jb_user_config` VALUES (2007701718148435968, '系统Admin后台是否启用多选项卡', 'JBOLT_ADMIN_WITH_TABS', 'true', 2001547605315665920, '2026-01-04 14:32:46', '2026-01-04 14:32:46', 'boolean');
INSERT INTO `jb_user_config` VALUES (2007701718207156224, '系统登录页面背景图是否启用模糊风格', 'JBOLT_LOGIN_BGIMG_BLUR', 'true', 2001547605315665920, '2026-01-04 14:32:46', '2026-01-04 14:32:46', 'boolean');
INSERT INTO `jb_user_config` VALUES (2007701718270070784, '系统登录页面是否开启线条特效', 'JBOLT_LOGIN_NEST', 'true', 2001547605315665920, '2026-01-04 14:32:46', '2026-01-04 14:32:46', 'boolean');
INSERT INTO `jb_user_config` VALUES (2008103332432695296, '系统Admin后台样式', 'JBOLT_ADMIN_STYLE', 'default', 2001546811778514944, '2026-01-05 17:08:38', '2026-01-13 11:36:03', 'string');
INSERT INTO `jb_user_config` VALUES (2008103332503998464, '系统Admin后台是否启用多选项卡', 'JBOLT_ADMIN_WITH_TABS', 'true', 2001546811778514944, '2026-01-05 17:08:38', '2026-01-05 17:08:38', 'boolean');
INSERT INTO `jb_user_config` VALUES (2008103332592078848, '系统登录页面背景图是否启用模糊风格', 'JBOLT_LOGIN_BGIMG_BLUR', 'true', 2001546811778514944, '2026-01-05 17:08:38', '2026-01-05 17:08:38', 'boolean');
INSERT INTO `jb_user_config` VALUES (2008103332625633280, '系统登录页面是否开启线条特效', 'JBOLT_LOGIN_NEST', 'true', 2001546811778514944, '2026-01-05 17:08:38', '2026-01-05 17:08:38', 'boolean');

-- ----------------------------
-- Table structure for jb_user_extend
-- ----------------------------
DROP TABLE IF EXISTS `jb_user_extend`;
CREATE TABLE `jb_user_extend`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `english_full_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '英文全名',
  `birthday` datetime NULL DEFAULT NULL COMMENT '出生日期',
  `residential_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '居住地址',
  `company_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司名称',
  `company_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司地址',
  `recipient_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '快递收件地址',
  `recipient_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收件人电话',
  `recipient_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收件人姓名',
  `id_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `country` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家',
  `nation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '民族',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
  `city_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市代码',
  `county` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县',
  `township` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '乡镇',
  `community` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行政村 社区',
  `marital_status` int NULL DEFAULT NULL COMMENT '婚姻状态',
  `is_cpc_member` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否党员',
  `is_cyl_member` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否共青团员',
  `professional_title` int NULL DEFAULT NULL COMMENT '职称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户扩展信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_user_extend
-- ----------------------------
INSERT INTO `jb_user_extend` VALUES (2001546811778514944, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001547415494049792, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001547605315665920, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001547886069792768, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001548007356481536, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001548170967891968, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001548276093927424, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2003992595003805696, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2009493497545871360, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);

-- ----------------------------
-- Table structure for jb_wechat_article
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_article`;
CREATE TABLE `jb_wechat_article`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新用户 ID',
  `brief_info` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `poster` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT NULL COMMENT '阅读数',
  `media_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信素材 ID',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图文链接地址',
  `type` int NULL DEFAULT NULL COMMENT '本地图文 公众号图文素材 外部图文',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信图文信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_article
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_autoreply
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_autoreply`;
CREATE TABLE `jb_wechat_autoreply`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `type` int NULL DEFAULT 0 COMMENT '类型 关注回复 无内容时回复 关键词回复',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则名称',
  `reply_type` int NULL DEFAULT NULL COMMENT '回复类型 全部还是随机一条',
  `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 ID',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信公众号自动回复规则' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_autoreply
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_config
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_config`;
CREATE TABLE `jb_wechat_config`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置key',
  `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置值',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置项名称',
  `type` int NULL DEFAULT NULL COMMENT '配置类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信公众号配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_config
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_keywords
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_keywords`;
CREATE TABLE `jb_wechat_keywords`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` int NULL DEFAULT 0 COMMENT '模糊 全匹配',
  `auto_reply_id` bigint NULL DEFAULT NULL COMMENT '回复规则ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信关键词回复中的关键词定义' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_keywords
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_media
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_media`;
CREATE TABLE `jb_wechat_media`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `digest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型 image video voice news',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `media_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信素材ID',
  `thumb_media_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图ID',
  `content_source_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原文地址',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问地址',
  `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图文作者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `server_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存服务器URL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信公众平台素材库同步管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_media
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_menu
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_menu`;
CREATE TABLE `jb_wechat_menu`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pid` bigint NULL DEFAULT 0,
  `sort_rank` int NULL DEFAULT NULL COMMENT '排序',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信小程序APPID',
  `page_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信小程序页面地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信菜单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_menu
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_mpinfo
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_mpinfo`;
CREATE TABLE `jb_wechat_mpinfo`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像LOGO',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `brief_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `type` int NULL DEFAULT NULL COMMENT '类型 订阅号、服务号、小程序、企业号',
  `is_authenticated` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否已认证',
  `subject_type` int NULL DEFAULT NULL COMMENT '申请认证主体的类型 个人还是企业',
  `wechat_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信号',
  `qrcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二维码',
  `link_app_id` bigint NULL DEFAULT NULL COMMENT '关联应用ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信公众号与小程序' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_mpinfo
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_reply_content
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_reply_content`;
CREATE TABLE `jb_wechat_reply_content`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '类型 图文 文字 图片 音频 视频',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `poster` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `auto_reply_id` bigint NULL DEFAULT NULL COMMENT '回复规则ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 ID',
  `media_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联数据',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '微信 ID',
  `sort_rank` int NULL DEFAULT NULL COMMENT '排序',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自动回复内容' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_reply_content
-- ----------------------------

-- ----------------------------
-- Table structure for jb_wechat_user
-- ----------------------------
DROP TABLE IF EXISTS `jb_wechat_user`;
CREATE TABLE `jb_wechat_user`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'openId',
  `union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'unionID',
  `sex` int NULL DEFAULT 0 COMMENT '性别 1男 2女 0 未知',
  `language` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '语言',
  `subscribe` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否已关注',
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
  `head_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `subscribe_time` datetime NULL DEFAULT NULL COMMENT '关注时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `group_id` int NULL DEFAULT NULL COMMENT '分组',
  `tag_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `subscribe_scene` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关注渠道',
  `qr_scene` int NULL DEFAULT NULL COMMENT '二维码场景-开发者自定义',
  `qr_scene_str` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二维码扫码场景描述-开发者自定义',
  `realname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `phone_country_code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号国家代码',
  `check_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机验证码',
  `is_checked` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否已验证',
  `source` int NULL DEFAULT NULL COMMENT '来源 小程序还是公众平台',
  `session_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小程序登录SessionKey',
  `enable` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '禁用访问',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `checked_time` datetime NULL DEFAULT NULL COMMENT '验证绑定手机号时间',
  `mp_id` bigint NULL DEFAULT NULL COMMENT '所属公众平台ID',
  `weixin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信号',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `first_auth_time` datetime NULL DEFAULT NULL COMMENT '首次授权时间',
  `last_auth_time` datetime NULL DEFAULT NULL COMMENT '最后一次更新授权时间',
  `first_login_time` datetime NULL DEFAULT NULL COMMENT '首次登录时间',
  `bind_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定其他用户信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信用户信息_模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jb_wechat_user
-- ----------------------------

-- ----------------------------
-- Table structure for siargo_customer
-- ----------------------------
DROP TABLE IF EXISTS `siargo_customer`;
CREATE TABLE `siargo_customer`  (
  `id` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户名称(Customer Name)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_customer
-- ----------------------------
INSERT INTO `siargo_customer` VALUES (2000847525710860288, '重庆捷定力c');
INSERT INTO `siargo_customer` VALUES (2000847563539288064, '深圳堡森s');
INSERT INTO `siargo_customer` VALUES (2000847627364012032, '常州斯尔特c');
INSERT INTO `siargo_customer` VALUES (2000847720016187392, '常州爱德克斯c');
INSERT INTO `siargo_customer` VALUES (2007693569572065280, '英国y');
INSERT INTO `siargo_customer` VALUES (2007710955599679488, '海林奥德h');
INSERT INTO `siargo_customer` VALUES (2007711025233514496, '济南时宜机械j');
INSERT INTO `siargo_customer` VALUES (2007711100328333312, '日本r');
INSERT INTO `siargo_customer` VALUES (2007711147526836224, '美国m');
INSERT INTO `siargo_customer` VALUES (2007711171857993728, '韩国h');
INSERT INTO `siargo_customer` VALUES (2007711232994168832, '泰国t');
INSERT INTO `siargo_customer` VALUES (2007711626285666304, '哥伦比亚g');
INSERT INTO `siargo_customer` VALUES (2007711682178961408, '张保利z');
INSERT INTO `siargo_customer` VALUES (2007714429901066240, '中山清匠z');
INSERT INTO `siargo_customer` VALUES (2007714485454622720, '钦州科华q');
INSERT INTO `siargo_customer` VALUES (2007714532925755392, '常州新昌商贸c');
INSERT INTO `siargo_customer` VALUES (2007714570045345792, '山东新华医用s');
INSERT INTO `siargo_customer` VALUES (2007714613645135872, '珠海联创z');
INSERT INTO `siargo_customer` VALUES (2007714677515997184, '天津和正仪t');
INSERT INTO `siargo_customer` VALUES (2007714709090717696, '广东兴成g');
INSERT INTO `siargo_customer` VALUES (2007714738543120384, '山东康诚s');
INSERT INTO `siargo_customer` VALUES (2007714759992791040, '上海穗杉s');
INSERT INTO `siargo_customer` VALUES (2007714795229138944, '深圳蓝迪s');
INSERT INTO `siargo_customer` VALUES (2007714830587121664, '赫比科技h');
INSERT INTO `siargo_customer` VALUES (2007714856986071040, '北京精研b');
INSERT INTO `siargo_customer` VALUES (2007714924677943296, '北京联华b');
INSERT INTO `siargo_customer` VALUES (2007714948971352064, '郑州通达z');
INSERT INTO `siargo_customer` VALUES (2007714970609766400, '苏州海宇s');
INSERT INTO `siargo_customer` VALUES (2007714998329921536, '久声智能j');
INSERT INTO `siargo_customer` VALUES (2007715022455558144, '瑞典r');
INSERT INTO `siargo_customer` VALUES (2007715058723704832, '上海沃尔特s');
INSERT INTO `siargo_customer` VALUES (2007715086636797952, '湖南一特h');
INSERT INTO `siargo_customer` VALUES (2007715109323788288, '上海依瑞s');
INSERT INTO `siargo_customer` VALUES (2007715134355394560, '漳州众环科技z');
INSERT INTO `siargo_customer` VALUES (2007715160188112896, '梅州市人民医院m');
INSERT INTO `siargo_customer` VALUES (2007719346942365696, '陕西卫峰s');
INSERT INTO `siargo_customer` VALUES (2007719394908426240, '成都联邦c');
INSERT INTO `siargo_customer` VALUES (2007719497152974848, '河南科荣h');
INSERT INTO `siargo_customer` VALUES (2007719816536641536, '长沙矽珺c');
INSERT INTO `siargo_customer` VALUES (2007720000314265600, '成都麦特斯c');
INSERT INTO `siargo_customer` VALUES (2008000445559984128, '南京威翔n');
INSERT INTO `siargo_customer` VALUES (2008067760343339008, '王增平w');
INSERT INTO `siargo_customer` VALUES (2008069857314983936, '广州铭鸿g');
INSERT INTO `siargo_customer` VALUES (2008070087435472896, '北京金昌b');
INSERT INTO `siargo_customer` VALUES (2008070408593330176, '上海瑞游s');
INSERT INTO `siargo_customer` VALUES (2008070674134716416, '河南莱帕克h');
INSERT INTO `siargo_customer` VALUES (2008079137560711168, '哈尔滨工业大学h');
INSERT INTO `siargo_customer` VALUES (2008081212201881600, '广州迪川g');
INSERT INTO `siargo_customer` VALUES (2008081588447727616, '河南云氧h');
INSERT INTO `siargo_customer` VALUES (2008081806144688128, '深圳捷工s');
INSERT INTO `siargo_customer` VALUES (2008081896909426688, '成都一牌c');
INSERT INTO `siargo_customer` VALUES (2008118232144007168, '青岛沃特勒q');
INSERT INTO `siargo_customer` VALUES (2008353170634166272, '深圳迈瑞s');
INSERT INTO `siargo_customer` VALUES (2008369853578989568, '湖南谊安优乐h');
INSERT INTO `siargo_customer` VALUES (2008372258152501248, '日本r');
INSERT INTO `siargo_customer` VALUES (2008405882360942592, '河北宜安h');
INSERT INTO `siargo_customer` VALUES (2008414381195251712, '易佳杰y');
INSERT INTO `siargo_customer` VALUES (2008415147318431744, '深圳工采s');
INSERT INTO `siargo_customer` VALUES (2008417707106357248, '深圳实益达s');
INSERT INTO `siargo_customer` VALUES (2008705235667505152, '苏州苏净s');
INSERT INTO `siargo_customer` VALUES (2008712028045037568, '北京擎研b');
INSERT INTO `siargo_customer` VALUES (2008718734284148736, '上海祖发s');
INSERT INTO `siargo_customer` VALUES (2008719110416748544, '先进科技x');
INSERT INTO `siargo_customer` VALUES (2008719311974027264, '杭州超钜h');
INSERT INTO `siargo_customer` VALUES (2008719496024281088, '上海毅恬工业s');
INSERT INTO `siargo_customer` VALUES (2008719753382580224, '弗卡恩(上海)生物科技f');
INSERT INTO `siargo_customer` VALUES (2008721832280969216, '上海华械自动化s');
INSERT INTO `siargo_customer` VALUES (2008722775034679296, '成都锐自达c');
INSERT INTO `siargo_customer` VALUES (2008773890749091840, '天津亚科t');
INSERT INTO `siargo_customer` VALUES (2008773995162095616, '上海星辉sh');
INSERT INTO `siargo_customer` VALUES (2008792751502381056, '法国f');
INSERT INTO `siargo_customer` VALUES (2008802273482625024, '北京华仪泰兴b');
INSERT INTO `siargo_customer` VALUES (2008802664131710976, '北京中智核安b');
INSERT INTO `siargo_customer` VALUES (2008802763016622080, '日照华兴r');
INSERT INTO `siargo_customer` VALUES (2008803112767049728, '武汉恒业通w');
INSERT INTO `siargo_customer` VALUES (2008803436777033728, 'QMT Teach AB(瑞典r)');
INSERT INTO `siargo_customer` VALUES (2009090038669627392, '湖南蓝天智能h');
INSERT INTO `siargo_customer` VALUES (2009091257458872320, '无锡众志晟自动化w');
INSERT INTO `siargo_customer` VALUES (2009149718863400960, '瑞士rs');
INSERT INTO `siargo_customer` VALUES (2009193617589915648, '巴西b');
INSERT INTO `siargo_customer` VALUES (2009223836984004608, '中钧科技z');
INSERT INTO `siargo_customer` VALUES (2009436221791391744, 'Queensland Health(澳大利亚a)');
INSERT INTO `siargo_customer` VALUES (2009468509057044480, '洛斯贝流体(温州w)');
INSERT INTO `siargo_customer` VALUES (2009505047618899968, '煜昌荣茂yc');
INSERT INTO `siargo_customer` VALUES (2009511454766387200, '山东新高工业s');
INSERT INTO `siargo_customer` VALUES (2009515167178412032, '易镜医疗y');
INSERT INTO `siargo_customer` VALUES (2009520427271835648, '深圳贝塔利s');
INSERT INTO `siargo_customer` VALUES (2009549818810978304, '山东智高流体s');
INSERT INTO `siargo_customer` VALUES (2009553606242324480, '北京华远b');
INSERT INTO `siargo_customer` VALUES (2009802341484449792, '迈瑞m');
INSERT INTO `siargo_customer` VALUES (2009812759917481984, '德国d');
INSERT INTO `siargo_customer` VALUES (2009898353339256832, '河北谊安h');
INSERT INTO `siargo_customer` VALUES (2010583151636500480, '青岛恒远q');
INSERT INTO `siargo_customer` VALUES (2010617080661790720, '兰州科海l');
INSERT INTO `siargo_customer` VALUES (2010625095762825216, '广东嘉润医工建筑g');
INSERT INTO `siargo_customer` VALUES (2010625260645109760, '汉中易方物资h');
INSERT INTO `siargo_customer` VALUES (2010625485929566208, '长沙扬诺医疗c');
INSERT INTO `siargo_customer` VALUES (2010625640707772416, '北京卓镭激光b');
INSERT INTO `siargo_customer` VALUES (2010889086942695424, '深圳捷力s');
INSERT INTO `siargo_customer` VALUES (2010889406141812736, '成都鸿瑞c');
INSERT INTO `siargo_customer` VALUES (2010897286836375552, '南京霍普斯n');
INSERT INTO `siargo_customer` VALUES (2010905350503256064, '信安诺医药x');
INSERT INTO `siargo_customer` VALUES (2010905611992944640, '沃迈(上海s)');
INSERT INTO `siargo_customer` VALUES (2010907066472714240, '西安云仪x');
INSERT INTO `siargo_customer` VALUES (2010907919644479488, '深圳准控s');
INSERT INTO `siargo_customer` VALUES (2010953250197327872, '北京唯若b');
INSERT INTO `siargo_customer` VALUES (2010955747762753536, 'TCB Energy Services,LLC(美国)');
INSERT INTO `siargo_customer` VALUES (2010955877500964864, '湖南远利恒泰医疗h');
INSERT INTO `siargo_customer` VALUES (2010989045750812672, '土耳其t');
INSERT INTO `siargo_customer` VALUES (2010989422000852992, '安克创新a');
INSERT INTO `siargo_customer` VALUES (2010989961023442944, '杭州微标h');
INSERT INTO `siargo_customer` VALUES (2010990688588386304, '武汉天虹w');
INSERT INTO `siargo_customer` VALUES (2010991169763135488, '莫帝斯燃烧m');
INSERT INTO `siargo_customer` VALUES (2011247058478813184, '重庆休顿c');
INSERT INTO `siargo_customer` VALUES (2011260144535326720, '西班牙x');
INSERT INTO `siargo_customer` VALUES (2011308729838718976, '广州熙福医疗器械g');
INSERT INTO `siargo_customer` VALUES (2011321380811689984, '未来之芯w');
INSERT INTO `siargo_customer` VALUES (2011344161783795712, '江苏泰亨j');
INSERT INTO `siargo_customer` VALUES (2011385065982775296, '意大利y');
INSERT INTO `siargo_customer` VALUES (2011620456857980928, '天津森罗t');
INSERT INTO `siargo_customer` VALUES (2011620672684281856, 'OHK Medical 以色列');
INSERT INTO `siargo_customer` VALUES (2011621384457670656, '佛山特种医用导管f');
INSERT INTO `siargo_customer` VALUES (2011622586750717952, '鸿盛芯创h');
INSERT INTO `siargo_customer` VALUES (2011625849470439424, '沈阳科汇仪器s');
INSERT INTO `siargo_customer` VALUES (2011969728602296320, '广东凌腾科技g');
INSERT INTO `siargo_customer` VALUES (2011985594622529536, '石家庄星测s');
INSERT INTO `siargo_customer` VALUES (2011986157196136448, '珠海安联z');
INSERT INTO `siargo_customer` VALUES (2012078681608802304, '湖南微分医疗h');
INSERT INTO `siargo_customer` VALUES (2012078806397734912, '上海隆达锐医疗s');
INSERT INTO `siargo_customer` VALUES (2013071008288591872, '安丰固a');
INSERT INTO `siargo_customer` VALUES (2013071680379670528, '上海进申s');
INSERT INTO `siargo_customer` VALUES (2013072266374270976, '苏州宝帆s');
INSERT INTO `siargo_customer` VALUES (2013084556410605568, '湖南泰瑞医疗h');
INSERT INTO `siargo_customer` VALUES (2013124143279362048, '北京中惠b');
INSERT INTO `siargo_customer` VALUES (2013138726744346624, '福州德为流体f');
INSERT INTO `siargo_customer` VALUES (2013427593464631296, '重庆川瀚医疗c');
INSERT INTO `siargo_customer` VALUES (2013429632307417088, '东莞达辉精密塑胶d');
INSERT INTO `siargo_customer` VALUES (2013438667874226176, '深圳德林科s');
INSERT INTO `siargo_customer` VALUES (2013438920664928256, '广东永力g');
INSERT INTO `siargo_customer` VALUES (2013536659369218048, '湖北南控h');

-- ----------------------------
-- Table structure for siargo_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `siargo_dictionary`;
CREATE TABLE `siargo_dictionary`  (
  `id` bigint NOT NULL,
  `type_id` bigint NULL DEFAULT NULL,
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'siargo数据字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_dictionary
-- ----------------------------
INSERT INTO `siargo_dictionary` VALUES (1, 1, '1', '传感器');
INSERT INTO `siargo_dictionary` VALUES (2, 1, '2', '小流量计');
INSERT INTO `siargo_dictionary` VALUES (3, 1, '3', '大流量计');
INSERT INTO `siargo_dictionary` VALUES (4, 2, '1', '产成品');
INSERT INTO `siargo_dictionary` VALUES (5, 2, '2', '退修品');
INSERT INTO `siargo_dictionary` VALUES (6, 3, '1', '精度待检');
INSERT INTO `siargo_dictionary` VALUES (7, 3, '2', '成品待检');
INSERT INTO `siargo_dictionary` VALUES (8, 3, '3', '包装待检');
INSERT INTO `siargo_dictionary` VALUES (9, 3, '4', '合格待批准');
INSERT INTO `siargo_dictionary` VALUES (10, 3, '5', '已完成');
INSERT INTO `siargo_dictionary` VALUES (11, 4, '1', '1-70m3/h');
INSERT INTO `siargo_dictionary` VALUES (12, 4, '2', '1-100m3/h');
INSERT INTO `siargo_dictionary` VALUES (13, 4, '3', '3-160m3/h');
INSERT INTO `siargo_dictionary` VALUES (14, 4, '4', '0.8-80m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (15, 4, '6', '0.65-65m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (16, 4, '7', '0.40-40m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (17, 4, '8', '0.25-25m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (18, 4, '9', '0.10-10m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (19, 4, '10', '4-240m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (20, 4, '11', '100-10000m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (21, 4, '12', '250-25000m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (22, 4, '13', '25-2500m3/h');
INSERT INTO `siargo_dictionary` VALUES (23, 4, '14', '15-1500m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (24, 4, '15', '4-400m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (25, 4, '16', '10-1000m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (26, 4, '17', '1.6-160m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (27, 4, '18', '2.5-250m3/h\r\n');
INSERT INTO `siargo_dictionary` VALUES (28, 5, '2', 'G/2');
INSERT INTO `siargo_dictionary` VALUES (29, 4, '19', '20-1333SLPM');

-- ----------------------------
-- Table structure for siargo_dictionarytype
-- ----------------------------
DROP TABLE IF EXISTS `siargo_dictionarytype`;
CREATE TABLE `siargo_dictionarytype`  (
  `id` bigint NOT NULL,
  `dict_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'siargo数据字典类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_dictionarytype
-- ----------------------------
INSERT INTO `siargo_dictionarytype` VALUES (1, 'prod_type', '产品类型');
INSERT INTO `siargo_dictionarytype` VALUES (2, 'rep_type', '报告单类型');
INSERT INTO `siargo_dictionarytype` VALUES (3, 'insp', '检验进度');
INSERT INTO `siargo_dictionarytype` VALUES (4, 'flow_range', '流量范围');
INSERT INTO `siargo_dictionarytype` VALUES (5, 'pdfver', '报告单版号');

-- ----------------------------
-- Table structure for siargo_product
-- ----------------------------
DROP TABLE IF EXISTS `siargo_product`;
CREATE TABLE `siargo_product`  (
  `id` bigint NOT NULL COMMENT '产品ID',
  `report_id` bigint NOT NULL COMMENT '检验报告单ID',
  `type` int(1) UNSIGNED ZEROFILL NOT NULL COMMENT '产品类型:\r\n1传感器, \r\n2小流量, \r\n3大流量',
  `modle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品型号',
  `qsi` int NOT NULL COMMENT '送检数量 (Quantity Submitted for Inspection)',
  `qi` int UNSIGNED NOT NULL COMMENT '检验数量 (Quantity Inspected)',
  `number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品编号',
  `flow_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流量范围(Flow Range)',
  `des` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `vd` int NOT NULL COMMENT '有效数据(valid data)',
  `insp` tinyint NOT NULL COMMENT '检验进度(Inspection Progress)：\r\n1未开始检验, \r\n2精度合格, \r\n3功能合格, \r\n4外观合格, \r\n5全部合格',
  `accq_time` datetime NULL DEFAULT NULL COMMENT '精度检验日期(Accuracy qualified)',
  `funq_time` datetime NULL DEFAULT NULL COMMENT '功能检验日期(Function qualified)',
  `appq_time` datetime NULL DEFAULT NULL COMMENT '外观检验日期(Appearance qualified)',
  `allq_time` datetime NULL DEFAULT NULL COMMENT '最终审核日期(All qualified)',
  `accq_uid` bigint UNSIGNED NULL DEFAULT NULL COMMENT '精度合格检验人',
  `funq_uid` bigint UNSIGNED NULL DEFAULT NULL COMMENT '功能合格检验人',
  `appq_uid` bigint UNSIGNED NULL DEFAULT NULL COMMENT '外观合格检验人',
  `allq_uid` bigint UNSIGNED NULL DEFAULT NULL COMMENT '最终审核人',
  `pdfstr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'pdf地址',
  `pdfver` int NULL DEFAULT NULL COMMENT 'PDF报告单版本号',
  `cuc` double(4, 2) NULL DEFAULT NULL COMMENT '整机最大电流 8v(Complete Unit Current)',
  `cucmax` double(4, 2) NULL DEFAULT NULL COMMENT '整机电流24v',
  `cucmin` double(4, 2) NULL DEFAULT NULL COMMENT '整机电流12v',
  `pv` double(5, 3) NULL DEFAULT NULL COMMENT '脉冲电压(Pulse Voltage)',
  `thv` int NULL DEFAULT NULL COMMENT '热头电压(Thermal Head Voltage)',
  `zp` int NULL DEFAULT NULL COMMENT '零点内码(Zero Point)',
  `fl` double(4, 2) NULL DEFAULT NULL COMMENT '故障电平(Fault Level)',
  `bv` double(5, 4) NULL DEFAULT NULL COMMENT '电池电压(Battery Voltage)',
  `la` int NULL DEFAULT NULL COMMENT '本地地址(Local Address)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_repid`(`report_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单里的产品型号，跟报告单多对一关系' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_product
-- ----------------------------
INSERT INTO `siargo_product` VALUES (2007720620630855680, 2007720620622467072, 2, 'MF4710-G4-100-BV-O', 1, 1, 'A1V04387', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:16:13', '2026-01-04 17:25:24', '2026-01-05 09:43:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007720844665409536, 2007720844652826624, 2, 'MF4708-R3-20-AB-B', 1, 1, 'A1V04401', NULL, 'Co2实标，CF980', NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:16:13', '2026-01-04 17:25:24', '2026-01-05 13:06:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721015558131712, 2007721015553937408, 2, 'MF5708-G-100-B-A', 2, 2, 'PEEUL19995-PEEUL19996', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:16:13', '2026-01-04 17:25:24', '2026-01-05 09:49:20', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721171728846848, 2007721171716263936, 2, 'MF5619-N-800-ABD-D-O', 1, 1, 'G6HUL22656', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:17:00', '2026-01-04 17:25:24', '2026-01-05 09:53:09', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721325462671360, 2007721325454282752, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GUH39473', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:17:00', '2026-01-04 17:25:24', '2026-01-05 09:50:48', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721438180397056, 2007721438176202752, 2, 'MF5612-N-200-B-D-A', 1, 1, 'G6GUL22657', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:17:00', '2026-01-04 17:25:24', '2026-01-05 09:53:09', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007737171346051072, 2007737171337662464, 3, 'MF2032Be-AB-O', 1, 1, 'GLEUL10603', '3', NULL, NULL, 1, 5, '2026-01-04 16:53:38', '2026-01-04 16:53:52', '2026-01-04 17:15:02', '2026-01-05 13:06:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, '/PDF/G2/102667_2007737171346051072.pdf', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008001595801718785, 2008001595801718784, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V02603-A1V02752', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:38:23', '2026-01-07 14:44:29', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008001595852050432, 2008001595801718784, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V02753-A1V02902', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:38:23', '2026-01-07 14:44:29', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008002955599269888, 2008002955578298368, 1, 'FS4308-20-R-EV-A', 1, 1, 'A1V04779', NULL, '', NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:08:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008002955611852800, 2008002955578298368, 1, 'FS4308-20-R-EV-B', 1, 1, 'A1V04780', NULL, 'CO2测，系数980', NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:08:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008003805612724224, 2008003805579169792, 1, 'FS6122-250F250-5P100-TH1', 500, 80, 'A1V03887-A1V04386', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:07', '2026-01-07 14:44:13', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008004576886509569, 2008004576886509568, 1, 'FS4001E-30-CV-A', 4, 4, 'A1V04552-A1V04555', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:40:02', '2026-01-05 15:40:11', '2026-01-05 17:06:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008018603964485633, 2008018603964485632, 1, 'FS5001E-500-EV-H', 1, 1, 'A1V04508', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:05:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008018603964485634, 2008018603964485632, 1, 'FS5001E-200-EV-H', 1, 1, 'A1V04509', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:05:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, '', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008018604006428672, 2008018603964485632, 1, 'FS5001E-1000-EV-A', 1, 1, 'A1V04510', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:05:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008018604006428673, 2008018603964485632, 1, 'FS5001E-200-EV-A', 1, 1, 'A1V04511', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:05:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008069976974282752, 2008069976961699840, 1, 'MF3000S-100-O8-BVN-A', 11, 11, 'A1V04389-A1V04399', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:50', '2026-01-07 14:43:21', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008070300078297089, 2008070300078297088, 1, 'FS4008-30-O6-V-A', 4, 4, 'A1V04775-A1V04778', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:50', '2026-01-07 14:43:21', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008070586138218496, 2008070586134024192, 1, 'MF4003-2-R-BV-A', 2, 2, 'A1V04736-A1V04737', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:50', '2026-01-07 14:43:21', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008070960031059969, 2008070960031059968, 1, 'FS4008-20-O6-BV-A', 1, 1, 'A1V04400', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:50', '2026-01-07 14:43:21', '2026-01-07 14:54:00', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008074146280230912, 2008074146267648000, 1, 'AM1000-30-BV', 1, 1, 'A1V04738', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:50', '2026-01-07 14:43:21', '2026-01-07 14:54:00', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008085860371517441, 2008085860371517440, 2, 'MF5712-N-250-B-A', 4, 4, 'PEGUL19997-PEGUL20000', NULL, NULL, NULL, 1, 5, '2026-01-05 15:59:12', '2026-01-05 16:53:40', '2026-01-07 14:43:21', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008086356880642049, 2008086356880642048, 2, 'MF5712-N-250-B-A', 80, 80, 'PEGUL20009-PEGUL20088', NULL, '', NULL, 1, 5, '2026-01-05 16:01:10', '2026-01-06 10:27:17', '2026-01-07 14:43:21', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008086356922585088, 2008086356880642048, 2, 'MF5712-G-250-B-R', 30, 30, 'PEGUL20089-PEGUL20118', NULL, 'D9 GCF1000', NULL, 1, 5, '2026-01-05 16:01:10', '2026-01-06 09:47:28', '2026-01-07 14:43:21', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008086670438420480, 2008086670430031872, 2, 'MF5212-E-Q-400-C', 30, 30, 'G7GUL43657-G7GUL43686', NULL, NULL, NULL, 1, 5, '2026-01-05 16:02:25', '2026-01-06 10:24:45', '2026-01-07 14:43:20', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008086670438420481, 2008086670430031872, 2, 'MF5212-E-Q-400-F', 10, 10, 'G7GUL43687-G7GUL43696', NULL, NULL, NULL, 1, 5, '2026-01-05 16:02:25', '2026-01-06 10:24:30', '2026-01-07 14:43:20', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008086983723569152, 2008086983698403328, 2, 'MF5219-E-Y-1000-F', 8, 8, 'G7HUL43709-G7HUL43716', NULL, NULL, NULL, 1, 5, '2026-01-05 16:03:40', '2026-01-05 16:53:40', '2026-01-07 14:43:20', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008087241442578432, 2008087241400635392, 2, 'MF5612-N-200-AB-D-A', 2, 2, 'G6GUL22658-G6GUL22659', NULL, NULL, NULL, 1, 5, '2026-01-05 16:04:41', '2026-01-05 16:53:40', '2026-01-07 14:43:20', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008087427757756417, 2008087427757756416, 2, 'MF5012-N4F-e-3-300-15-AB-D-A', 1, 1, 'G5GUL11748', NULL, NULL, NULL, 1, 3, '2026-01-05 16:05:25', '2026-01-06 09:47:08', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008100046124208129, 2008100046124208128, 2, 'MF5212-E-Q-300-C', 1, 1, 'G7GUD35560', NULL, NULL, NULL, 1, 5, '2026-01-05 16:55:34', '2026-01-05 16:57:29', '2026-01-07 14:43:20', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008118855530827776, 2008118855526633472, 1, 'FS34000H-20-V-C', 1500, 125, 'D3U16001-D3U17500', NULL, '系数540', NULL, 1, 5, '2026-01-05 18:10:18', '2026-01-07 11:44:14', '2026-01-07 14:34:35', '2026-01-07 14:54:00', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008366000846524416, 2008366000812969984, 2, 'MF5612-N-300-AB-N-A', 1, 1, 'G6GUL22653', NULL, NULL, NULL, 1, 5, '2026-01-06 10:32:22', '2026-01-06 10:34:53', '2026-01-07 14:41:06', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008369350916820992, 2008369350900043776, 2, 'MF5712-G-250-B-A', 2, 2, 'PEGUL20005-PEGUL20006', NULL, NULL, NULL, 1, 5, '2026-01-06 10:45:41', '2026-01-07 09:11:06', '2026-01-07 14:41:06', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008369541115924480, 2008369541111730176, 2, 'MF5712-G-250-B-A', 1, 1, 'PAGTI11402', NULL, NULL, NULL, 1, 5, '2026-01-06 10:46:26', '2026-01-07 09:11:06', '2026-01-07 14:41:06', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008369953478922240, 2008369953466339328, 2, 'MF5708-G-100-B-O', 3, 3, 'PEEUL20002-PEEUL20004', NULL, NULL, NULL, 1, 5, '2026-01-06 10:48:05', '2026-01-07 09:11:06', '2026-01-07 14:41:06', '2026-01-07 14:54:00', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008370118755471360, 2008370118747082752, 2, 'MF5712-G-250-B-A', 2, 2, 'PEGUI17400-PEGUI17515', NULL, NULL, NULL, 1, 5, '2026-01-06 10:48:44', '2026-01-07 09:11:22', '2026-01-07 14:41:06', '2026-01-07 14:53:53', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008372492584734720, 2008372492580540416, 1, 'FS7001E', 10, 10, 'A1V07056-A1V07065', NULL, NULL, NULL, 1, 5, '2026-01-06 10:58:10', '2026-01-07 11:44:06', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008406087240110080, 2008406087223332864, 1, 'FS4008-20-O8-BV-O', 50, 13, 'A1V02308-2357', NULL, NULL, NULL, 1, 5, '2026-01-06 13:11:40', '2026-01-07 11:44:06', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008414090806808577, 2008414090806808576, 1, 'FS4308-2-O6-BV-A', 7, 7, 'A1V07082-A1V07088', NULL, NULL, NULL, 1, 5, '2026-01-06 13:43:28', '2026-01-07 11:44:06', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008414232138076160, 2008414232125493248, 1, 'MF4008-20-O8-BV-A', 5, 5, 'A1V07066-A1V07070', NULL, NULL, NULL, 1, 5, '2026-01-06 13:44:02', '2026-01-07 11:43:18', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008414727938363392, 2008414727925780480, 1, 'MF4008-30-O6-V-A', 1, 1, 'A1V07081', NULL, NULL, NULL, 1, 5, '2026-01-06 13:46:00', '2026-01-07 11:43:18', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008414883563819008, 2008414883555430400, 1, 'MF4008-50-R-BV-A', 10, 10, 'A1V04726-A1V04735', NULL, NULL, NULL, 1, 5, '2026-01-06 13:46:37', '2026-01-07 11:43:18', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008415562407727104, 2008415562399338496, 1, 'MF4008-50-R-BV-A', 2, 2, 'A1V07090-A1V07091', NULL, NULL, NULL, 1, 5, '2026-01-06 13:49:19', '2026-01-07 11:44:06', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008417873511174144, 2008417873494396928, 1, 'FS8001', 120, 13, 'A1V03593-3712', NULL, NULL, NULL, 1, 5, '2026-01-06 13:58:30', '2026-01-07 14:29:51', '2026-01-07 17:05:57', '2026-01-08 09:18:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008704659743428608, 2008704659730845696, 1, 'FS1015E-100-ISO-EV-A', 10, 10, 'A1V07071-A1V07080', NULL, NULL, NULL, 1, 5, '2026-01-07 08:58:05', '2026-01-07 11:42:54', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008704992787943424, 2008704992779554816, 1, 'FS1015E-100-ISO-EV-A', 1, 1, 'A1V07089', NULL, NULL, NULL, 1, 5, '2026-01-07 08:59:24', '2026-01-07 11:42:54', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008705604070002688, 2008705604053225472, 1, 'FS4308-40-R-CV-A-0.5/4.5V', 100, 13, 'A1V02403-2502', NULL, NULL, NULL, 1, 5, '2026-01-07 09:01:50', '2026-01-07 11:41:32', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008705604074196992, 2008705604053225472, 1, 'FS4308-40-R-CV-A-0.5/4.5V', 100, 13, 'A1V02503-2602', NULL, NULL, NULL, 1, 5, '2026-01-07 09:01:50', '2026-01-07 11:41:32', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008706691216822272, 2008706691204239360, 1, 'MF3000M-100-R-BVZ-A', 30, 14, 'A1V02358-2387', NULL, NULL, NULL, 1, 5, '2026-01-07 09:06:09', '2026-01-07 11:42:35', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008706691233599488, 2008706691204239360, 1, 'MF3000M-200-R-BVZ-A', 15, 15, 'A1V02388-A1V02402', NULL, NULL, NULL, 1, 5, '2026-01-07 09:06:09', '2026-01-07 11:42:35', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008712104561725440, 2008712104553336832, 1, 'FS4001E-30-EV-A', 10, 10, 'A1V07046-A1V07055', NULL, NULL, NULL, 1, 5, '2026-01-07 09:27:40', '2026-01-07 17:05:17', '2026-01-07 17:05:57', '2026-01-08 09:18:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008718895412531200, 2008718895395753984, 2, 'MFC2000-050-K3-AB-1-C3H8', 1, 1, 'MC2UL11155', NULL, 'GCF 286', NULL, 1, 5, '2026-01-07 09:54:39', '2026-01-07 14:47:42', '2026-01-08 14:45:39', '2026-01-08 15:58:29', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008720014771605504, 2008720014754828288, 2, 'MFC2000-100-K4-BV-1-A', 1, 1, 'MC2VA11156', NULL, NULL, NULL, 1, 5, '2026-01-07 09:59:06', '2026-01-07 14:47:42', '2026-01-08 14:45:39', '2026-01-08 15:58:29', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008720216865755136, 2008720216857366528, 2, 'MF4710-G4-100-BV-A', 1, 1, 'A1V05937', NULL, NULL, NULL, 1, 5, '2026-01-07 09:59:54', '2026-01-07 14:47:42', '2026-01-08 14:44:14', '2026-01-08 15:58:29', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008720647998263296, 2008720647989874688, 2, 'MF4719-N6F-500-V-N', 1, 1, 'A1V05938', NULL, NULL, NULL, 1, 5, '2026-01-07 10:01:37', '2026-01-07 11:32:58', '2026-01-07 14:33:51', '2026-01-07 14:53:53', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008720833931759616, 2008720833919176704, 2, 'MF4708-N3-10-BV-A', 1, 1, 'A1V07101', NULL, NULL, NULL, 1, 5, '2026-01-07 10:02:21', '2026-01-07 14:47:42', '2026-01-08 14:44:14', '2026-01-08 15:58:29', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008721050554978304, 2008721050542395392, 2, 'FS4703-N1-1000-BV-A', 2, 2, 'A1V05935-A1V05936', NULL, NULL, NULL, 1, 5, '2026-01-07 10:03:13', '2026-01-07 14:47:42', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008721243891421184, 2008721243883032576, 2, 'MFC2000-0050-P6-BA-1-A', 1, 1, 'MC2VA11157', NULL, NULL, NULL, 1, 5, '2026-01-07 10:03:59', '2026-01-07 14:47:42', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008722349891637248, 2008722349874860032, 2, 'BC-C1000-O2-485-NPT', 1, 1, 'B014565', NULL, NULL, NULL, 1, 5, '2026-01-07 10:08:23', '2026-01-07 14:47:42', '2026-01-07 15:39:48', '2026-01-07 16:17:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008722349895831552, 2008722349874860032, 2, 'BC-C1000-N2-485-NPT', 2, 2, 'B020455/B021682', NULL, NULL, NULL, 1, 5, '2026-01-07 10:08:23', '2026-01-07 14:47:42', '2026-01-07 15:39:48', '2026-01-07 16:17:12', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008722967519678464, 2008722967511289856, 1, 'FS6122-250F250-40P40-TH0', 7, 7, 'A1V05939-A1V05945', NULL, NULL, NULL, 1, 5, '2026-01-07 10:10:50', '2026-01-07 17:05:17', '2026-01-07 17:05:57', '2026-01-08 09:18:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008774196044091392, 2008774196031508480, 1, 'MF3000M-500-R-BAN-A', 1, 1, 'D2U11505', NULL, NULL, NULL, 1, 5, '2026-01-07 13:34:24', '2026-01-07 17:05:17', '2026-01-07 17:05:57', '2026-01-08 09:18:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008774367440130048, 2008774367431741440, 1, 'MF3000M-500-R-BAN-A', 1, 1, 'A1V07143', NULL, NULL, NULL, 1, 5, '2026-01-07 13:35:05', '2026-01-07 17:05:17', '2026-01-07 17:05:57', '2026-01-08 09:18:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008793136522711040, 2008793136510128128, 1, 'FS8001-200-EV-A', 130, 14, 'A1V03321-3450', NULL, NULL, NULL, 1, 5, '2026-01-07 14:49:39', '2026-01-07 15:38:35', '2026-01-08 11:35:31', '2026-01-08 16:02:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008793294060769280, 2008793294048186368, 2, 'MF25HD-G4-B-T-LPG', 4, 4, 'GHLVA10709-GHLVA10712', NULL, 'GCF286', NULL, 1, 5, '2026-01-07 14:50:17', '2026-01-07 15:45:39', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008793294064963584, 2008793294048186368, 2, 'MF25HD-G2.5-B-B-LPG', 4, 4, 'GHLVA10713-GHLVA10716', NULL, 'GCF286', NULL, 1, 5, '2026-01-07 14:50:17', '2026-01-08 10:28:16', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008804015788118016, 2008804015779729408, 2, 'MF5212-E-Q-300-C', 1, 1, 'G7GVA43717', NULL, NULL, NULL, 1, 5, '2026-01-07 15:32:53', '2026-01-08 09:10:14', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008804255417094144, 2008804255408705536, 2, 'MF5619-Y-800-ABD-D-O', 4, 4, 'G6HVA22670-G6HVA22673', NULL, NULL, NULL, 1, 5, '2026-01-07 15:33:50', '2026-01-08 09:10:14', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008804621789548544, 2008804621781159936, 2, 'MF5619-N-600-A-D-O', 5, 5, 'G6HVA22662-G6HVA22666', NULL, NULL, NULL, 1, 5, '2026-01-07 15:35:18', '2026-01-08 09:10:13', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008804621797937152, 2008804621781159936, 2, 'MF5619-N-800-A-D-O', 2, 2, 'G6HVA22667-G6HVA22668', NULL, NULL, NULL, 1, 5, '2026-01-07 15:35:18', '2026-01-08 09:10:14', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008804888039772160, 2008804888027189248, 2, 'MF5619-N-48m3/h-ABD-D-O', 1, 1, 'G6HUL22660', NULL, NULL, NULL, 1, 5, '2026-01-07 15:36:21', '2026-01-08 09:10:13', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008805183645929472, 2008805183637540864, 2, 'MF5612-N-200-AB-D-A', 1, 1, 'G6GUL22661', NULL, NULL, NULL, 1, 5, '2026-01-07 15:37:32', '2026-01-08 09:10:13', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008805427762810880, 2008805427750227968, 2, 'MF5612-G4M-200-ABD-N-A', 1, 1, 'G6GVA22669', NULL, NULL, NULL, 1, 3, '2026-01-07 15:38:30', '2026-01-08 09:10:13', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008805925383426048, 2008805925375037440, 2, 'MF5212-E-Q-400-C', 1, 1, 'G7GTC20588', NULL, NULL, NULL, 1, 5, '2026-01-07 15:40:29', '2026-01-08 09:10:13', '2026-01-08 14:44:13', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008805925387620352, 2008805925375037440, 2, 'MF5212-E-Q-400-C', 9, 9, 'G7GTG24290/G7GTG24400/G7GTG24423/G7GTK28433/G7GUA32190/G7GUA32196/G7GUA32249/G7GUA32269/G7GUH39106', NULL, NULL, NULL, 1, 5, '2026-01-07 15:40:29', '2026-01-08 09:10:13', '2026-01-08 14:44:14', '2026-01-08 16:02:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008822951703334912, 2008822951699140608, 1, 'FS4003-2-O4-CV-E', 5, 5, 'A1V05788-A1V05792', NULL, 'E是实标', NULL, 1, 5, '2026-01-07 16:48:08', '2026-01-08 11:34:53', '2026-01-08 14:40:29', '2026-01-08 16:02:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008822951711723520, 2008822951699140608, 1, 'FS4003-5-O4-CV-A', 20, 20, 'A1V05793-A1V05812', NULL, '', NULL, 1, 5, '2026-01-07 16:48:08', '2026-01-08 11:34:53', '2026-01-08 14:40:29', '2026-01-08 16:02:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009089756351025152, 2009089756325859328, 2, 'MF5712-G-250-B-O', 4, 4, 'PEGVA20124-PEGVA20127', NULL, NULL, NULL, 1, 5, '2026-01-08 10:28:19', '2026-01-08 16:43:44', '2026-01-09 14:09:26', '2026-01-09 17:18:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009090473577009152, 2009090473564426240, 2, 'MF5708-G-100-B-C20R80', 2, 2, 'PEEUH17361/PEEUH17373', NULL, 'GCF 908；定制程序0.2.0.3', NULL, 1, 5, '2026-01-08 10:31:10', '2026-01-08 16:43:44', '2026-01-09 14:09:26', '2026-01-09 17:18:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009090992701820928, 2009090992689238016, 2, 'MF5706-G-20-B-He90A10', 1, 1, 'PEDVA20121', NULL, 'GCF 3880', NULL, 1, 5, '2026-01-08 10:33:14', '2026-01-08 16:43:44', '2026-01-09 14:09:26', '2026-01-09 17:18:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009091397330522112, 2009091397326327808, 2, 'MF5712-G-250-B-A', 2, 2, 'PEGVA20119-PEGVA20120', NULL, NULL, NULL, 1, 5, '2026-01-08 10:34:50', '2026-01-08 16:43:43', '2026-01-09 14:09:26', '2026-01-09 17:18:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009149885167554560, 2009149885163360256, 1, 'PFLOW2001-2000-B-VI2C-A', 200, 200, 'A1V05588-A1V05787', NULL, NULL, NULL, 1, 5, '2026-01-08 14:29:43', '2026-01-09 14:01:55', '2026-01-09 14:09:59', '2026-01-09 17:16:55', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009161954528972800, 2009161954524778496, 1, 'FS4308-5-R-BV-A', 20, 13, 'A1V04512-A1V04531', NULL, '地址2，波特率9600', NULL, 1, 5, '2026-01-08 15:16:45', '2026-01-09 14:01:55', '2026-01-09 14:09:59', '2026-01-09 17:16:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009161954533167104, 2009161954524778496, 1, 'FS4308-20-R-BV-A', 20, 13, 'A1V04532-A1V04551', NULL, '地址5，波特率9600', NULL, 1, 5, '2026-01-08 15:16:45', '2026-01-09 14:01:55', '2026-01-09 14:09:59', '2026-01-09 17:16:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009184964229058560, 2009184964220669952, 3, 'MF50FD-E-（4.0-400Nm3/h)', 1, 1, 'GSAVA30021', '15', NULL, '2026-01-09 15:31:29', 0, 1, '2026-01-08 16:46:38', '2026-01-09 09:03:48', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009189961687748608, 2009189961683554304, 2, 'MF4719-R6F-1000-B-A', 4, 4, 'A1V04769-A1V04772', NULL, NULL, NULL, 1, 5, '2026-01-08 17:06:30', '2026-01-09 09:43:26', '2026-01-09 14:09:26', '2026-01-09 17:15:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009190089555300352, 2009190089546911744, 2, 'MF4719-R6F-1000-B-A', 2, 2, 'A1V04773-A1V04774', NULL, NULL, NULL, 1, 5, '2026-01-08 17:07:00', '2026-01-09 09:43:26', '2026-01-09 14:09:26', '2026-01-09 17:15:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009190207851450368, 2009190207847256064, 2, 'MF4719-R6F-1000-B-N', 1, 1, 'A1V05581', NULL, NULL, NULL, 1, 5, '2026-01-08 17:07:29', '2026-01-09 09:43:26', '2026-01-09 14:09:26', '2026-01-09 17:15:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009193824847253504, 2009193824843059200, 1, 'FS1100-0F140-C', 10, 10, 'A1V02092-A1V02101', NULL, '系数540', NULL, 1, 5, '2026-01-08 18:39:21', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:15:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009213738895134720, 2009213738890940416, 1, 'PNEU-053904', 4, 4, 'A1V05913-A1V05916', NULL, 'FS35001-F50000-V-A，滤波深度9', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:14:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318212403200, 2009214318208208896, 1, 'PNEU-054453', 5, 5, 'A1V07152-7156', NULL, 'FS35001-2000-F20000-V-A，滤波深度9', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318216597504, 2009214318208208896, 1, 'PNEU-054454', 5, 5, 'A1V07157-7161', NULL, 'FS35001-5000F50000-V-A，滤波深度9', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318220791808, 2009214318208208896, 1, 'PNEU-054154', 5, 5, 'A1V07147-7151', NULL, 'FS35001-5F10-V-A，滤波深度3', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318229180416, 2009214318208208896, 1, 'PNEU-057367', 5, 5, 'A1V07162-A1V07166', NULL, 'FS35001-10F10-V-A，滤波深度3', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318233374720, 2009214318208208896, 1, 'PNEU-054457', 5, 5, 'A1V07177-7181', NULL, 'FS35001-250F250-V-A，滤波深度3', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318237569024, 2009214318208208896, 1, 'PNEU-054458', 5, 5, 'A1V07187-7191', NULL, 'FS35001-500F500-V-A，滤波深度3', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318241763328, 2009214318208208896, 1, 'PNEU-054455', 5, 5, 'A1V07167-7171', NULL, 'FS35001-20F20-V-A，滤波深度3', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318245957632, 2009214318208208896, 1, 'PNEU-054456', 5, 5, 'A1V07172-7176', NULL, 'FS35001-50F50-V-A，滤波深度3', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318245957633, 2009214318208208896, 1, 'PNEU-054459', 5, 5, 'A1V07182-7186', NULL, 'FS35001-1000F1000-V-A，滤波深度9', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318245957634, 2009214318208208896, 1, 'PNEU-054460', 5, 5, 'A1V12587-A1V12591', NULL, 'FS35001-3000F3000-V-A，滤波深度9', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009214318250151936, 2009214318208208896, 1, 'PNEU-054461', 5, 5, 'A1V12592-A1V12596', NULL, 'FS35001-10000F10000-V-A，滤波深度9', NULL, 1, 5, '2026-01-08 18:53:39', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:13:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009224146917904384, 2009224146913710080, 1, 'MF4308-5-O4-BV-EO', 35, 13, 'A1V03805-3839', NULL, '系数5500', NULL, 1, 5, '2026-01-08 19:22:46', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:13:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009230698815016960, 2009230698806628352, 1, 'MF4308-5-O4-BV-EO', 7, 7, 'A1V03840-A1V03846', NULL, '系数5500', NULL, 1, 5, '2026-01-08 19:48:35', '2026-01-09 14:01:55', '2026-01-09 14:07:57', '2026-01-09 17:12:30', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009431944851214336, 2009431944842825728, 3, 'MF40FD-E-(2.5-250Nm3/h)', 1, 1, 'GSFVA30001', '18', NULL, '2026-01-09 09:17:27', 0, 1, '2026-01-09 09:08:03', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009432419474460672, 2009432419470266368, 3, 'MF40FD-E-(2.5-250Nm3/h)', 1, 1, 'GSFVA30002', '18', NULL, '2026-01-09 09:17:27', 0, 1, '2026-01-09 09:09:56', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009433030316118016, 2009433030311923712, 3, 'MF40FD-E-(2.5-250Nm/3h)', 1, 1, 'GSFVA30003', '18', NULL, '2026-01-09 09:17:27', 0, 1, '2026-01-09 09:12:22', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009433415944622080, 2009433415940427776, 3, 'MF40FD-E-(2.5-250Nm3/h)', 1, 1, 'GSFVA30004', '18', NULL, '2026-01-09 09:15:01', 0, 1, '2026-01-09 09:13:54', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009434031597146112, 2009434031588757504, 3, 'MF40FD-E-(2.5-250Nm3/h)', 1, 1, 'GSFVA30004', '18', NULL, '2026-01-09 09:17:27', 0, 1, '2026-01-09 09:16:21', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009436880209039360, 2009436880204845056, 2, 'MF5706-N-20-B-A', 1, 1, 'PADVA20130', NULL, 'D6芯片；英文', NULL, 1, 5, '2026-01-09 11:34:45', '2026-01-09 13:33:52', '2026-01-12 14:47:11', '2026-01-12 16:33:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009436880217427968, 2009436880204845056, 2, 'MF5712-N-200-B-A', 1, 1, 'PEGVA20131', NULL, 'D9芯片；英文', NULL, 1, 5, '2026-01-09 11:34:45', '2026-01-09 13:33:52', '2026-01-12 14:47:11', '2026-01-12 16:33:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009437832135692288, 2009437832131497984, 2, 'MF5708-G-100-B-A', 2, 2, 'PAERA65046/PAESC73928', NULL, NULL, NULL, 1, 5, '2026-01-09 11:34:45', '2026-01-09 13:33:52', '2026-01-12 14:47:10', '2026-01-12 16:33:43', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009438299695730688, 2009438299691536384, 2, 'MF5712-G-250-B-R80C20', 1, 1, 'PAGSC73074', NULL, 'D6芯片；GCF 1078', NULL, 1, 5, '2026-01-09 11:34:45', '2026-01-09 13:33:52', '2026-01-12 14:47:10', '2026-01-12 16:33:43', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009450005285163008, 2009450005280968704, 1, 'PFLOW1015CL-KS3', 3, 3, 'A1U01620，A1U01637，A1U01686', NULL, 'FS1015E-74.1-ISO-C，输出0.25-2.75V，零点±5mv，系数540，最小校准到0.3L不截流', NULL, 1, 5, '2026-01-09 10:28:25', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:11:41', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009451072936857600, 2009451072932663296, 1, 'FS8003P-6-EV', 1, 1, 'A1V07207', NULL, NULL, NULL, 1, 5, '2026-01-09 10:28:25', '2026-01-09 14:01:55', '2026-01-09 14:07:14', '2026-01-09 17:10:34', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009451591751290880, 2009451591747096576, 1, 'FS7001C', 700, 80, 'A1V08001-A1V08700', NULL, NULL, NULL, 1, 5, '2026-01-09 10:28:25', '2026-01-14 16:46:24', '2026-01-14 16:59:05', '2026-01-14 17:13:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009464589438013440, 2009464589433819136, 1, 'MF4003-2-O6-BV-A', 10, 10, 'A1V08701-A1V08710', NULL, NULL, NULL, 1, 5, '2026-01-09 11:18:36', '2026-01-12 14:44:08', '2026-01-12 14:53:26', '2026-01-12 16:33:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009464589442207744, 2009464589433819136, 1, 'MF4008-10-O8-BV-A', 10, 10, 'A1V08711-A1V08720', NULL, NULL, NULL, 1, 5, '2026-01-09 11:18:36', '2026-01-12 14:44:08', '2026-01-12 14:53:26', '2026-01-12 16:33:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009468817166422016, 2009468817162227712, 2, 'MF4719-G6F-1000-BA-A', 2, 2, 'A1V05582-A1V05583', NULL, NULL, NULL, 1, 5, '2026-01-09 11:34:45', '2026-01-09 11:36:51', '2026-01-12 14:47:10', '2026-01-12 16:33:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009468817170616320, 2009468817162227712, 2, 'MF4712-G4F-300-BA-A', 4, 4, 'A1V05584-A1V05587', NULL, NULL, NULL, 1, 5, '2026-01-09 11:34:45', '2026-01-09 11:36:51', '2026-01-12 14:47:10', '2026-01-12 16:33:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009505146562531328, 2009505146554142720, 1, 'MF3000S-100-R-BZZ-A', 2, 2, 'A1V07144-A1V07145', NULL, NULL, NULL, 1, 5, '2026-01-09 14:01:52', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:57', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009505314426966016, 2009505314422771712, 1, 'MF4008-50-O8-BV-A', 7, 7, 'A1V07208-A1V07214', NULL, NULL, NULL, 1, 5, '2026-01-09 14:01:52', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009505768288407552, 2009505768284213248, 1, 'FS4308-3-O6-V-A', 10, 10, 'A1V03291-A1V03300', NULL, NULL, NULL, 1, 5, '2026-01-09 14:01:52', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:23', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009505768292601856, 2009505768284213248, 1, 'FS4308-1-O6-V-C', 10, 10, 'A1V03301-A1V03310', NULL, NULL, NULL, 1, 5, '2026-01-09 14:01:52', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009505768292601857, 2009505768284213248, 1, 'FS4308-50-O8-V-A', 10, 10, 'A1V03311-A1V03320', NULL, NULL, NULL, 1, 5, '2026-01-09 14:01:52', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:35', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009511539101257728, 2009511539097063424, 2, 'MF5006-N2M-e-0.5-50-15-AB-D-A', 1, 1, 'G5DVA11749', NULL, NULL, NULL, 1, 5, '2026-01-09 14:24:20', '2026-01-09 15:26:58', '2026-01-12 14:47:10', '2026-01-12 16:32:17', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009512315815055360, 2009512315810861056, 2, 'MF5619-N-800-AB-D-A', 4, 4, 'G6HVA22674-G6HVA22677', NULL, NULL, NULL, 1, 5, '2026-01-09 14:27:25', '2026-01-09 15:26:58', '2026-01-12 14:47:10', '2026-01-12 16:32:12', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009515362188382208, 2009515362179993600, 1, 'FS4008-20-O8-CV-C', 100, 13, 'A1V04408-4507', NULL, NULL, NULL, 1, 5, '2026-01-09 15:00:45', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009520635640664064, 2009520635636469760, 1, 'FS4308-20-O8-EV-A', 30, 13, 'A1V04739-4768', NULL, NULL, NULL, 1, 5, '2026-01-09 15:00:45', '2026-01-12 14:43:34', '2026-01-12 14:53:26', '2026-01-12 16:32:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009530705774497792, 2009530705770303488, 1, 'MF6600-50SLPM', 10, 10, 'FAGVA17567-FAGVA17576', NULL, NULL, NULL, 1, 5, '2026-01-09 15:41:30', '2026-01-12 14:44:08', '2026-01-12 14:53:26', '2026-01-12 16:32:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009530930584997888, 2009530930576609280, 1, 'MF6600-2SLPM', 10, 10, 'FAGVA17557-FAGVA17566', NULL, NULL, NULL, 1, 5, '2026-01-09 15:41:30', '2026-01-12 14:44:08', '2026-01-12 14:53:26', '2026-01-12 16:32:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009549901870780416, 2009549901866586112, 2, 'MF4708-N3-50-BV-A', 1, 1, 'A1V07146', NULL, NULL, NULL, 1, 5, '2026-01-09 16:56:52', '2026-01-09 17:14:11', '2026-01-12 14:47:10', '2026-01-12 16:32:02', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009553868617011200, 2009553868608622592, 3, 'MF2032Be-AB-O', 1, 1, 'GLEVA10605', '3', NULL, NULL, 1, 5, '2026-01-09 17:12:32', '2026-01-09 17:13:37', '2026-01-12 14:47:10', '2026-01-12 16:32:02', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009554067351523328, 2009554067347329024, 3, 'MF2032Be-AB-O', 1, 1, 'GLEVA10604', '3', NULL, NULL, 1, 5, '2026-01-09 17:13:20', '2026-01-09 17:13:37', '2026-01-12 14:47:10', '2026-01-12 16:31:49', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009802603422928896, 2009802603418734592, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V08946-A1V08995', NULL, NULL, NULL, 1, 3, '2026-01-10 10:04:07', '2026-01-14 16:46:24', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009802931815960576, 2009802931811766272, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V08818-A1V08867', NULL, NULL, NULL, 1, 3, '2026-01-10 10:04:07', '2026-01-14 16:46:24', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009803327162667008, 2009803327158472704, 1, 'FS34008-20-O8-CV-A-500mesh', 78, 13, 'A1V08868-8945', NULL, NULL, NULL, 1, 2, '2026-01-10 10:04:07', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009808179628462081, 2009808179628462080, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 13, 'A1V08768-A1V08817', NULL, 'CO2测系数980', NULL, 1, 3, '2026-01-10 10:04:07', '2026-01-14 16:46:24', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009812912334295040, 2009812912330100736, 1, 'FS4308-3-O8-CV-A-0.5/4.5V', 100, 13, 'A1V05813-5912', NULL, NULL, NULL, 1, 5, '2026-01-10 10:22:04', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536837627904, 2009899536833433600, 1, 'FS4308-33-O8-BV-A', 2, 2, 'A1V05933-5934', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536841822208, 2009899536833433600, 1, 'FS1100-10F240', 2, 2, 'A1V05927-A1V05928', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536846016512, 2009899536833433600, 1, 'FS1100-24F240', 2, 2, 'A1V05929-A1V05930', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:22', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536850210816, 2009899536833433600, 1, 'FS1100-100F250', 2, 2, 'A1V05931-A1V05932', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536850210817, 2009899536833433600, 1, 'FSP1000-500-EV-U', 10, 10, 'A1V05917-5926', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010584432375615488, 2010584432367226880, 1, 'MF3000M-1500-G4F-BAP-A', 10, 10, 'A1V12232-A1V12241', NULL, NULL, NULL, 1, 5, '2026-01-12 13:29:49', '2026-01-12 14:42:59', '2026-01-12 17:25:12', '2026-01-13 09:29:32', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010584432379809792, 2010584432367226880, 1, 'FS6122-0F250-0P0-TH0', 1100, 80, 'A1V05946-7045', NULL, NULL, NULL, 1, 5, '2026-01-12 13:29:49', '2026-01-12 14:42:59', '2026-01-12 14:54:30', '2026-01-12 16:31:05', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, '/PDF/G2/102448_2010584432379809792.pdf', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010584432384004096, 2010584432367226880, 1, 'FS4308-15-O6-BV-B', 20, 20, 'A1V12242-A1V12261', NULL, NULL, NULL, 1, 5, '2026-01-12 13:29:49', '2026-01-12 15:40:25', '2026-01-12 17:20:52', '2026-01-13 09:29:32', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010584432388198400, 2010584432367226880, 1, 'FS4308-15-O6-BV-A', 20, 20, 'A1V12262-A1V12281', NULL, NULL, NULL, 1, 5, '2026-01-12 13:29:49', '2026-01-12 15:40:25', '2026-01-12 17:20:52', '2026-01-13 09:29:32', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010585483648552960, 2010585483644358656, 1, 'FS4001E-100-CV-A', 20, 14, 'A1V08999-9018', NULL, NULL, NULL, 1, 5, '2026-01-12 13:33:01', '2026-01-12 14:42:59', '2026-01-12 17:20:52', '2026-01-13 09:29:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010585754139217920, 2010585754135023616, 1, 'FS4001E-30-CV-A', 6, 6, 'A1V12221-A1V12226', NULL, NULL, NULL, 1, 5, '2026-01-12 13:33:01', '2026-01-12 14:42:59', '2026-01-12 17:20:52', '2026-01-13 09:29:13', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010610597974888448, 2010610597966499840, 1, 'PFLOW1015CL-KS3', 800, 80, 'A1V04781-5580', NULL, 'FS1015E-74.1-ISO-C，输出0.25-2.75V，零点±0.005V，精度1.5+0.2，重复性0.25，最小流量0.3L不截流', NULL, 1, 5, '2026-01-13 09:40:34', '2026-01-14 16:46:24', '2026-01-14 16:59:05', '2026-01-14 17:13:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010617249428000768, 2010617249423806464, 1, 'FS5001E-500-EV-H', 2, 2, 'A1V08997-A1V08998', NULL, '通讯地址4', NULL, 1, 5, '2026-01-13 09:40:34', '2026-01-13 11:08:46', '2026-01-13 11:14:48', '2026-01-13 13:07:36', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010617466311266304, 2010617466307072000, 1, 'FS5001E-100-EV-H', 2, 2, 'A1V12582-A1V12583', NULL, NULL, NULL, 1, 5, '2026-01-13 09:40:34', '2026-01-13 11:08:46', '2026-01-13 11:14:48', '2026-01-13 13:07:36', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010619106363822080, 2010619106359627776, 1, 'FS5001E-200-EV-A', 1, 1, 'D3U14686', NULL, '通讯地址4', NULL, 1, 5, '2026-01-13 11:11:24', '2026-01-13 11:19:11', '2026-01-13 11:20:37', '2026-01-13 13:07:36', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010626592542150656, 2010626592537956352, 2, 'MF5219-Q-800-A', 2, 2, 'G7HQH53031/G7HSL12846', NULL, NULL, NULL, 1, 5, '2026-01-12 16:15:09', '2026-01-13 10:38:25', '2026-01-13 15:00:13', '2026-01-13 17:21:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010626912592711680, 2010626912584323072, 2, 'MF5612-N-300-AB-N-A', 6, 6, 'G6GVA22704-G6GVA22709', NULL, NULL, NULL, 1, 5, '2026-01-12 16:16:26', '2026-01-14 15:27:34', '2026-01-14 16:59:05', '2026-01-14 17:13:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010626912596905984, 2010626912584323072, 2, 'MF5619-N-600-AB-N-A', 2, 2, 'G6HVA22710-G6HVA22711', NULL, NULL, NULL, 1, 5, '2026-01-12 16:16:26', '2026-01-14 15:27:18', '2026-01-14 16:59:05', '2026-01-14 17:13:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010627116242948096, 2010627116230365184, 2, 'MF5612-N-200-ABD-N-A', 1, 1, 'G6GVA22698', NULL, NULL, NULL, 1, 5, '2026-01-12 16:17:14', '2026-01-13 10:39:29', '2026-01-14 16:59:05', '2026-01-14 17:13:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010627298296713216, 2010627298288324608, 2, 'MF5212-E-Q-200-C', 10, 10, 'G7GVA43798-G7GVA43807', NULL, NULL, NULL, 1, 5, '2026-01-12 16:17:58', '2026-01-13 10:38:49', '2026-01-13 14:59:03', '2026-01-13 17:21:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010627627868344320, 2010627627864150016, 2, 'MF4719-R6F-1000-B-N', 36, 36, 'A1V08731-A1V08766', NULL, 'MF4519-R6F-1000-B-N', NULL, 1, 5, '2026-01-12 16:19:16', '2026-01-13 10:37:48', '2026-01-13 14:58:38', '2026-01-13 17:21:05', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010627832483270656, 2010627832479076352, 2, 'MF5619-N-48m3/h-ABD-D-O', 2, 2, 'G6HVA22713-G6HVA22714', NULL, '英文', NULL, 1, 5, '2026-01-12 16:20:05', '2026-01-13 10:39:46', '2026-01-13 14:58:38', '2026-01-13 17:20:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010628177087287296, 2010628177083092992, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HPI38521', NULL, NULL, NULL, 1, 5, '2026-01-12 16:21:27', '2026-01-13 10:39:11', '2026-01-13 14:57:55', '2026-01-13 17:20:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010628317411921920, 2010628317407727616, 2, 'MF5619-N-800-ABD-D-N', 20, 20, 'G6HVA22678-G6HVA22697', NULL, NULL, NULL, 1, 5, '2026-01-12 16:22:01', '2026-01-12 16:43:39', '2026-01-13 11:20:22', '2026-01-13 13:04:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010628508273725441, 2010628508273725440, 2, 'MF4703-R1-5-BV-A', 10, 10, 'A1V07343-A1V07352', NULL, NULL, NULL, 1, 5, '2026-01-12 16:22:46', '2026-01-12 16:43:39', '2026-01-13 11:20:22', '2026-01-13 13:04:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010628695968829440, 2010628695964635136, 2, 'MF4703-N1-5-B-N', 1, 1, 'A1V08767', NULL, NULL, NULL, 1, 5, '2026-01-12 16:23:31', '2026-01-12 16:43:39', '2026-01-13 11:20:22', '2026-01-13 13:04:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010629183078518784, 2010629183065935872, 2, 'MF4719-Rc6-1000-BV-A', 5, 5, 'A1V07192-A1V07196', NULL, NULL, NULL, 1, 5, '2026-01-13 10:48:30', '2026-01-13 14:21:56', '2026-01-14 16:41:22', '2026-01-14 16:50:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010629183082713088, 2010629183065935872, 2, 'MF4703-R1-5-BV-A', 10, 10, 'A1V07197-A1V07206', NULL, NULL, NULL, 1, 5, '2026-01-13 10:48:30', '2026-01-13 14:21:56', '2026-01-14 16:41:22', '2026-01-14 16:50:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010629415518457856, 2010629415514263552, 2, 'MF4701-R1-100-BV-A', 5, 5, 'A1V08721-A1V08725', NULL, NULL, NULL, 1, 5, '2026-01-12 16:26:23', '2026-01-12 16:43:39', '2026-01-13 11:20:22', '2026-01-13 13:04:03', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010629415522652160, 2010629415514263552, 2, 'MF4701-R1-200-BV-A', 5, 5, 'A1V08726-A1V08730', NULL, NULL, NULL, 1, 5, '2026-01-12 16:26:23', '2026-01-12 16:43:39', '2026-01-13 11:20:22', '2026-01-13 13:04:03', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010888803625848832, 2010888803617460224, 1, 'FS8001N-500', 40, 13, 'A1V07102-7141', NULL, NULL, NULL, 1, 5, '2026-01-13 09:40:34', '2026-01-14 16:46:24', '2026-01-14 16:59:05', '2026-01-14 17:13:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010889255100731392, 2010889255096537088, 1, 'FS8001', 1, 1, 'A1V15112', NULL, NULL, NULL, 1, 5, '2026-01-13 09:40:34', '2026-01-14 16:54:18', '2026-01-14 16:59:05', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010889558516682752, 2010889558512488448, 1, 'FS5001L-6000-EV-A', 7, 7, 'A1V15356-A1V15362', NULL, '不设置最小流量，不截流', NULL, 1, 5, '2026-01-13 09:40:34', '2026-01-14 16:53:29', '2026-01-14 16:59:05', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010897426020159488, 2010897426015965184, 1, 'MF6600-2SLPM', 1, 1, 'FAGVA17577', NULL, NULL, NULL, 1, 5, '2026-01-13 10:12:10', '2026-01-14 16:53:29', '2026-01-14 16:59:05', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010897595889471488, 2010897595881082880, 1, 'FS1015E-100-ISO-EV-C', 5, 5, 'A1V12285-A1V12289', NULL, NULL, NULL, 1, 5, '2026-01-13 10:12:10', '2026-01-14 16:53:29', '2026-01-15 17:23:22', '2026-01-15 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010906703883522048, 2010906703879327744, 2, 'MFC2000-020-N3F-BV-1-A', 4, 4, 'MC2VA11160-MC2VA11163', NULL, '流量滤波深度 5', NULL, 1, 5, '2026-01-13 10:48:13', '2026-01-13 14:19:31', '2026-01-14 16:41:22', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010907166636888064, 2010907166632693760, 2, 'MF4719-R6F-500-BV-A', 1, 1, 'A1V09039', NULL, NULL, NULL, 1, 5, '2026-01-13 10:50:04', '2026-01-13 14:19:31', '2026-01-14 16:42:15', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010907187272863744, 2010907187268669440, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSFVA30003', '19', NULL, NULL, 1, 3, '2026-01-13 13:19:48', '2026-01-13 13:56:03', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 17.60, 13.20, 4.787, 1564, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2010907392416272384, 2010907392412078080, 2, 'MF4712-Rc4-200-BV-A', 5, 5, 'A1V15070-A1V15074', NULL, NULL, NULL, 1, 5, '2026-01-13 10:50:57', '2026-01-13 14:19:31', '2026-01-14 16:39:55', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010907642849775616, 2010907642845581312, 2, '01-0-8-1-1-DB9M-KMB1728', 1, 1, 'B020871', NULL, 'BC-C1000-N2-485-NPT', NULL, 1, 5, '2026-01-13 10:51:57', '2026-01-13 14:19:31', '2026-01-14 16:39:55', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010908029073870848, 2010908029069676544, 2, 'MFC2000-100-P8-BA-0-A', 1, 1, 'MC2UI10836', NULL, NULL, NULL, 1, 5, '2026-01-13 10:53:29', '2026-01-13 14:19:31', '2026-01-14 16:39:55', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010908108262330368, 2010908108258136064, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSFVA30003', '19', NULL, '2026-01-13 11:26:35', 0, 1, '2026-01-13 10:53:48', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, 17.60, 13.20, 4.787, 1564, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2010909056711905280, 2010909056703516672, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSDVA3004', '19', NULL, NULL, 1, 3, '2026-01-13 10:57:34', '2026-01-13 13:56:03', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 13.80, 20.80, 4.776, 1631, 6, NULL, NULL, 6);
INSERT INTO `siargo_product` VALUES (2010945605885349888, 2010945605881155584, 3, 'MF40FD-E-2.5-250-15-ABO-O', 1, 1, 'GSFVA30001', '18', NULL, '2026-01-13 14:19:50', 0, 1, '2026-01-13 13:22:48', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, 18.60, 14.60, 4.788, 1669, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2010946153774698497, 2010946153774698496, 3, 'MF40FD-E-2.5-250-15-ABO-O', 1, 1, 'GSFVA30001', '18', NULL, NULL, 1, 3, '2026-01-13 14:19:56', '2026-01-13 14:20:15', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 12.90, 17.40, 4.810, 1659, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2010946778486919168, 2010946778478530560, 3, 'MF50FD-E-4.0-400-15-A-N', 1, 1, 'GSAVA30021', '15', NULL, NULL, 1, 3, '2026-01-13 13:27:28', '2026-01-13 13:28:23', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 13.10, 17.70, 4.822, 1690, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2010947704782180352, 2010947704773791744, 3, 'MF40FD-E-2.5-250-15-ABO-O', 1, 1, 'GSFVA30002', '18', NULL, NULL, 1, 3, '2026-01-13 13:31:09', '2026-01-13 13:56:03', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 12.90, 17.40, 4.810, 1659, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2010953724828962816, 2010953724824768512, 3, 'MF25FD-E-1-100-15-ABD-O', 1, 1, 'GSBVA30041', '2', NULL, NULL, 1, 5, '2026-01-13 13:55:31', '2026-01-13 13:55:49', '2026-01-15 17:18:50', '2026-01-15 17:25:10', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 17.60, 13.50, 4.789, 1563, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2010956123731775488, 2010956123727581184, 2, 'MF5008-N3F-E-1.2-120-15-AB-D-A', 3, 3, 'G5EVA11750-G5EVA11752', NULL, NULL, NULL, 1, 5, '2026-01-13 14:04:36', '2026-01-13 15:48:25', '2026-01-14 16:42:15', '2026-01-14 16:50:28', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010956521158856704, 2010956521154662400, 2, 'MF5219-E-Q-1000-F', 20, 20, 'G7HVA43808-G7HVA43827', NULL, NULL, NULL, 1, 5, '2026-01-13 14:06:11', '2026-01-13 15:48:25', '2026-01-14 16:42:15', '2026-01-14 16:50:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010988786261807104, 2010988786253418496, 1, 'MF3000M-1000-R-BNN-A', 2, 2, 'A1V15095-A1V15096', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010989118614261760, 2010989118610067456, 1, 'FS4308-20-R-BV-A-0.5/4.5V', 2, 2, 'A1V12227-A1V12228', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010989576640647168, 2010989576632258560, 1, 'FS4308-20-R-BV-A', 2, 2, 'A1V15161-A1V15162', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010989745209724928, 2010989745205530624, 1, 'MF4008-50-O8-BV-A', 3, 3, 'A1V15051-A1V15053', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010990185485815808, 2010990185481621504, 1, 'MF4008-30-O8-BV-A', 3, 3, 'A1V12584-A1V12586', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010990384417460225, 2010990384417460224, 1, 'FS4008-50-R-BV-A', 3, 3, 'A1V15363-A1V15365', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010990931711217664, 2010990931707023360, 1, 'FS4003-5-O6-CV-A', 1, 1, 'A1V12282', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010990931719606272, 2010990931707023360, 1, 'FS4008-30-R-CV-A', 1, 1, 'A1V12283', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010990931723800576, 2010990931707023360, 1, 'FS4001E-500-CV-A', 1, 1, 'A1V12284', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:53:29', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010991318824505344, 2010991318820311040, 1, 'MF4008-10-R-CV-A', 2, 2, 'A1V12580-A1V12581', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-14 16:54:02', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010991615072391168, 2010991615068196864, 1, 'MF4003-2-R-BV-A', 10, 10, 'A1V15001-A1V15010', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-15 17:12:10', '2026-01-15 17:22:12', '2026-01-15 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010991615076585472, 2010991615068196864, 1, 'MF4003-5-R-BV-A', 20, 20, 'A1V15011-A1V15030', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-15 17:12:10', '2026-01-15 17:22:12', '2026-01-15 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010991615084974080, 2010991615068196864, 1, 'MF4008-10-R-BV-A', 10, 10, 'A1V15031-A1V15040', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-15 17:12:10', '2026-01-15 17:22:12', '2026-01-15 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010991615089168384, 2010991615068196864, 1, 'MF4008-20-R-BV-A', 10, 10, 'A1V15041-A1V15050', NULL, NULL, NULL, 1, 5, '2026-01-13 16:28:39', '2026-01-15 17:12:10', '2026-01-15 17:22:12', '2026-01-15 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010998447295811584, 2010998447291617280, 1, 'PFLOW2001-L-5000-U-VI2C-A', 20, 20, 'A1V09019-A1V09038', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:59', '2026-01-14 16:54:02', '2026-01-14 16:59:05', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011247172555493376, 2011247172547104768, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GVA43831', NULL, NULL, NULL, 1, 5, '2026-01-14 09:21:07', '2026-01-14 10:58:01', '2026-01-14 16:42:15', '2026-01-14 16:50:17', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011247591495159809, 2011247591495159808, 2, 'MF5212-E-Y-400-N', 2, 2, 'G7GUE35827/G7GUE35852', NULL, NULL, NULL, 1, 5, '2026-01-14 09:22:47', '2026-01-14 15:27:10', '2026-01-15 17:18:03', '2026-01-15 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011247591503548416, 2011247591495159808, 2, 'MF5219-E-Y-1000-F', 1, 1, 'G7HUD35409', NULL, NULL, NULL, 1, 5, '2026-01-14 09:22:47', '2026-01-14 15:27:10', '2026-01-15 17:18:03', '2026-01-15 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011259744746852353, 2011259744746852352, 1, 'FS4008-10-R-BV-A', 1, 1, 'D3U13031', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:59', '2026-01-15 17:12:10', '2026-01-15 17:18:03', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011259927886942208, 2011259927882747904, 1, 'MF4008-10-O8-BV-A', 10, 10, 'A1V15102-A1V15111', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:18:03', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260231386779648, 2011260231382585344, 1, 'FS4308-10-O6-BV-A-0.5/4.5V', 3, 3, 'A1V15366-A1V15368', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:18:03', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260231390973952, 2011260231382585344, 1, 'FS4308-20-O6-BV-A-0.5/4.5V', 2, 2, 'A1V15369-A1V15370', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:18:03', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471305162752, 2011260471300968448, 1, 'MF4003-2-O8-BV-A', 1, 1, 'A1V15163', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471309357056, 2011260471300968448, 1, 'MF4003-3-O8-BV-A', 1, 1, 'A1V15164', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471317745664, 2011260471300968448, 1, 'MF4003-4-O8-BV-A', 1, 1, 'A1V15165', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471326134272, 2011260471300968448, 1, 'MF4003-5-O8-BV-A', 1, 1, 'A1V15166', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471330328576, 2011260471300968448, 1, 'MF4008-10-O8-BV-A', 1, 1, 'A1V15167', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471334522880, 2011260471300968448, 1, 'MF4008-20-O8-BV-A', 1, 1, 'A1V15168', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471338717184, 2011260471300968448, 1, 'MF4008-30-O8-BV-A', 1, 1, 'A1V15169', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471342911488, 2011260471300968448, 1, 'MF4008-40-O8-BV-A', 1, 1, 'A1V15170', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260471347105792, 2011260471300968448, 1, 'MF4008-50-O8-BV-A', 1, 1, 'A1V15171', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:16:06', '2026-01-15 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260712959987712, 2011260712934821888, 1, 'FS4008-20-O6-BV-A', 4, 4, 'A1V14997-A1V15000', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011260890035113984, 2011260890026725376, 1, 'FS4008-20-O8-BV-A', 1, 1, 'A1V15341', NULL, NULL, NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011261137675210752, 2011261137666822144, 1, 'MF4008-50-O8-BV-A', 1, 1, 'A1V15374', NULL, 'MF4008产品', NULL, 1, 5, '2026-01-14 10:16:48', '2026-01-14 16:44:18', '2026-01-14 17:00:46', '2026-01-14 17:13:02', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011263071450681344, 2011263071446487040, 2, 'MF5706-G-20-B-C', 1, 1, 'PEDVA20393', NULL, 'CO2实标', NULL, 1, 5, '2026-01-14 10:24:18', '2026-01-15 09:42:46', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011264164826042368, 2011264164817653760, 1, 'PFLOW4008-50SLPM-ONET8-IOL-O2', 5, 5, 'A1V15097-A1V15101', NULL, NULL, NULL, 1, 5, '2026-01-14 10:29:11', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011309007459700736, 2011309007455506432, 2, 'MF4608-R3-50-BV-A', 3, 3, 'A3R03767/A3R03768/A3R03769', NULL, NULL, NULL, 1, 5, '2026-01-14 13:26:50', '2026-01-15 09:42:30', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011321653793771520, 2011321653789577216, 1, 'FS5001E-500-EV-A', 150, 13, 'A1V03029-A1V03178', NULL, NULL, NULL, 1, 5, '2026-01-14 14:23:30', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011321653797965824, 2011321653789577216, 1, 'FS5001E-50-EV-A', 90, 13, 'A1V03179-A1V03268', NULL, NULL, NULL, 1, 5, '2026-01-14 14:23:30', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011321949068578816, 2011321949064384512, 1, 'FS5001E-50-EV-A', 46, 13, 'A1V15113-A1V15158', NULL, NULL, NULL, 1, 5, '2026-01-14 14:23:30', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011335452613529600, 2011335452609335296, 1, 'PNEU-054444', 140, 13, 'A1V04586-4725', NULL, 'FS35001-5F50-V-A，滤波深度3', NULL, 1, 5, '2026-01-14 15:12:09', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011345896396935168, 2011345896300466176, 3, 'MF32FD-E-1.6-160-15-ABD-O', 1, 1, 'GSETI30129', '17', NULL, NULL, 1, 5, '2026-01-15 17:29:46', '2026-01-15 17:30:05', '2026-01-15 17:33:20', '2026-01-19 09:59:33', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 15.10, 20.50, 4.802, 1645, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2011366696688668672, 2011366696684474368, 2, 'MF5706-N-20-B-C', 1, 1, 'PADQD57960', NULL, '空气标定GCF670', NULL, 1, 5, '2026-01-14 17:16:04', '2026-01-15 09:45:06', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011366696692862976, 2011366696684474368, 2, 'MF5708-G-100-B-C', 6, 6, 'PAERE68718/PAERE68719/PAERE68720/PAESD76704/PAESD76706/PAESD76707', NULL, '空气标定GCF670', NULL, 1, 5, '2026-01-14 17:16:04', '2026-01-15 09:42:30', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011615273654538240, 2011615273595817984, 1, 'PFLOW4008-10SLPM-ONET6-IOL-AIR', 10, 10, 'A1V15302-A1V15311', NULL, NULL, NULL, 1, 5, '2026-01-15 09:46:35', '2026-01-15 17:12:31', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011615618015285248, 2011615618011090944, 1, 'FS6122-300F300-100P100-TH0', 500, 50, 'A1V08040-9539', NULL, '程序1313', NULL, 1, 5, '2026-01-15 09:46:35', '2026-01-16 14:25:36', '2026-01-19 11:14:30', '2026-01-19 11:26:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011615925025755136, 2011615925017366528, 1, 'MF4003-2-R-BV-A', 20, 20, 'A1V12201-A1V12220', NULL, '通讯延时5ms', NULL, 1, 5, '2026-01-15 09:46:35', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011616546462224384, 2011616546458030080, 1, 'FS34000H-20-V-C', 2, 2, 'A1V15159-A1V15160', NULL, '系数540', NULL, 1, 5, '2026-01-15 09:49:02', '2026-01-15 17:12:10', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011621057352290304, 2011621057348096000, 2, 'MFC2000-050-N3F-BA-1-A', 2, 2, 'MC2VA11158-MC2VA11159', NULL, NULL, NULL, 1, 5, '2026-01-15 10:06:48', '2026-01-15 17:13:48', '2026-01-16 14:30:32', '2026-01-19 09:59:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011621123014119424, 2011621123005730816, 2, 'MFC2000-100-N4F-BV-1-A', 2, 2, 'MC2VA11164-MC2VA11165', NULL, NULL, NULL, 1, 5, '2026-01-15 10:07:04', '2026-01-15 17:13:48', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011621542134140929, 2011621542134140928, 2, 'MF4608-R3-10-BV-A', 1, 1, 'A1V07142', NULL, NULL, NULL, 1, 5, '2026-01-15 10:08:44', '2026-01-15 11:13:38', '2026-01-15 17:13:53', '2026-01-15 17:24:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011622388221726721, 2011622388221726720, 1, 'FS8001', 80, 13, 'A1V12340-A1V12419', NULL, NULL, NULL, 1, 5, '2026-01-15 10:19:21', '2026-01-16 14:25:28', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011622746088132608, 2011622746083938304, 1, 'FS8001N', 4, 4, 'A1V15883-A1V15886', NULL, NULL, NULL, 1, 5, '2026-01-15 10:19:21', '2026-01-16 14:25:28', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011625989501472769, 2011625989501472768, 2, 'MF5708-G-100-B-A', 1, 1, 'PAESL93395', NULL, NULL, NULL, 1, 5, '2026-01-15 10:26:24', '2026-01-15 17:13:48', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011626124394483712, 2011626124381900800, 2, 'MF5712-G-250-B-A', 1, 1, 'PAGSB71906', NULL, NULL, NULL, 1, 5, '2026-01-15 10:26:56', '2026-01-15 17:13:48', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011626124394483713, 2011626124381900800, 2, 'MF5706-G-20-B-A', 1, 1, 'PEDUH16875', NULL, NULL, NULL, 1, 5, '2026-01-15 10:26:56', '2026-01-15 17:13:48', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011626228270616576, 2011626228266422272, 2, 'MF5712-G-250-B-A', 1, 1, 'PEGUI17517', NULL, NULL, NULL, 1, 5, '2026-01-15 10:27:21', '2026-01-15 17:13:48', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011704126771941376, 2011704126767747072, 2, 'MF4703-N1-5-BV-A', 1, 1, 'C1U04723', NULL, NULL, NULL, 1, 5, '2026-01-15 15:36:54', '2026-01-15 17:13:48', '2026-01-16 14:24:54', '2026-01-19 09:59:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011708406677491712, 2011708406673297408, 1, 'MF4308-5-O4-BV-EO', 1, 1, 'A1V15856', NULL, '系数5500', NULL, 1, 5, '2026-01-15 15:54:52', '2026-01-16 15:11:07', '2026-01-19 15:24:22', '2026-01-19 16:40:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011708594150297600, 2011708594146103296, 1, 'MF4308-5-O4-BV-EO', 16, 16, 'A1V15054-A1V15069', NULL, '系数5500', NULL, 1, 5, '2026-01-15 15:54:52', '2026-01-16 14:26:01', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011969838233014272, 2011969838228819968, 2, 'MF5212-Q-300-A', 1, 1, 'G7GRF68472', NULL, NULL, NULL, 1, 5, '2026-01-16 09:12:44', '2026-01-16 14:22:32', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001546811778514944, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011971893781712896, 2011971893777518592, 1, 'MF4008-20-O8-BV-A', 25, 13, 'A1V12293-A1V12317', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 14:22:32', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011971893785907200, 2011971893777518592, 1, 'MF4003-5-O8-BV-A', 22, 13, 'A1V12318-A1V12339', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 14:22:32', '2026-01-16 14:30:32', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011972675948105728, 2011972675943911424, 1, 'MF4008-20-O8-BV-A', 10, 10, 'A1V15312-A1V15321', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 15:11:06', '2026-01-19 15:24:22', '2026-01-19 16:40:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011972675948105729, 2011972675943911424, 1, 'MF4003-5-O8-BV-A', 7, 7, 'A1V15322-A1V15328', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 15:11:07', '2026-01-19 15:24:22', '2026-01-19 16:40:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011973199170752512, 2011973199166558208, 1, 'MF4008-20-O8-BV-A', 7, 7, 'A1V15342-A1V15348', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 15:11:06', '2026-01-16 15:11:41', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011973199174946816, 2011973199166558208, 1, 'MF4003-5-O8-BV-A', 7, 7, 'A1V15349-A1V15355', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 15:11:06', '2026-01-16 15:11:41', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011973469787246592, 2011973469778857984, 1, 'MF4308-5-O4-BV-EO', 1, 1, 'D1T12184', NULL, '系数5500', NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 14:20:28', '2026-01-19 15:24:22', '2026-01-19 16:40:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011985782439268352, 2011985782435074048, 1, 'FS4001E-500-CV-A', 20, 13, 'A1V15863-A1V15882', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-19 11:12:24', '2026-01-19 15:24:22', '2026-01-19 16:40:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011986021510402049, 2011986021510402048, 1, 'FS4001-100-CV-A', 1, 1, 'A1U02832', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-16 14:20:28', '2026-01-16 14:24:43', '2026-01-19 09:59:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011986320039989249, 2011986320039989248, 1, 'FS4001E-500-CV-CH4', 1, 1, 'A1V15972', NULL, '系数1030', NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-19 11:12:24', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011986527985192960, 2011986527980998656, 1, 'FS4001E-100-CV-A', 1, 1, 'A1V15969', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-19 11:12:24', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2011994947702345728, 2011994947698151424, 1, 'FS4001E-100-CV-A', 7, 7, 'A1V15614-A1V15620', NULL, NULL, NULL, 1, 5, '2026-01-16 11:15:18', '2026-01-19 11:12:24', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012000864342757376, 2012000864338563072, 1, 'FS8001N-500', 20, 13, 'A1V15897-A1V15916', NULL, NULL, NULL, 1, 5, '2026-01-16 11:16:07', '2026-01-20 14:14:49', '2026-01-20 14:15:50', '2026-01-20 16:39:24', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012039558294523904, 2012039558290329600, 1, 'PFLOW2001-L-2000-U-VI2C-A', 500, 100, 'A1V07353-A1V07852', NULL, NULL, NULL, 1, 5, '2026-01-16 13:50:10', '2026-01-20 14:14:49', '2026-01-20 14:15:50', '2026-01-20 16:39:24', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012044288810930176, 2012044288806735872, 1, 'FS5001E-300-EV-A', 15, 15, 'A1V15391-A1V15405', NULL, NULL, NULL, 1, 5, '2026-01-16 14:08:42', '2026-01-19 11:12:24', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012078972886437888, 2012078972878049280, 2, 'MF5212-E-Q-400-C', 2, 2, 'G7GTK29431/G7GTK29563', NULL, NULL, NULL, 1, 5, '2026-01-16 16:26:24', '2026-01-19 16:47:15', '2026-01-20 14:16:50', '2026-01-20 16:39:24', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012079089379037184, 2012079089370648576, 2, 'MF5219-E-Y-1000-C', 2, 2, 'G7HVA43867-G7HVA43868', NULL, NULL, NULL, 1, 5, '2026-01-16 16:26:52', '2026-01-19 16:45:41', '2026-01-19 17:03:02', '2026-01-19 17:25:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012079262624763904, 2012079262620569600, 2, 'MF5212-E-Q-300-C', 80, 80, 'G7GVA43718-G7GVA43797', NULL, NULL, NULL, 1, 5, '2026-01-16 16:27:33', '2026-01-19 16:45:41', '2026-01-20 14:17:40', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012079418413797377, 2012079418413797376, 2, 'MF5212-Y-300-A', 30, 30, 'G7GUL22593-G7GUL22622', NULL, NULL, NULL, 1, 5, '2026-01-16 16:28:10', '2026-01-19 16:48:28', '2026-01-19 17:03:02', '2026-01-19 17:25:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2012079418426380288, 2012079418413797376, 2, 'MF5219-Y-800-A', 30, 30, 'G7HUL22623-G7HUL22652', NULL, NULL, NULL, 1, 5, '2026-01-16 16:28:10', '2026-01-19 16:48:28', '2026-01-19 17:03:02', '2026-01-19 17:25:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013071482458853376, 2013071482454659072, 1, 'FS4308-50-R-BV-A', 10, 10, 'A1V15375-A1V15384', NULL, NULL, NULL, 1, 5, '2026-01-19 10:18:10', '2026-01-19 11:29:43', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013071482463047680, 2013071482454659072, 1, 'MF4003-2-R-BV-A', 2, 2, 'A1V15385-A1V15386', NULL, NULL, NULL, 1, 5, '2026-01-19 10:18:10', '2026-01-19 11:29:43', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013071482463047681, 2013071482454659072, 1, 'MF4008-50-R-BV-A', 2, 2, 'A1V15387-A1V15388', NULL, NULL, NULL, 1, 5, '2026-01-19 10:18:10', '2026-01-19 11:29:43', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013072135440683008, 2013072135432294400, 1, 'FS4008-40-R-BV-A', 4, 4, 'D1T06303，D1T06311，D1T06304，D1T06310', NULL, NULL, NULL, 1, 5, '2026-01-19 10:18:10', '2026-01-19 11:13:14', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013072476177551360, 2013072476173357056, 1, 'MF4008-30-R-BV-N', 2, 2, 'A1V15895-A1V15896', NULL, NULL, NULL, 1, 5, '2026-01-19 10:18:10', '2026-01-19 11:13:14', '2026-01-19 15:24:22', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013072620889427968, 2013072620885233664, 1, 'FS4308-10-R-BV-A', 1, 1, 'D2U13711', NULL, NULL, NULL, 1, 5, '2026-01-19 10:18:10', '2026-01-20 15:01:14', '2026-01-20 15:01:36', '2026-01-20 16:39:15', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013084806508564480, 2013084806500175872, 2, 'MF5612-N-300-AB-D-O', 10, 10, 'G6GVA22725-G6GVA22734', NULL, NULL, NULL, 1, 5, '2026-01-19 11:03:13', '2026-01-19 16:50:28', '2026-01-20 14:16:50', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013084806512758784, 2013084806500175872, 2, 'MF5619-N-800-AB-D-O', 8, 8, 'G6HVA22717-G6HVA22724', NULL, NULL, NULL, 1, 5, '2026-01-19 11:03:13', '2026-01-19 16:50:28', '2026-01-20 14:16:50', '2026-01-20 16:39:24', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013084998548967424, 2013084998544773120, 2, 'MF5612-N-300-ABD-D-O', 5, 5, 'G6GVA22754-G6GVA22758', NULL, NULL, NULL, 1, 5, '2026-01-19 11:03:59', '2026-01-19 16:50:28', '2026-01-20 14:16:50', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013084998548967425, 2013084998544773120, 2, 'MF5619-N-800-ABD-D-O', 5, 5, 'G6HVA22759-G6HVA22763', NULL, NULL, NULL, 1, 5, '2026-01-19 11:03:59', '2026-01-19 16:50:28', '2026-01-20 14:16:50', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013089048426303488, 2013089048413720576, 2, 'MF5212-E-Y-400-F', 33, 33, 'G7GVA43832-G7GVA43864', NULL, NULL, NULL, 1, 5, '2026-01-19 11:20:05', '2026-01-19 16:48:15', '2026-01-20 14:54:14', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013089048430497792, 2013089048413720576, 2, 'MF5219-E-Y-1000-F', 2, 2, 'G7HVA43865-G7HVA43866', NULL, NULL, NULL, 1, 5, '2026-01-19 11:20:05', '2026-01-19 16:48:15', '2026-01-20 14:54:14', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013124360527532032, 2013124360519143424, 1, 'MF3000S-50-O6-EVN-A', 1, 1, 'A1V15371', NULL, NULL, NULL, 1, 5, '2026-01-19 13:44:16', '2026-01-19 15:27:01', '2026-01-19 15:27:33', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013124360535920640, 2013124360519143424, 1, 'MF3000S-100-O8-EVN-A', 1, 1, 'A1V15372', NULL, NULL, NULL, 1, 5, '2026-01-19 13:44:16', '2026-01-19 15:27:01', '2026-01-19 15:27:33', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013124709892083712, 2013124709887889408, 1, 'FS4308-50-R-BV-R80C20', 3, 3, 'A1V15329-A1V15331', NULL, '系数908', NULL, 1, 5, '2026-01-19 13:44:16', '2026-01-19 15:27:00', '2026-01-19 15:27:33', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013124709913055232, 2013124709887889408, 1, 'FS4308-35-R-BV-A', 1, 1, 'A1V15332', NULL, NULL, NULL, 1, 5, '2026-01-19 13:44:16', '2026-01-19 15:27:00', '2026-01-19 15:27:33', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013125218610827264, 2013125218606632960, 1, 'MF3000M-200-R-BAZ-A', 1, 1, 'A1V15373', NULL, NULL, NULL, 1, 5, '2026-01-19 13:44:16', '2026-01-19 15:27:00', '2026-01-19 15:27:33', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013128856603316224, 2013128856594927616, 1, 'MF4008-20-O6-BV-A', 2, 2, 'A1V15389-A1V15390', NULL, NULL, NULL, 1, 5, '2026-01-19 13:59:56', '2026-01-19 15:27:00', '2026-01-19 15:27:33', '2026-01-19 16:40:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013139093100023808, 2013139093095829504, 2, 'BC-C0100-Air-485-NPT', 25, 25, 'B023852-B023876', NULL, NULL, NULL, 1, 5, '2026-01-19 14:38:56', '2026-01-19 17:03:36', '2026-01-20 14:16:50', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013139628934942720, 2013139628922359808, 2, 'MF4703-N1-1-AB-CO-AU', 20, 20, 'A1V15075-A1V15094', NULL, '-AU为客户型号不需要处理; CO：GCF1000', NULL, 1, 5, '2026-01-19 14:41:04', '2026-01-19 17:03:36', '2026-01-20 14:16:50', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013140439379333120, 2013140439370944512, 2, 'MF4712R-G6M-300-B-A', 1, 1, 'C3R02432', NULL, NULL, NULL, 1, 5, '2026-01-19 14:44:17', '2026-01-19 16:46:18', '2026-01-20 14:16:50', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013163981848301569, 2013163981848301568, 3, 'MF25FD-E-1-100-15-ABD-O', 1, 1, 'GSBVA30042', '2', NULL, NULL, 1, 3, '2026-01-19 16:18:00', '2026-01-19 16:18:11', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 13.80, 17.60, 4.913, 1652, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2013164528454193152, 2013164528437415936, 3, 'MF2025Be-AB-O', 1, 1, 'GLBVA10624', '2', NULL, NULL, 1, 3, '2026-01-19 16:22:08', '2026-01-19 16:22:17', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013164940292902912, 2013164940284514304, 3, 'MF2032Be-AB-O', 18, 18, 'GLEVA10606-10623', '3', NULL, NULL, 1, 3, '2026-01-19 16:22:08', '2026-01-19 16:22:17', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013428644435578880, 2013428644427190272, 2, 'MF5612-N-200-A-D-0', 1, 1, 'G6GVA22766', NULL, NULL, NULL, 1, 2, '2026-01-20 09:49:31', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013428644448161792, 2013428644427190272, 2, 'MF5619-N-600-A-D-O', 1, 1, 'G6HVA22765', NULL, NULL, NULL, 1, 3, '2026-01-20 09:49:31', '2026-01-20 16:23:00', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013428779865460736, 2013428779857072128, 2, 'MF5612-N-200-A-D-O', 1, 1, 'G6GVA22764', NULL, NULL, NULL, 1, 3, '2026-01-20 09:50:03', '2026-01-20 16:23:00', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429062913871873, 2013429062913871872, 2, 'MF6600-2SLPM', 1, 1, 'FAGVA17579', NULL, NULL, NULL, 1, 3, '2026-01-20 09:53:08', '2026-01-20 16:04:58', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429290949791744, 2013429290941403136, 2, 'MF6600-70SLPM', 1, 1, 'FAGTH10962', NULL, NULL, NULL, 1, 3, '2026-01-20 09:53:08', '2026-01-20 16:04:58', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429528926212096, 2013429528922017792, 2, 'MF6600-70SLPM', 10, 10, 'FAGVA17580-FAGVA17589', NULL, NULL, NULL, 1, 3, '2026-01-20 09:53:08', '2026-01-20 16:04:58', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429781448478721, 2013429781448478720, 2, 'MF4601-R2-200-BV-A', 1, 1, 'A1V16173', NULL, NULL, NULL, 1, 5, '2026-01-20 09:54:02', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425930219520, 2013431425926025216, 2, 'MF4701-R1-100-BV-A', 1, 1, 'A1V15336', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425934413824, 2013431425926025216, 2, 'MF4701-R1-200-BV-A', 1, 1, 'A1V15337', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425942802432, 2013431425926025216, 2, 'MF4701-R1-500-BV-A', 1, 1, 'A1V15338', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425946996736, 2013431425926025216, 2, 'MF4703-R1-1000-BV-A', 1, 1, 'A1V15339', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013438793615265792, 2013438793611071488, 1, 'FS8003P-6-EV', 5, 5, 'A1V16187-A1V16191', NULL, NULL, NULL, 1, 3, '2026-01-20 10:31:23', '2026-01-20 16:04:58', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013439058527506432, 2013439058523312128, 1, 'FS8001', 2, 2, 'A1V16181-A1V16182', NULL, NULL, NULL, 1, 3, '2026-01-20 10:31:23', '2026-01-20 16:04:58', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013479170460209152, 2013479170456014848, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V15514-5563', NULL, NULL, NULL, 1, 2, '2026-01-20 13:10:22', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013482430415884288, 2013482430411689984, 2, 'MF5712-N-250-B-A', 50, 50, 'PEGVA20133-PEGVA20182', NULL, '订单共500只，其中260只在新系统，剩下320只在25年电子版报告单上：\r\nMF5712-G-250-B-C：PEGUL19965-PEGUL19994 (30只25.12.31入库)；\r\nMF5712-N-250-B-A：PEGUL20009-PEGUL20088 (80只26.1.7入库)；\r\nMF5712-G-250-B-A：PEGUL19825-PEGUL19924 (100只25.12.25入库)；\r\nMF5712-G-250-B-R：PEGUL20089-PEGUL20118 (30只26.1.7入库)；', NULL, 1, 2, '2026-01-20 13:23:14', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013482430420078592, 2013482430411689984, 2, 'MF5712-G-250-B-A', 210, 210, 'PEGVA20183-PEGVA20392', NULL, '订单共500只，其中260只在新系统，剩下320只在25年电子版报告单上：\r\nMF5712-G-250-B-C：PEGUL19965-PEGUL19994 (30只25.12.31入库)；\r\nMF5712-N-250-B-A：PEGUL20009-PEGUL20088 (80只26.1.7入库)；\r\nMF5712-G-250-B-A：PEGUL19825-PEGUL19924 (100只25.12.25入库)；\r\nMF5712-G-250-B-R：PEGUL20089-PEGUL20118 (30只26.1.7入库)；', NULL, 1, 2, '2026-01-20 13:23:14', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013502073587224576, 2013502073583030272, 1, 'FS5001E-100-EV-H', 5, 5, 'A1V16176-A1V16180', NULL, NULL, NULL, 1, 3, '2026-01-20 14:41:23', '2026-01-20 16:04:58', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013518280524681216, 2013518280512098304, 3, 'MF50GD40', 1, 1, 'CSAVA31021', '7', NULL, NULL, 1, 3, '2026-01-20 15:49:45', '2026-01-20 15:49:56', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, 10.20, NULL, NULL, NULL, 1522, 4, 2.89, NULL, 4);
INSERT INTO `siargo_product` VALUES (2013518750274146304, 2013518750265757696, 3, 'MF50GD40', 1, 1, 'CSAVA31022', '7', NULL, NULL, 1, 3, '2026-01-20 15:49:45', '2026-01-20 15:49:56', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, 10.20, NULL, NULL, NULL, 1553, 3, 2.89, NULL, 3);
INSERT INTO `siargo_product` VALUES (2013519005539487744, 2013519005531099136, 3, 'MF50GD40', 1, 1, 'CSAVA31023', '7', NULL, NULL, 1, 3, '2026-01-20 15:49:45', '2026-01-20 15:49:56', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, 10.50, NULL, NULL, NULL, 1641, 9, 2.88, NULL, 9);
INSERT INTO `siargo_product` VALUES (2013528478626729984, 2013528478618341376, 1, 'FSP1000-500-E-B', 2, 2, 'A1V17000-A1V17001', NULL, NULL, NULL, 1, 2, '2026-01-20 16:26:17', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536078089015296, 2013536078084820992, 1, 'MF4008-40-O8-CV-C', 5, 5, 'A1V15887-A1V15891', NULL, 'MF4008产品', NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536530557947904, 2013536530553753600, 1, 'MF4003-5-R-BV-A', 1, 1, 'A1V15892', NULL, NULL, NULL, 1, 2, '2026-01-20 17:03:38', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536530557947905, 2013536530553753600, 1, 'MF3000S-5-R-BAZ-A', 2, 2, 'A1V15893-A1V15894', NULL, NULL, NULL, 1, 2, '2026-01-20 17:03:38', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536798397812736, 2013536798393618432, 1, 'FS4001-500-CV-A', 2, 2, 'A1V15857-A1V15858', NULL, NULL, NULL, 1, 2, '2026-01-20 17:03:38', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536798406201344, 2013536798393618432, 1, 'MF4003-2-O6-CV-A', 4, 4, 'A1V15859-A1V15862', NULL, NULL, NULL, 1, 2, '2026-01-20 17:03:38', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536979457527808, 2013536979432361984, 1, 'MF4008-10-R-BV-A-0.5/4.5V', 2, 2, 'A1V16183-A1V16184', NULL, NULL, NULL, 1, 2, '2026-01-20 17:03:38', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for siargo_qareport
-- ----------------------------
DROP TABLE IF EXISTS `siargo_qareport`;
CREATE TABLE `siargo_qareport`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单号',
  `cust_id` bigint NOT NULL COMMENT '客户ID',
  `formnum` int UNSIGNED NOT NULL COMMENT '表单编号(Form Number)',
  `rep_type` tinyint UNSIGNED NOT NULL COMMENT '报告单类型：1产成品，2返修品',
  `create_time` datetime NULL DEFAULT NULL COMMENT '录入时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_formnum`(`formnum` ASC) USING BTREE,
  INDEX `idx_cust`(`cust_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '矽翔质管部：检验报告单(Quality Report)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_qareport
-- ----------------------------
INSERT INTO `siargo_qareport` VALUES (2007720620622467072, 102637, 2007719497152974848, 202601001, 1, '2026-01-04 15:47:52');
INSERT INTO `siargo_qareport` VALUES (2007720844652826624, 102641, 2007719394908426240, 202601002, 1, '2026-01-04 15:48:45');
INSERT INTO `siargo_qareport` VALUES (2007721015553937408, 102647, 2007719346942365696, 202601003, 1, '2026-01-04 15:49:26');
INSERT INTO `siargo_qareport` VALUES (2007721171716263936, 102666, 2007714429901066240, 202601004, 1, '2026-01-04 15:50:03');
INSERT INTO `siargo_qareport` VALUES (2007721325454282752, 102670, 2007719816536641536, 202601005, 2, '2026-01-04 15:50:40');
INSERT INTO `siargo_qareport` VALUES (2007721438176202752, 102679, 2007720000314265600, 202601006, 1, '2026-01-04 15:51:07');
INSERT INTO `siargo_qareport` VALUES (2007737171337662464, 102667, 2007714429901066240, 202601007, 1, '2026-01-04 16:53:38');
INSERT INTO `siargo_qareport` VALUES (2008001595801718784, 102271, 2007693569572065280, 202601008, 1, '2026-01-05 10:24:21');
INSERT INTO `siargo_qareport` VALUES (2008002955578298368, 102663, 2008000445559984128, 202601009, 1, '2026-01-05 10:29:46');
INSERT INTO `siargo_qareport` VALUES (2008003805579169792, 102325, 2007715022455558144, 202601010, 1, '2026-01-05 10:33:08');
INSERT INTO `siargo_qareport` VALUES (2008004576886509568, 102650, 2000847563539288064, 202601011, 1, '2026-01-05 10:36:12');
INSERT INTO `siargo_qareport` VALUES (2008018603964485632, 102649, 2000847525710860288, 202601012, 1, '2026-01-05 11:31:57');
INSERT INTO `siargo_qareport` VALUES (2008069976961699840, 102644, 2008069857314983936, 202601013, 1, '2026-01-05 14:56:05');
INSERT INTO `siargo_qareport` VALUES (2008070300078297088, 102664, 2008070087435472896, 202601014, 1, '2026-01-05 14:57:22');
INSERT INTO `siargo_qareport` VALUES (2008070586134024192, 102655, 2008070408593330176, 202601015, 1, '2026-01-05 14:58:30');
INSERT INTO `siargo_qareport` VALUES (2008070960031059968, 102643, 2008070674134716416, 202601016, 1, '2026-01-05 14:59:59');
INSERT INTO `siargo_qareport` VALUES (2008074146267648000, 102662, 2007715109323788288, 202601017, 1, '2026-01-05 15:12:39');
INSERT INTO `siargo_qareport` VALUES (2008085860371517440, 102657, 2008079137560711168, 202601018, 1, '2026-01-05 15:59:12');
INSERT INTO `siargo_qareport` VALUES (2008086356880642048, 102442, 2000847525710860288, 202601019, 1, '2026-01-05 16:01:10');
INSERT INTO `siargo_qareport` VALUES (2008086670430031872, 102590, 2008081896909426688, 202601020, 1, '2026-01-05 16:02:25');
INSERT INTO `siargo_qareport` VALUES (2008086983698403328, 102691, 2008081806144688128, 202601021, 1, '2026-01-05 16:03:40');
INSERT INTO `siargo_qareport` VALUES (2008087241400635392, 102689, 2008081212201881600, 202601022, 1, '2026-01-05 16:04:41');
INSERT INTO `siargo_qareport` VALUES (2008087427757756416, 102642, 2000847525710860288, 202601023, 1, '2026-01-05 16:05:25');
INSERT INTO `siargo_qareport` VALUES (2008100046124208128, 102680, 2008081588447727616, 202601024, 2, '2026-01-05 16:55:34');
INSERT INTO `siargo_qareport` VALUES (2008118855526633472, 102087, 2008118232144007168, 202601025, 1, '2026-01-05 18:10:18');
INSERT INTO `siargo_qareport` VALUES (2008366000812969984, 102643, 2008070674134716416, 202601026, 1, '2026-01-06 10:32:22');
INSERT INTO `siargo_qareport` VALUES (2008369350900043776, 102699, 2000847525710860288, 202601027, 2, '2026-01-06 10:45:41');
INSERT INTO `siargo_qareport` VALUES (2008369541111730176, 102683, 2000847627364012032, 202601028, 2, '2026-01-06 10:46:26');
INSERT INTO `siargo_qareport` VALUES (2008369953466339328, 102692, 2008369853578989568, 202601029, 1, '2026-01-06 10:48:05');
INSERT INTO `siargo_qareport` VALUES (2008370118747082752, 102696, 2000847627364012032, 202601030, 2, '2026-01-06 10:48:44');
INSERT INTO `siargo_qareport` VALUES (2008372492580540416, 102682, 2008372258152501248, 202601031, 1, '2026-01-06 10:58:10');
INSERT INTO `siargo_qareport` VALUES (2008406087223332864, 102382, 2008405882360942592, 202601032, 1, '2026-01-06 13:11:40');
INSERT INTO `siargo_qareport` VALUES (2008414090806808576, 102700, 2007715109323788288, 202601033, 1, '2026-01-06 13:43:28');
INSERT INTO `siargo_qareport` VALUES (2008414232125493248, 102688, 2000847563539288064, 202601034, 1, '2026-01-06 13:44:02');
INSERT INTO `siargo_qareport` VALUES (2008414727925780480, 102690, 2008414381195251712, 202601035, 1, '2026-01-06 13:46:00');
INSERT INTO `siargo_qareport` VALUES (2008414883555430400, 102653, 2000847525710860288, 202601036, 1, '2026-01-06 13:46:37');
INSERT INTO `siargo_qareport` VALUES (2008415562399338496, 102694, 2008415147318431744, 202601037, 1, '2026-01-06 13:49:19');
INSERT INTO `siargo_qareport` VALUES (2008417873494396928, 102497, 2008417707106357248, 202601038, 1, '2026-01-06 13:58:30');
INSERT INTO `siargo_qareport` VALUES (2008704659730845696, 102687, 2000847525710860288, 202601039, 1, '2026-01-07 08:58:05');
INSERT INTO `siargo_qareport` VALUES (2008704992779554816, 102699, 2000847525710860288, 202601040, 1, '2026-01-07 08:59:24');
INSERT INTO `siargo_qareport` VALUES (2008705604053225472, 102551, 2008705235667505152, 202601041, 1, '2026-01-07 09:01:50');
INSERT INTO `siargo_qareport` VALUES (2008706691204239360, 102576, 2000847563539288064, 202601042, 1, '2026-01-07 09:06:09');
INSERT INTO `siargo_qareport` VALUES (2008712104553336832, 102686, 2008712028045037568, 202601043, 1, '2026-01-07 09:27:40');
INSERT INTO `siargo_qareport` VALUES (2008718895395753984, 102701, 2008718734284148736, 202601044, 1, '2026-01-07 09:54:39');
INSERT INTO `siargo_qareport` VALUES (2008720014754828288, 102714, 2008719753382580224, 202601045, 1, '2026-01-07 09:59:06');
INSERT INTO `siargo_qareport` VALUES (2008720216857366528, 102675, 2008719496024281088, 202601046, 1, '2026-01-07 09:59:54');
INSERT INTO `siargo_qareport` VALUES (2008720647989874688, 102673, 2000847563539288064, 202601047, 1, '2026-01-07 10:01:37');
INSERT INTO `siargo_qareport` VALUES (2008720833919176704, 102703, 2008719311974027264, 202601048, 1, '2026-01-07 10:02:21');
INSERT INTO `siargo_qareport` VALUES (2008721050542395392, 102676, 2000847563539288064, 202601049, 1, '2026-01-07 10:03:13');
INSERT INTO `siargo_qareport` VALUES (2008721243883032576, 102627, 2008719110416748544, 202601050, 1, '2026-01-07 10:03:59');
INSERT INTO `siargo_qareport` VALUES (2008722349874860032, 102665, 2008721832280969216, 202601051, 2, '2026-01-07 10:08:23');
INSERT INTO `siargo_qareport` VALUES (2008722967511289856, 102681, 2008722775034679296, 202601052, 1, '2026-01-07 10:10:50');
INSERT INTO `siargo_qareport` VALUES (2008774196031508480, 102695, 2008773890749091840, 202601053, 2, '2026-01-07 13:34:24');
INSERT INTO `siargo_qareport` VALUES (2008774367431741440, 102717, 2008773995162095616, 202601054, 1, '2026-01-07 13:35:05');
INSERT INTO `siargo_qareport` VALUES (2008793136510128128, 102617, 2008792751502381056, 202601055, 1, '2026-01-07 14:49:39');
INSERT INTO `siargo_qareport` VALUES (2008793294048186368, 102345, 2007693569572065280, 202601056, 1, '2026-01-07 14:50:17');
INSERT INTO `siargo_qareport` VALUES (2008804015779729408, 102734, 2008802273482625024, 202601057, 1, '2026-01-07 15:32:53');
INSERT INTO `siargo_qareport` VALUES (2008804255408705536, 102601, 2008803436777033728, 202601058, 1, '2026-01-07 15:33:50');
INSERT INTO `siargo_qareport` VALUES (2008804621781159936, 102716, 2008803112767049728, 202601059, 1, '2026-01-07 15:35:18');
INSERT INTO `siargo_qareport` VALUES (2008804888027189248, 102693, 2007715086636797952, 202601060, 1, '2026-01-07 15:36:21');
INSERT INTO `siargo_qareport` VALUES (2008805183637540864, 102697, 2008802664131710976, 202601061, 1, '2026-01-07 15:37:32');
INSERT INTO `siargo_qareport` VALUES (2008805427750227968, 102652, 2007719346942365696, 202601062, 1, '2026-01-07 15:38:30');
INSERT INTO `siargo_qareport` VALUES (2008805925375037440, 102678, 2008802763016622080, 202601063, 2, '2026-01-07 15:40:29');
INSERT INTO `siargo_qareport` VALUES (2008822951699140608, 102677, 2008081212201881600, 202601064, 1, '2026-01-07 16:48:08');
INSERT INTO `siargo_qareport` VALUES (2009089756325859328, 102746, 2008369853578989568, 202601065, 1, '2026-01-08 10:28:19');
INSERT INTO `siargo_qareport` VALUES (2009090473564426240, 102737, 2009090038669627392, 202601066, 2, '2026-01-08 10:31:10');
INSERT INTO `siargo_qareport` VALUES (2009090992689238016, 102733, 2000847627364012032, 202601067, 1, '2026-01-08 10:33:14');
INSERT INTO `siargo_qareport` VALUES (2009091397326327808, 102730, 2009091257458872320, 202601068, 1, '2026-01-08 10:34:50');
INSERT INTO `siargo_qareport` VALUES (2009149885163360256, 102591, 2009149718863400960, 202601069, 1, '2026-01-08 14:27:15');
INSERT INTO `siargo_qareport` VALUES (2009161954524778496, 102651, 2008070087435472896, 202601070, 1, '2026-01-08 15:15:13');
INSERT INTO `siargo_qareport` VALUES (2009184964220669952, 102635, 2008803112767049728, 202601071, 1, '2026-01-08 16:46:38');
INSERT INTO `siargo_qareport` VALUES (2009189961683554304, 102661, 2000847563539288064, 202601072, 1, '2026-01-08 17:06:30');
INSERT INTO `siargo_qareport` VALUES (2009190089546911744, 102660, 2000847563539288064, 202601073, 1, '2026-01-08 17:07:00');
INSERT INTO `siargo_qareport` VALUES (2009190207847256064, 102668, 2000847563539288064, 202601074, 1, '2026-01-08 17:07:29');
INSERT INTO `siargo_qareport` VALUES (2009193824843059200, 102593, 2009193617589915648, 202601075, 1, '2026-01-08 17:21:51');
INSERT INTO `siargo_qareport` VALUES (2009213738890940416, 102671, 2007711147526836224, 202601076, 1, '2026-01-08 18:40:59');
INSERT INTO `siargo_qareport` VALUES (2009214318208208896, 102539, 2007711147526836224, 202601077, 1, '2026-01-08 18:43:17');
INSERT INTO `siargo_qareport` VALUES (2009224146913710080, 102624, 2009223836984004608, 202601078, 1, '2026-01-08 19:22:20');
INSERT INTO `siargo_qareport` VALUES (2009230698806628352, 102633, 2009223836984004608, 202601079, 1, '2026-01-08 19:48:22');
INSERT INTO `siargo_qareport` VALUES (2009431944842825728, 102630, 2007715086636797952, 202601080, 1, '2026-01-09 09:08:03');
INSERT INTO `siargo_qareport` VALUES (2009432419470266368, 102630, 2007715086636797952, 202601081, 1, '2026-01-09 09:09:56');
INSERT INTO `siargo_qareport` VALUES (2009433030311923712, 102560, 2007715022455558144, 202601082, 1, '2026-01-09 09:12:22');
INSERT INTO `siargo_qareport` VALUES (2009433415940427776, 102560, 2007715022455558144, 202601083, 1, '2026-01-09 09:13:54');
INSERT INTO `siargo_qareport` VALUES (2009434031588757504, 102560, 2007715022455558144, 202601084, 1, '2026-01-09 09:16:21');
INSERT INTO `siargo_qareport` VALUES (2009436880204845056, 102766, 2009436221791391744, 202601085, 1, '2026-01-09 09:27:40');
INSERT INTO `siargo_qareport` VALUES (2009437832131497984, 102754, 2000847627364012032, 202601086, 2, '2026-01-09 09:31:27');
INSERT INTO `siargo_qareport` VALUES (2009438299691536384, 102772, 2000847627364012032, 202601087, 2, '2026-01-09 09:33:18');
INSERT INTO `siargo_qareport` VALUES (2009450005280968704, 5000104, 2009149718863400960, 202601088, 1, '2026-01-09 10:19:49');
INSERT INTO `siargo_qareport` VALUES (2009451072932663296, 102722, 2008719110416748544, 202601089, 1, '2026-01-09 10:24:04');
INSERT INTO `siargo_qareport` VALUES (2009451591747096576, 102210, 2008372258152501248, 202601090, 1, '2026-01-09 10:26:07');
INSERT INTO `siargo_qareport` VALUES (2009464589433819136, 102728, 2000847525710860288, 202601091, 1, '2026-01-09 11:17:46');
INSERT INTO `siargo_qareport` VALUES (2009468817162227712, 102448, 2009468509057044480, 202601092, 1, '2026-01-09 11:34:34');
INSERT INTO `siargo_qareport` VALUES (2009505146554142720, 102710, 2009505047618899968, 202601093, 1, '2026-01-09 13:58:56');
INSERT INTO `siargo_qareport` VALUES (2009505314422771712, 102720, 2000847563539288064, 202601094, 1, '2026-01-09 13:59:36');
INSERT INTO `siargo_qareport` VALUES (2009505768284213248, 102617, 2008792751502381056, 202601095, 1, '2026-01-09 14:01:24');
INSERT INTO `siargo_qareport` VALUES (2009511539097063424, 102707, 2009511454766387200, 202601096, 1, '2026-01-09 14:24:20');
INSERT INTO `siargo_qareport` VALUES (2009512315810861056, 102742, 2008081212201881600, 202601097, 1, '2026-01-09 14:27:25');
INSERT INTO `siargo_qareport` VALUES (2009515362179993600, 102646, 2009515167178412032, 202601098, 1, '2026-01-09 14:39:31');
INSERT INTO `siargo_qareport` VALUES (2009520635636469760, 102658, 2009520427271835648, 202601099, 1, '2026-01-09 15:00:29');
INSERT INTO `siargo_qareport` VALUES (2009530705770303488, 102738, 2008718734284148736, 202601100, 1, '2026-01-09 15:40:30');
INSERT INTO `siargo_qareport` VALUES (2009530930576609280, 102708, 2008718734284148736, 202601101, 1, '2026-01-09 15:41:23');
INSERT INTO `siargo_qareport` VALUES (2009549901866586112, 102711, 2009549818810978304, 202601102, 1, '2026-01-09 16:56:46');
INSERT INTO `siargo_qareport` VALUES (2009553868608622592, 102750, 2009553606242324480, 202601103, 1, '2026-01-09 17:12:32');
INSERT INTO `siargo_qareport` VALUES (2009554067347329024, 102731, 2007719816536641536, 202601104, 1, '2026-01-09 17:13:20');
INSERT INTO `siargo_qareport` VALUES (2009802603418734592, 49272, 2009802341484449792, 202601105, 1, '2026-01-10 09:40:55');
INSERT INTO `siargo_qareport` VALUES (2009802931811766272, 49271, 2009802341484449792, 202601106, 1, '2026-01-10 09:42:13');
INSERT INTO `siargo_qareport` VALUES (2009803327158472704, 49273, 2009802341484449792, 202601107, 1, '2026-01-10 09:43:48');
INSERT INTO `siargo_qareport` VALUES (2009808179628462080, 49270, 2009802341484449792, 202601108, 1, '2026-01-10 10:03:05');
INSERT INTO `siargo_qareport` VALUES (2009812912330100736, 102674, 2009812759917481984, 202601109, 1, '2026-01-10 10:21:53');
INSERT INTO `siargo_qareport` VALUES (2009899536833433600, 102613, 2009898353339256832, 202601110, 1, '2026-01-10 16:06:06');
INSERT INTO `siargo_qareport` VALUES (2010584432367226880, 102448, 2009468509057044480, 202601111, 1, '2026-01-12 13:27:38');
INSERT INTO `siargo_qareport` VALUES (2010585483644358656, 102739, 2010583151636500480, 202601112, 1, '2026-01-12 13:31:48');
INSERT INTO `siargo_qareport` VALUES (2010585754135023616, 102758, 2000847563539288064, 202601113, 1, '2026-01-12 13:32:53');
INSERT INTO `siargo_qareport` VALUES (2010610597966499840, 102354, 2009149718863400960, 202601114, 1, '2026-01-12 15:11:36');
INSERT INTO `siargo_qareport` VALUES (2010617249423806464, 102735, 2010617080661790720, 202601115, 1, '2026-01-12 15:38:02');
INSERT INTO `siargo_qareport` VALUES (2010617466307072000, 102769, 2000847627364012032, 202601116, 1, '2026-01-12 15:38:54');
INSERT INTO `siargo_qareport` VALUES (2010619106359627776, 102736, 2000847627364012032, 202601117, 2, '2026-01-12 15:45:25');
INSERT INTO `siargo_qareport` VALUES (2010626592537956352, 102757, 2010625095762825216, 202601118, 2, '2026-01-12 16:15:09');
INSERT INTO `siargo_qareport` VALUES (2010626912584323072, 102778, 2008070674134716416, 202601119, 1, '2026-01-12 16:16:26');
INSERT INTO `siargo_qareport` VALUES (2010627116230365184, 102759, 2010625260645109760, 202601120, 1, '2026-01-12 16:17:14');
INSERT INTO `siargo_qareport` VALUES (2010627298288324608, 102750, 2009553606242324480, 202601121, 1, '2026-01-12 16:17:58');
INSERT INTO `siargo_qareport` VALUES (2010627627864150016, 102464, 2000847563539288064, 202601122, 1, '2026-01-12 16:19:16');
INSERT INTO `siargo_qareport` VALUES (2010627832479076352, 102789, 2007715086636797952, 202601123, 1, '2026-01-12 16:20:05');
INSERT INTO `siargo_qareport` VALUES (2010628177083092992, 102779, 2010625485929566208, 202601124, 2, '2026-01-12 16:21:27');
INSERT INTO `siargo_qareport` VALUES (2010628317407727616, 102752, 2008081212201881600, 202601125, 1, '2026-01-12 16:22:01');
INSERT INTO `siargo_qareport` VALUES (2010628508273725440, 102724, 2010625640707772416, 202601126, 1, '2026-01-12 16:22:46');
INSERT INTO `siargo_qareport` VALUES (2010628695964635136, 102732, 2008705235667505152, 202601127, 1, '2026-01-12 16:23:31');
INSERT INTO `siargo_qareport` VALUES (2010629183065935872, 102709, 2000847525710860288, 202601128, 1, '2026-01-12 16:25:27');
INSERT INTO `siargo_qareport` VALUES (2010629415514263552, 102729, 2000847525710860288, 202601129, 1, '2026-01-12 16:26:23');
INSERT INTO `siargo_qareport` VALUES (2010888803617460224, 102706, 2008417707106357248, 202601130, 1, '2026-01-13 09:37:05');
INSERT INTO `siargo_qareport` VALUES (2010889255096537088, 102796, 2010889086942695424, 202601131, 1, '2026-01-13 09:38:53');
INSERT INTO `siargo_qareport` VALUES (2010889558512488448, 102823, 2010889406141812736, 202601132, 1, '2026-01-13 09:40:05');
INSERT INTO `siargo_qareport` VALUES (2010897426015965184, 102774, 2010897286836375552, 202601133, 1, '2026-01-13 10:11:21');
INSERT INTO `siargo_qareport` VALUES (2010897595881082880, 102764, 2008069857314983936, 202601134, 1, '2026-01-13 10:12:02');
INSERT INTO `siargo_qareport` VALUES (2010906703879327744, 102762, 2010905350503256064, 202601135, 1, '2026-01-13 10:48:13');
INSERT INTO `siargo_qareport` VALUES (2010907166632693760, 102743, 2010907066472714240, 202601136, 1, '2026-01-13 10:50:04');
INSERT INTO `siargo_qareport` VALUES (2010907187268669440, 102560, 2007715022455558144, 202601137, 1, '2026-01-13 10:50:08');
INSERT INTO `siargo_qareport` VALUES (2010907392412078080, 102787, 2000847525710860288, 202601138, 1, '2026-01-13 10:50:57');
INSERT INTO `siargo_qareport` VALUES (2010907642845581312, 102791, 2010905611992944640, 202601139, 1, '2026-01-13 10:51:57');
INSERT INTO `siargo_qareport` VALUES (2010908029069676544, 102794, 2010907919644479488, 202601140, 2, '2026-01-13 10:53:29');
INSERT INTO `siargo_qareport` VALUES (2010908108258136064, 102560, 2007715022455558144, 202601141, 1, '2026-01-13 10:53:48');
INSERT INTO `siargo_qareport` VALUES (2010909056703516672, 102560, 2007715022455558144, 202601142, 1, '2026-01-13 10:57:34');
INSERT INTO `siargo_qareport` VALUES (2010945605881155584, 102630, 2007715086636797952, 202601143, 1, '2026-01-13 13:22:48');
INSERT INTO `siargo_qareport` VALUES (2010946153774698496, 102630, 2007715086636797952, 202601144, 1, '2026-01-13 13:24:59');
INSERT INTO `siargo_qareport` VALUES (2010946778478530560, 102635, 2008803112767049728, 202601145, 1, '2026-01-13 13:27:28');
INSERT INTO `siargo_qareport` VALUES (2010947704773791744, 102630, 2007715086636797952, 202601146, 1, '2026-01-13 13:31:09');
INSERT INTO `siargo_qareport` VALUES (2010953724824768512, 102721, 2010953250197327872, 202601147, 1, '2026-01-13 13:55:04');
INSERT INTO `siargo_qareport` VALUES (2010956123727581184, 102765, 2010955747762753536, 202601148, 1, '2026-01-13 14:04:36');
INSERT INTO `siargo_qareport` VALUES (2010956521154662400, 102770, 2010955877500964864, 202601149, 1, '2026-01-13 14:06:11');
INSERT INTO `siargo_qareport` VALUES (2010988786253418496, 102795, 2007715109323788288, 202601150, 1, '2026-01-13 16:14:23');
INSERT INTO `siargo_qareport` VALUES (2010989118610067456, 102751, 2010989045750812672, 202601151, 1, '2026-01-13 16:15:42');
INSERT INTO `siargo_qareport` VALUES (2010989576632258560, 102760, 2010989422000852992, 202601152, 1, '2026-01-13 16:17:32');
INSERT INTO `siargo_qareport` VALUES (2010989745205530624, 102780, 2000847563539288064, 202601153, 1, '2026-01-13 16:18:12');
INSERT INTO `siargo_qareport` VALUES (2010990185481621504, 102768, 2010989961023442944, 202601154, 1, '2026-01-13 16:19:57');
INSERT INTO `siargo_qareport` VALUES (2010990384417460224, 102818, 2007711147526836224, 202601155, 1, '2026-01-13 16:20:44');
INSERT INTO `siargo_qareport` VALUES (2010990931707023360, 102763, 2010990688588386304, 202601156, 1, '2026-01-13 16:22:55');
INSERT INTO `siargo_qareport` VALUES (2010991318820311040, 102775, 2010991169763135488, 202601157, 1, '2026-01-13 16:24:27');
INSERT INTO `siargo_qareport` VALUES (2010991615068196864, 102788, 2000847525710860288, 202601158, 1, '2026-01-13 16:25:38');
INSERT INTO `siargo_qareport` VALUES (2010998447291617280, 102744, 2009149718863400960, 202601159, 1, '2026-01-13 16:52:47');
INSERT INTO `siargo_qareport` VALUES (2011247172547104768, 102822, 2011247058478813184, 202601160, 1, '2026-01-14 09:21:07');
INSERT INTO `siargo_qareport` VALUES (2011247591495159808, 102804, 2000847563539288064, 202601161, 2, '2026-01-14 09:22:47');
INSERT INTO `siargo_qareport` VALUES (2011259744746852352, 102804, 2000847563539288064, 202601162, 2, '2026-01-14 10:11:05');
INSERT INTO `siargo_qareport` VALUES (2011259927882747904, 102797, 2000847525710860288, 202601163, 1, '2026-01-14 10:11:48');
INSERT INTO `siargo_qareport` VALUES (2011260231382585344, 102816, 2011260144535326720, 202601164, 1, '2026-01-14 10:13:01');
INSERT INTO `siargo_qareport` VALUES (2011260471300968448, 102801, 2000847563539288064, 202601165, 1, '2026-01-14 10:13:58');
INSERT INTO `siargo_qareport` VALUES (2011260712934821888, 102778, 2008070674134716416, 202601166, 1, '2026-01-14 10:14:56');
INSERT INTO `siargo_qareport` VALUES (2011260890026725376, 102820, 2008414381195251712, 202601167, 1, '2026-01-14 10:15:38');
INSERT INTO `siargo_qareport` VALUES (2011261137666822144, 5000105, 2007711147526836224, 202601168, 1, '2026-01-14 10:16:37');
INSERT INTO `siargo_qareport` VALUES (2011263071446487040, 102810, 2010907066472714240, 202601169, 1, '2026-01-14 10:24:18');
INSERT INTO `siargo_qareport` VALUES (2011264164817653760, 102793, 2009149718863400960, 202601170, 1, '2026-01-14 10:28:39');
INSERT INTO `siargo_qareport` VALUES (2011309007455506432, 102809, 2011308729838718976, 202601171, 2, '2026-01-14 13:26:50');
INSERT INTO `siargo_qareport` VALUES (2011321653789577216, 102612, 2011321380811689984, 202601172, 1, '2026-01-14 14:17:05');
INSERT INTO `siargo_qareport` VALUES (2011321949064384512, 102798, 2011321380811689984, 202601173, 1, '2026-01-14 14:18:15');
INSERT INTO `siargo_qareport` VALUES (2011335452609335296, 102428, 2007711147526836224, 202601174, 1, '2026-01-14 15:11:55');
INSERT INTO `siargo_qareport` VALUES (2011345896300466176, 102828, 2011344161783795712, 202601175, 2, '2026-01-14 15:53:25');
INSERT INTO `siargo_qareport` VALUES (2011366696684474368, 102811, 2000847627364012032, 202601176, 2, '2026-01-14 17:16:04');
INSERT INTO `siargo_qareport` VALUES (2011615273595817984, 102812, 2007710955599679488, 202601177, 1, '2026-01-15 09:43:49');
INSERT INTO `siargo_qareport` VALUES (2011615618011090944, 102298, 2011385065982775296, 202601178, 1, '2026-01-15 09:45:12');
INSERT INTO `siargo_qareport` VALUES (2011615925017366528, 102761, 2007714830587121664, 202601179, 1, '2026-01-15 09:46:25');
INSERT INTO `siargo_qareport` VALUES (2011616546458030080, 102792, 2008118232144007168, 202601180, 1, '2026-01-15 09:48:53');
INSERT INTO `siargo_qareport` VALUES (2011621057348096000, 102745, 2011620456857980928, 202601181, 1, '2026-01-15 10:06:48');
INSERT INTO `siargo_qareport` VALUES (2011621123005730816, 102782, 2011620672684281856, 202601182, 1, '2026-01-15 10:07:04');
INSERT INTO `siargo_qareport` VALUES (2011621542134140928, 102704, 2011621384457670656, 202601183, 1, '2026-01-15 10:08:44');
INSERT INTO `siargo_qareport` VALUES (2011622388221726720, 102771, 2008417707106357248, 202601184, 1, '2026-01-15 10:12:06');
INSERT INTO `siargo_qareport` VALUES (2011622746083938304, 102851, 2011622586750717952, 202601185, 1, '2026-01-15 10:13:31');
INSERT INTO `siargo_qareport` VALUES (2011625989501472768, 102849, 2011625849470439424, 202601186, 2, '2026-01-15 10:26:24');
INSERT INTO `siargo_qareport` VALUES (2011626124381900800, 102829, 2000847525710860288, 202601187, 2, '2026-01-15 10:26:56');
INSERT INTO `siargo_qareport` VALUES (2011626228266422272, 102843, 2000847720016187392, 202601188, 2, '2026-01-15 10:27:21');
INSERT INTO `siargo_qareport` VALUES (2011704126767747072, 102858, 2008719311974027264, 202601189, 2, '2026-01-15 15:36:54');
INSERT INTO `siargo_qareport` VALUES (2011708406673297408, 102845, 2009223836984004608, 202601190, 1, '2026-01-15 15:53:54');
INSERT INTO `siargo_qareport` VALUES (2011708594146103296, 102785, 2009223836984004608, 202601191, 1, '2026-01-15 15:54:39');
INSERT INTO `siargo_qareport` VALUES (2011969838228819968, 102807, 2011969728602296320, 202601192, 2, '2026-01-16 09:12:44');
INSERT INTO `siargo_qareport` VALUES (2011971893777518592, 102777, 2000847525710860288, 202601193, 1, '2026-01-16 09:20:54');
INSERT INTO `siargo_qareport` VALUES (2011972675943911424, 102805, 2000847525710860288, 202601194, 1, '2026-01-16 09:24:01');
INSERT INTO `siargo_qareport` VALUES (2011973199166558208, 102826, 2000847627364012032, 202601195, 1, '2026-01-16 09:26:05');
INSERT INTO `siargo_qareport` VALUES (2011973469778857984, 102844, 2009223836984004608, 202601196, 2, '2026-01-16 09:27:10');
INSERT INTO `siargo_qareport` VALUES (2011985782435074048, 102857, 2011985594622529536, 202601197, 1, '2026-01-16 10:16:06');
INSERT INTO `siargo_qareport` VALUES (2011986021510402048, 102858, 2008719311974027264, 202601198, 2, '2026-01-16 10:17:03');
INSERT INTO `siargo_qareport` VALUES (2011986320039989248, 102869, 2011986157196136448, 202601199, 1, '2026-01-16 10:18:14');
INSERT INTO `siargo_qareport` VALUES (2011986527980998656, 102859, 2008719311974027264, 202601200, 1, '2026-01-16 10:19:03');
INSERT INTO `siargo_qareport` VALUES (2011994947698151424, 102848, 2008705235667505152, 202601201, 1, '2026-01-16 10:52:31');
INSERT INTO `siargo_qareport` VALUES (2012000864338563072, 102862, 2008417707106357248, 202601202, 1, '2026-01-16 11:16:01');
INSERT INTO `siargo_qareport` VALUES (2012039558290329600, 102484, 2009149718863400960, 202601203, 1, '2026-01-16 13:49:47');
INSERT INTO `siargo_qareport` VALUES (2012044288806735872, 102839, 2011385065982775296, 202601204, 1, '2026-01-16 14:08:35');
INSERT INTO `siargo_qareport` VALUES (2012078972878049280, 102856, 2012078806397734912, 202601205, 2, '2026-01-16 16:26:24');
INSERT INTO `siargo_qareport` VALUES (2012079089370648576, 102840, 2012078681608802304, 202601206, 1, '2026-01-16 16:26:52');
INSERT INTO `siargo_qareport` VALUES (2012079262620569600, 102740, 2007715086636797952, 202601207, 1, '2026-01-16 16:27:33');
INSERT INTO `siargo_qareport` VALUES (2012079418413797376, 102636, 2000847563539288064, 202601208, 1, '2026-01-16 16:28:10');
INSERT INTO `siargo_qareport` VALUES (2013071482454659072, 102831, 2013071008288591872, 202601209, 1, '2026-01-19 10:10:17');
INSERT INTO `siargo_qareport` VALUES (2013072135432294400, 102834, 2013071680379670528, 202601210, 2, '2026-01-19 10:12:52');
INSERT INTO `siargo_qareport` VALUES (2013072476173357056, 102863, 2013072266374270976, 202601211, 1, '2026-01-19 10:14:14');
INSERT INTO `siargo_qareport` VALUES (2013072620885233664, 102861, 2000847563539288064, 202601212, 2, '2026-01-19 10:14:48');
INSERT INTO `siargo_qareport` VALUES (2013084806500175872, 102790, 2013084556410605568, 202601213, 1, '2026-01-19 11:03:13');
INSERT INTO `siargo_qareport` VALUES (2013084998544773120, 102854, 2007715109323788288, 202601214, 1, '2026-01-19 11:03:59');
INSERT INTO `siargo_qareport` VALUES (2013089048413720576, 102836, 2008081806144688128, 202601215, 1, '2026-01-19 11:20:05');
INSERT INTO `siargo_qareport` VALUES (2013124360519143424, 102825, 2013124143279362048, 202601216, 1, '2026-01-19 13:40:24');
INSERT INTO `siargo_qareport` VALUES (2013124709887889408, 102815, 2007714677515997184, 202601217, 1, '2026-01-19 13:41:47');
INSERT INTO `siargo_qareport` VALUES (2013125218606632960, 102824, 2007715109323788288, 202601218, 1, '2026-01-19 13:43:48');
INSERT INTO `siargo_qareport` VALUES (2013128856594927616, 102838, 2000847563539288064, 202601219, 1, '2026-01-19 13:58:16');
INSERT INTO `siargo_qareport` VALUES (2013139093095829504, 102458, 2010905611992944640, 202601220, 1, '2026-01-19 14:38:56');
INSERT INTO `siargo_qareport` VALUES (2013139628922359808, 102773, 2013138726744346624, 202601221, 1, '2026-01-19 14:41:04');
INSERT INTO `siargo_qareport` VALUES (2013140439370944512, 102861, 2000847563539288064, 202601222, 2, '2026-01-19 14:44:17');
INSERT INTO `siargo_qareport` VALUES (2013163981848301568, 102854, 2007715109323788288, 202601223, 1, '2026-01-19 16:17:50');
INSERT INTO `siargo_qareport` VALUES (2013164528437415936, 102790, 2013084556410605568, 202601224, 1, '2026-01-19 16:20:01');
INSERT INTO `siargo_qareport` VALUES (2013164940284514304, 102790, 2013084556410605568, 202601225, 1, '2026-01-19 16:21:39');
INSERT INTO `siargo_qareport` VALUES (2013428644427190272, 102877, 2013427593464631296, 202601226, 1, '2026-01-20 09:49:31');
INSERT INTO `siargo_qareport` VALUES (2013428779857072128, 102873, 2008803112767049728, 202601227, 1, '2026-01-20 09:50:03');
INSERT INTO `siargo_qareport` VALUES (2013429062913871872, 102837, 2000847627364012032, 202601228, 1, '2026-01-20 09:51:10');
INSERT INTO `siargo_qareport` VALUES (2013429290941403136, 102850, 2000847720016187392, 202601229, 2, '2026-01-20 09:52:05');
INSERT INTO `siargo_qareport` VALUES (2013429528922017792, 102871, 2008718734284148736, 202601230, 1, '2026-01-20 09:53:02');
INSERT INTO `siargo_qareport` VALUES (2013429781448478720, 102866, 2013429632307417088, 202601231, 1, '2026-01-20 09:54:02');
INSERT INTO `siargo_qareport` VALUES (2013431425926025216, 102800, 2000847563539288064, 202601232, 1, '2026-01-20 10:00:34');
INSERT INTO `siargo_qareport` VALUES (2013438793611071488, 102879, 2013438667874226176, 202601233, 1, '2026-01-20 10:29:50');
INSERT INTO `siargo_qareport` VALUES (2013439058523312128, 102874, 2013438920664928256, 202601234, 1, '2026-01-20 10:30:54');
INSERT INTO `siargo_qareport` VALUES (2013479170456014848, 49275, 2008353170634166272, 202601235, 1, '2026-01-20 13:10:17');
INSERT INTO `siargo_qareport` VALUES (2013482430411689984, 102442, 2000847525710860288, 202601236, 1, '2026-01-20 13:23:14');
INSERT INTO `siargo_qareport` VALUES (2013502073583030272, 102875, 2000847627364012032, 202601237, 1, '2026-01-20 14:41:18');
INSERT INTO `siargo_qareport` VALUES (2013518280512098304, 102821, 2007693569572065280, 202601238, 1, '2026-01-20 15:45:42');
INSERT INTO `siargo_qareport` VALUES (2013518750265757696, 102821, 2007693569572065280, 202601239, 1, '2026-01-20 15:47:34');
INSERT INTO `siargo_qareport` VALUES (2013519005531099136, 102821, 2007693569572065280, 202601240, 1, '2026-01-20 15:48:34');
INSERT INTO `siargo_qareport` VALUES (2013528478618341376, 102886, 2007715109323788288, 202601241, 1, '2026-01-20 16:26:13');
INSERT INTO `siargo_qareport` VALUES (2013536078084820992, 102855, 2010907066472714240, 202601242, 1, '2026-01-20 16:56:25');
INSERT INTO `siargo_qareport` VALUES (2013536530553753600, 102853, 2000847563539288064, 202601243, 1, '2026-01-20 16:58:13');
INSERT INTO `siargo_qareport` VALUES (2013536798393618432, 102852, 2013536659369218048, 202601244, 1, '2026-01-20 16:59:17');
INSERT INTO `siargo_qareport` VALUES (2013536979432361984, 102878, 2008081212201881600, 202601245, 1, '2026-01-20 17:00:00');

SET FOREIGN_KEY_CHECKS = 1;
