package org.deploymentobjects.core.domain.model.configuration;

import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.Executable;

public interface ConfigurationManagement {

	public String getKickstartYumRepos();
	public String getKickstartPackages();
	public String getKickstartPost();
	public Executable postCompleteStep(Host node);
	public Executable newEnvironment(EnvironmentRepository repo);
	public Executable nodeProvisioned(Host node);
	
}
