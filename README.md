# data-service（数据服务系统）
为数据仓库访问数据统一接口，配置好数据源，可直接编写SQL（支持Mysql，Oracle，DB2，Hive，等主流数据源），即时或定时查询数据，数据可用邮件接收或直接打开数据报表。

依赖项目：
https://github.com/Char-CN/user-service-core

<br>
<br>
发布需要：
<br>
1.修改过滤器: web.xml 中的 PermissionsFilter
<br>
2.修改数据源: datasource.properties
<br>
3.修改调度路径：scheduler.properties
<br>
4.修改脚本路径：script.properties
