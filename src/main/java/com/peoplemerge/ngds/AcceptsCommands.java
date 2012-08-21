package com.peoplemerge.ngds;

import java.util.List;

public interface AcceptsCommands {

	public Job accept(Executable command);
	
	public List<Node> getNodes();
	
}
