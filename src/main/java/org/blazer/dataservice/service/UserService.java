package org.blazer.dataservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.body.PageBody;
import org.blazer.dataservice.model.USPermission;
import org.blazer.dataservice.model.USRole;
import org.blazer.dataservice.model.USUser;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public PageBody<USUser> findUserByPage(HashMap<String, String> params) {
		PageBody<USUser> pb = new PageBody<USUser>();
		String sql = "select * from us_user limit ?,?";
		int start = (IntegerUtil.getInt1(params.get("page")) - 1) * IntegerUtil.getInt0(params.get("rows"));
		int end = IntegerUtil.getInt0(params.get("rows"));
		logger.debug("start : " + start);
		logger.debug("end : " + end);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, start, end);
		logger.debug("list size : " + list.size());
		List<USUser> userList = new ArrayList<USUser>();
		for (Map<String, Object> map : list) {
			USUser user = new USUser();
			user.setId(IntegerUtil.getInt0(map.get("id")));
			user.setUserName(StringUtil.getStrEmpty(map.get("user_name")));
			user.setUserNameCn(StringUtil.getStrEmpty(map.get("user_name_cn")));
			user.setPassword(StringUtil.getStrEmpty(map.get("password")));
			user.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
			user.setEmail(StringUtil.getStrEmpty(map.get("email")));
			user.setRemark(StringUtil.getStrEmpty(map.get("remark")));
			userList.add(user);
		}
//		pb.setPage(1);
//		pb.setRows(10);
		pb.setTotal(IntegerUtil.getInt0(jdbcTemplate.queryForList("select count(0) as ct from us_user").get(0).get("ct")));
		pb.setRows(userList);
		logger.debug(pb.toString());
		return pb;
	}

	public USUser findUserById(HashMap<String, String> params) {
		String sql = "select * from us_user where id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		USUser user = new USUser();
		if (list.size() == 0) {
			return user;
		}
		Map<String, Object> map = list.get(0);
		user.setId(IntegerUtil.getInt0(map.get("id")));
		user.setUserName(StringUtil.getStrEmpty(map.get("user_name")));
		user.setUserNameCn(StringUtil.getStrEmpty(map.get("user_name_cn")));
		user.setPassword(StringUtil.getStrEmpty(map.get("password")));
		user.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
		user.setEmail(StringUtil.getStrEmpty(map.get("email")));
		user.setRemark(StringUtil.getStrEmpty(map.get("remark")));
		return user;
	}

	public void saveUser(USUser user) {

	}

	public void delUser(Integer id) {
		logger.debug("del user id " + id);
	}

	public void saveRole(USRole role) {

	}

	public void delRole(Integer id) {
		logger.debug("del role id " + id);

	}

	public void savePermission(USPermission permission) {

	}

	public void delPermission(Integer id) {
		logger.debug("del permission id " + id);

	}

}
