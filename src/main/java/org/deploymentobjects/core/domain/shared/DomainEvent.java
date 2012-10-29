package org.deploymentobjects.core.domain.shared;

import java.util.Date;

public abstract class DomainEvent<K extends DomainEvent<?>> extends TypedEvent {
	public abstract boolean sameEventAs(K other);

	public final Date occurred;

	public static interface EventType {
	};

	public DomainEvent() {
		this.occurred = new Date();

	}
}
