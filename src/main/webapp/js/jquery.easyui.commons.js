$(function() {
	$.ds = {
		url : {
			tree : "view/getTree.do?",
			configs_by_group_id : "view/getConfigsByGroupId.do?",
			config_by_id : "view/getConfigById.do?",
			datasource_all : "view/getDataSourceAll.do",
			save_config : "view/saveConfig.do"
		},
		commons : {
			referCenterByMenuId : function(id) {
			},
			addFootTask : function(window_id, text) {
			},
			removeFootTask : function(window_id) {
			},
			chooseTreeId : ""
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
		}
	};
});