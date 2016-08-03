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
			message : '该项不能包含空白字符!'
		}
	});
	// 自定义提示
	$.dsmessager = {
		show : function(message) {
			if (message == undefined || message == null) {
				message = "";
			}
			if (message == "") {
				return;
			}
			return $.messager.show({
				title : '提示',
				msg : message,
				showType : 'fade'
			});
		}
	};
});
