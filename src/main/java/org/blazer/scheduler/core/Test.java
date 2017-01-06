package org.blazer.scheduler.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.blazer.scheduler.model.ProcessModel;

public class Test {


	private static final CopyOnWriteArrayList<ProcessModel> processTaskList = new CopyOnWriteArrayList<ProcessModel>();
	
	private static final ConcurrentHashMap<String, ProcessModel> map = new ConcurrentHashMap<String, ProcessModel>();
	private static final ConcurrentHashMap<String, ProcessModel> map2 = new ConcurrentHashMap<String, ProcessModel>();
	
	public static void main(String[] args) {
		ProcessModel pm = new ProcessModel();
		pm.setNextTime("asdasdasd");
		processTaskList.add(pm);
		map.put("aaa", pm);
		map2.put("bbb", pm);
		map.get("aaa").setNextTime("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		
		for (ProcessModel p : processTaskList) {
			System.out.println(p.getNextTime());
		}
		System.out.println(map2.get("bbb").getNextTime());
//		processTaskList.remove(
//		map2.get("bbb"));
		ProcessModel ppp = map2.get("bbb");
		ppp.setNextTime("ccccc");
		for (ProcessModel p : processTaskList) {
			System.out.println(p.getNextTime());
		}
	}
}
