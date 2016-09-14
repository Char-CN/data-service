package org.blazer.dataservice.cache;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.model.UserModel;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.roaringbitmap.buffer.MutableRoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * disk cache
 * 
 * 流程：
 * 
 * 1.缓存用户与所对应权限Bigmap
 * 
 * 2.缓存权限的map
 * 
 * 3.缓存用户-角色、角色-权限
 * 
 * @author hyy
 *
 */
@Component(value = "userCache")
public class UserCache extends BaseCache implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(UserCache.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	public void init() {
		// 先清空
		this.clear();
		// 查询所有权限
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT uu.id, uu.user_name, uu.email, uu.`password` as `password`, uu.phone_number, uu.user_name_cn, group_concat(up.id) as permissions_ids");
		sql.append(" FROM us_user uu");
		sql.append(" LEFT JOIN us_user_role uur ON uu.id = uur.user_id");
		sql.append(" LEFT JOIN (SELECT * FROM us_role WHERE `enable`=1) ur ON ur.id = uur.role_id");
		sql.append(" LEFT JOIN us_role_permissions urp ON urp.role_id = ur.id");
		sql.append(" LEFT JOIN (SELECT * FROM us_permissions WHERE `enable`=1) up ON urp.permissions_id = up.id");
		sql.append(" WHERE uu.`enable`=1 GROUP BY uu.id");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
		for (Map<String, Object> map : list) {
			UserModel userModel = new UserModel();
			userModel.setId(IntegerUtil.getInt0(map.get("id")));
			userModel.setUserName(StringUtil.getStrEmpty(map.get("user_name")));
			userModel.setUserNameCn(StringUtil.getStrEmpty(map.get("user_name_cn")));
			userModel.setPassword(StringUtil.getStrEmpty(map.get("password")));
			userModel.setEmail(StringUtil.getStrEmpty(map.get("email")));
			userModel.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
			String permissions_ids = StringUtil.getStrEmpty(map.get("permissions_ids"));
			MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
			for (String id : StringUtils.splitPreserveAllTokens(permissions_ids, ",")) {
				bitmap.add(IntegerUtil.getInt0(id));
			}
			userModel.setPermissionsBitmap(bitmap);
			logger.debug("init user : " + userModel);
			this.add(userModel);
		}
		logger.info("init user size : " + list.size());
	}

	public void init(Integer userId) {
		if (userId == null) {
			logger.error("userId is null.");
			return;
		}
		// 查询所有权限
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT uu.id, uu.user_name, uu.email, uu.`password` as `password`, uu.phone_number, uu.user_name_cn, group_concat(up.id) as permissions_ids");
		sql.append(" FROM us_user uu");
		sql.append(" LEFT JOIN us_user_role uur ON uu.id = uur.user_id");
		sql.append(" LEFT JOIN (SELECT * FROM us_role WHERE `enable`=1) ur ON ur.id = uur.role_id");
		sql.append(" LEFT JOIN us_role_permissions urp ON urp.role_id = ur.id");
		sql.append(" LEFT JOIN (SELECT * FROM us_permissions WHERE `enable`=1) up ON urp.permissions_id = up.id");
		sql.append(" WHERE uu.`enable`=1 AND uu.id=? GROUP BY uu.id");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), userId);
		Map<String, Object> map = list.get(0);
		UserModel userModel = new UserModel();
		userModel.setId(IntegerUtil.getInt0(map.get("id")));
		userModel.setUserName(StringUtil.getStrEmpty(map.get("user_name")));
		userModel.setUserNameCn(StringUtil.getStrEmpty(map.get("user_name_cn")));
		userModel.setPassword(StringUtil.getStrEmpty(map.get("password")));
		userModel.setEmail(StringUtil.getStrEmpty(map.get("email")));
		userModel.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
		String permissions_ids = StringUtil.getStrEmpty(map.get("permissions_ids"));
		MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
		for (String id : StringUtils.splitPreserveAllTokens(permissions_ids, ",")) {
			bitmap.add(IntegerUtil.getInt0(id));
		}
		userModel.setPermissionsBitmap(bitmap);
		logger.debug("init user : " + userModel);
		this.add(userModel);
	}

	public void init(String userName) {
		if (userName == null) {
			logger.error("userName is null.");
			return;
		}
		// 查询所有权限
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT uu.id, uu.user_name, uu.email, uu.`password` as `password`, uu.phone_number, uu.user_name_cn, group_concat(up.id) as permissions_ids");
		sql.append(" FROM us_user uu");
		sql.append(" LEFT JOIN us_user_role uur ON uu.id = uur.user_id");
		sql.append(" LEFT JOIN (SELECT * FROM us_role WHERE `enable`=1) ur ON ur.id = uur.role_id");
		sql.append(" LEFT JOIN us_role_permissions urp ON urp.role_id = ur.id");
		sql.append(" LEFT JOIN (SELECT * FROM us_permissions WHERE `enable`=1) up ON urp.permissions_id = up.id");
		sql.append(" WHERE uu.`enable`=1 AND uu.user_name=? GROUP BY uu.id");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), userName);
		if (list.size() == 0) {
			return;
		}
		Map<String, Object> map = list.get(0);
		UserModel userModel = new UserModel();
		userModel.setId(IntegerUtil.getInt0(map.get("id")));
		userModel.setUserName(StringUtil.getStrEmpty(map.get("user_name")));
		userModel.setUserNameCn(StringUtil.getStrEmpty(map.get("user_name_cn")));
		userModel.setPassword(StringUtil.getStrEmpty(map.get("password")));
		userModel.setEmail(StringUtil.getStrEmpty(map.get("email")));
		userModel.setPhoneNumber(StringUtil.getStrEmpty(map.get("phone_number")));
		String permissions_ids = StringUtil.getStrEmpty(map.get("permissions_ids"));
		MutableRoaringBitmap bitmap = new MutableRoaringBitmap();
		for (String id : StringUtils.splitPreserveAllTokens(permissions_ids, ",")) {
			bitmap.add(IntegerUtil.getInt0(id));
		}
		userModel.setPermissionsBitmap(bitmap);
		logger.debug("init user : " + userModel);
		this.add(userModel);
	}

	public void clear() {
		getCache().clear();
	}

	public void add(UserModel userModel) {
		logger.debug("add user cache : " + userModel);
		getCache().put(userModel.getUserName(), userModel);
	}

	public void remove(String userName) {
		logger.debug("remove user : " + userName);
		getCache().evict(userName);
	}

	public boolean contains(String userName) {
		return getCache().get(userName) != null;
	}

	public UserModel get(String userName) {
		if (!contains(userName)) {
			this.init(userName);
		}
		if (!contains(userName)) {
			return null;
		}
		return (UserModel) getCache().get(userName).get();
	}

	@Override
	public String getCacheName() {
		return "user_cache";
	}

}
