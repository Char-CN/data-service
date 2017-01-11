#!/bin/sh
source ~/.bash_profile

############################################################################# 配置信息
mysql_path="/usr/local/mysql/bin/mysql";
hive_path="/Users/hyy/Work/hive-1.2.1-bin/bin/hive";
python_path="/usr/bin/python"
conn="${mysql_path} -hms -udev -pdev123456 -Ddw_dataservice -N -e";
email_util=email_util.py

############################################################################# 获取路径begin
SOURCE="$0"
while [ -h "$SOURCE"  ]; do
    DIR="$( cd -P "$( dirname "$SOURCE"  )" && pwd  )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /*  ]] && SOURCE="$DIR/$SOURCE"
done
WORKSPACE="$( cd -P "$( dirname "$SOURCE"  )" && pwd  )"
cd ${WORKSPACE}
############################################################################# 获取路径end ${WORKSPACE}

############################################################################# 获取参数begin
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
############################################################################# 获取参数end

result_path=$(get result_path)
mapping_config_job_id=$(get mapping_config_job_id)
config_id=$(get config_id)
emails=$(get emails)

echo "SYS_TASK_NAME         : ${SYS_TASK_NAME}";
echo "result_path           : ${result_path}"
echo "mapping_config_job_id : ${mapping_config_job_id}";
echo "config_id             : ${config_id}";
echo "emails                : ${emails}"

echo "################## config_id存在表示即时查询的任务"
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

echo "################## 获取datasource信息。用户生成实际执行命令"
rst=`${conn} "${sql}"`
echo $rst
db_type=`echo $rst | awk -F ' ' '{print $1}'`;
url=`echo $rst | awk -F ' ' '{print $2}'`;
host=`echo $rst | awk -F ' ' '{print $3}'`;
port=`echo $rst | awk -F ' ' '{print $4}'`;
dbname=`echo $rst | awk -F ' ' '{print $5}'`;
username=`echo $rst | awk -F ' ' '{print $6}'`;
password=`echo $rst | awk -F ' ' '{print $7}'`;
config_name=`echo $rst | awk -F ' ' '{print $8}'`;

echo "db_type  : ${db_type}"
echo "url      : ${url}"
echo "host     : ${host}"
echo "port     : ${port}"
echo "dbname   : ${dbname}"
echo "username : ${username}"
echo "password : ${password}"
echo "config   : ${config_name}"

if [ "${config_id}" == "" ];
then
  # 根据mappingid查询实际执行sql
  sql="
  SET NAMES utf8;
  select REPLACE(dcd.values, '\n', '\r') from mapping_config_job mcj
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
  select REPLACE(dcd.values, '\n', '\r') from ds_config dc
    inner join ds_config_detail dcd on dcd.config_id=dc.id
    where dc.enable=1
    and dcd.enable=1
    and dc.id=${config_id}
  "
fi
#select REPLACE(dcd.values, '\n', ' ') from ds_config dc
#select REPLACE(dcd.values, '\n', '\r') from ds_config dc

echo "${sql}"
echo "################## 实际查询的sql"
query_sql=`${conn} "${sql}"`;

echo "$query_sql"

echo "################## 实际查询的sql参数替换处理"
arr=(${params_sh_sys_maps//${params_sh_sys_split}/ })
for((i=0;i<${#arr[@]};i++))
do
  val=${arr[$i]}
  val_arr=(${val//=/ })
  key="${val_arr[0]}"
  value="${val_arr[1]}"
  query_sql=${query_sql//\$\{${key}\}/${value}}
done

#query_sql=${query_sql//\`/\\\`}

echo "################## 处理后的实际查询的sql"
echo "$query_sql"

if [ ${db_type} == "hive" ];
then
  query_sql="set hive.cli.print.header=true; ${query_sql}"
else
  query_sql="SET NAMES utf8; ${query_sql}"
fi

echo "################## 判断是mysql类型还是hive类型"
exec_cmd=""
if [ ${db_type} == "mysql" ];
then
  exec_cmd="${mysql_path} -h${host} -u${username} -p${password} -P${port} -D${dbname} -n -e"
else
  exec_cmd="${hive_path} -e"
fi

echo "${exec_cmd} \"${query_sql}\" > ${result_path}/${SYS_TASK_NAME}.csv"

echo "开始执行查询任务......"

${exec_cmd} "${query_sql}" > ${result_path}/${SYS_TASK_NAME}.csv

if [ "$?" = "0" ];
then
  echo "查询成功,开始发送邮件."
  if [ "${emails}" == "" ];
  then
    emails=`${conn} "select email from mapping_config_job where id=${mapping_config_job_id}"`
  fi
  echo "${python_path} ${email_util} ${emails} ${config_name}报表${SYS_TASK_NAME} ${SYS_TASK_NAME}请查收附件 ${result_path}/${SYS_TASK_NAME}.csv"
  ${python_path} ${email_util} ${emails} "${config_name}报表${SYS_TASK_NAME}" "${SYS_TASK_NAME}请查收附件" "${result_path}/${SYS_TASK_NAME}.csv"
else
  echo "查询失败."
  exit 1
fi



