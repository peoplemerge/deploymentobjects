package org.deploymentobjects.core.domain.model.execution;

import org.deploymentobjects.core.domain.shared.EventHistory;

public abstract class AdvancedExecutable extends Executable {
	
	public abstract void rollback();

	public abstract void resume(EventHistory history);

}
