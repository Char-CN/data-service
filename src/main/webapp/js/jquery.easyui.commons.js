$(function() {
	$.ds = {
		url : {
			tree : "view/getTree.do?",
			configs_by_group_id : "view/getConfigsByGroupId.do?",
			config_by_id : "view/getConfigById.do?",
			datasource_all : "view/getDataSourceAll.do"
		},
		commons : {
			referCenterByMenuId : function(id) {
			},
			addFootTask : function(window_id, text) {
			},
			removeFootTask : function(window_id) {
			}
		},
		getBytesLength : function(str) {
			// 在GBK编码里，除了ASCII字符，其它都占两个字符宽
			return str.replace(/[^\x00-\xff]/g, 'xx').length;
		},
		getMainHeight : function() {
			return $("#main").height();
		},
		getMainWidth : function() {
			return $("#main").width();
		},
		getCenterHeight : function() {
			return $("#center").height();
		},
		getCenterWidth : function() {
			return $("#center").width();
		},
		getHeadHeight : function() {
			return $("#head").height();
		},
		getHeadWidth : function() {
			return $("#head").width();
		},
		getDefaultWindowHeight : function() {
			return 600;
		},
		getDefaultWindowWidth : function() {
			return 888;
		},
		getWindowHeight : function() {
			return $(window).height();
		},
		getWindowWidth : function() {
			return $(window).width();
		},
		getQueryString : function(queryString, name) {
			var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
			var r = queryString.match(reg);
			if (r != null) {
				return unescape(r[2]);
			}
			return null;
		},
		replaceAll : function(data, old_regexp, new_str) {
			return data.replace(new RegExp(old_regexp, "gm"), new_str);
		},
		HashMap : function() {
			var length = 0;
			var obj = new Object();
			this.isEmpty = function() {
				return length == 0;
			};
			this.containsKey = function(key) {
				return (key in obj);
			};
			this.containsValue = function(value) {
				for ( var key in obj) {
					if (obj[key] == value) {
						return true;
					}
				}
				return false;
			};
			this.put = function(key, value) {
				if (!this.containsKey(key)) {
					length++;
				}
				obj[key] = value;
			};
			this.get = function(key) {
				return this.containsKey(key) ? obj[key] : null;
			};
			this.remove = function(key) {
				if (this.containsKey(key) && (delete obj[key])) {
					length--;
				}
			};
			this.values = function() {
				var _values = new Array();
				for ( var key in obj) {
					_values.push(obj[key]);
				}
				return _values;
			};
			this.keySet = function() {
				var _keys = new Array();
				for ( var key in obj) {
					_keys.push(key);
				}
				return _keys;
			};
			this.size = function() {
				return length;
			};
			this.clear = function() {
				length = 0;
				obj = new Object();
			};
		}
	};
});