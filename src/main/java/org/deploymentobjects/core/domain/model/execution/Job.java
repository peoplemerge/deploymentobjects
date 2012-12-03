package org.deploymentobjects.core.domain.model.execution;

import org.deploymentobjects.core.domain.shared.EventHistory;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperEventStore;

public class Job extends AdvancedExecutable {

	private Executable step;
	private EventPublisher publisher;
	private String id;

	public Executable getStep() {
		return step;
	}

	public Job(EventPublisher publisher, Executable step,
			String id) {
		this.step = step;
		this.publisher = publisher;
		this.id = id;
	}

	@Override
	public ExitCode execute() {
		//TODO this big hack is here until we can have the events encapsulate more of what they model.
		if (publisher.getEventStore() instanceof ZookeeperEventStore) {
			((ZookeeperEventStore) publisher.getEventStore()).setJob(this);
		}
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
	
	public String getId(){
		return id;
	}

}
