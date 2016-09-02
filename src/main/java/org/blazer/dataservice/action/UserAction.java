package org.blazer.dataservice.action;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.body.PageBody;
import org.blazer.dataservice.dao.CustomJdbcDao;
import org.blazer.dataservice.entity.USRole;
import org.blazer.dataservice.entity.USSystem;
import org.blazer.dataservice.entity.USUser;
import org.blazer.dataservice.service.UserService;
import org.blazer.dataservice.util.HMap;
import org.blazer.dataservice.util.IntegerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "userAction")
@RequestMapping("/user")
public class UserAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(UserAction.class);

	@Autowired
	UserService userService;

	@Autowired
	CustomJdbcDao customJdbcDao;

	/**
	 * TODO : 系统相关
	 */

	@ResponseBody
	@RequestMapping("/findSystemAll")
	public List<USSystem> findSystemAll(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return userService.findSystemAll();
	}

	@ResponseBody
	@RequestMapping("/findSystemByPage")
	public PageBody<USSystem> findSystemByPage(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return userService.findSystemByPage(getParamMap(request));
	}

	@ResponseBody
	@RequestMapping("/findSystemById")
	public USSystem findSystemById(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		HashMap<String, String> params = getParamMap(request);
		return userService.findSystemById(params);
	}

	@ResponseBody
	@RequestMapping("/saveSystem")
	public Body saveSystem(@RequestBody USSystem system) throws Exception {
		logger.debug("system : " + system);
		try {
			userService.saveSystem(system);
		} catch (Exception e) {
			return new Body().error().setMessage("保存失败：" + e.getMessage());
		}
		return new Body().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/delSystem")
	public Body delSystem(@RequestParam Integer id) throws Exception {
		logger.debug("system id : " + id);
		try {
			userService.delSystem(id);
		} catch (Exception e) {
			return new Body().error().setMessage("删除失败：" + e.getMessage());
		}
		return new Body().setMessage("删除成功！");
	}
	
	/**
	 * TODO : 用户相关
	 */

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
		// USUser user = ((Map<String, Object>)
		// params.get("user")).to(USUser.class);
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
	@RequestMapping("/delUser")
	public Body delUser(@RequestParam Integer id) throws Exception {
		logger.debug("userid : " + id);
		try {
			userService.delUser(id);
		} catch (Exception e) {
			return new Body().error().setMessage("删除失败：" + e.getMessage());
		}
		return new Body().setMessage("删除成功！");
	}

	/**
	 * TODO : 角色相关
	 */

	@ResponseBody
	@RequestMapping("/findRoleAll")
	public List<USRole> findRoleAll(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return userService.findRoleAll();
	}

	@ResponseBody
	@RequestMapping("/findRoleByPage")
	public PageBody<USRole> findRoleByPage(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return userService.findRoleByPage(getParamMap(request));
	}

	@ResponseBody
	@RequestMapping("/findRoleById")
	public USRole findRoleById(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		HashMap<String, String> params = getParamMap(request);
		return userService.findRoleById(params);
	}

	@ResponseBody
	@RequestMapping("/saveRole")
	public Body saveRole(@RequestBody USRole role) throws Exception {
		logger.debug("role : " + role);
		try {
			userService.saveRole(role);
		} catch (Exception e) {
			return new Body().error().setMessage("保存失败：" + e.getMessage());
		}
		return new Body().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/delRole")
	public Body delRole(@RequestParam Integer id) throws Exception {
		logger.debug("role id : " + id);
		try {
			userService.delRole(id);
		} catch (Exception e) {
			return new Body().error().setMessage("删除失败：" + e.getMessage());
		}
		return new Body().setMessage("删除成功！");
	}

	/**
	 * TODO : 权限相关
	 */
}
