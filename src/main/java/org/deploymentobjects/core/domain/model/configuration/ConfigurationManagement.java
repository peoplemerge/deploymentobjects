package org.deploymentobjects.core.domain.model.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.Step;

public interface ConfigurationManagement {

	public String getKickstartYumRepos();
	public String getKickstartPackages();
	public String getKickstartPost();
	public Step postCompleteStep(Host node);
	public Step newEnvironment(EnvironmentRepository repo);
	public Step nodeProvisioned(Host node);
	
}
