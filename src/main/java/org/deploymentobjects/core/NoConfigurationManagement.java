package org.deploymentobjects.core;

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

	public Step postCompleteStep(Node node) {
		return new Step(new ScriptedCommand("/bin/ls"), new Node("localhost"));
	}

	public Step newEnvironment(EnvironmentRepository repo) {
		return new Step(new ScriptedCommand("/bin/ls"), new Node("localhost"));
	}

	@Override
	public Step nodeProvisioned(Node node) {
		return new Step(new ScriptedCommand("/bin/ls"), new Node("localhost"));
	}

}
