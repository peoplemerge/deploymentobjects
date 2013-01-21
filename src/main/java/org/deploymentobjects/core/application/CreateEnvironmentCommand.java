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
package org.deploymentobjects.core.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.configuration.NamingService;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.configuration.NoConfigurationManagement;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.HostPool;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartTemplateService;
import org.deploymentobjects.core.domain.model.execution.ConcurrentSteps;
import org.deploymentobjects.core.domain.model.execution.CreatesJob;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.model.execution.PersistStep;
import org.deploymentobjects.core.domain.model.execution.SequentialSteps;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.configuration.TemplateHostsFile;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEnvironmentCommand implements CreatesJob {

	private List<Host> nodes = new ArrayList<Host>();
	private String environmentName;
	// private Persistence persistence;
	private EnvironmentRepository repo;
	private Dispatchable dispatchable;
	private KickstartTemplateService kickstartServer;
	private NamingService namingService;
	private Logger logger = LoggerFactory
			.getLogger(CreateEnvironmentCommand.class);
	private ConfigurationManagement configurationManagement;
	private EventStore eventStore;
	private EventPublisher publisher;
	private Environment environment;

	private CreateEnvironmentCommand() {
	}

	public static class Builder {

		// "Create a new environment called development using 1 small nodes from dom0."
		CreateEnvironmentCommand command = new CreateEnvironmentCommand();

		public Builder(String environmentName, EnvironmentRepository repo,
				EventPublisher publisher) {
			command.environmentName = environmentName;
			command.repo = repo;
			command.publisher = publisher;
		}

		public Builder withNodes(int quantity, Host.Type type, HostPool pool,
				Role... roles) {
			for (int i = 1; i <= quantity; i++) {
				String roleName = "";
				for (Role role : roles) {
					roleName += role.getName();
				}
				Host node = new Host(command.environmentName + roleName + i,
						"peoplemerge.com", type, pool, roles);
				command.nodes.add(node);
			}
			return this;
		}

		public Builder withDispatch(Dispatchable d) {
			command.dispatchable = d;
			return this;
		}

		public Builder withKickstartServer(
				KickstartTemplateService kickstartServer) {
			command.kickstartServer = kickstartServer;
			return this;
		}

		public Builder withNamingService(NamingService namingService) {
			command.namingService = namingService;
			return this;
		}

		public Builder withLogger(Logger logger) {
			command.logger = logger;
			return this;
		}

		public Builder withConfigurationManagement(ConfigurationManagement cm) {
			command.configurationManagement = cm;
			return this;
		}

		public Builder withEventStore(EventStore eventStore) {
			command.eventStore = eventStore;
			return this;
		}

		public CreateEnvironmentCommand build() {
			if (command.dispatchable == null) {
				command.dispatchable = new JschDispatch(command.publisher,
						System.getProperty("user.name"));
			}
			if (command.configurationManagement == null) {
				command.configurationManagement = new NoConfigurationManagement();
			}
			if (command.environment == null) {
				command.environment = new Environment(command.environmentName);
				command.environment.getHosts().addAll(command.nodes);
			}
			if (command.kickstartServer == null) {
				command.kickstartServer = KickstartTemplateService
						.factory(command.publisher, command.environment,
								"/mnt/media/software/kickstart", new NfsMount(
										"192.168.0.4", "/media")/*
																 * TODO better
																 * param
																 */,
								command.configurationManagement);
			}
			if (command.namingService == null) {
				command.namingService = new TemplateHostsFile();
			}
			if (command.eventStore == null) {
				command.eventStore = new InMemoryEventStore();
			}
			return command;
		}

	}

	// private long startTime;

	public Job create() {

		SequentialSteps sequence = new SequentialSteps(publisher);

		PersistStep persistStep = new PersistStep(repo, publisher, environment);
		sequence.add(persistStep);

		// ConcurrentSteps concurrent = new ConcurrentSteps(publisher);

		for (Host host : environment.getHosts()) {

			Executable writeKickstart = kickstartServer.buildStepFor(
					environment, host);
			Executable create = host.getSource().createStep(host.getType(),
					host);
			Executable block = host.getSource().buildStepForHostToStop(
					environment, host);
			Executable start = host.getSource().buildStepForStartingHost(
					environment, host);
			Executable hostRestarted = repo
					.buildStepToBlockUntilProvisioned(environment);
			Executable configMgtStep = configurationManagement
					.postCompleteStep(host);
			/*
			 * SequentialSteps blockAndStart = new SequentialSteps(publisher);
			 * blockAndStart.add(writeKickstart); blockAndStart.add(create);
			 * blockAndStart.add(block); blockAndStart.add(start);
			 * blockAndStart.add(hostRestarted);
			 * blockAndStart.add(configMgtStep);
			 */
			// concurrent.add(blockAndStart);
			sequence.add(writeKickstart);
			sequence.add(create);
			sequence.add(block);
			sequence.add(start);
			sequence.add(hostRestarted);
			sequence.add(configMgtStep);
		}
		// sequence.add(concurrent);
		// sequence.add(blockAndStart);
		Executable namingServiceStep = namingService.buildStepToUpdate(
				publisher, repo);
		sequence.add(namingServiceStep);

		Executable configStep = configurationManagement.newEnvironment(repo);
		sequence.add(configStep);
		ConcurrentSteps configSteps = new ConcurrentSteps(publisher);

		for (Host host : environment.getHosts()) {
			Executable puppetCatalogStep = configurationManagement
					.nodeProvisioned(host);
			configSteps.add(puppetCatalogStep);
		}
		sequence.add(configSteps);

		Job job = new Job(publisher, sequence, "CreateEnvironment-"
				+ environmentName);
		return job;
	}

	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}

}
