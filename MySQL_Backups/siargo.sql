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

 Date: 31/12/2025 17:16:29
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织机构' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jb_dept
-- ----------------------------
INSERT INTO `jb_dept` VALUES (2001545887328747520, '质管部', '质管部', 0, '2001545887328747520', '6', NULL, NULL, NULL, NULL, NULL, NULL, 2, '2', '1', '2025-12-24 16:55:27', '2025-12-18 14:51:41');

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
INSERT INTO `jb_dictionary` VALUES (1993858758059954176, '字典项1', 1993858758043176960, 0, NULL, '值1', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758068342784, '字典项2', 1993858758043176960, 0, NULL, '值2', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758072537088, '字典项2-1', 1993858758043176960, 1993858758068342784, NULL, '值2-1', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758080925696, '字典项2-2', 1993858758043176960, 1993858758068342784, NULL, '值2-2', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758089314304, '字典项2-2-1', 1993858758043176960, 1993858758080925696, NULL, '值2-2-1', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758093508608, '字典项2-3', 1993858758043176960, 1993858758068342784, NULL, '值2-3', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758097702912, '字典项3', 1993858758043176960, 0, NULL, '值3', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758106091520, '字典项3-1', 1993858758043176960, 1993858758097702912, NULL, '值3-1', 'extend_dic_demo', '1', '0');
INSERT INTO `jb_dictionary` VALUES (1993858758110285824, '字典项4', 1993858758043176960, 0, NULL, '值4', 'extend_dic_demo', '1', '0');

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
INSERT INTO `jb_dictionary_type` VALUES (1993858758043176960, '扩展数据字典Demo', 2, 'extend_dic_demo', '1', '0');

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
INSERT INTO `jb_global_config` VALUES (1992880864943382528, 'SYSTEM_NAME', 'Siargo QC', '2025-11-24 16:59:59', 1992880779681570816, '2025-11-28 14:02:04', 1992880779681570816, '系统名称', 'string', 'admin_login', 1993858739932172288, '1', NULL);
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '全局参数类型分组' ROW_FORMAT = Dynamic;

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
INSERT INTO `jb_online_user` VALUES (2004106698233942017, 'e51133bf6da54512a1ac6fb41b8f9a652004106698233942016', 2001547605315665920, 2004106698095529984, '2025-12-25 16:27:26', '2025-12-25 16:27:26', '2026-01-01 16:27:26', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2004433062287839233, '5f8172d6d3db44b9aeb5338c30c8e40b2004433062287839232', 2003992595003805696, 2004433062103289856, '2025-12-26 14:04:17', '2025-12-26 14:04:17', '2026-01-02 14:04:17', '0', 1, NULL);
INSERT INTO `jb_online_user` VALUES (2006197252572778497, 'c8aa026e7e8d4c58ad64eaf3ae78c1a42006197252572778496', 1992880779681570816, 2006197252396617728, '2025-12-31 10:54:33', '2025-12-31 16:53:58', '2025-12-31 18:54:33', '0', 1, NULL);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内部私信' ROW_FORMAT = Dynamic;

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
INSERT INTO `jb_role_permission` VALUES (2002985239272951808, 2001546465781989376, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2002985239272951809, 2001546465781989376, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2002985239272951810, 2001546465781989376, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2002985239272951811, 2001546465781989376, 1992880780096806912);
INSERT INTO `jb_role_permission` VALUES (2002985239272951812, 2001546465781989376, 1992880780121972736);
INSERT INTO `jb_role_permission` VALUES (2002985239272951813, 2001546465781989376, 1992880780138749952);
INSERT INTO `jb_role_permission` VALUES (2002985239272951814, 2001546465781989376, 1992880780155527168);
INSERT INTO `jb_role_permission` VALUES (2002985239272951815, 2001546465781989376, 1992880780168110080);
INSERT INTO `jb_role_permission` VALUES (2002985239272951816, 2001546465781989376, 1992880780180692992);
INSERT INTO `jb_role_permission` VALUES (2003750372194586624, 2002940469221724160, 1993873896657850368);
INSERT INTO `jb_role_permission` VALUES (2003750372194586625, 2002940469221724160, 1993878316057563136);
INSERT INTO `jb_role_permission` VALUES (2003750372194586626, 2002940469221724160, 1993879334514266112);
INSERT INTO `jb_role_permission` VALUES (2003750372194586627, 2002940469221724160, 1992880779778039808);
INSERT INTO `jb_role_permission` VALUES (2003750372194586628, 2002940469221724160, 1992880779794817024);
INSERT INTO `jb_role_permission` VALUES (2003750372194586629, 2002940469221724160, 1992880779811594240);
INSERT INTO `jb_role_permission` VALUES (2003750372194586630, 2002940469221724160, 1992880779832565760);
INSERT INTO `jb_role_permission` VALUES (2003750372194586631, 2002940469221724160, 1992880779849342976);
INSERT INTO `jb_role_permission` VALUES (2003750372194586632, 2002940469221724160, 1992880779866120192);
INSERT INTO `jb_role_permission` VALUES (2003750372194586633, 2002940469221724160, 1992880779878703104);
INSERT INTO `jb_role_permission` VALUES (2003750372194586634, 2002940469221724160, 1992880779899674624);
INSERT INTO `jb_role_permission` VALUES (2003750372194586635, 2002940469221724160, 1992880779945811968);
INSERT INTO `jb_role_permission` VALUES (2003750372194586636, 2002940469221724160, 1992880779958394880);
INSERT INTO `jb_role_permission` VALUES (2003750372194586637, 2002940469221724160, 1992880779979366400);
INSERT INTO `jb_role_permission` VALUES (2003750372194586638, 2002940469221724160, 1992880780004532224);
INSERT INTO `jb_role_permission` VALUES (2003750372194586639, 2002940469221724160, 1992880780096806912);
INSERT INTO `jb_role_permission` VALUES (2003750372194586640, 2002940469221724160, 1992880780121972736);
INSERT INTO `jb_role_permission` VALUES (2003750372194586641, 2002940469221724160, 1992880780138749952);
INSERT INTO `jb_role_permission` VALUES (2003750372194586642, 2002940469221724160, 1992880780155527168);
INSERT INTO `jb_role_permission` VALUES (2003750372194586643, 2002940469221724160, 1992880780168110080);
INSERT INTO `jb_role_permission` VALUES (2003750372194586644, 2002940469221724160, 1992880780180692992);
INSERT INTO `jb_role_permission` VALUES (2003750372194586645, 2002940469221724160, 1992880780197470208);
INSERT INTO `jb_role_permission` VALUES (2003750372194586646, 2002940469221724160, 1992880780214247424);
INSERT INTO `jb_role_permission` VALUES (2003750372194586647, 2002940469221724160, 1992880780444934144);
INSERT INTO `jb_role_permission` VALUES (2003750372194586648, 2002940469221724160, 1992880780461711360);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统通知' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知阅读用户关系表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '待办事项' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '顶部导航' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '顶部菜单对应左侧一级导航中间表' ROW_FORMAT = Dynamic;

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
INSERT INTO `jb_user` VALUES (1992880779681570816, 'admin', '1e9b5583157ddafe0139b1edd99381580682cbc533e9522dcfb3d5ab0b74639e', '总管理', NULL, NULL, '2025-11-24 16:59:38', NULL, NULL, '1', 1, 0, 'zgl,zongguanli', NULL, '1', 0, 'K9sIEthRzrDqIpHEOZeyQVVLxR9hqUqB', '127.0.0.1', '2025-12-31 10:54:33', NULL, NULL, NULL, '0', NULL, NULL, NULL, '2025-12-31 10:54:33', 0, '2025-12-22 11:18:29', 1, NULL);
INSERT INTO `jb_user` VALUES (2001546811778514944, 'hanzijin', 'adef7b7da197d6289ffa68e09b48d2c4222c8f2e4df956efffed841e71ff6c4b', '韩子衿', NULL, 'upload/user/avatar/20251218/c8e7ebc6659943388f2942e6fef13ff9.jpg', '2025-12-18 14:55:22', NULL, NULL, '1', 1, NULL, 'hzj,hanzijin', '2001546465781989376', '0', 1992880779681570816, 'HLfgOmdKIMgr8Bgf9ujYqHaz5WrZY2Gw', '192.168.3.31', '2025-12-26 09:32:09', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2025-12-26 09:32:09', 1992880779681570816, '2025-12-18 14:55:55', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547415494049792, 'fengying', '1ebd17b6c2d873a520dd89d1ce61df4b1d3b9e35fe1b532883ab3c154e5a2849', '冯英', NULL, 'assets/img/nv.png', '2025-12-18 14:57:45', NULL, NULL, '1', 2, NULL, 'fy,fengying', '2002940469221724160', '0', 1992880779681570816, 'vjrc6bdLpItUduWxMX65qKFW0a9fDJSS', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545737889890304', '2025-12-26 09:39:27', 2003992595003805696, '2025-12-26 09:39:27', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547605315665920, 'wuxiaoyu', '516e1cb55967ce54002ad3ba5f68709bbe41e7f92c20e7244de38e91a5710f7d', '吴小雨', NULL, 'assets/img/nv.png', '2025-12-18 14:58:31', NULL, NULL, '1', 2, NULL, 'wxy,wuxiaoyu', '2001546465781989376', '0', 1992880779681570816, 'bL5FLrkc3nASgeR5M7tp1iy9xxjyQkZX', '192.168.77.81', '2025-12-25 16:27:26', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545773755383808', '2025-12-25 16:27:26', 1992880779681570816, '2025-12-18 14:58:31', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001547886069792768, 'liangpeng', '77ee5afdcd4982257eaa2c84dd2712ad6ec0f1fd22990221b4adb1c5c592fd17', '梁鹏', NULL, 'assets/img/nan.png', '2025-12-18 14:59:38', NULL, NULL, '1', 1, NULL, 'lp,liangpeng', '2001546465781989376', '0', 1992880779681570816, 'Cgm5O9cNmImm6cAs2ofNkQlNlys6MIXk', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2025-12-18 14:59:38', 1992880779681570816, '2025-12-18 14:59:38', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548007356481536, 'luoxiongfei', '0fa0eeac4e38f32dce622b91b78cbc462925030163f6af0c7714b2f04957c743', '罗雄飞', NULL, 'assets/img/nv.png', '2025-12-18 15:00:07', NULL, NULL, '1', 2, NULL, 'lxf,luoxiongfei', '2001546465781989376', '0', 1992880779681570816, 'st4WHUfysHBHQGfWgoxCdFSppdM3ptx8', '192.168.3.30', '2025-12-22 16:34:57', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2025-12-22 16:34:58', 1992880779681570816, '2025-12-18 15:00:07', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548170967891968, 'shizhenyun', '696883428e592b11c7d3368e27ffe7cd2bf7613463e67663f3362c0c842b88db', '石珍云', NULL, 'assets/img/nv.png', '2025-12-18 15:00:46', NULL, NULL, '1', 2, NULL, 'szy,shizhenyun', '2001546465781989376', '0', 1992880779681570816, '7RRLBDgyDQIafysHBRyTbkKMUraBaBcW', NULL, NULL, NULL, NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2025-12-18 15:00:46', 1992880779681570816, '2025-12-18 15:00:46', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2001548276093927424, 'houliang', '4929e03616218c998ceae379dc15c92251a4c878b816027f1db147d13df55c6e', '侯亮', NULL, 'assets/img/nan.png', '2025-12-18 15:01:11', NULL, NULL, '1', 1, NULL, 'hl,houliang', '2001546465781989376', '0', 1992880779681570816, 'K0mhl2XI7cnGYRhH4G3UJr5svTgu3DTw', '192.168.3.12', '2025-12-22 16:36:39', '内网IP', NULL, NULL, '0', 2001545887328747520, '2001545887328747520', '2001545808043819008', '2025-12-22 16:36:39', 1992880779681570816, '2025-12-18 15:01:11', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2002984611549220864, 'siargo', '881459680bf0938ef4bf00d68dc41bbaa3f7a81d8a47429e9ff61f6757ade05a', 'siargo', NULL, 'assets/img/nan.png', '2025-12-22 14:08:40', NULL, NULL, '1', 0, NULL, '', '2002940469221724160', '0', 1992880779681570816, '1Krjxqs2GQF7dchOXwN4rGOIONUK77fG', '127.0.0.1', '2025-12-29 08:57:40', NULL, NULL, NULL, '0', NULL, '2003751330882457600', NULL, '2025-12-29 08:57:40', 2002984611549220864, '2025-12-24 16:57:07', 1, '1992880780549791744');
INSERT INTO `jb_user` VALUES (2003992595003805696, 'wuyong', '69138627d70227a9f3f00e791558a31550823e2adc35c5f8462fb751fe8ab136', '吴勇', NULL, 'assets/img/nan.png', '2025-12-25 08:54:02', NULL, NULL, '1', 1, NULL, 'wy,wuyong', '2002940469221724160', '0', 2002984611549220864, 'MLec0kYhZVJhwbGE7CJqEmrkBcnXMvJv', '192.168.9.11', '2025-12-26 14:04:17', '内网IP', NULL, NULL, '0', NULL, NULL, NULL, '2025-12-26 14:04:17', 2002984611549220864, '2025-12-25 08:54:02', 1, '1992880780549791744');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户扩展信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jb_user_extend
-- ----------------------------
INSERT INTO `jb_user_extend` VALUES (1992880779681570816, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001546811778514944, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001547415494049792, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001547605315665920, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001547886069792768, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001548007356481536, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001548170967891968, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2001548276093927424, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2002984611549220864, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);
INSERT INTO `jb_user_extend` VALUES (2003992595003805696, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', NULL);

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
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2000847720016187393 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户名称(Customer Name)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of siargo_customer
-- ----------------------------
INSERT INTO `siargo_customer` VALUES (2000847525710860288, '重庆捷定力');
INSERT INTO `siargo_customer` VALUES (2000847563539288064, '深圳堡森');
INSERT INTO `siargo_customer` VALUES (2000847627364012032, '常州斯尔特');
INSERT INTO `siargo_customer` VALUES (2000847720016187392, '常州爱德克斯');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'siargo数据字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of siargo_dictionary
-- ----------------------------
INSERT INTO `siargo_dictionary` VALUES (1, 1, '1', '传感器');
INSERT INTO `siargo_dictionary` VALUES (2, 1, '2', '小流量计');
INSERT INTO `siargo_dictionary` VALUES (3, 1, '3', '大流量计');
INSERT INTO `siargo_dictionary` VALUES (4, 2, '1', '产成品');
INSERT INTO `siargo_dictionary` VALUES (5, 2, '2', '返修品');
INSERT INTO `siargo_dictionary` VALUES (6, 3, '1', '精度待检');
INSERT INTO `siargo_dictionary` VALUES (7, 3, '2', '成品待检');
INSERT INTO `siargo_dictionary` VALUES (8, 3, '3', '包装待检');
INSERT INTO `siargo_dictionary` VALUES (9, 3, '4', '合格待批准');
INSERT INTO `siargo_dictionary` VALUES (10, 3, '5', '已完成');
INSERT INTO `siargo_dictionary` VALUES (11, 4, '1', '1-70m3/h');
INSERT INTO `siargo_dictionary` VALUES (12, 4, '2', '1-100m3/h');

-- ----------------------------
-- Table structure for siargo_dictionarytype
-- ----------------------------
DROP TABLE IF EXISTS `siargo_dictionarytype`;
CREATE TABLE `siargo_dictionarytype`  (
  `id` bigint NOT NULL,
  `dict_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'siargo数据字典类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of siargo_dictionarytype
-- ----------------------------
INSERT INTO `siargo_dictionarytype` VALUES (1, 'prod_type', '产品类型');
INSERT INTO `siargo_dictionarytype` VALUES (2, 'rep_type', '报告单类型');
INSERT INTO `siargo_dictionarytype` VALUES (3, 'insp', '检验进度');
INSERT INTO `siargo_dictionarytype` VALUES (4, 'flow_range', '流量范围');

-- ----------------------------
-- Table structure for siargo_product
-- ----------------------------
DROP TABLE IF EXISTS `siargo_product`;
CREATE TABLE `siargo_product`  (
  `id` bigint NOT NULL COMMENT '产品ID',
  `report_id` bigint NOT NULL COMMENT '检验报告单ID',
  `type` int(1) UNSIGNED ZEROFILL NOT NULL COMMENT '产品类型:\r\n0小流量, \r\n1传感器, \r\n2大流量',
  `modle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品型号',
  `qsi` int NOT NULL COMMENT '送检数量 (Quantity Submitted for Inspection)',
  `qi` int UNSIGNED NOT NULL COMMENT '检验数量 (Quantity Inspected)',
  `number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品编号',
  `flow_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流量范围(Flow Range)',
  `des` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
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
  `cuc` double(5, 2) NULL DEFAULT NULL COMMENT '整机电流 (Complete Unit Current)',
  `cucmax` double(5, 2) NULL DEFAULT NULL COMMENT '整机电流24v',
  `cucmin` double(5, 2) NULL DEFAULT NULL COMMENT '整机电流8v',
  `pv` double(5, 2) NULL DEFAULT NULL COMMENT '脉冲电压(Pulse Voltage)',
  `thv` double(5, 2) NULL DEFAULT NULL COMMENT '热头电压(Thermal Head Voltage)',
  `zp` double(5, 2) NULL DEFAULT NULL COMMENT '零点内码(Zero Point)',
  `fl` double(5, 2) NULL DEFAULT NULL COMMENT '故障电平(Fault Level)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_repid`(`report_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单里的产品型号，跟报告单多对一关系' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of siargo_product
-- ----------------------------
INSERT INTO `siargo_product` VALUES (2004452086719647745, 2004452086719647744, 2, 'MF5619-N-800-ABD-D-O', 1, 1, 'G6HUL22584', NULL, NULL, NULL, 1, 5, '2025-12-26 15:22:16', '2025-12-26 15:22:27', '2025-12-26 15:22:35', '2025-12-26 15:22:55', 1992880779681570816, 1992880779681570816, 1992880779681570816, 1992880779681570816, '/PDF/102592_2004452086719647745.pdf', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2004452086719647746, 2004452086719647744, 2, 'MF5219-E-Q-1000-D', 1, 1, 'G7HUL43595', NULL, NULL, NULL, 1, 5, '2025-12-26 15:22:16', '2025-12-26 15:22:27', '2025-12-26 15:22:35', '2025-12-26 15:22:55', 1992880779681570816, 1992880779681570816, 1992880779681570816, 1992880779681570816, '/PDF/102592_2004452086719647746.pdf', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2004452519781535744, 2004452519773147136, 2, 'MF5712-G-250-B-A', 1, 1, 'PAGSH86558', NULL, 'GCF 908', NULL, 1, 5, '2025-12-26 15:22:16', '2025-12-26 15:22:27', '2025-12-26 15:22:35', '2025-12-26 15:22:55', 1992880779681570816, 1992880779681570816, 1992880779681570816, 1992880779681570816, '/PDF/102570_2004452519781535744.pdf', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `siargo_product` VALUES (2006291730767745024, 2006291730721607680, 3, '1', 1, 1, '1', '2', '1', NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

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
INSERT INTO `siargo_qareport` VALUES (2004452086719647744, 102592, 2000847525710860288, 202512001, 1, '2025-12-26 15:19:53');
INSERT INTO `siargo_qareport` VALUES (2004452519773147136, 102570, 2000847627364012032, 202512002, 2, '2025-12-26 15:21:36');
INSERT INTO `siargo_qareport` VALUES (2006291730721607680, 12, 2000847563539288064, 202512003, 1, '2025-12-31 17:09:58');

SET FOREIGN_KEY_CHECKS = 1;
