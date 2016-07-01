package org.blazer.dataservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.ConfigBody;
import org.blazer.dataservice.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dataservice")
public class DataAction {

	@Autowired
	private DataService dataService;

	@ResponseBody
	@RequestMapping("/getconfig")
	public ConfigBody getConfig(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(request.getParameter("id"));
		System.out.println(request.getParameter("name"));
		return dataService.getConfigById(0);
	}

	@ResponseBody
	@RequestMapping("/getconfig2")
	public String getConfig2() {
		return dataService.getConfigById(0).toString();
	}

}
