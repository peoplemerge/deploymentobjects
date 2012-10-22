package org.deploymentobjects.core.domain.model.execution;


public interface ControlsHosts {

	public boolean startHost(String vm);

	public boolean pollForDomainToStop(String vm, int pollMs, int timeoutMs);

	public boolean stopHost(String vm);

	public boolean pollForDomainToStart(String vm, int pollMs, int timeoutMs);

}
