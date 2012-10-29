package org.deploymentobjects.core.domain.shared;


public interface EventStore {
	public void store(DomainEvent<?> event);
	public EventHistory lookup(String sagaName);
}
