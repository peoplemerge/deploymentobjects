package org.deploymentobjects.core.infrastructure.persistence;

import org.deploymentobjects.core.domain.shared.DomainEvent;
import org.deploymentobjects.core.domain.shared.EventHistory;
import org.deploymentobjects.core.domain.shared.EventStore;

public class InMemoryEventStore implements EventStore {

	
	private EventHistory history = new EventHistory();
	public EventHistory lookup(String sagaName) {
		return new EventHistory();
	}

	
	public void store(DomainEvent<?> event) {
		history.events.add(event);
	}

	public String toString(){
		return history.toString();
	}
}
