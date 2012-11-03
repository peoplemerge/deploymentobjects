package org.deploymentobjects.core.domain.model.execution;

import org.deploymentobjects.core.domain.shared.EventHistory;
import org.deploymentobjects.core.domain.shared.EventPublisher;

public class Job extends AdvancedExecutable {

	private Executable step;
	private EventPublisher publisher;
	
	public Executable getStep(){
		return step;
	}

	public Job(EventPublisher publisher, Executable step) {
		this.step = step;
		this.publisher = publisher;
	}	
	
	@Override
	public ExitCode execute() {
		return step.execute();
	}

	@Override
	public void resume(EventHistory history) {
		if (step instanceof AdvancedExecutable) {
			((AdvancedExecutable) step).resume(history);
		}

	}

	@Override
	public void rollback() {
		if (step instanceof AdvancedExecutable) {
			((AdvancedExecutable) step).rollback();
		}
	}

	public String toString() {
		return step.toString();
	}
	

}
