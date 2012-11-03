package org.deploymentobjects.core.domain.model.environment;

import java.util.List;

import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.HostWatcher;

//TODO EnvironmentRepository now depends on HostWatcher and Zookeeper.Watcher in turn.  External coupling is bad.
public interface EnvironmentRepository extends HostWatcher.HostAppears {

	public void save(Environment env);

	public Environment lookupByName(String name);

	public List<Environment> getAll();

	// TODO Refactor to BlockStep	private BlockingEventStep blockingEventStep;

	public Executable buildStepToBlockUntilProvisioned(Environment env);

}
