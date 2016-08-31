package org.blazer.dataservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.body.PageBody;
import org.blazer.dataservice.model.USPermission;
import org.blazer.dataservice.model.USRole;
import org.blazer.dataservice.model.USUser;
import org.blazer.dataservice.util.HMap;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.SqlUtil;
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

	/**
	 * TODO : 用户相关
	 */

	public PageBody<USUser> findUserByPage(HashMap<String, String> params) {
		PageBody<USUser> pb = new PageBody<USUser>();
		String where = " where 1=1 and enable = 1 ";
		String sql = "select * from us_user " + where + " limit ?,?";
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
			// user.setPassword(StringUtil.getStrEmpty(map.get("password")));
			user.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
			user.setEmail(StringUtil.getStrEmpty(map.get("email")));
			user.setRemark(StringUtil.getStrEmpty(map.get("remark")));
			userList.add(user);
		}
		// pb.setPage(1);
		// pb.setRows(10);
		pb.setTotal(IntegerUtil.getInt0(jdbcTemplate.queryForList("select count(0) as ct from us_user " + where).get(0).get("ct")));
		pb.setRows(userList);
		logger.debug(pb.toString());
		return pb;
	}

	public USUser findUserById(HashMap<String, String> params) {
		String sql = "select * from us_user where id = ? and enable=1";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		USUser user = new USUser();
		if (list.size() == 0) {
			return user;
		}
		Map<String, Object> map = list.get(0);
		user.setId(IntegerUtil.getInt0(map.get("id")));
		user.setUserName(StringUtil.getStrEmpty(map.get("user_name")));
		user.setUserNameCn(StringUtil.getStrEmpty(map.get("user_name_cn")));
		// user.setPassword(StringUtil.getStrEmpty(map.get("password")));
		user.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
		user.setEmail(StringUtil.getStrEmpty(map.get("email")));
		user.setRemark(StringUtil.getStrEmpty(map.get("remark")));
		return user;
	}

	public void saveUser(USUser user, String roleIds) {
		Integer userId = null;
		if (user.getId() == null) {
			String sql = "insert into us_user(user_name,user_name_cn,email,phone_number,remark) values(?,?,?,?,?)";
			jdbcTemplate.update(sql, user.getUserName(), user.getUserNameCn(), user.getEmail(), user.getPhoneNumber(), user.getRemark());
			userId = IntegerUtil.getInt0(jdbcTemplate.queryForMap("select max(id) as id from us_user where enable=1").get("id"));
		} else {
			userId = user.getId();
			String sql = "update us_user set user_name=?,user_name_cn=?,email=?,phone_number=?,remark=? where id=?";
			jdbcTemplate.update(sql, user.getUserName(), user.getUserNameCn(), user.getEmail(), user.getPhoneNumber(), user.getRemark(), user.getId());
		}
		addUserRole(userId, roleIds);
	}

	public void delUser(Integer id) {
		logger.debug("userId " + id);
		String sql = "update us_user set enable=0 where id=?";
		jdbcTemplate.update(sql, id);
	}

	public void delUserRoleByUserId(Integer id) {
		logger.debug("userId " + id);
		String sql = "delete from us_user_role where user_id = ?";
		jdbcTemplate.update(sql, id);
	}

	public void addUserRole(Integer userId, String roleIds) {
		logger.debug("userId " + userId);
		logger.debug("roleIds " + roleIds);
		delUserRoleByUserId(userId);
		String sql = "insert into us_user_role(user_id, role_id) values(?, ?)";
		if (StringUtils.isNotEmpty(roleIds)) {
			for (String id : StringUtils.split(roleIds, ",")) {
				if (IntegerUtil.getInt(id) != null) {
					logger.debug(SqlUtil.Show(sql, userId, IntegerUtil.getInt(id)));
					jdbcTemplate.update(sql, userId, IntegerUtil.getInt(id));
				}
			}
		}
	}

	public String findRoleByUserId(Integer userId) {
		logger.debug("userId " + userId);
		String sql = "select GROUP_CONCAT(role_id) as roleids from us_user_role where user_id=? group by user_id";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
		String roleIds = list.size() == 0 ? "" : StringUtil.getStrEmpty(list.get(0).get("roleids"));
		logger.debug(roleIds);
		return roleIds;
	}

	/**
	 * TODO : 角色相关
	 */

	/**
	 * @return
	 * @throws Exception
	 */
	public List<USRole> findRoleAll() {
		try {
			String sql = "select * from us_role where enable=1";
			List<Map<String, Object>> rst = jdbcTemplate.queryForList(sql);
			List<USRole> list = HMap.toList(rst, USRole.class);
			logger.debug("rst size : " + rst.size());
			if (list.size() > 0) {
				logger.debug("role : " + list.get(0));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<USRole>();
	}

	public void saveRole(USRole role) {

	}

	public void delRole(Integer id) {
		logger.debug("del role id " + id);

	}

	/**
	 * TODO : 权限相关
	 */

	public void savePermission(USPermission permission) {

	}

	public void delPermission(Integer id) {
		logger.debug("del permission id " + id);

	}

}
