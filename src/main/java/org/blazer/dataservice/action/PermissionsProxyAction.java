package org.blazer.dataservice.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PermissionsProxyAction {

	@ResponseBody
	@RequestMapping("/admin")
	public String admin() {
		return "hey,admin";
	}

	@ResponseBody
	@RequestMapping("/user")
	public String user() {
		return "hey,user";
	}

}
