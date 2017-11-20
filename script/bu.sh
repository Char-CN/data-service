#!/bin/sh
source ~/.bash_profile

############################################################################# 配置文件名称
config_file_name="config.sh" 

############################################################################# 获取路径
SOURCE="$0"
while [ -h "$SOURCE"  ]; do
    DIR="$( cd -P "$( dirname "$SOURCE"  )" && pwd  )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /*  ]] && SOURCE="$DIR/$SOURCE"
done
WORKSPACE="$( cd -P "$( dirname "$SOURCE"  )" && pwd  )"
cd ${WORKSPACE} 

############################################################################# 加载配置文件
if [ -f ${WORKSPACE}/${config_file_name} ];
then
  echo "加载配置文件: ${WORKSPACE}/${config_file_name}"
  source ${WORKSPACE}/${config_file_name}
else
  echo "找不到配置文件：${WORKSPACE}/${config_file_name}，请检查！"
  exit 1
fi
echo "debug模式是否打开：$debug"

#############################################################################
if [ "$2" == "" ];
then
  echo "Usage: sh bu.sh 2017_xx_xx_xx_xx_xx_cron_auto_00001 xxx@evergrande.com,ddd@evergrande.com"
  exit -1
fi

taskName=$1
emails=$2

${java_path} -jar "${data_service_util_jar}" "org.blazer.util.EmailUtil_Csv2HtmlTable" "${emails}" "补发" "您好，请查收本次任务的附件" "/home/www/scheduler_result/${taskName}.csv" "/home/www/scheduler_result/${taskName}.zip"
