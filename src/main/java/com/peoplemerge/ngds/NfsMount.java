package com.peoplemerge.ngds;

public class NfsMount implements Storage {

	// TODO: Node has hostname but not IP.
	// IP is needed here but not
	// usually. Think about the inheritance model better.
	// All nodes
	// probably need storage, right? But storage has different uses.
	// Also this is all hardcoded
	public String getIp() {
		return "192.168.10.107";
	}

	public String getMountPoint() {
		return getIp() + ":/media";
	}
}
