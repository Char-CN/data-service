package org.blazer.dataservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "indexAction")
@RequestMapping("/index")
public class IndexAction {

	private static Logger logger = LoggerFactory.getLogger(IndexAction.class);

	@ResponseBody
	@RequestMapping("/getTree")
	public String getConfig(HttpServletRequest request, HttpServletResponse response) {
		String str = "[{\"text\" : \"Item1\",\"state\" : \"closed\"}, {\"text\" : \"Item2\",\"state\" : \"closed\"}]";
		return str;
	}

}
