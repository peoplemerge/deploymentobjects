package org.deploymentobjects.core.domain.model.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
