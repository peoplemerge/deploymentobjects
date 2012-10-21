package org.deploymentobjects.core.domain.model.environment;

import org.deploymentobjects.core.domain.model.execution.AcceptsCommands;
import org.deploymentobjects.core.domain.model.execution.ControlsMachines;
import org.deploymentobjects.core.domain.model.execution.Step;

public interface NodePool extends AcceptsCommands, ControlsMachines {
	
	public Step createStep(Node.Type type, String hostname);
	
	
}
