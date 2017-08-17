package org.blazer.dataservice.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 此Action用户区分admin和user的权限，固不可以删除。
 * 
 * @author hyy
 *
 */
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
