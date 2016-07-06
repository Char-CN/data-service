package org.blazer.dataservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.blazer.dataservice.body.ConfigBody;
import org.blazer.dataservice.body.ConfigDetailBody;
import org.blazer.dataservice.dao.DSConfigDao;
import org.blazer.dataservice.dao.Dao;
import org.blazer.dataservice.model.DSConfig;
import org.blazer.dataservice.model.DSConfigDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dataService")
public class DataService {

	@Autowired
	DSConfigDao dsConfigDao;
	
	public ConfigBody getConfigById(Integer id) {
		ConfigBody cb = new ConfigBody();
		DSConfig config = dsConfigDao.getConfig(1);
		
		cb.setId(config.getId());
		cb.setConfigName(config.getConfigName());
		cb.setConfigType(config.getConfigType());
		cb.setList(new ArrayList<ConfigDetailBody>());
		
		List<DSConfigDetail> detailList = config.getDetailList();
		for (DSConfigDetail detail : detailList) {
			ConfigDetailBody cdb = new ConfigDetailBody();
			String sql = detail.getValues();
			Dao dao = detail.getDataSource();
			List<Map<String, Object>> values = dao.find(sql);
			
//			for (Map<String, Object> out : values) {
//				for (String outs : out.keySet()) {
//					System.out.println("k:" + outs + "v:" + out.get(outs));
//				}
//			}
			
			cdb.setId(detail.getId());
			cdb.setKey(detail.getKey());
			cdb.setValues(values);
			cb.getList().add(cdb);
		}
		
		return cb;
	}

}
