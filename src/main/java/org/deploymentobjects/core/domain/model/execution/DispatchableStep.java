/************************************************************************
 ** 
 ** Copyright (C) 2011 Dave Thomas, PeopleMerge.
 ** All rights reserved.
 ** Contact: opensource@peoplemerge.com.
 **
 ** This file is part of the NGDS language.
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **    http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 **  
 ** Other Uses
 ** Alternatively, this file may be used in accordance with the terms and
 ** conditions contained in a signed written agreement between you and the 
 ** copyright owner.
 ************************************************************************/
package org.deploymentobjects.core.domain.model.execution;

import java.util.List;

import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.shared.DomainSubscriber;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;

public class DispatchableStep extends Executable implements DomainSubscriber<DispatchEvent> {

	private Dispatchable dispatchable;
	private AcceptsCommands target;
	private Script command;
	private EventPublisher publisher;
	private BlockingEventStep blockingEventStep;
	private DispatchEvent waitingFor;
	private String output = "";

	public String getOutput() {
		return output;
	}

	public enum DispatchEventType implements EventType {
		DISPATCH_REQUESTED, DISPATCH_COMPLETED;
	}

	/*
	 * (EventType type, Dispatchable dispatchable, Script executable,
	 * AcceptsCommands target)
	 */

	public static DispatchableStep factory(EventPublisher publisher,
			Script command, AcceptsCommands target, Dispatchable dispatchable) {
		DispatchEvent eventToSend = new DispatchEvent(
				DispatchEventType.DISPATCH_REQUESTED, dispatchable, command,
				target);
		DispatchEvent waitingFor = DispatchEvent.fromEvent(eventToSend, 
				DispatchEventType.DISPATCH_COMPLETED);
		DispatchableStep retval = new DispatchableStep(publisher, eventToSend,
				waitingFor);
		publisher.addSubscriber(retval, waitingFor);
		return retval;
	}

	private DispatchableStep(EventPublisher publisher,
			DispatchEvent eventToSend, DispatchEvent waitingFor) {
		blockingEventStep = BlockingEventStep.factory(publisher, eventToSend, waitingFor);
		this.dispatchable = eventToSend.dispatchable;
		// TODO is this cast necessary?
		this.command = (Script) eventToSend.executable;
		this.target = eventToSend.target;
		this.publisher = publisher;
		this.waitingFor = waitingFor;
	}

	public void handle(DispatchEvent event) {
		System.err.println(event.getOutput());
		if (event.type == DispatchEventType.DISPATCH_REQUESTED) {
			dispatchable.dispatch(event);
			publisher.publish(waitingFor);
		}/*else if (event.type == DispatchEventType.DISPATCH_COMPLETED){
			output = event.getOutput();
		}*/
		if(event.getOutput() != null && ! event.getOutput().equals("")){
			output = event.getOutput();
		}
	}


	@Override
	public ExitCode execute() {
		ExitCode retval = blockingEventStep.execute();
		return retval;
	}

	
	public List<Host> getHosts() {
		return target.getHosts();
	}

	public String toString() {
		if (output == null) {
			return "Running: " + command + " on " + target;
		} else {
			return "Ran: " + command + " on " + target + " result: " + output;
		}
	}
/*
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		DispatchableStep rhs = (DispatchableStep) obj;
		// TODO invesigate why it fails when .appendSuper(super.equals(obj))
		EqualsBuilder builder = new EqualsBuilder()
				.append(command, rhs.command).append(target, rhs.target);
		if (!(output == null && rhs.output == null)) {
			builder.append(output, rhs.output);
		}
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1245737, 534515).append(command).append(
				target).append(output).toHashCode();
	}
*/

}
