package org.deploymentobjects.core.domain.shared;



public class EventPublisher extends Publisher<EventPublisher, TypedSubscriber, TypedEvent>{

	private EventStore store;
	
	public EventStore getEventStore(){
		return store;
	}

	public EventPublisher(EventStore store){
		this.store = store;
	}

	public void publish(DomainEvent<?> event) {
		store.store(event);
		super.publish(event);
	}

}
