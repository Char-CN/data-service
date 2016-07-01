/*
Navicat MySQL Data Transfer

Source Server         : ms
Source Server Version : 50713
Source Host           : 120.24.45.46:3306
Source Database       : dw_realtime

Target Server Type    : MYSQL
Target Server Version : 50713
File Encoding         : 65001

Date: 2016-07-01 17:09:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rt_deal
-- ----------------------------
DROP TABLE IF EXISTS `rt_deal`;
CREATE TABLE `rt_deal` (
  `period_key` bigint(20) unsigned NOT NULL,
  `time_key` bigint(20) unsigned NOT NULL,
  `register_count` bigint(20) DEFAULT NULL,
  `deal_count` float(20,0) DEFAULT NULL,
  `deal_amount` float(20,0) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`period_key`,`time_key`),
  KEY `idx_period_time` (`period_key`,`time_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rt_period
-- ----------------------------
DROP TABLE IF EXISTS `rt_period`;
CREATE TABLE `rt_period` (
  `period_key` bigint(20) unsigned NOT NULL,
  `period_name` varchar(20) DEFAULT NULL,
  `remark` text,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`period_key`),
  KEY `idx_period` (`period_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rt_time
-- ----------------------------
DROP TABLE IF EXISTS `rt_time`;
CREATE TABLE `rt_time` (
  `time_key` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `time_name` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`time_key`),
  KEY `idx_key_name` (`time_key`,`time_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=202106222360 DEFAULT CHARSET=utf8;
