package org.deploymentobjects.core.domain.model.environment;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.junit.Test;


public class HypervisorPublishingTest {
	
	private EventStore store = mock(EventStore.class);
	private Dispatchable dispatchable = mock(Dispatchable.class);
	
	@Test
	public void hypervisorStores(){
		EventPublisher publisher = new EventPublisher(store);
		
		Environment environment = new Environment("test");
		Host first = new Host("first.example.com");
		environment.add(first);
		
		Host second = new Host("second.example.com");
		environment.add(second);
		new Hypervisor.Builder(publisher, "dummydom0.example.com", new NfsMount(), dispatchable).build();

		
		EnvironmentEvent requestevent = new EnvironmentEvent.Builder(Hypervisor.HypervisorType.REQUESTED, environment ).build();
		publisher.publish(requestevent);
		verify(store,times(1)).store(eq(requestevent));
		EnvironmentEvent hostbuiltevent = new EnvironmentEvent.Builder(Hypervisor.HypervisorType.HOST_BUILT, environment ).withHost(first).build();
		verify(store,times(1)).store(eq(hostbuiltevent));
		EnvironmentEvent secondhostbuiltevent = new EnvironmentEvent.Builder(Hypervisor.HypervisorType.HOST_BUILT, environment ).withHost(second).build();
		verify(store,times(1)).store(eq(secondhostbuiltevent));
		EnvironmentEvent allhostsbuiltevent = new EnvironmentEvent.Builder(Hypervisor.HypervisorType.ALL_HOSTS_BUILT, environment ).build();
		verify(store,times(1)).store(eq(allhostsbuiltevent));
	}
}
