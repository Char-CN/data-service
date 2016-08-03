$(function() {
	//////////////////////////////////////////////center

	$.ds.commons.referCenterByMenuId = function(id) {
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
	};

	$.ds.commons.addFootTask = function(window_id, text) {
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

	$.ds.commons.removeFootTask = function(window_id) {
		$("#foot").find("[window_id='" + window_id + "']").remove();
	};

});