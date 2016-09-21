var $userservice = function() {
	var url = "http://bigdata.blazer.org:8030/user";
	var checkuser = url + "/userservice/checkuser.do";
	var getuser = url + "/userservice/getuser.do";
	var getlogin = url + "/login.html";
	var userName = null;
	var cookie_id = "MYSESSIONID";
	var init = function() {
//		 alert(checkuser);
		$.ajax({
			url : checkuser,
			type : "GET",
			async : false,
			data : {
				MYSESSIONID : $.cookie(cookie_id)
			},
			success : function(data) {
				if (data != undefined && data.split(",", 2)[0] == "false") {
					alert("对不起，您没有登录，请您登录。");
					location.href = getlogin + "?url=" + encodeURIComponent(location.href);
				} else if (data != undefined && data.split(",", 2)[0] == "true") {
					var expires = new Date();
					expires.setTime(expires.getTime() + (30 * 60 * 1000));
					$.cookie(cookie_id, data.split(",", 2)[1], { path: "/", expires: expires});
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
			}
		});
		$.ajax({
			url : getuser,
			type : "GET",
			async : false,
			data : {
				MYSESSIONID : $.cookie(cookie_id)
			},
			success : function(data) {
				userName = data.userName;
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
			}
		});
	};

	var logout = function() {
		var r=confirm("您确定退出登录吗？");
		if (r) {
			$.cookie(cookie_id, "", { path: "/", expires: -1});
			location.href = getlogin + "?url=" + encodeURIComponent(location.href);
		}
	};

	init();
	var obj = new Object();
	obj.userName = userName;
	obj.logout = logout;
	return obj;
};
var $userservice = new $userservice();
