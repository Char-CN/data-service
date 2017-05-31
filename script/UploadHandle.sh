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
    echo "$1"
  else
    echo ""
  fi
}
############################################################################# 获取传入参数
upload_path=$(get upload_path)
upload_name=$(get upload_name)
uuid=$(get uuid)
mapping_config_job_id=$(get mapping_config_job_id)
config_id=$(get config_id)

echo "upload_path           : ${upload_path}";
echo "upload_name           : ${upload_name}";
echo "uuid                  : ${uuid}";
echo "mapping_config_job_id : ${mapping_config_job_id}";
echo "config_id             : ${config_id}";
############################################################################# 获取数据源信息
echo "################## config_id存在表示即时查询的任务";
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

echo "################## 获取datasource信息。用户生成实际执行命令";
rst=`${conn} "${sql}"`
echo $(log "$rst");
db_type=`echo $rst | awk -F ' ' '{print $1}'`;
url=`echo $rst | awk -F ' ' '{print $2}'`;
host=`echo $rst | awk -F ' ' '{print $3}'`;
port=`echo $rst | awk -F ' ' '{print $4}'`;
dbname=`echo $rst | awk -F ' ' '{print $5}'`;
username=`echo $rst | awk -F ' ' '{print $6}'`;
password=`echo $rst | awk -F ' ' '{print $7}'`;
config_name=`echo $rst | awk -F ' ' '{print $8}'`;

echo "Excel转Csv"

upload_excel=${upload_path}/${upload_name}
upload_csv=${upload_excel}.csv
upload_csv_head=${upload_csv}.head
echo "upload_excel          : ${upload_excel}";
echo "upload_csv            : ${upload_csv}";
echo "upload_csv_head       : ${upload_csv_head}";

echo ${java_path} -jar ${data_service_util_jar} "org.blazer.util.Excel2Csv" "${upload_excel}"
${java_path} -jar ${data_service_util_jar} "org.blazer.util.Excel2Csv" "${upload_excel}"


echo head -1 ${upload_csv} > ${upload_csv_head}
head -1 ${upload_csv} > ${upload_csv_head}

# mac and linux parameter -ia
echo sed -ig '1d' ${upload_csv}
sed -ig '1d' ${upload_csv}

head=`cat ${upload_csv_head}`
echo "head : $head"
head=`${java_path} -jar ${data_service_util_jar} "org.blazer.util.Convert2Pinyin" "${head}"`
echo "convert head : $head"

echo $(log "################## 判断是mysql类型还是hive类型");
if [ ${db_type} == "mysql" ];
then
  echo $(log "################## 导入到mysql");
  fields=${head//	/ varchar(200), };
  query_sql="
    set names 'utf8';
    use ${import_database};
    create table ${uuid}(
        ${fields} varchar(200)
    ) 
    ENGINE=InnoDB 
    DEFAULT CHARACTER SET=utf8 
    COLLATE=utf8_general_ci 
    ROW_FORMAT=COMPACT;
    LOAD DATA LOCAL INFILE '${upload_path}/${upload_name}.csv' INTO TABLE ${uuid} 
    character set utf8 
    FIELDS TERMINATED BY '\t' 
    ENCLOSED BY '\"' 
    LINES TERMINATED BY '\n';
  ";
  exec_cmd="${mysql_path} -h${host} -u${username} -p${password} -P${port} -D${dbname} -n -e";
elif [ ${db_type} == "hive" ];
then
  echo $(log "################## 导入到hive");
  fields=${head//	/ string, };
  query_sql="
    use ${import_database};
    create table ${uuid}(
        ${fields} string
    )
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
    STORED AS TEXTFILE;
    LOAD DATA LOCAL INPATH '${upload_path}/${upload_name}.csv' INTO TABLE ${uuid}
  ";
  exec_cmd="${hive_path} -e"
fi

echo $(log "${exec_cmd} \"${query_sql}\"");

echo "开始执行......";
${exec_cmd} "${query_sql}"
if [ "$?" == "0" ];
then
  echo "执行成功。"
  exit 0
else
  echo "执行失败。"
  exit 1
fi

