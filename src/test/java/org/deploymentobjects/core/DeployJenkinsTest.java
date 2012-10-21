package org.deploymentobjects.core;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.DeployApplicationCommand;
import org.deploymentobjects.core.Dispatchable;
import org.deploymentobjects.core.Environment;
import org.deploymentobjects.core.EnvironmentRepository;
import org.deploymentobjects.core.ExitCode;
import org.deploymentobjects.core.Node;
import org.deploymentobjects.core.Role;
import org.deploymentobjects.core.ScriptedCommand;
import org.deploymentobjects.core.Step;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;


public class DeployJenkinsTest {

	EnvironmentRepository repo = mock(EnvironmentRepository.class);
	Environment environment = mock(Environment.class);
	Dispatchable dispatch = mock(Dispatchable.class);
	String commands = "/bin/ls /bin/ls";

	@Test
	public void deployJenkins() throws Exception {

		when(repo.lookupByName("jenkins1env")).thenReturn(environment);
		Role role = new Role("standard");
		Node node = new Node("jenkins1env");
		node.addRole(role); // TODO consider refactoring transitive relationship
		when(environment.lookupRoleByName("standard")).thenReturn(role);
		DeployApplicationCommand cmd = new DeployApplicationCommand.Builder(
				"jenkins", "jenkins1env",repo)
				.addCommandOnNodesByRole(commands, "standard").withDispatch(
						dispatch).build();
		Step step = new Step(new ScriptedCommand(commands), role);
		when(dispatch.dispatch(eq(step))).thenReturn(ExitCode.SUCCESS);
		ExitCode exit = cmd.execute();
		assertEquals(ExitCode.SUCCESS, exit);
	}
}
