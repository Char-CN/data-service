package org.blazer.dataservice.service;

import org.blazer.dataservice.model.RoleModel;
import org.blazer.dataservice.util.ApplicationUtil;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheTest {

	public static void main(String[] args) throws InterruptedException {
		ApplicationUtil.init();

		EhCacheCacheManager manager = (EhCacheCacheManager) ApplicationUtil.getBean("cacheManager");
		RoleModel roleModel = new RoleModel();
		roleModel.setId(1);
		roleModel.setRoleName("角色啊啊");
		org.springframework.cache.Cache cache = manager.getCache("user_cache");
		cache.put("key1", "value");
		cache.put("key2", roleModel);
		System.out.println(cache.get("key1").get());
		System.out.println(cache.get("key2").get());
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < 100000; i++) {
			sb.append(i);
		}

		for (int i = 0; i < 10001; i++) {
			RoleModel r = new RoleModel();
			r.setId(i);
			r.setRoleName("角色啊啊" + i + "|" + sb.toString());
			cache.put("new" + i, r);
			if (i % 5000 == 0) {
				System.out.println(i);
			}
		}

		for (int i = 0; i < 1000; i++) {
			System.out.println(cache.get("key1").get());
			System.out.println(cache.get("key2").get());
			Thread.sleep(1000);
		}

		CacheManager manager2 = CacheManager.newInstance("src/main/resources/ehcache.xml");
		Cache cache2 = manager2.getCache("aaa");
//
//		RoleModel roleModel = new RoleModel();
//		roleModel.setId(1);
//		roleModel.setRoleName("角色啊啊");
//		
//		Element e = new Element("key1", "value1");
//		
//		cache.put(e);
//		cache.put(new Element("key2", roleModel));

//		for (int i = 0;i < 1000;i++) {
//			Thread.sleep(1000);
//			if (i == 5 || i == 11 || i == 12) {
//				System.out.println(i + ":" + System.currentTimeMillis());
//				continue;
//			}
//			Element e2 = cache.get("key2");
//			if (cache.get("key2") == null) {
//				System.out.println(i + ":" + System.currentTimeMillis());
//				System.exit(0);
//			}
//			RoleModel r = new RoleModel();
//			roleModel.setId(i);
//			roleModel.setRoleName("角色啊啊"+i);
//			cache.put(new Element("new" + i, r));
//			System.out.println(i + ":" + System.currentTimeMillis() + ":" + e2.getLastUpdateTime() + ":" + e2.getExpirationTime());
//		}
//		StringBuffer sb = new StringBuffer("");
//		for (int i = 0;i < 100000;i++) {
//			sb.append(i);
//		}
//		for (int i = 0;i < 10000;i++) {
//			RoleModel r = new RoleModel();
//			r.setId(i);
//			r.setRoleName("角色啊啊"+i+"|"+sb.toString());
//			cache.put(new Element("new" + i, r));
//			if (i % 5000 == 0) {
//				System.out.println(i);
//			}
//		}
//		int isnull = 0;
//		int isnotnull = 0;
//		for (int i = 0;i < 10000;i++) {
//			Element e2 = cache.get("new" + i);
//			if (e2 != null) {
//				System.out.println(i + ":" + System.currentTimeMillis() + ":" + e2+ ":" + e2.getExpirationTime());
//				isnotnull++;
//			} else {
//				isnull++;
//			}
//		}
//		System.out.println("isnull : " + isnull);
//		System.out.println("isnotnull : " + isnotnull);
	}

}
