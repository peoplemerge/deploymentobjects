package org.deploymentobjects.core.domain.model.environment;

import org.deploymentobjects.core.domain.model.execution.AcceptsCommands;
import org.deploymentobjects.core.domain.model.execution.ControlsHosts;
import org.deploymentobjects.core.domain.model.execution.Step;

public interface NodePool extends AcceptsCommands, ControlsHosts {
	
	public Step createStep(Node.Type type, String hostname);
	
	
}
