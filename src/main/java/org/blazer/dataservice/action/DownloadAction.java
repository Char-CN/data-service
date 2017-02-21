package org.blazer.dataservice.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.blazer.userservice.core.filter.PermissionsFilter;
import org.blazer.userservice.core.model.SessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Scope("prototype")
@Controller(value = "downloadAction")
@RequestMapping("/download")
public class DownloadAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(DownloadAction.class);

	@Value("#{scriptProperties.result_path}")
	private String resultPath;

	@ResponseBody
	@RequestMapping("get")
	public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String taskName = getParamMap(request).get("taskName");
		if (StringUtils.isBlank(taskName) || taskName.indexOf(File.separator) != -1) {
			logger.error("文件名称有误。");
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("文件名称有误。");
			return null;
		}
		SessionModel sm = PermissionsFilter.getSessionModel(request);
		if (!sm.isValid()) {
			logger.error("用户验证失败。");
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("用户验证失败。");
			return null;
		}
		logger.info(sm.getUserName() + "下载了" + taskName);
		HttpHeaders headers = new HttpHeaders();
		String fileName = new String((taskName + ".xlsx").getBytes("UTF-8"), "iso-8859-1");
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		Path path = Paths.get(resultPath + File.separator + taskName + ".xlsx");
		try {
			byte[] data = Files.readAllBytes(path);
			return new ResponseEntity<byte[]>(data, headers, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("文件不存在。" + e.getMessage());
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("文件不存在。");
			return null;
		}
	}

}
