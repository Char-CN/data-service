package org.blazer.scheduler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.util.HMap;
import org.blazer.scheduler.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "jobService")
public class JobService {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Job> findAll() throws Exception {
		String sql = "select * from scheduler_job where enable = 1";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		List<Job> rst = new ArrayList<Job>();
		for (Map<String, Object> map : list) {
			Job job = HMap.to(map, Job.class);
			rst.add(job);
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

}
