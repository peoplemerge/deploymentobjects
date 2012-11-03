package org.deploymentobjects.core.domain.shared;

import java.util.Date;

public abstract class DomainEvent<K extends DomainEvent<?>> extends TypedEvent {
	public abstract boolean sameEventAs(K other);

	public final Date created;

	public static interface EventType {
	};
	
	
	

	public DomainEvent() {
		this.created = new Date();

	}
}
