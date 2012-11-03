package org.deploymentobjects.core.domain.model.configuration;

import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;

public interface ConfigurationManagement {

	public String getKickstartYumRepos();
	public String getKickstartPackages();
	public String getKickstartPost();
	public DispatchableStep postCompleteStep(Host node);
	public DispatchableStep newEnvironment(EnvironmentRepository repo);
	public DispatchableStep nodeProvisioned(Host node);
	
}
