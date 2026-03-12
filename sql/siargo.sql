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

 Date: 12/03/2026 13:46:44
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
INSERT INTO `jb_dictionary` VALUES (2015751237612802048, 'siargo_小数点保留_4位', 1993858757745381376, 0, 35, 'siargo_tofixed_4', 'code_gen_form_data_rule', '1', '1');
INSERT INTO `jb_dictionary` VALUES (2015751828305022976, '传感器', 2015751643067781120, 0, 1, '1', 'siargo_prod_type', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015751828346966016, '小流量计', 2015751643067781120, 0, 2, '2', 'siargo_prod_type', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015751828380520448, '大流量计', 2015751643067781120, 0, 3, '3', 'siargo_prod_type', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752245504053248, '产成品', 2015752122757746688, 0, 1, '1', 'siargo_rep_type', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752245566967808, '退修品', 2015752122757746688, 0, 2, '2', 'siargo_rep_type', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752466938138624, '精度待检', 2015752344154083328, 0, 1, '1', 'siargo_insp', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752466959110144, '外观待检', 2015752344154083328, 0, 2, '2', 'siargo_insp', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752467001053184, '包装待检', 2015752344154083328, 0, 3, '3', 'siargo_insp', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752467030413312, '合格待批准', 2015752344154083328, 0, 4, '4', 'siargo_insp', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752467076550656, '已完成', 2015752344154083328, 0, 5, '5', 'siargo_insp', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015752658445864960, 'G/2', 2015752612434350080, 0, 1, '2', 'siargo_pdfver', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015753847933702144, '1-70m3/h', 2015752764129742848, 0, 1, '1', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015753991030771712, '1-100m3/h', 2015752764129742848, 0, 2, '2', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015753991085297664, '3-160m3/h', 2015752764129742848, 0, 3, '3', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015753991114657792, '0.8-80m3/h', 2015752764129742848, 0, 4, '4', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015753991169183744, '0.65-65m3/h', 2015752764129742848, 0, 5, '5', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532305702912, '0.40-40m3/h', 2015752764129742848, 0, 6, '6', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532381200384, '0.25-25m3/h', 2015752764129742848, 0, 7, '7', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532444114944, '0.10-10m3/h', 2015752764129742848, 0, 8, '8', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532452503553, '4-240m3/h', 2015752764129742848, 0, 9, '9', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532515418112, '100-10000m3/h', 2015752764129742848, 0, 10, '10', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532557361152, '250-25000m3/h', 2015752764129742848, 0, 11, '11', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532590915584, '25-2500m3/h', 2015752764129742848, 0, 12, '12', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532590915586, '15-1500m3/h', 2015752764129742848, 0, 13, '13', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532653830144, '4-400m3/h', 2015752764129742848, 0, 14, '14', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532695773184, '10-1000m3/h', 2015752764129742848, 0, 15, '15', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532725133312, '1.6-160m3/h', 2015752764129742848, 0, 16, '16', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532767076352, '2.5-250m3/h', 2015752764129742848, 0, 17, '17', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532796436480, '20-1333SLPM', 2015752764129742848, 0, 18, '18', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754532796436482, '2-100m3/h', 2015752764129742848, 0, 19, '19', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754830805929984, '11', 2015754771875958784, 0, 1, '1', 'siargo_supplier ', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2015754845435662336, '22', 2015754771875958784, 0, 2, '2', 'siargo_supplier ', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2027564781282381824, '6.5-195m3/h', 2015752764129742848, 0, 20, '20', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2031898824568328192, '0.19-18m3/h', 2015752764129742848, 0, 21, '21', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2031901046018527232, '4-300m3/h', 2015752764129742848, 0, 22, '22', 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary` VALUES (2031906180438020096, '2.5-200m3/h', 2015752764129742848, 0, 23, '23', 'siargo_flow_range', '1', '0');

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
INSERT INTO `jb_dictionary_type` VALUES (2015751643067781120, 'siargo_产品类型', 1, 'siargo_prod_type', '1', '0');
INSERT INTO `jb_dictionary_type` VALUES (2015752122757746688, 'siargo_报告单类型', 1, 'siargo_rep_type', '1', '0');
INSERT INTO `jb_dictionary_type` VALUES (2015752344154083328, 'siargo_检验进度', 1, 'siargo_insp', '1', '0');
INSERT INTO `jb_dictionary_type` VALUES (2015752612434350080, 'siargo_报告单版号', 1, 'siargo_pdfver', '1', '0');
INSERT INTO `jb_dictionary_type` VALUES (2015752764129742848, 'siargo_流量范围', 1, 'siargo_flow_range', '1', '0');
INSERT INTO `jb_dictionary_type` VALUES (2015754771875958784, 'siargo_供应商', 1, 'siargo_supplier ', '1', '0');

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
INSERT INTO `jb_jbolt_file` VALUES (2013797284343304192, 'E:/siargo/webapp/upload/user/avatar/20260121/a8f980ce6c9d45b98e2d6475ce62ea84.jpg', 'upload/user/avatar/20260121/a8f980ce6c9d45b98e2d6475ce62ea84.jpg', NULL, '2026-01-21 10:14:22', 2001546811778514944, '1.jpg', 1, 70081, 'jpg');

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
INSERT INTO `jb_login_log` VALUES (2013551486393241600, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-20 17:57:39', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-20 17:57:39');
INSERT INTO `jb_login_log` VALUES (2013778740155109376, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-21 09:00:40', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 09:00:40');
INSERT INTO `jb_login_log` VALUES (2013794258362683392, 2001546811778514944, 'hanzijin', '192.168.77.110', '2026-01-21 10:02:20', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 10:02:20');
INSERT INTO `jb_login_log` VALUES (2013795831864217600, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-21 10:08:35', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 10:08:35');
INSERT INTO `jb_login_log` VALUES (2013802104793976832, 2009493497545871360, 'admin', '127.0.0.1', '2026-01-21 10:33:31', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 10:33:31');
INSERT INTO `jb_login_log` VALUES (2013842626405322752, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-21 13:14:32', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 13:14:32');
INSERT INTO `jb_login_log` VALUES (2013854396637368320, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-21 14:01:18', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 14:01:18');
INSERT INTO `jb_login_log` VALUES (2013860447361290240, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-21 14:25:21', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 14:25:21');
INSERT INTO `jb_login_log` VALUES (2013878282397536256, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-21 15:36:13', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 15:36:13');
INSERT INTO `jb_login_log` VALUES (2013898684234584064, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-21 16:57:17', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-21 16:57:17');
INSERT INTO `jb_login_log` VALUES (2014161926865408000, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-22 10:23:19', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-22 10:23:19');
INSERT INTO `jb_login_log` VALUES (2014170612329467904, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-22 10:57:50', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-22 10:57:50');
INSERT INTO `jb_login_log` VALUES (2014207304478347264, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-22 13:23:38', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-22 13:23:38');
INSERT INTO `jb_login_log` VALUES (2014505468548730880, NULL, 'houliang', '192.168.3.65', '2026-01-23 09:08:26', 2, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 09:08:26');
INSERT INTO `jb_login_log` VALUES (2014505499901153280, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-23 09:08:34', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 09:08:34');
INSERT INTO `jb_login_log` VALUES (2014575771924680704, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-01-23 13:47:48', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 13:47:48');
INSERT INTO `jb_login_log` VALUES (2014588434620731392, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-23 14:38:07', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 14:38:07');
INSERT INTO `jb_login_log` VALUES (2014590296312893440, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-23 14:45:31', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 14:45:31');
INSERT INTO `jb_login_log` VALUES (2014592186757009408, 2001546811778514944, 'hanzijin', '192.168.3.30', '2026-01-23 14:53:01', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 14:53:01');
INSERT INTO `jb_login_log` VALUES (2014602944815419392, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-23 15:35:46', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-23 15:35:46');
INSERT INTO `jb_login_log` VALUES (2015609478609489920, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-26 10:15:23', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 10:15:23');
INSERT INTO `jb_login_log` VALUES (2015613912274030592, NULL, 'houlliang', '192.168.3.65', '2026-01-26 10:33:00', 3, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 10:33:00');
INSERT INTO `jb_login_log` VALUES (2015613957807394816, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-26 10:33:10', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 10:33:10');
INSERT INTO `jb_login_log` VALUES (2015620340632113152, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-26 10:58:32', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 10:58:32');
INSERT INTO `jb_login_log` VALUES (2015624373186973696, 2001547605315665920, 'wuxiaoyu', '192.168.77.79', '2026-01-26 11:14:34', 1, '1', '11.0', 'MSIE11', 'Windows 7 or Windows Server 2008R2', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Trident', '7.0', NULL, NULL, '0', '2026-01-26 11:14:34');
INSERT INTO `jb_login_log` VALUES (2015625337738481664, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-01-26 11:18:24', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 11:18:24');
INSERT INTO `jb_login_log` VALUES (2015646744140697600, 2001547415494049792, 'fengying', '192.168.77.110', '2026-01-26 12:43:27', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 12:43:27');
INSERT INTO `jb_login_log` VALUES (2015654752459542528, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-26 13:15:17', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 13:15:17');
INSERT INTO `jb_login_log` VALUES (2015658866513924096, NULL, 'hanzijin', '192.168.3.60', '2026-01-26 13:31:38', 3, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 13:31:38');
INSERT INTO `jb_login_log` VALUES (2015658892539580416, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-26 13:31:44', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 13:31:44');
INSERT INTO `jb_login_log` VALUES (2015668145497165824, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-26 14:08:30', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 14:08:30');
INSERT INTO `jb_login_log` VALUES (2015687571319672832, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-26 15:25:41', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 15:25:41');
INSERT INTO `jb_login_log` VALUES (2015690272011046912, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-26 15:36:25', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 15:36:25');
INSERT INTO `jb_login_log` VALUES (2015691766198292480, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-01-26 15:42:21', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-26 15:42:21');
INSERT INTO `jb_login_log` VALUES (2015967362836975616, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-27 09:57:29', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-27 09:57:29');
INSERT INTO `jb_login_log` VALUES (2015969298697015296, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-27 10:05:10', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-27 10:05:10');
INSERT INTO `jb_login_log` VALUES (2016042823462014976, 2001546811778514944, 'hanzijin', '192.168.77.110', '2026-01-27 14:57:20', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-27 14:57:20');
INSERT INTO `jb_login_log` VALUES (2016044083766480896, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-27 15:02:21', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-27 15:02:21');
INSERT INTO `jb_login_log` VALUES (2016047597490130944, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-27 15:16:18', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-27 15:16:18');
INSERT INTO `jb_login_log` VALUES (2016318919621332992, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-28 09:14:26', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 09:14:26');
INSERT INTO `jb_login_log` VALUES (2016329483957424128, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-28 09:56:25', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 09:56:25');
INSERT INTO `jb_login_log` VALUES (2016344040415285248, 2001546811778514944, 'hanzijin', '192.168.77.122', '2026-01-28 10:54:16', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 10:54:16');
INSERT INTO `jb_login_log` VALUES (2016391704808902656, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-28 14:03:40', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 14:03:40');
INSERT INTO `jb_login_log` VALUES (2016414511395622912, NULL, 'hanzijin', '192.168.77.91', '2026-01-28 15:34:17', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 15:34:17');
INSERT INTO `jb_login_log` VALUES (2016414534497849344, NULL, 'hanzijin', '192.168.77.91', '2026-01-28 15:34:23', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 15:34:23');
INSERT INTO `jb_login_log` VALUES (2016414574318571520, NULL, 'HHANZIJIN', '192.168.77.91', '2026-01-28 15:34:32', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 15:34:32');
INSERT INTO `jb_login_log` VALUES (2016414606090424320, 2001546811778514944, 'hanzijin', '192.168.77.91', '2026-01-28 15:34:40', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 15:34:40');
INSERT INTO `jb_login_log` VALUES (2016414650466160640, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-28 15:34:51', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 15:34:51');
INSERT INTO `jb_login_log` VALUES (2016440216372236288, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-28 17:16:26', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-28 17:16:26');
INSERT INTO `jb_login_log` VALUES (2016680814337445888, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-29 09:12:29', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 09:12:29');
INSERT INTO `jb_login_log` VALUES (2016681458259578880, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-01-29 09:15:02', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 09:15:02');
INSERT INTO `jb_login_log` VALUES (2016699329844989952, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-29 10:26:03', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 10:26:03');
INSERT INTO `jb_login_log` VALUES (2016751470454362112, NULL, 'haznijin', '192.168.77.159', '2026-01-29 13:53:15', 3, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 13:53:15');
INSERT INTO `jb_login_log` VALUES (2016751499395059712, NULL, 'haznijin', '192.168.77.159', '2026-01-29 13:53:22', 3, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 13:53:22');
INSERT INTO `jb_login_log` VALUES (2016751543317811200, 2001546811778514944, 'hanzijin', '192.168.77.159', '2026-01-29 13:53:32', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 13:53:32');
INSERT INTO `jb_login_log` VALUES (2016767150222331904, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-29 14:55:33', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 14:55:33');
INSERT INTO `jb_login_log` VALUES (2016776677101522944, 2001546811778514944, 'hanzijin', '192.168.77.160', '2026-01-29 15:33:24', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 15:33:24');
INSERT INTO `jb_login_log` VALUES (2016787074701447168, 2009493497545871360, 'admin', '127.0.0.1', '2026-01-29 16:14:43', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 16:14:43');
INSERT INTO `jb_login_log` VALUES (2016792669542273024, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-29 16:36:57', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 16:36:57');
INSERT INTO `jb_login_log` VALUES (2016798990165463040, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-29 17:02:04', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 17:02:04');
INSERT INTO `jb_login_log` VALUES (2016803091527880704, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-29 17:18:22', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-29 17:18:22');
INSERT INTO `jb_login_log` VALUES (2017042099625381888, 2009493497545871360, 'admin', '192.168.77.150', '2026-01-30 09:08:06', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 09:08:06');
INSERT INTO `jb_login_log` VALUES (2017044877626167296, 2001548276093927424, 'houliang', '192.168.3.65', '2026-01-30 09:19:08', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 09:19:08');
INSERT INTO `jb_login_log` VALUES (2017055443488985088, NULL, 'hanzijin', '192.168.3.60', '2026-01-30 10:01:07', 2, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 10:01:07');
INSERT INTO `jb_login_log` VALUES (2017055469237817344, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-01-30 10:01:14', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 10:01:14');
INSERT INTO `jb_login_log` VALUES (2017063531243753472, 2009493497545871360, 'admin', '127.0.0.1', '2026-01-30 10:33:16', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 10:33:16');
INSERT INTO `jb_login_log` VALUES (2017126833256779776, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-01-30 14:44:48', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 14:44:48');
INSERT INTO `jb_login_log` VALUES (2017129636880568320, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-01-30 14:55:57', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 14:55:57');
INSERT INTO `jb_login_log` VALUES (2017152652830167040, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-01-30 16:27:24', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-01-30 16:27:24');
INSERT INTO `jb_login_log` VALUES (2017767364928327680, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-01 09:10:03', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-01 09:10:03');
INSERT INTO `jb_login_log` VALUES (2018135114515337216, 2009493497545871360, 'admin', '127.0.0.1', '2026-02-02 09:31:21', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 09:31:21');
INSERT INTO `jb_login_log` VALUES (2018140641026756608, NULL, 'hanzijin', '192.168.77.104', '2026-02-02 09:53:19', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 09:53:19');
INSERT INTO `jb_login_log` VALUES (2018140660979060736, 2001546811778514944, 'hanzijin', '192.168.77.104', '2026-02-02 09:53:23', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 09:53:23');
INSERT INTO `jb_login_log` VALUES (2018148465505062912, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-02 10:24:24', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 10:24:24');
INSERT INTO `jb_login_log` VALUES (2018154796278468608, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-02-02 10:49:34', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 10:49:34');
INSERT INTO `jb_login_log` VALUES (2018191540533186560, 2001547415494049792, 'fengying', '192.168.77.133', '2026-02-02 13:15:34', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 13:15:34');
INSERT INTO `jb_login_log` VALUES (2018204617202847744, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-02-02 14:07:32', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 14:07:32');
INSERT INTO `jb_login_log` VALUES (2018234264665837568, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-02 16:05:20', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-02 16:05:20');
INSERT INTO `jb_login_log` VALUES (2018498895103774720, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-03 09:36:53', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-03 09:36:53');
INSERT INTO `jb_login_log` VALUES (2018522944525684736, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-02-03 11:12:27', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-03 11:12:27');
INSERT INTO `jb_login_log` VALUES (2018605703302598656, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-03 16:41:18', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-03 16:41:18');
INSERT INTO `jb_login_log` VALUES (2018858820044705792, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-04 09:27:06', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-04 09:27:06');
INSERT INTO `jb_login_log` VALUES (2018871753948647424, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-02-04 10:18:30', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-04 10:18:30');
INSERT INTO `jb_login_log` VALUES (2018919674631016448, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-04 13:28:55', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-04 13:28:55');
INSERT INTO `jb_login_log` VALUES (2018932743729958912, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-02-04 14:20:51', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-04 14:20:51');
INSERT INTO `jb_login_log` VALUES (2018965970112860160, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-04 16:32:53', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-04 16:32:53');
INSERT INTO `jb_login_log` VALUES (2019226325443530752, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-05 09:47:26', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-05 09:47:26');
INSERT INTO `jb_login_log` VALUES (2019226625386598400, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-02-05 09:48:38', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-05 09:48:38');
INSERT INTO `jb_login_log` VALUES (2019237907871420416, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-05 10:33:28', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-05 10:33:28');
INSERT INTO `jb_login_log` VALUES (2019298350673547264, 2001546811778514944, 'hanzijin', '192.168.3.16', '2026-02-05 14:33:38', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-05 14:33:38');
INSERT INTO `jb_login_log` VALUES (2019306767614070784, 2001546811778514944, 'hanzijin', '192.168.3.11', '2026-02-05 15:07:05', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-05 15:07:05');
INSERT INTO `jb_login_log` VALUES (2019578125468160000, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-02-06 09:05:22', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-06 09:05:22');
INSERT INTO `jb_login_log` VALUES (2019587777220169728, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-06 09:43:43', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-06 09:43:43');
INSERT INTO `jb_login_log` VALUES (2019670730377908224, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-06 15:13:20', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-06 15:13:20');
INSERT INTO `jb_login_log` VALUES (2019674551321743360, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-02-06 15:28:31', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-06 15:28:31');
INSERT INTO `jb_login_log` VALUES (2019691588995633152, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-02-06 16:36:14', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-06 16:36:14');
INSERT INTO `jb_login_log` VALUES (2020038588463173632, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-07 15:35:05', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-07 15:35:05');
INSERT INTO `jb_login_log` VALUES (2020061243593576448, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-07 17:05:06', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-07 17:05:06');
INSERT INTO `jb_login_log` VALUES (2020416059234701312, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-08 16:35:01', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-08 16:35:01');
INSERT INTO `jb_login_log` VALUES (2020661146032132096, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-09 08:48:54', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-09 08:48:54');
INSERT INTO `jb_login_log` VALUES (2020729678220611584, 2001547415494049792, 'fengying', '192.168.77.152', '2026-02-09 13:21:13', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-09 13:21:13');
INSERT INTO `jb_login_log` VALUES (2020762184630259712, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-02-09 15:30:23', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-09 15:30:23');
INSERT INTO `jb_login_log` VALUES (2020773903897317376, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-09 16:16:58', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-09 16:16:58');
INSERT INTO `jb_login_log` VALUES (2020782600363036672, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-02-09 16:51:31', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-09 16:51:31');
INSERT INTO `jb_login_log` VALUES (2020783123321442304, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-09 16:53:36', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-09 16:53:36');
INSERT INTO `jb_login_log` VALUES (2021089779859181568, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-10 13:12:08', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-10 13:12:08');
INSERT INTO `jb_login_log` VALUES (2021124771540750336, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-10 15:31:11', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-10 15:31:11');
INSERT INTO `jb_login_log` VALUES (2021400773877682176, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-11 09:47:55', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-11 09:47:55');
INSERT INTO `jb_login_log` VALUES (2021421227862511616, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-11 11:09:12', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-11 11:09:12');
INSERT INTO `jb_login_log` VALUES (2021501825188745216, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-11 16:29:27', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-11 16:29:27');
INSERT INTO `jb_login_log` VALUES (2021772741470703616, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-12 10:25:59', 1, '1', '144.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-12 10:25:59');
INSERT INTO `jb_login_log` VALUES (2021855305363476480, NULL, 'luoxiongfei', '192.168.3.30', '2026-02-12 15:54:04', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-12 15:54:04');
INSERT INTO `jb_login_log` VALUES (2021855358941515776, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-12 15:54:16', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-12 15:54:16');
INSERT INTO `jb_login_log` VALUES (2022214444493164544, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-13 15:41:09', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-13 15:41:09');
INSERT INTO `jb_login_log` VALUES (2026098264560750592, 2009493497545871360, 'admin', '127.0.0.1', '2026-02-24 08:54:04', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-24 08:54:04');
INSERT INTO `jb_login_log` VALUES (2026099935953145856, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-02-24 09:00:42', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-24 09:00:42');
INSERT INTO `jb_login_log` VALUES (2026103007374856192, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-24 09:12:55', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-24 09:12:55');
INSERT INTO `jb_login_log` VALUES (2026116474764906496, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-02-24 10:06:26', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-24 10:06:26');
INSERT INTO `jb_login_log` VALUES (2026166587453460480, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-02-24 13:25:33', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-24 13:25:33');
INSERT INTO `jb_login_log` VALUES (2026214951851249664, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-02-24 16:37:44', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-24 16:37:44');
INSERT INTO `jb_login_log` VALUES (2026491758370476032, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-02-25 10:57:40', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-25 10:57:40');
INSERT INTO `jb_login_log` VALUES (2026544308528467968, 2001546811778514944, 'hanzijin', '192.168.77.173', '2026-02-25 14:26:29', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-25 14:26:29');
INSERT INTO `jb_login_log` VALUES (2026566178954924032, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-02-25 15:53:23', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-25 15:53:23');
INSERT INTO `jb_login_log` VALUES (2026831056366063616, NULL, 'houliang', '192.168.3.65', '2026-02-26 09:25:55', 2, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 09:25:55');
INSERT INTO `jb_login_log` VALUES (2026831091031986176, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-26 09:26:03', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 09:26:03');
INSERT INTO `jb_login_log` VALUES (2026863509449330688, NULL, 'yingfeng', '192.168.77.194', '2026-02-26 11:34:53', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 11:34:53');
INSERT INTO `jb_login_log` VALUES (2026864689697116160, NULL, 'fengying', '192.168.77.194', '2026-02-26 11:39:34', 2, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 11:39:34');
INSERT INTO `jb_login_log` VALUES (2026864816486731776, NULL, 'fengying', '192.168.77.194', '2026-02-26 11:40:04', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 11:40:04');
INSERT INTO `jb_login_log` VALUES (2026865092492906496, NULL, 'fengying', '192.168.77.194', '2026-02-26 11:41:10', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 11:41:10');
INSERT INTO `jb_login_log` VALUES (2026877393363128320, NULL, 'yingfeng', '192.168.77.194', '2026-02-26 12:30:03', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 12:30:03');
INSERT INTO `jb_login_log` VALUES (2026877529870946304, 2001547415494049792, 'fengying', '192.168.77.194', '2026-02-26 12:30:35', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 12:30:35');
INSERT INTO `jb_login_log` VALUES (2026890404358377472, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-26 13:21:45', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 13:21:45');
INSERT INTO `jb_login_log` VALUES (2026921228755652608, 2001546811778514944, 'hanzijin', '192.168.77.197', '2026-02-26 15:24:14', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 15:24:14');
INSERT INTO `jb_login_log` VALUES (2026926034052304896, NULL, 'luoxiongfei', '192.168.3.30', '2026-02-26 15:43:20', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 15:43:20');
INSERT INTO `jb_login_log` VALUES (2026926076846788608, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-26 15:43:30', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 15:43:30');
INSERT INTO `jb_login_log` VALUES (2026984622116425728, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-26 19:36:08', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-26 19:36:08');
INSERT INTO `jb_login_log` VALUES (2027191257086676992, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-27 09:17:14', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 09:17:14');
INSERT INTO `jb_login_log` VALUES (2027200096477499392, NULL, 'hanzijin', '192.168.3.9', '2026-02-27 09:52:21', 2, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 09:52:21');
INSERT INTO `jb_login_log` VALUES (2027200144707801088, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-02-27 09:52:33', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 09:52:33');
INSERT INTO `jb_login_log` VALUES (2027219919030833152, 2001546811778514944, 'hanzijin', '192.168.3.16', '2026-02-27 11:11:07', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 11:11:07');
INSERT INTO `jb_login_log` VALUES (2027268069091299328, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-27 14:22:27', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 14:22:27');
INSERT INTO `jb_login_log` VALUES (2027288183421390848, NULL, 'wuxaioyu', '192.168.3.11', '2026-02-27 15:42:23', 3, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 15:42:23');
INSERT INTO `jb_login_log` VALUES (2027312325684219904, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-27 17:18:19', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-27 17:18:19');
INSERT INTO `jb_login_log` VALUES (2027561936059486208, 2001548276093927424, 'houliang', '192.168.3.65', '2026-02-28 09:50:10', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-28 09:50:10');
INSERT INTO `jb_login_log` VALUES (2027573690994905088, 2001546811778514944, 'hanzijin', '192.168.77.209', '2026-02-28 10:36:53', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-28 10:36:53');
INSERT INTO `jb_login_log` VALUES (2027583225981685760, 2001546811778514944, 'hanzijin', '192.168.77.202', '2026-02-28 11:14:46', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-28 11:14:46');
INSERT INTO `jb_login_log` VALUES (2027647132767211520, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-02-28 15:28:43', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-02-28 15:28:43');
INSERT INTO `jb_login_log` VALUES (2028277700882190336, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-02 09:14:22', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-02 09:14:22');
INSERT INTO `jb_login_log` VALUES (2028373580230545408, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-02 15:35:22', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-02 15:35:22');
INSERT INTO `jb_login_log` VALUES (2028392998071291904, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-02 16:52:31', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-02 16:52:31');
INSERT INTO `jb_login_log` VALUES (2028401184182489088, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-02 17:25:03', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-02 17:25:03');
INSERT INTO `jb_login_log` VALUES (2028644762909528064, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-03 09:32:57', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-03 09:32:57');
INSERT INTO `jb_login_log` VALUES (2028671675128467456, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-03-03 11:19:53', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-03 11:19:53');
INSERT INTO `jb_login_log` VALUES (2028735148508172288, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-03 15:32:06', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-03 15:32:06');
INSERT INTO `jb_login_log` VALUES (2028742832221442048, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-03 16:02:38', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-03 16:02:38');
INSERT INTO `jb_login_log` VALUES (2028743153689677824, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-03-03 16:03:55', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-03 16:03:55');
INSERT INTO `jb_login_log` VALUES (2028764354306756608, 2001546811778514944, 'hanzijin', '192.168.77.234', '2026-03-03 17:28:09', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-03 17:28:09');
INSERT INTO `jb_login_log` VALUES (2029030088983302144, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-04 11:04:05', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-04 11:04:05');
INSERT INTO `jb_login_log` VALUES (2029033458393534464, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-03-04 11:17:29', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-04 11:17:29');
INSERT INTO `jb_login_log` VALUES (2029038455512485888, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-04 11:37:20', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-04 11:37:20');
INSERT INTO `jb_login_log` VALUES (2029363573681934336, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-05 09:09:14', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 09:09:14');
INSERT INTO `jb_login_log` VALUES (2029374404230500352, 2009493497545871360, 'admin', '127.0.0.1', '2026-03-05 09:52:17', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 09:52:17');
INSERT INTO `jb_login_log` VALUES (2029381703204458496, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-03-05 10:21:17', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 10:21:17');
INSERT INTO `jb_login_log` VALUES (2029395486660481024, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-03-05 11:16:03', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 11:16:03');
INSERT INTO `jb_login_log` VALUES (2029399742188736512, NULL, 'wuxiaoyu', '192.168.3.11', '2026-03-05 11:32:58', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 11:32:58');
INSERT INTO `jb_login_log` VALUES (2029399786518335488, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-03-05 11:33:08', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 11:33:08');
INSERT INTO `jb_login_log` VALUES (2029428948234784768, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-05 13:29:01', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 13:29:01');
INSERT INTO `jb_login_log` VALUES (2029470798819741696, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-05 16:15:19', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 16:15:19');
INSERT INTO `jb_login_log` VALUES (2029471041036603392, 2001547415494049792, 'fengying', '192.168.77.3', '2026-03-05 16:16:17', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-05 16:16:17');
INSERT INTO `jb_login_log` VALUES (2029752581847109632, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-06 10:55:01', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 10:55:01');
INSERT INTO `jb_login_log` VALUES (2029754187934191616, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-06 11:01:24', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 11:01:24');
INSERT INTO `jb_login_log` VALUES (2029792870083186688, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-06 13:35:07', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 13:35:07');
INSERT INTO `jb_login_log` VALUES (2029795020641259520, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-03-06 13:43:39', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 13:43:39');
INSERT INTO `jb_login_log` VALUES (2029796176142651392, 2001546811778514944, 'hanzijin', '192.168.77.209', '2026-03-06 13:48:15', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 13:48:15');
INSERT INTO `jb_login_log` VALUES (2029805957528014848, NULL, 'luoxiongfei', '192.168.3.30', '2026-03-06 14:27:07', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 14:27:07');
INSERT INTO `jb_login_log` VALUES (2029806005317914624, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-06 14:27:18', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 14:27:18');
INSERT INTO `jb_login_log` VALUES (2029825753141334016, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-03-06 15:45:47', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-06 15:45:47');
INSERT INTO `jb_login_log` VALUES (2030198278971052032, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-07 16:26:04', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-07 16:26:04');
INSERT INTO `jb_login_log` VALUES (2030832228655550464, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-09 10:25:09', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 10:25:09');
INSERT INTO `jb_login_log` VALUES (2030895106901266432, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-03-09 14:35:00', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 14:35:00');
INSERT INTO `jb_login_log` VALUES (2030904632123707392, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-09 15:12:51', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 15:12:51');
INSERT INTO `jb_login_log` VALUES (2030904780878893056, 2009493497545871360, 'admin', '127.0.0.1', '2026-03-09 15:13:27', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 15:13:27');
INSERT INTO `jb_login_log` VALUES (2030922949232283648, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-09 16:25:38', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 16:25:38');
INSERT INTO `jb_login_log` VALUES (2030927955939414016, 2001547605315665920, 'wuxiaoyu', '192.168.3.11', '2026-03-09 16:45:32', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 16:45:32');
INSERT INTO `jb_login_log` VALUES (2030936956320206848, 2001547605315665920, 'wuxiaoyu', '192.168.77.81', '2026-03-09 17:21:18', 1, '1', '135.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 17:21:18');
INSERT INTO `jb_login_log` VALUES (2030937730303512576, 2001546811778514944, 'hanzijin', '192.168.77.221', '2026-03-09 17:24:23', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-09 17:24:23');
INSERT INTO `jb_login_log` VALUES (2031203645498707968, 2001546811778514944, 'hanzijin', '192.168.3.31', '2026-03-10 11:01:02', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 11:01:02');
INSERT INTO `jb_login_log` VALUES (2031209135498711040, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-10 11:22:51', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 11:22:51');
INSERT INTO `jb_login_log` VALUES (2031235112715014144, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-10 13:06:04', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 13:06:04');
INSERT INTO `jb_login_log` VALUES (2031235568560361472, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-10 13:07:53', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 13:07:53');
INSERT INTO `jb_login_log` VALUES (2031241584316370944, 2009493497545871360, 'admin', '127.0.0.1', '2026-03-10 13:31:47', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 13:31:47');
INSERT INTO `jb_login_log` VALUES (2031273097028292608, 2001546811778514944, 'hanzijin', '192.168.77.11', '2026-03-10 15:37:00', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 15:37:00');
INSERT INTO `jb_login_log` VALUES (2031292076438441984, NULL, 'hanzijin', '192.168.77.11', '2026-03-10 16:52:25', 2, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 16:52:25');
INSERT INTO `jb_login_log` VALUES (2031292102417960960, 2001546811778514944, 'hanzijin', '192.168.77.11', '2026-03-10 16:52:31', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-10 16:52:31');
INSERT INTO `jb_login_log` VALUES (2031536635407880192, 2009493497545871360, 'admin', '127.0.0.1', '2026-03-11 09:04:13', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 09:04:13');
INSERT INTO `jb_login_log` VALUES (2031537205971636224, NULL, 'houl', '192.168.3.65', '2026-03-11 09:06:29', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 09:06:29');
INSERT INTO `jb_login_log` VALUES (2031537234245439488, NULL, 'houl', '192.168.3.65', '2026-03-11 09:06:35', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 09:06:35');
INSERT INTO `jb_login_log` VALUES (2031537286158340096, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-11 09:06:48', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 09:06:48');
INSERT INTO `jb_login_log` VALUES (2031542462340517888, NULL, 'hanzijin', '192.168.3.9', '2026-03-11 09:27:22', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 09:27:22');
INSERT INTO `jb_login_log` VALUES (2031542489863540736, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-03-11 09:27:28', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 09:27:28');
INSERT INTO `jb_login_log` VALUES (2031558959683194880, 2001546811778514944, 'hanzijin', '192.168.3.60', '2026-03-11 10:32:55', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 10:32:55');
INSERT INTO `jb_login_log` VALUES (2031610789549166592, 2001548007356481536, 'luoxiongfei', '192.168.3.30', '2026-03-11 13:58:52', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 13:58:52');
INSERT INTO `jb_login_log` VALUES (2031642425087086592, 2001546811778514944, 'hanzijin', '192.168.3.9', '2026-03-11 16:04:35', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-11 16:04:35');
INSERT INTO `jb_login_log` VALUES (2031893954159300608, 2001546811778514944, 'hanzijin', '127.0.0.1', '2026-03-12 08:44:04', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 08:44:04');
INSERT INTO `jb_login_log` VALUES (2031902918171283456, NULL, 'houl', '192.168.3.65', '2026-03-12 09:19:41', 3, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 09:19:41');
INSERT INTO `jb_login_log` VALUES (2031902960420507648, 2001548276093927424, 'houliang', '192.168.3.65', '2026-03-12 09:19:51', 1, '1', '145.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 09:19:51');
INSERT INTO `jb_login_log` VALUES (2031904262923866112, 2009493497545871360, 'admin', '127.0.0.1', '2026-03-12 09:25:02', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 09:25:02');
INSERT INTO `jb_login_log` VALUES (2031919808503468032, 2001546811778514944, 'hanzijin', '127.0.0.1', '2026-03-12 10:26:48', 1, '1', '143.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', NULL, 'XXXX', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 10:26:48');
INSERT INTO `jb_login_log` VALUES (2031937923417231360, 2001546811778514944, 'hanzijin', '192.168.77.3', '2026-03-12 11:38:47', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 11:38:47');
INSERT INTO `jb_login_log` VALUES (2031937974868758528, 2009493497545871360, 'admin', '192.168.77.3', '2026-03-12 11:38:59', 1, '1', '139.0.0.0', 'MSEdge', 'Windows 10 or Windows Server 2016', '内网IP', '0内网IP', NULL, NULL, NULL, '0', 'Windows', 'Webkit', '537.36', NULL, NULL, '0', '2026-03-12 11:38:59');

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
INSERT INTO `jb_online_user` VALUES (2029471041074352128, 'e8bbea9093524ff791703eeaa4056ff42026877529942249472', 2001547415494049792, 2029471041036603392, '2026-02-26 12:30:35', '2026-03-12 13:27:03', '2026-03-12 16:16:17', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2030936956492173313, '6c80abdc89004d0faf67bdef189bb72f2030936956492173312', 2001547605315665920, 2030936956320206848, '2026-03-09 17:21:18', '2026-03-12 11:57:18', '2026-03-16 17:21:18', '1', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2031610789725327361, '766c9533f1cc49768736f69e4e6238552031610789725327360', 2001548007356481536, 2031610789549166592, '2026-03-11 13:58:52', '2026-03-11 14:45:21', '2026-03-18 13:58:52', '1', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2031902960672165889, '5506fbe5488a4962beec2bd68db545e72031902960672165888', 2001548276093927424, 2031902960420507648, '2026-03-12 09:19:51', '2026-03-12 13:45:27', '2026-03-12 17:19:51', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2031937974994587649, '7161a59b7ddc4be0a527c41a7dc2de972031937974994587648', 2009493497545871360, 2031937974868758528, '2026-03-12 11:38:59', '2026-03-12 13:43:06', '2026-03-12 19:38:59', '0', 1, NULL);

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
INSERT INTO `jb_permission` VALUES (2018135260934295552, '到货单管理', 1993873896657850368, '/admin/siargo/imi', NULL, 3, 2, 'imi', '1', '0', '0', 1, NULL, 1, '1992880780549791744');

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
INSERT INTO `jb_role` VALUES (2031938046041903104, '批准', '3', 0);

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
INSERT INTO `jb_role_permission` VALUES (2018135426701578240, 2002940469221724160, 1992880779761262592);
INSERT INTO `jb_role_permission` VALUES (2018135426701578241, 2002940469221724160, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2018135426701578242, 2002940469221724160, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2018135426701578243, 2002940469221724160, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2018135426701578244, 2002940469221724160, 2018135260934295552);
INSERT INTO `jb_role_permission` VALUES (2018135426701578245, 2002940469221724160, 1992880779778039808);
INSERT INTO `jb_role_permission` VALUES (2018135426701578246, 2002940469221724160, 1992880779794817024);
INSERT INTO `jb_role_permission` VALUES (2018135426701578247, 2002940469221724160, 1992880779811594240);
INSERT INTO `jb_role_permission` VALUES (2018135426701578248, 2002940469221724160, 1992880779832565760);
INSERT INTO `jb_role_permission` VALUES (2018135426701578249, 2002940469221724160, 1992880779849342976);
INSERT INTO `jb_role_permission` VALUES (2018135426701578250, 2002940469221724160, 1992880779866120192);
INSERT INTO `jb_role_permission` VALUES (2018135426701578251, 2002940469221724160, 1992880779878703104);
INSERT INTO `jb_role_permission` VALUES (2018135426701578252, 2002940469221724160, 1992880779899674624);
INSERT INTO `jb_role_permission` VALUES (2018135426701578253, 2002940469221724160, 1992880779945811968);
INSERT INTO `jb_role_permission` VALUES (2018135426701578254, 2002940469221724160, 1992880779958394880);
INSERT INTO `jb_role_permission` VALUES (2018135426701578255, 2002940469221724160, 1992880779979366400);
INSERT INTO `jb_role_permission` VALUES (2018135426701578256, 2002940469221724160, 1992880780004532224);
INSERT INTO `jb_role_permission` VALUES (2018135426701578257, 2002940469221724160, 1992880780096806912);
INSERT INTO `jb_role_permission` VALUES (2018135426701578258, 2002940469221724160, 1992880780121972736);
INSERT INTO `jb_role_permission` VALUES (2018135426701578259, 2002940469221724160, 1992880780138749952);
INSERT INTO `jb_role_permission` VALUES (2018135426701578260, 2002940469221724160, 1992880780155527168);
INSERT INTO `jb_role_permission` VALUES (2018135426701578261, 2002940469221724160, 1992880780168110080);
INSERT INTO `jb_role_permission` VALUES (2018135426701578262, 2002940469221724160, 1992880780180692992);
INSERT INTO `jb_role_permission` VALUES (2018135426701578263, 2002940469221724160, 1992880780197470208);
INSERT INTO `jb_role_permission` VALUES (2018135426701578264, 2002940469221724160, 1992880780214247424);
INSERT INTO `jb_role_permission` VALUES (2018135426701578265, 2002940469221724160, 1992880780444934144);
INSERT INTO `jb_role_permission` VALUES (2018135426701578266, 2002940469221724160, 1992880780461711360);
INSERT INTO `jb_role_permission` VALUES (2031536689715728384, 2001546465781989376, 1992880779761262592);
INSERT INTO `jb_role_permission` VALUES (2031536689715728385, 2001546465781989376, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2031536689715728386, 2001546465781989376, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2031536689715728387, 2001546465781989376, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2031536689715728388, 2001546465781989376, 2018135260934295552);
INSERT INTO `jb_role_permission` VALUES (2031536689715728389, 2001546465781989376, 1992880780096806912);
INSERT INTO `jb_role_permission` VALUES (2031536689715728390, 2001546465781989376, 1992880780121972736);
INSERT INTO `jb_role_permission` VALUES (2031536689715728391, 2001546465781989376, 1992880780138749952);
INSERT INTO `jb_role_permission` VALUES (2031536689715728392, 2001546465781989376, 1992880780155527168);
INSERT INTO `jb_role_permission` VALUES (2031536689715728393, 2001546465781989376, 1992880780168110080);
INSERT INTO `jb_role_permission` VALUES (2031536689715728394, 2001546465781989376, 1992880780180692992);
INSERT INTO `jb_role_permission` VALUES (2031969369771069440, 2031938046041903104, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2031969369771069441, 2031938046041903104, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2031969369771069442, 2031938046041903104, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2031969369771069443, 2031938046041903104, 2018135260934295552);

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
INSERT INTO `jb_system_log` VALUES (2017042269708603392, '<span class=\'text-danger\'>[siargo(admin)]</span>更新字典数据<span class=\'text-danger\'>[20-1333SLPM]</span>的顺序', 2, 'admin/dictionary/show/2015754532796436480', 2009493497545871360, 'siargo', 2, '2026-01-30 09:08:47', 2015754532796436480, 1);
INSERT INTO `jb_system_log` VALUES (2017042269775712256, '<span class=\'text-danger\'>[siargo(admin)]</span>更新字典数据<span class=\'text-danger\'>[2-100m3/h]</span>的顺序', 2, 'admin/dictionary/show/2015754532796436482', 2009493497545871360, 'siargo', 2, '2026-01-30 09:08:47', 2015754532796436482, 1);
INSERT INTO `jb_system_log` VALUES (2018135260997210112, '<span class=\'text-danger\'>[siargo(admin)]</span>新增权限资源<span class=\'text-danger\'>[到货单管理]</span>', 1, 'admin/permission/show/2018135260934295552', 2009493497545871360, 'siargo', 3, '2026-02-02 09:31:56', 2018135260934295552, 1);
INSERT INTO `jb_system_log` VALUES (2018135371496148992, '<span class=\'text-danger\'>[siargo(admin)]</span>更新角色<span class=\'text-danger\'>[质检]</span>的可用权限设置', 2, 'admin/role/show/2001546465781989376', 2009493497545871360, 'siargo', 5, '2026-02-02 09:32:22', 2001546465781989376, 1);
INSERT INTO `jb_system_log` VALUES (2018135426743521280, '<span class=\'text-danger\'>[siargo(admin)]</span>更新角色<span class=\'text-danger\'>[管理员]</span>的可用权限设置', 2, 'admin/role/show/2002940469221724160', 2009493497545871360, 'siargo', 5, '2026-02-02 09:32:36', 2002940469221724160, 1);
INSERT INTO `jb_system_log` VALUES (2018135538878238720, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[侯亮]</span>', 2, 'admin/user/show/2001548276093927424', 2009493497545871360, 'siargo', 4, '2026-02-02 09:33:02', 2001548276093927424, 1);
INSERT INTO `jb_system_log` VALUES (2018135561464565760, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[石珍云]</span>', 2, 'admin/user/show/2001548170967891968', 2009493497545871360, 'siargo', 4, '2026-02-02 09:33:08', 2001548170967891968, 1);
INSERT INTO `jb_system_log` VALUES (2018135586768801792, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[罗雄飞]</span>', 2, 'admin/user/show/2001548007356481536', 2009493497545871360, 'siargo', 4, '2026-02-02 09:33:14', 2001548007356481536, 1);
INSERT INTO `jb_system_log` VALUES (2018135607778070528, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[梁鹏]</span>', 2, 'admin/user/show/2001547886069792768', 2009493497545871360, 'siargo', 4, '2026-02-02 09:33:19', 2001547886069792768, 1);
INSERT INTO `jb_system_log` VALUES (2018135656364888064, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[韩子衿]</span>', 2, 'admin/user/show/2001546811778514944', 2009493497545871360, 'siargo', 4, '2026-02-02 09:33:30', 2001546811778514944, 1);
INSERT INTO `jb_system_log` VALUES (2026545731987165184, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新字典数据<span class=\'text-danger\'>[外观待检]</span>', 2, 'admin/dictionary/show/2015752466959110144', 2001546811778514944, '韩子衿', 2, '2026-02-25 14:32:09', 2015752466959110144, 1);
INSERT INTO `jb_system_log` VALUES (2027564781345296384, '<span class=\'text-danger\'>[吴小雨(wuxiaoyu)]</span>新增字典数据<span class=\'text-danger\'>[6.5-195m3/h]</span>', 1, 'admin/dictionary/show/2027564781282381824', 2001547605315665920, '吴小雨', 2, '2026-02-28 10:01:29', 2027564781282381824, 1);
INSERT INTO `jb_system_log` VALUES (2031296688272822272, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新角色<span class=\'text-danger\'>[质检]</span>的可用权限设置', 2, 'admin/role/show/2001546465781989376', 2001546811778514944, '韩子衿', 5, '2026-03-10 17:10:45', 2001546465781989376, 1);
INSERT INTO `jb_system_log` VALUES (2031296757478838272, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[韩子衿]</span>', 2, 'admin/user/show/2001546811778514944', 2001546811778514944, '韩子衿', 4, '2026-03-10 17:11:01', 2001546811778514944, 1);
INSERT INTO `jb_system_log` VALUES (2031536689787031552, '<span class=\'text-danger\'>[siargo(admin)]</span>更新角色<span class=\'text-danger\'>[质检]</span>的可用权限设置', 2, 'admin/role/show/2001546465781989376', 2009493497545871360, 'siargo', 5, '2026-03-11 09:04:26', 2001546465781989376, 1);
INSERT INTO `jb_system_log` VALUES (2031536728592732160, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[韩子衿]</span>', 2, 'admin/user/show/2001546811778514944', 2009493497545871360, 'siargo', 4, '2026-03-11 09:04:35', 2001546811778514944, 1);
INSERT INTO `jb_system_log` VALUES (2031898824627048448, '<span class=\'text-danger\'>[吴小雨(wuxiaoyu)]</span>新增字典数据<span class=\'text-danger\'>[0.19-18m3/h]</span>', 1, 'admin/dictionary/show/2031898824568328192', 2001547605315665920, '吴小雨', 2, '2026-03-12 09:03:25', 2031898824568328192, 1);
INSERT INTO `jb_system_log` VALUES (2031901046198882304, '<span class=\'text-danger\'>[吴小雨(wuxiaoyu)]</span>新增字典数据<span class=\'text-danger\'>[4-300m3/h]</span>', 1, 'admin/dictionary/show/2031901046018527232', 2001547605315665920, '吴小雨', 2, '2026-03-12 09:12:15', 2031901046018527232, 1);
INSERT INTO `jb_system_log` VALUES (2031903097020600320, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[侯亮]</span>', 2, 'admin/user/show/2001548276093927424', 2001546811778514944, '韩子衿', 4, '2026-03-12 09:20:24', 2001548276093927424, 1);
INSERT INTO `jb_system_log` VALUES (2031903143988416512, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[石珍云]</span>', 2, 'admin/user/show/2001548170967891968', 2001546811778514944, '韩子衿', 4, '2026-03-12 09:20:35', 2001548170967891968, 1);
INSERT INTO `jb_system_log` VALUES (2031903165219983360, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[罗雄飞]</span>', 2, 'admin/user/show/2001548007356481536', 2001546811778514944, '韩子衿', 4, '2026-03-12 09:20:40', 2001548007356481536, 1);
INSERT INTO `jb_system_log` VALUES (2031903184723496960, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[梁鹏]</span>', 2, 'admin/user/show/2001547886069792768', 2001546811778514944, '韩子衿', 4, '2026-03-12 09:20:45', 2001547886069792768, 1);
INSERT INTO `jb_system_log` VALUES (2031903207334989824, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[吴小雨]</span>', 2, 'admin/user/show/2001547605315665920', 2001546811778514944, '韩子衿', 4, '2026-03-12 09:20:50', 2001547605315665920, 1);
INSERT INTO `jb_system_log` VALUES (2031903250418880512, '<span class=\'text-danger\'>[韩子衿(hanzijin)]</span>更新用户<span class=\'text-danger\'>[韩子衿]</span>', 2, 'admin/user/show/2001546811778514944', 2001546811778514944, '韩子衿', 4, '2026-03-12 09:21:00', 2001546811778514944, 1);
INSERT INTO `jb_system_log` VALUES (2031906180488351744, '<span class=\'text-danger\'>[siargo(admin)]</span>新增字典数据<span class=\'text-danger\'>[2.5-200m3/h]</span>', 1, 'admin/dictionary/show/2031906180438020096', 2009493497545871360, 'siargo', 2, '2026-03-12 09:32:39', 2031906180438020096, 1);
INSERT INTO `jb_system_log` VALUES (2031938046125789184, '<span class=\'text-danger\'>[siargo(admin)]</span>新增角色<span class=\'text-danger\'>[批准[3]]</span>', 1, 'admin/role/show/2031938046041903104', 2009493497545871360, 'siargo', 5, '2026-03-12 11:39:16', 2031938046041903104, 1);
INSERT INTO `jb_system_log` VALUES (2031938084285566976, '<span class=\'text-danger\'>[siargo(admin)]</span>更新角色<span class=\'text-danger\'>[批准]</span>的可用权限设置', 2, 'admin/role/show/2031938046041903104', 2009493497545871360, 'siargo', 5, '2026-03-12 11:39:26', 2031938046041903104, 1);
INSERT INTO `jb_system_log` VALUES (2031969369804623872, '<span class=\'text-danger\'>[siargo(admin)]</span>更新角色<span class=\'text-danger\'>[批准]</span>的可用权限设置', 2, 'admin/role/show/2031938046041903104', 2009493497545871360, 'siargo', 5, '2026-03-12 13:43:45', 2031938046041903104, 1);
INSERT INTO `jb_system_log` VALUES (2031969417288339456, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[吴小雨]</span>', 2, 'admin/user/show/2001547605315665920', 2009493497545871360, 'siargo', 4, '2026-03-12 13:43:56', 2001547605315665920, 1);
INSERT INTO `jb_system_log` VALUES (2031969440826773504, '<span class=\'text-danger\'>[siargo(admin)]</span>更新用户<span class=\'text-danger\'>[冯英]</span>', 2, 'admin/user/show/2001547415494049792', 2009493497545871360, 'siargo', 4, '2026-03-12 13:44:02', 2001547415494049792, 1);

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
INSERT INTO `jb_user` VALUES (2001546811778514944, 'hanzijin', 'adef7b7da197d6289ffa68e09b48d2c4222c8f2e4df956efffed841e71ff6c4b', '韩子衿', NULL, 'upload/user/avatar/20260121/a8f980ce6c9d45b98e2d6475ce62ea84.jpg', '2025-12-18 14:55:22', NULL, 'zhan@siargo.com.cn', '1', 1, NULL, 'hzj,hanzijin', '2001546465781989376', '0', 1992880779681570816, 'HLfgOmdKIMgr8Bgf9ujYqHaz5WrZY2Gw', '192.168.77.3', '2026-03-12 11:38:47', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-03-12 11:38:47', 2001546811778514944, '2026-03-12 09:21:00', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547415494049792, 'fengying', '05a9f06626bd38d6eb77f4785af0caf47dd5906b090b55b509ba4de88dcafc37', '冯英', NULL, 'assets/img/nv.png', '2025-12-18 14:57:45', NULL, 'yingfeng@siargo.com.cn', '1', 2, NULL, 'fy,fengying', '2001546465781989376,2002940469221724160,2031938046041903104', '0', 1992880779681570816, 'vjrc6bdLpItUduWxMX65qKFW0a9fDJSS', '192.168.77.194', '2026-02-26 12:30:35', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545737889890304', '2026-03-12 13:44:02', 2009493497545871360, '2026-03-12 13:44:02', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547605315665920, 'wuxiaoyu', '516e1cb55967ce54002ad3ba5f68709bbe41e7f92c20e7244de38e91a5710f7d', '吴小雨', NULL, 'assets/img/nv.png', '2025-12-18 14:58:31', NULL, 'xwu@siargo.com.cn', '1', 2, NULL, 'wxy,wuxiaoyu', '2001546465781989376,2002940469221724160,2031938046041903104', '0', 1992880779681570816, 'bL5FLrkc3nASgeR5M7tp1iy9xxjyQkZX', '192.168.77.81', '2026-03-09 17:21:18', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545773755383808', '2026-03-12 13:43:56', 2009493497545871360, '2026-03-12 13:43:56', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547886069792768, 'liangpeng', '77ee5afdcd4982257eaa2c84dd2712ad6ec0f1fd22990221b4adb1c5c592fd17', '梁鹏', NULL, 'assets/img/nan.png', '2025-12-18 14:59:38', NULL, 'pliang@siargo.com.cn', '1', 1, NULL, 'lp,liangpeng', '2001546465781989376', '0', 1992880779681570816, 'Cgm5O9cNmImm6cAs2ofNkQlNlys6MIXk', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-03-12 09:20:45', 2001546811778514944, '2026-03-12 09:20:45', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548007356481536, 'luoxiongfei', '0fa0eeac4e38f32dce622b91b78cbc462925030163f6af0c7714b2f04957c743', '罗雄飞', NULL, 'assets/img/nv.png', '2025-12-18 15:00:07', NULL, 'xluo@siargo.com.cn', '1', 2, NULL, 'lxf,luoxiongfei', '2001546465781989376', '0', 1992880779681570816, 'st4WHUfysHBHQGfWgoxCdFSppdM3ptx8', '192.168.3.30', '2026-03-11 13:58:52', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-03-12 09:20:40', 2001546811778514944, '2026-03-12 09:20:40', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548170967891968, 'shizhenyun', '696883428e592b11c7d3368e27ffe7cd2bf7613463e67663f3362c0c842b88db', '石珍云', NULL, 'assets/img/nv.png', '2025-12-18 15:00:46', NULL, 'zshi@siargo.com.cn', '1', 2, NULL, 'szy,shizhenyun', '2001546465781989376', '0', 1992880779681570816, '7RRLBDgyDQIafysHBRyTbkKMUraBaBcW', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-03-12 09:20:35', 2001546811778514944, '2026-03-12 09:20:35', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548276093927424, 'houliang', '4929e03616218c998ceae379dc15c92251a4c878b816027f1db147d13df55c6e', '侯亮', NULL, 'assets/img/nan.png', '2025-12-18 15:01:11', NULL, 'hou@siargo.com.cn', '1', 1, NULL, 'hl,houliang', '2001546465781989376', '0', 1992880779681570816, 'K0mhl2XI7cnGYRhH4G3UJr5svTgu3DTw', '192.168.3.65', '2026-03-12 09:19:51', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2026-03-12 09:20:24', 2001546811778514944, '2026-03-12 09:20:24', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2003992595003805696, 'wuyong', '69138627d70227a9f3f00e791558a31550823e2adc35c5f8462fb751fe8ab136', '吴勇', NULL, 'assets/img/nan.png', '2025-12-25 08:54:02', NULL, NULL, '1', 1, NULL, 'wy,wuyong', '2002940469221724160', '0', 2002984611549220864, 'MLec0kYhZVJhwbGE7CJqEmrkBcnXMvJv', '192.168.9.11', '2025-12-26 14:04:17', '内网IP', NULL, NULL, '0', 2007080161399705600, '2007080161399705600', NULL, '2026-01-02 21:23:09', 1992880779681570816, '2026-01-02 21:23:09', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2009493497545871360, 'admin', '0456810c087f40503d0a9b09933dab5b169182967a55dcc6edde6fc7d5cb1bb5', 'siargo', NULL, 'assets/img/nan.png', '2026-01-09 13:12:39', NULL, NULL, '1', 0, 0, '', '2002940469221724160', '1', 0, 'iPFW2GiLnayMLebIuCtiGAlfnkbw73T2', '192.168.77.3', '2026-03-12 11:38:59', '内网IP', NULL, NULL, '0', NULL, NULL, NULL, '2026-03-12 11:38:59', 2009493497545871360, '2026-01-13 09:18:28', 1, NULL);

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
INSERT INTO `siargo_customer` VALUES (2000847525710860288, '重庆捷定力');
INSERT INTO `siargo_customer` VALUES (2000847563539288064, '深圳堡森');
INSERT INTO `siargo_customer` VALUES (2000847627364012032, '常州斯尔特');
INSERT INTO `siargo_customer` VALUES (2000847720016187392, '常州爱德克斯');
INSERT INTO `siargo_customer` VALUES (2007693569572065280, '英国');
INSERT INTO `siargo_customer` VALUES (2007710955599679488, '海林奥德');
INSERT INTO `siargo_customer` VALUES (2007711025233514496, '济南时宜机械');
INSERT INTO `siargo_customer` VALUES (2007711100328333312, '日本');
INSERT INTO `siargo_customer` VALUES (2007711147526836224, '美国');
INSERT INTO `siargo_customer` VALUES (2007711171857993728, '韩国');
INSERT INTO `siargo_customer` VALUES (2007711232994168832, '泰国');
INSERT INTO `siargo_customer` VALUES (2007711626285666304, '哥伦比亚');
INSERT INTO `siargo_customer` VALUES (2007711682178961408, '张保利');
INSERT INTO `siargo_customer` VALUES (2007714429901066240, '中山清匠电器');
INSERT INTO `siargo_customer` VALUES (2007714485454622720, '钦州科华');
INSERT INTO `siargo_customer` VALUES (2007714532925755392, '常州新昌商贸');
INSERT INTO `siargo_customer` VALUES (2007714570045345792, '山东新华医用');
INSERT INTO `siargo_customer` VALUES (2007714613645135872, '珠海联创');
INSERT INTO `siargo_customer` VALUES (2007714677515997184, '天津和正仪');
INSERT INTO `siargo_customer` VALUES (2007714709090717696, '广东兴成');
INSERT INTO `siargo_customer` VALUES (2007714738543120384, '山东康诚');
INSERT INTO `siargo_customer` VALUES (2007714759992791040, '上海穗杉');
INSERT INTO `siargo_customer` VALUES (2007714795229138944, '深圳蓝迪');
INSERT INTO `siargo_customer` VALUES (2007714830587121664, '赫比科技');
INSERT INTO `siargo_customer` VALUES (2007714856986071040, '北京精研');
INSERT INTO `siargo_customer` VALUES (2007714924677943296, '北京联华创展');
INSERT INTO `siargo_customer` VALUES (2007714948971352064, '郑州通达');
INSERT INTO `siargo_customer` VALUES (2007714970609766400, '苏州海宇');
INSERT INTO `siargo_customer` VALUES (2007714998329921536, '久声智能');
INSERT INTO `siargo_customer` VALUES (2007715022455558144, '瑞典');
INSERT INTO `siargo_customer` VALUES (2007715058723704832, '上海沃尔特');
INSERT INTO `siargo_customer` VALUES (2007715086636797952, '湖南一特');
INSERT INTO `siargo_customer` VALUES (2007715109323788288, '上海依瑞');
INSERT INTO `siargo_customer` VALUES (2007715134355394560, '漳州众环科技');
INSERT INTO `siargo_customer` VALUES (2007715160188112896, '梅州市人民医院');
INSERT INTO `siargo_customer` VALUES (2007719346942365696, '陕西卫峰');
INSERT INTO `siargo_customer` VALUES (2007719394908426240, '成都联邦');
INSERT INTO `siargo_customer` VALUES (2007719497152974848, '河南科荣');
INSERT INTO `siargo_customer` VALUES (2007719816536641536, '长沙矽珺');
INSERT INTO `siargo_customer` VALUES (2007720000314265600, '成都麦特斯');
INSERT INTO `siargo_customer` VALUES (2008000445559984128, '南京威翔');
INSERT INTO `siargo_customer` VALUES (2008067760343339008, '王增平');
INSERT INTO `siargo_customer` VALUES (2008069857314983936, '广州铭鸿');
INSERT INTO `siargo_customer` VALUES (2008070087435472896, '北京金昌');
INSERT INTO `siargo_customer` VALUES (2008070408593330176, '上海瑞游');
INSERT INTO `siargo_customer` VALUES (2008070674134716416, '河南莱帕克');
INSERT INTO `siargo_customer` VALUES (2008079137560711168, '哈尔滨工业大学');
INSERT INTO `siargo_customer` VALUES (2008081212201881600, '广州迪川');
INSERT INTO `siargo_customer` VALUES (2008081588447727616, '河南云氧');
INSERT INTO `siargo_customer` VALUES (2008081806144688128, '深圳捷工');
INSERT INTO `siargo_customer` VALUES (2008081896909426688, '成都一牌c');
INSERT INTO `siargo_customer` VALUES (2008118232144007168, '青岛沃特勒');
INSERT INTO `siargo_customer` VALUES (2008353170634166272, '深圳迈瑞');
INSERT INTO `siargo_customer` VALUES (2008369853578989568, '湖南谊安优乐');
INSERT INTO `siargo_customer` VALUES (2008372258152501248, '日本');
INSERT INTO `siargo_customer` VALUES (2008414381195251712, '易佳杰');
INSERT INTO `siargo_customer` VALUES (2008415147318431744, '深圳工采');
INSERT INTO `siargo_customer` VALUES (2008417707106357248, '深圳实益达');
INSERT INTO `siargo_customer` VALUES (2008705235667505152, '苏州苏净');
INSERT INTO `siargo_customer` VALUES (2008712028045037568, '北京擎研');
INSERT INTO `siargo_customer` VALUES (2008718734284148736, '上海祖发');
INSERT INTO `siargo_customer` VALUES (2008719110416748544, '先进科技');
INSERT INTO `siargo_customer` VALUES (2008719311974027264, '杭州超钜');
INSERT INTO `siargo_customer` VALUES (2008719496024281088, '上海毅恬工业');
INSERT INTO `siargo_customer` VALUES (2008719753382580224, '弗卡恩(上海)生物科技');
INSERT INTO `siargo_customer` VALUES (2008721832280969216, '上海华械自动化');
INSERT INTO `siargo_customer` VALUES (2008722775034679296, '成都锐自达');
INSERT INTO `siargo_customer` VALUES (2008773890749091840, '天津亚科');
INSERT INTO `siargo_customer` VALUES (2008773995162095616, '上海星辉');
INSERT INTO `siargo_customer` VALUES (2008792751502381056, '法国');
INSERT INTO `siargo_customer` VALUES (2008802273482625024, '北京华仪泰兴');
INSERT INTO `siargo_customer` VALUES (2008802664131710976, '北京中智核安');
INSERT INTO `siargo_customer` VALUES (2008802763016622080, '日照华兴');
INSERT INTO `siargo_customer` VALUES (2008803112767049728, '武汉恒业通');
INSERT INTO `siargo_customer` VALUES (2008803436777033728, 'QMT Teach AB(瑞典)');
INSERT INTO `siargo_customer` VALUES (2009090038669627392, '湖南蓝天智能');
INSERT INTO `siargo_customer` VALUES (2009091257458872320, '无锡众志晟自动化');
INSERT INTO `siargo_customer` VALUES (2009149718863400960, '瑞士');
INSERT INTO `siargo_customer` VALUES (2009193617589915648, '巴西');
INSERT INTO `siargo_customer` VALUES (2009223836984004608, '中钧科技');
INSERT INTO `siargo_customer` VALUES (2009436221791391744, 'Queensland Health(澳大利亚)');
INSERT INTO `siargo_customer` VALUES (2009468509057044480, '洛斯贝流体(温州)');
INSERT INTO `siargo_customer` VALUES (2009505047618899968, '煜昌荣茂');
INSERT INTO `siargo_customer` VALUES (2009511454766387200, '山东新高工业');
INSERT INTO `siargo_customer` VALUES (2009515167178412032, '易镜医疗');
INSERT INTO `siargo_customer` VALUES (2009520427271835648, '深圳贝塔利');
INSERT INTO `siargo_customer` VALUES (2009549818810978304, '山东智高流体');
INSERT INTO `siargo_customer` VALUES (2009553606242324480, '北京华远');
INSERT INTO `siargo_customer` VALUES (2009802341484449792, '迈瑞');
INSERT INTO `siargo_customer` VALUES (2009812759917481984, '德国');
INSERT INTO `siargo_customer` VALUES (2009898353339256832, '河北谊安');
INSERT INTO `siargo_customer` VALUES (2010583151636500480, '青岛恒远');
INSERT INTO `siargo_customer` VALUES (2010617080661790720, '兰州科海');
INSERT INTO `siargo_customer` VALUES (2010625095762825216, '广东嘉润医工建筑');
INSERT INTO `siargo_customer` VALUES (2010625260645109760, '汉中易方物资');
INSERT INTO `siargo_customer` VALUES (2010625485929566208, '长沙扬诺医疗c');
INSERT INTO `siargo_customer` VALUES (2010625640707772416, '北京卓镭激光');
INSERT INTO `siargo_customer` VALUES (2010889086942695424, '深圳捷力');
INSERT INTO `siargo_customer` VALUES (2010889406141812736, '成都鸿瑞');
INSERT INTO `siargo_customer` VALUES (2010897286836375552, '南京霍普斯');
INSERT INTO `siargo_customer` VALUES (2010905350503256064, '信安诺医药');
INSERT INTO `siargo_customer` VALUES (2010905611992944640, '沃迈(上海)');
INSERT INTO `siargo_customer` VALUES (2010907066472714240, '西安云仪');
INSERT INTO `siargo_customer` VALUES (2010907919644479488, '深圳准控');
INSERT INTO `siargo_customer` VALUES (2010953250197327872, '北京唯若');
INSERT INTO `siargo_customer` VALUES (2010955747762753536, 'TCB Energy Services,LLC(美国)');
INSERT INTO `siargo_customer` VALUES (2010955877500964864, '湖南远利恒泰医疗');
INSERT INTO `siargo_customer` VALUES (2010989045750812672, '土耳其');
INSERT INTO `siargo_customer` VALUES (2010989422000852992, '安克创新');
INSERT INTO `siargo_customer` VALUES (2010989961023442944, '杭州微标');
INSERT INTO `siargo_customer` VALUES (2010990688588386304, '武汉天虹');
INSERT INTO `siargo_customer` VALUES (2010991169763135488, '莫帝斯燃烧');
INSERT INTO `siargo_customer` VALUES (2011247058478813184, '重庆休顿');
INSERT INTO `siargo_customer` VALUES (2011260144535326720, '西班牙');
INSERT INTO `siargo_customer` VALUES (2011308729838718976, '广州熙福医疗器械');
INSERT INTO `siargo_customer` VALUES (2011321380811689984, '未来之芯');
INSERT INTO `siargo_customer` VALUES (2011344161783795712, '江苏泰亨');
INSERT INTO `siargo_customer` VALUES (2011385065982775296, '意大利');
INSERT INTO `siargo_customer` VALUES (2011620456857980928, '天津森罗');
INSERT INTO `siargo_customer` VALUES (2011620672684281856, 'OHK Medical 以色列');
INSERT INTO `siargo_customer` VALUES (2011621384457670656, '佛山特种医用导管');
INSERT INTO `siargo_customer` VALUES (2011622586750717952, '鸿盛芯创');
INSERT INTO `siargo_customer` VALUES (2011625849470439424, '沈阳科汇仪器');
INSERT INTO `siargo_customer` VALUES (2011969728602296320, '广东凌腾科技');
INSERT INTO `siargo_customer` VALUES (2011985594622529536, '石家庄星测');
INSERT INTO `siargo_customer` VALUES (2011986157196136448, '珠海安联');
INSERT INTO `siargo_customer` VALUES (2012078681608802304, '湖南微分医疗');
INSERT INTO `siargo_customer` VALUES (2012078806397734912, '上海隆达锐医疗');
INSERT INTO `siargo_customer` VALUES (2013071008288591872, '安丰固');
INSERT INTO `siargo_customer` VALUES (2013071680379670528, '上海进申');
INSERT INTO `siargo_customer` VALUES (2013072266374270976, '苏州宝帆');
INSERT INTO `siargo_customer` VALUES (2013084556410605568, '湖南泰瑞医疗');
INSERT INTO `siargo_customer` VALUES (2013124143279362048, '北京中惠');
INSERT INTO `siargo_customer` VALUES (2013138726744346624, '福州德为流体');
INSERT INTO `siargo_customer` VALUES (2013427593464631296, '重庆川瀚医疗');
INSERT INTO `siargo_customer` VALUES (2013429632307417088, '东莞达辉精密塑胶');
INSERT INTO `siargo_customer` VALUES (2013438667874226176, '深圳德林科');
INSERT INTO `siargo_customer` VALUES (2013438920664928256, '广东永力');
INSERT INTO `siargo_customer` VALUES (2013536659369218048, '湖北南控仪表');
INSERT INTO `siargo_customer` VALUES (2013862626419658752, 'SMC');
INSERT INTO `siargo_customer` VALUES (2013862983799525376, '郑州华致');
INSERT INTO `siargo_customer` VALUES (2013863309252349952, '浙江恒达');
INSERT INTO `siargo_customer` VALUES (2013902307987410944, '成都维克安');
INSERT INTO `siargo_customer` VALUES (2014162082650247168, '成都雷宇测控');
INSERT INTO `siargo_customer` VALUES (2014248063466328064, '广州沃誉');
INSERT INTO `siargo_customer` VALUES (2014505851761315840, '深圳术为');
INSERT INTO `siargo_customer` VALUES (2014530991920369664, '丹麦');
INSERT INTO `siargo_customer` VALUES (2014532462644678656, '江苏春潮');
INSERT INTO `siargo_customer` VALUES (2014576433014099968, 'JETEC (台湾)');
INSERT INTO `siargo_customer` VALUES (2014576618557526016, '河北弘创气体');
INSERT INTO `siargo_customer` VALUES (2014576843300917248, '李小华');
INSERT INTO `siargo_customer` VALUES (2014577031339954176, '中科院力学研究所');
INSERT INTO `siargo_customer` VALUES (2014577280267702272, '广州匹思迅压力控制');
INSERT INTO `siargo_customer` VALUES (2014577471767040000, '四川港通医疗设备');
INSERT INTO `siargo_customer` VALUES (2014577795667972096, '广州埃珥赛璞医疗设备');
INSERT INTO `siargo_customer` VALUES (2014577964585177088, '湖南皓拓医疗');
INSERT INTO `siargo_customer` VALUES (2014585001301889024, '阿派斯');
INSERT INTO `siargo_customer` VALUES (2014585695568252928, '中山优胜');
INSERT INTO `siargo_customer` VALUES (2014592446904520704, '珠海清润');
INSERT INTO `siargo_customer` VALUES (2015609692292501504, '成都联康智诚');
INSERT INTO `siargo_customer` VALUES (2015625105067855872, '珠海启航');
INSERT INTO `siargo_customer` VALUES (2015625627841712128, '杭州神州洁净空气检测');
INSERT INTO `siargo_customer` VALUES (2015625767688196096, '四川震康电器');
INSERT INTO `siargo_customer` VALUES (2015626152817577984, 'EQUISONES(哥伦比亚)');
INSERT INTO `siargo_customer` VALUES (2015657123667365888, '米拓电气');
INSERT INTO `siargo_customer` VALUES (2015658945274564608, '市场部');
INSERT INTO `siargo_customer` VALUES (2015671917715771392, '北京久维');
INSERT INTO `siargo_customer` VALUES (2015969515408314368, '航天新长征医疗');
INSERT INTO `siargo_customer` VALUES (2015969723378683904, '湖南德信汽车');
INSERT INTO `siargo_customer` VALUES (2015969919147823104, '成都熊谷加世电器');
INSERT INTO `siargo_customer` VALUES (2015970085070295040, 'Hsiung Mei(台湾)');
INSERT INTO `siargo_customer` VALUES (2015970239416487936, 'B K (印度)');
INSERT INTO `siargo_customer` VALUES (2015982355649253376, '东莞市流量计仪表');
INSERT INTO `siargo_customer` VALUES (2015988326291329024, '北京本立');
INSERT INTO `siargo_customer` VALUES (2015989275340689408, 'Telesplicing(台湾)');
INSERT INTO `siargo_customer` VALUES (2016032736676794368, '苏州艾谨洋');
INSERT INTO `siargo_customer` VALUES (2016123322280824832, '广西珂深威医疗');
INSERT INTO `siargo_customer` VALUES (2016329694373072896, '余姚宇峰');
INSERT INTO `siargo_customer` VALUES (2016352244226445312, '天博智能');
INSERT INTO `siargo_customer` VALUES (2016400452004728832, '新西兰');
INSERT INTO `siargo_customer` VALUES (2016681610454093824, '陕西里诺电子');
INSERT INTO `siargo_customer` VALUES (2016751049635647488, '深圳新世联');
INSERT INTO `siargo_customer` VALUES (2016751257031397376, '优泰环保');
INSERT INTO `siargo_customer` VALUES (2016752149134692352, 'Bell Flow(英国)');
INSERT INTO `siargo_customer` VALUES (2016775377605480448, '蓝工');
INSERT INTO `siargo_customer` VALUES (2017056238917767168, 'iQ Valves Co(美国)');
INSERT INTO `siargo_customer` VALUES (2017056582234132480, '成都智宇博科技');
INSERT INTO `siargo_customer` VALUES (2017767579475365888, 'PalasGmbH');
INSERT INTO `siargo_customer` VALUES (2017780321645088768, 'Bunnell lnc');
INSERT INTO `siargo_customer` VALUES (2018156212963364864, '成都坤洋实业');
INSERT INTO `siargo_customer` VALUES (2018156684621238272, '大连力德致远科技');
INSERT INTO `siargo_customer` VALUES (2018160146889166848, '上海茸亚电子');
INSERT INTO `siargo_customer` VALUES (2018201456949972992, '河南宝瑞');
INSERT INTO `siargo_customer` VALUES (2018523659239280640, '青岛光电电子');
INSERT INTO `siargo_customer` VALUES (2018524386963607552, '湘潭新大粉末冶金');
INSERT INTO `siargo_customer` VALUES (2018525252835725312, '广州立勤');
INSERT INTO `siargo_customer` VALUES (2018553846635024384, '广州熙亮');
INSERT INTO `siargo_customer` VALUES (2018859066908856320, '空军特色医学中心');
INSERT INTO `siargo_customer` VALUES (2018859312435023872, '广东华科');
INSERT INTO `siargo_customer` VALUES (2018934242254114816, '上海精稻机电');
INSERT INTO `siargo_customer` VALUES (2018938756776448000, '深圳诺然美泰');
INSERT INTO `siargo_customer` VALUES (2018968800659886080, '南非');
INSERT INTO `siargo_customer` VALUES (2019227413773144064, '广州永烁医疗');
INSERT INTO `siargo_customer` VALUES (2019228297257144320, '陕西韧驰电子');
INSERT INTO `siargo_customer` VALUES (2019228867871232000, '武汉博松机电');
INSERT INTO `siargo_customer` VALUES (2019229459159044096, '深圳百顺益商贸');
INSERT INTO `siargo_customer` VALUES (2019326389318963200, '嘉兴凯博');
INSERT INTO `siargo_customer` VALUES (2019326817842614272, '广州纹徕');
INSERT INTO `siargo_customer` VALUES (2019327318906753024, '成都研创');
INSERT INTO `siargo_customer` VALUES (2019578579988107264, '哈尔滨道里区荣派电子产品');
INSERT INTO `siargo_customer` VALUES (2019587962801344512, '印度');
INSERT INTO `siargo_customer` VALUES (2019683524951265280, '石家庄开发区');
INSERT INTO `siargo_customer` VALUES (2019689399506030592, '中测易安(西安)检测');
INSERT INTO `siargo_customer` VALUES (2019701153103466496, '河北爱创');
INSERT INTO `siargo_customer` VALUES (2020703920030863360, '广州市杨杨电子');
INSERT INTO `siargo_customer` VALUES (2020764236521525248, '四川辐源核技术');
INSERT INTO `siargo_customer` VALUES (2020765094432854016, '国药工程');
INSERT INTO `siargo_customer` VALUES (2020767842838630400, '上海洛丁');
INSERT INTO `siargo_customer` VALUES (2020771962542739456, '湖南复泰');
INSERT INTO `siargo_customer` VALUES (2020774273419694080, '苏州孜求');
INSERT INTO `siargo_customer` VALUES (2021106609860497408, '漢唐科技');
INSERT INTO `siargo_customer` VALUES (2021116031584292864, 'INDUSTRIAL(英国）');
INSERT INTO `siargo_customer` VALUES (2021400966392041472, 'Devlabs');
INSERT INTO `siargo_customer` VALUES (2021425362821304320, '北京易世恒');
INSERT INTO `siargo_customer` VALUES (2021425614123028480, '天津利达通');
INSERT INTO `siargo_customer` VALUES (2021744586248802304, '费斯托');
INSERT INTO `siargo_customer` VALUES (2021773591534489600, '上海正久');
INSERT INTO `siargo_customer` VALUES (2021778350760906752, '吉安比科技');
INSERT INTO `siargo_customer` VALUES (2021817994084995072, '苏州众好');
INSERT INTO `siargo_customer` VALUES (2021847436844978176, '沈阳金杰科');
INSERT INTO `siargo_customer` VALUES (2021867811423244288, '成都四翔');
INSERT INTO `siargo_customer` VALUES (2026215185620783104, 'HYKO (印度)');
INSERT INTO `siargo_customer` VALUES (2026215499895787520, '郑州艾迪');
INSERT INTO `siargo_customer` VALUES (2026215751734382592, '成都中子新科技');
INSERT INTO `siargo_customer` VALUES (2027194992491876352, 'AMETEK');
INSERT INTO `siargo_customer` VALUES (2027204311874654208, '沈阳海龟医疗');
INSERT INTO `siargo_customer` VALUES (2027220142327189504, 'IDENTIC(德国)');
INSERT INTO `siargo_customer` VALUES (2027220435169300480, 'Airnetic, LLc');
INSERT INTO `siargo_customer` VALUES (2027562849968967680, 'GLOBAL GASEQUIPOS LTDA');
INSERT INTO `siargo_customer` VALUES (2027573998433193984, 'Moseb（马来西亚）');
INSERT INTO `siargo_customer` VALUES (2027670022979964928, '天津欧陆');
INSERT INTO `siargo_customer` VALUES (2028277961952448512, '珠海普瑞顺');
INSERT INTO `siargo_customer` VALUES (2028348306646487040, 'Elementar');
INSERT INTO `siargo_customer` VALUES (2028373792743346176, '深圳杉本贸易');
INSERT INTO `siargo_customer` VALUES (2028374060956504064, 'HEGDE (印度)');
INSERT INTO `siargo_customer` VALUES (2028374522959089664, 'mlu-recordum');
INSERT INTO `siargo_customer` VALUES (2028668810041348096, 'ATRICURE');
INSERT INTO `siargo_customer` VALUES (2028671912924532736, '西安为普测控');
INSERT INTO `siargo_customer` VALUES (2028672389825286144, '北京诺金恒昌科技');
INSERT INTO `siargo_customer` VALUES (2028672835826601984, '秦皇岛新华通');
INSERT INTO `siargo_customer` VALUES (2028673399624945664, '陕西东辉智能');
INSERT INTO `siargo_customer` VALUES (2028704009290633216, 'Cincinnati');
INSERT INTO `siargo_customer` VALUES (2028738862509838336, '万晟智造');
INSERT INTO `siargo_customer` VALUES (2029064336154939392, '斯诺文尼亚');
INSERT INTO `siargo_customer` VALUES (2029072032761499648, '大连创景');
INSERT INTO `siargo_customer` VALUES (2029072287703879680, '大连惠畅');
INSERT INTO `siargo_customer` VALUES (2029472294344970240, '思必达(无锡)');
INSERT INTO `siargo_customer` VALUES (2029477137528770560, '四川予桥');
INSERT INTO `siargo_customer` VALUES (2029478497955467264, '湖南博恩');
INSERT INTO `siargo_customer` VALUES (2029754454410907648, '苏州美腾');
INSERT INTO `siargo_customer` VALUES (2029755191408840704, 'HASSO(秘鲁)');
INSERT INTO `siargo_customer` VALUES (2030832396146692096, '四川久远');
INSERT INTO `siargo_customer` VALUES (2030895596846305280, '西安立能环保');
INSERT INTO `siargo_customer` VALUES (2030903565537366016, '重庆华渝电气');
INSERT INTO `siargo_customer` VALUES (2030928133987618816, 'Wooil(韩国)');
INSERT INTO `siargo_customer` VALUES (2030928528445132800, '上海谦和泰');
INSERT INTO `siargo_customer` VALUES (2031204551891996672, '成都俊骋机电');
INSERT INTO `siargo_customer` VALUES (2031205218773749760, '陕西镁泰医疗');
INSERT INTO `siargo_customer` VALUES (2031235790757810176, '汇鼎智联(江苏)');
INSERT INTO `siargo_customer` VALUES (2031238998821556224, '矽翔深圳办');
INSERT INTO `siargo_customer` VALUES (2031239490381402112, '深圳新益昌');
INSERT INTO `siargo_customer` VALUES (2031543168124440576, '北京镭默科技');
INSERT INTO `siargo_customer` VALUES (2031612311007776768, '深圳仪翔');
INSERT INTO `siargo_customer` VALUES (2031642703622426624, '山西卫戈');
INSERT INTO `siargo_customer` VALUES (2031643224043278336, '上海杰森');
INSERT INTO `siargo_customer` VALUES (2031903675784220672, '河北科析');
INSERT INTO `siargo_customer` VALUES (2031910755786149888, '清河普泰');

-- ----------------------------
-- Table structure for siargo_image
-- ----------------------------
DROP TABLE IF EXISTS `siargo_image`;
CREATE TABLE `siargo_image`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `supplier_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商ID',
  `storage_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存储文件名（到货单号）',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件存储路径',
  `md5_hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件MD5值（去重）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片描述',
  `upload_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploader_id` bigint NULL DEFAULT NULL COMMENT '上传者ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-删除',
  `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uk_md5_hash`(`md5_hash` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_uploader`(`uploader_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '到货单图片存储表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_image
-- ----------------------------

-- ----------------------------
-- Table structure for siargo_product
-- ----------------------------
DROP TABLE IF EXISTS `siargo_product`;
CREATE TABLE `siargo_product`  (
  `id` bigint NOT NULL COMMENT '产品ID',
  `report_id` bigint NOT NULL COMMENT '检验报告单ID',
  `type` int NOT NULL COMMENT '产品类型:\r\n1传感器, \r\n2小流量, \r\n3大流量',
  `modle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品型号',
  `qsi` int NOT NULL COMMENT '送检数量 (Quantity Submitted for Inspection)',
  `qi` int NOT NULL COMMENT '检验数量 (Quantity Inspected)',
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
  `accq_uid` bigint NULL DEFAULT NULL COMMENT '精度合格检验人',
  `funq_uid` bigint NULL DEFAULT NULL COMMENT '功能合格检验人',
  `appq_uid` bigint NULL DEFAULT NULL COMMENT '外观合格检验人',
  `allq_uid` bigint NULL DEFAULT NULL COMMENT '最终审核人',
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
  INDEX `idx_repid`(`report_id` ASC) USING BTREE,
  INDEX `idx_vd`(`vd` ASC) USING BTREE,
  INDEX `idx_modle`(`modle` ASC) USING BTREE,
  INDEX `idx_number`(`number` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单里的产品型号，跟报告单多对一关系' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_product
-- ----------------------------
INSERT INTO `siargo_product` VALUES (2007720620630855680, 2007720620622467072, 2, 'MF4710-G4-100-BV-O', 1, 1, 'A1V04387', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:16:13', '2026-01-04 17:25:24', '2026-01-05 09:43:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007720844665409536, 2007720844652826624, 2, 'MF4708-R3-20-AB-B', 1, 1, 'A1V04401', NULL, 'Co2实标，CF980', NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:16:13', '2026-01-04 17:25:24', '2026-01-05 13:06:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721015558131712, 2007721015553937408, 2, 'MF5708-G-100-B-A', 2, 2, 'PEEUL19995-PEEUL19996', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:16:13', '2026-01-04 17:25:24', '2026-01-05 09:49:20', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721171728846848, 2007721171716263936, 2, 'MF5619-N-800-ABD-D-O', 1, 1, 'G6HUL22656', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:17:00', '2026-01-04 17:25:24', '2026-01-05 09:53:09', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721325462671360, 2007721325454282752, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GUH39473', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:17:00', '2026-01-04 17:25:24', '2026-01-05 09:50:48', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007721438180397056, 2007721438176202752, 2, 'MF5612-N-200-B-D-A', 1, 1, 'G6GUL22657', NULL, NULL, NULL, 1, 5, '2026-01-04 15:51:29', '2026-01-04 17:17:00', '2026-01-04 17:25:24', '2026-01-05 09:53:09', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2007737171346051072, 2007737171337662464, 3, 'MF2032Be-AB-O', 1, 1, 'GLEUL10603', '3', NULL, NULL, 1, 5, '2026-01-04 16:53:38', '2026-01-04 16:53:52', '2026-01-04 17:15:02', '2026-01-05 13:06:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008001595801718785, 2008001595801718784, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V02603-A1V02752', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:38:23', '2026-01-07 14:44:29', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008001595852050432, 2008001595801718784, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V02753-A1V02902', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:38:23', '2026-01-07 14:44:29', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008002955599269888, 2008002955578298368, 1, 'FS4308-20-R-EV-A', 1, 1, 'A1V04779', NULL, '', NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:08:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008002955611852800, 2008002955578298368, 1, 'FS4308-20-R-EV-B', 1, 1, 'A1V04780', NULL, 'CO2测，系数980', NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:08:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008003805612724224, 2008003805579169792, 1, 'FS6122-250F250-5P100-TH1', 500, 80, 'A1V03887-A1V04386', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 17:16:07', '2026-01-07 14:44:13', '2026-01-07 14:54:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008004576886509569, 2008004576886509568, 1, 'FS4001E-30-CV-A', 4, 4, 'A1V04552-A1V04555', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:40:02', '2026-01-05 15:40:11', '2026-01-05 17:06:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008018603964485633, 2008018603964485632, 1, 'FS5001E-500-EV-H', 1, 1, 'A1V04508', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:05:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2008018603964485634, 2008018603964485632, 1, 'FS5001E-200-EV-H', 1, 1, 'A1V04509', NULL, NULL, NULL, 1, 5, '2026-01-05 15:36:25', '2026-01-05 15:37:23', '2026-01-05 15:38:50', '2026-01-05 17:05:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
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
INSERT INTO `siargo_product` VALUES (2008087427757756417, 2008087427757756416, 2, 'MF5012-N4F-e-3-300-15-AB-D-A', 1, 1, 'G5GUL11748', NULL, NULL, NULL, 1, 5, '2026-01-05 16:05:25', '2026-01-06 09:47:08', '2026-01-22 15:09:46', '2026-01-22 17:56:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
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
INSERT INTO `siargo_product` VALUES (2008805427762810880, 2008805427750227968, 2, 'MF5612-G4M-200-ABD-N-A', 1, 1, 'G6GVA22669', NULL, NULL, NULL, 1, 5, '2026-01-07 15:38:30', '2026-01-08 09:10:13', '2026-02-04 15:06:41', '2026-02-04 17:01:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
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
INSERT INTO `siargo_product` VALUES (2009802603422928896, 2009802603418734592, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V08946-A1V08995', NULL, NULL, NULL, 1, 5, '2026-01-10 10:04:07', '2026-01-14 16:46:24', '2026-02-26 15:32:53', '2026-02-26 15:50:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009802931815960576, 2009802931811766272, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V08818-A1V08867', NULL, NULL, NULL, 1, 5, '2026-01-10 10:04:07', '2026-01-14 16:46:24', '2026-02-11 16:33:44', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009803327162667008, 2009803327158472704, 1, 'FS34008-20-O8-CV-A-500mesh', 78, 13, 'A1V08868-8945', NULL, NULL, NULL, 1, 5, '2026-01-10 10:04:07', '2026-02-12 15:55:42', '2026-02-26 15:32:53', '2026-02-26 15:50:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009808179628462081, 2009808179628462080, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 13, 'A1V08768-A1V08817', NULL, 'CO2测系数980', NULL, 1, 5, '2026-01-10 10:04:07', '2026-01-14 16:46:24', '2026-02-11 16:33:44', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009812912334295040, 2009812912330100736, 1, 'FS4308-3-O8-CV-A-0.5/4.5V', 100, 13, 'A1V05813-5912', NULL, NULL, NULL, 1, 5, '2026-01-10 10:22:04', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536837627904, 2009899536833433600, 1, 'FS4308-33-O8-BV-A', 2, 2, 'A1V05933-5934', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536841822208, 2009899536833433600, 1, 'FS1100-10F240', 2, 2, 'A1V05927-A1V05928', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536846016512, 2009899536833433600, 1, 'FS1100-24F240', 2, 2, 'A1V05929-A1V05930', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:22', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536850210816, 2009899536833433600, 1, 'FS1100-100F250', 2, 2, 'A1V05931-A1V05932', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2009899536850210817, 2009899536833433600, 1, 'FSP1000-500-EV-U', 10, 10, 'A1V05917-5926', NULL, NULL, NULL, 1, 5, '2026-01-10 16:06:29', '2026-01-12 14:42:59', '2026-01-12 14:53:26', '2026-01-12 16:31:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010584432375615488, 2010584432367226880, 1, 'MF3000M-1500-G4F-BAP-A', 10, 10, 'A1V12232-A1V12241', NULL, NULL, NULL, 1, 5, '2026-01-12 13:29:49', '2026-01-12 14:42:59', '2026-01-12 17:25:12', '2026-01-13 09:29:32', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010584432379809792, 2010584432367226880, 1, 'FS6122-0F250-0P0-TH0', 1100, 80, 'A1V05946-7045', NULL, NULL, NULL, 1, 5, '2026-01-12 13:29:49', '2026-01-12 14:42:59', '2026-01-12 14:54:30', '2026-01-12 16:31:05', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
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
INSERT INTO `siargo_product` VALUES (2010907187272863744, 2010907187268669440, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSFVA30003', '18', NULL, NULL, 1, 5, '2026-01-13 13:19:48', '2026-01-13 13:56:03', '2026-02-04 14:48:10', '2026-02-04 17:01:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 17.60, 13.20, 4.787, 1564, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2010907392416272384, 2010907392412078080, 2, 'MF4712-Rc4-200-BV-A', 5, 5, 'A1V15070-A1V15074', NULL, NULL, NULL, 1, 5, '2026-01-13 10:50:57', '2026-01-13 14:19:31', '2026-01-14 16:39:55', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010907642849775616, 2010907642845581312, 2, '01-0-8-1-1-DB9M-KMB1728', 1, 1, 'B020871', NULL, 'BC-C1000-N2-485-NPT', NULL, 1, 5, '2026-01-13 10:51:57', '2026-01-13 14:19:31', '2026-01-14 16:39:55', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010908029073870848, 2010908029069676544, 2, 'MFC2000-100-P8-BA-0-A', 1, 1, 'MC2UI10836', NULL, NULL, NULL, 1, 5, '2026-01-13 10:53:29', '2026-01-13 14:19:31', '2026-01-14 16:39:55', '2026-01-14 16:50:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2010908108262330368, 2010908108258136064, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSFVA30003', '19', NULL, '2026-01-13 11:26:35', 0, 1, '2026-01-13 10:53:48', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, 17.60, 13.20, 4.787, 1564, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2010909056711905280, 2010909056703516672, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSDVA3004', '18', NULL, NULL, 1, 5, '2026-01-13 10:57:34', '2026-01-13 13:56:03', '2026-02-04 14:48:10', '2026-02-04 17:01:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.80, 20.80, 4.776, 1631, 6, NULL, NULL, 6);
INSERT INTO `siargo_product` VALUES (2010945605885349888, 2010945605881155584, 3, 'MF40FD-E-2.5-250-15-ABO-O', 1, 1, 'GSFVA30001', '18', NULL, '2026-01-13 14:19:50', 0, 1, '2026-01-13 13:22:48', NULL, NULL, NULL, 2001547605315665920, NULL, NULL, NULL, NULL, 2, NULL, 18.60, 14.60, 4.788, 1669, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2010946153774698497, 2010946153774698496, 3, 'MF40FD-E-2.5-250-15-ABO-O', 1, 1, 'GSFVA30001', '17', NULL, NULL, 1, 5, '2026-01-13 14:19:56', '2026-01-13 14:20:15', '2026-02-04 14:48:10', '2026-02-04 17:01:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.90, 17.40, 4.810, 1659, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2010946778486919168, 2010946778478530560, 3, 'MF50FD-E-4.0-400-15-A-N', 1, 1, 'GSAVA30021', '14', NULL, NULL, 1, 5, '2026-01-13 13:27:28', '2026-01-13 13:28:23', '2026-01-21 14:38:35', '2026-01-21 14:54:43', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.10, 17.70, 4.822, 1690, 0, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2010947704782180352, 2010947704773791744, 3, 'MF40FD-E-2.5-250-15-ABO-O', 1, 1, 'GSFVA30002', '17', NULL, NULL, 1, 5, '2026-01-13 13:31:09', '2026-01-13 13:56:03', '2026-02-04 14:48:10', '2026-02-04 17:01:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.90, 17.40, 4.810, 1659, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2010953724828962816, 2010953724824768512, 3, 'MF25FD-E-1-100-15-ABD-O', 1, 1, 'GSBVA30041', '2', NULL, NULL, 1, 5, '2026-01-13 13:55:31', '2026-01-13 13:55:49', '2026-01-15 17:18:50', '2026-01-15 17:25:10', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 17.60, 13.50, 4.789, 1563, 1, NULL, NULL, 3);
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
INSERT INTO `siargo_product` VALUES (2011345896396935168, 2011345896300466176, 3, 'MF32FD-E-1.6-160-15-ABD-O', 1, 1, 'GSETI30129', '16', NULL, NULL, 1, 5, '2026-01-15 17:29:46', '2026-01-15 17:30:05', '2026-01-15 17:33:20', '2026-01-19 09:59:33', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 15.10, 20.50, 4.802, 1645, 5, NULL, NULL, 4);
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
INSERT INTO `siargo_product` VALUES (2013163981848301569, 2013163981848301568, 3, 'MF25FD-E-1-100-15-ABD-O', 1, 1, 'GSBVA30042', '2', NULL, NULL, 1, 5, '2026-01-19 16:18:00', '2026-01-19 16:18:11', '2026-01-21 14:38:35', '2026-01-21 14:54:43', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.80, 17.60, 4.913, 1652, 1, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2013164528454193152, 2013164528437415936, 3, 'MF2025Be-AB-O', 1, 1, 'GLBVA10624', '19', NULL, NULL, 1, 5, '2026-01-19 16:22:08', '2026-01-19 16:22:17', '2026-01-21 15:36:32', '2026-01-21 17:25:14', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013164940292902912, 2013164940284514304, 3, 'MF2032Be-AB-O', 18, 18, 'GLEVA10606-10623', '3', NULL, NULL, 1, 5, '2026-01-19 16:22:08', '2026-01-19 16:22:17', '2026-01-21 15:36:32', '2026-01-21 17:25:14', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013428644435578880, 2013428644427190272, 2, 'MF5612-N-200-A-D-O', 1, 1, 'G6GVA22766', NULL, NULL, NULL, 1, 5, '2026-01-20 09:49:31', '2026-01-21 13:15:01', '2026-01-21 14:38:00', '2026-01-21 14:56:18', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013428644448161792, 2013428644427190272, 2, 'MF5619-N-600-A-D-O', 1, 1, 'G6HVA22765', NULL, NULL, NULL, 1, 5, '2026-01-20 09:49:31', '2026-01-20 16:23:00', '2026-01-21 14:38:00', '2026-01-21 14:56:18', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013428779865460736, 2013428779857072128, 2, 'MF5612-N-200-A-D-O', 1, 1, 'G6GVA22764', NULL, NULL, NULL, 1, 5, '2026-01-20 09:50:03', '2026-01-20 16:23:00', '2026-01-21 14:41:16', '2026-01-21 14:54:43', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429062913871873, 2013429062913871872, 2, 'MF6600-2SLPM', 1, 1, 'FAGVA17579', NULL, NULL, NULL, 1, 5, '2026-01-20 09:53:08', '2026-01-20 16:04:58', '2026-01-21 14:40:43', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429290949791744, 2013429290941403136, 2, 'MF6600-70SLPM', 1, 1, 'FAGTH10962', NULL, NULL, NULL, 1, 5, '2026-01-20 09:53:08', '2026-01-20 16:04:58', '2026-01-21 14:40:43', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429528926212096, 2013429528922017792, 2, 'MF6600-70SLPM', 10, 10, 'FAGVA17580-FAGVA17589', NULL, NULL, NULL, 1, 5, '2026-01-20 09:53:08', '2026-01-20 16:04:58', '2026-01-21 14:40:43', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013429781448478721, 2013429781448478720, 2, 'MF4601-R2-200-BV-A', 1, 1, 'A1V16173', NULL, NULL, NULL, 1, 5, '2026-01-20 09:54:02', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425930219520, 2013431425926025216, 2, 'MF4701-R1-100-BV-A', 1, 1, 'A1V15336', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425934413824, 2013431425926025216, 2, 'MF4701-R1-200-BV-A', 1, 1, 'A1V15337', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425942802432, 2013431425926025216, 2, 'MF4701-R1-500-BV-A', 1, 1, 'A1V15338', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013431425946996736, 2013431425926025216, 2, 'MF4703-R1-1000-BV-A', 1, 1, 'A1V15339', NULL, '精度1.5+0.5FS，英文证书，中文标签', NULL, 1, 5, '2026-01-20 10:00:34', '2026-01-20 14:43:28', '2026-01-20 14:55:01', '2026-01-20 16:39:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013438793615265792, 2013438793611071488, 1, 'FS8003P-6-EV', 5, 5, 'A1V16187-A1V16191', NULL, NULL, NULL, 1, 5, '2026-01-20 10:31:23', '2026-01-20 16:04:58', '2026-01-21 14:40:43', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013439058527506432, 2013439058523312128, 1, 'FS8001', 2, 2, 'A1V16181-A1V16182', NULL, NULL, NULL, 1, 5, '2026-01-20 10:31:23', '2026-01-20 16:04:58', '2026-01-21 14:40:43', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013479170460209152, 2013479170456014848, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V15514-5563', NULL, NULL, NULL, 1, 5, '2026-01-20 13:10:22', '2026-02-27 16:43:12', '2026-02-27 16:48:34', '2026-02-27 17:05:51', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013482430415884288, 2013482430411689984, 2, 'MF5712-N-250-B-A', 50, 50, 'PEGVA20133-PEGVA20182', NULL, '订单共500只，其中260只在新系统，剩下320只在25年电子版报告单上：\r\nMF5712-G-250-B-C：PEGUL19965-PEGUL19994 (30只25.12.31入库)；\r\nMF5712-N-250-B-A：PEGUL20009-PEGUL20088 (80只26.1.7入库)；\r\nMF5712-G-250-B-A：PEGUL19825-PEGUL19924 (100只25.12.25入库)；\r\nMF5712-G-250-B-R：PEGUL20089-PEGUL20118 (30只26.1.7入库)；', NULL, 1, 5, '2026-01-20 13:23:14', '2026-01-21 10:08:58', '2026-01-21 16:57:55', '2026-01-21 17:25:14', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013482430420078592, 2013482430411689984, 2, 'MF5712-G-250-B-A', 210, 210, 'PEGVA20183-PEGVA20392', NULL, '订单共500只，其中260只在新系统，剩下320只在25年电子版报告单上：\r\nMF5712-G-250-B-C：PEGUL19965-PEGUL19994 (30只25.12.31入库)；\r\nMF5712-N-250-B-A：PEGUL20009-PEGUL20088 (80只26.1.7入库)；\r\nMF5712-G-250-B-A：PEGUL19825-PEGUL19924 (100只25.12.25入库)；\r\nMF5712-G-250-B-R：PEGUL20089-PEGUL20118 (30只26.1.7入库)；', NULL, 1, 5, '2026-01-20 13:23:14', '2026-01-21 10:08:58', '2026-01-21 16:57:55', '2026-01-21 17:25:14', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013502073587224576, 2013502073583030272, 1, 'FS5001E-100-EV-H', 5, 5, 'A1V16176-A1V16180', NULL, NULL, NULL, 1, 5, '2026-01-20 14:41:23', '2026-01-20 16:04:58', '2026-01-21 14:52:35', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013518280524681216, 2013518280512098304, 3, 'MF50GD40', 1, 1, 'CSAVA31021', '6', NULL, NULL, 1, 5, '2026-01-20 15:49:45', '2026-01-20 15:49:56', '2026-01-21 14:38:35', '2026-01-21 14:54:43', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 10.20, NULL, NULL, NULL, 1522, 2, 2.89, NULL, 4);
INSERT INTO `siargo_product` VALUES (2013518750274146304, 2013518750265757696, 3, 'MF50GD40', 1, 1, 'CSAVA31022', '6', NULL, NULL, 1, 5, '2026-01-20 15:49:45', '2026-01-20 15:49:56', '2026-01-21 14:38:35', '2026-01-21 14:54:43', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 10.20, NULL, NULL, NULL, 1553, 1, 2.89, NULL, 3);
INSERT INTO `siargo_product` VALUES (2013519005539487744, 2013519005531099136, 3, 'MF50GD40', 1, 1, 'CSAVA31023', '6', NULL, NULL, 1, 5, '2026-01-20 15:49:45', '2026-01-20 15:49:56', '2026-01-21 14:38:35', '2026-01-21 14:54:43', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 10.50, NULL, NULL, NULL, 1641, 1, 2.88, NULL, 9);
INSERT INTO `siargo_product` VALUES (2013528478626729984, 2013528478618341376, 1, 'FSP1000-500-E-B', 2, 2, 'A1V17000-A1V17001', NULL, NULL, NULL, 1, 5, '2026-01-20 16:26:17', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:56:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536078089015296, 2013536078084820992, 1, 'MF4008-40-O8-CV-C', 5, 5, 'A1V15887-A1V15891', NULL, 'MF4008产品', NULL, 1, 5, '2026-01-21 14:13:27', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:56:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536530557947904, 2013536530553753600, 1, 'MF4003-5-R-BV-A', 1, 1, 'A1V15892', NULL, NULL, NULL, 1, 5, '2026-01-20 17:03:38', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:56:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536530557947905, 2013536530553753600, 1, 'MF3000S-5-R-BAZ-A', 2, 2, 'A1V15893-A1V15894', NULL, NULL, NULL, 1, 5, '2026-01-20 17:03:38', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:56:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536798397812736, 2013536798393618432, 1, 'FS4001-500-CV-A', 2, 2, 'A1V15857-A1V15858', NULL, NULL, NULL, 1, 5, '2026-01-20 17:03:38', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536798406201344, 2013536798393618432, 1, 'MF4003-2-O6-CV-A', 4, 4, 'A1V15859-A1V15862', NULL, NULL, NULL, 1, 5, '2026-01-20 17:03:38', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013536979457527808, 2013536979432361984, 1, 'MF4008-10-R-BV-A-0.5/4.5V', 2, 2, 'A1V16183-A1V16184', NULL, NULL, NULL, 1, 5, '2026-01-20 17:03:38', '2026-01-21 14:36:19', '2026-01-21 14:38:00', '2026-01-21 14:54:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013795165926182912, 2013795165921988608, 2, 'MF5712-G-250-B-C', 1, 1, 'PEGUI17878', NULL, 'GCF 540', NULL, 1, 5, '2026-01-21 10:05:56', '2026-01-21 14:17:00', '2026-01-22 17:03:13', '2026-01-22 17:56:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013795405202837505, 2013795405202837504, 2, 'MF5706-G-20-B-A', 1, 1, 'PEDUC15240', NULL, NULL, NULL, 1, 5, '2026-01-21 10:06:53', '2026-01-21 14:17:00', '2026-01-22 17:03:13', '2026-01-22 17:56:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013795743775444992, 2013795743771250688, 2, 'MF5708-G-100-B-C20R80', 1, 1, 'PEEVA20405', NULL, '定制程序V0203；GCF 908', NULL, 1, 5, '2026-01-21 10:08:14', '2026-01-21 14:17:00', '2026-01-22 17:03:13', '2026-01-22 17:56:31', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013854605522096128, 2013854605513707520, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A1V15564-5613', NULL, NULL, NULL, 1, 5, '2026-01-21 14:13:27', '2026-02-27 16:46:32', '2026-02-27 16:48:34', '2026-02-27 17:05:51', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013857363327307776, 2013857363323113472, 1, 'FS34008-20-O8-CV-A-500mesh', 58, 13, 'A1V15406-5463', NULL, NULL, NULL, 1, 5, '2026-01-21 14:13:27', '2026-02-27 14:31:29', '2026-02-27 14:31:45', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013862743675621376, 2013862743663038464, 1, 'FS4001E-30-CV-A', 5, 5, 'A1V16378-A1V16382', NULL, NULL, NULL, 1, 5, '2026-01-21 14:43:22', '2026-01-22 13:26:27', '2026-01-22 13:27:49', '2026-01-22 17:56:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013863048878346240, 2013863048874151936, 1, 'FS4001E-400-CV-A', 15, 15, 'A1V17002-A1V17016', NULL, NULL, NULL, 1, 5, '2026-01-21 14:43:22', '2026-01-22 13:26:27', '2026-01-22 13:27:49', '2026-01-22 17:56:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013863182659866624, 2013863182651478016, 1, 'FS8001N-500', 4, 4, 'A1V16374-A1V16377', NULL, NULL, NULL, 1, 5, '2026-01-21 14:43:22', '2026-01-22 13:26:27', '2026-01-22 13:27:49', '2026-01-22 17:56:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013863473413214208, 2013863473409019904, 1, 'FS8003P-6-EV-A', 5, 5, 'A1V17023-A1V17027', NULL, NULL, NULL, 1, 5, '2026-01-21 14:43:22', '2026-01-22 13:26:27', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013863740678459392, 2013863740670070784, 1, 'FS4001-1000-V-A', 39, 13, 'A1V16383-6421', NULL, NULL, NULL, 1, 5, '2026-01-21 14:43:22', '2026-01-22 13:26:27', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902190744031232, 2013902190739836928, 1, 'FS4008-50-O8-BV-A', 5, 5, 'A1V15621-A1V15625', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902190748225536, 2013902190739836928, 1, 'FS4008-50-R-BV-A', 3, 3, 'A1V15626-A1V15628', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902190748225537, 2013902190739836928, 1, 'FS4008-40-R-BV-A', 12, 12, 'A1V15629-A1V15640', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902190756614144, 2013902190739836928, 1, 'FS4008-20-O8-BV-A', 5, 5, 'A1V15641-A1V15645', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902190760808448, 2013902190739836928, 1, 'FS4008-10-R-BV-A', 5, 5, 'A1V15646-A1V15650', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902462920806400, 2013902462916612096, 1, 'MF4003-5-R-BV-A', 1, 1, 'A1V16186', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902628281241600, 2013902628277047296, 1, 'MF4008-20-O8-BV-A', 6, 6, 'A1V16422-A1V16427', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2013902628285435904, 2013902628277047296, 1, 'FS4008-10-R-BV-A', 2, 2, 'A1V16428-A1V16429', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-22 13:25:50', '2026-01-22 13:27:49', '2026-01-22 17:56:31', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014148709032251393, 2014148709032251392, 3, 'MF2025Be-AB-O', 3, 3, 'GLBVA10625-10627', '19', NULL, NULL, 1, 5, '2026-01-22 09:34:43', '2026-01-22 09:34:54', '2026-01-22 13:28:00', '2026-01-22 17:56:31', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014149304002662400, 2014149303994273792, 3, 'MF80GD160', 1, 1, 'CSCVA31001', '16', NULL, NULL, 1, 5, '2026-01-22 09:34:43', '2026-01-22 09:34:54', '2026-01-23 14:45:07', '2026-01-23 17:11:26', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.90, NULL, NULL, NULL, 1527, 2, 2.89, NULL, 6);
INSERT INTO `siargo_product` VALUES (2014149586270932993, 2014149586270932992, 3, 'MF80GD160', 1, 1, 'CSCVA31002', '16', NULL, NULL, 1, 5, '2026-01-22 09:34:43', '2026-01-22 09:34:54', '2026-01-23 14:45:07', '2026-01-23 17:11:26', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.60, NULL, NULL, NULL, 1504, 1, 2.89, NULL, 3);
INSERT INTO `siargo_product` VALUES (2014162480303820801, 2014162480303820800, 2, 'MFC2000-005-K6M-BV-0-A', 6, 6, 'MC2VA11169-MC2VA11174', NULL, '', NULL, 1, 5, '2026-01-22 10:25:31', '2026-01-22 14:01:37', '2026-01-23 14:44:42', '2026-01-23 17:11:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014162480303820802, 2014162480303820800, 2, 'MFC2000-002-K6M-BV-0-C35N65', 6, 6, 'MC2VA11175-MC2VA11180', NULL, 'GCF 785', NULL, 1, 5, '2026-01-22 10:25:31', '2026-01-22 14:01:37', '2026-01-23 14:44:42', '2026-01-23 17:11:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014170909722398720, 2014170909718204416, 1, 'FS4308-10-O6-BV-A', 130, 13, 'A1V12420-A1V12549', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014170909734981632, 2014170909718204416, 1, 'FS4308-2-R-BV-A', 20, 13, 'A1V12550-A1V12569', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014170909739175936, 2014170909718204416, 1, 'FS4308-10-R-BV-A', 10, 10, 'A1V12570-A1V12579', NULL, NULL, NULL, 1, 5, '2026-01-22 11:02:24', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014235894951563264, 2014235894947368960, 2, 'MF4712-R4F-200-B-N', 132, 132, 'A1V16203-A1V16334', NULL, 'MF4512-R4F-200-B-N', NULL, 1, 5, '2026-01-22 15:17:14', '2026-01-23 14:41:15', '2026-01-26 16:02:19', '2026-01-26 16:24:20', 2001546811778514944, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014247868934508544, 2014247868921925632, 1, 'FS4308-40-O6-EV-A-0.5/4.5V', 100, 13, 'A1V15202-5301', NULL, NULL, NULL, 1, 5, '2026-01-22 16:12:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014248189865873408, 2014248189861679104, 1, 'MF3000M-2-O6-BVZ-A', 1, 1, 'A1V16196', NULL, NULL, NULL, 1, 5, '2026-01-22 16:12:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014248189870067712, 2014248189861679104, 1, 'MF3000M-5-O6-BVZ-A', 1, 1, 'A1V16197', NULL, NULL, NULL, 1, 5, '2026-01-22 16:12:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014248518636392448, 2014248518628003840, 1, 'FS34103-2-O4-V-A', 4, 4, 'A1V17060-A1V17063', NULL, 'FS34308-2-O4-V-A-0.5/4.5V', NULL, 1, 5, '2026-01-22 16:12:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014248652896063488, 2014248652891869184, 1, 'FS4308-10-R-BV-A', 5, 5, 'A1V17030-A1V17034', NULL, NULL, NULL, 1, 5, '2026-01-22 16:12:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014248780839112704, 2014248780830724096, 1, 'MF4008-10-O8-BV-C-0.5/4.5V', 2, 2, 'A1V16480-A1V16481', NULL, NULL, NULL, 1, 5, '2026-01-22 16:12:23', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014248973026316289, 2014248973026316288, 1, 'FS4308-30-O8-BV-A', 6, 6, 'A1V17017-A1V17022', NULL, NULL, NULL, 1, 5, '2026-01-22 16:12:22', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014250597073408000, 2014250597069213696, 1, 'FS1015E-150-ISO-V-A', 10, 10, 'A1V16483-A1V16492', NULL, NULL, NULL, 1, 5, '2026-01-22 16:23:35', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014257692057391104, 2014257692036419584, 3, 'MF80GD160', 1, 1, 'CSCVA31003', '16', NULL, NULL, 1, 5, '2026-01-23 14:50:42', '2026-01-23 14:51:25', '2026-01-23 15:36:24', '2026-01-23 17:11:26', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.60, NULL, NULL, NULL, 1565, 3, 2.88, NULL, 7);
INSERT INTO `siargo_product` VALUES (2014257768364363776, 2014257768355975168, 1, 'FS4003-4-R-CV-A', 20, 13, 'A1V17069-A1V17088', NULL, NULL, NULL, 1, 5, '2026-01-23 11:13:35', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014506015863459840, 2014506015859265536, 1, 'FS1015CL-60-ISO-C', 200, 50, 'A1V15973-6172', NULL, '厂内型号FS1015E-60-ISO-V-C', NULL, 1, 5, '2026-01-23 11:13:35', '2026-01-26 15:28:17', '2026-01-27 16:38:47', '2026-01-28 13:00:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014530586511527936, 2014530586498945024, 1, 'FS4003-2-O6-CV-A', 50, 13, 'A1V15917-5966', NULL, '响应时间200ms，滤波深度20，程序3030', NULL, 1, 5, '2026-01-23 11:13:35', '2026-01-23 17:08:53', '2026-01-27 15:35:39', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014530887951962113, 2014530887951962112, 1, 'FS1100-0F250', 4, 4, 'A1V17064-A1V17067', NULL, NULL, NULL, 1, 5, '2026-01-23 11:13:35', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014531182954139648, 2014531182949945344, 1, 'FS5001E-1000-EV-N', 50, 13, 'A1V16430-6479', NULL, NULL, NULL, 1, 5, '2026-01-23 11:13:35', '2026-01-23 14:41:34', '2026-01-27 15:35:39', '2026-01-27 15:42:43', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001548007356481536, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014532645633773568, 2014532645625384960, 1, 'FS5001E-500-EV-A', 5, 5, 'A1V17729-A1V17733', NULL, NULL, NULL, 1, 5, '2026-01-23 11:13:34', '2026-01-23 14:41:15', '2026-01-23 14:44:42', '2026-01-23 17:11:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014565198570835969, 2014565198570835968, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 13, 'A1V15464-5513', NULL, 'CO2测系数980', NULL, 1, 5, '2026-01-23 13:06:39', '2026-02-28 15:30:31', '2026-03-02 16:56:35', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014574626015662080, 2014574626007273472, 1, 'FS5001E-6000-EV-A', 2, 2, 'A1V18361-A1V18362', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:30:11', '2026-01-26 16:06:39', '2026-01-26 16:24:20', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014578482413948928, 2014578482409754624, 2, 'MF5708-N-100-B-A', 1, 1, 'PAEVA20408', NULL, NULL, NULL, 1, 5, '2026-01-23 13:58:33', '2026-01-23 17:15:43', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014578590958342144, 2014578590954147840, 2, 'MF5706-G-20-B-O', 37, 37, 'PEDVA20409-PEDVA20445', NULL, NULL, NULL, 1, 5, '2026-01-23 13:58:59', '2026-01-26 10:21:52', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014579374856982528, 2014579374848593920, 2, 'MF5003-N1F-e-0.15-15-15-AB-D-A', 1, 1, 'G5CTC11223', NULL, NULL, NULL, 1, 5, '2026-01-23 14:02:06', '2026-01-23 17:12:22', '2026-01-26 16:02:19', '2026-01-26 16:24:20', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014579540917866496, 2014579540913672192, 2, 'MF5019-N6M-e-8-600-15-AB-D-C', 1, 1, 'G5HSJ11157', NULL, 'GCF 670', NULL, 1, 5, '2026-01-23 14:02:46', '2026-01-23 17:15:17', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580027398410240, 2014580027343884288, 2, 'MF5612-N-300-AB-D-O', 1, 1, 'G6GVA22768', NULL, NULL, NULL, 1, 5, '2026-01-23 14:04:42', '2026-01-23 17:14:20', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580173041422336, 2014580173037228032, 2, 'MF5212-Q-200-N', 1, 1, 'G7GVA22715', NULL, NULL, NULL, 1, 5, '2026-01-23 14:05:16', '2026-01-23 17:13:47', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580173045616640, 2014580173037228032, 2, 'MF5219-Q-800-N', 1, 1, 'G7HVA22716', NULL, NULL, NULL, 1, 5, '2026-01-23 14:05:17', '2026-01-23 17:13:47', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580304402829312, 2014580304398635008, 2, 'MF5212-Q-300-A', 10, 10, 'G7GVA22736-G7GVA22745', NULL, NULL, NULL, 1, 5, '2026-01-23 14:05:48', '2026-01-23 17:12:58', '2026-01-26 16:02:19', '2026-01-26 16:24:20', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580410061541376, 2014580410057347072, 2, 'MF5219-Q-600-A', 1, 1, 'G7HVA22746', NULL, NULL, NULL, 1, 5, '2026-01-23 14:06:13', '2026-01-23 17:13:26', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580513727959040, 2014580513723764736, 2, 'MF5212-Q-300-A', 3, 3, 'G7GVA22701-G7GVA22703', NULL, NULL, NULL, 1, 5, '2026-01-23 14:06:38', '2026-01-23 17:14:05', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580679474270208, 2014580679465881600, 2, 'MF5619-N-800-ABD-D-O', 1, 1, 'G6HSJ98183', NULL, NULL, NULL, 1, 5, '2026-01-23 14:07:17', '2026-01-23 17:14:35', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014580815466188800, 2014580815457800192, 2, 'MF5619-N-48m3/h-AB-D-O', 1, 1, 'G6HMH40155', NULL, NULL, NULL, 1, 5, '2026-01-23 14:07:50', '2026-01-23 17:16:13', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014581001055752192, 2014581001047363584, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HPH37357', NULL, NULL, NULL, 1, 5, '2026-01-23 14:08:34', '2026-01-23 17:14:49', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014584581640802305, 2014584581640802304, 1, 'FS4308-40-O6-BV-C', 1, 1, 'A1V16482', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:40', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014584704072536065, 2014584704072536064, 1, 'MF4008-20-O8-BV-A', 2, 2, 'A1V17825-A1V17826', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:20', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014584847924580352, 2014584847920386048, 1, 'FS4308-20-O6-BV-A', 2, 2, 'A1V17725-A1V17726', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014585166725238784, 2014585166721044480, 1, 'MF4003-5-R-BV-A', 4, 4, 'A1V17591-A1V17594', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014585400255696896, 2014585400251502592, 1, 'FS4008-10-R-BV-A', 1, 1, 'A1V17728', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014585501799796736, 2014585501795602432, 1, 'FS4008-10-R-BV-A', 1, 1, 'A1V17821', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014585774161121280, 2014585774156926976, 1, 'FS4008-10-O8-V-A', 2, 2, 'A1V17822-A1V17823', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014586036946849793, 2014586036946849792, 1, 'FS4001E-30-V-A', 1, 1, 'A1V17028', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014586036980404224, 2014586036946849792, 1, 'FS4008-10-O6-V-A', 1, 1, 'A1V17029', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014591515550928896, 2014591515546734592, 3, 'MF80GD160', 1, 1, 'CSCVA31003', '16', NULL, NULL, 1, 5, '2026-01-27 11:31:24', '2026-01-27 11:31:35', '2026-02-02 16:17:45', '2026-02-02 16:24:16', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.60, NULL, NULL, NULL, 1563, 2, 2.89, NULL, 2);
INSERT INTO `siargo_product` VALUES (2014592520296452096, 2014592520292257792, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HVA22772', NULL, NULL, NULL, 1, 5, '2026-01-23 14:54:20', '2026-01-23 14:58:17', '2026-01-23 15:36:16', '2026-01-23 17:11:26', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2014599345444081664, 2014599345439887360, 1, 'MF3000S-1500-R-BZZ-A', 7, 7, 'A1V16993-A1V16999', NULL, NULL, NULL, 1, 5, '2026-01-23 15:21:36', '2026-01-26 15:28:17', '2026-01-26 16:06:39', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015609776866447360, 2015609776858058752, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GUK42375', NULL, NULL, NULL, 1, 5, '2026-01-26 10:16:33', '2026-01-26 15:38:03', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015614053391388672, 2015614053387194368, 1, 'MF3000S-100-R-BZZ-A', 1, 1, 'A1V17893', NULL, NULL, NULL, 1, 5, '2026-01-26 10:34:44', '2026-01-26 15:26:36', '2026-01-26 16:07:17', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015614185583267841, 2015614185583267840, 1, 'MF3000S-100-R-BZZ-A', 4, 4, 'A1V16192-A1V16195', NULL, NULL, NULL, 1, 5, '2026-01-26 10:34:44', '2026-01-26 15:26:36', '2026-01-26 16:07:17', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015614305515196416, 2015614305511002112, 1, 'MF3000S-100-R-BZZ-A', 4, 4, 'A1V16335-A1V16338', NULL, NULL, NULL, 1, 5, '2026-01-26 10:34:44', '2026-01-26 15:26:36', '2026-01-26 16:07:17', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015620542843703297, 2015620542843703296, 2, 'MFC2000-0100-K1-AB-1-N', 2, 2, 'MC2VA11182-MC2VA11183', NULL, '精度 1.0%', NULL, 1, 5, '2026-01-26 10:59:20', '2026-01-26 13:16:06', '2026-01-26 16:02:19', '2026-01-26 16:24:20', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015620601861754881, 2015620601861754880, 2, 'MFC2000-2000-K3M-B-1-A', 1, 1, 'MC2VA11181', NULL, NULL, NULL, 1, 5, '2026-01-26 10:59:34', '2026-01-26 13:16:25', '2026-01-27 15:36:11', '2026-01-27 16:30:19', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015620713291829249, 2015620713291829248, 2, 'MF4710-G4-100-BV-N', 1, 1, 'A1V17068', NULL, NULL, NULL, 1, 5, '2026-01-26 11:00:01', '2026-01-26 13:15:44', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015621371067748352, 2015621371063554048, 1, 'FS4001E-500-V-H', 1, 1, 'A1V17035', NULL, NULL, NULL, 1, 5, '2026-01-26 13:22:48', '2026-01-26 15:26:36', '2026-01-26 16:07:17', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015621371071942656, 2015621371063554048, 1, 'FS4003-5-O6-V-A', 1, 1, 'A1V17036', NULL, NULL, NULL, 1, 5, '2026-01-26 13:22:48', '2026-01-26 15:26:36', '2026-01-26 16:07:17', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015624905230241793, 2015624905230241792, 1, 'FS6122-200F200-40P40-TH1', 2, 2, 'A1V17037-A1V17038', NULL, NULL, NULL, 1, 5, '2026-01-26 13:22:48', '2026-01-26 15:26:36', '2026-01-26 16:07:17', '2026-01-26 16:24:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015626078649700352, 2015626078641311744, 3, 'MF2025Be-AB-O', 1, 1, 'GLBVA10628', '19', NULL, NULL, 1, 5, '2026-01-27 11:31:24', '2026-01-27 11:31:35', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015626683246039040, 2015626683241844736, 2, 'MF5219-E-Y-1000-F', 15, 15, 'G7HTJ26876/G7HTJ26877/G7HTJ26879/G7HTJ26881/G7HTJ26882/G7HTJ26887/G7HTJ26888/G7HTJ26892/G7HTJ26894/G7HTJ26901/G7HTJ26902/G7HTJ26905/G7HTJ26906/G7HTJ26912/G7HTJ26913/G7HTJ26913', NULL, NULL, NULL, 1, 5, '2026-01-26 11:23:44', '2026-01-26 15:36:51', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015626913546883072, 2015626913534300160, 2, 'MF5219-E-Q-600-D-P', 4, 4, 'G7HVA43871-G7HVA43874', NULL, NULL, NULL, 1, 5, '2026-01-26 11:24:39', '2026-01-26 15:39:01', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015627036888780800, 2015627036884586496, 2, 'MF5219-E-Y-800-C', 1, 1, 'G7HTF22456', NULL, NULL, NULL, 1, 5, '2026-01-26 11:25:08', '2026-01-26 15:38:21', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015627228178403328, 2015627228174209024, 2, 'MF5219-E-Q-1000-F', 2, 2, 'G7HUC33922/G7HUC33931', NULL, NULL, NULL, 1, 5, '2026-01-26 11:25:54', '2026-01-26 15:38:39', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015627471548698624, 2015627471544504320, 2, 'MF5212-Q-300-A', 1, 1, 'G7GRJ75875', NULL, NULL, NULL, 1, 5, '2026-01-26 11:26:52', '2026-01-26 15:37:43', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015628128989073409, 2015628128989073408, 2, 'MF5008-N3M-e-1.2-120-15-AB-D-A-AU', 1, 1, 'G5EVA11755', NULL, '-AU为客户型号', NULL, 1, 5, '2026-01-26 11:29:29', '2026-01-26 15:37:18', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015656873934311424, 2015656873930117120, 1, 'FS4001E-500-CV-A', 21, 13, 'A1V18312-A1V18329', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:52', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015656873938505728, 2015656873930117120, 1, 'FS4001E-100-CV-A', 30, 13, 'A1V18331-A1V18360', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:52', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015657215388405760, 2015657215384211456, 1, 'FS4001E-200-CV-O', 40, 13, 'A1V17854-A1V17886', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:52', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015659039990337536, 2015659039986143232, 2, 'MF4701-B1-500-BV-N', 1, 1, 'A1V18563', NULL, '0~5V；很急，上午标定下午检', NULL, 1, 5, '2026-01-26 13:32:18', '2026-01-26 13:42:46', '2026-01-26 15:36:50', '2026-01-26 16:22:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015671745132744704, 2015671745128550400, 1, 'FS4001-100-CV-A', 1, 1, 'A1V19792', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:52', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672025035427840, 2015672025031233536, 1, 'FS4001E-60-CV-A', 1, 1, 'A1V19839', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:52', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672210490773504, 2015672210486579200, 1, 'FS4001E-30-V-A', 1, 1, 'A1V17824', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672509905358848, 2015672509888581632, 1, 'MF3000S-20-O6-EVN-A', 16, 16, 'A1V17828-A1V17843', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672509909553152, 2015672509888581632, 1, 'FS5001E-2000-EV-A', 2, 2, 'A1V17844-A1V17845', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672509917941760, 2015672509888581632, 1, 'FS5001E-4000-EV-A', 2, 2, 'A1V17846-A1V17847', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672509922136064, 2015672509888581632, 1, 'FS5001E-6000-EV-A', 2, 2, 'A1V17848-A1V17849', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015672869118136320, 2015672869113942016, 1, 'MF3000M-200-R-BAZ-A', 1, 1, 'A1V17827', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:29:12', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015694219081797632, 2015694219077603328, 2, 'MF6600-10SLPM', 4, 4, 'FAGVA17591-FAGVA17594', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015694343191252992, 2015694343187058688, 2, 'MF6600-70SLPM', 1, 1, 'FAGTI12186', NULL, NULL, NULL, 1, 5, '2026-01-26 15:54:17', '2026-01-26 17:28:51', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015707435249815552, 2015707435245621248, 1, 'FS4003-5-R-BV-A', 20, 13, 'A1V16339-A1V16358', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-27 16:37:49', '2026-01-27 16:38:06', '2026-01-28 13:00:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015707435249815553, 2015707435245621248, 1, 'FS4003-5-O6-BV-A', 5, 5, 'A1V16359-A1V16363', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-27 16:37:49', '2026-01-27 16:38:06', '2026-01-28 13:00:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015707435254009856, 2015707435245621248, 1, 'FS4003-2-O6-BV-A', 5, 5, 'A1V16364-A1V16368', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-27 16:37:49', '2026-01-27 16:38:06', '2026-01-28 13:00:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015707435258204160, 2015707435245621248, 1, 'FS4003-2-R-BV-A', 5, 5, 'A1V16369-A1V16373', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-27 16:37:49', '2026-01-27 16:38:07', '2026-01-28 13:00:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015712259391475712, 2015712259387281408, 1, 'MF6600-70SLPM', 2, 2, 'FAGTI12192', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-27 16:37:49', '2026-01-27 16:38:06', '2026-01-28 13:00:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015967487772708864, 2015967487764320256, 1, 'MF3000S-1500-R-EVZ-A', 1, 1, 'A1V19847', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-27 15:30:47', '2026-01-27 15:34:40', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015970378617049088, 2015970378612854784, 2, 'MF5706-G-20-B-A', 1, 1, 'PEDVA20526', NULL, NULL, NULL, 1, 5, '2026-01-27 10:09:27', '2026-01-27 15:45:06', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015970772185370624, 2015970772181176320, 2, 'MF4701-R1-200-BV-A', 2, 2, 'A2V01097-A2V01098', NULL, NULL, NULL, 1, 5, '2026-01-27 10:11:01', '2026-01-27 15:46:37', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015970772189564928, 2015970772181176320, 2, 'MF4708-R3-10-BV-A', 2, 2, 'A2V01099-A2V01100', NULL, NULL, NULL, 1, 5, '2026-01-27 10:11:01', '2026-01-27 15:46:37', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015970772193759232, 2015970772181176320, 2, 'MF4719-R6F-500-BV-A', 4, 4, 'A2V01101-A2V01104', NULL, NULL, NULL, 1, 5, '2026-01-27 10:11:01', '2026-01-27 15:46:37', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015971021020844033, 2015971021020844032, 2, 'MF4708-B3-50-BV-A', 1, 1, 'A1V17039', NULL, 'B3 -> R3', NULL, 1, 5, '2026-01-27 10:12:00', '2026-01-28 15:40:56', '2026-01-28 16:06:34', '2026-01-28 17:07:11', 2001546811778514944, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015971021029232640, 2015971021020844032, 2, 'MF4701-G1-100-BA-E', 1, 1, 'A1V17040', NULL, '', NULL, 1, 5, '2026-01-27 10:12:01', '2026-01-28 15:40:56', '2026-01-28 16:06:34', '2026-01-28 17:07:11', 2001546811778514944, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015971319160360960, 2015971319156166656, 2, 'MF4608-N2-50-BV-C20R80', 2, 2, 'A1V17589-A1V17590', NULL, 'GCF1078', NULL, 1, 5, '2026-01-27 10:13:12', '2026-01-27 15:46:01', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015971543656288257, 2015971543656288256, 2, 'MF4708-N3-15-A-A', 1, 1, 'A1V17850', NULL, 'GCF 4500', NULL, 1, 5, '2026-01-27 10:14:05', '2026-01-27 15:45:26', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015971663126843392, 2015971663122649088, 2, 'MF4701-R1-200-BV-C', 1, 1, 'A2V01006', NULL, '实标', NULL, 1, 5, '2026-01-27 10:14:34', '2026-01-27 15:46:15', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015971949300011009, 2015971949300011008, 2, 'FS4701-B1-100-BV-A', 8, 8, 'A2V01007-A2V01014', NULL, 'B1 -> R1', NULL, 1, 5, '2026-01-27 10:15:42', '2026-01-28 17:17:29', '2026-01-29 16:43:17', '2026-01-29 16:52:48', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015972332311269376, 2015972332307075072, 2, 'FS4701-N1-500-BV-A', 2, 2, 'A1V19895-A1V19896', NULL, NULL, NULL, 1, 5, '2026-01-27 10:17:13', '2026-01-28 17:17:29', '2026-01-29 16:43:17', '2026-01-29 16:52:48', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015974091104571392, 2015974091096182784, 1, 'FS4308-30-O8-CV-A-0.5/4.5V', 100, 13, 'A1V18210-A1V18309', NULL, NULL, NULL, 1, 5, '2026-01-27 10:24:20', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015985925085843456, 2015985925069066240, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30014', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:45', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.60, 17.90, 4.791, 1660, 2, NULL, NULL, 3);
INSERT INTO `siargo_product` VALUES (2015986253634064385, 2015986253634064384, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30005', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:24', '2026-01-29 16:47:05', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 14.10, 16.80, 4.770, 1618, 1, NULL, NULL, 3);
INSERT INTO `siargo_product` VALUES (2015986756954738689, 2015986756954738688, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30006', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.20, 17.60, 4.760, 1641, 0, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2015987984413609985, 2015987984413609984, 2, 'MF4712-R4-300-B-A', 29, 29, 'A1V15172-A1V15200', NULL, NULL, NULL, 1, 5, '2026-01-27 11:19:25', '2026-01-27 15:45:45', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015988493425954816, 2015988493417566208, 1, 'FS5001E-500-EV-A', 5, 5, 'A1V19848-A1V19852', NULL, '', NULL, 1, 5, '2026-01-27 11:22:17', '2026-01-28 16:03:30', '2026-01-28 16:06:34', '2026-01-28 17:07:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015988493425954817, 2015988493417566208, 1, 'FS5001E-1000-EV-A', 10, 10, 'A1V19853-A1V19862', NULL, '', NULL, 1, 5, '2026-01-27 11:22:17', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015988493425954818, 2015988493417566208, 1, 'FS5001E-1000-EV-R95H5', 5, 5, 'A1V19863-A1V19867', NULL, '系数1346', NULL, 1, 5, '2026-01-27 11:22:17', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015988671566434305, 2015988671566434304, 1, 'FS5001E-500-EV-A', 5, 5, 'A1V19793-A1V19797', NULL, NULL, NULL, 1, 5, '2026-01-27 11:22:17', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015988671566434306, 2015988671566434304, 1, 'FS5001E-1000-EV-A', 5, 5, 'A1V19798-A1V19802', NULL, NULL, NULL, 1, 5, '2026-01-27 11:22:17', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015989461118996480, 2015989461106413568, 2, 'MFC2000-050-N3F-B-1-A-J0604', 1, 1, 'MC2UJ10866', NULL, NULL, NULL, 1, 5, '2026-01-27 15:44:35', '2026-01-27 15:44:50', '2026-01-28 16:08:56', '2026-01-28 17:07:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2015989861675028481, 2015989861675028480, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30011', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.60, 16.70, 4.760, NULL, 4, NULL, NULL, 13);
INSERT INTO `siargo_product` VALUES (2015991536611610625, 2015991536611610624, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30008', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 14.50, 17.10, 4.810, 1638, 1, NULL, NULL, 4);
INSERT INTO `siargo_product` VALUES (2015991902916956161, 2015991902916956160, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30007', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.70, 16.60, 4.805, 1688, 3, NULL, NULL, 15);
INSERT INTO `siargo_product` VALUES (2015992219922452481, 2015992219922452480, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30010', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.10, 18.20, 4.780, 1666, 1, NULL, NULL, 6);
INSERT INTO `siargo_product` VALUES (2015992494695501825, 2015992494695501824, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30009', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 15.60, 16.50, 4.805, 1666, 0, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2015992783599161345, 2015992783599161344, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30015', '23', NULL, NULL, 1, 5, '2026-01-27 11:38:29', '2026-01-28 13:33:25', '2026-01-29 16:47:05', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 14.10, 18.70, 4.781, 1676, 1, NULL, NULL, 10);
INSERT INTO `siargo_product` VALUES (2015993048352018433, 2015993048352018432, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30012', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.60, 17.50, 4.810, 1657, 1, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2015993379966275585, 2015993379966275584, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30013', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.20, 17.20, 4.760, 1649, 1, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2016023620004728833, 2016023620004728832, 1, 'FS6122-250F250-5P100-TH0', 1, 1, 'A2V01027', NULL, NULL, NULL, 1, 5, '2026-01-27 13:43:55', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016024057315446785, 2016024057315446784, 1, 'FS6122-250F250-5P100-TH1', 500, 80, 'A1V17089-A1V17588', NULL, NULL, NULL, 1, 5, '2026-01-27 13:43:55', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016025779588943872, 2016025779580555264, 1, 'PFLOW1015CL-KS3', 2400, 512, 'A1V12599-A1V14986', NULL, 'FS1015E-74.1-ISO-C，精度1.5+0.2%，输出0.25-2.75V，零点±0.005mv，不截流', NULL, 1, 5, '2026-01-28 10:12:24', '2026-01-29 16:39:06', '2026-01-30 16:48:49', '2026-01-30 17:18:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016033018475302913, 2016033018475302912, 1, 'FS1015E-150-ISO-A-BEC', 2, 2, 'A1V17769-A1V17770', NULL, 'BEC是客户型号', NULL, 1, 5, '2026-01-27 14:22:30', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016033181314961409, 2016033181314961408, 1, 'FS1015CL-60-ISO-C', 1, 1, 'A1V17851', NULL, NULL, NULL, 1, 5, '2026-01-27 14:22:30', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016046601447788545, 2016046601447788544, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30015', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 14.10, 18.70, 4.781, 1676, 3, NULL, NULL, 8);
INSERT INTO `siargo_product` VALUES (2016047119612104705, 2016047119612104704, 1, 'FS4008-20-R-BV-A', 100, 13, 'A1V15651-5750', NULL, NULL, NULL, 1, 5, '2026-01-27 15:14:32', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016047119620493312, 2016047119612104704, 1, 'FS4008-20-R-BV-A', 100, 13, 'A1V15751-5850', NULL, NULL, NULL, 1, 5, '2026-01-27 15:14:32', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016049504845680641, 2016049504845680640, 1, 'MF3000M-5-O6-BVP-A', 1, 1, 'A1V17041', NULL, NULL, NULL, 1, 5, '2026-01-27 15:24:07', '2026-01-27 16:25:03', '2026-01-27 16:25:31', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016049504845680642, 2016049504845680640, 1, 'MF3000M-200-R-BVP-A', 2, 2, 'A1V17042-A1V17043', NULL, NULL, NULL, 1, 5, '2026-01-27 15:24:07', '2026-01-27 16:25:03', '2026-01-27 16:25:31', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016049504845680643, 2016049504845680640, 1, 'MF3000M-2-O6-BVZ-A', 2, 2, 'A1V17044-A1V17045', NULL, NULL, NULL, 1, 5, '2026-01-27 15:24:07', '2026-01-27 16:25:03', '2026-01-27 16:25:31', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016049504845680644, 2016049504845680640, 1, 'MF3000M-1500-G4F-BAZ-A', 1, 1, 'A1V17046', NULL, NULL, NULL, 1, 5, '2026-01-27 15:24:07', '2026-01-27 16:25:02', '2026-01-27 16:25:31', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016049504845680645, 2016049504845680640, 1, 'MF3000M-10-R-BVP-A', 2, 2, 'A1V17047-A1V17048', NULL, NULL, NULL, 1, 5, '2026-01-27 15:24:07', '2026-01-27 16:25:03', '2026-01-27 16:25:31', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016049504845680646, 2016049504845680640, 1, 'FS4308-50-R-BV-C', 11, 11, 'A1V17049-A1V17059', NULL, NULL, NULL, 1, 5, '2026-01-27 15:24:07', '2026-01-27 16:25:03', '2026-01-27 16:25:31', '2026-01-27 16:30:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016058536004276225, 2016058536004276224, 1, 'MF3000M-3000-R-BAZ-A', 1, 1, 'A1V17727', NULL, '程序0136', NULL, 1, 5, '2026-01-27 15:59:53', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016071959681290241, 2016071959681290240, 1, 'FS4308-10-R-CV-A', 5, 5, 'A1V17759-A1V17763', NULL, NULL, NULL, 1, 5, '2026-01-27 16:53:16', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016071959681290242, 2016071959681290240, 1, 'FS4003-5-R-BV-A', 5, 5, 'A1V17764-A1V17768', NULL, NULL, NULL, 1, 5, '2026-01-27 16:53:16', '2026-01-28 16:03:30', '2026-01-28 16:06:20', '2026-01-28 17:07:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016123446881013760, 2016123446876819456, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HVA22773', NULL, NULL, NULL, 1, 5, '2026-01-27 20:17:42', '2026-01-28 13:25:05', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016123592205258752, 2016123592201064448, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HVA22777', NULL, '英文', NULL, 1, 5, '2026-01-27 20:18:16', '2026-01-28 13:25:26', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016123715589099521, 2016123715589099520, 2, 'MF5619-N-800-AB-D-O', 2, 2, 'G6HVA22775-G6HVA22776', NULL, NULL, NULL, 1, 5, '2026-01-27 20:18:46', '2026-01-28 13:24:43', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016329838128648192, 2016329838120259584, 2, 'MF5212-E-Q-400-D', 300, 300, 'G7GVA43875-G7GVA44174', NULL, NULL, NULL, 1, 5, '2026-01-28 09:57:49', '2026-01-29 15:04:18', '2026-01-29 16:43:17', '2026-01-29 16:52:36', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016334711784067072, 2016334711779872768, 2, 'MF5212-Q-300-A', 2, 2, 'G7GVB22789-G7GVB22790', NULL, NULL, NULL, 1, 5, '2026-01-28 10:17:11', '2026-01-28 13:23:36', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016334711784067073, 2016334711779872768, 2, 'MF5219-Q-800-A', 5, 5, 'G7HVB22784-G7HVB22788', NULL, NULL, NULL, 1, 5, '2026-01-28 10:17:11', '2026-01-28 13:23:36', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016335712339480577, 2016335712339480576, 2, 'MF5612-N-200-ABD-D-A', 1, 1, 'G6GVB22779', NULL, NULL, NULL, 1, 5, '2026-01-28 10:21:10', '2026-01-28 13:24:27', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016335761828073473, 2016335761828073472, 2, 'MF5612-N-200-ABD-D-A', 1, 1, 'G6GVB22778', NULL, NULL, NULL, 1, 5, '2026-01-28 10:21:21', '2026-01-28 13:24:14', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016335840253169665, 2016335840253169664, 2, 'MF5612-N-200-ABD-D-A', 2, 2, 'G6GVB22780-G6GVB22781', NULL, NULL, NULL, 1, 5, '2026-01-28 10:21:40', '2026-01-28 13:23:56', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016344582361305088, 2016344582352916480, 2, 'MF25HD-10-B-B-A', 1, 1, 'GHLVB10717', NULL, '第一个-B是BSPT 1“ (R8M)', NULL, 1, 5, '2026-01-28 10:56:24', '2026-01-29 15:04:26', '2026-01-29 16:43:17', '2026-01-29 16:52:36', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349154970554369, 2016349154970554368, 1, 'FS4008-50-O8-V-A', 6, 6, 'A1V19833-A1V19838', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:10', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:55', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349340065189888, 2016349340044218368, 1, 'FS4008-30-O8-V-A', 2, 2, 'A1V19897-A1V19898', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:10', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:55', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349682316201985, 2016349682316201984, 1, 'FS4308-50-O8-BV-A', 5, 5, 'A1V18194-A1V18198', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349682316201986, 2016349682316201984, 1, 'FS4308-2-O8-BV-A', 5, 5, 'A1V18199-A1V18203', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:55', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349682316201987, 2016349682316201984, 1, 'FS8001-500-EV-A', 5, 5, 'A1V18205-A1V18209', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:55', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349951980589057, 2016349951980589056, 1, 'MF4008-30-O6-V-A', 1, 1, 'A1V19884', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349951980589058, 2016349951980589056, 1, 'FS4003-2-R-BV-A', 8, 8, 'A1V19885-A1V19892', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016349951980589059, 2016349951980589056, 1, 'FS4008-10-O8-BV-A', 2, 2, 'A1V19893-A1V19894', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016351341796773888, 2016351341788385280, 1, 'MF4008-20-O8-BV-A', 5, 5, 'A2V01001-A2V01005', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016351511334735873, 2016351511334735872, 1, 'FS4308-20-R/O8-BV-A', 6, 6, 'A1V18566-A1V18571', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016351657552367616, 2016351657510424576, 1, 'FS4008-10-O8-BV-A', 1, 1, 'A1V17852', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016351920732360705, 2016351920732360704, 1, 'MF4008-10-O8-BV-A', 20, 13, 'A1V17736-A1V17749', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016351920732360706, 2016351920732360704, 1, 'MF4003-5-R-BV-A', 5, 5, 'A1V17754-A1V17758', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016352122021203968, 2016352121996038144, 1, 'MF4003-5-R-BV-A', 1, 1, 'A1V19840', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016352122021203969, 2016352121996038144, 1, 'MF3000S-5-R-BAZ-A', 2, 2, 'A1V19841-A1V19842', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016352122021203970, 2016352121996038144, 1, 'MF4008-10-O8-BV-A', 4, 4, 'A1V19843-A1V19846', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016352370210754561, 2016352370210754560, 1, 'MF4003-5-R-BV-A', 16, 16, 'A1V19868-A1V19883', NULL, NULL, NULL, 1, 5, '2026-01-28 11:35:01', '2026-01-29 16:39:06', '2026-01-29 16:40:29', '2026-01-29 16:52:48', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016382610622500865, 2016382610622500864, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30018', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 15.70, 16.80, 4.785, 1659, 2, NULL, NULL, 13);
INSERT INTO `siargo_product` VALUES (2016382880769232896, 2016382880752455680, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30016', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:06', '2026-01-29 16:52:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 13.00, 17.20, 4.794, 1656, 1, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2016383159719809025, 2016383159719809024, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30017', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:05', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 11.10, 15.40, 4.769, 1660, 3, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2016383412166578176, 2016383412158189568, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30019', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:05', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.50, 17.50, 4.784, 1665, 1, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2016383670233714689, 2016383670233714688, 3, 'MF40FD-E-2.5-200-15-AB-O', 1, 1, 'GSFVA30020', '23', NULL, NULL, 1, 5, '2026-01-28 13:32:40', '2026-01-28 13:33:18', '2026-01-29 16:47:05', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 14.60, 15.80, 7.785, 1672, 0, NULL, NULL, 6);
INSERT INTO `siargo_product` VALUES (2016392160410980353, 2016392160410980352, 2, 'MF5706-N-20-B-C', 1, 1, 'PEDVB20527', NULL, '实标', NULL, 1, 5, '2026-01-28 14:05:28', '2026-01-29 15:03:22', '2026-01-30 16:49:26', '2026-01-30 17:18:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016392332301946881, 2016392332301946880, 2, 'MF5706-N-20-B-A', 80, 80, 'PEDVA20446-PEDVA20525', NULL, 'D6芯片', NULL, 1, 5, '2026-01-28 14:06:09', '2026-01-29 17:18:55', '2026-01-30 16:49:40', '2026-01-30 17:18:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016394272826052609, 2016394272826052608, 2, 'MF4712-R4F-300-B-A', 34, 34, 'A1V07215-A1V07248', NULL, NULL, NULL, 1, 5, '2026-01-28 14:13:52', '2026-01-28 17:17:22', '2026-01-29 16:44:33', '2026-01-29 16:52:36', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016394272851218432, 2016394272826052608, 2, 'MF4719-R6F-500-B-A', 18, 18, 'A1V07249-A1V07266', NULL, NULL, NULL, 1, 5, '2026-01-28 14:13:52', '2026-01-28 17:17:22', '2026-01-28 17:18:59', '2026-01-29 16:52:55', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016394272851218433, 2016394272826052608, 2, 'MF4719-R6F-1000-B-A', 12, 12, 'A1V07267-A1V07278', NULL, NULL, NULL, 1, 5, '2026-01-28 14:13:52', '2026-01-28 17:17:22', '2026-01-28 17:18:59', '2026-01-29 16:52:55', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016394410642493441, 2016394410642493440, 2, 'MF4712-R4F-300-B-A', 34, 34, 'A1V07279-A1V07312', NULL, NULL, NULL, 1, 5, '2026-01-28 14:14:24', '2026-01-28 17:17:22', '2026-01-29 16:43:17', '2026-01-29 16:52:36', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016394410650882048, 2016394410642493440, 2, 'MF4719-R6F-500-B-A', 18, 18, 'A1V07313-A1V07330', NULL, NULL, NULL, 1, 5, '2026-01-28 14:14:24', '2026-01-28 17:17:22', '2026-01-28 17:18:59', '2026-01-29 16:52:55', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016394410650882049, 2016394410642493440, 2, 'MF4719-R6F-1000-B-A', 12, 12, 'A1V07331-A1V07342', NULL, NULL, NULL, 1, 5, '2026-01-28 14:14:24', '2026-01-28 17:17:22', '2026-01-28 17:18:59', '2026-01-29 16:52:55', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016400373596409857, 2016400373596409856, 1, 'FS7001E', 500, 50, 'A1V16493-A1V16992', NULL, NULL, NULL, 1, 5, '2026-01-28 14:39:54', '2026-02-04 13:29:28', '2026-02-04 13:38:24', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016400801457360897, 2016400801457360896, 1, 'FS7002-C', 100, 13, 'A2V01593-1692', NULL, NULL, NULL, 1, 5, '2026-01-28 14:39:54', '2026-01-30 15:01:25', '2026-02-02 16:19:52', '2026-02-02 16:24:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016410788690776065, 2016410788690776064, 1, 'FS6122-100F100-100P100-TH1', 10, 10, 'A2V01028-A2V01037', NULL, NULL, NULL, 1, 5, '2026-01-28 15:19:34', '2026-01-30 15:15:35', '2026-01-30 15:15:47', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016427373623103489, 2016427373623103488, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V17894-8193', NULL, '5V供电', '2026-01-28 16:27:32', 0, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016428242154409985, 2016428242154409984, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V17894-8043', NULL, '5V供电', NULL, 1, 5, '2026-01-28 16:28:55', '2026-02-03 16:50:39', '2026-02-03 16:51:04', '2026-02-03 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016428242166992896, 2016428242154409984, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A1V18044-8193', NULL, '5V供电', NULL, 1, 5, '2026-01-28 16:28:55', '2026-02-03 16:50:39', '2026-02-03 16:51:04', '2026-02-03 17:25:04', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681110476279809, 2016681110476279808, 1, 'FS4008-50-O8-V-A', 1, 1, 'A2V01026', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:15', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681302634123265, 2016681302634123264, 1, 'MF4008-30-O8-BV-A', 5, 5, 'A2V01015-A2V01019', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:15', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681495630827521, 2016681495630827520, 1, 'FS4008-50-O8-BV-A-0.5/4.5V', 1, 1, 'A2V01276', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:15', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681697339101185, 2016681697339101184, 2, 'MF5619-N-600-ABD-D-O', 1, 1, 'G6HVB22782', NULL, '英文版', NULL, 1, 5, '2026-01-29 09:15:59', '2026-01-30 14:46:51', '2026-02-02 16:19:52', '2026-02-02 16:24:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681738166456321, 2016681738166456320, 1, 'FS4001E-1000-CV-A', 3, 3, 'A1V19777-A1V19779', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:14', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681738195816448, 2016681738166456320, 1, 'FS4001E-200-CV-A', 5, 5, 'A1V19780-A1V19784', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:14', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681738195816449, 2016681738166456320, 1, 'MF4003-5-O6-BV-A', 5, 5, 'A1V19785-A1V19789', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:14', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016681738195816450, 2016681738166456320, 1, 'MF4008-50-R-BV-A', 2, 2, 'A1V19790-A1V19791', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:14', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016692824882139137, 2016692824882139136, 1, 'PFLOW2001-1000-U-VI2C-A', 15, 15, 'A2V01232-A2V01246', NULL, NULL, NULL, 1, 5, '2026-01-29 10:20:22', '2026-01-30 15:04:14', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016698038276182017, 2016698038276182016, 1, 'FS1015CL-150-ISO-A', 1, 1, 'A2V01748', NULL, NULL, NULL, 1, 5, '2026-01-29 10:21:13', '2026-01-30 15:04:14', '2026-01-30 15:07:16', '2026-01-30 15:19:12', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016699771761053696, 2016699771752665088, 2, '01-0-08-1-25-25-DB9M-24-KM7100', 1, 1, 'B022221', NULL, 'BC-L0025-N2-232-NPT-CM', NULL, 1, 5, '2026-01-29 10:27:48', '2026-01-29 15:03:35', '2026-01-30 15:08:55', '2026-01-30 15:19:12', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016699892947079169, 2016699892947079168, 2, 'MF5708-G-100-B-A', 1, 1, 'PEEVB20528', NULL, NULL, NULL, 1, 5, '2026-01-29 10:28:17', '2026-01-29 15:03:11', '2026-01-30 16:49:26', '2026-01-30 17:18:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016751166174384129, 2016751166174384128, 1, 'FS5001E-500-V-H', 3, 3, 'A2V01020-A2V01022', NULL, NULL, NULL, 1, 5, '2026-01-29 14:15:21', '2026-01-30 15:04:14', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016751372764827649, 2016751372764827648, 1, 'FS5001E-100-EV-A', 5, 5, 'A2V01743-A2V01747', NULL, NULL, NULL, 1, 5, '2026-01-29 14:15:21', '2026-01-30 15:04:14', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016751546580979713, 2016751546580979712, 1, 'FS5001E-6000-EV-A', 4, 4, 'A2V01113-A2V01116', NULL, NULL, NULL, 1, 5, '2026-01-29 14:15:21', '2026-01-30 15:04:14', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016751546580979714, 2016751546580979712, 1, 'FS5001E-15000-EV-A', 10, 10, 'A2V01117-A2V01126', NULL, NULL, NULL, 1, 5, '2026-01-29 14:15:21', '2026-01-30 15:04:14', '2026-02-02 16:22:03', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016753192824328193, 2016753192824328192, 2, 'MF25HD-G4-B-T-LPG', 1, 1, 'GHLVA10711', NULL, 'GCF 286', NULL, 1, 5, '2026-01-29 14:00:05', '2026-01-30 11:23:23', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016753192832716800, 2016753192824328192, 2, 'MF25HD-G2.5-B-B-LPG', 1, 1, 'GHLVA10715', NULL, 'GCF 286、外部供电', NULL, 1, 5, '2026-01-29 14:00:05', '2026-01-30 11:23:24', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016756092166328320, 2016756092157939712, 1, 'FS8001', 40, 13, 'A2V01332-A2V01371', NULL, NULL, NULL, 1, 5, '2026-01-29 14:15:21', '2026-01-30 15:04:14', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016768637795160064, 2016768637790965760, 3, 'MF50FD-E-4.0-240-15-ABD-A', 1, 1, 'GSAVA30022', '22', NULL, NULL, 1, 5, '2026-01-29 15:02:38', '2026-01-29 15:02:46', '2026-01-29 16:47:05', '2026-01-29 16:52:36', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 16.70, 12.60, 4.802, 1676, 1, NULL, NULL, 8);
INSERT INTO `siargo_product` VALUES (2016775537874030593, 2016775537874030592, 1, 'FS6122-0F250-0P0-TH0', 105, 13, 'A1V18575-A1V18656', NULL, NULL, NULL, 1, 5, '2026-01-29 15:28:56', '2026-02-02 16:07:53', '2026-02-02 16:10:32', '2026-02-02 16:24:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016790216470024192, 2016790216411303936, 1, 'FS4001E-30-CV-A', 1, 1, 'A2V02720', NULL, NULL, NULL, 1, 5, '2026-01-29 16:30:57', '2026-01-30 15:14:19', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016790216486801408, 2016790216411303936, 1, 'FS4001E-100-CV-A', 1, 1, 'A2V02721', NULL, NULL, NULL, 1, 5, '2026-01-29 16:30:57', '2026-01-30 15:14:19', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016790358090698752, 2016790358052950016, 1, 'FS4001E-100-CV-A', 10, 10, 'A2V02305-A2V02314', NULL, NULL, NULL, 1, 5, '2026-01-29 16:30:57', '2026-01-30 15:14:19', '2026-02-02 16:21:19', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2016790486826471424, 2016790486809694208, 1, 'FS4001-1000-CV-C', 1, 1, 'A2V01025', NULL, NULL, NULL, 1, 5, '2026-01-29 16:30:57', '2026-02-02 16:07:53', '2026-02-02 16:10:32', '2026-02-02 16:24:16', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017045014830239744, 2017045014826045440, 1, 'MF3000S-100-R-BZZ-A', 6, 6, 'A1V19899-A1V19904', NULL, NULL, NULL, 1, 5, '2026-01-30 09:20:15', '2026-02-03 16:46:07', '2026-02-04 13:37:58', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017055631322501120, 2017055631318306816, 2, 'MFC2000-005-K2-BA-1-A', 1, 1, 'MC2VB11185', NULL, NULL, NULL, 1, 5, '2026-01-30 10:01:52', '2026-01-30 14:45:32', '2026-02-02 16:19:52', '2026-02-02 16:24:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017055846624514049, 2017055846624514048, 2, 'MF4719-R6F-500-BV-A', 2, 2, 'A2V01095-A2V01096', NULL, NULL, NULL, 1, 5, '2026-01-30 10:02:43', '2026-01-30 14:45:49', '2026-02-02 16:19:52', '2026-02-02 16:24:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017055951280787457, 2017055951280787456, 2, 'MF4712-Rc4-200-BV-A', 6, 6, 'A2V01038-A2V01043', NULL, NULL, NULL, 1, 5, '2026-01-30 10:03:08', '2026-01-30 14:46:06', '2026-02-02 16:19:52', '2026-02-02 16:24:16', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017056367305412608, 2017056367301218304, 2, 'MF4708-N3-50-AB-A', 2, 2, 'A2V01111-A2V01112', NULL, NULL, NULL, 1, 5, '2026-01-30 10:04:47', '2026-01-30 14:46:28', '2026-02-02 16:19:52', '2026-02-02 16:24:06', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017056654443270145, 2017056654443270144, 2, 'MF4708-R3-50-AB-A', 6, 6, 'A2V01105-A2V01110', NULL, NULL, NULL, 1, 5, '2026-01-30 10:05:56', '2026-01-30 14:45:17', '2026-02-02 16:19:52', '2026-02-02 16:24:06', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017057291331555328, 2017057291323166720, 2, 'MF4719-N6F-1000-AB-O', 1, 1, 'A2V01094', NULL, '英文版', NULL, 1, 5, '2026-01-30 10:08:28', '2026-01-30 14:46:51', '2026-02-02 16:19:52', '2026-02-02 16:24:06', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017074449268723712, 2017074449260335104, 1, 'FS4008-50-O8-BV-A', 150, 13, 'A2V02415-2564', NULL, '厂内型号FS4308-50-O8-BV-A', NULL, 1, 5, '2026-01-30 11:23:45', '2026-02-03 16:46:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017074449272918016, 2017074449260335104, 1, 'FS4008-50-O8-BV-A', 100, 13, 'A2V02565-2664', NULL, '厂内型号FS4308-50-O8-BV-A', NULL, 1, 5, '2026-01-30 11:23:45', '2026-02-03 16:46:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017106635573284864, 2017106635535536128, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A2V01849-A2V01898', NULL, NULL, NULL, 1, 5, '2026-01-30 14:11:40', '2026-03-04 11:39:16', '2026-03-06 13:40:33', '2026-03-06 15:46:15', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017118479100792832, 2017118479096598528, 1, 'FS4001E-60-CV-A', 4, 4, 'A2V02938-A2V02941', NULL, NULL, NULL, 1, 5, '2026-01-30 14:11:40', '2026-02-02 16:07:07', '2026-02-02 16:21:31', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017159566297255936, 2017159566288867328, 1, 'FS4003-2-O4-CV-A', 30, 13, 'A1V19803-9832', NULL, NULL, NULL, 1, 5, '2026-01-30 17:06:20', '2026-02-02 16:07:07', '2026-02-02 16:22:39', '2026-02-02 16:24:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017166448487419904, 2017166448483225600, 3, 'MF2025Be-AB-O', 1, 1, 'GLBVB10629', '19', NULL, NULL, 1, 5, '2026-02-02 09:03:07', '2026-02-02 09:03:32', '2026-02-02 16:17:45', '2026-02-02 16:24:16', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017167007906910208, 2017167007902715904, 3, 'MF40FD-E-2.5-250-15-AB-O', 1, 1, 'GSFVB30061', '17', NULL, NULL, 1, 5, '2026-02-02 09:03:07', '2026-02-02 09:03:32', '2026-02-02 16:17:45', '2026-02-02 16:24:16', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 14.20, 15.10, 4.804, 1666, 1, NULL, NULL, 3);
INSERT INTO `siargo_product` VALUES (2017167480248455169, 2017167480248455168, 3, 'MF80GD160', 1, 1, 'CSCVB31004', '16', NULL, NULL, 1, 5, '2026-02-02 09:03:07', '2026-02-02 09:03:32', '2026-02-03 16:50:13', '2026-02-03 17:25:10', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.60, NULL, NULL, NULL, 1517, 1, 2.89, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017167899494305792, 2017167899490111488, 3, 'MF80GD160', 1, 1, 'CSCVB31005', '16', NULL, NULL, 1, 5, '2026-02-02 09:03:07', '2026-02-02 09:03:32', '2026-02-03 16:50:13', '2026-02-03 17:25:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.20, NULL, NULL, NULL, 1513, 1, 2.91, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017168092021248000, 2017168092017053696, 3, 'MF80GD160', 1, 1, 'CSCVB31006', '16', NULL, NULL, 1, 5, '2026-02-02 09:03:07', '2026-02-02 09:03:32', '2026-02-03 16:50:13', '2026-02-03 17:25:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 13.70, NULL, NULL, NULL, 1631, 3, 2.89, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017767717325361152, 2017767717321166848, 1, 'FS4308-10-O6-BV-A-0.5/4.5V', 100, 13, 'A1V19677-A1V19776', NULL, NULL, NULL, 1, 5, '2026-02-02 10:27:27', '2026-02-02 16:07:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2017780438737473536, 2017780438733279232, 1, 'FS4308-50-R/O8-BV-A', 130, 13, 'A1V17596-A1V17724', NULL, NULL, NULL, 1, 5, '2026-02-02 10:27:27', '2026-02-03 16:46:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018149194118582273, 2018149194118582272, 1, 'FS7002-B', 50, 13, 'A2V01693-1742', NULL, NULL, NULL, 1, 5, '2026-02-02 10:27:27', '2026-02-03 16:46:07', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018155580722368513, 2018155580722368512, 2, 'MF5019-N-E-0.48-48-15-AB-D-N', 1, 1, 'G5HVB11756', NULL, NULL, NULL, 1, 5, '2026-02-02 10:52:40', '2026-02-03 11:22:25', '2026-02-03 16:59:43', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018155845726883841, 2018155845726883840, 2, 'MF5612-N-18m3/h-ABD-D-O', 2, 2, 'G6GVB22840-G6GVB22841', NULL, NULL, NULL, 1, 5, '2026-02-02 10:53:43', '2026-02-02 15:38:56', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018155995216072704, 2018155995199295488, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GUK41829', NULL, NULL, NULL, 1, 5, '2026-02-02 10:54:19', '2026-02-02 15:38:21', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018156277408845824, 2018156277400457216, 2, 'MF5212-E-Q-300-D', 2, 2, 'G7GTG24864/G7GTG24874', NULL, NULL, NULL, 1, 5, '2026-02-02 10:55:26', '2026-02-02 15:38:40', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018156734147579904, 2018156734139191296, 2, 'MF5619-R6F-800-AB-D-O', 4, 4, 'G6HVB22842-G6HVB22845', NULL, NULL, NULL, 1, 5, '2026-02-02 10:57:15', '2026-02-02 15:39:13', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018157061190045697, 2018157061190045696, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HVB22799', NULL, NULL, NULL, 1, 5, '2026-02-02 10:58:33', '2026-02-02 15:39:27', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018157591412985856, 2018157591371042816, 2, 'MF5219-E-Y-1000-C', 6, 6, 'G7HVA44175-G7HVA44180', NULL, NULL, NULL, 1, 5, '2026-02-02 11:00:40', '2026-02-02 15:38:09', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018159948897046529, 2018159948897046528, 2, 'MF5612-N-200-ABD-N-A', 8, 8, 'G6GVB22791-G6GVB22798', NULL, NULL, NULL, 1, 5, '2026-02-02 11:10:02', '2026-02-02 15:39:41', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018160214895611904, 2018160214887223296, 2, 'MF5212-Q-300-A', 1, 1, 'G7GSA82047', NULL, NULL, NULL, 1, 5, '2026-02-02 11:11:05', '2026-02-02 15:37:46', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018201073062105088, 2018201073015967744, 1, 'FS4001E-30-CV-A', 1, 1, 'A2V02975', NULL, NULL, NULL, 1, 5, '2026-02-02 15:04:29', '2026-02-02 16:07:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018201541448421377, 2018201541448421376, 1, 'FS1015E-150-ISO-EV-A', 3, 3, 'A2V02977-A2V02979', NULL, NULL, NULL, 1, 5, '2026-02-02 15:04:29', '2026-02-02 16:07:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018204829015199745, 2018204829015199744, 2, 'MF5708-G-80-B-C', 1, 1, 'PAEVB20529', NULL, '空气标定，GCF 670', NULL, 1, 5, '2026-02-02 14:08:22', '2026-02-03 09:02:27', '2026-02-03 16:57:53', '2026-02-03 17:25:04', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018218863043137536, 2018218863009583104, 1, 'MF4003-2-O6-CV-A-0.5/4.5V', 4, 4, 'A1V15859-A1V15862', NULL, NULL, NULL, 1, 5, '2026-02-02 15:04:29', '2026-02-02 16:07:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018243668551258112, 2018243668542869504, 1, 'MF3000M-500-R-BAN-A', 9, 9, 'A2V01267-A2V01275', NULL, NULL, NULL, 1, 5, '2026-02-02 16:43:05', '2026-02-03 16:46:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018499134434955265, 2018499134434955264, 1, 'FS4308-20-O8-EV-A', 30, 13, 'A2V01302-1331', NULL, NULL, NULL, 1, 5, '2026-02-03 09:53:24', '2026-02-03 16:46:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018503027369824257, 2018503027369824256, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 13, 'A2V01899-A2V01948', NULL, 'CO2测系数980', NULL, 1, 5, '2026-03-06 13:36:52', '2026-03-06 13:37:49', '2026-03-06 14:28:54', '2026-03-06 15:46:15', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018523157940260864, 2018523157931872256, 2, 'MFC2000-050-N3F-BA-1-A', 2, 2, 'MC2VA11167-MC2VA11168', NULL, NULL, NULL, 1, 5, '2026-02-03 11:13:17', '2026-02-03 16:36:47', '2026-02-04 13:36:22', '2026-02-04 13:58:14', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018523371145121792, 2018523371136733184, 2, 'MFC2000-010-N2F-B-0-A', 6, 6, 'MC2VB11194-MC2VB11199', NULL, NULL, NULL, 1, 5, '2026-02-03 11:14:08', '2026-02-03 16:37:02', '2026-02-04 13:36:22', '2026-02-04 13:58:14', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018523480440295424, 2018523480423518208, 2, 'MF4708-N3-50-BV-A', 1, 1, 'A2V02304', NULL, NULL, NULL, 1, 5, '2026-02-03 11:14:34', '2026-02-03 16:35:59', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018523728403353600, 2018523728399159296, 2, 'MF4603-R2-5-BV-C', 1, 1, 'A2V02907', NULL, '实标', NULL, 1, 5, '2026-02-03 11:15:33', '2026-02-03 16:35:39', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018523880685948928, 2018523880677560320, 2, 'MF4701-R1-200-BV-E', 1, 1, 'A2V02982', NULL, '实标', NULL, 1, 5, '2026-02-03 11:16:10', '2026-02-03 16:36:25', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018524130226065408, 2018524130221871104, 2, 'MF4719-Rc6-1000-B-N', 1, 1, 'A2V01999', NULL, '精度：1.5+0.5FS，小标签', NULL, 1, 5, '2026-02-03 11:17:09', '2026-02-03 16:35:01', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018524130234454016, 2018524130221871104, 2, 'MF4712-R4-300-B-N', 3, 3, 'A2V02000-A2V02002', NULL, '精度：1.5+0.5FS，小标签', NULL, 1, 5, '2026-02-03 11:17:09', '2026-02-03 16:35:01', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018524441154015233, 2018524441154015232, 2, 'MF4712-R4M-150-AB-N', 14, 14, 'A2V02728-A2V02741', NULL, NULL, NULL, 1, 5, '2026-02-03 11:18:23', '2026-02-03 16:34:45', '2026-02-04 13:35:29', '2026-02-04 13:58:25', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018524469071302657, 2018524469071302656, 1, 'PFLOW2001-L-30-B-VI2C-A-10X', 300, 300, 'A1V18677-A1V18976', NULL, NULL, NULL, 1, 5, '2026-02-03 11:18:34', '2026-02-05 11:22:58', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018525436646903808, 2018525436621737984, 1, 'FS1015E-70-ISO-V-C', 5, 5, 'A2V02928-A2V02932', NULL, '输出0.25-2.75V', NULL, 1, 5, '2026-02-03 11:25:59', '2026-02-03 16:46:07', '2026-02-03 16:49:11', '2026-02-03 17:25:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018553615163969536, 2018553615159775232, 1, 'FS4308-10-O8-BV-A', 6, 6, 'A2V02722-A2V02727', NULL, NULL, NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-03 16:46:07', '2026-02-04 13:35:29', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018553927513788416, 2018553927509594112, 1, 'MF4008-10-O8-BV-O', 1, 1, 'A2V02746', NULL, NULL, NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-03 16:46:07', '2026-02-04 13:35:29', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018554063308574721, 2018554063308574720, 1, 'MF4008-50-R-BV-A', 10, 10, 'A2V02747-A2V02756', NULL, NULL, NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-03 16:46:07', '2026-02-04 13:35:29', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018554452057640960, 2018554452049252352, 1, 'FS4001E-30-CV-A', 2, 2, 'A2V03020-A2V03021', NULL, '甲烷系数1030', NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-03 16:46:07', '2026-02-04 13:35:29', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018554452061835264, 2018554452049252352, 1, 'FS4001E-500-CV-A', 2, 2, 'A2V03022-A2V03023', NULL, '甲烷系数1030', NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-03 16:46:07', '2026-02-04 13:35:29', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018575883940253696, 2018575883936059392, 1, 'FS1100-250F250', 1, 1, 'A2V02944', NULL, NULL, NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-03 16:46:07', '2026-02-04 13:36:22', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018584082495950848, 2018584082487562240, 1, 'MF3000M-10-O4-BNN-A', 50, 26, 'A1V17771-7820', NULL, NULL, NULL, 1, 5, '2026-02-03 15:32:07', '2026-02-04 13:29:28', '2026-02-04 13:38:24', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018595958860664832, 2018595958856470528, 1, 'MF3000M-1000-R-BAN-A', 1, 1, 'A2V02303', NULL, NULL, NULL, 1, 5, '2026-02-04 09:29:48', '2026-02-04 13:29:28', '2026-02-04 13:38:24', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018609272265232384, 2018609272261038080, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 2, 2, 'A2V02980-A2V02981', NULL, NULL, NULL, 1, 5, '2026-02-04 09:29:48', '2026-02-04 13:29:28', '2026-02-04 13:38:24', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018609609118175232, 2018609609105592320, 3, 'MF2025Be-AB-O', 1, 1, 'GLBVB10630', '19', NULL, NULL, 1, 5, '2026-02-03 16:57:18', '2026-02-03 16:57:33', '2026-02-03 17:01:24', '2026-02-03 17:25:04', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018859151625408512, 2018859151621214208, 1, 'FS6122-0F200-0P0-TH0', 4, 4, 'A2V03024-A2V03027', NULL, NULL, NULL, 1, 5, '2026-02-04 09:29:48', '2026-02-04 13:29:28', '2026-02-04 13:38:24', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018859477350862848, 2018859477346668544, 1, 'FS6122-0F250-100P100-TH1', 8, 8, 'A2V02983-A2V02990', NULL, NULL, NULL, 1, 5, '2026-02-04 09:29:48', '2026-02-04 13:29:28', '2026-02-04 13:38:24', '2026-02-04 13:58:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018872081876111360, 2018872081867722752, 2, 'MF5212-E-Q-400-D', 200, 200, 'G7GVB44200-G7GVB44399', NULL, NULL, NULL, 1, 5, '2026-02-04 10:19:47', '2026-02-05 16:51:35', '2026-02-05 17:04:24', '2026-02-06 11:37:05', 2001546811778514944, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933067094020097, 2018933067094020096, 2, 'MF4703-R1-1-BV-A', 5, 5, 'A2V02908-A2V02912', NULL, NULL, NULL, 1, 5, '2026-02-04 14:22:07', '2026-02-05 11:21:12', '2026-02-05 16:51:11', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933067094020098, 2018933067094020096, 2, 'MF4708-R3-10-BV-A', 5, 5, 'A2V02913-A2V02917', NULL, NULL, NULL, 1, 5, '2026-02-04 14:22:07', '2026-02-05 11:21:12', '2026-02-05 16:51:11', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933067094020099, 2018933067094020096, 2, 'MF4708-R3-20-BV-A', 5, 5, 'A2V02918-A2V02922', NULL, NULL, NULL, 1, 5, '2026-02-04 14:22:07', '2026-02-05 11:21:12', '2026-02-05 16:51:11', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933067094020100, 2018933067094020096, 2, 'MF4712-Rc4-200-BV-A', 5, 5, 'A2V02923-A2V02927', NULL, NULL, NULL, 1, 5, '2026-02-04 14:22:07', '2026-02-05 11:21:12', '2026-02-05 16:51:11', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933067094020101, 2018933067094020096, 2, 'MF4708-R3-50-BV-A', 5, 5, 'A2V02933-A2V02937', NULL, NULL, NULL, 1, 5, '2026-02-04 14:22:07', '2026-02-05 11:21:12', '2026-02-05 16:51:11', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933274682707968, 2018933274674319360, 2, 'MF4719-R6F-1000-B-N', 4, 4, 'A2V03013-A2V03016', NULL, 'MF4519', NULL, 1, 5, '2026-02-04 14:22:57', '2026-02-05 11:20:57', '2026-02-05 16:51:11', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018933385575911424, 2018933385571717120, 2, 'MF4719-N6F-500-AB-A', 1, 1, 'A2V03010', NULL, NULL, NULL, 1, 5, '2026-02-04 14:23:23', '2026-02-05 11:20:47', '2026-02-05 16:55:46', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018934343399755776, 2018934343395561472, 2, 'MF4703-R1-5-AB-A', 1, 1, 'A2V03017', NULL, NULL, NULL, 1, 5, '2026-02-04 14:27:12', '2026-02-05 11:20:31', '2026-02-05 16:55:46', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018937358269665280, 2018937358261276672, 1, 'PFLOW2001-L-2000-U-VI2C-A', 200, 50, 'A1V18367-A1V18559', NULL, NULL, NULL, 1, 5, '2026-02-04 16:52:44', '2026-02-05 11:22:58', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018938868244271105, 2018938868244271104, 1, 'FS1015E-150-ISO-EV-A', 1, 1, 'A2V04279', NULL, NULL, NULL, 1, 5, '2026-02-04 16:52:44', '2026-02-05 11:22:58', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018958620769505280, 2018958620761116672, 1, 'FS4003-4-R-CV-A', 105, 13, 'A2V01127-A2V01231', NULL, NULL, NULL, 1, 5, '2026-02-04 16:52:44', '2026-02-05 16:52:16', '2026-02-06 15:25:14', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018959013893230592, 2018959013884841984, 1, 'FS34103-2-O4-V-A', 25, 13, 'A2V01277-A2V01301', NULL, 'FS34308-2-O4-V-A-0.5/4.5V', NULL, 1, 5, '2026-02-04 16:52:44', '2026-02-05 11:22:58', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018960880576614401, 2018960880576614400, 2, 'MF5706-G-20-B-A', 100, 100, 'PEDVB20530-PEDVB20629', NULL, 'D9芯片', NULL, 1, 5, '2026-02-04 16:12:39', '2026-02-05 14:53:13', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018968948198723585, 2018968948198723584, 1, 'MF3000S-200-O8-EVN-A', 20, 13, 'A2V01247-A2V01266', NULL, NULL, NULL, 1, 5, '2026-02-04 16:52:44', '2026-02-05 11:22:58', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933820424192, 2018975933816229888, 2, 'MF6600-1SLPM-C', 8, 8, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:12:54', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933824618496, 2018975933816229888, 2, 'MF6600-1SLPM', 23, 13, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:13:07', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933828812800, 2018975933816229888, 2, 'MF6600-2SLPM-C', 10, 10, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:13:17', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933828812801, 2018975933816229888, 2, 'MF6600-2SLPM', 38, 13, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:13:28', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933833007104, 2018975933816229888, 2, 'MF6600-5SLPM-C', 12, 12, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:13:37', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933837201408, 2018975933816229888, 2, 'MF6600-5SLPM', 34, 13, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:13:46', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933862367232, 2018975933816229888, 2, 'MF6600-10SLPM-C', 12, 12, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:00', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933866561536, 2018975933816229888, 2, 'MF6600-10SLPM', 38, 13, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:07', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933866561537, 2018975933816229888, 2, 'MF6600-20SLPM-C', 12, 12, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:16', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933866561538, 2018975933816229888, 2, 'MF6600-20SLPM', 12, 12, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:29', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933870755840, 2018975933816229888, 2, 'MF6600-40SLPM-C', 10, 10, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:36', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933870755841, 2018975933816229888, 2, 'MF6600-50SLPM', 33, 13, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:46', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:24', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2018975933874950144, 2018975933816229888, 2, 'MF6600-70SLPM', 45, 13, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-04 17:14:58', '2026-02-06 15:20:19', '2026-02-06 15:23:09', '2026-02-06 17:06:24', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019226500366979072, 2019226500362784768, 1, 'FS4008-50-O8-BV-A', 30, 30, 'A2V02945-A2V02974', NULL, NULL, NULL, 1, 5, '2026-02-05 10:16:31', '2026-02-05 11:22:58', '2026-02-05 16:49:35', '2026-02-05 17:02:37', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019227193811259392, 2019227193807065088, 2, 'MF5006-N2M-e-0.03-1.2-15-AB-D-A', 1, 1, 'G5DVB11757', NULL, NULL, NULL, 1, 5, '2026-02-05 09:50:53', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019227193819648000, 2019227193807065088, 2, 'MF5012-N4M-e-0.18-6-15-AB-D-A', 1, 1, 'G5GVB11758', NULL, NULL, NULL, 1, 5, '2026-02-05 09:50:53', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019227511420735488, 2019227511416541184, 2, 'MF5619-N-800-AB-D-O', 1, 1, 'G6HRD66368', NULL, NULL, NULL, 1, 5, '2026-02-05 09:52:08', '2026-02-05 14:55:13', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019228345382588416, 2019228345374199808, 2, 'MF5619-N-800-AB-D-A', 2, 2, 'G6HVB22856-G6HVB22857', NULL, NULL, NULL, 1, 5, '2026-02-05 09:55:27', '2026-02-05 14:55:40', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019228501041598464, 2019228501033209856, 2, 'MF5619-N-800-ABD-D-O', 2, 2, 'G6HVB22849-G6HVB22850', NULL, NULL, NULL, 1, 5, '2026-02-05 09:56:04', '2026-02-05 14:53:27', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019228645803806720, 2019228645799612416, 2, 'MF5619-N-48m3/h-ABD-D-O', 1, 1, 'G6HVB22855', NULL, '英文', NULL, 1, 5, '2026-02-05 09:56:39', '2026-02-05 14:55:00', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019228944828321792, 2019228944824127488, 2, 'MF5612-N-200-AB-N-A', 1, 1, 'G6GTJ19021', NULL, NULL, NULL, 1, 5, '2026-02-05 09:57:50', '2026-02-05 14:54:20', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019229059714502656, 2019229059706114048, 2, 'MF5612-N-200-B-D-A', 1, 1, 'G6GVB22847', NULL, NULL, NULL, 1, 5, '2026-02-05 09:58:17', '2026-02-05 14:54:46', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019229225305624576, 2019229225297235968, 2, 'MF5612-N-300-AB-D-A', 1, 1, 'G6GVB22853', NULL, '英文', NULL, 1, 5, '2026-02-05 09:58:57', '2026-02-05 14:54:07', '2026-02-13 16:07:03', '2026-02-13 17:12:50', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019229515878617088, 2019229515874422784, 2, 'MF5612-N-300-AB-D-O', 2, 2, 'G6GVB22851-G6GVB22852', NULL, NULL, NULL, 1, 5, '2026-02-05 10:00:06', '2026-02-05 14:54:34', '2026-02-06 15:26:45', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019230149956718592, 2019230149952524288, 1, 'FS8001', 150, 13, 'A2V02757-A2V02906', NULL, NULL, NULL, 1, 5, '2026-02-05 10:16:31', '2026-02-09 16:23:21', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019233606755274753, 2019233606755274752, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A2V01749-A2V01798', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019237654204108800, 2019237654195720192, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 13, 'A2V01799-A2V01848', NULL, 'CO2测系数980', NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019251808507908096, 2019251808503713792, 1, 'PNEU-053898', 10, 10, 'A2V03000-A2V03009', NULL, 'FS35001-F250-V-A，滤波深度3', NULL, 1, 5, '2026-02-05 16:55:08', '2026-02-06 15:17:48', '2026-02-06 15:25:13', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019298750411689984, 2019298750403301376, 2, 'FS4708-N3-50-BV-O', 300, 300, 'A2V02003-A2V02302', NULL, NULL, NULL, 1, 5, '2026-02-05 14:35:13', '2026-02-06 15:08:39', '2026-02-09 16:44:34', '2026-02-09 16:57:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019302487167455232, 2019302487163260928, 2, 'MF6600-20SLPM', 200, 50, 'FAGVB17597-FAGVB17796', NULL, NULL, NULL, 1, 5, '2026-02-05 16:55:08', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019320687959592960, 2019320687955398656, 3, 'MF80GD100', 1, 1, 'CSCVB31007', '2', NULL, NULL, 1, 5, '2026-02-05 16:04:04', '2026-02-09 16:24:42', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 11.60, NULL, NULL, NULL, 1503, 1, 2.89, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019321011894079489, 2019321011894079488, 3, 'MF80GD100', 1, 1, 'CSCVB31008', '2', NULL, NULL, 1, 5, '2026-02-05 16:04:04', '2026-02-09 16:24:42', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 11.70, NULL, NULL, NULL, 1494, 1, 2.88, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019326491005669376, 2019326490997280768, 1, 'MF4008-20-R-CV-A', 2, 2, 'A2V02942-A2V02943', NULL, NULL, NULL, 1, 5, '2026-02-05 16:55:08', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019326889363886080, 2019326889355497472, 1, 'MF4003-5-O6-BV-N', 2, 2, 'A2V03018-A2V03019', NULL, NULL, NULL, 1, 5, '2026-02-05 16:55:08', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019327098642878464, 2019327098634489856, 1, 'MF4003-5-O8-BV-A', 1, 1, 'A2V02976', NULL, NULL, NULL, 1, 5, '2026-02-05 16:55:08', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019327381380911104, 2019327381376716800, 1, 'FS4003-5-O6-BV-N', 9, 9, 'A2V02991-A2V02999', NULL, NULL, NULL, 1, 5, '2026-02-05 16:55:08', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019578714553962496, 2019578714549768192, 2, 'MF5219-E-Q-800-C', 41, 41, 'G7HVB44403-G7HVB44443', NULL, NULL, NULL, 1, 5, '2026-02-06 09:07:42', '2026-02-06 15:08:50', '2026-02-09 16:46:56', '2026-02-09 16:57:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019588107018293248, 2019588107014098944, 1, 'MF3000S-1500-R-BVZ-A', 1, 1, 'A2V04731', NULL, NULL, NULL, 1, 5, '2026-02-06 10:42:51', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019588107047653376, 2019588107014098944, 1, 'MF3000S-500-R-BVZ-A', 1, 1, 'A2V04732', NULL, NULL, NULL, 1, 5, '2026-02-06 10:42:51', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019588211632623616, 2019588211628429312, 1, 'MF3000S-1500-R-BVZ-A', 3, 3, 'A2V04240-A2V04242', NULL, NULL, NULL, 1, 5, '2026-02-06 10:42:51', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019590326723661824, 2019590326719467520, 2, 'MF5212-Q-300-N', 1, 1, 'G7GVA22715', NULL, NULL, NULL, 1, 5, '2026-02-06 09:53:50', '2026-02-06 10:05:39', '2026-02-06 15:29:11', '2026-02-06 17:06:10', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019602626633388032, 2019602626629193728, 1, 'FS4308-50-O8-EV-C', 2, 2, 'A2V03011-A2V03012', NULL, NULL, NULL, 1, 5, '2026-02-06 10:42:51', '2026-02-06 15:17:48', '2026-02-06 15:24:59', '2026-02-06 17:06:10', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019605707798466560, 2019605707794272256, 2, 'MF5619-N-600-ABD-D-C', 1, 1, 'G6HSJ98303', NULL, 'GCF 670', NULL, 1, 5, '2026-02-06 10:54:57', '2026-02-06 16:17:56', '2026-02-09 16:46:56', '2026-02-09 16:57:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019605707802660864, 2019605707794272256, 2, 'MF5219-Q-800-A', 1, 1, 'G7HSE89415', NULL, '', NULL, 1, 5, '2026-02-06 10:54:57', '2026-02-06 16:17:56', '2026-02-09 16:46:56', '2026-02-09 16:57:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019612650541797376, 2019612650537603072, 2, 'MF5612-N-18m3/h-ABD-D-O', 1, 1, 'G6GVB22854', NULL, NULL, NULL, 1, 5, '2026-02-06 11:22:33', '2026-02-06 16:16:13', '2026-02-09 16:46:56', '2026-02-09 16:57:22', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019648079144013824, 2019648079139819520, 1, 'PFLOW4008SW-50-RO8-IOV-A', 50, 50, 'A2V01044-A2V01093', NULL, NULL, NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:38', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019667186786619392, 2019667186782425088, 1, 'FS5001E-200-EV-H', 2, 2, 'A2V04809-A2V04810', NULL, NULL, NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019667186790813696, 2019667186782425088, 1, 'FS5001E-1000-EV-A', 2, 2, 'A2V04811-A2V04812', NULL, NULL, NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019674793530216448, 2019674793526022144, 2, 'MFC2000-005-N2F-BA-1-A', 40, 40, 'MC2VB11200-MC2VB11239', NULL, '流量滤波深度5', NULL, 1, 5, '2026-02-06 15:29:29', '2026-02-09 16:24:09', '2026-02-09 16:36:15', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019674793534410752, 2019674793526022144, 2, 'MFC2000-010-N3F-BV-1-A', 40, 40, 'MC2VB11240-MC2VB11279', NULL, '流量滤波深度5', NULL, 1, 5, '2026-02-06 15:29:29', '2026-02-09 16:24:09', '2026-02-09 16:36:15', '2026-02-09 16:42:18', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019676426355986432, 2019676426351792128, 2, 'MF4712-N4F-200-BV-O', 1, 1, 'A2V03028', NULL, NULL, NULL, 1, 5, '2026-02-06 15:35:58', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019676600339910656, 2019676600335716352, 2, 'MF4719-Rc6-350-B-A', 1, 1, 'A2V03029', NULL, '1.5+0.5FS', NULL, 1, 5, '2026-02-06 15:36:39', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019680685692669952, 2019680685688475648, 1, 'FS4008-20-O8-CV-O', 1, 1, 'A2V04261', NULL, '', NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019680685696864256, 2019680685688475648, 1, 'FS4008-20-O8-CV-B', 3, 3, 'A2V04262-A2V04264', NULL, 'CO2测系数1050', NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019683705121787904, 2019683705109204992, 1, 'FS4001E-30-CV-A', 3, 3, 'A2V04806-A2V04808', NULL, NULL, NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019683803675348992, 2019683803671154688, 1, 'FS4001E-30-CV-A', 4, 4, 'A2V04265-A2V04268', NULL, NULL, NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019683931341574144, 2019683931337379840, 1, 'FS4001E-100-CV-A', 4, 4, 'A2V04861-A2V04864', NULL, NULL, NULL, 1, 5, '2026-02-06 16:07:59', '2026-02-07 17:06:53', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019688585400012800, 2019688585391624192, 2, 'BC-L0025-O2-485-NPT', 1, 1, 'B018506', NULL, NULL, NULL, 1, 5, '2026-02-06 16:24:17', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019688585400012801, 2019688585391624192, 2, '01-1-11-1-20', 1, 1, 'B019666', NULL, 'BC-L0020-O2-485-NPT', NULL, 1, 5, '2026-02-06 16:24:17', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019689451343433728, 2019689451326656512, 2, 'MF5712-G-250-B-A', 2, 2, 'PEGVB20630-PEGVB20631', NULL, NULL, NULL, 1, 5, '2026-02-06 16:27:43', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019689847411560448, 2019689847407366144, 2, 'MF4708-R3-50-AB-A-ZD', 1, 1, 'A1V16174', NULL, '-ZD为客户型号', NULL, 1, 5, '2026-02-06 16:29:18', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019689847415754752, 2019689847407366144, 2, 'MF4712-R4F-200-AB-N-ZD', 1, 1, 'A1V16175', NULL, '-ZD为客户型号', NULL, 1, 5, '2026-02-06 16:29:18', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2019692067465056256, 2019692067460861952, 3, 'MF25FD-E-1-100-15-AB-O', 1, 1, 'GSBVB30043', '2', NULL, NULL, 1, 5, '2026-02-06 16:41:56', '2026-02-06 16:42:08', '2026-02-09 16:46:56', '2026-02-09 16:57:22', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.50, 16.40, 4.787, 1652, 1, NULL, NULL, 11);
INSERT INTO `siargo_product` VALUES (2019692615778029569, 2019692615778029568, 3, 'MF32FD-E-1.6-160-15-ABD-A', 1, 1, 'GSEVB30081', '16', NULL, NULL, 1, 5, '2026-02-06 16:40:30', '2026-02-06 16:41:08', '2026-02-09 16:46:56', '2026-02-09 16:57:22', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.70, 16.10, 4.042, 1647, 1, NULL, NULL, 21);
INSERT INTO `siargo_product` VALUES (2019701257231257600, 2019701257227063296, 1, 'MF4008-30-R-BV-A-0.5/4.5V', 50, 13, 'A2V01976', NULL, NULL, NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-09 16:24:04', '2026-02-09 16:28:19', '2026-02-09 16:42:18', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020038716762738688, 2020038716758544384, 1, 'FS6122-200F200-40P40-TH0', 1, 1, 'A2V04813', NULL, NULL, NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-09 16:29:12', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020038905577721857, 2020038905577721856, 1, 'FS6122-250F250-5P100-TH1', 500, 80, 'A2V03030-A2V03529', NULL, NULL, '2026-02-26 15:43:57', 0, 1, '2026-02-09 08:51:37', '2026-02-12 15:57:31', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020050878134603776, 2020050878130409472, 1, 'FS4008-20-O8-CV-A', 100, 13, 'A2V04329-A2V04428', NULL, '标准表37度', NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-10 15:36:49', '2026-02-10 15:37:14', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020050878138798080, 2020050878130409472, 1, 'FS4008-20-O8-CV-A', 100, 13, 'A2V04429-A2V04528', NULL, '标准表37度', NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-10 15:36:49', '2026-02-10 15:37:14', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020050878138798081, 2020050878130409472, 1, 'FS4008-20-O8-CV-B', 100, 13, 'A2V04531-A2V04621', NULL, 'CO2测系数1050', NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-10 15:36:49', '2026-02-10 15:37:14', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020416476882522112, 2020416476878327808, 1, 'FS4308-40-O6-EV-A-0.5/4.5V', 100, 13, 'A2V01373-A2V01472', NULL, NULL, NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-10 15:36:31', '2026-02-10 15:37:14', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020416476890910720, 2020416476878327808, 1, 'FS4308-40-O6-EV-A-0.5/4.5V', 100, 13, 'A2V01473-A2V01572', NULL, NULL, NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-10 15:36:31', '2026-02-10 15:37:14', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020416476899299328, 2020416476878327808, 1, 'FS4308-20-O8-CV-A-0.5/4.5V', 20, 13, 'A2V01573-A2V01591', NULL, NULL, NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-10 15:36:31', '2026-02-10 15:37:14', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020661717304725504, 2020661717296336896, 1, 'FS6122-250F250-5P100-TH1', 500, 80, 'A2V03030-A2V03529', NULL, NULL, NULL, 1, 5, '2026-02-09 08:51:37', '2026-02-11 11:10:46', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020674795266232320, 2020674795262038016, 1, 'FS1015E-150-ISO-EV-A', 2, 2, 'A2V04744-A2V04745', NULL, NULL, NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-09 16:20:17', '2026-02-10 15:38:06', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020680966790303744, 2020680966786109440, 1, 'MF4008-50-O8-BV-A', 50, 13, 'A2V02315-A2V02364', NULL, '', NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-09 16:19:20', '2026-02-10 15:38:06', '2026-02-10 17:29:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020680966794498048, 2020680966786109440, 1, 'MF4008-20-R-BV-A', 20, 20, 'A2V02365-A2V02384', NULL, '', NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-09 16:19:20', '2026-02-10 15:38:06', '2026-02-10 17:29:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020680966794498049, 2020680966786109440, 1, 'MF4003-5-R-BV-A', 5, 5, 'A2V02385-A2V02389', NULL, '', NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-09 16:19:20', '2026-02-10 15:38:06', '2026-02-10 17:29:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020680966798692352, 2020680966786109440, 1, 'MF4003-2-R-BV-A', 5, 5, 'A2V02390-A2V02394', NULL, '', NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-09 16:19:20', '2026-02-10 15:38:06', '2026-02-10 17:29:06', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020680966802886656, 2020680966786109440, 1, 'MF4003-5-O6-CV-A', 20, 13, 'A2V02395-A2V02414', NULL, 'MF4003产品', NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-09 16:19:20', '2026-02-10 15:38:06', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020692728612769792, 2020692728608575488, 1, 'FS7001E', 500, 50, 'A2V03740-A2V04239', NULL, NULL, NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-13 15:47:14', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020692903188090880, 2020692903179702272, 1, 'FS7001C', 11, 11, 'A2V04869-A2V04879', NULL, NULL, NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-10 15:33:07', '2026-02-10 15:33:54', '2026-02-10 17:29:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020699103367647232, 2020699103359258624, 1, 'FS4003-2-O6-CV-A', 50, 13, 'A2V02665-A2V02714', NULL, NULL, NULL, 1, 5, '2026-02-09 11:34:07', '2026-02-10 15:33:07', '2026-02-10 15:33:54', '2026-02-10 17:29:17', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020703299613609985, 2020703299613609984, 2, 'MF5219-E-Q-1000-F', 1, 1, 'G7HUC33922', '15', NULL, NULL, 1, 5, '2026-02-09 15:46:53', '2026-02-09 15:48:36', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020764004954001408, 2020764004949807104, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GUK42512', NULL, NULL, NULL, 1, 5, '2026-02-09 15:46:32', '2026-02-09 15:48:46', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020764519259557888, 2020764519255363584, 2, 'MF5612-N-200-AB-D-A', 1, 1, 'G6GQA44371', NULL, NULL, NULL, 1, 5, '2026-02-09 15:45:52', '2026-02-09 15:49:09', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020764924760674305, 2020764924760674304, 2, 'MF5212-E-Y-300-C', 1, 1, 'G7GVB44449', NULL, NULL, NULL, 1, 5, '2026-02-09 15:45:28', '2026-02-09 15:49:24', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020765455923138561, 2020765455923138560, 2, 'MF5012-N4M-e-0-50-15-A-D-A', 1, 1, 'G5GVB11759-11764', NULL, NULL, NULL, 1, 5, '2026-02-09 15:47:54', '2026-02-09 15:48:24', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020768025324408833, 2020768025324408832, 2, 'MF4710-G4-50-AB-N', 1, 1, 'A2V04328', NULL, NULL, NULL, 1, 5, '2026-02-09 15:53:35', '2026-02-09 15:53:50', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020772161436635136, 2020772161432440832, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GTL31056', NULL, NULL, NULL, 1, 5, '2026-02-09 16:10:02', '2026-02-09 16:10:13', '2026-02-09 16:44:34', '2026-02-09 16:57:22', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020773873400532993, 2020773873400532992, 2, 'MF5212-E-Q-400-D', 2, 2, 'G7GUA32458-G7GUH38653', NULL, NULL, NULL, 1, 5, '2026-02-09 16:16:50', '2026-02-09 16:24:09', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020774368873664513, 2020774368873664512, 2, 'MF5612-N-12m3/h-ABD-D-O', 2, 2, 'G6GPE32656-G6GPL43418', NULL, NULL, NULL, 1, 5, '2026-02-09 16:18:48', '2026-02-09 16:24:08', '2026-02-09 16:41:03', '2026-02-09 16:42:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020783209241759744, 2020783209237565440, 2, 'MF5612-N-200-B-D-A', 40, 40, 'G6GVB22800-G6GVB22839', NULL, NULL, NULL, 1, 5, '2026-02-09 16:53:56', '2026-02-09 16:54:46', '2026-02-10 15:39:15', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784109578473472, 2020784109574279168, 1, 'FS4308-2-O6-BV-A-0.5/4.5V', 2, 2, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:33:07', '2026-02-10 15:33:54', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784109582667776, 2020784109574279168, 1, 'FS4308-10-O6-BV-A-0.5/4.5V', 11, 11, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:33:07', '2026-02-10 15:33:54', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784109586862080, 2020784109574279168, 1, 'FS4308-20-O6-BV-A-0.5/4.5V', 5, 5, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:33:07', '2026-02-10 15:33:54', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784278961246208, 2020784278952857600, 1, 'MF4008-50-R-BV-A', 10, 10, 'A2V04269-A2V04278', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:32:31', '2026-02-13 16:10:46', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784528065155072, 2020784528060960768, 1, 'MF4003-2-O6-CV-A', 14, 14, 'A2V04819-A2V04832', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:32:31', '2026-02-10 15:35:45', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784528069349376, 2020784528060960768, 1, 'FS4008-50-O8-BV-A', 6, 6, 'A2V04833-A2V04838', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:32:31', '2026-02-10 15:35:45', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784528073543680, 2020784528060960768, 1, 'FS4001E-200-CV-A', 13, 13, 'A2V04839-A2V04851', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:32:31', '2026-02-10 15:35:45', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2020784528077737984, 2020784528060960768, 1, 'FS4001E-500-CV-A', 3, 3, 'A2V04852-A2V04854', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-10 15:32:31', '2026-02-10 15:35:45', '2026-02-10 17:29:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021055679223943169, 2021055679223943168, 2, 'MF4701-R1-100-BV-A', 5, 5, 'A2V04308-A2V04312', NULL, NULL, NULL, 1, 5, '2026-02-10 10:56:37', '2026-02-10 11:02:57', '2026-02-11 16:38:22', '2026-02-11 16:40:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021055830009171969, 2021055830009171968, 2, 'MF4708-R3-10-BV-A', 5, 5, 'A2V04313-A2V04317', NULL, NULL, NULL, 1, 5, '2026-02-10 10:57:13', '2026-02-10 11:02:57', '2026-02-11 16:38:22', '2026-02-11 16:40:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021055965137063936, 2021055965132869632, 2, 'MF4701-R1-200-BV-A', 5, 5, 'A2V04318-A2V04322', NULL, NULL, NULL, 1, 5, '2026-02-10 10:57:46', '2026-02-10 11:02:57', '2026-02-11 16:38:22', '2026-02-11 16:40:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021056073593376768, 2021056073589182464, 2, 'MF4708-R3-50-BV-A', 5, 5, 'A2V04323-A2V04327', NULL, NULL, NULL, 1, 5, '2026-02-10 10:58:12', '2026-02-10 11:02:57', '2026-02-11 16:38:22', '2026-02-11 16:40:11', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021056297137197057, 2021056297137197056, 2, 'FS4708-N3-50-AB-A', 8, 8, 'A2V04300-A2V04307', NULL, NULL, NULL, 1, 5, '2026-02-10 10:59:05', '2026-02-10 11:02:57', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021056578495303680, 2021056578482720768, 2, 'MF34719-B6F-500-B-A', 2, 2, 'A2V04729-A2V04730', NULL, NULL, NULL, 1, 5, '2026-02-10 11:02:44', '2026-02-10 11:02:57', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021056770586038272, 2021056770577649664, 2, 'FS4708-N3-10-BV-A', 2, 2, 'A2V04855-A2V04856', NULL, NULL, NULL, 1, 5, '2026-02-10 11:00:58', '2026-02-10 11:02:57', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021056923648774145, 2021056923648774144, 2, 'FS4701-N1-500-BV-A', 1, 1, 'A2V04857', NULL, NULL, NULL, 1, 5, '2026-02-10 11:01:34', '2026-02-10 11:02:57', '2026-02-10 15:45:12', '2026-02-10 17:29:06', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021089894955077632, 2021089894950883328, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 50, 'A1V08818-A1V08867', NULL, NULL, NULL, 1, 5, '2026-02-10 13:12:54', '2026-02-11 11:10:46', '2026-02-11 16:35:56', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021099651401109505, 2021099651401109504, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 50, 'A1V08768-A1V08817', NULL, 'CO2测系数980', NULL, 1, 5, '2026-02-10 13:51:30', '2026-02-11 11:10:46', '2026-02-11 16:35:56', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021106185501790208, 2021106185497595904, 1, 'MF3000M-100-R-BVZ-A', 5, 5, 'A2V04814-A2V04818', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-10 15:32:31', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021106313314816000, 2021106313310621696, 1, 'FS4008-20-R-BV-A', 1, 1, 'A2V04858', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-10 15:32:31', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021106692530229248, 2021106692517646336, 1, 'MF3000S-100-L-BVZ-A', 1, 1, 'A2V04733', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-10 15:32:31', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021107031153168384, 2021107031148974080, 1, 'MF3000S-200-R-BAZ-A', 2, 2, 'A2V04746-A2V04747', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-10 15:32:31', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021115565630672896, 2021115565622284288, 1, 'FS4308-50-O8-BV-A', 10, 10, 'A2V04752-A2V04761', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-11 11:10:46', '2026-02-11 16:33:30', '2026-02-11 16:40:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021115565634867200, 2021115565622284288, 1, 'FS4308-2-O4-BV-A', 44, 13, 'A2V04765-A2V04803', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-11 11:10:46', '2026-02-11 16:33:30', '2026-02-11 16:40:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021116150727692288, 2021116150719303680, 1, 'FS5002-A-1000-EV-H', 7, 7, 'A2V04734-A2V04740', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-10 15:32:31', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021116150731886592, 2021116150719303680, 1, 'FS5002-A-1000-EV-E', 3, 3, 'A2V04741-A2V04743', NULL, NULL, NULL, 1, 5, '2026-02-10 15:15:44', '2026-02-10 15:32:31', '2026-02-11 16:35:26', '2026-02-11 16:40:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021122550916829184, 2021122550908440576, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A2V04629-A2V04678', NULL, NULL, NULL, 1, 2, '2026-02-11 16:54:00', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021122973287436288, 2021122973283241984, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 13, 'A2V04679-A2V04728', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021137449713782784, 2021137449709588480, 1, 'FS6122-250F250-100P100-TH1', 5, 5, 'A2V04911-A2V04915', NULL, NULL, NULL, 1, 5, '2026-02-11 09:56:13', '2026-02-11 16:30:14', '2026-02-11 16:33:30', '2026-02-11 16:40:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021138180357345280, 2021138180353150976, 1, 'FS6122-250F250-40P100-TH1', 200, 50, 'A2V03530-A2V03729', NULL, NULL, NULL, 1, 5, '2026-02-11 09:56:13', '2026-02-11 16:30:14', '2026-02-11 16:33:30', '2026-02-11 16:40:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021401033169555456, 2021401033165361152, 1, 'FS4308-20-O6-EV-A', 10, 10, 'A2V05743-A2V05752', NULL, NULL, NULL, 1, 5, '2026-02-11 09:56:13', '2026-02-11 11:10:46', '2026-02-11 16:33:30', '2026-02-11 16:40:19', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021425036793860097, 2021425036793860096, 1, 'MF3000S-10-R-BVZ-CH4', 10, 10, 'A2V04280-A2V04289', NULL, '量程比100:1，精度1.5+0.5FS，系数1030', NULL, 1, 5, '2026-02-11 11:34:50', '2026-02-11 16:30:13', '2026-02-12 16:03:01', '2026-02-12 16:55:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021425036802248704, 2021425036793860096, 1, 'MF3000S-300-R-BVZ-CH4', 10, 10, 'A2V04290-A2V04299', NULL, '系数1030', NULL, 1, 5, '2026-02-11 11:34:50', '2026-02-11 16:30:14', '2026-02-13 16:09:14', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021425178762661888, 2021425178758467584, 1, 'FS4308-30-R-BV-A', 2, 2, 'A2V05741-A2V05742', NULL, NULL, NULL, 1, 5, '2026-02-11 11:34:50', '2026-02-11 16:30:13', '2026-02-12 16:03:01', '2026-02-12 16:55:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021425442362085376, 2021425442357891072, 1, 'FS4008-10-O6-CV-A', 1, 1, 'A2V04865', NULL, NULL, NULL, 1, 5, '2026-02-11 11:34:50', '2026-02-11 16:30:13', '2026-02-12 16:03:01', '2026-02-12 16:55:33', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021425703826608128, 2021425703822413824, 1, 'MF4008-40-O8-BV-R80C20', 2, 2, 'A2V04890-A2V04891', NULL, '系数1078', NULL, 1, 5, '2026-02-11 11:34:50', '2026-02-11 16:30:13', '2026-02-12 16:03:01', '2026-02-12 16:55:27', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021499301580886016, 2021499301572497408, 1, 'MF4008-10-O8-BV-E', 1, 1, 'A2V04859', NULL, '按系数6700来检验', NULL, 1, 5, '2026-02-11 16:54:00', '2026-02-12 15:56:26', '2026-02-13 15:53:38', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021499301585080320, 2021499301572497408, 1, 'MF4008-20-O8-BV-E', 1, 1, 'A2V04860', NULL, '按系数6700来检验', NULL, 1, 5, '2026-02-11 16:54:00', '2026-02-12 15:56:26', '2026-02-13 15:53:38', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021500987842088961, 2021500987842088960, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 50, 'A1V08946-A1V08995', NULL, NULL, NULL, 1, 5, '2026-02-11 16:54:00', '2026-02-12 15:55:42', '2026-02-26 13:26:29', '2026-02-26 13:36:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021507270074814464, 2021507270070620160, 1, 'FS4001E-1000-CV-O', 25, 13, 'A2V06772-A2V06791', NULL, NULL, NULL, 1, 5, '2026-02-11 16:54:00', '2026-02-12 15:56:26', '2026-02-13 15:53:38', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021507270079008768, 2021507270070620160, 1, 'FS4001E-200-CV-O', 20, 13, 'A2V06796-A2V06812', NULL, NULL, NULL, 1, 5, '2026-02-11 16:54:00', '2026-02-12 15:56:26', '2026-02-13 15:53:38', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021744005958455296, 2021744005950066688, 2, 'MF4703-R1-5-BV-A', 30, 30, 'A2V05711-A2V05740', NULL, NULL, NULL, 1, 5, '2026-02-12 08:31:47', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021744188612005888, 2021744188603617280, 2, 'MF5708-G-100-B-A', 1, 1, 'PEEVB20632', NULL, NULL, NULL, 1, 5, '2026-02-12 08:32:31', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021744759423225857, 2021744759423225856, 2, 'MFC2000-0200-G1-BA-1-A', 1, 1, 'MC2VB11186', NULL, NULL, NULL, 1, 5, '2026-02-12 08:34:47', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021744997298982912, 2021744997294788608, 2, 'MFC2000-0500-G1-BA-1-A', 1, 1, 'MC2VB11187', NULL, NULL, NULL, 1, 5, '2026-02-12 08:35:44', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021745150231695360, 2021745150227501056, 2, 'MFC2000-001-G1-BA-1-A', 1, 1, 'MC2VB11188', NULL, NULL, NULL, 1, 5, '2026-02-12 08:36:20', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021745337205379072, 2021745337201184768, 2, 'MFC2000-002-G2-BA-1-A', 1, 1, 'MC2VB11189', NULL, NULL, NULL, 1, 5, '2026-02-12 08:37:05', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021745516323131392, 2021745516318937088, 2, 'MFC2000-005-G2-BA-1-A', 1, 1, 'MC2VB11190', NULL, NULL, NULL, 1, 5, '2026-02-12 08:37:47', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021745700855730176, 2021745700851535872, 2, 'MFC2000-010-G3-BA-1-A', 1, 1, 'MC2VB11191', NULL, NULL, NULL, 1, 5, '2026-02-12 08:38:31', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021745924772843520, 2021745924768649216, 2, 'MFC2000-020-G3-BA-1-A', 1, 1, 'MC2VB11192', NULL, NULL, NULL, 1, 5, '2026-02-12 08:39:25', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021746072475258880, 2021746072471064576, 2, 'MFC2000-0100-G1-BA-1-A', 1, 1, 'MC2VB11193', NULL, NULL, NULL, 1, 5, '2026-02-12 08:40:00', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021746833733046273, 2021746833733046272, 2, 'MF4601-R2-100-V-A', 1, 1, 'A2V04887', NULL, NULL, NULL, 1, 5, '2026-02-12 08:43:02', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021746950129176577, 2021746950129176576, 2, 'MF4602-R2-1000-V-A', 1, 1, 'A2V04867', NULL, NULL, NULL, 1, 5, '2026-02-12 08:43:29', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021747165963866112, 2021747165959671808, 2, 'MF4608-N2-10-V-A', 5, 5, 'A2V02715-A2V02719', NULL, NULL, NULL, 1, 5, '2026-02-12 08:44:21', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021747375712620544, 2021747375708426240, 2, 'MF4703-R1-5-BV-A', 1, 1, 'A2V04866', NULL, NULL, NULL, 1, 5, '2026-02-12 08:45:11', '2026-02-12 08:45:51', '2026-02-12 16:07:47', '2026-02-12 16:55:27', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021772891119276032, 2021772891110887424, 1, 'FS34008-20-O8-CV-A-500mesh', 78, 78, 'A1V08868-A1V08945', NULL, NULL, NULL, 1, 5, '2026-02-12 10:26:39', '2026-02-12 15:56:26', '2026-02-26 13:26:14', '2026-02-26 13:36:26', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021773718290550784, 2021773718286356480, 1, 'FS5001E-1000-EV-A', 1, 1, 'A2V04892', NULL, NULL, NULL, 1, 5, '2026-02-12 10:32:25', '2026-02-12 15:57:31', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021773860997550080, 2021773860993355776, 1, 'FS5001E-500-V-H', 1, 1, 'D3U05827', NULL, NULL, NULL, 1, 5, '2026-02-12 10:32:25', '2026-02-12 15:57:31', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021773976055697408, 2021773976051503104, 1, 'FS5001E-300-EV-A', 15, 15, 'A2V05753-A2V05767', NULL, NULL, NULL, 1, 5, '2026-02-12 10:32:25', '2026-02-12 15:57:31', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021778090210742272, 2021778090206547968, 1, 'FS8001', 30, 13, 'A2V06813-A2V06842', NULL, NULL, NULL, 1, 5, '2026-02-12 11:30:16', '2026-02-12 15:57:31', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021778529945767937, 2021778529945767936, 1, 'MF4008-50-R-BV-A', 1, 1, 'A2V04885', NULL, NULL, NULL, 1, 5, '2026-02-12 11:30:16', '2026-02-13 15:47:25', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021779911050383360, 2021779911046189056, 1, 'FS1015CL-100-15-A', 17, 17, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-12 11:30:16', '2026-02-12 15:57:31', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021780109197692928, 2021780109189304320, 1, 'FS1015CL-100-15-A', 1, 1, 'C1U02218', NULL, NULL, NULL, 1, 5, '2026-02-12 11:30:16', '2026-02-12 15:57:31', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021788726940520448, 2021788726936326144, 1, 'FS4001-100-CV-A', 1, 1, 'A2V06857', NULL, NULL, NULL, 1, 5, '2026-02-12 11:30:16', '2026-02-13 15:47:14', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021818078591832064, 2021818078587637760, 2, 'MF6600-50SLPM', 12, 12, 'FAGVB17797-FAGVB17808', NULL, NULL, NULL, 1, 5, '2026-02-12 13:30:23', '2026-02-12 15:58:08', '2026-02-13 15:53:38', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021847278115737601, 2021847278115737600, 1, 'FS4308-50-R-BV-A', 4, 4, 'A2V06853-A2V06856', NULL, NULL, NULL, 1, 5, '2026-02-12 15:46:58', '2026-02-13 15:47:14', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021847595280617472, 2021847595276423168, 1, 'FS4308-50-O8-CV-A', 4, 4, 'A2V06848-A2V06851', NULL, NULL, NULL, 1, 5, '2026-02-12 15:46:58', '2026-02-13 15:47:14', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021853316126724096, 2021853316114141184, 1, 'MF4008-30-O8-V-R', 4, 4, 'A2V06844-A2V06847', NULL, '系数1180', NULL, 1, 5, '2026-02-12 15:46:58', '2026-02-13 15:47:14', '2026-02-13 16:05:41', '2026-02-13 17:12:50', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021867904171888640, 2021867904167694336, 1, 'MF4008-10-R-BV-A', 1, 1, 'A2V07858', NULL, NULL, NULL, 1, 5, '2026-02-12 16:52:59', '2026-02-13 15:47:14', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021868044454580225, 2021868044454580224, 1, 'MF3000M-1000-R-BAN-A', 5, 5, 'A2V04880-A2V04884', NULL, NULL, NULL, 1, 5, '2026-02-12 16:52:59', '2026-02-13 15:47:14', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2021868240383102977, 2021868240383102976, 1, 'FS4008-20-R-BV-A', 3, 3, 'A2V09082-A2V09084', NULL, NULL, NULL, 1, 5, '2026-02-12 16:52:59', '2026-02-13 15:47:14', '2026-02-13 15:48:59', '2026-02-13 17:13:03', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2022230316347674624, 2022230316343480320, 2, 'MF4719-B6F-600-BV-A', 1, 1, 'A2V04886', NULL, NULL, NULL, 1, 5, '2026-02-13 16:44:13', '2026-02-13 16:48:31', '2026-02-13 16:51:02', '2026-02-13 17:12:50', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2022230793118404608, 2022230793114210304, 2, 'MF5712-G-250-B-A', 2, 2, 'PEGVB20633-PEGVB20634', NULL, NULL, NULL, 1, 5, '2026-02-13 16:46:06', '2026-02-13 16:48:31', '2026-02-13 16:51:02', '2026-02-13 17:12:50', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2022231091182424064, 2022231091178229760, 2, 'MF4708-N3-20-AB-A', 2, 2, 'A2V04888-A2V04889', NULL, NULL, NULL, 1, 5, '2026-02-13 16:47:18', '2026-02-13 16:48:31', '2026-02-13 16:51:01', '2026-02-13 17:12:50', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2022231339757850624, 2022231339745267712, 2, 'MF5612-N-18m3/h-ABD-D-O', 1, 1, 'G6GVB22864', NULL, NULL, NULL, 1, 5, '2026-02-13 16:48:17', '2026-02-13 16:48:31', '2026-02-13 16:51:01', '2026-02-13 17:12:50', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026100682249850880, 2026100682212102144, 3, 'MF40FD-E-2.5-250-15-ABD-O', 1, 1, 'GSFVA30001', '17', NULL, '2026-02-24 10:27:37', 0, 1, '2026-01-07 09:03:40', '2026-01-07 09:09:48', '2026-01-07 09:13:56', '2026-01-07 10:21:42', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 17.60, 12.40, 4.895, 1654, 2, NULL, NULL, 12);
INSERT INTO `siargo_product` VALUES (2026101324808835072, 2026101324796252160, 3, 'MF40FD-E-2.5-250-15-ABD-O', 1, 1, 'GSFVA30002', '17', NULL, '2026-02-24 10:27:37', 0, 1, '2026-01-07 09:06:13', '2026-01-07 09:09:48', '2026-01-07 09:13:56', '2026-01-07 10:21:42', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 16.50, 12.50, 4.898, 1652, 1, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2026101759066099712, 2026101759057711104, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSFVA30003', '18', NULL, '2026-02-24 10:27:37', 0, 1, '2026-01-07 09:07:57', '2026-01-07 09:09:48', '2026-01-07 09:13:56', '2026-01-07 10:21:42', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 16.30, 10.20, 4.896, 1658, 2, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2026102099068964864, 2026102099060576256, 3, 'MF40FD-E-20-1333SLPM-15-ABD-A', 1, 1, 'GSFVA30004', '18', NULL, '2026-02-24 10:27:37', 0, 1, '2026-01-07 09:09:18', '2026-01-07 09:09:48', '2026-01-07 09:13:55', '2026-01-07 10:21:42', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 16.50, 12.40, 4.897, 1654, 1, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2026116690389880832, 2026116690381492224, 2, 'MF5212-E-Q-400-D', 2, 2, 'G7GUE36490/G7GUI40648', NULL, NULL, NULL, 1, 5, '2026-02-24 10:07:17', '2026-02-24 13:26:16', '2026-02-26 13:22:31', '2026-02-26 13:36:26', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026116912826404864, 2026116912822210560, 2, 'MF5212-E-Q-400-F', 1, 1, 'G7GTC20410', NULL, NULL, NULL, 1, 5, '2026-02-24 10:08:10', '2026-02-24 13:26:16', '2026-02-26 13:22:31', '2026-02-26 13:36:26', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026116912834793472, 2026116912822210560, 2, 'MF5212-E-Q-400-C', 1, 1, 'G7GUE36840', NULL, NULL, NULL, 1, 5, '2026-02-24 10:08:10', '2026-02-24 13:26:16', '2026-02-26 13:22:31', '2026-02-26 13:36:26', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026116912838987776, 2026116912822210560, 2, 'MF5219-E-Q-1000-C', 5, 5, 'G7HUE35599/G7HUE35601/G7HUE35602/G7HUE35603/G7HUE35607', NULL, NULL, NULL, 1, 5, '2026-02-24 10:08:10', '2026-02-24 13:26:16', '2026-02-26 13:22:31', '2026-02-26 13:36:26', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026117400846258176, 2026117400833675264, 2, 'MF5212-E-Q-400-F', 1, 1, 'G7GTJ26815', NULL, NULL, NULL, 1, 5, '2026-02-24 10:10:06', '2026-02-24 13:26:16', '2026-02-26 13:22:31', '2026-02-26 13:36:26', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026173675718168577, 2026173675718168576, 1, 'PFLOW2001-L-30-B-VI2C-A-10X', 700, 700, 'A1V18977-A1V19676', NULL, NULL, NULL, 1, 5, '2026-02-24 13:53:43', '2026-02-26 13:23:51', '2026-02-26 13:26:45', '2026-02-26 13:36:26', 2001547605315665920, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026215259511836673, 2026215259511836672, 2, 'MFC2000-0050-N2F-BA-1-A', 1, 1, 'MC2VB11280', NULL, NULL, NULL, 1, 5, '2026-02-24 16:38:57', '2026-02-25 10:59:00', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026215566497140737, 2026215566497140736, 2, 'MFC2000-0200-K1-BA-1-A', 2, 2, 'MC2VB11281-MC2VB11282', NULL, NULL, NULL, 1, 5, '2026-02-24 16:40:10', '2026-02-25 10:59:17', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026215826871144448, 2026215826854367232, 2, 'MF4708-N3-50-BV-N', 1, 1, 'A2V05580', NULL, NULL, NULL, 1, 5, '2026-02-24 16:41:13', '2026-02-25 10:58:33', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026216197995745281, 2026216197995745280, 2, 'MF4712R-Rc4-350-B-N', 7, 7, 'A3R15268/A3R15340/A3R18268/A3R18280/B1R09810/B1R14513/D3R02427', NULL, NULL, NULL, 1, 5, '2026-02-24 16:51:42', '2026-02-25 10:58:16', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026216197995745282, 2026216197995745280, 2, 'MF4712-Rc4-100-B-N', 3, 3, 'A3R18067/B1R09745/B1R14107', NULL, NULL, NULL, 1, 5, '2026-02-24 16:51:42', '2026-02-25 10:58:16', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026216198016716800, 2026216197995745280, 2, 'MF4712-Rc4-350-B-N', 2, 2, 'A3R18094/B1R12354', NULL, NULL, NULL, 1, 5, '2026-02-24 16:51:42', '2026-02-25 10:58:16', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026216198016716801, 2026216197995745280, 2, 'MF4712R-Rc4-100-B-N', 6, 6, 'A3R18187/A3R18188/A3R18192/A3R18193/B1R09801/B1R14076', NULL, NULL, NULL, 1, 5, '2026-02-24 16:51:42', '2026-02-25 10:58:16', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026216198016716802, 2026216197995745280, 2, 'MF4719-Rc6-1000-B-N', 1, 1, 'A3R18318', NULL, NULL, NULL, 1, 5, '2026-02-24 16:51:42', '2026-02-25 10:58:16', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026216198016716803, 2026216197995745280, 2, 'MF4719R-Rc6-1000-B-N', 4, 4, 'A3R18333/A3R18340/B1R14626/C3R01880', NULL, NULL, NULL, 1, 5, '2026-02-24 16:51:42', '2026-02-25 10:58:16', '2026-02-26 15:10:11', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026544632861413376, 2026544632832053248, 2, 'MF5212-E-Y-400-N', 85, 85, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-25 14:27:46', '2026-02-26 11:35:00', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026566314267365377, 2026566314267365376, 2, 'MFC2000-001-N2F-BA-1-CH4', 1, 1, 'MC2VC11284', NULL, 'GCF 1030', NULL, 1, 5, '2026-02-25 15:53:55', '2026-02-26 11:35:00', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026566314267365378, 2026566314267365376, 2, 'MFC2000-010-N2F-BA-1-A', 1, 1, 'MC2VC11285', NULL, NULL, NULL, 1, 5, '2026-02-25 15:53:55', '2026-02-26 11:35:00', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026566518022459392, 2026566518014070784, 2, 'MFC2000-0050-G1F-BV-0-A', 1, 1, 'MC2VC11283', NULL, NULL, NULL, 1, 5, '2026-02-25 15:54:44', '2026-02-26 11:35:00', '2026-02-26 15:10:10', '2026-02-26 15:12:13', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026831317683785728, 2026831317633454080, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 250, 50, 'A2V05330-A2V05579', NULL, '5V供电', NULL, 1, 5, '2026-02-26 09:27:03', '2026-02-27 14:23:40', '2026-02-27 14:24:23', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026848858099535872, 2026848858091147264, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A2V05030-A2V05179', NULL, '5V供电，5V供电', NULL, 1, 5, '2026-02-26 10:36:45', '2026-02-28 15:31:19', '2026-03-02 16:56:35', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026848858107924480, 2026848858091147264, 1, 'FS34308-50-O8-BV-A-0.5/4.5V', 150, 13, 'A2V05180-5329', NULL, '5V供电，5V供电', NULL, 1, 5, '2026-02-26 10:36:45', '2026-02-28 15:31:19', '2026-03-02 16:56:35', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026904938661924864, 2026904938657730560, 1, 'FS1015CL-150-ISO-A', 10, 10, 'A3V01427-A3V01436', NULL, NULL, NULL, 1, 5, '2026-02-26 14:38:32', '2026-02-27 14:23:40', '2026-02-27 14:24:23', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026907291582582784, 2026907291578388480, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 50, 'A1V15514-A1V15563', NULL, NULL, NULL, 1, 5, '2026-02-26 14:38:32', '2026-02-27 14:23:40', '2026-02-27 14:24:23', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026907424458133505, 2026907424458133504, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 50, 'A1V15564-A1V15613', NULL, NULL, NULL, 1, 5, '2026-02-26 14:38:32', '2026-02-27 14:23:40', '2026-02-27 14:24:23', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026921381583507456, 2026921381575118848, 2, 'MF4712-R4F-200-B-N', 80, 80, 'A3V01001-A3V01080', NULL, 'MF4512', NULL, 1, 5, '2026-02-26 15:24:50', '2026-02-28 15:47:18', '2026-02-28 15:55:53', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2026984718254067713, 2026984718254067712, 1, 'FS34008-20-O8-CV-A-500mesh', 58, 58, 'A1V15406-A1V15463', NULL, NULL, NULL, 1, 5, '2026-02-26 19:36:39', '2026-02-27 14:23:40', '2026-02-27 14:30:17', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027192101345546241, 2027192101345546240, 1, 'PFLOW1015CL-KS3', 800, 160, 'A1V04781-A1V05580', NULL, 'FS1015E-74.1-ISO-C，输出0.25-2.75V，零点±0.005V，精度1.5+0.2，重复性0.25，最小流量0.3L不截流', NULL, 1, 5, '2026-02-27 09:20:44', '2026-02-28 15:31:19', '2026-02-28 15:32:17', '2026-02-28 16:43:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027194864834039809, 2027194864834039808, 1, 'FS4001E-30-EV-A', 3, 3, 'A2V09150-A2V09152', NULL, NULL, NULL, 1, 5, '2026-02-27 14:26:54', '2026-02-27 14:27:27', '2026-02-27 14:27:41', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027195119868694529, 2027195119868694528, 1, 'FS4308-2-R-BV-A', 1, 1, 'A3V01325', NULL, NULL, NULL, 1, 5, '2026-02-27 14:26:54', '2026-02-27 14:27:27', '2026-02-27 14:27:41', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027195119868694530, 2027195119868694528, 1, 'FS4308-10-R-BV-A', 1, 1, 'A3V01326', NULL, NULL, NULL, 1, 5, '2026-02-27 14:26:54', '2026-02-27 14:27:27', '2026-02-27 14:27:41', '2026-02-27 16:27:11', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027200974324355073, 2027200974324355072, 2, 'MF5612-N-300-ABD-D-A', 1, 1, 'G6GVC22865', NULL, NULL, NULL, 1, 5, '2026-02-27 09:55:50', '2026-02-27 10:49:17', '2026-02-27 14:29:38', '2026-02-27 16:27:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027200974332743680, 2027200974324355072, 2, 'MF5619-N-800-ABD-D-A', 1, 1, 'G6HVC22866', NULL, '', NULL, 1, 5, '2026-02-27 09:55:50', '2026-02-27 10:49:18', '2026-02-27 14:29:38', '2026-02-27 16:27:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027200974332743681, 2027200974324355072, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GVC44546', NULL, 'D6 芯片', NULL, 1, 5, '2026-02-27 09:55:50', '2026-02-27 10:49:18', '2026-02-27 14:29:38', '2026-02-27 16:27:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027200974332743682, 2027200974324355072, 2, 'MF5219-E-Q-1000-D', 1, 1, 'G7HVC44547', NULL, 'D6 芯片', NULL, 1, 5, '2026-02-27 09:55:50', '2026-02-27 10:49:18', '2026-02-27 14:29:38', '2026-02-27 16:27:11', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027203414121631744, 2027203414109048832, 2, 'MF5619-N-800-ABD-D-N', 15, 15, 'G6HVC22867-G6HVC22881', NULL, NULL, NULL, 1, 5, '2026-02-27 10:05:32', '2026-02-27 16:11:30', '2026-02-28 15:37:28', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027204352060280832, 2027204352039309312, 2, 'MF5219-E-Q-600-N', 6, 6, 'G7HVC44548-G7HVC44553', NULL, NULL, NULL, 1, 5, '2026-02-27 10:09:15', '2026-02-27 16:11:49', '2026-02-28 15:37:28', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027220191429906433, 2027220191429906432, 2, 'MF5712-G-250-B-N', 5, 5, 'PEGVC20636-PEGVC20640', NULL, NULL, NULL, 1, 5, '2026-02-27 11:12:12', '2026-02-27 16:12:35', '2026-02-28 15:37:28', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027220639180247040, 2027220639167664128, 2, 'MF5712-N-250-B-O', 2, 2, 'PEGVC20641-PEGVC20642', NULL, NULL, NULL, 1, 5, '2026-02-27 11:13:58', '2026-02-27 16:12:09', '2026-02-28 15:37:28', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027254393579884544, 2027254393571495936, 1, 'MF4008-20-O8-BV-A', 23, 13, 'A2V09101-A2V09122', NULL, NULL, NULL, 1, 5, '2026-02-27 14:26:54', '2026-02-28 15:31:19', '2026-02-28 15:32:17', '2026-02-28 16:43:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027254393588273152, 2027254393571495936, 1, 'MF4003-5-O8-BV-A', 23, 23, 'A2V09124-A2V09146', NULL, NULL, NULL, 1, 5, '2026-02-27 14:26:54', '2026-02-28 15:31:19', '2026-02-28 15:32:17', '2026-02-28 16:43:44', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027273786221252609, 2027273786221252608, 1, 'FS34008-20-O8-CV-A-500mesh', 94, 13, 'A2V04936-A2V05029', NULL, NULL, NULL, 1, 3, '2026-03-11 09:51:10', '2026-03-11 13:59:50', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027293379501346816, 2027293379492958208, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 50, 'A1V15464-A1V15513', NULL, 'CO2测系数980', NULL, 1, 5, '2026-02-27 16:03:07', '2026-02-28 15:30:31', '2026-03-02 16:56:35', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027562242482753537, 2027562242482753536, 1, 'FS5001E-200-EV-C', 5, 5, '见数据库', NULL, NULL, NULL, 1, 5, '2026-02-28 10:51:38', '2026-02-28 15:31:19', '2026-03-02 16:56:35', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027562523870220289, 2027562523870220288, 1, 'FS5001E-200-V-A', 1, 1, 'A3V01311', NULL, NULL, NULL, 1, 5, '2026-02-28 10:51:38', '2026-02-28 15:31:19', '2026-03-02 16:56:35', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027565316924362753, 2027565316924362752, 3, 'MF65FD-E-6.5-195-15-ABD-C3H8', 1, 1, 'GSGVC30101', '20', '系数：300', NULL, 1, 5, '2026-02-28 10:03:36', '2026-02-28 10:03:51', '2026-02-28 15:34:20', '2026-02-28 16:43:44', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027574051180761089, 2027574051180761088, 2, 'MF25HD-36m3/h-N-BT-NG', 4, 4, 'GHLVC10720-GHLVC10723', NULL, 'GCF 1030；15°C  101.325Kpa', NULL, 1, 5, '2026-02-28 10:38:18', '2026-02-28 15:47:18', '2026-02-28 15:55:53', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027574051180761090, 2027574051180761088, 2, 'MF25HD-36m3/h-N-BT-A', 1, 1, 'GHLVC10724', NULL, '15°C  101.325Kpa', NULL, 1, 5, '2026-02-28 10:38:18', '2026-02-28 15:47:18', '2026-02-28 15:55:53', '2026-02-28 16:43:44', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027577615177338880, 2027577615168950272, 1, 'PFLOW2001-L-500-U-VI2C-A', 130, 13, 'A2V05581--A2V05710', NULL, NULL, NULL, 1, 5, '2026-02-28 10:52:57', '2026-03-02 16:54:59', '2026-03-02 16:58:07', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027635031172567041, 2027635031172567040, 1, 'FS4308-20-O8-BV-A', 10, 10, 'A2V09072-A2V09081', NULL, NULL, NULL, 1, 5, '2026-02-28 14:43:11', '2026-03-02 16:54:59', '2026-03-02 16:58:07', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027635178233253888, 2027635178224865280, 1, 'FS4308-20-O8-BV-A', 10, 10, 'A2V07859-A2V07868', NULL, NULL, NULL, 1, 5, '2026-02-28 14:43:11', '2026-03-02 16:54:59', '2026-03-02 16:58:07', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027635178237448192, 2027635178224865280, 1, 'FS5001E-4000-V-A', 10, 10, 'A2V07869-A2V07878', NULL, NULL, NULL, 1, 5, '2026-02-28 14:43:11', '2026-03-02 16:54:59', '2026-03-02 16:58:07', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027645209427169281, 2027645209427169280, 1, 'FS4308-3-O6-BV-A', 3, 3, 'A3V01081-A3V01083', NULL, NULL, NULL, 1, 5, '2026-02-28 15:21:09', '2026-03-02 16:54:59', '2026-03-02 16:58:07', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027659673920720897, 2027659673920720896, 1, 'FS34000H-20-V-C', 1000, 80, 'A2V05768-A2V06767', NULL, '系数540', NULL, 1, 5, '2026-02-28 16:20:17', '2026-03-04 15:42:57', '2026-03-04 15:43:06', '2026-03-04 17:08:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210498908160, 2027670210490519552, 1, 'FS4008-10-R-CV-A', 7, 7, 'A2V08679-A2V08685', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210503102464, 2027670210490519552, 1, 'FS4008-20-R-CV-A', 7, 7, 'A2V08686-A2V08692', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210503102465, 2027670210490519552, 1, 'FS4008-30-R-CV-A', 9, 9, 'A2V08693-A2V08701', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210503102466, 2027670210490519552, 1, 'FS4008-40-R-CV-A', 9, 9, 'A2V08702-A2V08710', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210503102467, 2027670210490519552, 1, 'FS4008-50-R-CV-A', 11, 11, 'A2V08711-A2V08721', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210503102468, 2027670210490519552, 1, 'FS4003-2-R-CV-A', 15, 15, 'A2V08722-A2V08736', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670210503102469, 2027670210490519552, 1, 'FS4003-5-R-CV-A', 15, 15, 'A2V08737-A2V08751', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:54:58', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2027670492297416704, 2027670492289028096, 1, 'FS4008-40-R-BV-A', 3, 3, 'A3V01812-A3V01814', NULL, NULL, NULL, 1, 5, '2026-02-28 17:02:20', '2026-03-02 16:53:43', '2026-03-02 16:58:47', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028277782335574016, 2028277782327185408, 1, 'FS4001E-500-V-H', 2, 2, 'A3V01184-A3V01185', NULL, NULL, NULL, 1, 5, '2026-03-02 09:16:01', '2026-03-02 16:53:43', '2026-03-02 16:58:47', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028278085411786753, 2028278085411786752, 1, 'FS4001E-200-EV-A', 2, 2, 'A3V01879-A3V01880', NULL, NULL, NULL, 1, 5, '2026-03-02 09:16:01', '2026-03-02 16:53:43', '2026-03-02 16:58:47', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028348407381086208, 2028348407376891904, 1, 'FS8001-700-EV', 25, 13, 'A3V01527-A3V01550', NULL, NULL, NULL, 1, 5, '2026-03-02 13:56:55', '2026-03-03 15:33:40', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028348760881221633, 2028348760881221632, 1, 'FS5001E-1000-V-A-KB-O4-P', 100, 13, 'A2V08752-A2V08843', NULL, NULL, NULL, 1, 5, '2026-03-02 13:56:55', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028348760902193152, 2028348760881221632, 1, 'FS4308-5-O4-CV-A-0.5/4.5V', 70, 13, 'A2V08904-A2V08964', NULL, NULL, NULL, 1, 5, '2026-03-02 13:56:55', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028348760906387456, 2028348760881221632, 1, 'FS4308-20-O8-CV-A-0.5/4.5V', 50, 13, 'A2V09004-A2V09046', NULL, NULL, NULL, 1, 5, '2026-03-02 13:56:55', '2026-03-02 16:53:43', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028350360655876096, 2028350360622321664, 1, 'FS5001E-6000-EV-A', 3, 3, 'A2V09147-A2V09149', NULL, '程序0136', NULL, 1, 5, '2026-03-02 14:03:10', '2026-03-03 15:33:40', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028356341569802241, 2028356341569802240, 1, 'FS4003-2-R-CV-A', 10, 10, 'A3V03116-A3V03125', NULL, NULL, NULL, 1, 5, '2026-03-02 14:30:34', '2026-03-03 15:33:40', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028371474513776640, 2028371474505388032, 1, 'FS7002-C', 1000, 80, 'A3V02101-3100', NULL, NULL, NULL, 1, 5, '2026-03-02 17:00:52', '2026-03-06 14:29:48', '2026-03-06 14:30:49', '2026-03-06 15:46:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028373841284026369, 2028373841284026368, 2, 'MF5708-G-100-B-N', 4, 4, 'PEEVC20643-PEEVC20646', NULL, NULL, NULL, 1, 5, '2026-03-02 15:36:23', '2026-03-03 10:03:08', '2026-03-03 15:36:02', '2026-03-04 11:28:49', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028374225259974657, 2028374225259974656, 2, 'MF5712-G-200-B-R', 1, 1, 'PEGVC20647', NULL, 'D9  GCF：1000', NULL, 1, 5, '2026-03-02 15:37:55', '2026-03-09 15:28:14', '2026-03-09 15:28:22', '2026-03-09 17:28:40', 2001546811778514944, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028374596548153344, 2028374596539764736, 2, 'MF4703-N1-2-V-A', 1, 1, 'A3V01581', NULL, NULL, NULL, 1, 5, '2026-03-02 15:39:23', '2026-03-03 10:02:40', '2026-03-03 15:36:02', '2026-03-04 11:28:49', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028374987876716544, 2028374987859939328, 2, 'MFC2000-005-G2F-BA-1-A-ZD', 128, 128, 'MC2VC11286-MC2VC11413', NULL, '精度1.0%；DB9线；-ZD为客户型号', NULL, 1, 5, '2026-03-02 15:40:57', '2026-03-03 16:04:13', '2026-03-04 15:46:29', '2026-03-04 17:08:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028380852369346561, 2028380852369346560, 2, 'MFC2000-0200-K6M-BV-0-A', 1, 1, 'MC2UI10847', NULL, NULL, NULL, 1, 5, '2026-03-02 16:04:15', '2026-03-03 10:02:58', '2026-03-03 15:36:02', '2026-03-04 11:28:49', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028394830269108224, 2028394830256525312, 1, 'FS5001E-1000-EV-A', 1, 1, 'C2U18096', NULL, NULL, NULL, 1, 5, '2026-03-02 17:00:52', '2026-03-04 11:37:48', '2026-03-04 11:37:57', '2026-03-04 11:40:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028645006682476544, 2028645006674087936, 1, 'FS4308-40-O6-EV-A-0.5/4.5V', 100, 13, 'A3V04424-A3V04523', NULL, NULL, NULL, 1, 5, '2026-03-03 11:09:04', '2026-03-05 13:30:10', '2026-03-05 13:31:18', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028668927968399360, 2028668927955816448, 1, 'FS1100-50F100-A', 30, 13, 'A3V01584-A3V01604', NULL, NULL, NULL, 1, 5, '2026-03-03 11:09:04', '2026-03-03 15:33:40', '2026-03-03 15:34:54', '2026-03-04 11:28:49', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028672060530479104, 2028672060513701888, 2, 'MF5612-N-200-AB-D-A', 26, 26, 'G6GVC22882-G6GVC22907', NULL, NULL, NULL, 1, 5, '2026-03-03 11:21:24', '2026-03-03 17:28:08', '2026-03-04 15:46:29', '2026-03-04 17:08:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028672526857392128, 2028672526840614912, 2, 'MF5619-N-600-AB-D-O', 1, 1, 'G6HVC22908', NULL, NULL, NULL, 1, 5, '2026-03-03 11:23:15', '2026-03-03 17:28:08', '2026-03-04 15:46:29', '2026-03-04 17:08:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028672526857392129, 2028672526840614912, 2, 'MF5212-E-Q-200-D', 4, 4, 'G7GVC44554-G7GVC44557', NULL, NULL, NULL, 1, 2, '2026-03-03 11:23:15', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028672936590561281, 2028672936590561280, 2, 'MF5019-N6M-e-8-800-15-AB-D-R', 1, 1, 'G5HTK11432', NULL, 'GCF 1180', NULL, 1, 5, '2026-03-03 11:24:53', '2026-03-03 17:28:07', '2026-03-04 15:46:29', '2026-03-04 17:08:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028673087593893888, 2028673087585505280, 2, 'MF5612-N-18m3/h-ABD-D-O', 1, 1, 'G6GVC22909', NULL, NULL, NULL, 1, 5, '2026-03-03 11:25:29', '2026-03-03 17:28:07', '2026-03-04 15:46:29', '2026-03-04 17:08:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028673494986641408, 2028673494982447104, 2, 'MF5619-N-800-ABD-N-R', 1, 1, 'G6HVC22910', NULL, 'GCF 1180', NULL, 1, 5, '2026-03-03 17:29:02', '2026-03-03 17:29:21', '2026-03-04 15:46:29', '2026-03-04 17:08:08', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028704208025145345, 2028704208025145344, 1, 'PNEU-053904', 1, 1, 'A3V01883', NULL, 'FS35001-F50000-B-A，滤波深度9', NULL, 1, 5, '2026-03-03 15:45:22', '2026-03-04 11:39:54', '2026-03-04 15:42:46', '2026-03-04 17:08:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028704626109173761, 2028704626109173760, 1, 'PNEU-056660', 5, 5, 'A3V01884-A3V01888', NULL, 'FS35001-F-10-V-A，滤波深度3', NULL, 1, 5, '2026-03-03 15:45:22', '2026-03-04 11:39:54', '2026-03-04 15:42:46', '2026-03-04 17:08:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028739014540775425, 2028739014540775424, 1, 'MF3000M-1500-R-BZZ-A', 2, 2, 'A3V04725-A3V04726', NULL, NULL, NULL, 1, 5, '2026-03-03 16:55:37', '2026-03-04 11:39:54', '2026-03-04 15:42:46', '2026-03-04 17:08:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028739178559033345, 2028739178559033344, 1, 'MF3000S-1500-R-BZZ-A', 7, 7, 'A3V01312-A3V01318', NULL, NULL, NULL, 1, 5, '2026-03-03 16:55:37', '2026-03-04 11:39:54', '2026-03-04 15:42:46', '2026-03-04 17:08:08', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028743239362531328, 2028743239354142720, 2, 'MF5706-G-20-B-A', 100, 100, 'PEDVC20648-PEDVC20747', NULL, NULL, NULL, 1, 5, '2026-03-03 16:04:15', '2026-03-05 11:33:50', '2026-03-06 13:41:36', '2026-03-06 13:49:23', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2028756121055776768, 2028756121038999552, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 50, 'A2V01849-A2V01898', NULL, NULL, NULL, 1, 5, '2026-03-03 16:55:37', '2026-03-04 11:39:16', '2026-03-06 13:40:33', '2026-03-06 15:46:15', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029024815963033601, 2029024815963033600, 3, 'MF80GD160', 1, 1, 'CSCSD31094', '16', NULL, NULL, 1, 5, '2026-03-04 10:43:08', '2026-03-04 10:43:47', '2026-03-05 13:30:23', '2026-03-05 16:28:29', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, 6.90, NULL, NULL, NULL, 1531, 4, 2.88, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029030300837597185, 2029030300837597184, 1, 'FS4308-40-O6-EV-A-0.5/4.5V', 200, 50, 'A3V04524-A3V04723', NULL, NULL, NULL, 1, 5, '2026-03-04 13:49:47', '2026-03-06 14:29:15', '2026-03-06 14:29:25', '2026-03-06 15:46:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029030512779972608, 2029030512771584000, 1, 'MF4003-5-O6-BV-A', 25, 13, 'A3V01186-A3V01210', NULL, NULL, NULL, 1, 5, '2026-03-04 13:49:47', '2026-03-05 13:30:10', '2026-03-05 13:31:18', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029064508209483776, 2029064508196900864, 1, 'FS4001-500-EV-A', 150, 13, 'A3V04867-A3V05016', NULL, NULL, NULL, 1, 5, '2026-03-04 13:49:47', '2026-03-05 13:30:10', '2026-03-06 13:40:33', '2026-03-06 15:46:15', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029072140349591553, 2029072140349591552, 2, 'MF6600-1SLPM', 2, 2, 'FAGVC17817-FAGVC17818', NULL, NULL, NULL, 1, 5, '2026-03-04 14:10:51', '2026-03-05 13:31:57', '2026-03-05 13:32:20', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029072140357980160, 2029072140349591552, 2, 'MF6600-3SLPM', 1, 1, 'FAGVC17819', NULL, NULL, NULL, 1, 5, '2026-03-04 14:10:51', '2026-03-05 13:31:57', '2026-03-05 13:32:20', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029072420646539264, 2029072420633956352, 2, 'MF6600-1SLPM', 2, 2, 'FAGVC17820-FAGVC17821', NULL, NULL, NULL, 1, 5, '2026-03-04 14:10:51', '2026-03-05 13:31:57', '2026-03-05 13:32:20', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029072420646539265, 2029072420633956352, 2, 'MF6600-3SLPM', 1, 1, 'FAGVC17822', NULL, NULL, NULL, 1, 5, '2026-03-04 14:10:51', '2026-03-05 13:31:57', '2026-03-05 13:32:20', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029072613274144768, 2029072613253173248, 2, 'MF6600-10SLPM', 8, 8, 'FAGVC17809-FAGVC17816', NULL, NULL, NULL, 1, 5, '2026-03-04 14:10:51', '2026-03-05 13:31:57', '2026-03-09 15:22:41', '2026-03-09 17:29:07', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029077300400476161, 2029077300400476160, 1, 'FS4003-2-O6-CV-A', 50, 13, 'A2V04374-A3V04423', NULL, NULL, NULL, 1, 5, '2026-03-04 14:13:49', '2026-03-05 13:33:11', '2026-03-05 13:33:23', '2026-03-05 16:28:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029363731161272321, 2029363731161272320, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 50, 'A2V01899-A2V01948', NULL, 'CO2测系数980', NULL, 1, 5, '2026-03-05 16:10:51', '2026-03-06 13:37:49', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029379490562297856, 2029379490553909248, 1, 'FS6122-250F250-100P100-TH0', 20, 13, 'A3V01989-A3V02008', NULL, NULL, NULL, 1, 5, '2026-03-05 16:10:51', '2026-03-06 13:37:49', '2026-03-06 13:39:07', '2026-03-06 15:46:22', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029382348078698497, 2029382348078698496, 3, 'MF25FD-E-1-100-15-AB-O', 1, 1, 'GSBVC30045', '2', NULL, NULL, 1, 5, '2026-03-05 10:23:50', '2026-03-05 10:28:34', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 12.60, 16.50, 4.802, 1626, 2, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2029382866322706433, 2029382866322706432, 3, 'MF25FD-E-1.0-100-15-ABD-A', 1, 1, 'GSBVC30046', '2', NULL, NULL, 1, 5, '2026-03-05 10:25:54', '2026-03-05 10:28:34', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 12.80, 15.80, 4.829, 1647, 1, NULL, NULL, 21);
INSERT INTO `siargo_product` VALUES (2029383228840595456, 2029383228836401152, 3, 'MF25FD-E-1.0-100-15-ABD-A', 1, 1, 'GSBVC30047', '2', NULL, NULL, 1, 5, '2026-03-05 10:27:20', '2026-03-05 10:28:34', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 13.20, 16.50, 4.763, 1648, 1, NULL, NULL, 24);
INSERT INTO `siargo_product` VALUES (2029383487905976321, 2029383487905976320, 3, 'MF25FD-E-1.0-100-15-ABD-A', 1, 1, 'GSBVC30048', '2', NULL, NULL, 1, 5, '2026-03-05 10:28:22', '2026-03-05 10:28:34', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, 12.80, 17.80, 4.763, 1648, 1, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2029395682270236673, 2029395682270236672, 2, 'MF5219-E-Q-1000-D', 30, 30, 'G7HVC44558-G7HVC44587', NULL, '共100只：第一批30只', '2026-03-05 11:23:21', 0, 1, '2026-03-05 11:16:49', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029395842236796928, 2029395842228408320, 2, 'MF5219-E-Q-1000-D', 30, 30, 'G7HVC44558-G7HVC44587', NULL, '共100只：第一批30只', NULL, 1, 5, '2026-03-05 11:17:27', '2026-03-05 11:33:32', '2026-03-06 13:41:25', '2026-03-06 13:49:23', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029396384627412992, 2029396384619024384, 2, 'MF5212-E-Q-400-D', 50, 50, 'G7GVC44658-G7GVC44707', NULL, '共100只：第一批50只', NULL, 1, 5, '2026-03-05 11:19:37', '2026-03-05 11:33:32', '2026-03-06 13:41:25', '2026-03-06 13:49:23', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029469874357391360, 2029469874349002752, 1, 'AM1000-10-BV-A', 1, 1, 'A3V01881', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 13:39:07', '2026-03-06 15:46:22', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029469874365779968, 2029469874349002752, 1, 'MF4003-2-R-BV-N', 1, 1, 'A3V01882', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 13:39:07', '2026-03-06 15:46:22', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029470073326784512, 2029470073318395904, 1, 'FS4308-5-O8-BV-A', 1, 1, 'A3V03111', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 13:39:07', '2026-03-06 15:46:15', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029470073326784513, 2029470073318395904, 1, 'FS4001E-500-CV-A', 1, 1, 'A3V03112', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 13:39:07', '2026-03-06 15:46:22', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029471302677286913, 2029471302677286912, 2, 'MFC2000-010-N3F-BV-1-A', 6, 6, 'MC2VC11417-MC2VC11422', NULL, NULL, NULL, 1, 5, '2026-03-05 16:17:18', '2026-03-06 13:44:30', '2026-03-09 15:22:41', '2026-03-09 17:29:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029471464573227009, 2029471464573227008, 2, 'MF4712-R4F-300-B-A', 34, 34, 'A3V01815-A3V01848', NULL, 'MF45XX', NULL, 1, 5, '2026-03-05 16:17:57', '2026-03-06 15:46:03', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029471464590004224, 2029471464573227008, 2, 'MF4719-R6F-500-B-A', 18, 18, 'A3V01849-A3V01866', NULL, 'MF45XX', NULL, 1, 5, '2026-03-05 16:17:57', '2026-03-06 15:46:03', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029471464590004225, 2029471464573227008, 2, 'MF4719-R6F-1000-B-A', 12, 12, 'A3V01867-A3V01878', NULL, 'MF45XX', NULL, 1, 5, '2026-03-05 16:17:57', '2026-03-06 15:46:03', '2026-03-09 15:22:41', '2026-03-09 17:29:07', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029471918715686913, 2029471918715686912, 2, 'MF4712-N4F-200-BV-O', 7, 7, 'A3V03102-A3V03108', NULL, NULL, NULL, 1, 5, '2026-03-05 16:19:45', '2026-03-06 13:44:08', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029471918715686914, 2029471918715686912, 2, 'MF4719-R6F-500-BV-A', 1, 1, 'A3V03109', NULL, NULL, NULL, 1, 5, '2026-03-05 16:19:45', '2026-03-06 13:44:08', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029472045652103169, 2029472045652103168, 2, 'FS4701-R1-50-BV-A', 1, 1, 'A3V03115', NULL, NULL, NULL, 1, 5, '2026-03-05 16:20:16', '2026-03-06 13:45:39', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029472352721293312, 2029472352712904704, 2, 'MF4701-N1-100-BV-A', 1, 1, 'A3V03110', NULL, NULL, NULL, 1, 5, '2026-03-05 16:21:29', '2026-03-06 13:52:21', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029477227651780609, 2029477227651780608, 1, 'MF3000M-100-R-BAZ-A', 1, 1, 'A3V04724', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029477356110729216, 2029477356098146304, 1, 'MF3000S-100-R-BZZ-A', 4, 4, 'A3V04827-A3V04830', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 14:28:54', '2026-03-06 15:46:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029478574186942464, 2029478574144999424, 1, 'MF3000M-5-O8-BNP-A', 2, 2, 'A3V03113-A3V03114', NULL, NULL, NULL, 1, 5, '2026-03-05 16:46:23', '2026-03-06 13:37:49', '2026-03-06 14:30:31', '2026-03-06 15:46:14', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547605315665920, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029752836361670657, 2029752836361670656, 1, 'MF4008-10-O8-BV-A', 20, 13, 'A3V05517-A3V05536', NULL, NULL, NULL, 1, 5, '2026-03-06 10:59:40', '2026-03-06 13:37:49', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029753031006736385, 2029753031006736384, 1, 'MF4008-20-O8-BV-A', 14, 14, 'A3V08557-A3V08570', NULL, NULL, NULL, 1, 5, '2026-03-06 10:59:40', '2026-03-06 13:37:49', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029753031006736386, 2029753031006736384, 1, 'MF4003-5-O8-BV-A', 14, 14, 'A3V08571-A3V08584', NULL, NULL, NULL, 1, 5, '2026-03-06 10:59:40', '2026-03-06 13:37:49', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029753152972902401, 2029753152972902400, 1, 'MF4008-20-O8-BV-A', 6, 6, 'A3V08585-A3V08590', NULL, NULL, NULL, 1, 5, '2026-03-06 10:59:40', '2026-03-06 13:37:49', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029753152972902402, 2029753152972902400, 1, 'MF4003-5-O8-BV-A', 6, 6, 'A3V08591-A3V08596', NULL, NULL, NULL, 1, 5, '2026-03-06 10:59:40', '2026-03-06 13:37:49', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029754282343780352, 2029754282335391744, 2, 'MF4712-Rc4-100-BV-A', 7, 7, 'A3V08707-A3V08713', NULL, NULL, NULL, 1, 5, '2026-03-06 11:01:46', '2026-03-06 16:54:05', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029754515438030848, 2029754515429642240, 2, 'MF4712-N4F-300-AB-N', 4, 4, 'A3V08703-A3V08706', NULL, NULL, NULL, 1, 5, '2026-03-06 11:02:42', '2026-03-06 16:53:17', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029754642772905985, 2029754642772905984, 2, 'FS4710-G4-100-V-A', 1, 1, 'A3V08735', NULL, NULL, NULL, 1, 5, '2026-03-06 11:03:12', '2026-03-06 16:53:05', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029754758644748289, 2029754758644748288, 2, 'MFC2000-005-N2F-BA-1-A', 10, 10, 'MC2VC11426-MC2VC11435', NULL, '滤波深度5', NULL, 1, 5, '2026-03-06 11:03:40', '2026-03-06 16:53:51', '2026-03-09 15:22:41', '2026-03-09 17:28:58', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, '/export/PDF/G2/103337_2029754758644748289.pdf', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029754880900321281, 2029754880900321280, 2, 'MFC2000-0200-K1-BA-1-A', 3, 3, 'MC2VC11423-MC2VC11425', NULL, NULL, NULL, 1, 5, '2026-03-06 11:04:09', '2026-03-06 16:53:32', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029755015285821441, 2029755015285821440, 2, 'MF4708-N3-10-BV-A', 1, 1, 'A3V08714', NULL, NULL, NULL, 1, 5, '2026-03-06 11:04:41', '2026-03-06 16:52:38', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029755243258826752, 2029755243246243840, 2, 'MF4701-R1-50-BV-A', 1, 1, 'A3V08766', NULL, NULL, NULL, 1, 5, '2026-03-06 11:05:35', '2026-03-06 16:52:51', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029761140852903937, 2029761140852903936, 1, 'FS5001E-1000-EV-A', 6, 6, 'A3V08597-A3V08602', NULL, 'A3V08597/98-I2C地址6，A3V08599/600-I2C地址4，A3V08601/602-I2C地址2', NULL, 1, 5, '2026-03-06 14:04:46', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029761346965196801, 2029761346965196800, 1, 'FS5001E-200-EV-A', 20, 13, 'A3V08746-A3V08765', NULL, NULL, NULL, 1, 5, '2026-03-06 14:04:46', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029796619627384833, 2029796619627384832, 2, 'MF4703-R1-5-BV-A', 3, 3, 'A3V03166-A3V03168', NULL, NULL, NULL, 1, 5, '2026-03-06 13:50:00', '2026-03-06 13:52:40', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029813199853703169, 2029813199853703168, 1, 'FS4008-20-O8-BV-O', 15, 15, 'A2V09086-A2V09100', NULL, NULL, NULL, 1, 5, '2026-03-06 14:57:40', '2026-03-06 15:39:32', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029822128117633024, 2029822128109244416, 1, 'FS6122-250F250-5P100-TH1', 1000, 80, 'A2V06858-A2V07857', NULL, NULL, NULL, 1, 5, '2026-03-06 15:35:23', '2026-03-10 14:04:54', '2026-03-10 14:06:36', '2026-03-10 16:29:46', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029836037830594560, 2029836037826400256, 1, 'FS7001E', 500, 50, 'A3V05017-A3V05516', NULL, NULL, NULL, 1, 5, '2026-03-06 16:26:45', '2026-03-10 17:19:27', '2026-03-11 14:11:28', '2026-03-11 16:57:29', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029846629769400320, 2029846629761011712, 1, 'FS4001E-30-CV-A', 50, 26, 'A3V08603-A3V08652', NULL, NULL, '2026-03-06 17:08:54', 0, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029846629773594624, 2029846629761011712, 1, 'FS4001E-500-CV-A', 50, 13, 'A3V08653-A3V08702', NULL, NULL, '2026-03-06 17:08:54', 0, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029846821738500096, 2029846821717528576, 1, 'FS4001E-30-CV-A', 50, 26, 'A3V08603-A3V08652', NULL, '系数1030', NULL, 1, 5, '2026-03-06 17:09:51', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2029846821742694400, 2029846821717528576, 1, 'FS4001E-500-CV-A', 50, 13, 'A3V08653-A3V08702', NULL, '系数1030', NULL, 1, 5, '2026-03-06 17:09:51', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030198357492617216, 2030198357480034304, 1, 'FS4008-50-R-BV-A', 10, 10, 'A3V08736-A3V08745', NULL, NULL, NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030198551382708225, 2030198551382708224, 1, 'MF4008-20-R-BV-A', 1, 1, 'A3V08734', NULL, NULL, NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030198920749895681, 2030198920749895680, 1, 'FS4003-2-O4-BV-H', 5, 5, 'A3V03169-A3V03173', NULL, '厂内型号FS4003-2-O4-BV-A', NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030199108340142080, 2030199108331753472, 1, 'FS4008-40-R-BV-A', 4, 4, 'A3V08820-A3V08823', NULL, NULL, NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030199284412829697, 2030199284412829696, 1, 'MF3000M-200-R-BVZ-A', 4, 4, 'A3V01577-A3V01580', NULL, NULL, NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-09 15:15:30', '2026-03-09 15:22:41', '2026-03-09 17:28:40', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030832527776534529, 2030832527776534528, 1, 'FSP1000-250-E-B', 5, 5, 'A3V09820-A3V09824', NULL, NULL, NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-09 15:15:30', '2026-03-10 14:07:36', '2026-03-10 16:29:46', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030832823386886145, 2030832823386886144, 1, 'FS1015CL-150-ISO-A', 100, 13, 'A3V09489-A3V09588', NULL, NULL, NULL, 1, 5, '2026-03-09 10:27:56', '2026-03-10 14:05:04', '2026-03-10 14:06:36', '2026-03-10 16:29:46', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030843575925592064, 2030843575913009152, 1, 'PFLOW1015CL-KS3', 800, 80, 'A2V07879-A2V08678', NULL, '厂内型号FS1015E-74.1-ISO-C，输出0.25-2.75V，精度1.5+0.2FS%，零点±5mv，不截流', NULL, 1, 2, '2026-03-09 13:54:00', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030891928059236353, 2030891928059236352, 1, 'FS4308-10-O6-BV-A-0.5/4.5V', 100, 13, 'A3V01211-A3V01310', NULL, NULL, NULL, 1, 5, '2026-03-09 14:46:39', '2026-03-10 14:05:04', '2026-03-10 14:06:36', '2026-03-10 16:29:46', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030894908783316992, 2030894908766539776, 1, 'FS4008-30-O6-V-A', 8, 8, 'A3V08715-A3V08722', NULL, NULL, NULL, 1, 5, '2026-03-09 14:46:39', '2026-03-09 15:15:30', '2026-03-10 14:07:36', '2026-03-10 16:29:41', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030894908783316993, 2030894908766539776, 1, 'FS4001E-1000-V-A', 1, 1, 'A3V08723', NULL, NULL, NULL, 1, 5, '2026-03-09 14:46:39', '2026-03-09 15:15:30', '2026-03-10 14:07:36', '2026-03-10 16:29:41', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030895268998533121, 2030895268998533120, 2, 'MF5612-N-200-AB-D-A', 17, 17, 'G6GVC22911-G6GVC22927', NULL, NULL, NULL, 1, 5, '2026-03-09 14:35:38', '2026-03-09 16:46:23', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030895429778788352, 2030895429762011136, 2, 'MF5612-N-300-AB-D-O', 4, 4, 'G6GVC22931-G6GVC22934', NULL, NULL, NULL, 1, 5, '2026-03-09 14:36:17', '2026-03-09 16:45:53', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030895429787176960, 2030895429762011136, 2, 'MF5619-N-800-AB-D-O', 3, 3, 'G6HVC22928-G6HVC22930', NULL, NULL, NULL, 1, 5, '2026-03-09 14:36:17', '2026-03-09 16:45:53', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030895655537201152, 2030895655512035328, 2, 'MF5612-N-200-AB-D-A', 1, 1, 'G6GVC22935', NULL, NULL, NULL, 1, 5, '2026-03-09 14:37:11', '2026-03-09 16:46:35', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030903630406471681, 2030903630406471680, 2, 'MF5003-N-e-0.15-15-15-ABD-D-A', 4, 4, 'G5CNA10023/G5CNE10065/G5CNH10119/G5CPK10457', NULL, NULL, NULL, 1, 5, '2026-03-09 15:08:52', '2026-03-09 16:46:07', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030903707715883009, 2030903707715883008, 2, 'MF5003-N1M-e-0.15-15-15-AB-D-A', 1, 1, 'G5CSJ11147', NULL, NULL, NULL, 1, 5, '2026-03-09 15:09:10', '2026-03-09 16:46:07', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030914125691736065, 2030914125691736064, 3, 'MF25FD-E-1-100-15-ABD-O', 1, 1, 'GSBVI30121', '2', NULL, NULL, 1, 5, '2026-03-09 15:50:34', '2026-03-09 15:55:26', '2026-03-10 15:41:45', '2026-03-10 16:29:41', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.60, 15.70, 3.879, 1654, 5, NULL, NULL, 1);
INSERT INTO `siargo_product` VALUES (2030914357276037121, 2030914357276037120, 3, 'MF25FD-E-1-100-15-ABD-O', 1, 1, 'GSBVI30122', '2', NULL, NULL, 1, 5, '2026-03-09 15:51:29', '2026-03-09 15:55:26', '2026-03-10 15:41:45', '2026-03-10 16:29:41', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, 12.60, 15.90, 6.898, 1657, 6, NULL, NULL, 2);
INSERT INTO `siargo_product` VALUES (2030914869656408064, 2030914869643825152, 3, 'MF2032Be-AB-O', 6, 6, 'GLEVC10631-10636', '3', NULL, NULL, 1, 5, '2026-03-09 15:53:32', '2026-03-09 15:55:26', '2026-03-10 15:41:45', '2026-03-10 16:29:41', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030915200394055680, 2030915200377278464, 3, 'MF2025Be-AB-O', 2, 2, 'GLBVC10637-10638', '19', NULL, NULL, 1, 5, '2026-03-09 15:54:51', '2026-03-09 15:55:26', '2026-03-10 15:41:45', '2026-03-10 16:29:41', 2001547605315665920, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030923086063063041, 2030923086063063040, 2, 'MF5706-N-20-B-A', 70, 70, 'PADVC20849-PADVC20918', NULL, NULL, NULL, 1, 5, '2026-03-09 16:26:11', '2026-03-10 11:29:55', '2026-03-11 14:11:28', '2026-03-11 16:57:29', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030928241810591744, 2030928241793814528, 2, 'MF4603-N1-2-AB-A', 1, 1, 'A3V03101', NULL, NULL, NULL, 1, 5, '2026-03-09 16:46:40', '2026-03-09 17:21:46', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030928627715919873, 2030928627715919872, 2, 'MF4701-R1-200-BV-A', 1, 1, 'C3S08301', NULL, NULL, NULL, 1, 5, '2026-03-09 16:48:12', '2026-03-09 17:21:59', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030928868179562496, 2030928868162785280, 2, 'MF4703-N1-3-AB-CO-AU', 1, 1, 'A3V08878', NULL, 'GCF 1000', NULL, 1, 5, '2026-03-09 16:49:09', '2026-03-09 17:21:35', '2026-03-10 15:41:15', '2026-03-10 16:29:41', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2030929057573359617, 2030929057573359616, 2, 'MF4701-G1-500-BA-A', 1, 1, 'A3V08812', NULL, NULL, NULL, 1, 3, '2026-03-09 16:49:54', '2026-03-12 11:15:46', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031203919785218049, 2031203919785218048, 2, 'MF5219-E-Q-1000-D', 70, 70, 'G7HVC44588-G7HVC44657', NULL, '共100只：第二批70只', NULL, 1, 5, '2026-03-10 11:02:07', '2026-03-10 17:21:03', '2026-03-11 14:11:28', '2026-03-11 16:57:29', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031204614584258560, 2031204614575869952, 2, 'MF5219-E-Q-1000-D', 4, 4, 'G7HVC45179-G7HVC45182', NULL, NULL, NULL, 1, 5, '2026-03-10 11:04:52', '2026-03-10 17:21:23', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031204809715863552, 2031204809673920512, 2, 'MF5212-E-Q-400-D', 50, 50, 'G7GVC44708-G7GVC44757', NULL, '共100只：第二批50只', NULL, 1, 5, '2026-03-10 11:05:39', '2026-03-10 17:21:13', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031205266500734976, 2031205266483957760, 2, 'MF5212-Q-300-A', 1, 1, 'G7GPI38285', NULL, NULL, NULL, 1, 5, '2026-03-10 11:07:28', '2026-03-10 17:21:58', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031205446788698112, 2031205446784503808, 2, 'MF5212-Q-300-A', 1, 1, 'G7GOH24497', NULL, NULL, NULL, 1, 5, '2026-03-10 11:08:11', '2026-03-10 17:21:48', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031205568226381825, 2031205568226381824, 2, 'MF5212-E-Q-400-D', 1, 1, 'G7GTL30609', NULL, NULL, NULL, 1, 5, '2026-03-10 11:08:40', '2026-03-10 17:22:10', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031235847334776832, 2031235847326388224, 2, 'MF4719-R6F-1000-V-A', 4, 4, 'A3V08879-A3V08882', NULL, NULL, NULL, 1, 5, '2026-03-10 13:08:59', '2026-03-10 17:21:34', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001546811778514944, 2001547605315665920, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031239265667371008, 2031239265621233664, 1, 'FS34000H-35-O8-EV-R80C20-1/3V', 2, 2, 'D2U14043-D2U14044', NULL, '输出1-3V系数908', NULL, 1, 5, '2026-03-10 13:24:56', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031239581334884353, 2031239581334884352, 1, 'FS8001', 2, 2, 'A3V09841-A3V09842', NULL, NULL, NULL, 1, 5, '2026-03-10 13:24:56', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031239821169381377, 2031239821169381376, 1, 'FS4001-1000-EV-H', 2, 2, 'A3V09594-A3V09595', NULL, NULL, NULL, 1, 5, '2026-03-10 13:24:56', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031242764597710848, 2031242764589322240, 1, 'MF3000M-100-R-BVZ-A', 21, 21, 'A3V04846-A3V04866', NULL, '用TI模拟芯片', NULL, 1, 5, '2026-03-10 13:55:53', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031244287897620481, 2031244287897620480, 1, 'MF3000M-100-R-BVZ-A', 20, 20, 'A3V05537-A3V05556', NULL, '用TI模拟芯片', NULL, 1, 5, '2026-03-10 13:55:53', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031252903094505473, 2031252903094505472, 1, 'MF3000S-100-R-BZZ-A', 15, 15, 'A3V04831-A3V04845', NULL, NULL, NULL, 1, 5, '2026-03-10 14:30:00', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031255702549155841, 2031255702549155840, 1, 'FS1015CL-100-ISO-A', 1, 1, 'A3V15059', NULL, NULL, NULL, 1, 5, '2026-03-10 14:30:00', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031255835802193921, 2031255835802193920, 1, 'FS1015E-100-ISO-EV-A', 3, 3, 'A3V09838-A3V09840', NULL, NULL, NULL, 1, 5, '2026-03-10 14:30:00', '2026-03-11 13:59:50', '2026-03-11 14:11:28', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031537569592627200, 2031537569563267072, 1, 'MF4008-20-O8-BV-A', 1, 1, 'A3V08824', NULL, NULL, NULL, 1, 5, '2026-03-11 09:51:10', '2026-03-11 14:11:38', '2026-03-11 14:11:49', '2026-03-11 16:57:21', 2001548276093927424, 2001548007356481536, 2001548007356481536, 2001547415494049792, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031543453379055616, 2031543453349695488, 2, 'MF5612-N-200-AB-D-A', 2, 2, 'G6GVC22997-G6GVC22998', NULL, NULL, NULL, 1, 2, '2026-03-11 09:31:18', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031546235104382976, 2031546235091800064, 1, 'PFLOW2001-L-5000-U-VI2C-A', 40, 40, 'A3V03126-A3V03165', NULL, NULL, NULL, 1, 3, '2026-03-11 09:51:10', '2026-03-11 13:59:50', NULL, NULL, 2001548276093927424, 2001548007356481536, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031547426613874688, 2031547426605486080, 1, 'PFLOW2001-L-2000-U-VI2C-A', 900, 160, 'A3V06057-A3V06956', NULL, NULL, NULL, 1, 2, '2026-03-11 09:51:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031559111483445248, 2031559111462473728, 2, 'MF5712-G-250-B-A', 100, 100, 'PEGVC20749-PEGVC20848', NULL, '共300只：第一批100只', NULL, 1, 2, '2026-03-11 10:33:31', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031612392935116800, 2031612392926728192, 1, 'FS4008-50-R-BV-A', 100, 26, 'A3V01327-A3V01426', NULL, NULL, NULL, 1, 2, '2026-03-11 15:36:56', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031635026427105280, 2031635026414522368, 1, 'FS34008-20-O8-CV-A-500mesh', 50, 50, 'A2V01749-A2V01798', NULL, NULL, NULL, 1, 2, '2026-03-11 15:36:56', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031635290592759808, 2031635290584371200, 1, 'FS34008-20-O8-CV-B-500mesh', 50, 50, 'A2V01799-A2V01848', NULL, 'CO2测系数980', NULL, 1, 2, '2026-03-11 15:36:56', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031642765337415680, 2031642765333221376, 2, 'MF5619-N-48m3/h-ABD-D-O', 1, 1, 'G6HVC22938', NULL, NULL, NULL, 1, 2, '2026-03-11 16:05:56', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031642931876450304, 2031642931872256000, 2, 'MF5612-N-200-B-D-A', 5, 5, 'G6GVC22941-G6GVC22945', NULL, NULL, NULL, 1, 2, '2026-03-11 16:06:35', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031643069160214528, 2031643069156020224, 2, 'MF5612-N-300-AB-N-A', 2, 2, 'G6GVC22939-G6GVC22940', NULL, NULL, NULL, 1, 2, '2026-03-11 16:07:08', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031643269996072960, 2031643269991878656, 2, 'MF5612-N-300-B-D-A', 1, 1, 'G6GVC22946', NULL, NULL, NULL, 1, 2, '2026-03-11 16:07:56', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031643452456685568, 2031643452452491264, 2, 'MF5219-E-Q-800-C', 20, 20, 'G7NVC45158-G7NVC45177', NULL, 'D6 芯片', NULL, 1, 2, '2026-03-11 16:08:39', NULL, NULL, NULL, 2001546811778514944, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031657939045830656, 2031657939041636352, 2, 'MF5006-N-e-0.5-50-15-ABD-D-A', 2, 2, 'G5DNH10120/G5DNH10121', NULL, NULL, NULL, 1, 3, '2026-03-11 17:06:13', '2026-03-12 11:16:13', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031657939050024960, 2031657939041636352, 2, 'MF5006-N2M-E-0.5-50-15-AB-D-A', 1, 1, 'G5DTG11368', NULL, NULL, NULL, 1, 3, '2026-03-11 17:06:13', '2026-03-12 11:16:13', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031657939058413568, 2031657939041636352, 2, 'MF5012-N-e-3-300-15-ABD-D-A', 2, 2, 'G5GOH10231/G5GOH10232', NULL, NULL, NULL, 1, 3, '2026-03-11 17:06:13', '2026-03-12 11:16:13', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031657939062607872, 2031657939041636352, 2, 'MF5019-N-e-8-800-15-ABD-D-A', 4, 4, 'G5GQC10507/G5GQC10508/G5HNH10122/G5HOH10233', NULL, NULL, NULL, 1, 3, '2026-03-11 17:06:13', '2026-03-12 11:16:13', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031657939066802176, 2031657939041636352, 2, 'MF5012-N4M-e-3-300-15-AB-D-A', 1, 1, 'G5GSK11158', NULL, NULL, NULL, 1, 3, '2026-03-11 17:06:13', '2026-03-12 11:16:13', NULL, NULL, 2001546811778514944, 2001547605315665920, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031899856497135616, 2031899856480358400, 3, 'MF50FD-E-4.0-400-15-ABD-A', 1, 1, 'GSAVC30023', '14', NULL, NULL, 1, 3, '2026-03-12 09:07:31', '2026-03-12 09:19:29', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 12.80, 17.60, 4.771, 1645, 1, NULL, NULL, 13);
INSERT INTO `siargo_product` VALUES (2031900772486991872, 2031900772478603264, 3, 'MF32FD-E-1.6-160-15-ABD-O', 1, 1, 'GSEVC30082', '16', NULL, NULL, 1, 3, '2026-03-12 09:11:09', '2026-03-12 09:19:29', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 12.80, 17.50, 4.791, 1654, 1, NULL, NULL, 6);
INSERT INTO `siargo_product` VALUES (2031901501679325185, 2031901501679325184, 3, 'MF50FD-E-4-300-15-ABD-C', 1, 1, 'GSAVC30024', '22', 'GSAVC30024', NULL, 1, 3, '2026-03-12 09:14:03', '2026-03-12 09:19:29', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, NULL, 13.60, 17.80, 4.818, 1654, 3, NULL, NULL, 5);
INSERT INTO `siargo_product` VALUES (2031902797606014977, 2031902797606014976, 3, 'MF50GDG40', 1, 1, 'CSAVC31024', '6', '丙烷，0.19-18m3/h MPa:0.6', NULL, 1, 3, '2026-03-12 09:19:12', '2026-03-12 09:19:29', NULL, NULL, 2001547605315665920, 2001547605315665920, NULL, NULL, NULL, 2, 10.60, NULL, NULL, 2.980, NULL, 2, 2.98, 3.3000, NULL);
INSERT INTO `siargo_product` VALUES (2031903221171998720, 2031903221167804416, 1, 'MF4008-50-R-BV-A', 1, 1, 'A3V15056', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031903221176193024, 2031903221167804416, 1, 'MF4008-20-R-BV-A', 2, 2, 'A3V15057-A3V15058', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031903451833552897, 2031903451833552896, 1, 'FS4008-50-R-CV-A', 4, 4, 'A3V08985-A3V08988', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031903746101727232, 2031903746089144320, 1, 'MF4008-50-R-BV-A', 2, 2, 'A3V15479-A3V15480', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031903866658607104, 2031903866650218496, 1, 'MF4008-50-R-BV-A', 5, 5, 'A3V09589-A3V09593', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031904002612776961, 2031904002612776960, 1, 'FS4008-40-R-BV-A', 2, 2, 'A3V09798-A3V09799', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031907319896199168, 2031907319892004864, 1, 'MF4008-10-R-BV-R', 1, 1, 'A3V08813', NULL, '系数1000', NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031907319900393472, 2031907319892004864, 1, 'MF4003-2-R-BV-R', 1, 1, 'A3V08814', NULL, '系数1000', NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031907319904587776, 2031907319892004864, 1, 'MF3000M-500-G4F-BVP-A', 2, 2, 'A3V08815-A3V08816', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031907319908782080, 2031907319892004864, 1, 'MF3000M-2-O6-BVZ-A', 2, 2, 'A3V08817-A3V08818', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031907319912976384, 2031907319892004864, 1, 'FS4308-2-O6-BV-A', 1, 1, 'A3V08819', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031908470695776256, 2031908470687387648, 1, 'FS4008-50-R-BV-A', 5, 5, 'A3V09765-A3V09769', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031908470699970560, 2031908470687387648, 1, 'FS4003-2-O6-BV-A', 1, 1, 'A3V09770', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031908470704164864, 2031908470687387648, 1, 'FS4001E-1000-CV-A', 2, 2, 'A3V09771-A3V09772', NULL, NULL, NULL, 1, 2, '2026-03-12 09:44:10', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031910884794552321, 2031910884794552320, 1, 'MF4003-2-O4-BV-A', 5, 5, 'A3V09759-A3V09763', NULL, NULL, NULL, 1, 2, '2026-03-12 10:08:55', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031914819026538497, 2031914819026538496, 1, 'MF3000M-1000-R-BAP-A', 3, 3, 'A3V08875-A3V08877', NULL, NULL, NULL, 1, 2, '2026-03-12 10:08:55', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031926649392320512, 2031926649333600256, 1, 'FS4308-50-O8-EV-C', 20, 20, 'A3V08825-A3V08844', NULL, NULL, NULL, 1, 2, '2026-03-12 10:54:06', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031926649392320513, 2031926649333600256, 1, 'FS4308-2-R-BV-A', 10, 10, 'A3V08845-A3V08854', NULL, NULL, NULL, 1, 2, '2026-03-12 10:54:06', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031926649434263552, 2031926649333600256, 1, 'FS4308-10-R-BV-A', 10, 10, 'A3V08855-A3V08864', NULL, NULL, NULL, 1, 2, '2026-03-12 10:54:06', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031926649434263553, 2031926649333600256, 1, 'FS4308-50-R-BV-A', 10, 10, 'A3V08865-A3V08874', NULL, NULL, NULL, 1, 2, '2026-03-12 10:54:06', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031931338536112128, 2031931338494169088, 1, 'FS4308-20-O6-CV-A', 1, 1, 'D3U12631', NULL, '', NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031931338582249472, 2031931338494169088, 1, 'FS4308-20-O6-CV-B', 1, 1, 'D3U12633', NULL, 'CO2测系数980', NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023176523777, 2031970023176523776, 1, 'FS4308-2-R-CV-A', 5, 5, 'A3V08767-A3V08771', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023189106688, 2031970023176523776, 1, 'FS4308-5-R-CV-A', 5, 5, 'A3V08772-A3V08776', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023197495296, 2031970023176523776, 1, 'FS4308-10-R-CV-A', 5, 5, 'A3V08777-A3V08781', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023197495297, 2031970023176523776, 1, 'FS4308-20-R-CV-A', 5, 5, 'A3V08782-A3V08786', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023197495298, 2031970023176523776, 1, 'FS4308-30-R-CV-A', 5, 5, 'A3V08787-A3V08791', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023214272512, 2031970023176523776, 1, 'FS4308-40-R-CV-A', 5, 5, 'A3V08792-A3V08796', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2031970023214272513, 2031970023176523776, 1, 'FS4308-50-R-CV-A', 5, 5, 'A3V08797-A3V08801', NULL, NULL, NULL, 1, 2, '2026-03-12 13:46:37', NULL, NULL, NULL, 2001548276093927424, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

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
INSERT INTO `siargo_qareport` VALUES (2013795165921988608, 102893, 2000847627364012032, 202601246, 2, '2026-01-21 10:05:56');
INSERT INTO `siargo_qareport` VALUES (2013795405202837504, 102900, 2000847627364012032, 202601247, 2, '2026-01-21 10:06:53');
INSERT INTO `siargo_qareport` VALUES (2013795743771250688, 102882, 2009090038669627392, 202601248, 2, '2026-01-21 10:08:14');
INSERT INTO `siargo_qareport` VALUES (2013854605513707520, 49276, 2008353170634166272, 202601249, 1, '2026-01-21 14:02:08');
INSERT INTO `siargo_qareport` VALUES (2013857363323113472, 49277, 2008353170634166272, 202601250, 1, '2026-01-21 14:13:05');
INSERT INTO `siargo_qareport` VALUES (2013862743663038464, 102898, 2013862626419658752, 202601251, 1, '2026-01-21 14:34:28');
INSERT INTO `siargo_qareport` VALUES (2013863048874151936, 102913, 2013862983799525376, 202601252, 1, '2026-01-21 14:35:41');
INSERT INTO `siargo_qareport` VALUES (2013863182651478016, 102889, 2008719110416748544, 202601253, 1, '2026-01-21 14:36:13');
INSERT INTO `siargo_qareport` VALUES (2013863473409019904, 102885, 2013863309252349952, 202601254, 1, '2026-01-21 14:37:22');
INSERT INTO `siargo_qareport` VALUES (2013863740670070784, 102903, 2008372258152501248, 202601255, 1, '2026-01-21 14:38:26');
INSERT INTO `siargo_qareport` VALUES (2013902190739836928, 102847, 2008069857314983936, 202601256, 1, '2026-01-21 17:11:13');
INSERT INTO `siargo_qareport` VALUES (2013902462916612096, 102876, 2013902307987410944, 202601257, 1, '2026-01-21 17:12:18');
INSERT INTO `siargo_qareport` VALUES (2013902628277047296, 102899, 2000847563539288064, 202601258, 1, '2026-01-21 17:12:57');
INSERT INTO `siargo_qareport` VALUES (2014148709032251392, 102910, 2007719394908426240, 202601259, 1, '2026-01-22 09:30:47');
INSERT INTO `siargo_qareport` VALUES (2014149303994273792, 102783, 2007693569572065280, 202601260, 1, '2026-01-22 09:33:09');
INSERT INTO `siargo_qareport` VALUES (2014149586270932992, 102783, 2007693569572065280, 202601261, 1, '2026-01-22 09:34:17');
INSERT INTO `siargo_qareport` VALUES (2014162480303820800, 102864, 2014162082650247168, 202601262, 1, '2026-01-22 10:25:31');
INSERT INTO `siargo_qareport` VALUES (2014170909718204416, 102776, 2000847563539288064, 202601263, 1, '2026-01-22 10:59:01');
INSERT INTO `siargo_qareport` VALUES (2014235894947368960, 102698, 2000847563539288064, 202601264, 1, '2026-01-22 15:17:14');
INSERT INTO `siargo_qareport` VALUES (2014247868921925632, 102808, 2009812759917481984, 202601265, 1, '2026-01-22 16:04:49');
INSERT INTO `siargo_qareport` VALUES (2014248189861679104, 102881, 2014248063466328064, 202601266, 1, '2026-01-22 16:06:06');
INSERT INTO `siargo_qareport` VALUES (2014248518628003840, 102890, 2007711147526836224, 202601267, 1, '2026-01-22 16:07:24');
INSERT INTO `siargo_qareport` VALUES (2014248652891869184, 102919, 2000847525710860288, 202601268, 1, '2026-01-22 16:07:56');
INSERT INTO `siargo_qareport` VALUES (2014248780830724096, 102908, 2008081212201881600, 202601269, 1, '2026-01-22 16:08:26');
INSERT INTO `siargo_qareport` VALUES (2014248973026316288, 102914, 2008070408593330176, 202601270, 1, '2026-01-22 16:09:12');
INSERT INTO `siargo_qareport` VALUES (2014250597069213696, 102907, 2009193617589915648, 202601271, 1, '2026-01-22 16:15:39');
INSERT INTO `siargo_qareport` VALUES (2014257692036419584, 102783, 2007693569572065280, 202601272, 1, '2026-01-22 16:43:51');
INSERT INTO `siargo_qareport` VALUES (2014257768355975168, 102925, 2008705235667505152, 202601273, 1, '2026-01-22 16:44:09');
INSERT INTO `siargo_qareport` VALUES (2014506015859265536, 102865, 2014505851761315840, 202601274, 1, '2026-01-23 09:10:36');
INSERT INTO `siargo_qareport` VALUES (2014530586498945024, 102841, 2013863309252349952, 202601275, 1, '2026-01-23 10:48:14');
INSERT INTO `siargo_qareport` VALUES (2014530887951962112, 102922, 2009520427271835648, 202601276, 1, '2026-01-23 10:49:26');
INSERT INTO `siargo_qareport` VALUES (2014531182949945344, 102904, 2014530991920369664, 202601277, 1, '2026-01-23 10:50:36');
INSERT INTO `siargo_qareport` VALUES (2014532645625384960, 102946, 2014532462644678656, 202601278, 1, '2026-01-23 10:56:25');
INSERT INTO `siargo_qareport` VALUES (2014565198570835968, 49274, 2008353170634166272, 202601279, 1, '2026-01-23 13:05:46');
INSERT INTO `siargo_qareport` VALUES (2014574626007273472, 102981, 2010889406141812736, 202601280, 1, '2026-01-23 13:43:14');
INSERT INTO `siargo_qareport` VALUES (2014578482409754624, 102931, 2014576433014099968, 202601281, 1, '2026-01-23 13:58:33');
INSERT INTO `siargo_qareport` VALUES (2014578590954147840, 102924, 2014576618557526016, 202601282, 1, '2026-01-23 13:58:59');
INSERT INTO `siargo_qareport` VALUES (2014579374848593920, 102951, 2014576843300917248, 202601283, 2, '2026-01-23 14:02:06');
INSERT INTO `siargo_qareport` VALUES (2014579540913672192, 102929, 2014577031339954176, 202601284, 2, '2026-01-23 14:02:46');
INSERT INTO `siargo_qareport` VALUES (2014580027343884288, 102940, 2014577280267702272, 202601285, 1, '2026-01-23 14:04:42');
INSERT INTO `siargo_qareport` VALUES (2014580173037228032, 102784, 2014577471767040000, 202601286, 1, '2026-01-23 14:05:16');
INSERT INTO `siargo_qareport` VALUES (2014580304398635008, 102806, 2014577471767040000, 202601287, 1, '2026-01-23 14:05:48');
INSERT INTO `siargo_qareport` VALUES (2014580410057347072, 102830, 2014577471767040000, 202601288, 1, '2026-01-23 14:06:13');
INSERT INTO `siargo_qareport` VALUES (2014580513723764736, 102756, 2014577471767040000, 202601289, 1, '2026-01-23 14:06:38');
INSERT INTO `siargo_qareport` VALUES (2014580679465881600, 102927, 2007714429901066240, 202601290, 2, '2026-01-23 14:07:17');
INSERT INTO `siargo_qareport` VALUES (2014580815457800192, 102928, 2014577795667972096, 202601291, 2, '2026-01-23 14:07:50');
INSERT INTO `siargo_qareport` VALUES (2014581001047363584, 102955, 2014577964585177088, 202601292, 1, '2026-01-23 14:08:34');
INSERT INTO `siargo_qareport` VALUES (2014584581640802304, 102906, 2009812759917481984, 202601293, 1, '2026-01-23 14:22:48');
INSERT INTO `siargo_qareport` VALUES (2014584704072536064, 102965, 2000847563539288064, 202601294, 1, '2026-01-23 14:23:17');
INSERT INTO `siargo_qareport` VALUES (2014584847920386048, 102932, 2008070674134716416, 202601295, 1, '2026-01-23 14:23:51');
INSERT INTO `siargo_qareport` VALUES (2014585166721044480, 102926, 2014585001301889024, 202601296, 1, '2026-01-23 14:25:07');
INSERT INTO `siargo_qareport` VALUES (2014585400251502592, 102943, 2000847563539288064, 202601297, 1, '2026-01-23 14:26:03');
INSERT INTO `siargo_qareport` VALUES (2014585501795602432, 102953, 2000847563539288064, 202601298, 1, '2026-01-23 14:26:27');
INSERT INTO `siargo_qareport` VALUES (2014585774156926976, 102950, 2014585695568252928, 202601299, 1, '2026-01-23 14:27:32');
INSERT INTO `siargo_qareport` VALUES (2014586036946849792, 102917, 2000847627364012032, 202601300, 1, '2026-01-23 14:28:35');
INSERT INTO `siargo_qareport` VALUES (2014591515546734592, 102783, 2007693569572065280, 202601301, 1, '2026-01-23 14:50:21');
INSERT INTO `siargo_qareport` VALUES (2014592520292257792, 102970, 2014592446904520704, 202601302, 1, '2026-01-23 14:54:20');
INSERT INTO `siargo_qareport` VALUES (2014599345439887360, 102909, 2009505047618899968, 202601303, 1, '2026-01-23 15:21:28');
INSERT INTO `siargo_qareport` VALUES (2015609776858058752, 102949, 2015609692292501504, 202601304, 2, '2026-01-26 10:16:33');
INSERT INTO `siargo_qareport` VALUES (2015614053387194368, 102971, 2009505047618899968, 202601305, 1, '2026-01-26 10:33:33');
INSERT INTO `siargo_qareport` VALUES (2015614185583267840, 102880, 2009505047618899968, 202601306, 1, '2026-01-26 10:34:04');
INSERT INTO `siargo_qareport` VALUES (2015614305511002112, 102894, 2009505047618899968, 202601307, 1, '2026-01-26 10:34:33');
INSERT INTO `siargo_qareport` VALUES (2015620542843703296, 102911, 2008718734284148736, 202601308, 1, '2026-01-26 10:59:20');
INSERT INTO `siargo_qareport` VALUES (2015620601861754880, 102902, 2000847525710860288, 202601309, 1, '2026-01-26 10:59:34');
INSERT INTO `siargo_qareport` VALUES (2015620713291829248, 102921, 2000847563539288064, 202601310, 1, '2026-01-26 11:00:01');
INSERT INTO `siargo_qareport` VALUES (2015621371063554048, 102918, 2000847525710860288, 202601311, 1, '2026-01-26 11:02:37');
INSERT INTO `siargo_qareport` VALUES (2015624905230241792, 102912, 2007693569572065280, 202601312, 1, '2026-01-26 11:16:40');
INSERT INTO `siargo_qareport` VALUES (2015626078641311744, 102966, 2015625105067855872, 202601313, 1, '2026-01-26 11:21:20');
INSERT INTO `siargo_qareport` VALUES (2015626683241844736, 102972, 2008081806144688128, 202601314, 2, '2026-01-26 11:23:44');
INSERT INTO `siargo_qareport` VALUES (2015626913534300160, 102895, 2015626152817577984, 202601315, 1, '2026-01-26 11:24:39');
INSERT INTO `siargo_qareport` VALUES (2015627036884586496, 102985, 2007719816536641536, 202601316, 2, '2026-01-26 11:25:08');
INSERT INTO `siargo_qareport` VALUES (2015627228174209024, 102988, 2010955877500964864, 202601317, 2, '2026-01-26 11:25:54');
INSERT INTO `siargo_qareport` VALUES (2015627471544504320, 102984, 2015625767688196096, 202601318, 2, '2026-01-26 11:26:52');
INSERT INTO `siargo_qareport` VALUES (2015628128989073408, 102960, 2015625627841712128, 202601319, 1, '2026-01-26 11:29:29');
INSERT INTO `siargo_qareport` VALUES (2015656873930117120, 102983, 2008719311974027264, 202601320, 1, '2026-01-26 13:23:42');
INSERT INTO `siargo_qareport` VALUES (2015657215384211456, 102969, 2015657123667365888, 202601321, 1, '2026-01-26 13:25:03');
INSERT INTO `siargo_qareport` VALUES (2015659039986143232, 5000106, 2015658945274564608, 202601322, 1, '2026-01-26 13:32:18');
INSERT INTO `siargo_qareport` VALUES (2015671745128550400, 103000, 2008719311974027264, 202601323, 1, '2026-01-26 14:22:48');
INSERT INTO `siargo_qareport` VALUES (2015672025031233536, 103003, 2015671917715771392, 202601324, 1, '2026-01-26 14:23:54');
INSERT INTO `siargo_qareport` VALUES (2015672210486579200, 102945, 2008070087435472896, 202601325, 1, '2026-01-26 14:24:39');
INSERT INTO `siargo_qareport` VALUES (2015672509888581632, 102962, 2013124143279362048, 202601326, 1, '2026-01-26 14:25:50');
INSERT INTO `siargo_qareport` VALUES (2015672869113942016, 102964, 2007715109323788288, 202601327, 1, '2026-01-26 14:27:16');
INSERT INTO `siargo_qareport` VALUES (2015694219077603328, 103004, 2010905350503256064, 202601328, 1, '2026-01-26 15:52:06');
INSERT INTO `siargo_qareport` VALUES (2015694343187058688, 102989, 2000847525710860288, 202601329, 2, '2026-01-26 15:52:35');
INSERT INTO `siargo_qareport` VALUES (2015707435245621248, 102884, 2008069857314983936, 202601330, 1, '2026-01-26 16:44:37');
INSERT INTO `siargo_qareport` VALUES (2015712259387281408, 103016, 2000847525710860288, 202601331, 2, '2026-01-26 17:03:47');
INSERT INTO `siargo_qareport` VALUES (2015967487764320256, 102997, 2007711147526836224, 202601332, 1, '2026-01-27 09:57:58');
INSERT INTO `siargo_qareport` VALUES (2015970378612854784, 102996, 2015969515408314368, 202601333, 1, '2026-01-27 10:09:27');
INSERT INTO `siargo_qareport` VALUES (2015970772181176320, 102892, 2015969723378683904, 202601334, 1, '2026-01-27 10:11:01');
INSERT INTO `siargo_qareport` VALUES (2015971021020844032, 102887, 2009468509057044480, 202601335, 1, '2026-01-27 10:12:00');
INSERT INTO `siargo_qareport` VALUES (2015971319156166656, 102934, 2015969919147823104, 202601336, 1, '2026-01-27 10:13:12');
INSERT INTO `siargo_qareport` VALUES (2015971543656288256, 102954, 2015970085070295040, 202601337, 1, '2026-01-27 10:14:05');
INSERT INTO `siargo_qareport` VALUES (2015971663122649088, 103014, 2000847525710860288, 202601338, 1, '2026-01-27 10:14:34');
INSERT INTO `siargo_qareport` VALUES (2015971949300011008, 102916, 2015970239416487936, 202601339, 1, '2026-01-27 10:15:42');
INSERT INTO `siargo_qareport` VALUES (2015972332307075072, 103007, 2000847563539288064, 202601340, 1, '2026-01-27 10:17:13');
INSERT INTO `siargo_qareport` VALUES (2015974091096182784, 102827, 2009812759917481984, 202601341, 1, '2026-01-27 10:24:12');
INSERT INTO `siargo_qareport` VALUES (2015985925069066240, 102540, 2015982355649253376, 202601342, 1, '2026-01-27 11:11:14');
INSERT INTO `siargo_qareport` VALUES (2015986253634064384, 102540, 2015982355649253376, 202601343, 1, '2026-01-27 11:12:32');
INSERT INTO `siargo_qareport` VALUES (2015986756954738688, 102540, 2015982355649253376, 202601344, 1, '2026-01-27 11:14:32');
INSERT INTO `siargo_qareport` VALUES (2015987984413609984, 102803, 2000847563539288064, 202601345, 1, '2026-01-27 11:19:25');
INSERT INTO `siargo_qareport` VALUES (2015988493417566208, 102999, 2015988326291329024, 202601346, 1, '2026-01-27 11:21:26');
INSERT INTO `siargo_qareport` VALUES (2015988671566434304, 103005, 2015988326291329024, 202601347, 1, '2026-01-27 11:22:09');
INSERT INTO `siargo_qareport` VALUES (2015989461106413568, 102980, 2015989275340689408, 202601348, 2, '2026-01-27 11:25:17');
INSERT INTO `siargo_qareport` VALUES (2015989861675028480, 102540, 2015982355649253376, 202601349, 1, '2026-01-27 11:26:52');
INSERT INTO `siargo_qareport` VALUES (2015991536611610624, 102540, 2015982355649253376, 202601350, 1, '2026-01-27 11:33:32');
INSERT INTO `siargo_qareport` VALUES (2015991902916956160, 102540, 2015982355649253376, 202601351, 1, '2026-01-27 11:34:59');
INSERT INTO `siargo_qareport` VALUES (2015992219922452480, 102540, 2015982355649253376, 202601352, 1, '2026-01-27 11:36:15');
INSERT INTO `siargo_qareport` VALUES (2015992494695501824, 102540, 2015982355649253376, 202601353, 1, '2026-01-27 11:37:20');
INSERT INTO `siargo_qareport` VALUES (2015992783599161344, 102540, 2015982355649253376, 202601354, 1, '2026-01-27 11:38:29');
INSERT INTO `siargo_qareport` VALUES (2015993048352018432, 102540, 2015982355649253376, 202601355, 1, '2026-01-27 11:39:32');
INSERT INTO `siargo_qareport` VALUES (2015993379966275584, 102540, 2015982355649253376, 202601356, 1, '2026-01-27 11:40:51');
INSERT INTO `siargo_qareport` VALUES (2016023620004728832, 103033, 2007711100328333312, 202601357, 1, '2026-01-27 13:41:01');
INSERT INTO `siargo_qareport` VALUES (2016024057315446784, 102326, 2007715022455558144, 202601358, 1, '2026-01-27 13:42:45');
INSERT INTO `siargo_qareport` VALUES (2016025779580555264, 102353, 2009149718863400960, 202601359, 1, '2026-01-27 13:49:36');
INSERT INTO `siargo_qareport` VALUES (2016033018475302912, 102948, 2016032736676794368, 202601360, 1, '2026-01-27 14:18:22');
INSERT INTO `siargo_qareport` VALUES (2016033181314961408, 102968, 2014505851761315840, 202601361, 1, '2026-01-27 14:19:01');
INSERT INTO `siargo_qareport` VALUES (2016046601447788544, 102540, 2015982355649253376, 202601362, 1, '2026-01-27 15:12:20');
INSERT INTO `siargo_qareport` VALUES (2016047119612104704, 102846, 2010989045750812672, 202601363, 1, '2026-01-27 15:14:24');
INSERT INTO `siargo_qareport` VALUES (2016049504845680640, 102887, 2009468509057044480, 202601364, 1, '2026-01-27 15:23:53');
INSERT INTO `siargo_qareport` VALUES (2016058536004276224, 102941, 2015970085070295040, 202601365, 1, '2026-01-27 15:59:46');
INSERT INTO `siargo_qareport` VALUES (2016071959681290240, 102944, 2008415147318431744, 202601366, 1, '2026-01-27 16:53:06');
INSERT INTO `siargo_qareport` VALUES (2016123446876819456, 102976, 2016123322280824832, 202601367, 1, '2026-01-27 20:17:42');
INSERT INTO `siargo_qareport` VALUES (2016123592201064448, 103008, 2007719394908426240, 202601368, 1, '2026-01-27 20:18:16');
INSERT INTO `siargo_qareport` VALUES (2016123715589099520, 102992, 2007719394908426240, 202601369, 1, '2026-01-27 20:18:46');
INSERT INTO `siargo_qareport` VALUES (2016329838120259584, 102891, 2016329694373072896, 202601370, 1, '2026-01-28 09:57:49');
INSERT INTO `siargo_qareport` VALUES (2016334711779872768, 102860, 2014577471767040000, 202601371, 1, '2026-01-28 10:17:11');
INSERT INTO `siargo_qareport` VALUES (2016335712339480576, 103024, 2000847525710860288, 202601372, 1, '2026-01-28 10:21:10');
INSERT INTO `siargo_qareport` VALUES (2016335761828073472, 103023, 2000847525710860288, 202601373, 1, '2026-01-28 10:21:21');
INSERT INTO `siargo_qareport` VALUES (2016335840253169664, 103025, 2000847525710860288, 202601374, 1, '2026-01-28 10:21:40');
INSERT INTO `siargo_qareport` VALUES (2016344582352916480, 102967, 2000847563539288064, 202601375, 1, '2026-01-28 10:56:24');
INSERT INTO `siargo_qareport` VALUES (2016349154970554368, 103002, 2014585695568252928, 202601376, 1, '2026-01-28 11:14:35');
INSERT INTO `siargo_qareport` VALUES (2016349340044218368, 103012, 2000847525710860288, 202601377, 1, '2026-01-28 11:15:19');
INSERT INTO `siargo_qareport` VALUES (2016349682316201984, 102916, 2015970239416487936, 202601378, 1, '2026-01-28 11:16:40');
INSERT INTO `siargo_qareport` VALUES (2016349951980589056, 103007, 2000847563539288064, 202601379, 1, '2026-01-28 11:17:45');
INSERT INTO `siargo_qareport` VALUES (2016351341788385280, 103017, 2000847627364012032, 202601380, 1, '2026-01-28 11:23:16');
INSERT INTO `siargo_qareport` VALUES (2016351511334735872, 102979, 2007711147526836224, 202601381, 1, '2026-01-28 11:23:56');
INSERT INTO `siargo_qareport` VALUES (2016351657510424576, 102973, 2014585695568252928, 202601382, 1, '2026-01-28 11:24:31');
INSERT INTO `siargo_qareport` VALUES (2016351920732360704, 102947, 2013071008288591872, 202601383, 1, '2026-01-28 11:25:34');
INSERT INTO `siargo_qareport` VALUES (2016352121996038144, 103001, 2000847563539288064, 202601384, 1, '2026-01-28 11:26:22');
INSERT INTO `siargo_qareport` VALUES (2016352370210754560, 102998, 2016352244226445312, 202601385, 1, '2026-01-28 11:27:21');
INSERT INTO `siargo_qareport` VALUES (2016382610622500864, 102540, 2015982355649253376, 202601386, 1, '2026-01-28 13:27:31');
INSERT INTO `siargo_qareport` VALUES (2016382880752455680, 102540, 2015982355649253376, 202601387, 1, '2026-01-28 13:28:36');
INSERT INTO `siargo_qareport` VALUES (2016383159719809024, 102540, 2015982355649253376, 202601388, 1, '2026-01-28 13:29:42');
INSERT INTO `siargo_qareport` VALUES (2016383412158189568, 102540, 2015982355649253376, 202601389, 1, '2026-01-28 13:30:42');
INSERT INTO `siargo_qareport` VALUES (2016383670233714688, 102540, 2015982355649253376, 202601390, 1, '2026-01-28 13:31:44');
INSERT INTO `siargo_qareport` VALUES (2016392160410980352, 103030, 2010907066472714240, 202601391, 1, '2026-01-28 14:05:28');
INSERT INTO `siargo_qareport` VALUES (2016392332301946880, 102982, 2000847525710860288, 202601392, 1, '2026-01-28 14:06:09');
INSERT INTO `siargo_qareport` VALUES (2016394272826052608, 102718, 2000847563539288064, 202601393, 1, '2026-01-28 14:13:52');
INSERT INTO `siargo_qareport` VALUES (2016394410642493440, 102719, 2000847563539288064, 202601394, 1, '2026-01-28 14:14:24');
INSERT INTO `siargo_qareport` VALUES (2016400373596409856, 102595, 2007711100328333312, 202601395, 1, '2026-01-28 14:38:06');
INSERT INTO `siargo_qareport` VALUES (2016400801457360896, 102978, 2016400452004728832, 202601396, 1, '2026-01-28 14:39:48');
INSERT INTO `siargo_qareport` VALUES (2016410788690776064, 103032, 2007711626285666304, 202601397, 1, '2026-01-28 15:19:29');
INSERT INTO `siargo_qareport` VALUES (2016427373623103488, 102271, 2007693569572065280, 202601398, 1, '2026-01-28 16:25:23');
INSERT INTO `siargo_qareport` VALUES (2016428242154409984, 102271, 2007693569572065280, 202601399, 1, '2026-01-28 16:28:51');
INSERT INTO `siargo_qareport` VALUES (2016681110476279808, 103026, 2000847525710860288, 202601400, 1, '2026-01-29 09:13:39');
INSERT INTO `siargo_qareport` VALUES (2016681302634123264, 103027, 2010989961023442944, 202601401, 1, '2026-01-29 09:14:25');
INSERT INTO `siargo_qareport` VALUES (2016681495630827520, 103045, 2008081212201881600, 202601402, 1, '2026-01-29 09:15:11');
INSERT INTO `siargo_qareport` VALUES (2016681697339101184, 103034, 2016681610454093824, 202601403, 1, '2026-01-29 09:15:59');
INSERT INTO `siargo_qareport` VALUES (2016681738166456320, 102994, 2008722775034679296, 202601404, 1, '2026-01-29 09:16:09');
INSERT INTO `siargo_qareport` VALUES (2016692824882139136, 103041, 2009149718863400960, 202601405, 1, '2026-01-29 10:00:12');
INSERT INTO `siargo_qareport` VALUES (2016698038276182016, 103061, 2013536659369218048, 202601406, 1, '2026-01-29 10:20:55');
INSERT INTO `siargo_qareport` VALUES (2016699771752665088, 103048, 2010905611992944640, 202601407, 2, '2026-01-29 10:27:48');
INSERT INTO `siargo_qareport` VALUES (2016699892947079168, 103058, 2007719346942365696, 202601408, 1, '2026-01-29 10:28:17');
INSERT INTO `siargo_qareport` VALUES (2016751166174384128, 103028, 2016751049635647488, 202601409, 1, '2026-01-29 13:52:02');
INSERT INTO `siargo_qareport` VALUES (2016751372764827648, 103062, 2016751257031397376, 202601410, 1, '2026-01-29 13:52:51');
INSERT INTO `siargo_qareport` VALUES (2016751546580979712, 103039, 2007711147526836224, 202601411, 1, '2026-01-29 13:53:32');
INSERT INTO `siargo_qareport` VALUES (2016753192824328192, 103060, 2016752149134692352, 202601412, 1, '2026-01-29 14:00:05');
INSERT INTO `siargo_qareport` VALUES (2016756092157939712, 103055, 2008417707106357248, 202601413, 1, '2026-01-29 14:11:36');
INSERT INTO `siargo_qareport` VALUES (2016768637790965760, 102905, 2007715022455558144, 202601414, 1, '2026-01-29 15:01:27');
INSERT INTO `siargo_qareport` VALUES (2016775537874030592, 102987, 2016775377605480448, 202601415, 1, '2026-01-29 15:28:52');
INSERT INTO `siargo_qareport` VALUES (2016790216411303936, 103084, 2008069857314983936, 202601416, 1, '2026-01-29 16:27:12');
INSERT INTO `siargo_qareport` VALUES (2016790358052950016, 103073, 2010583151636500480, 202601417, 1, '2026-01-29 16:27:46');
INSERT INTO `siargo_qareport` VALUES (2016790486809694208, 103029, 2008069857314983936, 202601418, 1, '2026-01-29 16:28:16');
INSERT INTO `siargo_qareport` VALUES (2017045014826045440, 103013, 2009505047618899968, 202601419, 1, '2026-01-30 09:19:41');
INSERT INTO `siargo_qareport` VALUES (2017055631318306816, 103036, 2000847627364012032, 202601420, 1, '2026-01-30 10:01:52');
INSERT INTO `siargo_qareport` VALUES (2017055846624514048, 103035, 2010907066472714240, 202601421, 1, '2026-01-30 10:02:43');
INSERT INTO `siargo_qareport` VALUES (2017055951280787456, 103022, 2000847525710860288, 202601422, 1, '2026-01-30 10:03:08');
INSERT INTO `siargo_qareport` VALUES (2017056367301218304, 103039, 2017056238917767168, 202601423, 1, '2026-01-30 10:04:47');
INSERT INTO `siargo_qareport` VALUES (2017056654443270144, 103038, 2017056582234132480, 202601424, 1, '2026-01-30 10:05:56');
INSERT INTO `siargo_qareport` VALUES (2017057291323166720, 103034, 2016681610454093824, 202601425, 1, '2026-01-30 10:08:28');
INSERT INTO `siargo_qareport` VALUES (2017074449260335104, 102833, 2007714677515997184, 202601426, 1, '2026-01-30 11:16:38');
INSERT INTO `siargo_qareport` VALUES (2017106635535536128, 49284, 2008353170634166272, 202601427, 1, '2026-01-30 13:24:32');
INSERT INTO `siargo_qareport` VALUES (2017118479096598528, 103099, 2015671917715771392, 202601428, 1, '2026-01-30 14:11:36');
INSERT INTO `siargo_qareport` VALUES (2017159566288867328, 102995, 2008415147318431744, 202601429, 1, '2026-01-30 16:54:52');
INSERT INTO `siargo_qareport` VALUES (2017166448483225600, 103063, 2000847563539288064, 202601430, 1, '2026-01-30 17:22:13');
INSERT INTO `siargo_qareport` VALUES (2017167007902715904, 103057, 2007714613645135872, 202601431, 1, '2026-01-30 17:24:26');
INSERT INTO `siargo_qareport` VALUES (2017167480248455168, 102961, 2007693569572065280, 202601432, 1, '2026-01-30 17:26:19');
INSERT INTO `siargo_qareport` VALUES (2017167899490111488, 102961, 2007693569572065280, 202601433, 1, '2026-01-30 17:27:59');
INSERT INTO `siargo_qareport` VALUES (2017168092017053696, 102961, 2007693569572065280, 202601434, 1, '2026-01-30 17:28:44');
INSERT INTO `siargo_qareport` VALUES (2017767717321166848, 102993, 2017767579475365888, 202602001, 1, '2026-02-01 09:11:26');
INSERT INTO `siargo_qareport` VALUES (2017780438733279232, 102939, 2017780321645088768, 202602002, 1, '2026-02-01 10:01:59');
INSERT INTO `siargo_qareport` VALUES (2018149194118582272, 103056, 2000847627364012032, 202602003, 1, '2026-02-02 10:27:17');
INSERT INTO `siargo_qareport` VALUES (2018155580722368512, 103046, 2008069857314983936, 202602004, 1, '2026-02-02 10:52:40');
INSERT INTO `siargo_qareport` VALUES (2018155845726883840, 103087, 2007715086636797952, 202602005, 1, '2026-02-02 10:53:43');
INSERT INTO `siargo_qareport` VALUES (2018155995199295488, 103065, 2016329694373072896, 202602006, 2, '2026-02-02 10:54:19');
INSERT INTO `siargo_qareport` VALUES (2018156277400457216, 103044, 2018156212963364864, 202602007, 2, '2026-02-02 10:55:26');
INSERT INTO `siargo_qareport` VALUES (2018156734139191296, 102897, 2018156684621238272, 202602008, 1, '2026-02-02 10:57:15');
INSERT INTO `siargo_qareport` VALUES (2018157061190045696, 103078, 2007714924677943296, 202602009, 1, '2026-02-02 10:58:33');
INSERT INTO `siargo_qareport` VALUES (2018157591371042816, 102952, 2013536659369218048, 202602010, 1, '2026-02-02 11:00:39');
INSERT INTO `siargo_qareport` VALUES (2018159948897046528, 103059, 2007719346942365696, 202602011, 1, '2026-02-02 11:10:02');
INSERT INTO `siargo_qareport` VALUES (2018160214887223296, 103052, 2018160146889166848, 202602012, 2, '2026-02-02 11:11:05');
INSERT INTO `siargo_qareport` VALUES (2018201073015967744, 103093, 2010907066472714240, 202602013, 1, '2026-02-02 13:53:26');
INSERT INTO `siargo_qareport` VALUES (2018201541448421376, 103096, 2018201456949972992, 202602014, 1, '2026-02-02 13:55:18');
INSERT INTO `siargo_qareport` VALUES (2018204829015199744, 103088, 2013536659369218048, 202602015, 2, '2026-02-02 14:08:22');
INSERT INTO `siargo_qareport` VALUES (2018218863009583104, 103089, 2013536659369218048, 202602016, 2, '2026-02-02 15:04:08');
INSERT INTO `siargo_qareport` VALUES (2018243668542869504, 103043, 2008773890749091840, 202602017, 1, '2026-02-02 16:42:42');
INSERT INTO `siargo_qareport` VALUES (2018499134434955264, 103054, 2009520427271835648, 202602018, 1, '2026-02-03 09:37:50');
INSERT INTO `siargo_qareport` VALUES (2018503027369824256, 49286, 2008353170634166272, 202602019, 1, '2026-02-03 09:53:18');
INSERT INTO `siargo_qareport` VALUES (2018523157931872256, 102867, 2011620456857980928, 202602020, 1, '2026-02-03 11:13:17');
INSERT INTO `siargo_qareport` VALUES (2018523371136733184, 102883, 2010907066472714240, 202602021, 1, '2026-02-03 11:14:08');
INSERT INTO `siargo_qareport` VALUES (2018523480423518208, 103070, 2009549818810978304, 202602022, 1, '2026-02-03 11:14:34');
INSERT INTO `siargo_qareport` VALUES (2018523728399159296, 103086, 2018523659239280640, 202602023, 1, '2026-02-03 11:15:33');
INSERT INTO `siargo_qareport` VALUES (2018523880677560320, 103106, 2000847525710860288, 202602024, 1, '2026-02-03 11:16:10');
INSERT INTO `siargo_qareport` VALUES (2018524130221871104, 103066, 2000847563539288064, 202602025, 1, '2026-02-03 11:17:09');
INSERT INTO `siargo_qareport` VALUES (2018524441154015232, 103082, 2018524386963607552, 202602026, 1, '2026-02-03 11:18:23');
INSERT INTO `siargo_qareport` VALUES (2018524469071302656, 102672, 2009149718863400960, 202602027, 1, '2026-02-03 11:18:30');
INSERT INTO `siargo_qareport` VALUES (2018525436621737984, 103092, 2018525252835725312, 202602028, 1, '2026-02-03 11:22:21');
INSERT INTO `siargo_qareport` VALUES (2018553615159775232, 103085, 2000847563539288064, 202602029, 1, '2026-02-03 13:14:19');
INSERT INTO `siargo_qareport` VALUES (2018553927509594112, 103090, 2018553846635024384, 202602030, 1, '2026-02-03 13:15:33');
INSERT INTO `siargo_qareport` VALUES (2018554063308574720, 103095, 2000847525710860288, 202602031, 1, '2026-02-03 13:16:06');
INSERT INTO `siargo_qareport` VALUES (2018554452049252352, 103118, 2000847627364012032, 202602032, 1, '2026-02-03 13:17:38');
INSERT INTO `siargo_qareport` VALUES (2018575883936059392, 103103, 2000847627364012032, 202602033, 1, '2026-02-03 14:42:48');
INSERT INTO `siargo_qareport` VALUES (2018584082487562240, 102956, 2013438920664928256, 202602034, 1, '2026-02-03 15:15:23');
INSERT INTO `siargo_qareport` VALUES (2018595958856470528, 103072, 2007715109323788288, 202602035, 1, '2026-02-03 16:02:34');
INSERT INTO `siargo_qareport` VALUES (2018609272261038080, 103075, 2007693569572065280, 202602036, 1, '2026-02-03 16:55:29');
INSERT INTO `siargo_qareport` VALUES (2018609609105592320, 103100, 2007715086636797952, 202602037, 1, '2026-02-03 16:56:49');
INSERT INTO `siargo_qareport` VALUES (2018859151621214208, 103123, 2018859066908856320, 202602038, 1, '2026-02-04 09:28:24');
INSERT INTO `siargo_qareport` VALUES (2018859477346668544, 103108, 2018859312435023872, 202602039, 1, '2026-02-04 09:29:42');
INSERT INTO `siargo_qareport` VALUES (2018872081867722752, 102933, 2018160146889166848, 202602040, 1, '2026-02-04 10:19:47');
INSERT INTO `siargo_qareport` VALUES (2018933067094020096, 103094, 2000847525710860288, 202602041, 1, '2026-02-04 14:22:07');
INSERT INTO `siargo_qareport` VALUES (2018933274674319360, 103114, 2000847563539288064, 202602042, 1, '2026-02-04 14:22:57');
INSERT INTO `siargo_qareport` VALUES (2018933385571717120, 103098, 2000847563539288064, 202602043, 1, '2026-02-04 14:23:23');
INSERT INTO `siargo_qareport` VALUES (2018934343395561472, 103117, 2018934242254114816, 202602044, 1, '2026-02-04 14:27:12');
INSERT INTO `siargo_qareport` VALUES (2018937358261276672, 102937, 2009149718863400960, 202602045, 1, '2026-02-04 14:39:10');
INSERT INTO `siargo_qareport` VALUES (2018938868244271104, 103138, 2018938756776448000, 202602046, 1, '2026-02-04 14:45:10');
INSERT INTO `siargo_qareport` VALUES (2018958620761116672, 103047, 2008705235667505152, 202602047, 1, '2026-02-04 16:03:40');
INSERT INTO `siargo_qareport` VALUES (2018959013884841984, 103051, 2007711147526836224, 202602048, 1, '2026-02-04 16:05:14');
INSERT INTO `siargo_qareport` VALUES (2018960880576614400, 102982, 2000847525710860288, 202602049, 1, '2026-02-04 16:12:39');
INSERT INTO `siargo_qareport` VALUES (2018968948198723584, 103049, 2018968800659886080, 202602050, 1, '2026-02-04 16:44:42');
INSERT INTO `siargo_qareport` VALUES (2018975933816229888, 103016, 2000847525710860288, 202602051, 2, '2026-02-04 17:12:28');
INSERT INTO `siargo_qareport` VALUES (2019226500362784768, 103101, 2008414381195251712, 202602052, 1, '2026-02-05 09:48:07');
INSERT INTO `siargo_qareport` VALUES (2019227193807065088, 103121, 2000847563539288064, 202602053, 1, '2026-02-05 09:50:53');
INSERT INTO `siargo_qareport` VALUES (2019227511416541184, 103105, 2019227413773144064, 202602054, 2, '2026-02-05 09:52:08');
INSERT INTO `siargo_qareport` VALUES (2019228345374199808, 103153, 2019228297257144320, 202602055, 1, '2026-02-05 09:55:27');
INSERT INTO `siargo_qareport` VALUES (2019228501033209856, 103116, 2007715109323788288, 202602056, 1, '2026-02-05 09:56:04');
INSERT INTO `siargo_qareport` VALUES (2019228645799612416, 103136, 2007715086636797952, 202602057, 1, '2026-02-05 09:56:39');
INSERT INTO `siargo_qareport` VALUES (2019228944824127488, 103097, 2019228867871232000, 202602058, 2, '2026-02-05 09:57:50');
INSERT INTO `siargo_qareport` VALUES (2019229059706114048, 103112, 2007720000314265600, 202602059, 1, '2026-02-05 09:58:17');
INSERT INTO `siargo_qareport` VALUES (2019229225297235968, 103128, 2010907066472714240, 202602060, 1, '2026-02-05 09:58:57');
INSERT INTO `siargo_qareport` VALUES (2019229515874422784, 103130, 2019229459159044096, 202602061, 1, '2026-02-05 10:00:06');
INSERT INTO `siargo_qareport` VALUES (2019230149952524288, 103091, 2013438920664928256, 202602062, 1, '2026-02-05 10:02:37');
INSERT INTO `siargo_qareport` VALUES (2019233606755274752, 49281, 2008353170634166272, 202602063, 1, '2026-02-05 10:16:22');
INSERT INTO `siargo_qareport` VALUES (2019237654195720192, 49285, 2008353170634166272, 202602064, 1, '2026-02-05 10:32:27');
INSERT INTO `siargo_qareport` VALUES (2019251808503713792, 103110, 2007711147526836224, 202602065, 1, '2026-02-05 11:28:41');
INSERT INTO `siargo_qareport` VALUES (2019298750403301376, 102802, 2008070408593330176, 202602066, 1, '2026-02-05 14:35:13');
INSERT INTO `siargo_qareport` VALUES (2019302487163260928, 103037, 2000847627364012032, 202602067, 1, '2026-02-05 14:50:04');
INSERT INTO `siargo_qareport` VALUES (2019320687955398656, 103079, 2007693569572065280, 202602068, 1, '2026-02-05 16:02:23');
INSERT INTO `siargo_qareport` VALUES (2019321011894079488, 103079, 2007693569572065280, 202602069, 1, '2026-02-05 16:03:41');
INSERT INTO `siargo_qareport` VALUES (2019326490997280768, 103104, 2019326389318963200, 202602070, 1, '2026-02-05 16:25:27');
INSERT INTO `siargo_qareport` VALUES (2019326889355497472, 103119, 2019326817842614272, 202602071, 1, '2026-02-05 16:27:02');
INSERT INTO `siargo_qareport` VALUES (2019327098634489856, 103102, 2016681610454093824, 202602072, 1, '2026-02-05 16:27:52');
INSERT INTO `siargo_qareport` VALUES (2019327381376716800, 103107, 2019327318906753024, 202602073, 1, '2026-02-05 16:28:59');
INSERT INTO `siargo_qareport` VALUES (2019578714549768192, 103067, 2019578579988107264, 202602074, 1, '2026-02-06 09:07:42');
INSERT INTO `siargo_qareport` VALUES (2019588107014098944, 103151, 2019587962801344512, 202602075, 1, '2026-02-06 09:45:01');
INSERT INTO `siargo_qareport` VALUES (2019588211628429312, 103125, 2000847563539288064, 202602076, 1, '2026-02-06 09:45:26');
INSERT INTO `siargo_qareport` VALUES (2019590326719467520, 103170, 2014577471767040000, 202602077, 2, '2026-02-06 09:53:50');
INSERT INTO `siargo_qareport` VALUES (2019602626629193728, 103113, 2000847563539288064, 202602078, 1, '2026-02-06 10:42:43');
INSERT INTO `siargo_qareport` VALUES (2019605707794272256, 103160, 2000847525710860288, 202602079, 2, '2026-02-06 10:54:57');
INSERT INTO `siargo_qareport` VALUES (2019612650537603072, 103135, 2007715086636797952, 202602080, 2, '2026-02-06 11:22:33');
INSERT INTO `siargo_qareport` VALUES (2019648079139819520, 103020, 2009149718863400960, 202602081, 1, '2026-02-06 13:43:19');
INSERT INTO `siargo_qareport` VALUES (2019667186782425088, 103154, 2000847627364012032, 202602082, 1, '2026-02-06 14:59:15');
INSERT INTO `siargo_qareport` VALUES (2019674793526022144, 102986, 2010905350503256064, 202602083, 1, '2026-02-06 15:29:29');
INSERT INTO `siargo_qareport` VALUES (2019676426351792128, 103120, 2010907066472714240, 202602084, 1, '2026-02-06 15:35:58');
INSERT INTO `siargo_qareport` VALUES (2019676600335716352, 103121, 2000847563539288064, 202602085, 1, '2026-02-06 15:36:39');
INSERT INTO `siargo_qareport` VALUES (2019680685688475648, 103126, 2009520427271835648, 202602086, 1, '2026-02-06 15:52:53');
INSERT INTO `siargo_qareport` VALUES (2019683705109204992, 103166, 2019683524951265280, 202602087, 1, '2026-02-06 16:04:53');
INSERT INTO `siargo_qareport` VALUES (2019683803671154688, 103146, 2000847563539288064, 202602088, 1, '2026-02-06 16:05:17');
INSERT INTO `siargo_qareport` VALUES (2019683931337379840, 103178, 2008069857314983936, 202602089, 1, '2026-02-06 16:05:47');
INSERT INTO `siargo_qareport` VALUES (2019688585391624192, 103147, 2008721832280969216, 202602090, 1, '2026-02-06 16:24:17');
INSERT INTO `siargo_qareport` VALUES (2019689451326656512, 103139, 2019689399506030592, 202602091, 1, '2026-02-06 16:27:43');
INSERT INTO `siargo_qareport` VALUES (2019689847407366144, 102872, 2008718734284148736, 202602092, 1, '2026-02-06 16:29:18');
INSERT INTO `siargo_qareport` VALUES (2019692067460861952, 103135, 2007715086636797952, 202602093, 1, '2026-02-06 16:38:07');
INSERT INTO `siargo_qareport` VALUES (2019692615778029568, 103172, 2007715022455558144, 202602094, 1, '2026-02-06 16:40:18');
INSERT INTO `siargo_qareport` VALUES (2019701257227063296, 103069, 2019701153103466496, 202602095, 1, '2026-02-06 17:14:38');
INSERT INTO `siargo_qareport` VALUES (2020038716758544384, 103163, 2008722775034679296, 202602096, 1, '2026-02-07 15:35:35');
INSERT INTO `siargo_qareport` VALUES (2020038905577721856, 102959, 2009149718863400960, 202602097, 1, '2026-02-07 15:36:20');
INSERT INTO `siargo_qareport` VALUES (2020050878130409472, 102974, 2009520427271835648, 202602098, 1, '2026-02-07 16:23:54');
INSERT INTO `siargo_qareport` VALUES (2020416476878327808, 103053, 2009812759917481984, 202602099, 1, '2026-02-08 16:36:40');
INSERT INTO `siargo_qareport` VALUES (2020661717296336896, 102959, 2007715022455558144, 202602100, 1, '2026-02-09 08:51:10');
INSERT INTO `siargo_qareport` VALUES (2020674795262038016, 103157, 2008069857314983936, 202602101, 1, '2026-02-09 09:43:08');
INSERT INTO `siargo_qareport` VALUES (2020680966786109440, 103071, 2010907066472714240, 202602102, 1, '2026-02-09 10:07:39');
INSERT INTO `siargo_qareport` VALUES (2020692728608575488, 102835, 2007711100328333312, 202602103, 1, '2026-02-09 10:54:23');
INSERT INTO `siargo_qareport` VALUES (2020692903179702272, 103186, 2007711100328333312, 202602104, 1, '2026-02-09 10:55:05');
INSERT INTO `siargo_qareport` VALUES (2020699103359258624, 103077, 2007711147526836224, 202602105, 1, '2026-02-09 11:19:43');
INSERT INTO `siargo_qareport` VALUES (2020703299613609984, 103175, 2009505047618899968, 202602106, 1, '2026-02-09 11:36:24');
INSERT INTO `siargo_qareport` VALUES (2020764004949807104, 103184, 2018160146889166848, 202602107, 1, '2026-02-09 15:37:37');
INSERT INTO `siargo_qareport` VALUES (2020764519255363584, 103174, 2020764236521525248, 202602108, 1, '2026-02-09 15:39:40');
INSERT INTO `siargo_qareport` VALUES (2020764924760674304, 103183, 2020703920030863360, 202602109, 1, '2026-02-09 15:41:16');
INSERT INTO `siargo_qareport` VALUES (2020765455923138560, 103156, 2020765094432854016, 202602110, 1, '2026-02-09 15:43:23');
INSERT INTO `siargo_qareport` VALUES (2020768025324408832, 103143, 2020767842838630400, 202602111, 1, '2026-02-09 15:53:35');
INSERT INTO `siargo_qareport` VALUES (2020772161432440832, 103129, 2020771962542739456, 202602112, 1, '2026-02-09 16:10:02');
INSERT INTO `siargo_qareport` VALUES (2020773873400532992, 103124, 2016329694373072896, 202602113, 1, '2026-02-09 16:16:50');
INSERT INTO `siargo_qareport` VALUES (2020774368873664512, 103155, 2020774273419694080, 202602114, 1, '2026-02-09 16:18:48');
INSERT INTO `siargo_qareport` VALUES (2020783209237565440, 103081, 2007720000314265600, 202602115, 1, '2026-02-09 16:53:56');
INSERT INTO `siargo_qareport` VALUES (2020784109574279168, 103131, 2011260144535326720, 202602116, 2, '2026-02-09 16:57:30');
INSERT INTO `siargo_qareport` VALUES (2020784278952857600, 103142, 2000847525710860288, 202602117, 1, '2026-02-09 16:58:11');
INSERT INTO `siargo_qareport` VALUES (2020784528060960768, 103168, 2008070408593330176, 202602118, 1, '2026-02-09 16:59:10');
INSERT INTO `siargo_qareport` VALUES (2021055679223943168, 103141, 2000847525710860288, 202602119, 1, '2026-02-10 10:56:37');
INSERT INTO `siargo_qareport` VALUES (2021055830009171968, 103141, 2000847525710860288, 202602120, 1, '2026-02-10 10:57:13');
INSERT INTO `siargo_qareport` VALUES (2021055965132869632, 103141, 2000847525710860288, 202602121, 1, '2026-02-10 10:57:46');
INSERT INTO `siargo_qareport` VALUES (2021056073589182464, 103141, 2000847525710860288, 202602122, 1, '2026-02-10 10:58:12');
INSERT INTO `siargo_qareport` VALUES (2021056297137197056, 103137, 2000847525710860288, 202602123, 1, '2026-02-10 10:59:05');
INSERT INTO `siargo_qareport` VALUES (2021056578482720768, 103132, 2007711147526836224, 202602124, 1, '2026-02-10 11:00:12');
INSERT INTO `siargo_qareport` VALUES (2021056770577649664, 103169, 2008070408593330176, 202602125, 1, '2026-02-10 11:00:58');
INSERT INTO `siargo_qareport` VALUES (2021056923648774144, 103169, 2008070408593330176, 202602126, 1, '2026-02-10 11:01:34');
INSERT INTO `siargo_qareport` VALUES (2021089894950883328, 49271, 2008353170634166272, 202602127, 1, '2026-02-10 13:12:35');
INSERT INTO `siargo_qareport` VALUES (2021099651401109504, 49270, 2008353170634166272, 202602128, 1, '2026-02-10 13:51:21');
INSERT INTO `siargo_qareport` VALUES (2021106185497595904, 103167, 2000847563539288064, 202602129, 1, '2026-02-10 14:17:19');
INSERT INTO `siargo_qareport` VALUES (2021106313310621696, 103177, 2000847563539288064, 202602130, 1, '2026-02-10 14:17:50');
INSERT INTO `siargo_qareport` VALUES (2021106692517646336, 103149, 2021106609860497408, 202602131, 1, '2026-02-10 14:19:20');
INSERT INTO `siargo_qareport` VALUES (2021107031148974080, 103159, 2008069857314983936, 202602132, 1, '2026-02-10 14:20:41');
INSERT INTO `siargo_qareport` VALUES (2021115565622284288, 103134, 2009468509057044480, 202602133, 1, '2026-02-10 14:54:36');
INSERT INTO `siargo_qareport` VALUES (2021116150719303680, 103150, 2021116031584292864, 202602134, 1, '2026-02-10 14:56:55');
INSERT INTO `siargo_qareport` VALUES (2021122550908440576, 49282, 2008353170634166272, 202602135, 1, '2026-02-10 15:22:21');
INSERT INTO `siargo_qareport` VALUES (2021122973283241984, 49280, 2008353170634166272, 202602136, 1, '2026-02-10 15:24:02');
INSERT INTO `siargo_qareport` VALUES (2021137449709588480, 103208, 2007711147526836224, 202602137, 1, '2026-02-10 16:21:33');
INSERT INTO `siargo_qareport` VALUES (2021138180353150976, 102977, 2019587962801344512, 202602138, 1, '2026-02-10 16:24:27');
INSERT INTO `siargo_qareport` VALUES (2021401033165361152, 103213, 2021400966392041472, 202602139, 1, '2026-02-11 09:48:56');
INSERT INTO `siargo_qareport` VALUES (2021425036793860096, 103144, 2008415147318431744, 202602140, 1, '2026-02-11 11:24:19');
INSERT INTO `siargo_qareport` VALUES (2021425178758467584, 103215, 2000847627364012032, 202602141, 1, '2026-02-11 11:24:53');
INSERT INTO `siargo_qareport` VALUES (2021425442357891072, 103181, 2021425362821304320, 202602142, 1, '2026-02-11 11:25:56');
INSERT INTO `siargo_qareport` VALUES (2021425703822413824, 103199, 2021425614123028480, 202602143, 1, '2026-02-11 11:26:58');
INSERT INTO `siargo_qareport` VALUES (2021499301572497408, 103179, 2010907066472714240, 202602144, 1, '2026-02-11 16:19:25');
INSERT INTO `siargo_qareport` VALUES (2021500987842088960, 49272, 2008353170634166272, 202602145, 1, '2026-02-11 16:26:07');
INSERT INTO `siargo_qareport` VALUES (2021507270070620160, 103218, 2015657123667365888, 202602146, 1, '2026-02-11 16:51:05');
INSERT INTO `siargo_qareport` VALUES (2021744005950066688, 103180, 2010625640707772416, 202602147, 1, '2026-02-12 08:31:47');
INSERT INTO `siargo_qareport` VALUES (2021744188603617280, 103196, 2000847525710860288, 202602148, 1, '2026-02-12 08:32:31');
INSERT INTO `siargo_qareport` VALUES (2021744759423225856, 103018, 2021744586248802304, 202602149, 1, '2026-02-12 08:34:47');
INSERT INTO `siargo_qareport` VALUES (2021744997294788608, 103018, 2021744586248802304, 202602150, 1, '2026-02-12 08:35:44');
INSERT INTO `siargo_qareport` VALUES (2021745150227501056, 103018, 2021744586248802304, 202602151, 1, '2026-02-12 08:36:20');
INSERT INTO `siargo_qareport` VALUES (2021745337201184768, 103018, 2021744586248802304, 202602152, 1, '2026-02-12 08:37:05');
INSERT INTO `siargo_qareport` VALUES (2021745516318937088, 103018, 2021744586248802304, 202602153, 1, '2026-02-12 08:37:47');
INSERT INTO `siargo_qareport` VALUES (2021745700851535872, 103018, 2021744586248802304, 202602154, 1, '2026-02-12 08:38:31');
INSERT INTO `siargo_qareport` VALUES (2021745924768649216, 103018, 2021744586248802304, 202602155, 1, '2026-02-12 08:39:25');
INSERT INTO `siargo_qareport` VALUES (2021746072471064576, 103018, 2021744586248802304, 202602156, 1, '2026-02-12 08:40:00');
INSERT INTO `siargo_qareport` VALUES (2021746833733046272, 103189, 2013536659369218048, 202602157, 1, '2026-02-12 08:43:02');
INSERT INTO `siargo_qareport` VALUES (2021746950129176576, 103189, 2013536659369218048, 202602158, 1, '2026-02-12 08:43:29');
INSERT INTO `siargo_qareport` VALUES (2021747165959671808, 103080, 2007711147526836224, 202602159, 1, '2026-02-12 08:44:21');
INSERT INTO `siargo_qareport` VALUES (2021747375708426240, 103187, 2000847563539288064, 202602160, 1, '2026-02-12 08:45:11');
INSERT INTO `siargo_qareport` VALUES (2021772891110887424, 49273, 2008353170634166272, 202602161, 1, '2026-02-12 10:26:34');
INSERT INTO `siargo_qareport` VALUES (2021773718286356480, 103200, 2021773591534489600, 202602162, 1, '2026-02-12 10:29:51');
INSERT INTO `siargo_qareport` VALUES (2021773860993355776, 103222, 2016751049635647488, 202602163, 2, '2026-02-12 10:30:25');
INSERT INTO `siargo_qareport` VALUES (2021773976051503104, 103214, 2011385065982775296, 202602164, 1, '2026-02-12 10:30:53');
INSERT INTO `siargo_qareport` VALUES (2021778090206547968, 103219, 2008417707106357248, 202602165, 1, '2026-02-12 10:47:14');
INSERT INTO `siargo_qareport` VALUES (2021778529945767936, 103195, 2021778350760906752, 202602166, 1, '2026-02-12 10:48:59');
INSERT INTO `siargo_qareport` VALUES (2021779911046189056, 103197, 2007711100328333312, 202602167, 2, '2026-02-12 10:54:28');
INSERT INTO `siargo_qareport` VALUES (2021780109189304320, 103198, 2007711100328333312, 202602168, 1, '2026-02-12 10:55:15');
INSERT INTO `siargo_qareport` VALUES (2021788726936326144, 103228, 2008719311974027264, 202602169, 1, '2026-02-12 11:29:30');
INSERT INTO `siargo_qareport` VALUES (2021818078587637760, 103211, 2021817994084995072, 202602170, 1, '2026-02-12 13:26:08');
INSERT INTO `siargo_qareport` VALUES (2021847278115737600, 103227, 2008070408593330176, 202602171, 1, '2026-02-12 15:22:09');
INSERT INTO `siargo_qareport` VALUES (2021847595276423168, 103221, 2021847436844978176, 202602172, 1, '2026-02-12 15:23:25');
INSERT INTO `siargo_qareport` VALUES (2021853316114141184, 103223, 2019326817842614272, 202602173, 1, '2026-02-12 15:46:09');
INSERT INTO `siargo_qareport` VALUES (2021867904167694336, 103231, 2021867811423244288, 202602174, 1, '2026-02-12 16:44:07');
INSERT INTO `siargo_qareport` VALUES (2021868044454580224, 103194, 2000847525710860288, 202602175, 1, '2026-02-12 16:44:40');
INSERT INTO `siargo_qareport` VALUES (2021868240383102976, 103240, 2019587962801344512, 202602176, 1, '2026-02-12 16:45:27');
INSERT INTO `siargo_qareport` VALUES (2022230316343480320, 103195, 2021778350760906752, 202602177, 1, '2026-02-13 16:44:13');
INSERT INTO `siargo_qareport` VALUES (2022230793114210304, 103220, 2007711147526836224, 202602178, 1, '2026-02-13 16:46:06');
INSERT INTO `siargo_qareport` VALUES (2022231091178229760, 103190, 2007719346942365696, 202602179, 1, '2026-02-13 16:47:18');
INSERT INTO `siargo_qareport` VALUES (2022231339745267712, 103210, 2007715086636797952, 202602180, 1, '2026-02-13 16:48:17');
INSERT INTO `siargo_qareport` VALUES (2026100682212102144, 102630, 2007715086636797952, 202602181, 1, '2026-02-24 09:03:40');
INSERT INTO `siargo_qareport` VALUES (2026101324796252160, 102630, 2007715086636797952, 202602182, 1, '2026-02-24 09:06:13');
INSERT INTO `siargo_qareport` VALUES (2026101759057711104, 102560, 2007715022455558144, 202602183, 1, '2026-02-24 09:07:57');
INSERT INTO `siargo_qareport` VALUES (2026102099060576256, 102560, 2007715022455558144, 202602184, 1, '2026-02-24 09:09:18');
INSERT INTO `siargo_qareport` VALUES (2026116690381492224, 103217, 2016329694373072896, 202602185, 2, '2026-02-24 10:07:17');
INSERT INTO `siargo_qareport` VALUES (2026116912822210560, 103212, 2000847563539288064, 202602186, 2, '2026-02-24 10:08:10');
INSERT INTO `siargo_qareport` VALUES (2026117400833675264, 103201, 2000847563539288064, 202602187, 2, '2026-02-24 10:10:06');
INSERT INTO `siargo_qareport` VALUES (2026173675718168576, 102672, 2009149718863400960, 202602188, 1, '2026-02-24 13:53:43');
INSERT INTO `siargo_qareport` VALUES (2026215259511836672, 103185, 2026215185620783104, 202602189, 1, '2026-02-24 16:38:57');
INSERT INTO `siargo_qareport` VALUES (2026215566497140736, 103205, 2026215499895787520, 202602190, 1, '2026-02-24 16:40:10');
INSERT INTO `siargo_qareport` VALUES (2026215826854367232, 103207, 2026215751734382592, 202602191, 1, '2026-02-24 16:41:13');
INSERT INTO `siargo_qareport` VALUES (2026216197995745280, 103193, 2000847563539288064, 202602192, 2, '2026-02-24 16:42:41');
INSERT INTO `siargo_qareport` VALUES (2026544632832053248, 103192, 2000847563539288064, 202602193, 2, '2026-02-25 14:27:46');
INSERT INTO `siargo_qareport` VALUES (2026566314267365376, 103238, 2010907066472714240, 202602194, 1, '2026-02-25 15:53:55');
INSERT INTO `siargo_qareport` VALUES (2026566518014070784, 103158, 2009468509057044480, 202602195, 1, '2026-02-25 15:54:44');
INSERT INTO `siargo_qareport` VALUES (2026831317633454080, 102532, 2007693569572065280, 202602196, 1, '2026-02-26 09:26:57');
INSERT INTO `siargo_qareport` VALUES (2026848858091147264, 102271, 2007693569572065280, 202602197, 1, '2026-02-26 10:36:39');
INSERT INTO `siargo_qareport` VALUES (2026904938657730560, 103225, 2009520427271835648, 202602198, 1, '2026-02-26 14:19:30');
INSERT INTO `siargo_qareport` VALUES (2026907291578388480, 49275, 2008353170634166272, 202602199, 1, '2026-02-26 14:28:51');
INSERT INTO `siargo_qareport` VALUES (2026907424458133504, 49276, 2008353170634166272, 202602200, 1, '2026-02-26 14:29:22');
INSERT INTO `siargo_qareport` VALUES (2026921381575118848, 103176, 2000847563539288064, 202602201, 1, '2026-02-26 15:24:50');
INSERT INTO `siargo_qareport` VALUES (2026984718254067712, 49277, 2008353170634166272, 202602202, 1, '2026-02-26 19:36:31');
INSERT INTO `siargo_qareport` VALUES (2027192101345546240, 102354, 2009149718863400960, 202602203, 1, '2026-02-27 09:20:35');
INSERT INTO `siargo_qareport` VALUES (2027194864834039808, 103242, 2009468509057044480, 202602204, 1, '2026-02-27 09:31:33');
INSERT INTO `siargo_qareport` VALUES (2027195119868694528, 103285, 2027194992491876352, 202602205, 1, '2026-02-27 09:32:34');
INSERT INTO `siargo_qareport` VALUES (2027200974324355072, 5000107, 2015658945274564608, 202602206, 1, '2026-02-27 09:55:50');
INSERT INTO `siargo_qareport` VALUES (2027203414109048832, 103254, 2008081212201881600, 202602207, 1, '2026-02-27 10:05:32');
INSERT INTO `siargo_qareport` VALUES (2027204352039309312, 103257, 2027204311874654208, 202602208, 1, '2026-02-27 10:09:15');
INSERT INTO `siargo_qareport` VALUES (2027220191429906432, 103271, 2027220142327189504, 202602209, 1, '2026-02-27 11:12:12');
INSERT INTO `siargo_qareport` VALUES (2027220639167664128, 103287, 2027220435169300480, 202602210, 1, '2026-02-27 11:13:58');
INSERT INTO `siargo_qareport` VALUES (2027254393571495936, 103243, 2000847720016187392, 202602211, 1, '2026-02-27 13:28:06');
INSERT INTO `siargo_qareport` VALUES (2027273786221252608, 49283, 2008353170634166272, 202602212, 1, '2026-02-27 14:45:10');
INSERT INTO `siargo_qareport` VALUES (2027293379492958208, 49274, 2008353170634166272, 202602213, 1, '2026-02-27 16:03:01');
INSERT INTO `siargo_qareport` VALUES (2027562242482753536, 103251, 2011385065982775296, 202602214, 2, '2026-02-28 09:51:23');
INSERT INTO `siargo_qareport` VALUES (2027562523870220288, 103252, 2011385065982775296, 202602215, 1, '2026-02-28 09:52:30');
INSERT INTO `siargo_qareport` VALUES (2027565316924362752, 103273, 2027562849968967680, 202602216, 1, '2026-02-28 10:03:36');
INSERT INTO `siargo_qareport` VALUES (2027574051180761088, 103204, 2027573998433193984, 202602217, 1, '2026-02-28 10:38:18');
INSERT INTO `siargo_qareport` VALUES (2027577615168950272, 102957, 2009149718863400960, 202602218, 1, '2026-02-28 10:52:28');
INSERT INTO `siargo_qareport` VALUES (2027635031172567040, 103241, 2008792751502381056, 202602219, 1, '2026-02-28 14:40:37');
INSERT INTO `siargo_qareport` VALUES (2027635178224865280, 103232, 2008792751502381056, 202602220, 1, '2026-02-28 14:41:12');
INSERT INTO `siargo_qareport` VALUES (2027645209427169280, 103255, 2000847563539288064, 202602221, 1, '2026-02-28 15:21:04');
INSERT INTO `siargo_qareport` VALUES (2027659673920720896, 102870, 2008118232144007168, 202602222, 1, '2026-02-28 16:18:33');
INSERT INTO `siargo_qareport` VALUES (2027670210490519552, 103237, 2027670022979964928, 202602223, 1, '2026-02-28 17:00:25');
INSERT INTO `siargo_qareport` VALUES (2027670492289028096, 103292, 2008069857314983936, 202602224, 1, '2026-02-28 17:01:32');
INSERT INTO `siargo_qareport` VALUES (2028277782327185408, 103266, 2000847525710860288, 202603001, 1, '2026-03-02 09:14:41');
INSERT INTO `siargo_qareport` VALUES (2028278085411786752, 103290, 2028277961952448512, 202603002, 1, '2026-03-02 09:15:53');
INSERT INTO `siargo_qareport` VALUES (2028348407376891904, 103297, 2028348306646487040, 202603003, 1, '2026-03-02 13:55:19');
INSERT INTO `siargo_qareport` VALUES (2028348760881221632, 102963, 2007711100328333312, 202603004, 1, '2026-03-02 13:56:44');
INSERT INTO `siargo_qareport` VALUES (2028350360622321664, 103244, 2010889406141812736, 202603005, 1, '2026-03-02 14:03:05');
INSERT INTO `siargo_qareport` VALUES (2028356341569802240, 103314, 2010991169763135488, 202603006, 1, '2026-03-02 14:26:51');
INSERT INTO `siargo_qareport` VALUES (2028371474505388032, 102817, 2009149718863400960, 202603007, 1, '2026-03-02 15:26:59');
INSERT INTO `siargo_qareport` VALUES (2028373841284026368, 103291, 2028373792743346176, 202603008, 1, '2026-03-02 15:36:23');
INSERT INTO `siargo_qareport` VALUES (2028374225259974656, 103280, 2028374060956504064, 202603009, 1, '2026-03-02 15:37:55');
INSERT INTO `siargo_qareport` VALUES (2028374596539764736, 103278, 2028374522959089664, 202603010, 1, '2026-03-02 15:39:23');
INSERT INTO `siargo_qareport` VALUES (2028374987859939328, 103006, 2008718734284148736, 202603011, 1, '2026-03-02 15:40:57');
INSERT INTO `siargo_qareport` VALUES (2028380852369346560, 103294, 2008070408593330176, 202603012, 2, '2026-03-02 16:04:15');
INSERT INTO `siargo_qareport` VALUES (2028394830256525312, 103251, 2011385065982775296, 202603013, 2, '2026-03-02 16:59:47');
INSERT INTO `siargo_qareport` VALUES (2028645006674087936, 103216, 2009812759917481984, 202603014, 1, '2026-03-03 09:33:54');
INSERT INTO `siargo_qareport` VALUES (2028668927955816448, 103286, 2028668810041348096, 202603015, 1, '2026-03-03 11:08:57');
INSERT INTO `siargo_qareport` VALUES (2028672060513701888, 103276, 2028671912924532736, 202603016, 1, '2026-03-03 11:21:24');
INSERT INTO `siargo_qareport` VALUES (2028672526840614912, 103321, 2028672389825286144, 202603017, 1, '2026-03-03 11:23:15');
INSERT INTO `siargo_qareport` VALUES (2028672936590561280, 103295, 2028672835826601984, 202603018, 2, '2026-03-03 11:24:53');
INSERT INTO `siargo_qareport` VALUES (2028673087585505280, 103307, 2007715086636797952, 202603019, 1, '2026-03-03 11:25:29');
INSERT INTO `siargo_qareport` VALUES (2028673494982447104, 103308, 2028673399624945664, 202603020, 1, '2026-03-03 11:27:06');
INSERT INTO `siargo_qareport` VALUES (2028704208025145344, 103263, 2028704009290633216, 202603021, 1, '2026-03-03 13:29:09');
INSERT INTO `siargo_qareport` VALUES (2028704626109173760, 103281, 2028704009290633216, 202603022, 1, '2026-03-03 13:30:49');
INSERT INTO `siargo_qareport` VALUES (2028739014540775424, 103326, 2028738862509838336, 202603023, 1, '2026-03-03 15:47:27');
INSERT INTO `siargo_qareport` VALUES (2028739178559033344, 103264, 2009505047618899968, 202603024, 1, '2026-03-03 15:48:06');
INSERT INTO `siargo_qareport` VALUES (2028743239354142720, 103253, 2000847720016187392, 202603025, 1, '2026-03-03 16:04:15');
INSERT INTO `siargo_qareport` VALUES (2028756121038999552, 49284, 2008353170634166272, 202603026, 1, '2026-03-03 16:55:26');
INSERT INTO `siargo_qareport` VALUES (2029024815963033600, 103303, 2014576433014099968, 202603027, 1, '2026-03-04 10:43:08');
INSERT INTO `siargo_qareport` VALUES (2029030300837597184, 103233, 2027220142327189504, 202603028, 1, '2026-03-04 11:04:55');
INSERT INTO `siargo_qareport` VALUES (2029030512771584000, 103274, 2000847525710860288, 202603029, 1, '2026-03-04 11:05:46');
INSERT INTO `siargo_qareport` VALUES (2029064508196900864, 103203, 2029064336154939392, 202603030, 1, '2026-03-04 13:20:51');
INSERT INTO `siargo_qareport` VALUES (2029072140349591552, 103322, 2029072032761499648, 202603031, 1, '2026-03-04 13:51:11');
INSERT INTO `siargo_qareport` VALUES (2029072420633956352, 103323, 2029072287703879680, 202603032, 1, '2026-03-04 13:52:18');
INSERT INTO `siargo_qareport` VALUES (2029072613253173248, 103298, 2010905350503256064, 202603033, 1, '2026-03-04 13:53:04');
INSERT INTO `siargo_qareport` VALUES (2029077300400476160, 103077, 2007711147526836224, 202603034, 1, '2026-03-04 14:11:41');
INSERT INTO `siargo_qareport` VALUES (2029363731161272320, 49286, 2008353170634166272, 202603035, 1, '2026-03-05 09:09:51');
INSERT INTO `siargo_qareport` VALUES (2029379490553909248, 103247, 2007711147526836224, 202603036, 1, '2026-03-05 10:12:29');
INSERT INTO `siargo_qareport` VALUES (2029382348078698496, 103333, 2007715086636797952, 202603037, 1, '2026-03-05 10:23:50');
INSERT INTO `siargo_qareport` VALUES (2029382866322706432, 103269, 2007715022455558144, 202603038, 1, '2026-03-05 10:25:54');
INSERT INTO `siargo_qareport` VALUES (2029383228836401152, 103269, 2007715022455558144, 202603039, 1, '2026-03-05 10:27:20');
INSERT INTO `siargo_qareport` VALUES (2029383487905976320, 103269, 2007715022455558144, 202603040, 1, '2026-03-05 10:28:22');
INSERT INTO `siargo_qareport` VALUES (2029395682270236672, 103336, 2009505047618899968, 202603041, 1, '2026-03-05 11:16:49');
INSERT INTO `siargo_qareport` VALUES (2029395842228408320, 103336, 2018160146889166848, 202603042, 1, '2026-03-05 11:17:27');
INSERT INTO `siargo_qareport` VALUES (2029396384619024384, 103325, 2009505047618899968, 202603043, 1, '2026-03-05 11:19:37');
INSERT INTO `siargo_qareport` VALUES (2029469874349002752, 103280, 2019587962801344512, 202603044, 1, '2026-03-05 16:11:38');
INSERT INTO `siargo_qareport` VALUES (2029470073318395904, 103310, 2000847563539288064, 202603045, 1, '2026-03-05 16:12:25');
INSERT INTO `siargo_qareport` VALUES (2029471302677286912, 103298, 2010905350503256064, 202603046, 1, '2026-03-05 16:17:18');
INSERT INTO `siargo_qareport` VALUES (2029471464573227008, 103268, 2000847563539288064, 202603047, 1, '2026-03-05 16:17:57');
INSERT INTO `siargo_qareport` VALUES (2029471918715686912, 103301, 2010907066472714240, 202603048, 1, '2026-03-05 16:19:45');
INSERT INTO `siargo_qareport` VALUES (2029472045652103168, 103311, 2000847563539288064, 202603049, 1, '2026-03-05 16:20:16');
INSERT INTO `siargo_qareport` VALUES (2029472352712904704, 103320, 2029472294344970240, 202603050, 1, '2026-03-05 16:21:29');
INSERT INTO `siargo_qareport` VALUES (2029477227651780608, 103324, 2029477137528770560, 202603051, 1, '2026-03-05 16:40:51');
INSERT INTO `siargo_qareport` VALUES (2029477356098146304, 103329, 2009505047618899968, 202603052, 1, '2026-03-05 16:41:22');
INSERT INTO `siargo_qareport` VALUES (2029478574144999424, 103316, 2029478497955467264, 202603053, 1, '2026-03-05 16:46:12');
INSERT INTO `siargo_qareport` VALUES (2029752836361670656, 103338, 2000847525710860288, 202603054, 1, '2026-03-06 10:56:01');
INSERT INTO `siargo_qareport` VALUES (2029753031006736384, 103345, 2000847720016187392, 202603055, 1, '2026-03-06 10:56:48');
INSERT INTO `siargo_qareport` VALUES (2029753152972902400, 103346, 2000847720016187392, 202603056, 1, '2026-03-06 10:57:17');
INSERT INTO `siargo_qareport` VALUES (2029754282335391744, 103339, 2000847525710860288, 202603057, 1, '2026-03-06 11:01:46');
INSERT INTO `siargo_qareport` VALUES (2029754515429642240, 103340, 2029754454410907648, 202603058, 1, '2026-03-06 11:02:42');
INSERT INTO `siargo_qareport` VALUES (2029754642772905984, 103350, 2008070087435472896, 202603059, 1, '2026-03-06 11:03:12');
INSERT INTO `siargo_qareport` VALUES (2029754758644748288, 103337, 2010905350503256064, 202603060, 1, '2026-03-06 11:03:40');
INSERT INTO `siargo_qareport` VALUES (2029754880900321280, 103334, 2013862983799525376, 202603061, 1, '2026-03-06 11:04:09');
INSERT INTO `siargo_qareport` VALUES (2029755015285821440, 103343, 2008719311974027264, 202603062, 1, '2026-03-06 11:04:41');
INSERT INTO `siargo_qareport` VALUES (2029755243246243840, 103358, 2029755191408840704, 202603063, 1, '2026-03-06 11:05:35');
INSERT INTO `siargo_qareport` VALUES (2029761140852903936, 103347, 2010617080661790720, 202603064, 1, '2026-03-06 11:29:01');
INSERT INTO `siargo_qareport` VALUES (2029761346965196800, 103357, 2013124143279362048, 202603065, 1, '2026-03-06 11:29:50');
INSERT INTO `siargo_qareport` VALUES (2029796619627384832, 103304, 2000847563539288064, 202603066, 1, '2026-03-06 13:50:00');
INSERT INTO `siargo_qareport` VALUES (2029813199853703168, 103239, 2009898353339256832, 202603067, 1, '2026-03-06 14:55:53');
INSERT INTO `siargo_qareport` VALUES (2029822128109244416, 102958, 2007715022455558144, 202603068, 1, '2026-03-06 15:31:22');
INSERT INTO `siargo_qareport` VALUES (2029836037826400256, 103040, 2007711100328333312, 202603069, 1, '2026-03-06 16:26:38');
INSERT INTO `siargo_qareport` VALUES (2029846629761011712, 103344, 2000847627364012032, 202603070, 1, '2026-03-06 17:08:43');
INSERT INTO `siargo_qareport` VALUES (2029846821717528576, 103344, 2000847627364012032, 202603071, 1, '2026-03-06 17:09:29');
INSERT INTO `siargo_qareport` VALUES (2030198357480034304, 103353, 2000847525710860288, 202603072, 1, '2026-03-07 16:26:22');
INSERT INTO `siargo_qareport` VALUES (2030198551382708224, 103349, 2010907066472714240, 202603073, 1, '2026-03-07 16:27:08');
INSERT INTO `siargo_qareport` VALUES (2030198920749895680, 103317, 2008069857314983936, 202603074, 1, '2026-03-07 16:28:36');
INSERT INTO `siargo_qareport` VALUES (2030199108331753472, 103370, 2007714677515997184, 202603075, 1, '2026-03-07 16:29:21');
INSERT INTO `siargo_qareport` VALUES (2030199284412829696, 103293, 2000847563539288064, 202603076, 1, '2026-03-07 16:30:03');
INSERT INTO `siargo_qareport` VALUES (2030832527776534528, 103388, 2030832396146692096, 202603077, 1, '2026-03-09 10:26:20');
INSERT INTO `siargo_qareport` VALUES (2030832823386886144, 103161, 2018938756776448000, 202603078, 1, '2026-03-09 10:27:30');
INSERT INTO `siargo_qareport` VALUES (2030843575913009152, 102431, 2009149718863400960, 202603079, 1, '2026-03-09 11:10:14');
INSERT INTO `siargo_qareport` VALUES (2030891928059236352, 103259, 2017767579475365888, 202603080, 1, '2026-03-09 14:22:22');
INSERT INTO `siargo_qareport` VALUES (2030894908766539776, 103348, 2007715109323788288, 202603081, 1, '2026-03-09 14:34:13');
INSERT INTO `siargo_qareport` VALUES (2030895268998533120, 103332, 2019228867871232000, 202603082, 1, '2026-03-09 14:35:38');
INSERT INTO `siargo_qareport` VALUES (2030895429762011136, 103312, 2013084556410605568, 202603083, 1, '2026-03-09 14:36:17');
INSERT INTO `siargo_qareport` VALUES (2030895655512035328, 103356, 2030895596846305280, 202603084, 1, '2026-03-09 14:37:11');
INSERT INTO `siargo_qareport` VALUES (2030903630406471680, 103362, 2030903565537366016, 202603085, 2, '2026-03-09 15:08:52');
INSERT INTO `siargo_qareport` VALUES (2030903707715883008, 103362, 2030903565537366016, 202603086, 2, '2026-03-09 15:09:10');
INSERT INTO `siargo_qareport` VALUES (2030914125691736064, 103354, 2007715109323788288, 202603087, 1, '2026-03-09 15:50:34');
INSERT INTO `siargo_qareport` VALUES (2030914357276037120, 103354, 2007715109323788288, 202603088, 1, '2026-03-09 15:51:29');
INSERT INTO `siargo_qareport` VALUES (2030914869643825152, 103312, 2013084556410605568, 202603089, 1, '2026-03-09 15:53:32');
INSERT INTO `siargo_qareport` VALUES (2030915200377278464, 103312, 2013084556410605568, 202603090, 1, '2026-03-09 15:54:51');
INSERT INTO `siargo_qareport` VALUES (2030923086063063040, 103253, 2000847720016187392, 202603091, 1, '2026-03-09 16:26:11');
INSERT INTO `siargo_qareport` VALUES (2030928241793814528, 103300, 2030928133987618816, 202603092, 1, '2026-03-09 16:46:40');
INSERT INTO `siargo_qareport` VALUES (2030928627715919872, 103380, 2030928528445132800, 202603093, 1, '2026-03-09 16:48:12');
INSERT INTO `siargo_qareport` VALUES (2030928868162785280, 103368, 2013138726744346624, 202603094, 1, '2026-03-09 16:49:09');
INSERT INTO `siargo_qareport` VALUES (2030929057573359616, 103365, 2009468509057044480, 202603095, 1, '2026-03-09 16:49:54');
INSERT INTO `siargo_qareport` VALUES (2031203919785218048, 103336, 2018160146889166848, 202603096, 1, '2026-03-10 11:02:07');
INSERT INTO `siargo_qareport` VALUES (2031204614575869952, 103401, 2031204551891996672, 202603097, 1, '2026-03-10 11:04:52');
INSERT INTO `siargo_qareport` VALUES (2031204809673920512, 103325, 2009505047618899968, 202603098, 1, '2026-03-10 11:05:39');
INSERT INTO `siargo_qareport` VALUES (2031205266483957760, 103374, 2031205218773749760, 202603099, 2, '2026-03-10 11:07:28');
INSERT INTO `siargo_qareport` VALUES (2031205446784503808, 103390, 2009553606242324480, 202603100, 2, '2026-03-10 11:08:11');
INSERT INTO `siargo_qareport` VALUES (2031205568226381824, 103392, 2018160146889166848, 202603101, 2, '2026-03-10 11:08:40');
INSERT INTO `siargo_qareport` VALUES (2031235847326388224, 103369, 2031235790757810176, 202603102, 1, '2026-03-10 13:08:59');
INSERT INTO `siargo_qareport` VALUES (2031239265621233664, 103363, 2031238998821556224, 202603103, 2, '2026-03-10 13:22:34');
INSERT INTO `siargo_qareport` VALUES (2031239581334884352, 103420, 2031239490381402112, 202603104, 1, '2026-03-10 13:23:49');
INSERT INTO `siargo_qareport` VALUES (2031239821169381376, 103381, 2007711232994168832, 202603105, 1, '2026-03-10 13:24:46');
INSERT INTO `siargo_qareport` VALUES (2031242764589322240, 103331, 2000847563539288064, 202603106, 1, '2026-03-10 13:36:28');
INSERT INTO `siargo_qareport` VALUES (2031244287897620480, 103342, 2000847563539288064, 202603107, 1, '2026-03-10 13:42:31');
INSERT INTO `siargo_qareport` VALUES (2031252903094505472, 103328, 2009505047618899968, 202603108, 1, '2026-03-10 14:16:45');
INSERT INTO `siargo_qareport` VALUES (2031255702549155840, 103428, 2014585695568252928, 202603109, 1, '2026-03-10 14:27:53');
INSERT INTO `siargo_qareport` VALUES (2031255835802193920, 103419, 2008415147318431744, 202603110, 1, '2026-03-10 14:28:24');
INSERT INTO `siargo_qareport` VALUES (2031537569563267072, 103372, 2013536659369218048, 202603111, 1, '2026-03-11 09:07:55');
INSERT INTO `siargo_qareport` VALUES (2031543453349695488, 103435, 2031543168124440576, 202603112, 1, '2026-03-11 09:31:18');
INSERT INTO `siargo_qareport` VALUES (2031546235091800064, 103302, 2009149718863400960, 202603113, 1, '2026-03-11 09:42:21');
INSERT INTO `siargo_qareport` VALUES (2031547426605486080, 102937, 2009149718863400960, 202603114, 1, '2026-03-11 09:47:05');
INSERT INTO `siargo_qareport` VALUES (2031559111462473728, 103389, 2008081212201881600, 202603115, 1, '2026-03-11 10:33:31');
INSERT INTO `siargo_qareport` VALUES (2031612392926728192, 103283, 2031612311007776768, 202603116, 1, '2026-03-11 14:05:14');
INSERT INTO `siargo_qareport` VALUES (2031635026414522368, 49281, 2008353170634166272, 202603117, 1, '2026-03-11 15:35:10');
INSERT INTO `siargo_qareport` VALUES (2031635290584371200, 49285, 2008353170634166272, 202603118, 1, '2026-03-11 15:36:13');
INSERT INTO `siargo_qareport` VALUES (2031642765333221376, 103400, 2031642703622426624, 202603119, 1, '2026-03-11 16:05:56');
INSERT INTO `siargo_qareport` VALUES (2031642931872256000, 103408, 2007720000314265600, 202603120, 1, '2026-03-11 16:06:35');
INSERT INTO `siargo_qareport` VALUES (2031643069156020224, 103404, 2008070674134716416, 202603121, 1, '2026-03-11 16:07:08');
INSERT INTO `siargo_qareport` VALUES (2031643269991878656, 103418, 2031643224043278336, 202603122, 1, '2026-03-11 16:07:56');
INSERT INTO `siargo_qareport` VALUES (2031643452452491264, 103383, 2008802763016622080, 202603123, 1, '2026-03-11 16:08:39');
INSERT INTO `siargo_qareport` VALUES (2031657939041636352, 103362, 2030903565537366016, 202603124, 2, '2026-03-11 17:06:13');
INSERT INTO `siargo_qareport` VALUES (2031899856480358400, 103409, 2007715022455558144, 202603125, 1, '2026-03-12 09:07:31');
INSERT INTO `siargo_qareport` VALUES (2031900772478603264, 103397, 2008070408593330176, 202603126, 1, '2026-03-12 09:11:09');
INSERT INTO `siargo_qareport` VALUES (2031901501679325184, 103397, 2008070408593330176, 202603127, 1, '2026-03-12 09:14:03');
INSERT INTO `siargo_qareport` VALUES (2031902797606014976, 103397, 2008070408593330176, 202603128, 1, '2026-03-12 09:19:12');
INSERT INTO `siargo_qareport` VALUES (2031903221167804416, 103426, 2009505047618899968, 202603129, 1, '2026-03-12 09:20:53');
INSERT INTO `siargo_qareport` VALUES (2031903451833552896, 103377, 2009149718863400960, 202603130, 1, '2026-03-12 09:21:48');
INSERT INTO `siargo_qareport` VALUES (2031903746089144320, 103456, 2031903675784220672, 202603131, 1, '2026-03-12 09:22:58');
INSERT INTO `siargo_qareport` VALUES (2031903866650218496, 103385, 2031903675784220672, 202603132, 1, '2026-03-12 09:23:27');
INSERT INTO `siargo_qareport` VALUES (2031904002612776960, 103406, 2008070087435472896, 202603133, 1, '2026-03-12 09:23:59');
INSERT INTO `siargo_qareport` VALUES (2031907319892004864, 103365, 2009468509057044480, 202603134, 1, '2026-03-12 09:37:10');
INSERT INTO `siargo_qareport` VALUES (2031908470687387648, 103398, 2008070408593330176, 202603135, 1, '2026-03-12 09:41:45');
INSERT INTO `siargo_qareport` VALUES (2031910884794552320, 103391, 2031910755786149888, 202603136, 1, '2026-03-12 09:51:20');
INSERT INTO `siargo_qareport` VALUES (2031914819026538496, 103375, 2000847563539288064, 202603137, 1, '2026-03-12 10:06:58');
INSERT INTO `siargo_qareport` VALUES (2031926649333600256, 103373, 2000847563539288064, 202603138, 1, '2026-03-12 10:53:59');
INSERT INTO `siargo_qareport` VALUES (2031931338494169088, 103378, 2021425362821304320, 202603139, 2, '2026-03-12 11:12:37');
INSERT INTO `siargo_qareport` VALUES (2031970023176523776, 103360, 2027670022979964928, 202603140, 1, '2026-03-12 13:46:20');

SET FOREIGN_KEY_CHECKS = 1;
