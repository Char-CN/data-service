package org.blazer.dataservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.body.GroupBody;
import org.blazer.dataservice.body.view.ViewConfigBody;
import org.blazer.dataservice.body.view.ViewConfigDetailBody;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "viewService")
public class ViewService {

	private static Logger logger = LoggerFactory.getLogger(DataService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<GroupBody> findTreeById(HashMap<String, String> params) {
		logger.debug("qeury id " + params.get("id"));
		String sql = "select * from ds_group where parent_id=? order by order_asc";
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

	public List<ViewConfigBody> getConfigsByGroupId(HashMap<String, String> params) {
		logger.debug("qeury id " + params.get("id"));
		String sql = "select * from ds_config where group_id=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		List<ViewConfigBody> rst = new ArrayList<ViewConfigBody>();
		for (Map<String, Object> map : list) {
			ViewConfigBody vcb = new ViewConfigBody();
			vcb.setId(IntegerUtil.getInt0(map.get("id")));
			vcb.setConfigType(StringUtil.getStrEmpty(map.get("config_type")));
			vcb.setConfigName(StringUtil.getStrEmpty(map.get("config_name")));
			vcb.setList(new ArrayList<ViewConfigDetailBody>());
			rst.add(vcb);
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

	public ViewConfigBody getConfigById(HashMap<String, String> params) {
		logger.debug("qeury id " + params.get("id"));
		String sql = "select * from ds_config where id=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		if (list.size() == 0) {
			return new ViewConfigBody();
		}
		ViewConfigBody vcb = new ViewConfigBody();
		vcb.setId(IntegerUtil.getInt0(list.get(0).get("id")));
		vcb.setDatasourceId(IntegerUtil.getInt0(list.get(0).get("datasource_id")));
		vcb.setConfigType(StringUtil.getStrEmpty(list.get(0).get("config_type")));
		vcb.setConfigName(StringUtil.getStrEmpty(list.get(0).get("config_name")));
		vcb.setList(new ArrayList<ViewConfigDetailBody>());
		sql = "select * from ds_config_detail where config_id=?";
		List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql, vcb.getId());
		for (Map<String, Object> map : list2) {
			ViewConfigDetailBody vcdb = new ViewConfigDetailBody();
			vcdb.setId(IntegerUtil.getInt0(map.get("id")));
			vcdb.setDatasourceId(IntegerUtil.getInt0(map.get("datasource_id")));
			if (vcdb.getDatasourceId() == 0) {
				vcdb.setDatasourceId(vcb.getDatasourceId());
			}
			vcdb.setKey(StringUtil.getStrEmpty(map.get("key")));
			vcdb.setValues(StringUtil.getStrEmpty(map.get("values")));
			vcb.getList().add(vcdb);
		}
		return vcb;
	}

}
