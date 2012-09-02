package com.peoplemerge.ngds;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.peoplemerge.ngds.Node.Type;

public class CreateEnvironmentIntegrationTest {

	@Test
	public void createTestcluster() throws Exception {
		File tempRepo = File.createTempFile("repo", "yaml");
		//tempRepo.deleteOnExit();
		System.out.println("Should create repo to: " + tempRepo.getAbsolutePath());

		CreateEnvironmentCommand command = new CreateEnvironmentCommand.Builder("testclus", new YamlRepository(tempRepo.getAbsolutePath()))
				.withNodes(2, Type.SMALL, new Dom0("xen2", new NfsMount())).build();
		ExitCode exit = command.execute();
		assertEquals(ExitCode.SUCCESS, exit);
	}
}
