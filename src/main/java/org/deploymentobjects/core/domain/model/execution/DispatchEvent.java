package org.deploymentobjects.core.domain.model.execution;



public class DispatchEvent extends StepExecutionEvent {

	public final Dispatchable dispatchable;
	public final AcceptsCommands target;
	private String output = "";
	public String getContents(){
		//TODO rethink this
		return ((Script) executable).getContents();
	}

	public DispatchEvent addOutput(String output) {
		if(this.output != ""){
			this.output += "\n";
		}
		this.output += output;
		return this;
	}

	public String getOutput() {
		return output;
	}

	
	public DispatchEvent(EventType type, Dispatchable dispatchable,
			Script executable, AcceptsCommands target) {
		super(type, executable);
		this.dispatchable = dispatchable;
		this.target = target;
	}

	
	public static DispatchEvent fromEvent(DispatchEvent from, EventType type){
		DispatchEvent to = new DispatchEvent(type, from.dispatchable, (Script) from.executable, from.target);
		return to;
	}
	//TODO Add StepExecutionEvent.dispatchable and .target to sameEventAs
}
