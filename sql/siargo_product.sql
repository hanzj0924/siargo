-- =============================================
-- siargo_product 表结构同步脚本
-- 用途：将生产环境表结构同步到测试环境版本
-- 创建时间：2024-01-12
-- 说明：添加生产环境缺失的9个字段
-- =============================================

-- 1. 备份当前表结构（建议在生产环境执行前手动备份）
-- CREATE TABLE siargo_product_backup_20260112 AS SELECT * FROM siargo_product;

-- 2. 添加缺失字段
-- 注意：如果字段已存在，会报错，可以忽略或先删除后添加

-- 检查并添加 `cuc` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `cuc` double(4,2) DEFAULT NULL COMMENT '整机最大电流 8v(Complete Unit Current)' AFTER `pdfver`;

-- 检查并添加 `cucmax` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `cucmax` double(4,2) DEFAULT NULL COMMENT '整机电流24v' AFTER `cuc`;

-- 检查并添加 `cucmin` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `cucmin` double(4,2) DEFAULT NULL COMMENT '整机电流12v' AFTER `cucmax`;

-- 检查并添加 `pv` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `pv` double(4,2) DEFAULT NULL COMMENT '脉冲电压(Pulse Voltage)' AFTER `cucmin`;

-- 检查并添加 `thv` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `thv` int DEFAULT NULL COMMENT '热头电压(Thermal Head Voltage)' AFTER `pv`;

-- 检查并添加 `zp` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `zp` int DEFAULT NULL COMMENT '零点内码(Zero Point)' AFTER `thv`;

-- 检查并添加 `fl` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `fl` double(4,2) DEFAULT NULL COMMENT '故障电平(Fault Level)' AFTER `zp`;

-- 检查并添加 `bv` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `bv` double(5,4) DEFAULT NULL COMMENT '电池电压(Battery Voltage)' AFTER `fl`;

-- 检查并添加 `la` 字段
ALTER TABLE `siargo_product` 
ADD COLUMN `la` int DEFAULT NULL COMMENT '本地地址(Local Address)' AFTER `bv`;

-- =============================================
-- 3. 验证脚本（可选）
-- =============================================
/*
-- 查看表结构确认字段已添加
SHOW CREATE TABLE `siargo_product`;

-- 查看所有字段列表
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'siargo_product' 
ORDER BY ORDINAL_POSITION;

-- 查看新增字段是否为空（预期应为空）
SELECT 
  COUNT(*) as total_rows,
  SUM(CASE WHEN `cuc` IS NULL THEN 1 ELSE 0 END) as cuc_null_count,
  SUM(CASE WHEN `cucmax` IS NULL THEN 1 ELSE 0 END) as cucmax_null_count,
  SUM(CASE WHEN `cucmin` IS NULL THEN 1 ELSE 0 END) as cucmin_null_count,
  SUM(CASE WHEN `pv` IS NULL THEN 1 ELSE 0 END) as pv_null_count,
  SUM(CASE WHEN `thv` IS NULL THEN 1 ELSE 0 END) as thv_null_count,
  SUM(CASE WHEN `zp` IS NULL THEN 1 ELSE 0 END) as zp_null_count,
  SUM(CASE WHEN `fl` IS NULL THEN 1 ELSE 0 END) as fl_null_count,
  SUM(CASE WHEN `bv` IS NULL THEN 1 ELSE 0 END) as bv_null_count,
  SUM(CASE WHEN `la` IS NULL THEN 1 ELSE 0 END) as la_null_count
FROM `siargo_product`;

-- =============================================
-- 4. 回滚脚本（备用）
-- =============================================
/*
-- 如果新增字段有问题，可以执行以下语句回滚：

ALTER TABLE `siargo_product` 
DROP COLUMN `cuc`,
DROP COLUMN `cucmax`,
DROP COLUMN `cucmin`,
DROP COLUMN `pv`,
DROP COLUMN `thv`,
DROP COLUMN `zp`,
DROP COLUMN `fl`,
DROP COLUMN `bv`,
DROP COLUMN `la`;

-- 注意：DROP COLUMN 会删除字段及其数据，请谨慎操作！
*/