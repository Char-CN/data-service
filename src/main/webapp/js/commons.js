var data_service_commons_util = function() {
	this.yyyy_MM_dd = function(t) {
		var date = t == undefined ? new Date() : t;
		var date = new Date();
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (day >= 0 && day <= 9) {
			day = "0" + day;
		}
		return year + "-" + month + "-" + day;
	};

	this.yyyyMMdd = function(t) {
		var date = t == undefined ? new Date() : t;
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		if (day >= 0 && day <= 9) {
			day = "0" + day;
		}
		return year + month + day;
	};

	this.HH = function() {
		var date = new Date();
		return date.getHours();
	};

	this.MM = function() {
		var date = new Date();
		var month = date.getMonth() + 1;
		if (month >= 1 && month <= 9) {
			month = "0" + month;
		}
		return month;
	};

	this.mm = function() {
		var date = new Date();
		return date.getMinutes();
	};

	this.toFixed = function(num, cent, isThousand) {
		// return d.toFixed(n);
		num = num.toString().replace(/\$|\,/g, '');

		// 检查传入数值为数值类型
		if (isNaN(num))
			num = "0";

		// 获取符号(正/负数)
		sign = (num == (num = Math.abs(num)));

		num = Math.floor(num * Math.pow(10, cent) + 0.50000000001); // 把指定的小数位先转换成整数.多余的小数位四舍五入
		cents = num % Math.pow(10, cent); // 求出小数位数值
		num = Math.floor(num / Math.pow(10, cent)).toString(); // 求出整数位数值
		cents = cents.toString(); // 把小数位转换成字符串,以便求小数位长度

		// 补足小数位到指定的位数
		while (cents.length < cent)
			cents = "0" + cents;

		if (isThousand == undefined || isThousand) {
			// 对整数部分进行千分位格式化.
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));
		}

		if (cent > 0)
			return (((sign) ? '' : '-') + num + '.' + cents);
		else
			return (((sign) ? '' : '-') + num);
	};

	this.toFixed2 = function(num) {
		return this.toFixed(num, 2, true);
	};

	this.toFixed0 = function(num) {
		return this.toFixed(num, 0, true);
	};

	// 自定义格式转换
	// old details:[{'key1':'val1','key2':'val2'},{'key1':'val3','key2':'val4'}]
	// new rstMap:{'key1':['val1', 'val2'],'key2':['val3', 'val4']}
	this.ConvertDetailsToMap = function(details) {
		var rstMap = {};
		if (details.length > 0) {
			// 获得所有 结果表头
			keys = Object.keys(details[0]);
			// 初始化结果map
			for ( var i in keys) {
				rstMap[keys[i]] = [];
			}
			// 循环读取数据,结果数据格式为:rstMap['key1'] = ['val1', 'val2', 'val3']
			for ( var i in details) {
				for ( var j in keys) {
					rstMap[keys[j]].push(details[i][keys[j]]);
				}
			}
		}
		return rstMap;
	};
};

var $DSUtil = new data_service_commons_util();
