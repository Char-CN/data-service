/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.13-log : Database - dw_dataservice
*********************************************************************
*/
/*Table structure for table `ds_config` */

DROP TABLE IF EXISTS `ds_config`;

CREATE TABLE `ds_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) DEFAULT NULL,
  `datasource_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `config_name` varchar(50) DEFAULT NULL,
  `config_type` int(9) DEFAULT NULL,
  `order_asc` int(9) DEFAULT NULL,
  `remark` text,
  `enable` int(9) DEFAULT '1',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8;

/*Table structure for table `ds_config_detail` */

DROP TABLE IF EXISTS `ds_config_detail`;

CREATE TABLE `ds_config_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_id` bigint(20) DEFAULT NULL,
  `config_id` bigint(20) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `values` text,
  `order_asc` int(9) DEFAULT NULL,
  `enable` int(9) DEFAULT '1',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1060 DEFAULT CHARSET=utf8;

/*Table structure for table `ds_datasource` */

DROP TABLE IF EXISTS `ds_datasource`;

CREATE TABLE `ds_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `database_name` varchar(32) NOT NULL,
  `title` varchar(32) NOT NULL,
  `url` varchar(256) DEFAULT NULL,
  `host` varchar(64) DEFAULT NULL,
  `port` varchar(64) DEFAULT NULL,
  `dbname` varchar(64) DEFAULT NULL,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `remark` text,
  `enable` tinyint(1) DEFAULT NULL,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_name_title` (`database_name`,`title`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `ds_group` */

DROP TABLE IF EXISTS `ds_group`;

CREATE TABLE `ds_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `group_name` varchar(20) DEFAULT NULL,
  `order_asc` int(9) DEFAULT NULL,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

/*Table structure for table `ds_upload` */

DROP TABLE IF EXISTS `ds_upload`;

CREATE TABLE `ds_upload` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `task_name` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '此task是上传文件之后，处理文件导入到hive或者mysql或者其他数据库的任务。',
  `file_name` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '系统生成的名称',
  `file_full_name` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '系统生成的带后缀名称',
  `file_suffix` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '后缀名',
  `file_path` varchar(256) CHARACTER SET utf8 DEFAULT NULL COMMENT '上传之后的所在路径',
  `file_old_name` varchar(256) CHARACTER SET utf8 DEFAULT NULL COMMENT '文件的原始名称',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_FILE_NAME` (`file_name`) USING BTREE,
  KEY `IDX_TASK_NAME` (`task_name`),
  KEY `IDX_USER_ID` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=latin1;

/*Table structure for table `ds_user_group` */

DROP TABLE IF EXISTS `ds_user_group`;

CREATE TABLE `ds_user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL COMMENT '该列标识唯一值',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IDX_GROUP_ID` (`group_id`) USING BTREE,
  KEY `IDX_USER_ID` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
