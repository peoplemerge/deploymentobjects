package org.deploymentobjects.core.application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.junit.Test;



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
		DispatchableStep step = new DispatchableStep(new ScriptedCommand(commands), role);
		when(dispatch.dispatch(eq(step))).thenReturn(ExitCode.SUCCESS);
		ExitCode exit = cmd.execute();
		assertEquals(ExitCode.SUCCESS, exit);
	}
}
