package org.blazer.dataservice.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.entity.DSUpload;
import org.blazer.dataservice.exception.FileHandleException;
import org.blazer.dataservice.util.FileUtil;
import org.blazer.scheduler.core.TaskServer;
import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.TaskType;
import org.blazer.scheduler.model.ProcessModel;
import org.blazer.scheduler.service.TaskService;
import org.blazer.scheduler.util.DateUtil;
import org.blazer.userservice.core.model.SessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Scope("prototype")
@Service(value = "fileService")
public class FileService {

	private static Logger logger = LoggerFactory.getLogger(FileService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	TaskService taskService;

	@Value("#{scriptProperties.script_path}")
	private String scriptPath;

	@Value("#{scriptProperties.upload_path}")
	private String uploadPath;

	@Value("#{scriptProperties.upload_shell}")
	private String uploadShell;

	@Value("#{scriptProperties.upload_file_import_database}")
	private String uploadFileImportDatabase;

	@Value("#{scriptProperties.result_path}")
	private String resultPath;

	public ResponseEntity<byte[]> download(HttpServletResponse response, HashMap<String, String> params, SessionModel sm) throws IOException {
		String taskName = params.get("taskName");
		if (StringUtils.isBlank(taskName) || taskName.indexOf(File.separator) != -1) {
			logger.error("文件名称有误。");
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("文件名称有误。");
			return null;
		}
		if (!sm.isValid()) {
			logger.error("用户验证失败。");
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("用户验证失败。");
			return null;
		}
		logger.info(sm.getUserName() + "准备下载" + taskName);
		String filePath1 = resultPath + File.separator + taskName + ".zip";
		String filePath2 = resultPath + File.separator + taskName + ".xlsx";
		String filePath3 = resultPath + File.separator + taskName + ".csv";
		String existsFilePath = FileUtil.exists(filePath1, filePath2, filePath3);
		String existsFileName = new String(FileUtil.getFileName(existsFilePath).getBytes("UTF-8"), "iso-8859-1");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", existsFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		Path path = Paths.get(existsFilePath);
		byte[] data = Files.readAllBytes(path);
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.CREATED);
	}

	public String upload(HashMap<String, String> params, SessionModel sm, MultipartFile file) throws Exception {
		String config_id = params.get("config_id");
		String uuid = null;
		if (TaskType.right_now.toString().equals(params.get("task_type"))) {
			uuid = TaskType.right_now + "_" + DateUtil.newDateStr_yyyy_MM_dd_HH_mm_ss_SSS() + "_" + sm.getUserId() + "_" + config_id;
		} else {
			uuid = TaskType.cron_auto + "_" + DateUtil.newDateStr_yyyy_MM_dd_HH_mm_ss_SSS() + "_" + sm.getUserId() + "_" + config_id;
		}
		String originalFileName = file.getOriginalFilename();
		String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
		// 保存文件
		File targetFile = new File(uploadPath, uuid + suffix);
		file.transferTo(targetFile);
		// 保存到数据库
		DSUpload upload = new DSUpload();
		upload.setFileOldName(originalFileName);
		upload.setFileSuffix(suffix);
		upload.setFilePath(uploadPath);
		upload.setUserId(sm.getUserId());
		upload.setFileName(uuid);
		upload.setFileFullName(uuid + suffix);
		// 处理文件
		String cmdParams = " config_id=" + params.get("config_id");
		cmdParams += " upload_path=" + uploadPath;
		cmdParams += " upload_name=" + uuid + suffix;
		cmdParams += " uuid=" + uuid;
		String cmd = "sh " + scriptPath + File.separator + uploadShell + cmdParams;
		logger.info("上传文件执行的UploadHandle.sh：" + cmd);
		Job job = new Job();
		job.setId(-10);
		job.setJobName("上传文件处理");
		job.setCommand(cmd);
		job.setParams(null);
		// paramList 是需要记录的参数信息
		ProcessModel pm = TaskServer.spawnRightNowTaskProcess(job);
		String sql = "insert into ds_upload(user_id, task_name, file_name, file_full_name, file_suffix, file_path, file_old_name) values(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, sm.getUserId(), pm.getTask().getTaskName(), upload.getFileName(), upload.getFileFullName(), upload.getFileSuffix(), upload.getFilePath(),
				upload.getFileOldName());
		pm.getTask().setRemark("上传文件处理 的即时任务。");
		taskService.updateTaskRemark(pm.getTask());
		int statusId = pm.getTask().getStatusId();
		// 等待任务执行结束
		for (; (statusId == 10 || statusId == 20); statusId = pm.getTask().getStatusId()) {
			Thread.sleep(1000);
		}
		if (statusId >= 40) {
			logger.info("上传文件成功，文件处理失败。" + upload.getFilePath() + File.separator + upload.getFileFullName());
			throw new FileHandleException("上传文件成功,文件处理失败。");
		} else {
			logger.info("上传文件成功，文件处理成功。" + upload.getFilePath() + File.separator + upload.getFileFullName());
		}
		return uploadFileImportDatabase + "." + uuid;
	}

}
