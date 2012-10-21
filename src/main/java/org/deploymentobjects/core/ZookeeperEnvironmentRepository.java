package org.deploymentobjects.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperEnvironmentRepository implements EnvironmentRepository {

	public static final String SEPERATOR = ", ";
	private ZookeeperPersistence persistence;
	private Logger logger = LoggerFactory
			.getLogger(ZookeeperEnvironmentRepository.class);

	public ZookeeperEnvironmentRepository(ZookeeperPersistence persistence) {
		this.persistence = persistence;
	}

	private String environmentsStr = "environments";
	private String hostsKey = "hosts";

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
			Environment env = lookup(composite);
			retval.add(env);
		}
		return retval;
	}

	private Environment lookup(Composite composite) {

		String environmentName = composite.getKey().substring(
				composite.getKey().lastIndexOf("/") + 1);
		Environment env = new Environment(environmentName);
		// If there this environment has nodes not in a role
		String nodesWithoutRoles = composite.getValue();
		if (!nodesWithoutRoles.equals("")) {
			for (String nodeWithoutRole : nodesWithoutRoles.split(SEPERATOR)) {
				String key = hostsKey + "/" + nodeWithoutRole;
				Composite hostComposite = persistence.retrieve(key);
				Node node = new Node(nodeWithoutRole, hostComposite.getValue());
				Composite domainname = hostComposite.getChild(key + "/domainname");
				if(domainname != null){
					node.setDomainname(domainname.getValue());
				}
				env.addNode(node);
			}
		}
		// If there this environment has nodes in a role
		Composite rolesComposite = composite.getChild(composite.getKey() + "/"
				+ "roles");
		if (rolesComposite != null) {
			for (Composite roleComposite : rolesComposite.getChildren()) {
				String roleStr = roleComposite.getKey().substring(
						roleComposite.getKey().lastIndexOf("/") + 1);
				Role role = new Role(roleStr);
				String hostsStr = roleComposite.getValue();
				for (String hostName : hostsStr.split(SEPERATOR)) {
					String key = hostsKey + "/" + hostName;
					Composite hostComposite = persistence.retrieve(key);
					if (env.containsHostNamed(hostName)) {
						Node node = env.getNodeByName(hostName);
						node.addRole(role);
						Composite domainname = hostComposite.getChild(key + "/domainname");
						if(domainname != null){
							node.setDomainname(domainname.getValue());
						}
					} else {
						Node node = new Node(hostName,
								hostComposite.getValue(), role);
						env.addNode(node);
						Composite domainname = hostComposite.getChild(key + "/domainname");
						if(domainname != null){
							node.setDomainname(domainname.getValue());
						}
					}
				}
			}
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
		// TODO if name != envComposite.key exception
		// String hostsStr = envComposite.getValue();

		return lookup(envComposite);
	}

	@Override
	public void save(Environment env) {
		// TODO clean up any existing entries in environment, removing excess
		// ones. TODO test corner cases.
		String hostsWithoutRole = "";
		Map<String, String> rolesToHostNames = new TreeMap<String, String>();
		for (Node node : env.getNodes()) {
			if (node.getRoles().size() == 0) {
				if (hostsWithoutRole == "") {
					hostsWithoutRole = node.getHostname();
				} else {
					hostsWithoutRole += ", " + node.getHostname();
				}
			} else {
				for (Role role : node.getRoles()) {
					if (rolesToHostNames.containsKey(role.getName())) {
						String hostsToThatRole = rolesToHostNames.get(role
								.getName())
								+ SEPERATOR + node.getHostname();
						rolesToHostNames.put(role.getName(), hostsToThatRole);
					} else {
						rolesToHostNames
								.put(role.getName(), node.getHostname());

					}

				}
			}
			if (node.getIp() != null) {
				Composite host = new Composite(hostsKey + "/" + node.getHostname(),
						node.getIp());
				try {
					persistence.save(host);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			if(node.getDomainname() != null){
				Composite domainname = new Composite(hostsKey + "/" + node.getHostname() + "/domainname",
						node.getDomainname());
				try {
					persistence.save(domainname);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Composite environment = new Composite(environmentsStr + "/"
				+ env.getName(), hostsWithoutRole);
		Composite roles = new Composite(environment.getKey() + "/roles", "");
		environment.addChild(roles);
		for(String role : rolesToHostNames.keySet()){
			String hostnamesToRole = rolesToHostNames.get(role);
			Composite roleComposite = new Composite(roles.getKey() + "/" + role, hostnamesToRole);
			roles.addChild(roleComposite);
		}
		try {
			persistence.save(environment);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Map<String, CountDownLatch> latches = new HashMap<String, CountDownLatch>();
	private Map<String, Environment> environmentsToProvision = new HashMap<String, Environment>();

	// TODO more unit tests around this logic
	public synchronized void nodeAppears(Node appeared) {
		// logger.debug("appeared: " + appeared);
		for (String environmentName : environmentsToProvision.keySet()) {
			boolean hasRemaining = false;
			Environment environment = environmentsToProvision
					.get(environmentName);
			for (Node node : environment.getNodes()) {
				if (node.getHostname().equals(appeared.getHostname())) {
					logger.debug("provisioned: " + node);
					node.setProvisioned();
				} else {
					if (!node.isProvisioned()) {
						//logger.debug("not provisioned yet: " + node);
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
