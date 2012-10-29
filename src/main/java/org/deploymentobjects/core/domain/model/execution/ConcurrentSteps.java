package org.deploymentobjects.core.domain.model.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;

public class ConcurrentSteps extends Executable {

	private final List<Executable> steps = new ArrayList<Executable>();
	private EventPublisher publisher;

	public ConcurrentSteps(EventPublisher publisher) {
		this.publisher = publisher;
	}

	public boolean equals(Object o) {
		if (o instanceof ConcurrentSteps) {
			ConcurrentSteps other = (ConcurrentSteps) o;
			return other.steps.equals(steps);
		}
		return false;
	}

	public void add(Executable runnable) {
		steps.add(runnable);
	}

	public enum ConcurrentEvent implements EventType {
		CONCURRENT_STEPS_REQUESTED, CONCURRENT_STEPS_EXECUTED, CONCURRENT_STEPS_FAILED
	}


	public ExitCode execute() {
		ExecutorService executor = Executors.newCachedThreadPool();
		final Map<Executable, ExitCode> completionCodes = new TreeMap<Executable, ExitCode>();
		for (final Executable step : steps) {
			publisher.publish(new StepExecutionEvent(ConcurrentEvent.CONCURRENT_STEPS_REQUESTED, step));
			Runnable toRun = new Runnable() {
				public void run() {
					ExitCode code = step.execute();
					completionCodes.put(step, code);
				}
			};
			executor.execute(toRun);
		}
		try {
			executor.shutdown();
			// TODO get this from somewhere nicer
			boolean complete = executor.awaitTermination(1, TimeUnit.HOURS);
			if (complete == false) {
				return ExitCode.FAILURE;
			}
		} catch (InterruptedException e) {
			// TODO think about how to handle user termination better.
			return ExitCode.FAILURE;
		}
		ExitCode retval = ExitCode.SUCCESS;
		for (Executable step : completionCodes.keySet()) {
			ExitCode code = completionCodes.get(step);
			if (code == ExitCode.SUCCESS) {
				publisher.publish(new StepExecutionEvent(ConcurrentEvent.CONCURRENT_STEPS_EXECUTED,step));
			} else {
				publisher.publish(new StepExecutionEvent(ConcurrentEvent.CONCURRENT_STEPS_FAILED,step));
				retval = code;
			}
		}
		return retval;
	}

	public String toString() {
		String retval = super.toString() + ": \n{\n";
		for (Executable step : steps) {
			retval += "+ " + step.toString() + "\n";
		}
		retval += "}";
		return retval;
	}

}
