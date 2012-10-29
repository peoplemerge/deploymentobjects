package org.deploymentobjects.core.domain.model.execution;

import java.util.Date;

import org.deploymentobjects.core.domain.shared.DomainEvent;


public class DispatchEvent extends StepExecutionEvent {

	public final Dispatchable dispatchable;
	public final AcceptsCommands target;
	private String output = "";
	public enum DispatchEventType implements EventType{
		DISPATCH_REQUESTED, DISPATCH_COMPLETED;
	}
	public void addOutput(String output) {
		if(this.output != ""){
			this.output += "\n";
		}
		this.output += output;
	}

	public String getOutput() {
		return output;
	}

	
	public DispatchEvent(EventType type, Date occurred, Dispatchable dispatchable,
			Script executable, AcceptsCommands target) {
		super(type, executable);
		this.dispatchable = dispatchable;
		this.target = target;
	}


}
