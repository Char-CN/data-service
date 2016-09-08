package org.blazer.dataservice.action;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.body.ParamsBody;
import org.blazer.dataservice.cache.SessionCache;
import org.blazer.dataservice.cache.UserCache;
import org.blazer.dataservice.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/userservice")
public class UserServiceAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(UserServiceAction.class);
	private static final String SESSION_KEY = "MYSESSIONID";

	@Autowired
	SessionCache sessionCache;
	
	@Autowired
	UserCache userCache;

	private String getSessionId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		Cookie sessionCookie = null;
		for (Cookie cookie : cookies) {
			if (SESSION_KEY.equals(cookie.getName())) {
				logger.debug("cookie : " + cookie.getName() + " | " + cookie.getValue());
				sessionCookie = cookie;
				break;
			}
		}
		return sessionCookie == null ? null : sessionCookie.getValue();
	}

	@ResponseBody
	@RequestMapping("/login")
	public Body login(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> params = getParamMap(request);
		if (!params.containsKey("userName")) {
			return new Body().setStatus("201").setMessage("登录失败");
		}
		UserModel um = userCache.get(params.get("userName").toString());
		if (um == null) {
			return new Body().setStatus("201").setMessage("找不到账号，登录失败");
		}
		logger.debug("user password : " + um.getPassword());
		logger.debug("params password : " + params.get("password"));
		if (um.getPassword().equals(params.get("password").toString())) {
			String sessionId = sessionCache.add(um);
			Cookie cookie = new Cookie(SESSION_KEY, sessionId);
			response.addCookie(cookie);
			return new Body().setStatus("200").setMessage("登录成功");
		}
		return new Body().setStatus("201").setMessage("密码不对，登录失败");
		
//		String sessionId = getSessionId(request);
//		if (sessionId != null && sessionCache.contains(sessionId)) {
//			logger.debug("sessionid : " + sessionId);
//		} else {
//			System.out.println(UUID.randomUUID().toString().length());
//			Cookie cookie = new Cookie(SESSION_KEY, UUID.randomUUID().toString());
//			response.addCookie(cookie);
//		}
	}

	@ResponseBody
	@RequestMapping("/getuser")
	public UserModel getUser(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		if (sessionCache.get(sessionId) == null) {
			UserModel um = new UserModel();
//			um.setEmail("aaaaaaaa");
//			um.setUserName("22222222222222aaaaaaaa");
			return um;
		}
		return sessionCache.get(sessionId);
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
