package org.blazer.dataservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blazer.userservice.core.filter.PermissionsFilter;
import org.blazer.userservice.core.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "analyticService")
public class AnalyticService {

	private static Logger logger = LoggerFactory.getLogger(AnalyticService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> getRunTask(HashMap<String, String> params) throws Exception {
		String sql = "SELECT mut.user_id as id, COUNT(*) as ct, MAX(DATE_FORMAT(st.execute_time, '%Y-%m-%d')) as max_time"
				+ " FROM scheduler_task st"
				+ " INNER JOIN mapping_user_task mut ON st.task_name=mut.task_name"
				+ " WHERE st.type_name='right_now'"
				+ " GROUP BY mut.user_id"
				+ " ORDER BY ct desc";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		logger.debug(list.toString());
		StringBuilder ids = new StringBuilder();
		for (Map<String, Object> map : list) {
			if (ids.length() != 0) {
				ids.append(",");
			}
			ids.append(map.get("id"));
		}
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		logger.debug(users.toString());
		for (int i = 0; i < list.size(); i++) {
			list.get(i).put("user_name", users.get(i).getUserNameCn());
		}
		logger.debug(list.toString());
		return list;
	}

	public List<Map<String, Object>> getAddTask(HashMap<String, String> paramMap) throws Exception {
		String sql = "SELECT user_id as id, COUNT(1) AS ct, MAX(DATE_FORMAT(mtime, '%Y-%m-%d')) AS max_mtime, MAX(DATE_FORMAT(ctime, '%Y-%m-%d')) AS max_ctime"
				+ " FROM ds_config"
				+ " WHERE `enable`=1"
				+ " AND user_id IS NOT NULL "
				+ " GROUP BY user_id";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		logger.debug(list.toString());
		StringBuilder ids = new StringBuilder();
		for (Map<String, Object> map : list) {
			if (ids.length() != 0) {
				ids.append(",");
			}
			ids.append(map.get("id"));
		}
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		logger.debug(users.toString());
		for (int i = 0; i < list.size(); i++) {
			list.get(i).put("user_name", users.get(i).getUserNameCn());
		}
		logger.debug(list.toString());
		return list;
	}

}
