package org.deploymentobjects.core.infrastructure.execution;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentEvent;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.shared.DomainSubscriber;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;

public class Ssh implements DomainSubscriber<EnvironmentEvent> {

	public static Ssh factory(EventPublisher publisher) {
		Ssh hypervisor = new Ssh(publisher);
		publisher.addSubscriber(hypervisor, new EnvironmentEvent.Builder(
				SshDispatchType.DISPATCH_REQUESTED, null).build());
		return hypervisor;
	}

	private EventPublisher publisher;

	private Ssh(EventPublisher publisher) {
		this.publisher = publisher;
	}


	public void sshToHost(Host host, String command) {
		System.out.println("actually ssh to " + host + " to run " + command);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public enum SshDispatchType implements EventType {
		DISPATCH_REQUESTED, TASK_COMPLETED;
	}

	public void handle(EnvironmentEvent event) {
		if (event.type == SshDispatchType.DISPATCH_REQUESTED) {
			sshToHost(event.getHost(), event.getCommand());
			EnvironmentEvent done = new EnvironmentEvent.Builder(SshDispatchType.TASK_COMPLETED, event.environment).build();
			publisher.publish(done);
		}
	}

	public BlockingEventStep buildStepFor(Environment environment, Host host, String command) {
		EnvironmentEvent eventToSend = new EnvironmentEvent.Builder(SshDispatchType.DISPATCH_REQUESTED,environment).withHost(host).withCommand( command).build();
		EnvironmentEvent waitingFor = new EnvironmentEvent.Builder(SshDispatchType.TASK_COMPLETED,environment).withHost(host).withCommand( command).build();

		BlockingEventStep step = BlockingEventStep.factory(publisher,
				eventToSend, waitingFor);
		return step;
	}

}
