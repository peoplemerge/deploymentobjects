package org.deploymentobjects.core;

import java.util.List;

public interface EnvironmentRepository extends HostWatcher.NodeAppears{
	
	public void save(Environment env);
	
	public Environment lookupByName(String name);
	
	public List<Environment> getAll();
	
	public void blockUntilProvisioned(Environment env) throws InterruptedException;
	
}
