package com.peoplemerge.ngds;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperEnvironmentRepository implements EnvironmentRepository {

	public static final String SEPERATOR = ", ";
	private ZookeeperPersistence persistence;
	private Logger logger = LoggerFactory.getLogger(ZookeeperEnvironmentRepository.class);


	public ZookeeperEnvironmentRepository(ZookeeperPersistence persistence) {
		this.persistence = persistence;
	}

	private String environmentsStr = "environments";
	private String hostsStr = "hosts";

	@Override
	public List<Environment> getAll() {
		List<Environment> retval = new LinkedList<Environment>();
		Composite envComposite;
		try {
			envComposite = persistence.retrieve(environmentsStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return retval;
		}
		for (Composite composite : envComposite.getChildren()) {
			Environment env = lookup(composite.getKey(), composite.getValue());
			retval.add(env);
		}
		return retval;
	}

	private Environment lookup(String name, String hosts) {
		Environment env = new Environment(name);
		for (String hostname : hosts.split(SEPERATOR)) {
			String key = hostsStr + "/" + hostname;
			Composite hostComposite;
			try {
				hostComposite = persistence.retrieve(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return env;
			}
			// TODO wire in nodepool to framework. There's nowhere to get it
			// from at the moment!

			Node node = new Node(hostname, hostComposite.getValue());
			// TODO is host missing? throw exception. Write test coverage for
			// this
			env.addNode(node);
		}
		return env;
	}

	@Override
	public Environment lookupByName(String name) {
		Composite envComposite;
		try {
			envComposite = persistence.retrieve(environmentsStr + "/" + name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// TODO if name != envComposite.key
		String hostsStr = envComposite.getValue();
		return lookup(name, hostsStr);
	}

	@Override
	public void save(Environment env) {
		// TODO clean up any existing entries in environment, removing excess
		// ones. TODO test corner cases.
		String allHosts = "";
		for (Node node : env.getNodes()) {
			if (allHosts == "") {
				allHosts = node.getHostname();
			} else {
				allHosts += ", " + node.getHostname();
			}
			if (node.getIp() == null) {
				continue;
			}
			Composite host = new Composite(hostsStr + "/" + node.getHostname(),
					node.getIp());
			try {
				persistence.save(host);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Composite environment = new Composite(environmentsStr + "/"
				+ env.getName(), allHosts);
		try {
			persistence.save(environment);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Map<String, CountDownLatch> latches = new HashMap<String, CountDownLatch>();
	private Map<String, Environment> environmentsToProvision = new HashMap<String, Environment>();

	//TODO more unit tests around this logic
	public synchronized void nodeAppears(Node appeared) {
		//logger.debug("appeared: " + appeared);
		for (String environmentName : environmentsToProvision.keySet()) {
			boolean hasRemaining = false;
			Environment environment = environmentsToProvision.get(environmentName);
			for (Node node : environment.getNodes()) {
				if (node.getHostname().equals(appeared.getHostname())) {
					logger.debug("provisioned: " + node);
					node.setProvisioned();
				} else {
					if (!node.isProvisioned()) {
						logger.debug("not provisioned yet: " + node);
						hasRemaining = true;
					}
				}
			}

			if (!hasRemaining) {
				logger.debug("complete: " + environmentName);
				CountDownLatch latch = latches.get(environmentName);
				environmentsToProvision.remove(environmentName);
				latches.remove(environmentName);
				latch.countDown();
				return;
			}
		}
	}

	@Override
	public void blockUntilProvisioned(Environment env)
			throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		environmentsToProvision.put(env.getName(), env);
		latches.put(env.getName(), latch);
		new HostWatcher(this, persistence);
		latch.await();

	}
}
