package org.blazer.dataservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> getConfigById(Integer id) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from ds_datasource");
		return list;
	}

}
