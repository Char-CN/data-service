$(function() {
	// 拖拽不允许移出浏览器
	var easyuiPanelOnMove = function(left, top) {
		var parentObj = $(this).panel('panel').parent();
		if (left < 0) {
			$(this).window('move', {
				left : 1
			});
		}
		if (top < 0) {
			$(this).window('move', {
				top : 1
			});
		}
		var width = $(this).panel('options').width;
		var height = $(this).panel('options').height;
//		var width = $(window).width();
//		var height = $(window).height();
		var right = left + width;
		var buttom = top + height;
		var parentWidth = parentObj.width();
		var parentHeight = parentObj.height();
		if (left > parentWidth - width) {
			$(this).window('move', {
				"left" : parentWidth - width
			});
		}
		if (top > parentHeight - $(this).parent().height()) {
			$(this).window('move', {
				"top" : parentHeight - $(this).parent().height() - 15
			});
		}
	};
	// 重写拖拽事件
	$.fn.panel.defaults.onMove = easyuiPanelOnMove;
	$.fn.window.defaults.onMove = easyuiPanelOnMove;
	$.fn.dialog.defaults.onMove = easyuiPanelOnMove;
	// 扩展验证规则
	$.extend($.fn.validatebox.defaults.rules, {
		nospace : {
			validator : function(value) {
				return !/\s/g.test(value);
			},
			message : '您填写的值不能包含空白字符!'
		},
		// 一组textbox的唯一值验证
		onlyValue : {
			// 例如一组标签 ：
			// <input seconds_name="key" index="1"> ...
			// <input seconds_name="key" index="2"> ..
			// <input seconds_name="key" index="3"> ..
			// 传入参数就为：index, seconds_name, key
			// 参数一：唯一的索引值的属性名称
			// 参数二：能表示是同一组的属性名称，切记不能用name，easyui生成插件的特殊原因，textbox会分成3个input，因此无法定位到该元素
			// 参数三：参数二的属性值
			validator : function(value, params) {
				var index_name = params[0];
				var ds_index_name = params[1];
				var ds_index_value = params[2];
				var val = $(this).parent().parent().find('[' + ds_index_name + '="' + ds_index_value + '"]').textbox('getValue');
				var index = $(this).parent().parent().find('[' + ds_index_name + '="' + ds_index_value + '"]').attr(index_name);
				var rst = true;
				$('[' + ds_index_name + '="' + ds_index_value + '"]').each(function() {
					if ($(this).attr(index_name) == index) {
						return;
					}
					if (val == $(this).textbox('getValue')) {
						rst = false;
					}
				});
				return rst;
			},
			message : '您填写的值不是唯一的，请检查!'
		}
	});
	// 自定义提示
//	$.dsmessager = {
//		show : function(message) {
//			if (message == undefined || message == null) {
//				message = "";
//			}
//			if (message == "") {
//				return;
//			}
//			return $.messager.show({
//				title : '提示',
//				msg : message,
//				showType : 'fade'
//			});
//		}
//	};
	// 自定义查询
	$.extend({
		DSFindByRoot : function(CONT) {
			return $.find('[ds_index="' + CONT + '"]');
		}
	});
	$.fn.DSAdd = function(CONT) {
		return $(this).attr('ds_index', CONT);
	};
	$.fn.DSFind = function(CONT) {
		return $(this).find('[ds_index="' + CONT + '"]');
	};
	$.fn.DSAll = function() {
		return $(this).find('*[ds_index]');
	};
	$.fn.DSGetIndex = function() {
		return $(this).attr('ds_index');
	};
});
