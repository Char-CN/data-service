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
		String sql = "select * from ds_config where group_id=? and enable=1";
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
		String sql = "select * from ds_config where id=? and enable=1";
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
		sql = "select * from ds_config_detail where config_id=? and enable=1";
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

	public void saveConfig(ViewConfigBody config) {
		// 应该加入事务，暂未处理
		// 新增config
		if (config.getId() == null) {
			String insertConfig = "insert into ds_config(group_id,datasource_id,config_name,config_type,enable) values(?,?,?,?,?)";
			int code = jdbcTemplate.update(insertConfig, config.getGroupId(), config.getDatasourceId(), config.getConfigName(), "1", 1);
			System.out.println("inset code : " + code);
			String selectMaxId = "select max(id) as max_id from ds_config";
			Integer maxId = IntegerUtil.getInt0(jdbcTemplate.queryForList(selectMaxId).get(0).get("max_id"));
			if (maxId == null) {
				throw new RuntimeException("insert data error! maxId is null!");
			}
			config.setId(maxId);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("id", "" + maxId);
			ViewConfigBody maxConfigBody = getConfigById(params);
			if (config.getId() != maxConfigBody.getId() || config.getGroupId() != maxConfigBody.getGroupId()
					|| config.getDatasourceId() != maxConfigBody.getDatasourceId() || !config.getConfigName().equals(maxConfigBody.getConfigName())
					|| !config.getConfigType().equals(maxConfigBody.getConfigType())) {
				throw new RuntimeException("insert data error!");
			}
		}
		// 修改config
		else {
			String updateConfig = "update ds_config set group_id=?,datasource_id=?,config_name=?,config_type=? where id=?";
			int code = jdbcTemplate.update(updateConfig, config.getGroupId(), config.getDatasourceId(), config.getConfigName(), "1", config.getId());
			System.out.println("update code : " + code);
		}

		// 软删除该configId的details
		String deleteDetail = "update ds_config_detail set enable=0 where config_id=?";
		int code = jdbcTemplate.update(deleteDetail, config.getId());
		System.out.println("delete code : " + code);

		// 增加detials
		for (int i = 0; i < config.getList().size(); i++) {
			ViewConfigDetailBody detail = config.getList().get(i);
			String insertDetail = "insert into ds_config_detail(datasource_id,config_id,key,values,order_asc,enable) values(?,?,?,?,?,?)";
			jdbcTemplate.update(insertDetail, config.getDatasourceId(), config.getId(), detail.getKey(), detail.getValues(), (i + 1), 1);
		}

	}

}
