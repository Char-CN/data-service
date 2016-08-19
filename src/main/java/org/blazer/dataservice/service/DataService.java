package org.blazer.dataservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.body.ConfigBody;
import org.blazer.dataservice.body.ConfigDetailBody;
import org.blazer.dataservice.body.ParamsBody;
import org.blazer.dataservice.dao.DSConfigDao;
import org.blazer.dataservice.dao.Dao;
import org.blazer.dataservice.model.DSConfig;
import org.blazer.dataservice.model.DSConfigDetail;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.SqlUtil;
import org.blazer.dataservice.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dataService")
public class DataService {

	private static Logger logger = LoggerFactory.getLogger(DataService.class);

	@Autowired
	DSConfigDao dsConfigDao;

	public ConfigBody getConfigById(HashMap<String, String> paramMap) {
		ConfigBody cb = new ConfigBody();
		Integer id = IntegerUtil.getInt0(paramMap.get("id"));
		String detailsId = StringUtil.getStr(paramMap.get("detailsid"));
		if (detailsId != null) {
			detailsId = "," + detailsId + ",";
		}
		String detailsKey = StringUtil.getStr(paramMap.get("detailskey"));
		if (detailsKey != null) {
			detailsKey = "," + detailsKey + ",";
		}
		DSConfig config = dsConfigDao.getConfig(id);

		cb.setId(config.getId());
		cb.setConfigName(config.getConfigName());
		cb.setConfigType(config.getConfigType());
		cb.setDetails(new HashMap<String, ConfigDetailBody>());

		List<DSConfigDetail> detailList = config.getDetailList();
		for (DSConfigDetail detail : detailList) {
			// 匹配details id
			if (detailsId != null && !detailsId.contains("," + detail.getId() + ",")) {
				continue;
			}
			// 匹配details key
			if (detailsKey != null && !detailsKey.contains("," + detail.getKey() + ",")) {
				continue;
			}
			ConfigDetailBody cdb = new ConfigDetailBody();
			String sql = detail.getValues();

			// 替换参数
			for (String key : paramMap.keySet()) {
				// 防止SQL注入
				sql = sql.replace("${" + key + "}", SqlUtil.TransactSQLInjection(paramMap.get(key)));
			}

			Dao dao = detail.getDataSource();
			List<Map<String, Object>> values = null;

			String errorMessage = StringUtils.EMPTY;
			try {
				values = dao.find(sql);
			} catch (Exception e) {
				values = new ArrayList<Map<String, Object>>();
				logger.error(e.getMessage(), e);
				errorMessage = e.getMessage();
			}

			cdb.setErrorMessage(errorMessage);
			cdb.setId(detail.getId());
			cdb.setValues(values);
			cb.getDetails().put(detail.getKey(), cdb);
		}

		return cb;
	}

	public ParamsBody getParamsById(HashMap<String, String> paramMap) {
		ParamsBody body = new ParamsBody();
		Integer id = IntegerUtil.getInt0(paramMap.get("id"));
		DSConfig config = dsConfigDao.getConfig(id);

		List<DSConfigDetail> detailList = config.getDetailList();
		body.setParams(new ArrayList<String>());
		body.setDetails(new HashMap<String, List<String>>());
		for (DSConfigDetail detail : detailList) {
			String key = detail.getKey();
			String sql = detail.getValues();
			List<String> tmpList = new ArrayList<String>();
			for (String param : SqlUtil.ExtractParams(sql)) {
				if (!body.getParams().contains(param)) {
					body.getParams().add(param);
				}
				if (!tmpList.contains(param)) {
					tmpList.add(param);
				}
			}
			body.getDetails().put(key, tmpList);
		}
		return body;
	}

}
