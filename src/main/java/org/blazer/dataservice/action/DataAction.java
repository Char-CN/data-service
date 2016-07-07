package org.blazer.dataservice.action;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.ConfigBody;
import org.blazer.dataservice.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "dataAction")
@RequestMapping("/dataservice")
public class DataAction extends BaseAction {

	@Autowired
	private DataService dataService;

	@ResponseBody
	@RequestMapping("/getconfig")
	public ConfigBody getConfig(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> paramMap = getParamMap(request);
		ConfigBody cb = dataService.getConfigById(paramMap);
		return cb;
	}

	@ResponseBody
	@RequestMapping("/getconfig2")
	public String getConfig2(HttpServletRequest request, HttpServletResponse response) {
		return dataService.getConfigById(getParamMap(request)).toString();
	}

}
