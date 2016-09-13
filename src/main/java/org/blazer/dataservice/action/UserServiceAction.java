package org.blazer.dataservice.action;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.body.LoginBody;
import org.blazer.dataservice.cache.PermissionsCache;
import org.blazer.dataservice.cache.SessionCache;
import org.blazer.dataservice.cache.UserCache;
import org.blazer.dataservice.model.LoginType;
import org.blazer.dataservice.model.PermissionsModel;
import org.blazer.dataservice.model.UserModel;
import org.blazer.dataservice.util.DesUtil;
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
	private static final String COOKIE_PATH = "/";
	private static final int COOKIE_SECONDS = 1000;
	// LoginType,UserId,UserName
	private static final String LOGGIN_FORMAT = "%s,%s,%s,%s";

//	@Autowired
//	SessionCache sessionCache;

	@Autowired
	UserCache userCache;

	@Autowired
	PermissionsCache permissionsCache;

	@RequestMapping("/getlogin")
	public String getLogin(HttpServletRequest request, HttpServletResponse response) {
		return "/login.html";
	}

	@ResponseBody
	@RequestMapping("/logout")
	public Body logout(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		logger.debug("logout session id : " + sessionId);
		Cookie cookie = new Cookie(SESSION_KEY, null);
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return new Body().setStatus("200").setMessage("注销成功");
	}

	@ResponseBody
	@RequestMapping("/login")
	public Body login(HttpServletRequest request, HttpServletResponse response) {
		if (checkUser(request, response)) {
			return new Body().setMessage("您好，" + getUser(request, response).getUserName() + "，您已经登录，无需再次登录");
		}
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
			String sessionId = DesUtil.encrypt(String.format(LOGGIN_FORMAT, LoginType.userName.index, um.getId(), um.getUserName(), getExpire()));
//			String sessionId = sessionCache.add(um);
			Cookie cookie = new Cookie(SESSION_KEY, sessionId);
			cookie.setPath(COOKIE_PATH);
			cookie.setMaxAge(COOKIE_SECONDS);
			response.addCookie(cookie);
			return new LoginBody().setSessionId(sessionId).setMessage("登录成功");
		}
		return new Body().setStatus("201").setMessage("密码不对，登录失败");

		// String sessionId = getSessionId(request);
		// if (sessionId != null && sessionCache.contains(sessionId)) {
		// logger.debug("sessionid : " + sessionId);
		// } else {
		// System.out.println(UUID.randomUUID().toString().length());
		// Cookie cookie = new Cookie(SESSION_KEY,
		// UUID.randomUUID().toString());
		// response.addCookie(cookie);
		// }
	}

	@ResponseBody
	@RequestMapping("/getuser")
	public UserModel getUser(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		sessionId = DesUtil.decrypt(sessionId);
		String[] contents = StringUtils.splitByWholeSeparator(sessionId, ",");
		if (sessionId == null || contents.length != 4) {
			return new UserModel();
		}
		UserModel um = userCache.get(contents[2]);
		if (um == null) {
			um = new UserModel();
		}
		um.setPermissionsBitmap(null);
//		UserModel um = sessionCache.get(sessionId);
//		if (um == null) {
//			um = new UserModel();
//		}
//		um.setPermissionsBitmap(null);
		return um;
	}

	@ResponseBody
	@RequestMapping("/checkuser")
	public boolean checkUser(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		sessionId = DesUtil.decrypt(sessionId);
		String[] contents = StringUtils.splitByWholeSeparator(sessionId, ",");
		if (sessionId == null || contents.length != 4) {
			return false;
		}
		if (Long.parseLong(contents[3]) > System.currentTimeMillis()) {
			return false;
		}
		return true;
//		String sessionId = getSessionId(request);
//		return sessionCache.contains(sessionId);
	}

	@ResponseBody
	@RequestMapping("/delay")
	public boolean delay(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		if (sessionId == null) {
			return false;
		}
		String[] contents = StringUtils.splitByWholeSeparator(sessionId, ",");
		String.format(LOGGIN_FORMAT, contents[0], contents[1], contents[2], getExpire());
		Cookie cookie = new Cookie(SESSION_KEY, sessionId);
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(COOKIE_SECONDS);
		response.addCookie(cookie);
		return true;
	}

	@ResponseBody
	@RequestMapping("/checkurl")
	public boolean checkUrl(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		sessionId = DesUtil.decrypt(sessionId);
		String[] contents = StringUtils.splitByWholeSeparator(sessionId, ",");
		UserModel um = userCache.get(contents[2]);
//		String sessionId = getSessionId(request);
//		UserModel um = sessionCache.get(sessionId);
//		logger.debug("user : " + um);
		if (um == null) {
			logger.debug("check user is null...");
			return false;
		}
		PermissionsModel permissionsModel = permissionsCache.get(permissionsCache.get(getSystemName_Url(request)));
		logger.debug("url key : " + getSystemName_Url(request));
		logger.debug("permissionsModel : " + permissionsModel);
		logger.debug("bitmap : " + um.getPermissionsBitmap());
		// 系统存了该URL并且该URL的bitmap是没有值得
		if (permissionsModel != null && !um.getPermissionsBitmap().contains(permissionsModel.getId())) {
			return false;
		}
		return true;
	}

	private long getExpire() {
		return COOKIE_SECONDS * 1000 + System.currentTimeMillis();
	}

	private void removeCookie(HttpServletRequest request) {
		HashMap<String, String> params = getParamMap(request);
	}

	private String getSystemName_Url(HttpServletRequest request) {
		HashMap<String, String> params = getParamMap(request);
		return params.get("systemName") + "_" + params.get("url");
	}

	private String getSessionId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		Cookie sessionCookie = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (SESSION_KEY.equals(cookie.getName())) {
					logger.debug("cookie : " + cookie.getName() + " | " + cookie.getValue());
					sessionCookie = cookie;
					break;
				}
			}
		}
		String paramKey = getParamMap(request).get(SESSION_KEY);
		if (StringUtils.isNotBlank(paramKey)) {
			return paramKey;
		}
		return sessionCookie == null ? null : sessionCookie.getValue();
	}

}
