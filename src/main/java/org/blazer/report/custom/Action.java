package org.blazer.report.custom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hyy")
public class Action {

	@RequestMapping("/a")
	public String a() {
		return "a.html";
	}

	@RequestMapping("/b")
	public String b(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("key", "后台value！");
		return "b.jsp";
	}

	@ResponseBody
	@RequestMapping("/say")
	public String say() {
		return "say~~~~~~~~~~";
	}

	@ResponseBody
	@RequestMapping("/say2")
	public Entity say2() {
		Entity en = new Entity();
		en.setId(111);
		en.setName("哈哈!");
		return en;
	}

}
