package org.deploymentobjects.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public interface ConfigurationManagement {

	public String getKickstartYumRepos();
	public String getKickstartPackages();
	public String getKickstartPost();
	public Step postCompleteStep(Node node);
	public Step newEnvironment(EnvironmentRepository repo);
	public Step nodeProvisioned(Node node);
	
}
