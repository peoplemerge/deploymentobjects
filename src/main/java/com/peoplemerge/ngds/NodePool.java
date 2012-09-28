package com.peoplemerge.ngds;

public interface NodePool extends AcceptsCommands, ControlsMachines {
	
	public Step createStep(Node.Type type, String hostname);
	
	
}
