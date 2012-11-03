package org.deploymentobjects.core.domain.model.execution;

public class Script extends Executable{

	public Script(String contents) {
		this.contents = contents;
	}
	private String contents;
	public String getContents(){
		return contents;
	}
	public String toString(){
		return contents;
	}
	@Override
	public ExitCode execute() {
		// TODO Auto-generated method stub
		// Should probably run a local script here.
		return null;
	}
}
