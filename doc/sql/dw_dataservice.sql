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
  PRIMARY KEY (`id`),
  KEY `IDX_DATASOURCE_ID` (`datasource_id`),
  KEY `IDX_GROUP_ID` (`group_id`),
  KEY `IDX_USER_ID` (`user_id`)
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
  PRIMARY KEY (`id`),
  KEY `IDX_DATASOURCE_ID` (`datasource_id`),
  KEY `IDX_CONFIG_ID` (`config_id`)
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
  PRIMARY KEY (`id`),
  KEY `IDX_PARENT_ID` (`parent_id`)
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

/*Table structure for table `mapping_config_job` */

DROP TABLE IF EXISTS `mapping_config_job`;

CREATE TABLE `mapping_config_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `config_id` bigint(20) DEFAULT NULL,
  `job_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `result_mode` int(9) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `email_userids` varchar(256) DEFAULT NULL,
  `enable` int(9) DEFAULT '1',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IDX_CONFIG_ID` (`config_id`),
  KEY `IDX_USER_ID` (`user_id`),
  KEY `IDX_JOB_ID` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

/*Table structure for table `mapping_user_task` */

DROP TABLE IF EXISTS `mapping_user_task`;

CREATE TABLE `mapping_user_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IDX_USER_ID` (`user_id`),
  KEY `IDX_TASK_NAME` (`task_name`)
) ENGINE=InnoDB AUTO_INCREMENT=953 DEFAULT CHARSET=utf8;

/*Table structure for table `scheduler_job` */

DROP TABLE IF EXISTS `scheduler_job`;

CREATE TABLE `scheduler_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(9) DEFAULT '1',
  `job_name` varchar(50) DEFAULT NULL,
  `cron` varchar(20) DEFAULT NULL,
  `command` varchar(512) DEFAULT NULL,
  `remark` varchar(512) DEFAULT NULL,
  `enable` int(9) DEFAULT '1',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

/*Table structure for table `scheduler_job_param` */

DROP TABLE IF EXISTS `scheduler_job_param`;

CREATE TABLE `scheduler_job_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) DEFAULT NULL,
  `param_name` varchar(50) DEFAULT NULL,
  `title_name` varchar(50) DEFAULT NULL,
  `default_value` varchar(100) DEFAULT NULL,
  `enable` int(9) DEFAULT '1',
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_ID` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `scheduler_status` */

DROP TABLE IF EXISTS `scheduler_status`;

CREATE TABLE `scheduler_status` (
  `id` bigint(20) DEFAULT NULL,
  `status_name` varchar(20) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `scheduler_task` */

DROP TABLE IF EXISTS `scheduler_task`;

CREATE TABLE `scheduler_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `type_name` varchar(20) DEFAULT NULL COMMENT 'cron_auto|right_now',
  `task_name` varchar(100) DEFAULT NULL COMMENT '以 yyyy_MM_dd_HH_mm_JobId_[cron_auto|right_now]_01 格式来定义task_name',
  `execute_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `command` varchar(512) DEFAULT NULL,
  `params` varchar(512) DEFAULT NULL,
  `log_path` varchar(512) DEFAULT NULL,
  `error_log_path` varchar(512) DEFAULT NULL,
  `exception` text,
  `remark` varchar(255) DEFAULT NULL,
  `mtime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_TASK_NAME` (`task_name`) USING BTREE,
  KEY `IDX_EXECUTE_TIME` (`execute_time`),
  KEY `IDX_END_TIME` (`end_time`),
  KEY `IDX_JOB_ID` (`job_id`),
  KEY `IDX_STATUS_ID` (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1459 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
