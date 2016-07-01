package org.blazer.dataservice.action;

import java.util.List;
import java.util.Map;

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
	public List<Map<String, Object>> getConfig() {
		return dataService.getConfigById(0);
	}

	@ResponseBody
	@RequestMapping("/getconfig2")
	public String getConfig2() {
		return dataService.getConfigById(0).toString();
	}

}
