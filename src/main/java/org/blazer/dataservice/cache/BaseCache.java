package org.blazer.dataservice.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

@Component(value = "baseCache")
public abstract class BaseCache {

	@Autowired
	EhCacheCacheManager ehCacheManager;

	public abstract String getCacheName();

	protected Cache getCache() {
		return ehCacheManager.getCache(getCacheName());
	}

}
