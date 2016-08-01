$(function() {
	var getTreeUrl = "/index/getTree.do";

//	$("#foo").sortable({"disabled" : true});
	var sortable = Sortable.create(document.getElementById("foo"), {"disabled" : true});
//	sortable.option("disabled", true);

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
		method : 'post',
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
		onBeforeExpand : function(node) {
			$('#tree').tree('options').url = getTreeUrl + '?id=' + node.id;
			$("#main").layout('panel', 'center').panel('setTitle', node.text);
			referCenterByMenuId(node.id);
		}
	});

	var xhr = null;
	var referCenterByMenuId = function(id) {
		if (xhr != null) {
			xhr.abort();
		}
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

	center_menu.menu('appendItem', {
		text : '新增配置项',
		iconCls : 'icon-add',
		onclick : function() {
			$.messager.confirm('确定', '您确定要新增一个配置项?', function(r){
				if (r){
					
				}
			});
		}
	});

	center_menu.append($('<div class="menu-sep"></div>')).menu('appendItem', {
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
//			$("#foo").sortable({"disabled" : false});
		}
	});

	center_menu.menu('appendItem', {
		text : '刷新',
		iconCls : 'icon-reload',
		onclick : function() {
			return $.mymessager.show('刷新!');
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
			return $.mymessager.show('您保存了本次自定义排序操作!');
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
			return $.mymessager.show('您取消了本次自定义排序操作!');
		}
	});
});