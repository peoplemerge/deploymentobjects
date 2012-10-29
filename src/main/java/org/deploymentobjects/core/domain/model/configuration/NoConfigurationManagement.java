package org.deploymentobjects.core.domain.model.configuration;

import org.deploymentobjects.core.application.ScriptedCommand;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;

public class NoConfigurationManagement implements ConfigurationManagement {

	@Override
	public String getKickstartPackages() {
		return "";
	}

	@Override
	public String getKickstartPost() {
		return "";
	}

	@Override
	public String getKickstartYumRepos() {
		return "";
	}

	public DispatchableStep postCompleteStep(Host node) {
		return null;
	}

	public DispatchableStep newEnvironment(EnvironmentRepository repo) {
		return null;
	}

	@Override
	public DispatchableStep nodeProvisioned(Host node) {
		return null;
	}

}
