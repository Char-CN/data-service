$(function() {
	$.ds = {
		url : {
			root : window.location.href,
			get_params : "dataservice/getparams.do",
			get_config : "dataservice/getconfig.do",
			tree : "view/getTree.do",
			configs_by_group_id : "view/getConfigsByGroupId.do",
			config_by_id : "view/getConfigById.do",
			datasource_all : "view/getDataSourceAll.do",
			run_config : "view/runConfig.do",
			delete_config : "view/deleteConfig.do",
			save_config_order : "view/saveConfigOrderAsc.do",
			save_config : "view/saveConfig.do"
		},
		commons : {
			chooseTreeId : "",
			referCenterByMenuId : function(id) {
			},
			addFootTask : function(window_id, text) {
			},
			removeFootTask : function(window_id) {
			},
			openWindow : function(id, title, url, queryString) {
			},
			configRun : function(id) {
			}
		},
		icon : {
			cube3_cls : 'fa fa-cube fa-3x',
			cube3 : '<i class="fa fa-cube fa-3x"></i>',
			run_cls : 'fa fa-play-circle fa-1x',
			run : '<i class="fa fa-play-circle fa-1x"></i>',
			add_cls : 'fa fa-plus-circle fa-1x',
			add : '<i class="fa fa-plus-circle fa-1x"></i>',
			edit_cls : 'fa fa-edit fa-1x',
			edit : '<i class="fa fa-edit fa-1x"></i>',
			list_cls : 'fa fa-list fa-1x',
			list : '<i class="fa fa-list fa-1x"></i>',
			refresh_cls : 'fa fa-refresh fa-1x',
			refresh : '<i class="fa fa-refresh fa-1x"></i>',
			loading3_cls : 'fa fa-spinner fa-pulse fa-3x fa-fw',
			loading3 : '<i class="fa fa-spinner fa-pulse fa-3x fa-fw"></i>',
			save_cls : 'fa fa-save fa-1x',
			save : '<i class="fa fa-save fa-1x"></i>',
			remove_cls : 'fa fa-times-circle fa-1x',
			remove : '<i class="fa fa-times-circle fa-1x"></i>'
		},
		show : function(message) {
			if (message == undefined || message == null || message == "") {
				return;
			}
			return $.messager.show({
				title : '提示',
				msg : message,
				showType : 'fade'
			});
		},
		alert : function(message) {
			if (message == undefined || message == null || message == "") {
				return;
			}
			$.messager.alert('提示', message);
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
		getKeys : function(obj) {
			var arr = [ ];
			for ( var i in obj) {
				arr.push(i);
			}
			return arr;
		},
		getCustomKey : function(key) {
			return key.replace('${', '').replace('}', '');
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