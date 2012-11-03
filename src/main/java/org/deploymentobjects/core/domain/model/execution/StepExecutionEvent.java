package org.deploymentobjects.core.domain.model.execution;

import org.deploymentobjects.core.domain.shared.DomainEvent;

public class StepExecutionEvent extends DomainEvent<StepExecutionEvent> {

	public final Executable executable;

	public final EventType type;
	public StepExecutionEvent(EventType type, Executable executable){
		this.executable = executable;
		this.type = type;
	}

	public boolean sameEventAs(StepExecutionEvent event) {
		return event.executable.equals(event.executable) && event.type == type; 
	}
	
	public String toString(){
		return this.getClass().getSimpleName() + " "+ type + " " + executable + " " + created ;
	}

}
