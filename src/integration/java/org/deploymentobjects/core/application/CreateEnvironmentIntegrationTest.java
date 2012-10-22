package org.deploymentobjects.core.application;

import static org.junit.Assert.assertEquals;

import org.deploymentobjects.core.application.CreateEnvironmentCommand;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.environment.Hypervisor;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Host.Type;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperEnvironmentRepository;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence;
import org.junit.Test;


public class CreateEnvironmentIntegrationTest {

	@Test
	public void createTestcluster() throws Exception {

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder(
				"puppet1env", new ZookeeperEnvironmentRepository(
						new ZookeeperPersistence("ino:2181"))).withNodes(1,
				Type.SMALL, new Hypervisor("root", "kowalski", new NfsMount()))
				.withConfigurationManagement(
						new Puppet(new Host("puppetmaster1", "peoplemerge.com",
								"192.168.10.137"))).withDispatch(
						new JschDispatch("root")).build();
		ExitCode exit = command.execute();
		assertEquals(ExitCode.SUCCESS, exit);

	}

}
