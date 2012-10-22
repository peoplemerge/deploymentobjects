package org.deploymentobjects.core.domain.model.environment;

import java.util.List;

import org.deploymentobjects.core.infrastructure.persistence.zookeeper.HostWatcher;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.HostWatcher.HostAppears;

public interface EnvironmentRepository extends HostWatcher.HostAppears{
	
	public void save(Environment env);
	
	public Environment lookupByName(String name);
	
	public List<Environment> getAll();
	
	public void blockUntilProvisioned(Environment env) throws InterruptedException;
	
}
