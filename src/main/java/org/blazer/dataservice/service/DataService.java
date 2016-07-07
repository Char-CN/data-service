package org.blazer.dataservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.body.ConfigBody;
import org.blazer.dataservice.body.ConfigDetailBody;
import org.blazer.dataservice.dao.DSConfigDao;
import org.blazer.dataservice.dao.Dao;
import org.blazer.dataservice.model.DSConfig;
import org.blazer.dataservice.model.DSConfigDetail;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dataService")
public class DataService {

	@Autowired
	DSConfigDao dsConfigDao;

	public ConfigBody getConfigById(HashMap<String, String> paramMap) {
		ConfigBody cb = new ConfigBody();
		Integer id = IntegerUtil.getInt0(paramMap.get("id"));
		DSConfig config = dsConfigDao.getConfig(id);

		cb.setId(config.getId());
		cb.setConfigName(config.getConfigName());
		cb.setConfigType(config.getConfigType());
		cb.setDetails(new HashMap<String, ConfigDetailBody>());

		List<DSConfigDetail> detailList = config.getDetailList();
		for (DSConfigDetail detail : detailList) {
			ConfigDetailBody cdb = new ConfigDetailBody();
			String sql = detail.getValues();

			// 替换参数
			for (String key : paramMap.keySet()) {
				sql = sql.replace("${" + key + "}", SqlUtil.TransactSQLInjection(paramMap.get(key)));
			}

			Dao dao = detail.getDataSource();
			List<Map<String, Object>> values = dao.find(sql);

			cdb.setId(detail.getId());
			cdb.setValues(values);
			cb.getDetails().put(detail.getKey(), cdb);
		}

		return cb;
	}

}
