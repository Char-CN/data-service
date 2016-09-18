package org.blazer.dataservice.action;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.body.LoginBody;
import org.blazer.dataservice.cache.PermissionsCache;
import org.blazer.dataservice.cache.UserCache;
import org.blazer.dataservice.model.LoginType;
import org.blazer.dataservice.model.PermissionsModel;
import org.blazer.dataservice.model.SessionModel;
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
	private static final String COOKIE_KEY = "MYSESSIONID";
	private static final String COOKIE_PATH = "/";
	private static final int COOKIE_SECONDS = 10;
	// LoginType,UserId,UserName,ExpireTime
	private static final String LOGGIN_FORMAT = "%s,%s,%s,%s";

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
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = getSessionId(request);
		logger.debug("logout session id : " + sessionId);
		Cookie cookie = new Cookie(COOKIE_KEY, null);
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
//		return new Body().setStatus("200").setMessage("注销成功");
		return true + "," + null;
	}

	@ResponseBody
	@RequestMapping("/login")
	public Body login(HttpServletRequest request, HttpServletResponse response) {
		String sessionStr = getSessionId(request);
		sessionStr = DesUtil.decrypt(sessionStr);
		SessionModel sessionModel = new SessionModel(sessionStr);
		if (checkUser(sessionModel)) {
			delay(sessionModel, response);
			return new Body().setMessage("您好，" + getUser(request, response).getUserName() + "，您已经登录，无需再次登录");
		}
		HashMap<String, String> params = getParamMap(request);
		if (!params.containsKey("userName")) {
			return new Body().setStatus("201").setMessage("登录失败");
		}
		logger.debug("user name : " + params.get("userName"));
		UserModel um = userCache.get(params.get("userName").toString());
		if (um == null) {
			return new Body().setStatus("201").setMessage("找不到账号，登录失败");
		}
		logger.debug("user password : " + um.getPassword());
		logger.debug("params password : " + params.get("password"));
		if (um.getPassword().equals(params.get("password").toString())) {
			String sessionId = DesUtil.encrypt(String.format(LOGGIN_FORMAT, LoginType.userName.index, um.getId(), um.getUserName(), getExpire()));
			Cookie cookie = new Cookie(COOKIE_KEY, sessionId);
			cookie.setPath(COOKIE_PATH);
			cookie.setMaxAge(COOKIE_SECONDS);
			response.addCookie(cookie);
			return new LoginBody().setSessionId(sessionId).setMessage("登录成功");
		}
		return new Body().setStatus("201").setMessage("密码不对，登录失败");
	}

	@ResponseBody
	@RequestMapping("/getuser")
	public UserModel getUser(HttpServletRequest request, HttpServletResponse response) {
		String sessionStr = getSessionId(request);
		sessionStr = DesUtil.decrypt(sessionStr);
		SessionModel sessionModel = new SessionModel(sessionStr);
		delay(sessionModel, response);
		UserModel um = getUser(sessionModel);
		um.setPermissionsBitmap(null);
		return um;
	}

	@ResponseBody
	@RequestMapping("/checkuser")
	public String checkUser(HttpServletRequest request, HttpServletResponse response) {
		String sessionStr = getSessionId(request);
		sessionStr = DesUtil.decrypt(sessionStr);
		SessionModel sessionModel = new SessionModel(sessionStr);
//		delay(sessionModel, response);
		return checkUser(sessionModel) + "," + delay(sessionModel, response);
	}

	@ResponseBody
	@RequestMapping("/delay")
	public String delay(HttpServletRequest request, HttpServletResponse response) {
		String sessionStr = getSessionId(request);
		sessionStr = DesUtil.decrypt(sessionStr);
		SessionModel sessionModel = new SessionModel(sessionStr);
		String newSession = delay(sessionModel, response);
		return (newSession != null) + "," + newSession;
	}

	@ResponseBody
	@RequestMapping("/checkurl")
	public String checkUrl(HttpServletRequest request, HttpServletResponse response) {
		String sessionStr = getSessionId(request);
		sessionStr = DesUtil.decrypt(sessionStr);
		SessionModel sessionModel = new SessionModel(sessionStr);
		String newSession = delay(sessionModel, response);
		if (!checkUser(sessionModel)) {
			logger.debug("checkurl by 1 false");
			return false + "," + newSession;
		}
		UserModel um = getUser(sessionModel);
		if (um == null) {
			logger.debug("checkurl by 2 false");
			return false + "," + newSession;
		}
		PermissionsModel permissionsModel = permissionsCache.get(permissionsCache.get(getSystemName_Url(request)));
		logger.debug("url key : " + getSystemName_Url(request));
		logger.debug("permissionsModel : " + permissionsModel);
		logger.debug("bitmap : " + um.getPermissionsBitmap());
		// 系统存了该URL并且该URL的bitmap是没有值的
		if (permissionsModel != null && um.getPermissionsBitmap() != null && !um.getPermissionsBitmap().contains(permissionsModel.getId())) {
			logger.debug("checkurl by 3 false");
			return false + "," + newSession;
		}
		logger.debug("checkurl true");
		return true + "," + newSession;
	}

	private UserModel getUser(SessionModel sessionModel) {
		if (!sessionModel.isValid()) {
			return new UserModel();
		}
		if (sessionModel.getExpireTime() < System.currentTimeMillis()) {
			return new UserModel();
		}
		UserModel um = userCache.get(sessionModel.getUserName());
		if (um == null) {
			return new UserModel();
		}
		return um;
	}

	private boolean checkUser(SessionModel sessionModel) {
		if (!sessionModel.isValid()) {
			return false;
		}
		if (sessionModel.getExpireTime() < System.currentTimeMillis()) {
			return false;
		}
		UserModel um = userCache.get(sessionModel.getUserName());
		if (um == null) {
			logger.debug("check user is null...");
			return false;
		}
		return true;
	}

	public String delay(SessionModel sessionModel, HttpServletResponse response) {
		UserModel um = getUser(sessionModel);
		if (um == null) {
			return null;
		}
		String newSessionId = DesUtil.encrypt(String.format(LOGGIN_FORMAT, LoginType.userName.index, um.getId(), um.getUserName(), getExpire()));
		Cookie cookie = new Cookie(COOKIE_KEY, newSessionId);
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(COOKIE_SECONDS);
		response.addCookie(cookie);
		return newSessionId;
	}

	private long getExpire() {
		return COOKIE_SECONDS * 1000 + System.currentTimeMillis();
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
				if (COOKIE_KEY.equals(cookie.getName())) {
					logger.debug("cookie : " + cookie.getName() + " | " + cookie.getValue());
					sessionCookie = cookie;
					break;
				}
			}
		}
		String paramKey = getParamMap(request).get(COOKIE_KEY);
		if (StringUtils.isNotBlank(paramKey)) {
			return paramKey;
		}
		return sessionCookie == null ? null : sessionCookie.getValue();
	}

}
