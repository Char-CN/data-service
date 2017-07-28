package org.blazer.dataservice.model;

public class CacheModel {

	private String cacheName;

	private Long inMemorySize; // 堆内存

	private Long offHeapSize; // 非堆内存

	private Long onDiskSize; // 磁盘

	private String unit = "KB";

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Long getInMemorySize() {
		return inMemorySize;
	}

	public void setInMemorySize(Long inMemorySize) {
		this.inMemorySize = inMemorySize;
	}

	public Long getOffHeapSize() {
		return offHeapSize;
	}

	public void setOffHeapSize(Long offHeapSize) {
		this.offHeapSize = offHeapSize;
	}

	public Long getOnDiskSize() {
		return onDiskSize;
	}

	public void setOnDiskSize(Long onDiskSize) {
		this.onDiskSize = onDiskSize;
	}

	public String getUnit() {
		return unit;
	}

}
