package org.mittman.schedule;

import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Job implements Runnable {
	public static final String RUNNING_TIME = "runningTime";
	
	@Getter
	private String name;
	private volatile Map<String,String> parameters;
	@Getter
	private volatile int status;
	@Getter
	private volatile Exception exception;
	
	
	public Job(String name) {
		this.name = name;
		parameters = new HashMap<String,String>();
	}
	
	public String setParameter(String paramName, String value) {
		return parameters.put(paramName, value);
	}
	
	public String getParameter(String paramName) {
		return parameters.get(paramName);
	}
	
	protected void execute() throws Exception {
		long runningTime = 1000l;
		if (parameters.containsKey(RUNNING_TIME)) {
			runningTime = Long.valueOf( parameters.get(RUNNING_TIME) );
		}
		Thread.sleep(runningTime);
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Job " + name + " is starting...");
			execute();
			System.out.println("Job " + name + " succeeded");
		}
		catch(Exception e) {
			exception = e;
			status = 1;
			System.err.println("Job " + name + " failed: " + e.getMessage());
		}
	}

}
