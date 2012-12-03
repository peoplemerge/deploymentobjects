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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.deploymentobjects.core.domain.model.configuration.Storage;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.model.execution.ControlsHosts;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.Script;
import org.deploymentobjects.core.domain.shared.DomainSubscriber;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;
import org.deploymentobjects.core.infrastructure.execution.LibvirtAdapter;

public class Hypervisor implements
		DomainSubscriber<EnvironmentEvent>, HostPool {

	private ControlsHosts controller;
	private Storage storage;
	private String userAt = "";
	private EventPublisher publisher;
	private Dispatchable dispatch;
	private Host dom0;

	public enum HypervisorType implements EventType {
		REQUESTED, HOST_BUILT, ALL_HOSTS_BUILT, BLOCK_UNTIL_HOST_STOPPED, HOST_STOPPED, BLOCK_UNTIL_HOST_STARTED, HOST_STARTED
	}

	private synchronized ControlsHosts getController() {
		if (controller == null) {
			controller = new LibvirtAdapter("qemu+ssh://" + dom0.getHostname()
					+ "/system?socket=/var/run/libvirt/libvirt-sock");
		}
		return controller;
	}

	public static class Builder {
		private Hypervisor hypervisor;

		public Builder(EventPublisher publisher, String hostname,
				Storage storage, Dispatchable dispatch) {
			hypervisor = new Hypervisor(publisher, hostname, storage, dispatch);
		}

		public Builder withUserName(String userName) {
			hypervisor.userAt = userName + "@";
			return this;
		}

		public Hypervisor build() {
			hypervisor.publisher
					.addSubscriber(hypervisor, new EnvironmentEvent.Builder(
							HypervisorType.REQUESTED, null).build());
			return hypervisor;

		}
	}

	private Hypervisor(EventPublisher publisher, String hostname,
			Storage storage, Dispatchable dispatch) {
		this.dom0 = new Host(hostname);
		this.publisher = publisher;
		this.storage = storage;
		this.dispatch = dispatch;
	}

	// TODO consider pushing up Storage constructor
	public Hypervisor(String hostname, Storage storage) {
		this.dom0 = new Host(hostname);
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

	@Override
	public BlockingEventStep buildStepForHostToStop(
			Environment environment, Host host) {
		EnvironmentEvent eventToSend = new EnvironmentEvent.Builder(
				HypervisorType.BLOCK_UNTIL_HOST_STOPPED, environment).withHost(
				host).build();
		EnvironmentEvent waitingFor = new EnvironmentEvent.Builder(
				HypervisorType.HOST_STOPPED, environment).withHost(host)
				.build();

		BlockingEventStep step = BlockingEventStep.factory(publisher,
				eventToSend, waitingFor);
		return step;
	};

	private boolean pollForDomainToStart(String vm, int pollMs, int timeoutMs) {
		return getController().pollForDomainToStart(vm, pollMs, timeoutMs);
	}

	private boolean pollForDomainToStop(String vm, int pollMs, int timeoutMs) {
		return getController().pollForDomainToStop(vm, pollMs, timeoutMs);
	}

	// TODO consider pulling up. The ControlsHosts interface could use some
	// work.
	public boolean startHost(String vm) {
		return getController().startHost(vm);
	}

	public boolean stopHost(String vm) {
		return getController().stopHost(vm);

	}


	public BlockingEventStep buildStepForStartingHost(Environment environment,
			Host host) {
		EnvironmentEvent eventToSend = new EnvironmentEvent.Builder(
				HypervisorType.BLOCK_UNTIL_HOST_STARTED, environment).withHost(host).build();
		EnvironmentEvent waitingFor = new EnvironmentEvent.Builder(
				HypervisorType.HOST_STARTED, environment).withHost(host)
				.build();

		BlockingEventStep step = BlockingEventStep.factory(publisher,
				eventToSend, waitingFor);
		return step;

	}

	private ExecutorService executor = Executors.newCachedThreadPool();

	public void handle(EnvironmentEvent event) {
		/*
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
		}*/
		if (event.type == HypervisorType.BLOCK_UNTIL_HOST_STOPPED) {
			final Host host = event.getHost();
			final Environment environment = event.environment;
			// TODO spawn a thread.
			Runnable runnable = new Runnable() {
				public void run() {
					pollForDomainToStop(host.getHostname(), 500, 600000);
					EnvironmentEvent hostStoppedEvent = new EnvironmentEvent.Builder(
							HypervisorType.HOST_STOPPED, environment).withHost(
							host).build();
					publisher.publish(hostStoppedEvent);
				}
			};
			executor.execute(runnable);
		}
		if (event.type == HypervisorType.BLOCK_UNTIL_HOST_STARTED) {
			final Host host = event.getHost();
			final Environment environment = event.environment;
			// TODO spawn a thread.
			Runnable runnable = new Runnable() {
				public void run() {
					startHost(host.getHostname());
					EnvironmentEvent hostStoppedEvent = new EnvironmentEvent.Builder(
							HypervisorType.HOST_STARTED, environment).withHost(
							host).build();
					publisher.publish(hostStoppedEvent);
				}
			};
			executor.execute(runnable);
		}
	}

	@Override
	public String toString() {
		return userAt + super.toString();
	}

	// TODO encapsulate better. This domain class is anemic!
	@Override
	public Executable createStep(Host.Type type, Host host) {
		// TODO Critical - this is a big hack hardcoding these commands here.
		// They should really come from the grammar.
		Script command = new Script("/mnt/media/software/kickstart/launch.sh "
				+ host.getHostname() + " " + storage.getMountPoint());

		DispatchableStep step = DispatchableStep.factory(publisher, command,
				dom0, dispatch);
		return step;
	}

	@Override
	public List<Host> getHosts() {
		// TODO return hosts running
		return null;
	}

}
