package com.peoplemerge.ngds;

public interface NodePool extends AcceptsCommands {
	
	public Step createStep(Node.Type type, String hostname);
	
	
	
}
