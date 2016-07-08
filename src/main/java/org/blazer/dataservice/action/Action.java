package org.blazer.dataservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hyy")
public class Action {

	@RequestMapping("/a")
	public String b(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("key", "后台value！" + request.toString());
		return "a";
	}

	@ResponseBody
	@RequestMapping("/say")
	public String say() {
		return "say~~~~~~~~~~";
	}

	@ResponseBody
	@RequestMapping("/say2")
	public Action.Entity say2() {
		Entity en = new Entity();
		en.setId(111);
		en.setName("哈哈!");
		return en;
	}

	class Entity {

		private int id;

		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
