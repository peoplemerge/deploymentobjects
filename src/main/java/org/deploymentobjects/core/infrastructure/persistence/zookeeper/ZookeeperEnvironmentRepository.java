package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentEvent;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.shared.DomainSubscriber;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence.ZookeeperPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperEnvironmentRepository extends ZookeeperRepository
		implements DomainSubscriber<EnvironmentEvent>, EnvironmentRepository {

	public static final String SEPERATOR = ", ";
	private ZookeeperPersistence persistence;
	private Logger logger = LoggerFactory
			.getLogger(ZookeeperEnvironmentRepository.class);
	private EventPublisher publisher;

	public static ZookeeperEnvironmentRepository factory(ZookeeperPersistence persistence,
			EventPublisher publisher) {
		ZookeeperEnvironmentRepository retval = new ZookeeperEnvironmentRepository(persistence, publisher);
		
		publisher.addSubscriber(retval,  new EnvironmentEvent.Builder(ZookeeperEnvironmentEventType.HOST_APPEARED,null).build());
		return retval;
	}
	
	private ZookeeperEnvironmentRepository(ZookeeperPersistence persistence,
			EventPublisher publisher) {
		this.persistence = persistence;
		this.publisher = publisher;
	}

	private String environmentsStr = "environments";
	private String hostsKey = "hosts";
	private boolean isListening = false;

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
				Composite hostComposite;
				try {
					hostComposite = persistence.retrieve(key);
				} catch (ZookeeperPersistenceException e) {
					Host node = new Host(nodeWithoutRole);
					env.addHost(node);
					continue;
				}
				Host node = new Host(nodeWithoutRole, hostComposite.getValue());
				Composite domainname = hostComposite.getChild(key
						+ "/domainname");
				if (domainname != null) {
					node.setDomainname(domainname.getValue());
				}
				env.addHost(node);
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
					Composite hostComposite;
					try {
						hostComposite = persistence.retrieve(key);
					} catch (ZookeeperPersistenceException e) {
						Host node = new Host(hostName);
						env.addHost(node);
						continue;
					}
					if (env.containsHostNamed(hostName)) {
						Host node = env.getHostByName(hostName);
						node.addRole(role);
						Composite domainname = hostComposite.getChild(key
								+ "/domainname");
						if (domainname != null) {
							node.setDomainname(domainname.getValue());
						}
					} else {
						Host node = new Host(hostName,
								hostComposite.getValue(), role);
						env.addHost(node);
						Composite domainname = hostComposite.getChild(key
								+ "/domainname");
						if (domainname != null) {
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
		for (Host node : env.getHosts()) {
			if (node.getRoles().size() == 0) {
				if (hostsWithoutRole == "") {
					hostsWithoutRole = node.getHostname() + "." + node.getDomainname();
				} else {
					hostsWithoutRole += ", " + node.getHostname() + "." + node.getDomainname();
				}
			} else {
				for (Role role : node.getRoles()) {
					if (rolesToHostNames.containsKey(role.getName())) {
						String hostsToThatRole = rolesToHostNames.get(role
								.getName())
								+ SEPERATOR + node.getHostname() + "." + node.getDomainname();
						rolesToHostNames.put(role.getName(), hostsToThatRole);
					} else {
						rolesToHostNames
								.put(role.getName(), node.getHostname() + "." + node.getDomainname());

					}

				}
			}
			if (node.getIp() != null) {
				Composite host = new Composite(hostsKey + "/"
						+ node.getHostname(), node.getIp());
				try {
					persistence.save(host);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (node.getDomainname() != null) {
					Composite domainname = new Composite(hostsKey + "/"
							+ node.getHostname() + "/domainname", node
							.getDomainname());
					try {
						persistence.save(domainname);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}
		Composite environment = new Composite(environmentsStr + "/"
				+ env.getName(), hostsWithoutRole);
		Composite roles = new Composite(environment.getKey() + "/roles", "");
		environment.addChild(roles);
		for (String role : rolesToHostNames.keySet()) {
			String hostnamesToRole = rolesToHostNames.get(role);
			Composite roleComposite = new Composite(
					roles.getKey() + "/" + role, hostnamesToRole);
			roles.addChild(roleComposite);
		}
		try {
			persistence.save(environment);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 private Map<String, BlockingEventStep> latches = new HashMap<String, BlockingEventStep>();
	private Map<String, Environment> environmentsToProvision = new HashMap<String, Environment>();

	// TODO more unit tests around this logic
	public synchronized void nodeAppears(Host appeared) {
		// logger.debug("appeared: " + appeared);

		for (Environment environment : environmentsToProvision.values()) {
			if (environment.containsHostNamed(appeared.getHostname())) {
				EnvironmentEvent hostAppearedEvent = new EnvironmentEvent.Builder(
						ZookeeperEnvironmentEventType.HOST_APPEARED,
						environment).withHost(appeared).build();
				if(!isListening){
					//TODO consider putting this blocw
				}
				publisher.publish(hostAppearedEvent);
			}
		}

	}

	public enum ZookeeperEnvironmentEventType implements EventType {
		BLOCK_UNTIL_ENVIRONMENT_PROVISIONED, HOST_APPEARED, ALL_HOSTS_APPEARED;
	}

	@Override
	public BlockingEventStep buildStepToBlockUntilProvisioned(Environment env) {

		EnvironmentEvent toSend = new EnvironmentEvent.Builder(
				ZookeeperEnvironmentEventType.BLOCK_UNTIL_ENVIRONMENT_PROVISIONED,
				env).build();
		EnvironmentEvent waitingFor = new EnvironmentEvent.Builder(
				ZookeeperEnvironmentEventType.ALL_HOSTS_APPEARED, env).build();

		BlockingEventStep blockingEventStep = BlockingEventStep.factory(
				publisher, toSend, waitingFor);

		environmentsToProvision.put(env.getName(), env);
		latches.put(env.getName(), blockingEventStep);
		//TODO suscpicous
		new HostWatcher(this, persistence);
		return blockingEventStep;
	}

	@Override
	public void handle(EnvironmentEvent appeared) {
		if (appeared.type == ZookeeperEnvironmentEventType.HOST_APPEARED) {
			for (String environmentName : environmentsToProvision.keySet()) {
				boolean hasRemaining = false;
				//TODO environment are already encapsulated, so this should not be necessary:
				Environment environment = environmentsToProvision
						.get(environmentName);
				for (Host node : environment.getHosts()) {
					if (node.getHostname().equals(appeared.getHost().getHostname())|| (node.getHostname() + "." + node.getDomainname()).equals(appeared.getHost().getHostname())) {
						logger.debug("provisioned: " + node);
						node.setProvisioned();
					} else {
						if (!node.isProvisioned()) {
							// logger.debug("not provisioned yet: " + node);
							hasRemaining = true;
						}
					}
				}

				if (!hasRemaining) {
					logger.debug("complete: " + environmentName);
					//BlockingEventStep latch = latches.get(environmentName);
					EnvironmentEvent done = new EnvironmentEvent.Builder(
							ZookeeperEnvironmentEventType.ALL_HOSTS_APPEARED, environment).build();
					publisher.publish(done);
					environmentsToProvision.remove(environmentName);
					latches.remove(environmentName);
					return;
				}
			}
		}
	}
}
