#!/bin/sh
source ~/.bash_profile

############################################################################# 配置信息
debug=true;

java_path="/usr/bin/java";

mysql_path="/usr/local/mysql/bin/mysql";

hive_path="/Users/hyy/Work/hive-1.2.1-bin/bin/hive";

python_path="/usr/bin/python";

zip_path="/usr/bin/zip";

data_service_util_jar="data-service-util-0.0.1-SNAPSHOT.jar";

email_util="email_util.py";

conn="${mysql_path} -h172.16.52.137 -uroot -p123456 -Ddw_dataservice -N -e";

get_mails_url="http://bigdata.blazer.org:8030/userservice/getmailsbyuserids.do";

import_database="excel";
