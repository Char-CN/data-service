package org.blazer.dataservice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.body.GroupBody;
import org.blazer.dataservice.body.PageBody;
import org.blazer.dataservice.body.TreeBody;
import org.blazer.dataservice.body.view.ViewConfigBody;
import org.blazer.dataservice.body.view.ViewConfigDetailBody;
import org.blazer.dataservice.body.view.ViewMappingConfigJobBody;
import org.blazer.dataservice.cache.ConfigCache;
import org.blazer.dataservice.cache.DataSourceCache;
import org.blazer.dataservice.entity.DSGroup;
import org.blazer.dataservice.entity.MappingConfigJob;
import org.blazer.dataservice.exception.NoPermissionsException;
import org.blazer.dataservice.exception.SystemRetentionParameters;
import org.blazer.dataservice.util.HMap;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.ParamsUtil;
import org.blazer.dataservice.util.SqlUtil;
import org.blazer.dataservice.util.StringUtil;
import org.blazer.scheduler.core.JobServer;
import org.blazer.scheduler.core.ProcessHelper;
import org.blazer.scheduler.core.TaskServer;
import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.JobParam;
import org.blazer.scheduler.entity.Status;
import org.blazer.scheduler.entity.Task;
import org.blazer.scheduler.entity.TaskType;
import org.blazer.scheduler.model.ResultModel;
import org.blazer.scheduler.model.TaskLog;
import org.blazer.scheduler.service.JobService;
import org.blazer.scheduler.service.TaskService;
import org.blazer.userservice.core.filter.PermissionsFilter;
import org.blazer.userservice.core.model.CheckUrlStatus;
import org.blazer.userservice.core.model.SessionModel;
import org.blazer.userservice.core.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "viewService")
public class ViewService {

