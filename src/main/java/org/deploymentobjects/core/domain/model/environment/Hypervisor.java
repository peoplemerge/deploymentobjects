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
package org.deploymentobjects.core.domain.model.environment;

import java.util.List;

import org.deploymentobjects.core.application.ScriptedCommand;
import org.deploymentobjects.core.domain.model.configuration.Storage;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.model.execution.ControlsHosts;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;
import org.deploymentobjects.core.domain.shared.DomainSubscriber;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;
import org.deploymentobjects.core.infrastructure.execution.LibvirtAdapter;

public class Hypervisor extends Host implements
		DomainSubscriber<EnvironmentEvent>, HostPool {

	private ControlsHosts controller;
	private Storage storage;
	private String userAt = "";
	private EventPublisher publisher;

	public enum HypervisorType implements EventType {
		REQUESTED, HOST_BUILT, ALL_HOSTS_BUILT
	}

	private synchronized ControlsHosts getController() {
		if (controller == null) {
			controller = new LibvirtAdapter("qemu+ssh://" + getHostname()
					+ "/system?socket=/var/run/libvirt/libvirt-sock");
		}
		return controller;
	}
	public static class Builder{
		private Hypervisor hypervisor;
		public Builder(EventPublisher publisher,
				String hostname, Storage storage){
			hypervisor = new Hypervisor(publisher, hostname, storage);
		}
		public Builder withUserName(String userName){
			hypervisor.userAt = userName + "@";
			return this;
		}
		public Hypervisor build(){
			hypervisor.publisher.addSubscriber(hypervisor, new EnvironmentEvent.Builder(
					HypervisorType.REQUESTED, null).build());
			return hypervisor;
			
		}
	}


	private Hypervisor(EventPublisher publisher,
			String hostname, Storage storage) {
		super(hostname);
		this.publisher = publisher;
		this.storage = storage;
	}

	// TODO consider pushing up Storage constructor
	public Hypervisor(String hostname, Storage storage) {
		super(hostname);
		this.storage = storage;
	}

	public String getCreateCommand() {
		return "";
	}

	public void provisionHost(Host host) {
		// Actually go to VMWare, EC2, etc.
		System.out.println("Hypervisor is provisioning " + host.getHostname());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public BlockingEventStep buildStepFor(Environment environment) {
		EnvironmentEvent eventToSend = new EnvironmentEvent.Builder(
				HypervisorType.REQUESTED, environment).build();
		EnvironmentEvent waitingFor = new EnvironmentEvent.Builder(
				HypervisorType.ALL_HOSTS_BUILT, environment).build();

		BlockingEventStep step = BlockingEventStep.factory(publisher,
				eventToSend, waitingFor);
		return step;

	}

	public void handle(EnvironmentEvent event) {
		if (event.type == HypervisorType.REQUESTED) {
			for (Host host : event.environment.getHosts()) {
				provisionHost(host);
				EnvironmentEvent hostEvent = new EnvironmentEvent.Builder(
						HypervisorType.HOST_BUILT, event.environment).withHost(
						host).build();

				publisher.publish(hostEvent);
			}
			EnvironmentEvent hostsBuiltEvent = new EnvironmentEvent.Builder(
					HypervisorType.ALL_HOSTS_BUILT, event.environment).build();

			publisher.publish(hostsBuiltEvent);
		}
	}


	// TODO need to rethink how this interface is used
	@Override
	public boolean pollForDomainToStart(String vm, int pollMs, int timeoutMs) {
		return getController().pollForDomainToStart(vm, pollMs, timeoutMs);
	}

	@Override
	public boolean pollForDomainToStop(String vm, int pollMs, int timeoutMs) {
		return getController().pollForDomainToStop(vm, pollMs, timeoutMs);
	}

	@Override
	public boolean startHost(String vm) {
		return getController().startHost(vm);
	}

	@Override
	public boolean stopHost(String vm) {
		return getController().stopHost(vm);

	}

	@Override
	public String toString() {
		return userAt + super.toString();
	}

	// TODO encapsulate better. This domain class is anemic!
	@Override
	public Executable createStep(Type type, String name) {
		// TODO Critical - this is a big hack hardcoding these commands here.
		// They should really come from the grammar.
		ScriptedCommand command = new ScriptedCommand(
				"/mnt/media/software/kickstart/launch.sh " + name + " "
						+ storage.getMountPoint());
		DispatchableStep step = new DispatchableStep(command, this);
		return step;
	}


	@Override
	public List<Host> getHosts() {
		// TODO Auto-generated method stub
		return null;
	}

}
