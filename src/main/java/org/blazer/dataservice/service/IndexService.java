package org.blazer.dataservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.body.GroupBody;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "indexService")
public class IndexService {

	private static Logger logger = LoggerFactory.getLogger(DataService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<GroupBody> findTreeById(HashMap<String, String> params) {
		logger.debug("qeury id " + params.get("id"));
		String sql = "select * from ds_group where parent_id=? order by order_asc;";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		List<GroupBody> rst = new ArrayList<GroupBody>();
		for (Map<String, Object> map : list) {
			GroupBody gb = new GroupBody();
			gb.setId(IntegerUtil.getInt0(map.get("id")));
			gb.setText(StringUtil.getStrEmpty(map.get("group_name")));
			gb.setState("closed");
			rst.add(gb);
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

}
