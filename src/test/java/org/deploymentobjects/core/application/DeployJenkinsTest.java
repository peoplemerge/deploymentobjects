package org.deploymentobjects.core.application;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.application.DeployApplicationCommand;
import org.deploymentobjects.core.application.ScriptedCommand;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Step;
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
		Host node = new Host("jenkins1env");
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
