package org.deploymentobjects.core.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.Script;

public class InMemoryEnvironmentRepository implements EnvironmentRepository {

	private List<Environment> environments = new ArrayList<Environment>();
	
	public void save(Environment environment) {
		environments.add(environment);
	}
	
	public String toString(){
		return environments.toString();
	}


	@Override
	public List<Environment> getAll() {
		return environments;
	}

	@Override
	public Environment lookupByName(String name) {
		for(Environment environment : environments){
			if(environment.getName().equals(name)){
				return environment;
			}
		}
		return null;
	}

	@Override
	public void nodeAppears(Host appeared) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Executable buildStepToBlockUntilProvisioned(Environment env) {
		// TODO Auto-generated method stub
		return new Script("ls /dev/null");
	}
}
