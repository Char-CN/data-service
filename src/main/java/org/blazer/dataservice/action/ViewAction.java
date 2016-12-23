package org.blazer.dataservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.body.Body;
import org.blazer.dataservice.body.DataSourceBody;
import org.blazer.dataservice.body.GroupBody;
import org.blazer.dataservice.body.TreeBody;
import org.blazer.dataservice.body.view.ViewConfigBody;
import org.blazer.dataservice.body.view.ViewMappingConfigJobBody;
import org.blazer.dataservice.cache.ConfigCache;
import org.blazer.dataservice.cache.DataSourceCache;
import org.blazer.dataservice.entity.MappingConfigJob;
import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.service.ViewService;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.userservice.core.filter.PermissionsFilter;
import org.blazer.userservice.core.model.CheckUrlStatus;
import org.blazer.userservice.core.model.SessionModel;
import org.blazer.userservice.core.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "viewAction")
@RequestMapping("/view")
public class ViewAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ViewAction.class);

	@Autowired
	ViewService viewService;

	@Autowired
	DataSourceCache dataSourceCache;

	@Autowired
	ConfigCache configCache;

	@ResponseBody
	@RequestMapping("/saveScheduler")
	public Body saveScheduler(HttpServletRequest request, HttpServletResponse response, @RequestBody MappingConfigJob s) {
		logger.debug(s.toString());
		return success().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/saveSchedulers")
	public Body saveSchedulers(HttpServletRequest request, HttpServletResponse response, @RequestBody ViewMappingConfigJobBody vBody) {
		SessionModel sm = PermissionsFilter.getSessionModel(request);
		try {
			viewService.saveMappingConfigJob(vBody, sm);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return fail().setMessage(e.getMessage());
		}
		return success().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/findSchedulers")
	public List<MappingConfigJob> findSchedulers(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> params = getParamMap(request);
		List<MappingConfigJob> list = null;
		try {
			list = viewService.findSchedulersByConfigId(IntegerUtil.getInt0(params.get("config_id")));
		} catch (Exception e) {
			list = new ArrayList<MappingConfigJob>();
		}
		return list;
	}

	@ResponseBody
	@RequestMapping("/getAllUser")
	public List<UserModel> getAllUser(HttpServletRequest request, HttpServletResponse response) {
		List<UserModel> list = null;
		try {
			list = PermissionsFilter.findAllUserBySystemNameAndUrl(PermissionsFilter.getSystemName(), "isuser");
		} catch (Exception e) {
			list = new ArrayList<UserModel>();
			logger.error(e.getMessage(), e);
		}
		return list;
	}

	@ResponseBody
	@RequestMapping("/getUserGroupIds")
	public List<Integer> getUserGroupIds(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		List<Integer> list = viewService.findUserGroupIds(getParamMap(request));
		return list;
	}

	@ResponseBody
	@RequestMapping("/saveUserGroup")
	public Body saveUserGroup(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> params = getParamMap(request);
		try {
			viewService.saveUserGroup(params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return fail().setMessage(e.getMessage());
		}
		return success().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/getTreeAll")
	public List<TreeBody> getTreeAll(HttpServletRequest request, HttpServletResponse response) {
		List<TreeBody> list = null;
		try {
			list = viewService.findTreeAllByParentId(-1);
		} catch (Exception e) {
			list = new ArrayList<TreeBody>();
			logger.error(e.getMessage(), e);
		}
		return list;
	}

	@ResponseBody
	@RequestMapping("/getTree")
	public List<GroupBody> getGroup(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		SessionModel sm = PermissionsFilter.getSessionModel(request);
		logger.debug("session model : " + sm);
		try {
			CheckUrlStatus cus = PermissionsFilter.checkUrl(request, response, "isadmin");
			if (cus == CheckUrlStatus.Success) {
				return viewService.findTreeById(getParamMap(request), sm);
			}
			cus = PermissionsFilter.checkUrl(request, response, "isuser");
			if (cus == CheckUrlStatus.Success) {
				return viewService.findTreeByIdAndUserId(getParamMap(request), sm);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("返回了空目录。");
		return new ArrayList<GroupBody>();
	}

	@ResponseBody
	@RequestMapping("/getConfigsByGroupId")
	public List<ViewConfigBody> getConfigsByGroupId(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return viewService.getConfigsByGroupId(getParamMap(request));
	}

	@ResponseBody
	@RequestMapping("/getConfigById")
	public ViewConfigBody getConfigById(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("map : " + getParamMap(request));
		return viewService.getConfigById(getParamMap(request));
	}

	@ResponseBody
	@RequestMapping("/saveConfig")
	public Body saveConfig(HttpServletRequest request, HttpServletResponse response, @RequestBody ViewConfigBody viewConfigBody) {
		try {
			viewService.saveConfig(viewConfigBody);
			configCache.initConfigEntity(viewConfigBody.getId());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return fail().setMessage(e.getMessage());
		}
		return success().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/saveConfigOrderAsc")
	public Body saveConfigOrderAsc(HttpServletRequest request, HttpServletResponse response, @RequestBody List<ViewConfigBody> list) {
		try {
			viewService.saveConfigOrderAsc(list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return fail().setMessage(e.getMessage());
		}
		return success().setMessage("保存成功！");
	}

	@ResponseBody
	@RequestMapping("/deleteConfig")
	public Body deleteConfig(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> params = getParamMap(request);
		try {
			viewService.deleteConfig(params);
			configCache.initConfigEntity(IntegerUtil.getInt0(params.get("id")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return fail().setMessage(e.getMessage());
		}
		return success().setMessage("删除成功！");
	}

	@ResponseBody
	@RequestMapping("/getDataSourceAll")
	public List<DataSourceBody> getDataSourceAll(HttpServletRequest request, HttpServletResponse response) throws UnknowDataSourceException {
		logger.debug("map : " + getParamMap(request));
		List<DataSourceBody> list = new ArrayList<DataSourceBody>();
		for (Integer i : dataSourceCache.getKeySet()) {
			DataSourceBody dsb = new DataSourceBody();
			dsb.setId(i);
			dsb.setDatabaseName(dataSourceCache.getDataSourceBean(i).getDatabase_name());
			dsb.setTitle(dataSourceCache.getDataSourceBean(i).getTitle());
			dsb.setRemark(dataSourceCache.getDataSourceBean(i).getRemark());
			list.add(dsb);
		}
		return list;
	}

}
