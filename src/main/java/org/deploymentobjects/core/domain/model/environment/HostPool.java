package org.deploymentobjects.core.domain.model.environment;

import org.deploymentobjects.core.domain.model.execution.AcceptsCommands;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.model.execution.Executable;

public interface HostPool extends AcceptsCommands {
	
	public Executable createStep(Host.Type type, Host host);

	public BlockingEventStep buildStepForHostToStop(Environment environment, Host host);

	public BlockingEventStep buildStepForStartingHost(Environment environment, Host host);
	
	
}
