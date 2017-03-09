$(function() {
	$.ds = {
		url : {
			root : function() {
				var urls = window.location.href.match(new RegExp("([a-zA-Z:]*//[.a-zA-Z0-9:]*/).*"));
				if (urls == null) {
					return window.location.href;
				}
				return urls[1];
			},
			analytic_add_task : "analytic/getAddTask.do",
			analytic_run_task : "analytic/getRunTask.do",
			find_report_by_task_name : "view/findReportByTaskName.do",
			find_task_log_by_name : "view/findTaskLogByName.do",
			find_task_by_name : "view/findTaskByName.do",
			find_schedulers_all : "view/findSchedulersAll.do",
			find_schedulers : "view/findSchedulers.do",
			save_schedulers : "view/saveSchedulers.do",
			save_scheduler : "view/saveScheduler.do",
			get_params : "dataservice/getparams.do",
			get_config : "dataservice/getconfig.do",
			add_task : "view/addTask.do",
			find_task_by_user : "view/findTaskByUser.do",
			find_task_by_admin : "view/findTaskByAdmin.do",
			tree : "view/getTree.do",
			treeAll : "view/getTreeAll.do",
			get_user_group_ids : "view/getUserGroupIds.do",
			save_user_group : "view/saveUserGroup.do",
			configs_by_group_id : "view/getConfigsByGroupId.do",
			config_by_id : "view/getConfigById.do",
			datasource_all : "view/getDataSourceAll.do",
			run_config : "view/runConfig.do",
			delete_config : "view/deleteConfig.do",
			save_config_order : "view/saveConfigOrderAsc.do",
			save_config : "view/saveConfig.do",
			move_config : "view/moveConfig.do",
			save_config_remark : "view/saveConfigRemark.do",
			find_user_by_page : "user/findUserByPage.do",
			find_user_by_id : "user/findUserById.do",
			get_user_all : "view/getAllUser.do",
			save_user : "user/saveUser.do",
			del_user : "user/delUser.do",
			find_role_by_page : "user/findRoleByPage.do",
			find_role_by_id : "user/findRoleById.do",
			save_role : "user/saveRole.do",
			del_role : "user/delRole.do",
			find_role_all : "user/findRoleAll.do",
			find_system_by_page : "user/findSystemByPage.do",
			find_system_by_id : "user/findSystemById.do",
			save_system : "user/saveSystem.do",
			del_system : "user/delSystem.do",
			find_system_all : "user/findSystemAll.do"
		},
		commons : {
			chooseTreeId : "",
			referCenterByMenuId : function(id) {
				$.ds.commons.chooseTreeId = id;
				var c = $("#center");
				c.unbind('contextmenu');
				c.bind('contextmenu', function(e) {
					e.preventDefault();
					$('#center_menu').menu('show', {
						left : e.pageX,
						top : e.pageY
					});
					rp_center_block = true;
				});
				c.panel({
					href : 'center.html',
					extractor : function(data) {
						data = $.ds.replaceAll(data, "[$]queryString", "id=" + id);
						data = $.ds.replaceAll(data, "[$]windowId", "center");
						return data;
					}
				});
			},
			getStatus : function(value) {
				if (value == "WAIT")
					return "等待执行";
				if (value == "RUN")
					return "正在执行";
				if (value == "SUCCESS")
					return "执行成功";
				if (value == "FAIL")
					return "执行失败";
				if (value == "CANCEL")
					return "执行取消";
				return "未知状态";
			},
			cancelTask : function(taskName) {
				$.ds.commons.confirm('您确定要停止该任务吗？', function(taskName) {
					alert(taskName);
				})
			},
			openTaskLog : function(taskName) {
				var width = 800;
				$.ds.commons.openWindow(taskName, $.ds.icon.file, taskName, "taskinfo.html", "taskName=" + taskName).dialog('resize', {
					width : width,
					left : width / 2,
					top : 100
				});
			},
			addFootTask : function(window_id, icon, text) {
				var a = $('<a style="margin:2px;"></a>');
				// a.attr("id", id);
				a.attr("window_id", window_id);
				a.attr("data-options", "plain:false");
				if (icon) {
					a.html(icon + (text.length > 10 ? text.substr(0, 10) + '...' : text));
				} else {
					a.html(text.length > 10 ? text.substr(0, 10) + '...' : text);
				}
				a.linkbutton();
				a.click(function() {
					var window_id = $(this).attr("window_id");
					$('#' + window_id).window('open');
				});
				$("#foot").append(a);
			},
			removeFootTask : function(window_id) {
				$("#foot").find("[window_id='" + window_id + "']").remove();
			},
			execFunc : function (funcs, func) {
				if (funcs != undefined && funcs[func] != undefined && typeof funcs[func] === 'function') {
					funcs[func]();
				}
			},
			openWindow : function(id, icon, title, url, queryString) {
				if ($("#" + id).length != 0) {
					$("#" + id).window('open');
					$.ds.show("[" + icon + title + "]已经存在。");
					return null;
				}
				var _window = $('<div></div>');
				_window.attr("id", id);
				_window.window({
					href : url,
					title : icon + "&nbsp;" + title,
					resizable : false,
					height : $.ds.getCenterHeight() < $.ds.getDefaultWindowHeight() ? $.ds.getCenterHeight() : $.ds.getDefaultWindowHeight(),
					width : $.ds.getDefaultWindowWidth(),
					closed : false,
					modal : false,
					loadingMessage : '请稍等，正在读取服务器内容，加载数据。。。',
					doSize : false,
					maximizable : false,
					minimizable : true,
					extractor : function(data) {
						data = $.ds.replaceAll(data, "[$]queryString", queryString);
						data = $.ds.replaceAll(data, "[$]windowId", id);
						return data;
					},
					onClose : function(forceDestroy) {
						$(this).window('destroy');
					},
					onDestroy : function() {
						$.ds.commons.removeFootTask(this.id);
					}
				}).window('center');
				$.ds.commons.addFootTask(id, icon, title);
				return _window;
			},
			openDialog : function(id, icon, title, url, queryString, funcs) {
				if ($("#" + id).length != 0) {
					$("#" + id).window('open');
					return $.ds.show("[" + icon + title + "]已经存在。");
				}
				var _dialog = $('<div></div>');
				_dialog.attr("id", id);
				_dialog.dialog({
					title : icon + "&nbsp;" + title,
					width : 400,
					height : 200,
					cache : false,
					href : url,
					modal : true,
					extractor : function(data) {
						data = $.ds.replaceAll(data, "[$]queryString", queryString);
						data = $.ds.replaceAll(data, "[$]windowId", id);
						return data;
					},
					onClose : function(forceDestroy) {
						$(this).window('destroy');
						$.ds.commons.execFunc(funcs, 'onClose');
					},
					onDestroy : function() {
						$.ds.commons.removeFootTask(this.id);
					}
				}).dialog('center');
				$.ds.commons.addFootTask(id, icon, title);
				return _dialog;
			}
		},
		icon : {
			cog_cls : 'fa fa-cog fa-1x',
			cog : '<i class="fa fa-cog fa-1x"></i>',
			bug_cls : 'fa fa-bug fa-1x',
			bug : '<i class="fa fa-bug fa-1x"></i>',
			table_cls : 'fa fa-table fa-1x',
			table : '<i class="fa fa-table fa-1x"></i>',
			download_cls : 'fa fa-download fa-1x',
			download : '<i class="fa fa-download fa-1x"></i>',
			file_cls : 'fa fa-file-text fa-1x',
			file : '<i class="fa fa-file-text fa-1x"></i>',
			clock_cls : 'fa fa-clock-o fa-1x',
			clock : '<i class="fa fa-clock-o fa-1x"></i>',
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
			list_alt_cls : 'fa fa-list-alt fa-1x',
			list_alt : '<i class="fa fa-list-alt fa-1x"></i>',
			refresh_cls : 'fa fa-refresh fa-1x',
			refresh : '<i class="fa fa-refresh fa-1x"></i>',
			loading3_cls : 'fa fa-spinner fa-pulse fa-3x fa-fw',
			loading3 : '<i class="fa fa-spinner fa-pulse fa-3x fa-fw"></i>',
			loading_cls : 'fa fa-spinner fa-pulse fa-1x fa-fw',
			loading : '<i class="fa fa-spinner fa-pulse fa-1x fa-fw"></i>',
			save_cls : 'fa fa-save fa-1x',
			save : '<i class="fa fa-save fa-1x"></i>',
			remove_cls : 'fa fa-times-circle fa-1x',
			remove : '<i class="fa fa-times-circle fa-1x"></i>',
			user_cls : 'fa fa-user fa-1x',
			user : '<i class="fa fa-user fa-1x"></i>',
			role_cls : 'fa fa-users fa-1x',
			role : '<i class="fa fa-users fa-1x"></i>',
			permission_cls : 'fa fa-th-list fa-1x',
			permission : '<i class="fa fa-th-list fa-1x"></i>',
			system_cls : 'fa fa-desktop fa-1x',
			system : '<i class="fa fa-desktop fa-1x"></i>'
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
		confirm : function(message, func) {
			$.messager.confirm('提示', message, function(r){
				if (func && r) func();
			});
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
			var arr = [];
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