package org.deploymentobjects.core.application;

import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEnvironmentRepository;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.junit.Assert;
import org.junit.Test;

public class NewCreateEnvironmentScenarioTest {

	@Test
	public void testScenario() {

		EventStore eventStore = new InMemoryEventStore();
		EventPublisher publisher = new EventPublisher(eventStore);
		EnvironmentRepository repo = new InMemoryEnvironmentRepository();

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder(
				"test", repo, publisher).withEventStore(eventStore).build();
		Job saga = command.create();
		System.out.println("saga: " + saga);
		ExitCode exit = saga.execute();
		Assert.assertEquals(ExitCode.SUCCESS, exit);
		System.out.println("event store: " + eventStore);
		System.out.println("repo: " + repo);
	}
}
