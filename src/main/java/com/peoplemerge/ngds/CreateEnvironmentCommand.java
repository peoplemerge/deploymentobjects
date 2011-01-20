package com.peoplemerge.ngds;

public class CreateEnvironmentCommand implements Command{

	@Override
	public Result execute() {
		Result retval = new Result();
		retval.setCommand(this);
		return retval;
	}

}
