package org.deploymentobjects.core.application;

import static org.junit.Assert.assertEquals;

import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Hypervisor;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.environment.Host.Type;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperEnvironmentRepository;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence;
import org.junit.Test;


public class CreateEnvironmentIntegrationTest {

	@Test
	public void createTestcluster() throws Exception {


		EventStore eventStore = new InMemoryEventStore();
		EventPublisher publisher = new EventPublisher(eventStore);
		EnvironmentRepository repo =  ZookeeperEnvironmentRepository.factory(
				new ZookeeperPersistence("ino:2181"), publisher);
		Dispatchable dispatch = new JschDispatch(publisher, "root");
		ConfigurationManagement configMgt = new Puppet(publisher, new Host("puppetmaster1", "peoplemerge.com",
		"192.168.0.7"), dispatch);

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder(
				"puppet2env", "peoplemerge.com", repo, publisher).withEventStore(eventStore).withNodes(1,
						Type.SMALL, new Hypervisor.Builder(publisher, "kowalski", new NfsMount("192.168.0.4", "/media"), dispatch).withUserName("root").build()).withConfigurationManagement(
						configMgt).withDispatch(
					dispatch).build();
		Job saga = command.create();
		System.out.println("saga: " + saga);
		ExitCode exit = saga.execute();
		assertEquals(ExitCode.SUCCESS, exit);
		System.out.println("event store: " + eventStore);
		System.out.println("repo: " + repo);
		
	
	}

}
