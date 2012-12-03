package org.deploymentobjects.core.domain.shared;

import java.io.Serializable;
import java.util.Date;

public abstract class DomainEvent<K extends DomainEvent<?>> extends TypedEvent implements Serializable {
	public abstract boolean sameEventAs(K other);

	public final Date created;

	public static interface EventType {
	};
	
	
	

	public DomainEvent() {
		this.created = new Date();

	}




	public String getId() {
		
		return this.getClass().getSimpleName() +"-"+ this.hashCode();
	}
}
