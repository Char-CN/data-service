$(function() {
	var getTreeUrl = "/view/getTree.do";
	var getConfigsByGroupIdUrl = "/view/getConfigsByGroupId.do";
	var getConfigByIdUrl = "/view/getConfigById.do";

	var menu = $('<div></div>');
	menu.attr('id', 'tree_menu');
	menu.attr('class', 'easyui-menu');

	// 增加按钮
	var menu_add = $('<div>新增子目录</div>');
	menu_add.attr('data-options', "iconCls:'icon-add'");
	menu_add.bind('click', function() {
		alert(1);
	});
	menu.append(menu_add);

	// 排序按钮
	var menu_s = $('<div>自定义排序</div>');
	menu_s.attr('data-options', "iconCls:'icon-edit'");
	menu_s.bind('click', function() {
		$("#tree").tree('enableDnd');
		$("#tree").unbind('contextmenu');
		$("#tree").bind('contextmenu', function(e) {
			e.preventDefault();
			$('#tree_menu_sort').menu('show', {
				left : e.pageX,
				top : e.pageY
			});
		});
	});
	menu.append(menu_s);
	// 初始化右键菜单
	menu.menu().appendTo('body');

	// 自定义排序时菜单
	var menu_sort = $('<div></div>');
	menu_sort.attr("id", "tree_menu_sort");
	menu_sort.attr("class", "easyui-menu");

	// 自定义排序时保存按钮
	var menu_sort_save = $('<div>保存</div>');
	menu_sort_save.attr("data-options", "iconCls:'icon-save'");
	menu_sort_save.bind('click', function() {
		alert('save');
		$("#tree").tree('disableDnd');
		$("#tree").unbind('contextmenu');
		$("#tree").bind('contextmenu', function(e) {
			e.preventDefault();
			$('#tree_menu').menu('show', {
				left : e.pageX,
				top : e.pageY
			});
		});
	});

	// 自定义排序时取消按钮
	var menu_sort_cancel = $('<div>取消</div>');
	menu_sort_cancel.attr("data-options", "iconCls:'icon-cancel'");
	menu_sort_cancel.bind('click', function() {
		$("#tree").tree('disableDnd');
		$("#tree").unbind('contextmenu');
		$("#tree").bind('contextmenu', function(e) {
			e.preventDefault();
			$('#tree_menu').menu('show', {
				left : e.pageX,
				top : e.pageY
			});
		});
	});

	menu_sort.append(menu_sort_save);
	menu_sort.append(menu_sort_cancel);
	menu_sort.menu().appendTo('body');

	// 初始化树形菜单
	$("#tree").tree({
		lines : true,
		method : 'post',
		animate : true,
		url : getTreeUrl + '?id=-1',
		onContextMenu : function(e, node) {
			$(this).tree('select', node.target);
			e.preventDefault();
			$('#tree_menu').menu('show', {
				left : e.pageX,
				top : e.pageY
			});
		},
		onDblClick : function(node) {
			if (node.state == "closed") {
				$(this).tree('expand', node.target);
			} else {
				$(this).tree('collapse', node.target);
			}
		},
		onBeforeCollapse : function(node) {
			$('#tree').tree('options').url = getTreeUrl + '?id=' + node.id;
			$("#main").layout('panel', 'center').panel('setTitle', node.text);
			referCenterByMenuId(node.id);
		},
		onBeforeExpand : function(node) {
			$('#tree').tree('options').url = getTreeUrl + '?id=' + node.id;
			$("#main").layout('panel', 'center').panel('setTitle', node.text);
			referCenterByMenuId(node.id);
		}
	});

	//////////////////////////////////////////////center
	var sortable = Sortable.create(document.getElementById("center_ul"), {"disabled" : true});

	var xhr = null;
	var referCenterByMenuId = function(id) {
		if (xhr != null) {
			xhr.abort();
		}
		var url = getConfigsByGroupIdUrl + "?id=" + id;
		$.post(url, function(data){
			$("#center_ul").html('');
			var rst = eval(data);
			if (rst.length != 0) {
				for (var i in rst) {
					var li = $('<li class="dsli">');
					li.append('<span class="tree-file"></span>');
					li.append('<span>' + rst[i].configName + '</span>');
					li.dblclick(function(){
						openConfig(rst[i].id);
					});
					$("#center_ul").append(li);
				}
			}
			return $.dsmessager.show('加载[' + rst.length + ']条配置。');
		});
	};

	var openConfig = function(id) {
		alert(id);
	};

	var appendWindow = function(id, title, url) {
		var window = $('<div></div>');
		window.attr("id", id);
		window.attr("title", title);
		window.attr("class", "easyui-window");
		window.window({
			top : $.ds.getHeadHeight(),
			left : 0,
			height : $.ds.getDefaultWindowHeight(),
			width : $.ds.getDefaultWindowWidth(),
			closed : false,
			modal : false,
			// collapsible : false,
			// minimizable : false,
			maximizable : false,
			onClose : function(forceDestroy) {
				$(this).window('destroy');
			},
			onDestroy : function() {
				$("#foot").find("[window_id='" + this.id + "']").remove();
			}
		}).window('center');
		window.window('refresh', url);
		appendFoot("center_add_config", title);
	};

	var appendFoot = function(window_id, text) {
		var a = $('<a style="margin:2px;"></a>');
		// a.attr("id", id);
		a.attr("window_id", window_id);
		a.attr("data-options", "plain:false");
		a.html(text.length > 10 ? text.substr(0,10) + '...' : text);
		a.linkbutton();
		a.click(function() {
			var window_id = $(this).attr("window_id");
			$('#' + window_id).window('open');
		});
		$("#foot").append(a);
	};

	var c = $("#center");
	c.bind('contextmenu', function(e) {
		e.preventDefault();
		$('#center_menu').menu('show', {
			left : e.pageX,
			top : e.pageY
		});
		rp_center_block = true;
	});

	var center_menu = $('<div id="center_menu"></div>');
	center_menu.appendTo('body');
	center_menu.menu({
		onHide : function() {
			rp_center_block = false;
		}
	});

	var add_count = 10;
	center_menu.menu('appendItem', {
		text : '新增配置项',
		iconCls : 'icon-add',
		disabled : false,
		onclick : function() {
			$("#center_ul").append('<li ><span class="tree-file"></span><span>中文字符'+add_count+'</span></li>');
			add_count++;
			if ($("#center_add_config").length != 0) {
				return;
			}
			appendWindow("center_add_config", "新增配置项", "addconfig.html");
		}
	});

	center_menu.append($('<div class="menu-sep"></div>'));

	center_menu.menu('appendItem', {
		text : '自定义排序',
		iconCls : 'icon-edit',
		onclick : function() {
			var c = $("#center");
			c.unbind('contextmenu');
			c.bind('contextmenu', function(e) {
				e.preventDefault();
				$('#center_menu_save_cancel').menu('show', {
					left : e.pageX,
					top : e.pageY
				});
			});
			sortable.option("disabled", false);
		}
	});

	center_menu.menu('appendItem', {
		text : '刷新',
		iconCls : 'icon-reload',
		onclick : function() {
			return $.dsmessager.show('刷新!');
		}
	});

	var menuSaveCancel = $('<div id="center_menu_save_cancel"></div>');
	menuSaveCancel.appendTo('body').menu({});
	menuSaveCancel.menu('appendItem', {
		text : '保存',
		iconCls : 'icon-save',
		onclick : function() {
			var c = $("#center");
			c.unbind('contextmenu');
			c.bind('contextmenu', function(e) {
				e.preventDefault();
				$('#center_menu').menu('show', {
					left : e.pageX,
					top : e.pageY
				});
			});
			sortable.option("disabled", true);
			return $.dsmessager.show('您保存了本次自定义排序操作!');
		}
	}).menu('appendItem', {
		text : '取消',
		iconCls : 'icon-remove',
		onclick : function() {
			var c = $("#center");
			c.unbind('contextmenu');
			c.bind('contextmenu', function(e) {
				e.preventDefault();
				$('#center_menu').menu('show', {
					left : e.pageX,
					top : e.pageY
				});
			});
			sortable.option("disabled", true);
			return $.dsmessager.show('您取消了本次自定义排序操作!');
		}
	});
});