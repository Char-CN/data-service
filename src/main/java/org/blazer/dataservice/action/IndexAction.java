package org.blazer.dataservice.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.GroupBody;
import org.blazer.dataservice.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "indexAction")
@RequestMapping("/index")
public class IndexAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(IndexAction.class);

	@Autowired
	IndexService indexService;
	
	@ResponseBody
	@RequestMapping("/getTree")
	public List<GroupBody> getConfig(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
//		String str = "[{\"text\" : \"Item1\",\"state\" : \"closed\", \"id\" : \"-1\"}, {\"text\" : \"Item2\",\"state\" : \"closed\", \"id\" : \"2\"}]";
//		if (getParamMap(request).get("id").equals("-1")) {
//			return str;
//		}
		return indexService.findTreeById(getParamMap(request));
	}

}
