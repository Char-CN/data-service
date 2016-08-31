package org.blazer.dataservice.action;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.body.PageBody;
import org.blazer.dataservice.dao.CustomJdbcDao;
import org.blazer.dataservice.model.USRole;
import org.blazer.dataservice.model.USUser;
import org.blazer.dataservice.service.UserService;
import org.blazer.dataservice.util.HMap;
import org.blazer.dataservice.util.IntegerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "userAction")
@RequestMapping("/user")
public class UserAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(UserAction.class);

	@Autowired
	UserService userService;

	@Autowired
	CustomJdbcDao customJdbcDao;

	@ResponseBody
	@RequestMapping("/findUserByPage")
	public PageBody<USUser> findUserByPage(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return userService.findUserByPage(getParamMap(request));
	}

	@ResponseBody
	@RequestMapping("/findUserById")
	public HashMap<String, Object> findUserById(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		logger.debug("map : " + getParamMap(request));
		HashMap<String, String> params = getParamMap(request);
		map.put("user", userService.findUserById(params));
		map.put("role_ids", userService.findRoleByUserId(IntegerUtil.getInt0(params.get("id"))));
		return map;
	}

	@ResponseBody
	@RequestMapping("/saveUser")
	public Body saveUser(@RequestBody HashMap<String, Object> params) throws Exception {
		USUser user = HMap.to(params.get("user"), USUser.class);
		String roleIds = (String) params.get("roleIds");
//		USUser user = ((Map<String, Object>) params.get("user")).to(USUser.class);
		logger.debug("user : " + user);
		logger.debug("user : " + roleIds);
		try {
			userService.saveUser(user, roleIds);
		} catch (Exception e) {
			return new Body().error().setMessage("保存失败：" + e.getMessage());
		}
		return new Body().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/findRoleAll")
	public List<USRole> findRoleAll(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return userService.findRoleAll();
	}

}
