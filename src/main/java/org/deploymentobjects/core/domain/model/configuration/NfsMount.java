package org.deploymentobjects.core.domain.model.configuration;

public class NfsMount implements Storage {

	
 public NfsMount(String ip, String mountpoint) {
		super();
		this.ip = ip;
		this.mountpoint = mountpoint;
	}

	private String ip;
	private String mountpoint;
	// TODO: Node has hostname but not IP.
	// IP is needed here but not
	// usually. Think about the inheritance model better.
	// All nodes
	// probably need storage, right? But storage has different uses.
	// Also this is all hardcoded
	public String getIp() {
		return ip;
	}

	public String getMountPoint() {
		return ip + ":" + mountpoint;
	}
}
