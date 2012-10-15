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
package com.peoplemerge.ngds;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployApplicationCommand implements Executable {

	private Dispatchable dispatch;
	private String appName;
	private EnvironmentRepository environmentRepository;
	private Environment environment;
	private List<Step> steps = new LinkedList<Step>();
	public List<Step> getSteps() {
		return steps;
	}

	private Logger logger = LoggerFactory
			.getLogger(DeployApplicationCommand.class);

	private DeployApplicationCommand() {
	}

	public static class Builder {
		private static class Tuple {
			public final String commands;
			public final String role;

			public Tuple(String x, String y) {
				this.commands = x;
				this.role = y;
			}
		}

		List<Tuple> commandsToNodes = new LinkedList<Tuple>();

		private DeployApplicationCommand command = new DeployApplicationCommand();
		private String environmentName;

		public Builder(String appName, String environmentName, EnvironmentRepository repo) {
			command.appName = appName;
			this.environmentName = environmentName;
			command.environmentRepository = repo;
		}

		public DeployApplicationCommand build() {
			command.environment = command.environmentRepository
					.lookupByName(environmentName);
			for (Tuple tuple : commandsToNodes) {
				Executable cmd = new ScriptedCommand(tuple.commands);
				Step step = new Step(cmd, command.environment
						.lookupRoleByName(tuple.role));
				command.steps.add(step);
			}
			return command;
		}


		public Builder addCommandOnNodesByRole(String commands, String role) {
			commandsToNodes.add(new Tuple(commands, role));
			return this;
		}

		public Builder withDispatch(Dispatchable dispatch) {
			command.dispatch = dispatch;
			return this;
		}

	}

	@Override
	public ExitCode execute() {
		for (Step step : steps) {
			try {
				logger.info("Dispatch " + step);
				ExitCode exitcode = dispatch.dispatch(step);
				if (exitcode != ExitCode.SUCCESS) {
					logger.error("Dispatch execution failed: "
							+ step.getOutput());
					return ExitCode.FAILURE;
				}else{
					logger.debug(step.getOutput());
				}
			} catch (Exception e) {
				// TODO fail-fast behavior, alternatives would be desirable for
				// users.
				// Consider rollback
				// Add finer controls than "failure"
				logger.error("Exception dispatching " + e.toString());
				return ExitCode.FAILURE;
			}
		}
		return ExitCode.SUCCESS;
	}

}
