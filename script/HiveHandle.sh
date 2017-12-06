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
  echo "找不到配置文件：${WORKSPACE}/${config_file_name},请检查!"
  exit 1
fi
echo "debug模式是否打开：$debug"
############################################################################# 获取参数
params_sh_sys_split="^_^"
params_sh_sys_maps=""

for((i=1;i<$#+1;i++))
do
  if [ "${params_sh_sys_maps}" != "" ];
  then
    params_sh_sys_maps="${params_sh_sys_maps}${params_sh_sys_split}"
  fi
  tmp=`eval echo '$'"$i"`
  params_sh_sys_maps="${params_sh_sys_maps}${tmp}"
done

# 获取系统传进来的参数,格式为:sh a.sh key=value
function get() {
  arr=(${params_sh_sys_maps//${params_sh_sys_split}/ })
  for((i=0;i<${#arr[@]};i++))
  do
    val=${arr[$i]}
    val_arr=(${val//=/ })
    # key
    if [ "${val_arr[0]}" == "$1" ];
    then
      echo "${val_arr[1]}"
      return
    fi
  done
  echo ""
  return
}
############################################################################# log函数
function log() {
  if [ $debug == true ];
  then
    echo "echo $1"
  fi
}
############################################################################# 获取传入参数
result_path=$(get result_path)
mapping_config_job_id=$(get mapping_config_job_id)
config_id=$(get config_id)
emails=$(get emails)

echo "################## 传入参数"
echo "SYS_TASK_NAME         : ${SYS_TASK_NAME}";
echo "result_path           : ${result_path}";
echo "mapping_config_job_id : ${mapping_config_job_id}";
echo "config_id             : ${config_id}";
echo "emails                : ${emails}";
############################################################################# 获取数据源信息
$(log "################## config_id存在表示即时查询的任务");
if [ "${config_id}" == "" ];
then
  # 根据mappingid查询实际数据源
  sql="
    SET NAMES utf8;
    select dd.database_name,dd.url,dd.host,dd.port,dd.dbname,dd.username,dd.password,dc.config_name 
    from mapping_config_job mcj
    inner join ds_config dc on mcj.config_id=dc.id
    inner join ds_datasource dd on dc.datasource_id=dd.id
    where dc.enable=1 and mcj.enable=1
    and mcj.id=${mapping_config_job_id}
  ";
else
  # 根据configid查询实际数据源
  sql="
    SET NAMES utf8;
    select dd.database_name,dd.url,dd.host,dd.port,dd.dbname,dd.username,dd.password,dc.config_name 
    from ds_config dc
    inner join ds_datasource dd on dc.datasource_id=dd.id
    where dc.enable=1 and dc.id=${config_id}
  "
fi

$(log "################## 获取datasource信息, 用户生成实际执行命令");
rst=`${conn} "${sql}"`
db_type=`echo $rst | awk -F ' ' '{print $1}'`;
url=`echo $rst | awk -F ' ' '{print $2}'`;
host=`echo $rst | awk -F ' ' '{print $3}'`;
port=`echo $rst | awk -F ' ' '{print $4}'`;
dbname=`echo $rst | awk -F ' ' '{print $5}'`;
username=`echo $rst | awk -F ' ' '{print $6}'`;
password=`echo $rst | awk -F ' ' '{print $7}'`;
config_name=`echo $rst | awk -F ' ' '{print $8}'`;
# 隐藏密码的显示
$(log "db_type     : $db_type");
$(log "url         : $url");
$(log "host        : $host");
$(log "port        : $port");
$(log "dbname      : $dbname");
$(log "username    : $username");
$(log "config_name : $config_name");

############################################################################# 获取实际查询的SQL
if [ "${config_id}" == "" ];
then
  # 根据mappingid查询实际执行sql
  sql="
  SET NAMES utf8;
  select REPLACE(REPLACE(dcd.values, '\n', '\r'), '\t', ' ') from mapping_config_job mcj
    inner join ds_config dc on mcj.config_id=dc.id
    inner join ds_config_detail dcd on dcd.config_id=dc.id
    where dc.enable=1
    and mcj.enable=1
    and dcd.enable=1
    and mcj.id=${mapping_config_job_id}
  ";
else
  # 根据config查询实际执行sql
  sql="
  SET NAMES utf8;
  select REPLACE(REPLACE(dcd.values, '\n', '\r'), '\t', ' ') from ds_config dc
    inner join ds_config_detail dcd on dcd.config_id=dc.id
    where dc.enable=1
    and dcd.enable=1
    and dc.id=${config_id}
  "
fi

$(log "################## 获得实际查询sql的语句");
$(log "${sql}");

query_sql=`${conn} "${sql}"`;

# $(log "################## 实际查询的sql");
# $(log "${query_sql}");

$(log "################## 实际查询的sql参数替换处理");
# 强制替换系统任务名称参数
query_sql=${query_sql//\$\{SYS_TASK_NAME\}/${SYS_TASK_NAME}};
arr=(${params_sh_sys_maps//${params_sh_sys_split}/ })
for((i=0;i<${#arr[@]};i++))
do
  val=${arr[$i]}
  val_arr=(${val//=/ })
  key="${val_arr[0]}"
  value="${val_arr[1]}"
  value_arr=(${value//|/ })
  # 格式[df|today+0|yyyy-MM-dd]
  if [[ ${#value_arr[@]} != 1 ]];
  then
    real_value=`${java_path} -jar ${data_service_util_jar} "org.blazer.dateformat.DateFormat" "${value}"`
    query_sql=${query_sql//\$\{${key}\}/${real_value}};
  else
    query_sql=${query_sql//\$\{${key}\}/${value}};
  fi
done

if [ ${db_type} == "hive" ];
then
  query_sql="set hive.cli.print.header=true; ${query_sql}"
else
  query_sql="SET NAMES utf8; ${query_sql}"
fi

############################################################################# 拼接实际执行的命令
$(log "################## 判断是mysql类型还是hive类型");
exec_cmd=""
if [ ${db_type} == "mysql" ];
then
  # 隐藏密码的显示
  exec_cmd_show="${mysql_path} -h${host} -u${username} -p -P${port} -D${dbname} -n -e"
  exec_cmd="${mysql_path} -h${host} -u${username} -p${password} -P${port} -D${dbname} -n -e"
else
  exec_cmd_show="${hive_path} -e"
  exec_cmd="${hive_path} -e"
fi

echo "################## 执行的命令"
echo "${exec_cmd_show} \"${query_sql}\" > ${result_path}/${SYS_TASK_NAME}.csv";

############################################################################# 执行命令
echo "################## 开始执行查询任务......";
${exec_cmd} "${query_sql}" > ${result_path}/${SYS_TASK_NAME}.csv

if [ "$?" = "0" ];
then
  echo "################## 查询成功.";
  ########################################################################### email信息来自2个字段,需要拼接
  if [ "${emails}" == "" ];
  then
    emails=`${conn} "select ifnull(email,'') from mapping_config_job where id=${mapping_config_job_id}"`
    user_ids=`${conn} "select ifnull(email_userids,'') from mapping_config_job where id=${mapping_config_job_id}"`
    $(log "################## 查询email的命令");
    $(log "curl -d \"userids=${user_ids}\" ${get_mails_url}");
    emails2=`curl -d "userids=${user_ids}" ${get_mails_url}`
    if [[ "${emails}" == "" && "${emails2}" != "" ]];
    then
      emails="${emails2}"
    elif [[ "${emails}" != "" && "${emails2}" == "" ]];
    then
      emails="${emails}"
    elif [[ "${emails}" != "" && "${emails2}" != "" ]];
    then
      emails="${emails},${emails2}"
    else
      echo "################## 没有填写邮箱.";
      exit 1
    fi
  fi
  if [ "${config_id}" == "" ];
  then
    # 根据mappingid查询实际执行的备注信息
    sql="
    SET NAMES utf8;
    select REPLACE(dc.remark, '\n', '</br>') from mapping_config_job mcj
      inner join ds_config dc on mcj.config_id=dc.id
      inner join ds_config_detail dcd on dcd.config_id=dc.id
      where dc.enable=1
      and mcj.enable=1
      and dcd.enable=1
      and mcj.id=${mapping_config_job_id}
    ";
  else
    # 根据config查询实际执行的备注信息
    sql="
    SET NAMES utf8;
    select REPLACE(dc.remark, '\n', '</br>') from ds_config dc
      inner join ds_config_detail dcd on dcd.config_id=dc.id
      where dc.enable=1
      and dcd.enable=1
      and dc.id=${config_id}
    "
  fi
  $(log "################## 查询备注信息的sql");
  $(log "${sql}");
  email_content=`${conn} "${sql}"`;
  email_title="${config_name}_报表_${SYS_TASK_NAME}"
  email_content="您好,请查收本次任务的附件!</br>备注信息：${email_content}"
  result_file_path="${result_path}/${SYS_TASK_NAME}.csv"
  $(log "################## 将csv转excel命令");
  $(log "${java_path} -jar ${data_service_util_jar} org.blazer.util.Csv2Excel ${result_path}/${SYS_TASK_NAME}.csv");
  ${java_path} -jar ${data_service_util_jar} "org.blazer.util.Csv2Excel" "${result_path}/${SYS_TASK_NAME}.csv"
  if [ "$?" = "0" ];
  then
    result_file_path=${result_path}/${SYS_TASK_NAME}.csv.xlsx
  fi
  $(log "################## 将csv或excel压缩命令");
  $(log "${zip_path} -j ${result_path}/${SYS_TASK_NAME}.zip ${result_file_path}");
  ${zip_path} -j ${result_path}/${SYS_TASK_NAME}.zip ${result_file_path}
  if [ "$?" = "0" ];
  then
    result_file_path=${result_path}/${SYS_TASK_NAME}.zip
  fi
  # remove on 2017-09-15
  # $(log "${python_path} ${email_util} ${emails} ${email_title} ${email_content} ${result_file_path}");
  # ${python_path} ${email_util} "${emails}" "${email_title}" "${email_content}" "${result_file_path}"
  $(log "################## 发送邮件命令（支持将csv转换成html）");
  $(log "${java_path} -jar ${data_service_util_jar} org.blazer.util.EmailUtil_Csv2HtmlTable ${emails} ${email_title} ${email_content} ${result_path}/${SYS_TASK_NAME}.csv ${result_file_path}");
  $(log "################## 开始发送邮件");
  ${java_path} -jar ${data_service_util_jar} "org.blazer.util.EmailUtil_Csv2HtmlTable" "${emails}" "${email_title}" "${email_content}" "${result_path}/${SYS_TASK_NAME}.csv" "${result_file_path}"
  if [ "$?" = "0" ];
  then
    echo "执行完毕."
    exit 0
  fi
fi

echo "查询失败.";
echo "执行完毕.";
exit 1
