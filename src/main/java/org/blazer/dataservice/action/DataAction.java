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
		Integer id = 0;
		try {
			id = Integer.parseInt(request.getParameter("id"));
		} catch (Exception e) {
		}
//		String referer = request.getHeader("Referer");
//		response.setHeader("Access-Control-Allow-Origin", "*");
		ConfigBody cb = dataService.getConfigById(id);
		return cb;
	}

	@ResponseBody
	@RequestMapping("/getconfig2")
	public String getConfig2() {
		return dataService.getConfigById(0).toString();
	}

}
