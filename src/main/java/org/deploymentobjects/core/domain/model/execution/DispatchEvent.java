package org.deploymentobjects.core.domain.model.execution;

import org.deploymentobjects.core.domain.model.environment.Host;



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
	
	@Override
	public boolean sameEventAs(StepExecutionEvent event) {
		if(event.getClass() != getClass()){
			return false;
		}
		DispatchEvent in = (DispatchEvent) event;
		if(target instanceof Host && in.target instanceof Host){
			return super.sameEventAs(event) && ((Host)target).sameIdentityAs((Host)in.target);
		}else{
			return super.sameEventAs(event) && target.equals(in.target);
		}
	}
}
