package org.deploymentobjects.com;

import static org.junit.Assert.assertEquals;

import org.deploymentobjects.core.CreateEnvironmentCommand;
import org.deploymentobjects.core.Dom0;
import org.deploymentobjects.core.ExitCode;
import org.deploymentobjects.core.JschDispatch;
import org.deploymentobjects.core.NfsMount;
import org.deploymentobjects.core.Node;
import org.deploymentobjects.core.Puppet;
import org.deploymentobjects.core.Role;
import org.deploymentobjects.core.ZookeeperEnvironmentRepository;
import org.deploymentobjects.core.ZookeeperPersistence;
import org.deploymentobjects.core.Node.Type;
import org.junit.Test;


public class CreateEnvironmentWithRolesIntegrationTest {

	@Test
	public void createClusterWithRoles() throws Exception {

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder(
				"jenkins7env", new ZookeeperEnvironmentRepository(
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
