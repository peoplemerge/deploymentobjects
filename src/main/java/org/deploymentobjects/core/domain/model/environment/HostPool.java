package org.deploymentobjects.core.domain.model.environment;

import org.deploymentobjects.core.domain.model.execution.AcceptsCommands;
import org.deploymentobjects.core.domain.model.execution.ControlsHosts;
import org.deploymentobjects.core.domain.model.execution.Executable;

public interface HostPool extends AcceptsCommands, ControlsHosts {
	
	public Executable createStep(Host.Type type, String hostname);
	
	
}
