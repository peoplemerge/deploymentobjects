package org.deploymentobjects.core;

import java.util.ArrayList;
import java.util.List;

/**
 * NoEnvironmentRepository
 * Used by LangTest
 * @author dave
 *
 */
public class NoEnvironmentRepository implements EnvironmentRepository {

	@Override
	public void blockUntilProvisioned(Environment env)
			throws InterruptedException {

	}

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
	public void nodeAppears(Node appeared) {

	}

}
