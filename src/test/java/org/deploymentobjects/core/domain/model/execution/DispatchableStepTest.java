package org.deploymentobjects.core.domain.model.execution;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.junit.Assert;
import org.junit.Test;

public class DispatchableStepTest {

	//TODO check that there's output
	
	@Test
	public void willDispatch(){
		EventStore eventStore = new InMemoryEventStore();
		EventPublisher publisher = new EventPublisher(eventStore);
		Host host = new Host("name");
		Script command = new Script("test");
		Dispatchable dispatchable = mock(Dispatchable.class);
		DispatchableStep step =  DispatchableStep.factory(publisher, command, host, dispatchable);
		//when(step.execute())
		verify(dispatchable,never()).dispatch(any(DispatchEvent.class));
		step.execute();
		verify(dispatchable, times(1)).dispatch(any(DispatchEvent.class));
		Assert.assertTrue(eventStore.toString().contains("DISPATCH_REQUESTED"));
		Assert.assertTrue(eventStore.toString().contains("DISPATCH_COMPLETED"));
		System.out.println(eventStore);
	}
}