	private static Logger logger = LoggerFactory.getLogger(ViewService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	JobService jobService;

	@Autowired
	TaskService taskService;

	@Autowired
	ConfigCache configCache;

	@Autowired
	DataSourceCache dataSourceCache;

	@Value("#{scriptProperties.script_path}")
	private String scriptPath;

	@Value("#{scriptProperties.script_name}")
	private String scriptName;

	@Value("#{scriptProperties.result_path}")
	private String resultPath;

	@Value("#{reportProperties.read_row_number}")
	private Integer readRowNumber;

	public ResultModel findReportByTaskName(HashMap<String, String> params) throws Exception {
		// 为了减小服务器压力，每次均只允许读取100行
		String taskName = StringUtil.getStrEmpty(params.get("taskName"));
		Integer skipRowNumber = IntegerUtil.getInt0(params.get("skipRowNumber"));
		Integer maxRowNumber = readRowNumber;
		if (skipRowNumber == 0) {
			maxRowNumber++;
		}
		ResultModel rm = ProcessHelper.readSingleLog(resultPath + File.separator + taskName + ".csv", skipRowNumber, maxRowNumber);
		return rm;
	}

	public TaskLog findTaskLogByName(HashMap<String, String> params) throws Exception {
		String taskName = StringUtil.getStrEmpty(params.get("taskName"));
		Integer skipRowNumber = IntegerUtil.getInt0(params.get("skipRowNumber"));
		return taskService.findTaskLogByName(taskName, skipRowNumber);
	}

	public Task findTaskByName(HashMap<String, String> params) throws Exception {
		return taskService.findTaskByName(params.get("taskName"));
	}

	public PageBody<Task> findTaskByUser(HashMap<String, String> params, SessionModel sm) throws Exception {
		PageBody<Task> pb = new PageBody<Task>();
		String sql = "";
		sql += "SELECT * FROM (";
		sql += " SELECT st.* FROM scheduler_task st";
		sql += " INNER JOIN scheduler_job sj ON st.job_id=sj.id";
		sql += " INNER JOIN mapping_config_job mcj ON sj.id=mcj.job_id";
		sql += "  WHERE LOCATE(CONCAT(',',?,','), CONCAT(',',mcj.email_userids,',')) ";
		sql += "   and st.execute_time>=?";
		sql += "   and st.execute_time<=?";
		sql += "   and st.job_id>=0";
		sql += " UNION ALL";
		sql += "  SELECT st.* FROM mapping_user_task mut";
		sql += "   INNER JOIN scheduler_task st ON st.task_name=mut.task_name";
		sql += "   WHERE mut.user_id=?";
		sql += "    and st.execute_time>=?";
		sql += "    and st.execute_time<=?";
		sql += "    and st.job_id>=0";
		sql += " ) t";
		sql += " ORDER BY t.execute_time DESC";
		sql += " limit ?,?;";
		int start = (IntegerUtil.getInt1(params.get("page")) - 1) * IntegerUtil.getInt0(params.get("rows"));
		int end = IntegerUtil.getInt0(params.get("rows"));
		String startTime = StringUtil.getStrEmpty(params.get("startTime"));
		String endTime = StringUtil.getStrEmpty(params.get("endTime"));
		logger.debug("id : " + sm.getUserId());
		logger.debug("startTime : " + startTime);
		logger.debug("endTime : " + endTime);
		logger.debug("start : " + start);
		logger.debug("end : " + end);
		logger.debug(SqlUtil.Show(sql, sm.getUserId(), startTime, endTime, sm.getUserId(), startTime, endTime, sm.getUserId(), startTime, endTime, start, end));
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, sm.getUserId(), startTime, endTime, sm.getUserId(), startTime, endTime, start, end);
		logger.debug("list size : " + list.size());
		List<Task> taskList = HMap.toList(list, Task.class);
		for (Task task : taskList) {
			task.setStatus(Status.get(task.getStatusId()));
			if (TaskType.cron_auto.toString().equals(task.getTypeName())) {
				String jobName = "已经删除的JOB";
				Job job = JobServer.getJobById(task.getJobId());
				if (job != null) {
					jobName = job.getJobName();
				}
				task.setRemark(jobName);
				task.setTypeName(TaskType.cron_auto.getCNName());
			} else {
				task.setTypeName(TaskType.right_now.getCNName());
			}
		}
		sql = "";
		sql += "SELECT count(0) AS ct FROM (";
		sql += " SELECT st.* FROM scheduler_task st";
		sql += " INNER JOIN scheduler_job sj ON st.job_id=sj.id";
		sql += " INNER JOIN mapping_config_job mcj ON sj.id=mcj.job_id";
		sql += "  WHERE LOCATE(CONCAT(',',?,','), CONCAT(',',mcj.email_userids,',')) ";
		sql += "   and st.execute_time>=?";
		sql += "   and st.execute_time<=?";
		sql += "   and st.job_id>=0";
		sql += " UNION ALL";
		sql += "  SELECT st.* FROM mapping_user_task mut";
		sql += "   INNER JOIN scheduler_task st ON st.task_name=mut.task_name";
		sql += "   WHERE mut.user_id=?";
		sql += "    and st.execute_time>=?";
		sql += "    and st.execute_time<=?";
		sql += "    and st.job_id>=0";
		sql += " ) t";
		pb.setTotal(IntegerUtil.getInt0(jdbcTemplate.queryForList(sql, sm.getUserId(), startTime, endTime, sm.getUserId(), startTime, endTime).get(0).get("ct")));
		pb.setRows(taskList);
		logger.debug(pb.toString());
		return pb;
	}

