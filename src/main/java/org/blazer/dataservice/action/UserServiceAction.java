package org.blazer.dataservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.ConfigBody;
import org.blazer.dataservice.body.ParamsBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/userservice")
public class UserServiceAction extends BaseAction {

	@ResponseBody
	@RequestMapping("/getuser")
	public ConfigBody getUser(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@ResponseBody
	@RequestMapping("/checkuser")
	public ParamsBody checkUser(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@ResponseBody
	@RequestMapping("/checkurl")
	public ParamsBody checkUrl(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

}
