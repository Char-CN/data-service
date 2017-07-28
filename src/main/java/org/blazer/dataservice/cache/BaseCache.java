package org.blazer.dataservice.cache;

import org.blazer.dataservice.model.CacheModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;

@Component(value = "baseCache")
public abstract class BaseCache {

	@Autowired
	EhCacheCacheManager ehCacheManager;

	public abstract String getCacheName();

	public CacheModel getCalcCacheModel() {
		CacheModel cm = new CacheModel();
		cm.setCacheName(getCacheName());
		cm.setInMemorySize(getCache().getStatistics().getLocalHeapSizeInBytes() / 1024);
		cm.setOffHeapSize(getCache().getStatistics().getLocalOffHeapSizeInBytes() / 1024);
		cm.setOnDiskSize(getCache().getStatistics().getLocalDiskSizeInBytes() / 1024);
		return cm;
	}

	protected Cache getCache() {
		// return ehCacheManager.getCache(getCacheName());
		return ehCacheManager.getCacheManager().getCache(getCacheName());
	}

}
