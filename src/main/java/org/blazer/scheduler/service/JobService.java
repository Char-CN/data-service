package org.blazer.scheduler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.util.HMap;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.JobParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "jobService")
public class JobService {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Transactional(propagation=Propagation.NOT_SUPPORTED,readOnly=true)
	public List<Job> findAll() throws Exception {
		String sql = "select * from scheduler_job where enable = 1";
		List<Map<String, Object>> jobList = jdbcTemplate.queryForList(sql);
		sql = "select * from scheduler_job_param where enable = 1";
		List<Map<String, Object>> paramList = jdbcTemplate.queryForList(sql);
		List<Job> rstList = new ArrayList<Job>();
		for (Map<String, Object> map : jobList) {
			Job job = HMap.to(map, Job.class);
			List<JobParam> params = new ArrayList<JobParam>();
			for (Map<String, Object> map2 : paramList) {
				if (IntegerUtil.getInt0(map2.get("job_id")) == job.getId()) {
					JobParam jp = HMap.to(map2, JobParam.class);
					params.add(jp);
				}
			}
			job.setParams(params);
			rstList.add(job);
		}
		logger.debug("rst size : " + rstList.size());
		return rstList;
	}

	@Transactional(propagation=Propagation.NOT_SUPPORTED,readOnly=true)
	public List<Job> findJobById(String idStr) throws Exception {
		String sql = "select * from scheduler_job where id in (" + idStr + ") enable = 1";
		List<Map<String, Object>> jobList = jdbcTemplate.queryForList(sql);
		sql = "select * from scheduler_job_param where job_id in (" + idStr + ") enable = 1";
		List<Map<String, Object>> paramList = jdbcTemplate.queryForList(sql);
		List<Job> rstList = new ArrayList<Job>();
		for (Map<String, Object> map : jobList) {
			Job job = HMap.to(map, Job.class);
			List<JobParam> params = new ArrayList<JobParam>();
			for (Map<String, Object> map2 : paramList) {
				if (IntegerUtil.getInt0(map2.get("job_id")) == job.getId()) {
					JobParam jp = HMap.to(map2, JobParam.class);
					params.add(jp);
				}
			}
			job.setParams(params);
			rstList.add(job);
		}
		logger.debug("rst size : " + rstList.size());
		return rstList;
	}

	@Transactional
	public void saveJob(Job job) throws Exception {
		logger.debug("save job : " + job);
		if (job.getId() != null) {
			String sql = "update scheduler_job set type=?, job_name=?, cron=?, command=? where id=?";
			jdbcTemplate.update(sql, job.getType(), job.getJobName(), job.getCron(), job.getCommand(), job.getId());
		} else {
			String sql = "insert into scheduler_job(type, job_name, cron, command) values(?, ?, ?, ?)";
			jdbcTemplate.update(sql, job.getType(), job.getJobName(), job.getCron(), job.getCommand());
			sql = "select max(id) as id from scheduler_job";
			job.setId(IntegerUtil.getInt0(jdbcTemplate.queryForList(sql).get(0).get("id")));
		}
		String deleteSql = "delete from scheduler_job_param where job_id=?";
		jdbcTemplate.update(deleteSql, job.getId());
		if (job.getParams() != null) {
			for (JobParam jp : job.getParams()) {
				String sql = "insert into scheduler_job_param(job_id, param_name, title_name, default_value) values(?, ?, ?, ?)";
				jdbcTemplate.update(sql, job.getId(), jp.getParamName(), jp.getTitleName(), jp.getDefaultValue());
			}
		}
		logger.debug("save job end : " + job);
	}

	@Transactional
	public void deleteJob(Integer jobId) throws Exception {
		logger.debug("delete job : " + jobId);
		String sql = "update scheduler_job set enable=0 where id=?";
		jdbcTemplate.update(sql, jobId);
	}

}
