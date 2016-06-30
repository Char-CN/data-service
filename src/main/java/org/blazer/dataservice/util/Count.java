package org.blazer.dataservice.util;

public class Count {

	int count;

	int interval;

	public Count(int count, int interval) {
		this.count = count;
		this.interval = interval;
	}

	public Count(int count) {
		this.count = count;
		this.interval = 5000;
	}

	public int getCount() {
		return count;
	}

	public void add(int number) {
		this.count += number;
	}

	public int getInterval() {
		return interval;
	}
	
	public boolean modZero() {
		return this.count % this.interval == 0;
	}

}
