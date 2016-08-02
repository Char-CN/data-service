$(function() {
	$.dsmessager = {
		show : function(message) {
			if (message == undefined || message == null) {
				message = "";
			}
			if (message == "") {
				return;
			}
			return $.messager.show({
				title : $.dsmessager.defaults.title,
				msg : '' + message,
				showType : $.dsmessager.defaults.showType
			});
		}
	};

	$.dsmessager.defaults = {
		title : '提示',
		showType : 'fade'
	};
});
