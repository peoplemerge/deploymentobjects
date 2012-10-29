package org.deploymentobjects.core.application;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Hypervisor;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.model.execution.ConcurrentSteps;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.model.execution.PersistStep;
import org.deploymentobjects.core.domain.model.execution.SequentialSteps;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.execution.Ssh;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEnvironmentRepository;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;

public class NewCreateEnvironmentCommand implements CreatesJob {

	private EventStore eventStore = new InMemoryEventStore();
	private EventPublisher publisher ;
	private EnvironmentRepository repo = new InMemoryEnvironmentRepository();

	private NewCreateEnvironmentCommand() {
	}

	public static class Builder {
		private NewCreateEnvironmentCommand command = new NewCreateEnvironmentCommand();

		public Builder withEventStore(EventStore eventStore) {
			command.eventStore = eventStore;
			return this;
		}
		
		public Builder withEnvironmentRepository(EnvironmentRepository repo){
			command.repo  = repo;
			return this;
		}

		public NewCreateEnvironmentCommand build() {
			if(command.publisher == null){
				command.publisher = new EventPublisher(command.eventStore);
			}
			return command;
		}
	}

	public Job create() {
		
		Environment environment = new Environment();
		environment.add(new Host("first.example.com"));
		environment.add(new Host("second.example.com"));

		Hypervisor hypervisor = Hypervisor.factory(publisher);
		Ssh ssh = Ssh.factory(publisher);

		
		SequentialSteps sequence = new SequentialSteps(publisher);
		BlockingEventStep createVms = hypervisor.buildStepFor(environment);
		sequence.add(createVms);

		ConcurrentSteps concurrent = new ConcurrentSteps(publisher);
		for(Host host : environment.hosts){
			BlockingEventStep sshToVms = ssh.buildStepFor(environment, host, "echo hello world");
			concurrent.add(sshToVms);
		}
		sequence.add(concurrent);
		
		PersistStep persistStep = new PersistStep(repo, publisher,environment);
		sequence.add(persistStep);
		
		Job saga = new Job(publisher, sequence);

		return saga;
	}

}
