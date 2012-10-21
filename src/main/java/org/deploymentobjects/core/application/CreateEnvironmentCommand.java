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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.configuration.NamingService;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.configuration.NoConfigurationManagement;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Node;
import org.deploymentobjects.core.domain.model.environment.NodePool;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartServer;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Step;
import org.deploymentobjects.core.infrastructure.configuration.TemplateHostsFile;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEnvironmentCommand implements Executable {

	private List<Node> nodes = new ArrayList<Node>();
	private String environmentName;
	// private Persistence persistence;
	private EnvironmentRepository repo;
	private Dispatchable dispatchable;
	private KickstartServer kickstartServer;
	private NamingService namingService;
	private Logger logger = LoggerFactory
			.getLogger(CreateEnvironmentCommand.class);
	public ConfigurationManagement configurationManagement;

	private CreateEnvironmentCommand() {
	}

	public static class Builder {

		// "Create a new environment called development using 1 small nodes from dom0."
		CreateEnvironmentCommand command = new CreateEnvironmentCommand();

		public Builder(String environmentName, EnvironmentRepository repo) {
			command.environmentName = environmentName;
			command.repo = repo;
		}

		public Builder withNodes(int quantity, Node.Type type, NodePool pool,
				Role... roles) {
			for (int i = 1; i <= quantity; i++) {
				String roleName = "";
				for (Role role : roles) {
					roleName += role.getName();
				}
				Node node = new Node(command.environmentName + roleName + i,
						"peoplemerge.com", type, pool, roles);
				command.nodes.add(node);
			}
			return this;
		}

		public Builder withDispatch(Dispatchable d) {
			command.dispatchable = d;
			return this;
		}

		public Builder withKickstartServer(KickstartServer kickstartServer) {
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

		public CreateEnvironmentCommand build() {
			if (command.dispatchable == null) {
				command.dispatchable = new JschDispatch(System
						.getProperty("user.name"));
			}
			if (command.configurationManagement == null) {
				command.configurationManagement = new NoConfigurationManagement();
			}
			if (command.kickstartServer == null) {
				command.kickstartServer = new KickstartServer(
						"/mnt/media/software/kickstart", new NfsMount(),
						command.configurationManagement);
			}
			if (command.namingService == null) {
				command.namingService = new TemplateHostsFile();
			}
			return command;
		}

	}

	private long startTime;

	@Override
	public ExitCode execute() {
		startTime = System.currentTimeMillis();
		for (Node node : nodes) {
			try {
				logger.info("Writing kickstart for " + node.getHostname());
				kickstartServer.writeKickstartFile(node.getHostname());
			} catch (Exception e) {
				logger.error(e.toString());
				return ExitCode.FAILURE;
			}
		}
		List<Step> dispatched = new LinkedList<Step>();
		for (Node node : nodes) {
			Step step = node.getSource().createStep(node.getType(),
					node.getHostname());
			try {
				logger.info("Dispatch " + step);
				ExitCode exitcode = dispatchable.dispatch(step);
				if (exitcode != ExitCode.SUCCESS) {
					logger.error("Dispatch execution failed: "
							+ step.getOutput());
					return ExitCode.FAILURE;
				}
			} catch (Exception e) {
				// TODO fail-fast behavior, alternatives would be desirable for
				// users.
				// Consider rollback
				// Add finer controls than "failure"
				logger.error("Exception dispatching " + e.toString());
				return ExitCode.FAILURE;
			}
			dispatched.add(step);
		}
		Environment environment = new Environment(environmentName);
		for (Node node : nodes) {
			// TODO extract these timing variables to the grammar
			logger.info("Polling for " + node + " to stop");

			node.getSource().pollForDomainToStop(node.getHostname(), 500,
					600000);
			logger.info("Restarting " + node);
			node.getSource().startHost(node.getHostname());
			
			logger.info("Signing puppet cert for " + node);

			Step step = configurationManagement.postCompleteStep(node);
			ExitCode exitcode;
			try {
				exitcode = dispatchable.dispatch(step);
			} catch (Exception e) {
				logger.error("Exception dispatching " + e.toString());
				return ExitCode.FAILURE;
			}
			if (exitcode != ExitCode.SUCCESS) {
				logger.error("Dispatch execution failed: " + step.getOutput());
				return ExitCode.FAILURE;
			}
			environment.addNode(node);
		}

		logger.info("Waiting for nodes to write to Zookeeper");
		try {
			repo.blockUntilProvisioned(environment);
		} catch (InterruptedException e) {
			logger.error(e.toString());
			return ExitCode.FAILURE;
		}
		
		try {
			logger.info("Saving " + environmentName + " to repo " + repo);
			repo.save(environment);
		} catch (Exception e) {
			logger.error(e.toString());
			return ExitCode.FAILURE;
		}
		
		logger.info("Updating " + namingService);

		namingService.update(repo);

		try {
			logger.info("Updating " + configurationManagement);
			Step step = configurationManagement.newEnvironment(repo);
			ExitCode exitcode = dispatchable.dispatch(step);
			if (exitcode != ExitCode.SUCCESS) {
				logger.error("Dispatch execution failed: " + step.getOutput());
				return ExitCode.FAILURE;
			}

		} catch (Exception e) {
			logger.error(e.toString());
			return ExitCode.FAILURE;
		}

		try {
			for (Node node : nodes) {
				logger.info("Using " + configurationManagement
						+ " to complete node provisioning on " + node);
				Step step = configurationManagement.nodeProvisioned(node);
				ExitCode exitcode = dispatchable.dispatch(step);
				if (exitcode != ExitCode.SUCCESS) {
					logger.error("Dispatch execution failed: "
							+ step.getOutput());
					return ExitCode.FAILURE;
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
			return ExitCode.FAILURE;
		}

		long duration = (System.currentTimeMillis() - startTime) / 1000;
		logger.info("Operation completed in " + duration + "s");
		return ExitCode.SUCCESS;

	}
	
	public String toString(){
		return new ReflectionToStringBuilder(this).toString();
	}

}
