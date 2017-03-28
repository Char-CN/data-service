package org.blazer.dataservice.action;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.service.FileService;
import org.blazer.userservice.core.filter.PermissionsFilter;
import org.blazer.userservice.core.model.SessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller(value = "downloadAction")
@RequestMapping("/file")
public class FileAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(FileAction.class);

	@Autowired
	FileService fileService;

	@ResponseBody
	@RequestMapping("download")
	public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HashMap<String, String> params = getParamMap(request);
		SessionModel sm = PermissionsFilter.getSessionModel(request);
		try {
			return fileService.download(response, params, sm);
		} catch (Exception e) {
			logger.error("文件不存在。" + e.getMessage());
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("文件不存在。");
			return null;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public Body upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		HashMap<String, String> params = getParamMap(request);
		SessionModel sm = PermissionsFilter.getSessionModel(request);
		try {
			// 保存到服务器
			String message = fileService.upload(params, sm, file);
			return new Body().success().setMessage(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new Body().error().setMessage(e.getMessage());
		}
	}

}
