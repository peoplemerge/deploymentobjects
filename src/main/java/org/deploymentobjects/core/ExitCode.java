package org.deploymentobjects.core;

public class ExitCode {

	private int value = 0;
	
	private ExitCode (int value){
		this.value = value;
	}
	
	public static final ExitCode SUCCESS = new ExitCode(0);
	public static final ExitCode FAILURE = new ExitCode(1);
	
	public ExitCode factory(int value){
		if (value == 0) return SUCCESS;
		if (value == 1) return FAILURE;
		else return new ExitCode(value); // also failure
	}
	
	public boolean isSuccessful(){
		return value == 0;
	}	
	
	public String toString(){
		return String.valueOf(value);
	}
	
	public int compareTo(ExitCode compared){
		return compared.value - value;
	}
	
	public boolean equals(ExitCode compared){
		return compared.value == value;
	}
	
	public String hashcode(){
		return toString();
	}
	
}
