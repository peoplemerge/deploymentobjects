package org.deploymentobjects.com;

import static org.junit.Assert.assertEquals;

import org.deploymentobjects.core.application.CreateEnvironmentCommand;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.environment.Dom0;
import org.deploymentobjects.core.domain.model.environment.Node;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.environment.Node.Type;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperEnvironmentRepository;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence;
import org.junit.Test;


public class CreateEnvironmentWithRolesIntegrationTest {

	@Test
	public void createClusterWithRoles() throws Exception {

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder(
				"mock1", new ZookeeperEnvironmentRepository(
						new ZookeeperPersistence("ino:2181"))).withNodes(1,
				Type.SMALL, new Dom0("root", "kowalski", new NfsMount()),
				new Role("standard"))
				.withConfigurationManagement(
						new Puppet(new Node("puppetmaster1", "peoplemerge.com",
								"192.168.10.137"))).withDispatch(
						new JschDispatch("root")).build();
		ExitCode exit = command.execute();
		assertEquals(ExitCode.SUCCESS, exit);

	}
}