	public PageBody<Map<String, Object>> findTaskByAdmin(HashMap<String, String> params, SessionModel sm) throws Exception {
		PageBody<Map<String, Object>> pb = new PageBody<Map<String, Object>>();
		CheckUrlStatus cus = PermissionsFilter.checkUrl(sm, "isadmin");
		// 如果不是管理员
		if (cus != CheckUrlStatus.Success) {
			return pb;
		}
		String sql = "";
		sql += "SELECT * FROM (";
		sql += " SELECT st.*, mcj.email AS email, mcj.email_userids AS email_userids FROM scheduler_task st";
		sql += " INNER JOIN scheduler_job sj ON st.job_id=sj.id";
		sql += " INNER JOIN mapping_config_job mcj ON sj.id=mcj.job_id";
		sql += "  WHERE st.execute_time>=?";
		sql += "   and st.execute_time<=?";
		sql += " UNION ALL";
		sql += "  SELECT st.*, '' AS email, mut.user_id AS email_userids FROM mapping_user_task mut";
		sql += "   INNER JOIN scheduler_task st ON st.task_name=mut.task_name";
		sql += "   WHERE st.execute_time>=?";
		sql += "    and st.execute_time<=?";
		sql += " ) t";
		sql += " ORDER BY t.execute_time DESC";
		sql += " limit ?,?;";
		int start = (IntegerUtil.getInt1(params.get("page")) - 1) * IntegerUtil.getInt0(params.get("rows"));
		int end = IntegerUtil.getInt0(params.get("rows"));
		String startTime = StringUtil.getStrEmpty(params.get("startTime"));
		String endTime = StringUtil.getStrEmpty(params.get("endTime"));
		logger.debug("id : " + sm.getUserId());
		logger.debug("startTime : " + startTime);
		logger.debug("endTime : " + endTime);
		logger.debug("start : " + start);
		logger.debug("end : " + end);
		logger.debug(SqlUtil.Show(sql, startTime, endTime, startTime, endTime, start, end));
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, startTime, endTime, startTime, endTime, start, end);
		StringBuilder ids = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			ids.append(list.get(i).get("email_userids") + ((i + 1) == list.size() ? "" : ","));
		}
		System.out.println(ids.toString());
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		System.out.println(users.toString());
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			int userCount = StringUtils.splitByWholeSeparatorPreserveAllTokens(StringUtil.getStrEmpty(list.get(i).get("email_userids")), ",").length;
			String userNames = "";
			for (int j = 0; j == 0 || j < userCount; j++, index++) {
				if (j != 0) {
					userNames += ",";
				}
				userNames += users.get(index).getUserNameCn() == null ? "" : users.get(index).getUserNameCn();
			}
			list.get(i).put("email", StringUtil.getStrEmpty(list.get(i).get("email")));
			list.get(i).put("email_usernames", userNames);
			list.get(i).put("status", Status.get(IntegerUtil.getInt0(list.get(i).get("status_id"))));
			if (TaskType.cron_auto.toString().equals(list.get(i).get("type_name"))) {
				String jobName = "已经删除的JOB";
				Job job = JobServer.getJobById(IntegerUtil.getInt0(list.get(i).get("job_id")));
				if (job != null) {
					jobName = job.getJobName();
				}
				list.get(i).put("remark", jobName);
				list.get(i).put("type_name", TaskType.cron_auto.getCNName());
			} else {
				list.get(i).put("type_name", TaskType.right_now.getCNName());
			}
		}
		sql = "";
		sql += "SELECT count(0) AS ct FROM (";
		sql += " SELECT st.*, mcj.email AS email, mcj.email_userids AS email_userids FROM scheduler_task st";
		sql += " INNER JOIN scheduler_job sj ON st.job_id=sj.id";
		sql += " INNER JOIN mapping_config_job mcj ON sj.id=mcj.job_id";
		sql += "  WHERE st.execute_time>=?";
		sql += "   and st.execute_time<=?";
		sql += " UNION ALL";
		sql += "  SELECT st.*, '' AS email, mut.user_id AS email_userids FROM mapping_user_task mut";
		sql += "   INNER JOIN scheduler_task st ON st.task_name=mut.task_name";
		sql += "   WHERE st.execute_time>=?";
		sql += "    and st.execute_time<=?";
		sql += " ) t";
		pb.setTotal(IntegerUtil.getInt0(jdbcTemplate.queryForList(sql, startTime, endTime, startTime, endTime).get(0).get("ct")));
		pb.setRows(list);
		logger.debug(pb.toString());
		return pb;
	}

	public void addUserTaskMapping(Integer userId, String taskName) {
		String sql = "insert into mapping_user_task(user_id, task_name) values(?,?)";
		jdbcTemplate.update(sql, userId, taskName);
	}

	@Transactional
	public Task addTask(HashMap<String, String> params, SessionModel sm) throws RuntimeException {
		Task task = null;
		try {
			Integer configId = IntegerUtil.getInt0(params.get("config_id"));
			// 判断权限
			CheckUrlStatus cus = PermissionsFilter.checkUrl(sm, "isadmin");
			// 如果不是管理员
			if (cus != CheckUrlStatus.Success) {
				// 则需要检查数据库
				if (!checkUserOnGroup(sm.getUserId(), configCache.get(configId).getGroupId())) {
					logger.error("UserId=" + sm.getUserId());
					logger.error("ConfigId=" + configId);
					logger.error("GroupId=" + configCache.get(configId).getGroupId());
					logger.error("Config=" + configCache.get(configId));
					throw new NoPermissionsException("该用户无权执行该配置。无法执行！");
				}
			}
			// 判断是否是任务
			if (!configCache.get(configId).isTask()) {
				throw new RuntimeException("该配置不是一个任务。无法执行！");
			}
			// 增加参数
			StringBuilder cmdParams = new StringBuilder();
			List<JobParam> paramList = new ArrayList<JobParam>();
			cmdParams.append(" emails=").append(sm.getEmail());
			cmdParams.append(" result_path=").append(resultPath);
			// paramList.add(new JobParam("emails", sm.getEmail()));
			// paramList.add(new JobParam("result_path", resultPath));
			for (String key : params.keySet()) {
				cmdParams.append(" ").append(key).append("=").append(params.get(key));
				if ("config_id".equals(key)) {
					continue;
				}
				paramList.add(new JobParam(key, params.get(key)));
			}
			String cmd = "sh " + scriptPath + File.separator + scriptName + cmdParams.toString();
			logger.info("Real command : " + cmd);
			// paramList 是需要记录的参数信息
			task = TaskServer.spawnRightNowTaskProcess(cmd, paramList).getTask();
			task.setRemark(configCache.get(configId).getConfigName() + " 即时查询任务");
			taskService.updateTaskRemark(task);
			// 增加user和task映射关系
			addUserTaskMapping(sm.getUserId(), task.getTaskName());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
		return task;
	}

	public boolean checkUserOnGroup(Integer userId, Integer groupId) {
		String sql = "select 1 from ds_user_group where user_id=? and group_id=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId, groupId);
		return !(list == null || list.size() == 0);
	}

	public List<MappingConfigJob> findSchedulersAll() throws Exception {
		String sql = "select mcj.* from mapping_config_job mcj inner join scheduler_job sj on sj.id=mcj.job_id where mcj.enable=1 and sj.enable=1";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		List<MappingConfigJob> rst = HMap.toList(list, MappingConfigJob.class);
		StringBuilder ids = new StringBuilder();
		for (int i = 0; i < rst.size(); i++) {
			MappingConfigJob mcj = rst.get(i);
			ids.append(mcj.getEmailUserids() + ((i + 1) == rst.size() ? "" : ","));
		}
		System.out.println(ids.toString());
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		System.out.println(users.toString());
		int index = 0;
		for (int i = 0; i < rst.size(); i++) {
			MappingConfigJob mcj = rst.get(i);
			mcj.setJob(JobServer.getJobById(mcj.getJobId()));
			mcj.setConfigName(configCache.get(mcj.getConfigId()).getConfigName());
//			mcj.setConfigName(mcj.getJob().getJobName());
			int userCount = StringUtils.splitByWholeSeparatorPreserveAllTokens(mcj.getEmailUserids(), ",").length;
			String emails = "";
			for (int j = 0; j == 0 || j < userCount; j++, index++) {
				if (j != 0) {
					emails += ",";
				}
				emails += users.get(index).getUserNameCn() == null ? "" : users.get(index).getUserNameCn();
			}
			mcj.setEmailUserids(emails);
		}
//		for (MappingConfigJob mcj : rst) {
//			mcj.setJob(JobServer.getJobById(mcj.getJobId()));
//			mcj.setConfigName(configCache.get(mcj.getConfigId()).getConfigName());
//			// TODO
//		}
		return rst;
	}

	public List<MappingConfigJob> findSchedulersByConfigId(Integer id) throws Exception {
		String sql = "select * from mapping_config_job where config_id=? and enable=1";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
		List<MappingConfigJob> rst = HMap.toList(list, MappingConfigJob.class);
		for (MappingConfigJob mcj : rst) {
			// TODO
			mcj.setJob(JobServer.getJobById(mcj.getJobId()));
		}
		return rst;
	}

	public void deleteMappingConfigJob(Integer id) throws Exception {
		logger.debug("delete mapping config job : " + id);
		String sql = "update mapping_config_job set enable=0 where id=?";
		jdbcTemplate.update(sql, id);
	}

	public void saveMappingConfigJob(ViewMappingConfigJobBody vBody, SessionModel sm) throws Exception {
		HashMap<Integer, Integer> existsMap = new HashMap<Integer, Integer>();
		if (vBody.getConfigId() != null) {
			String sql = "select id, job_id from mapping_config_job where config_id=? and enable=1";
			List<Map<String, Object>> existsList = jdbcTemplate.queryForList(sql, vBody.getConfigId());
			for (Map<String, Object> map : existsList) {
				existsMap.put(IntegerUtil.getInt0(map.get("id")), IntegerUtil.getInt0(map.get("job_id")));
			}
		}
		for (MappingConfigJob mcj : vBody.getList()) {
			// mcj.getJob().getParams().add(new JobParam("result_path",
			// resultPath));
			jobService.saveJob(mcj.getJob());
			if (mcj.getId() == null) {
				String sql = "insert into mapping_config_job(config_id, job_id, user_id, result_mode, email, email_userids) values(?, ?, ?, ?, ?, ?)";
				jdbcTemplate.update(sql, mcj.getConfigId(), mcj.getJob().getId(), sm.getUserId(), mcj.getResultMode(), mcj.getEmail(), mcj.getEmailUserids());
				sql = "select max(id) as id from mapping_config_job";
				mcj.setId(IntegerUtil.getInt0(jdbcTemplate.queryForList(sql).get(0).get("id")));
			} else {
				String sql = "update mapping_config_job set config_id=?, job_id=?, user_id=?, result_mode=?, email=?, email_userids=? where id=?";
				jdbcTemplate.update(sql, mcj.getConfigId(), mcj.getJob().getId(), sm.getUserId(), mcj.getResultMode(), mcj.getEmail(), mcj.getEmailUserids(),
						mcj.getId());
				existsMap.remove(mcj.getId());
			}
			// mcj.getJob().getParams().add(new
			// JobParam("mapping_config_job_id", "" + mcj.getId()));
			StringBuilder cmdParams = new StringBuilder();
			cmdParams.append(" result_path=").append(resultPath);
			cmdParams.append(" mapping_config_job_id=").append(mcj.getId());
			for (JobParam jp : mcj.getJob().getParams()) {
				cmdParams.append(" ").append(jp.getParamName()).append("=").append(jp.getDefaultValue());
			}
			String cmd = "sh " + scriptPath + File.separator + scriptName + cmdParams.toString();
			mcj.getJob().setCommand(cmd);
			jobService.updateJobCommand(mcj.getJob().getId(), mcj.getJob().getCommand());
			// 单独增加参数
			// JobParam param2 = new JobParam("mapping_config_job_id", "" +
			// mcj.getId());
			// param2.setJobId(mcj.getJob().getId());
			// jobService.saveJobParam(param2);
			// mcj.getJob().getParams().add(param2);
			JobServer.reloadJob(mcj.getJob());
		}
		// remove job and mapping
		for (Integer id : existsMap.keySet()) {
			jobService.deleteJob(existsMap.get(id));
			deleteMappingConfigJob(id);
			JobServer.removeJob(existsMap.get(id));
		}
	}

	public List<GroupBody> findTreeById(HashMap<String, String> params, SessionModel sm) {
		logger.debug("qeury id " + params.get("id"));
		String sql = "select * from ds_group where parent_id=? order by order_asc";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		List<GroupBody> rst = new ArrayList<GroupBody>();
		for (Map<String, Object> map : list) {
			GroupBody gb = new GroupBody();
			gb.setId(IntegerUtil.getInt0(map.get("id")));
			gb.setText(StringUtil.getStrEmpty(map.get("group_name")));
			gb.setState("closed");
			// fa-c 兼容easyui的自定义fa-c
			gb.setIconCls("fa fa-cubes fa-1x fa-c");
			rst.add(gb);
			logger.debug(gb.toString());
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

	public List<GroupBody> findTreeByIdAndUserId(HashMap<String, String> params, SessionModel sm) {
		logger.debug("qeury id " + params.get("id"));
		logger.debug("SessionModel id " + sm.getUserId());
		String sql = "select * from ds_group where parent_id=? and id in(select group_id from ds_user_group where user_id=?) order by order_asc";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")), sm.getUserId());
		List<GroupBody> rst = new ArrayList<GroupBody>();
		for (Map<String, Object> map : list) {
			GroupBody gb = new GroupBody();
			gb.setId(IntegerUtil.getInt0(map.get("id")));
			gb.setText(StringUtil.getStrEmpty(map.get("group_name")));
			gb.setState("closed");
			// fa-c 兼容easyui的自定义fa-c
			gb.setIconCls("fa fa-cubes fa-1x fa-c");
			rst.add(gb);
			logger.debug(gb.toString());
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

	public void saveUserGroup(HashMap<String, String> params) {
		Integer userId = IntegerUtil.getInt0(params.get("id"));
		String sql = "delete from ds_user_group where user_id = ?";
		jdbcTemplate.update(sql, userId);
		sql = "insert into ds_user_group(group_id, user_id) values(?,?)";
		String[] group_ids = StringUtils.splitByWholeSeparator(StringUtil.getStrEmpty(params.get("group_ids")), ",");
		for (String group_id : group_ids) {
			jdbcTemplate.update(sql, IntegerUtil.getInt0(group_id), userId);
		}
	}

	public List<Integer> findUserGroupIds(HashMap<String, String> params) {
		// 逻辑：一级目录下有子目录的则过滤掉该一级目录id，若没有子目录，则需要加上该目录id
		String sql = "select dg.* from ds_user_group dug inner join ds_group dg on dug.group_id=dg.id where dug.user_id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		List<Integer> rstList = new ArrayList<Integer>();
		List<Integer> rootList = new ArrayList<Integer>();
		Set<Integer> rootSet = new HashSet<Integer>();
		// 先获取根目录
		for (Map<String, Object> map : list) {
			if (IntegerUtil.getInt0(map.get("parent_id")) == -1) {
				rootList.add(IntegerUtil.getInt0(map.get("id")));
			}
		}
		// 再把子目录加上
		for (Map<String, Object> map : list) {
			if (IntegerUtil.getInt0(map.get("parent_id")) == -1) {
				continue;
			}
			rstList.add(IntegerUtil.getInt0(map.get("id")));
			rootSet.add(IntegerUtil.getInt0(map.get("parent_id")));
		}
		// 如果存在根目录则不加入结果集
		for (Map<String, Object> map : list) {
			if (IntegerUtil.getInt0(map.get("parent_id")) == -1) {
				Integer id = IntegerUtil.getInt0(map.get("id"));
				if (!rootSet.contains(id)) {
					rstList.add(id);
				}
			}
		}
		return rstList;
	}

	private List<DSGroup> findGroupAll() throws Exception {
		String sql = "select * from ds_group order by order_asc";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return HMap.toList(list, DSGroup.class);
	}

	public List<TreeBody> findTreeAllByParentId(Integer parentId) throws Exception {
		List<DSGroup> groupList = findGroupAll();
		return findTreeAllByParentId(groupList, parentId);
	}

	private List<TreeBody> findTreeAllByParentId(List<DSGroup> groupList, Integer parentId) {
		List<TreeBody> treeList = new ArrayList<TreeBody>();
		for (DSGroup group : groupList) {
			if (group.getParentId() == parentId) {
				TreeBody body = new TreeBody();
				body.setId(group.getId());
				body.setText(group.getGroupName());
				body.setState("open");
				List<TreeBody> childList = findTreeAllByParentId(groupList, group.getId());
				body.setChildren(childList);
				if (childList.size() == 0) {
					body.setIconCls("fa fa-cube fa-1x fa-c");
				} else {
					body.setIconCls("fa fa-cubes fa-1x fa-c");
				}
				treeList.add(body);
			}
		}
		return treeList;
	}

	public List<ViewConfigBody> getConfigsByGroupId(HashMap<String, String> params) throws Exception {
		logger.debug("qeury id " + params.get("group_id"));
		String sql = "select * from ds_config where group_id=? and enable=1 order by order_asc, id";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("group_id")));
		List<ViewConfigBody> rst = new ArrayList<ViewConfigBody>();
		StringBuilder ids = new StringBuilder();
		for (Map<String, Object> map : list) {
			if (ids.length() != 0) {
				ids.append(",");
			}
			ids.append(map.get("user_id"));
		}
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			ViewConfigBody vcb = new ViewConfigBody();
			vcb.setId(IntegerUtil.getInt0(map.get("id")));
			vcb.setUserName(users.get(i).getUserNameCn());
			vcb.setConfigType(StringUtil.getStrEmpty(map.get("config_type")));
			vcb.setConfigName(StringUtil.getStrEmpty(map.get("config_name")));
			vcb.setRemark(StringUtil.getStrEmpty(map.get("remark")));
			vcb.setList(new ArrayList<ViewConfigDetailBody>());
			rst.add(vcb);
			logger.debug(vcb.toString());
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

	public List<ViewConfigBody> getConfigsByConfigNameAndAdmin(HashMap<String, String> params) throws Exception {
		logger.debug("qeury config name " + params.get("configName"));
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("select * from ds_config where enable=1 and config_name like '%" + SqlUtil.TransactSQLInjection(params.get("configName")) + "%' order by order_asc, id");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sbSql.toString());
		List<ViewConfigBody> rst = new ArrayList<ViewConfigBody>();
		StringBuilder ids = new StringBuilder();
		for (Map<String, Object> map : list) {
			if (ids.length() != 0) {
				ids.append(",");
			}
			ids.append(map.get("user_id"));
		}
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			ViewConfigBody vcb = new ViewConfigBody();
			vcb.setId(IntegerUtil.getInt0(map.get("id")));
			vcb.setUserName(users.get(i).getUserNameCn());
			vcb.setConfigType(StringUtil.getStrEmpty(map.get("config_type")));
			vcb.setConfigName(StringUtil.getStrEmpty(map.get("config_name")));
			vcb.setRemark(StringUtil.getStrEmpty(map.get("remark")));
			vcb.setGroupId(IntegerUtil.getInt(map.get("group_id")));
			vcb.setList(new ArrayList<ViewConfigDetailBody>());
			rst.add(vcb);
			logger.debug(vcb.toString());
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

	public List<ViewConfigBody> getConfigsByConfigNameAndUser(SessionModel sm, HashMap<String, String> params) throws Exception {
		logger.debug("qeury config name " + params.get("configName"));
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("select ds.* from (select * from ds_user_group where user_id=?) dug");
		sbSql.append(" inner join ds_config ds on dug.group_id=ds.group_id");
		sbSql.append(" where enable=1 and config_name like '%" + SqlUtil.TransactSQLInjection(params.get("configName")) + "%' order by order_asc, id");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sbSql.toString(), sm.getUserId());
		List<ViewConfigBody> rst = new ArrayList<ViewConfigBody>();
		StringBuilder ids = new StringBuilder();
		for (Map<String, Object> map : list) {
			if (ids.length() != 0) {
				ids.append(",");
			}
			ids.append(map.get("user_id"));
		}
		List<UserModel> users = PermissionsFilter.findAllUserByUserIds(ids.toString());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			ViewConfigBody vcb = new ViewConfigBody();
			vcb.setId(IntegerUtil.getInt0(map.get("id")));
			vcb.setUserName(users.get(i).getUserNameCn());
			vcb.setConfigType(StringUtil.getStrEmpty(map.get("config_type")));
			vcb.setConfigName(StringUtil.getStrEmpty(map.get("config_name")));
			vcb.setRemark(StringUtil.getStrEmpty(map.get("remark")));
			vcb.setGroupId(IntegerUtil.getInt(map.get("group_id")));
			vcb.setList(new ArrayList<ViewConfigDetailBody>());
			rst.add(vcb);
			logger.debug(vcb.toString());
		}
		logger.debug("rst size : " + rst.size());
		return rst;
	}

	public ViewConfigBody getConfigById(HashMap<String, String> params) {
		logger.debug("qeury id " + params.get("id"));
		String sql = "select * from ds_config where id=? and enable=1";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, IntegerUtil.getInt0(params.get("id")));
		if (list.size() == 0) {
			logger.debug("config not found , id is " + params.get("id"));
			return new ViewConfigBody();
		}
		ViewConfigBody vcb = new ViewConfigBody();
		vcb.setId(IntegerUtil.getInt0(list.get(0).get("id")));
		vcb.setGroupId(IntegerUtil.getInt0(list.get(0).get("group_id")));
		vcb.setDatasourceId(IntegerUtil.getInt0(list.get(0).get("datasource_id")));
		vcb.setConfigType(StringUtil.getStrEmpty(list.get(0).get("config_type")));
		vcb.setConfigName(StringUtil.getStrEmpty(list.get(0).get("config_name")));
		vcb.setIsInterface(IntegerUtil.getInt0(list.get(0).get("is_interface")));
		vcb.setIsTask(IntegerUtil.getInt0(list.get(0).get("is_task")));
		vcb.setRemark(StringUtil.getStrEmpty(list.get(0).get("remark")));
		vcb.setEnable(IntegerUtil.getInt0(list.get(0).get("enable")));
		vcb.setList(new ArrayList<ViewConfigDetailBody>());
		sql = "select * from ds_config_detail where config_id=? and enable=1";
		List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql, vcb.getId());
		logger.debug(vcb.toString());
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
			logger.debug(vcdb.toString());
		}
		return vcb;
	}

	public void saveConfigRemark(ViewConfigBody viewConfigBody) {
		String updateConfig = "update ds_config set remark=? where id=?";
		int code = jdbcTemplate.update(updateConfig, viewConfigBody.getRemark(), viewConfigBody.getId());
		logger.debug("update code : " + code);
	}

	@Transactional
	public void saveConfig(SessionModel sm, ViewConfigBody config) throws SystemRetentionParameters {
		// 先检查合法性

		// 新增config
		try {
			// 允许参数中有带EXCEL:开头的
			String dbName = dataSourceCache.getDataSourceBean(config.getDatasourceId()).getDatabase_name();
			// 是否是一个允许上传Excel的数据源
			boolean allowUploadExcelDatasource = dbName.equalsIgnoreCase("hive") || dbName.equalsIgnoreCase("mysql");
			if (config.getId() == null) {
				// 强制设置configType和enable和orderAsc
				config.setConfigType("1");
				config.setEnable(1);
				String insertConfig = "insert into ds_config(group_id,datasource_id,user_id,config_name,config_type,is_interface,is_task,remark,order_asc,enable) values(?,?,?,?,?,?,?,?,99999,?)";
				int code = jdbcTemplate.update(insertConfig, config.getGroupId(), config.getDatasourceId(), sm.getUserId(), config.getConfigName(),
						config.getConfigType(), config.getIsInterface(), config.getIsTask(), config.getRemark(), config.getEnable());
				logger.debug("inset code : " + code);
				String selectMaxId = "select max(id) as max_id from ds_config";
				Integer maxId = IntegerUtil.getInt0(jdbcTemplate.queryForList(selectMaxId).get(0).get("max_id"));
				if (maxId == null) {
					logger.error("save config [" + config.getId() + "] fail. maxId is null.");
					return;
				}
				config.setId(maxId);
			}
			// 修改config
			else {
				String updateConfig = "update ds_config set group_id=?,datasource_id=?,user_id=?,config_name=?,config_type=?,is_interface=?,is_task=?,remark=? where id=?";
				int code = jdbcTemplate.update(updateConfig, config.getGroupId(), config.getDatasourceId(), sm.getUserId(), config.getConfigName(), "1",
						config.getIsInterface(), config.getIsTask(), config.getRemark(), config.getId());
				logger.debug("update code : " + code);
			}

			// 软删除该configId的details
			String deleteDetail = "update ds_config_detail set enable=0 where config_id=? and enable=1";
			int code = jdbcTemplate.update(deleteDetail, config.getId());
			logger.debug("delete code : " + code);

			// 验证参数是否有系统保留参数
			for (ViewConfigDetailBody detail : config.getList()) {
				for (String param : SqlUtil.ExtractParams(detail.getValues())) {
					if (ParamsUtil.Set.contains(param)) {
						throw new RuntimeException("有系统保留参数[" + param + "],请更换参数名称!");
					}
					// 是一个Excel参数
					if (ParamsUtil.isExcel(param)) {
						// 参数不合法
						if (!ParamsUtil.isExcelValid(param)) {
							throw new RuntimeException("Excel类型参数[" + param + "]不合法，请检查是否没有写Excel名称!");
						}
						// 参数合法，数据源不允许Excel
						if (!allowUploadExcelDatasource) {
							throw new RuntimeException("数据源[" + dbName + "]不允许上传Excel，请更换数据源，或者更换参数名!");
						}
					}
				}
			}

			// 增加detials
			for (int i = 0; i < config.getList().size(); i++) {
				ViewConfigDetailBody detail = config.getList().get(i);
				String insertDetail = "insert into ds_config_detail(datasource_id,config_id,`key`,`values`,order_asc,enable) values(?,?,?,?,?,?)";
				code = jdbcTemplate.update(insertDetail, config.getDatasourceId(), config.getId(), detail.getKey(), detail.getValues(), (i + 1), 1);
				logger.debug("update detail code : " + code);
			}

			logger.debug("save config [" + config.getId() + "] success.");
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void moveConfig(HashMap<String, String> params) {
		String sql = "update ds_config set group_id=?, order_asc=100000 where id=?";
		jdbcTemplate.update(sql, IntegerUtil.getInt(params.get("group_id")), IntegerUtil.getInt(params.get("id")));
	}

	public void deleteConfig(HashMap<String, String> params) {
		int configId = IntegerUtil.getInt0(params.get("id"));
		logger.debug("delete config id : " + configId);
		String sql = "update ds_config set enable=0 where id=?";
		int code = jdbcTemplate.update(sql, configId);
		logger.debug("sql [" + sql + "] result : " + code);
		sql = "update mapping_config_job set enable=0 where config_id=?";
		code = jdbcTemplate.update(sql, configId);
		logger.debug("sql [" + sql + "] result : " + code);
		sql = "UPDATE scheduler_job SET `enable`=0 WHERE id IN (SELECT job_id FROM mapping_config_job WHERE config_id=?)";
		code = jdbcTemplate.update(sql, configId);
		logger.debug("sql [" + sql + "] result : " + code);
	}

	public void saveConfigOrderAsc(List<ViewConfigBody> list) {
		String updateConfig = "update ds_config set order_asc=? where id=?";
		for (int i = 0; i < list.size(); i++) {
			int code = jdbcTemplate.update(updateConfig, (i + 1), list.get(i).getId());
			logger.debug("update code : " + code);
		}
	}

}
