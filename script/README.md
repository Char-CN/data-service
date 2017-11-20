该目录下的jar包引用工程:https://github.com/Char-CN/data-service-util
该目录下的脚本需要传到服务器目录进行配置

核心脚本（发布需要）：
	config.sh
	data-service-util.jar
	HiveHandle.sh
	UploadHandle.sh

遗留脚本：
	bu.sh：补发email的shell
	email_util.py：python发送邮件脚本，现在用data-service-uril.jar替换
	params_util.sh：获取参数的脚本，现在嵌入到HiveHandle.sh中
	realpath_util.sh：获取路径的脚本，现在嵌入到HiveHandle.sh中
