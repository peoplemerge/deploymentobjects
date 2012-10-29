package org.deploymentobjects.core.domain.model.execution;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;

public class PersistStep extends Executable {

	private EnvironmentRepository repository;
	private EventPublisher publisher;
	private Environment environment;

	public PersistStep(EnvironmentRepository repository,
			EventPublisher publisher, Environment environment) {
		this.repository = repository;
		this.publisher = publisher;
		this.environment = environment;
	}

	public enum PersistEvent implements EventType {
		PERSIST_COMPLETED
	}

	@Override
	public ExitCode execute() {
		repository.save(environment);
		StepExecutionEvent event = new StepExecutionEvent(
				PersistEvent.PERSIST_COMPLETED, this);
		publisher.publish(event);
		return ExitCode.SUCCESS;

	}

}
