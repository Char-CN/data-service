/*
Navicat MySQL Data Transfer

Source Server         : ms
Source Server Version : 50713
Source Host           : 120.24.45.46:3306
Source Database       : dw_dataservice

Target Server Type    : MYSQL
Target Server Version : 50713
File Encoding         : 65001

Date: 2016-07-01 17:09:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ds_config
-- ----------------------------
DROP TABLE IF EXISTS `ds_config`;
CREATE TABLE `ds_config` (
  `id` bigint(20) NOT NULL,
  `datasource_id` bigint(20) DEFAULT NULL,
  `config_name` varchar(20) DEFAULT NULL,
  `config_type` int(9) DEFAULT NULL,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ds_config
-- ----------------------------
INSERT INTO `ds_config` VALUES ('1', '1', '数据报表', '1', null, '2016-06-30 16:57:27');

-- ----------------------------
-- Table structure for ds_config_detail
-- ----------------------------
DROP TABLE IF EXISTS `ds_config_detail`;
CREATE TABLE `ds_config_detail` (
  `id` bigint(20) NOT NULL,
  `datasource_id` bigint(20) DEFAULT NULL,
  `config_id` bigint(20) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `values` text,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ds_config_detail
-- ----------------------------
INSERT INTO `ds_config_detail` VALUES ('1', null, '1', 'data1', 'select t.time_key,t.time_name,register_count,deal_count,deal_amount\r\nfrom (select time_key,time_name from dw_realtime.rt_time where time_key>=\'2016031600\' and time_key<=\'2016031623\') t\r\nINNER JOIN (select time_key,register_count,deal_count,deal_amount from dw_realtime.rt_deal) d \r\non t.time_key=d.time_key;', '2016-06-30 17:49:57', '2016-06-30 16:58:37');

-- ----------------------------
-- Table structure for ds_datasource
-- ----------------------------
DROP TABLE IF EXISTS `ds_datasource`;
CREATE TABLE `ds_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `database_name` varchar(32) NOT NULL,
  `title` varchar(32) NOT NULL,
  `url` varchar(256) DEFAULT NULL,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `remark` text,
  `enable` tinyint(1) DEFAULT NULL,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_name_title` (`database_name`,`title`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of ds_datasource
-- ----------------------------
INSERT INTO `ds_datasource` VALUES ('1', 'mysql', 'default', '', '', '', '当前数据源，也就是当前库的数据源，由系统配置。该数据不可删除！', '1', '2016-06-30 16:58:06');
