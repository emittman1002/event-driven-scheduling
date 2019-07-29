package org.mittman.schedule;

import java.util.Random;

import lombok.Getter;

@Getter
public class FailingJob extends Job {
	private double failureRate = .25;

	public FailingJob(String name, double failureRate) {
		super(name);
		this.failureRate = failureRate;
	}

	@Override
	protected void execute() throws Exception {
		super.execute();
		
		if (getStatus()==0) {
			double value = new Random().nextDouble() * 100.;

			if (value < failureRate) {
				throw new Exception("Job failed to meet required failure rate");
			}	
		}
	}

	
}
