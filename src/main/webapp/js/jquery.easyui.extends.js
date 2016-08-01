$(function() {
	$.mymessager = {
		show : function(message) {
			if (message == undefined || message == null) {
				message = "";
			}
			if (message == "") {
				return;
			}
			return $.messager.show({
				title : $.mymessager.defaults.title,
				msg : '' + message,
				showType : $.mymessager.defaults.showType
			});
		}
	};

	$.mymessager.defaults = {
		title : '提示',
		showType : 'fade'
	};
});
