package org.blazer.scheduler.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.expression.CronException;
import org.blazer.scheduler.expression.DuplicateJobException;
import org.blazer.scheduler.model.JobModel;
import org.blazer.scheduler.util.DateUtil;

public class Scheduler extends Thread {

	private static ConcurrentHashMap<String, List<Integer>> timeMap = new ConcurrentHashMap<String, List<Integer>>();

	private static HashMap<Integer, Job> map = new HashMap<Integer, Job>();

	public static void main(String[] args) throws DuplicateJobException, CronException {
		JobModel jm = new JobModel();
		Job job = new Job();
		job.setId(1);
		job.setJobName("JobName111");
		job.setCron("10,20,30,40,50 * * * *");
		jm.setNextDate(CronParser.getNextDate(job.getCron()));
		jm.setJob(job);
		map.put(1, job);

		add(jm);

		System.out.println(timeMap);
		System.out.println(map);
	}

	public static void add(JobModel jm) throws DuplicateJobException {
		String time = jm.getNextDateStr();
		Integer jobId = jm.getJob().getId();
		if (!timeMap.containsKey(time)) {
			timeMap.put(time, new ArrayList<Integer>());
		}
		List<Integer> list = timeMap.get(jm.getNextDateStr());
		if (list.contains(jobId)) {
			throw new DuplicateJobException("已经存在重复Job Id，" + jm.getJob());
		}
		list.add(jobId);
	}

	public static void remove(Job job, String time) {

	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public static void main(String[] args) throws Exception {
	// Thread t1 = new Thread(new Runnable() {
	// public void run() {
	// System.out.println("aaaaaaaaaaaa");
	// try {
	// sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// t1.start();
	//
	// ExecutorService executor = Executors.newFixedThreadPool(3);
	// executor.submit(new Runnable() {
	// public void run() {
	// System.out.println("aaaaaaaaaaaa");
	// try {
	// sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// System.out.println("bbbbbbbbbbbbbbbbb");
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// Thread.sleep(100);
	// System.out.println(t1.getState());
	// }

}
