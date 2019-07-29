package org.mittman.schedule;

import java.util.Random;

public class LongPollingScheduler {
	private static final int NUM_JOBS = 10;
	
	private Random random;
	private int jobId;
	
	
	public LongPollingScheduler() {
		random = new Random();
	}
	
	private Job createJob() {
		String name = "Job" + ++jobId;
		
		Job job;
		switch(2) {
			case 0:
				job = createSimpleJob(name);
				break;
			case 1:
				job = createFailingJob(name);
				break;
			case 2:
				job = createLongPollingJob(name);
				break;
			default:
				System.err.println("oops");
				job = null;
				break;
		};
		
		return job;
	}
	
	private Job createSimpleJob(String name) {	
		Job job = new Job(name);
		long waitTime = random.nextInt(5) * 1000;
		job.setParameter(Job.RUNNING_TIME, Long.toString(waitTime));
		
		return job;
	}
	
	private Job createFailingJob(String name) {
		double failureRate = random.nextDouble() * 100.;
		
		Job job = new FailingJob(name, failureRate);
		long waitTime = random.nextInt(5) * 1000;
		job.setParameter(Job.RUNNING_TIME, Long.toString(waitTime));
		
		return job;
	}
	
	private Job createLongPollingJob(String name) {
		Job job = new LongPollingJob(name);
		long waitTime = random.nextInt(21);
		job.setParameter(Job.RUNNING_TIME, Long.toString(waitTime));
		
		return job;
	}
	
	private void scheduleJob(Job job) throws Exception {
		new Thread(job).run();
	}

	public static void main(String[] args) {
		System.out.println("*** Starting Simulation ***");
		
		LongPollingScheduler scheduler = new LongPollingScheduler();
		
		for(int jobId=0; jobId<NUM_JOBS; ++jobId) {
			try {
				scheduler.scheduleJob( scheduler.createJob() );
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("*** Ended Simulation ***");
	}

}
