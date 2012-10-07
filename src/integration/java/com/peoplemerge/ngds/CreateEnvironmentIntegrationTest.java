package com.peoplemerge.ngds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.peoplemerge.ngds.Node.Type;

public class CreateEnvironmentIntegrationTest {

	@Test
	public void createTestcluster() throws Exception {

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder(
				"refactor5test", new ZookeeperEnvironmentRepository(
						new ZookeeperPersistence("ino:2181"))).withNodes(3,
				Type.SMALL, new Dom0("root", "kowalski", new NfsMount()))
				.withDispatch(new JschDispatch("root")).build();
		ExitCode exit = command.execute();
		assertEquals(ExitCode.SUCCESS, exit);

	}

}
