package org.deploymentobjects.core.domain.shared;

import java.util.Date;

abstract class TypedEvent extends Event<EventPublisher, TypedSubscriber, TypedEvent> {

	public TypedEvent() {
		this.occurred = new Date();
	}

	protected final Date occurred;
	
	public String toString(){
		return super.toString() + " " + occurred ;
	}
}
