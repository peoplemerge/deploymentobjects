package org.deploymentobjects.core.domain.model.environment;

import java.util.ArrayList;
import java.util.List;

import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.Script;


/**
 * NoEnvironmentRepository
 * Used by LangTest
 * @author dave
 *
 */
public class NoEnvironmentRepository implements EnvironmentRepository {


	@Override
	public List<Environment> getAll() {
		return new ArrayList<Environment>();
	}

	@Override
	public Environment lookupByName(String name) {
		return new Environment(name);
	}

	@Override
	public void save(Environment env) {

	}

	@Override
	public void nodeAppears(Host appeared) {

	}

	@Override
	public Executable buildStepToBlockUntilProvisioned(Environment env) {
		// TODO Auto-generated method stub
		return new Script("touch /dev/null");
	}

}
