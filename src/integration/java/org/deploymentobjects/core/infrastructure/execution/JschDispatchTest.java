package org.deploymentobjects.core.infrastructure.execution;
import junit.framework.Assert;

import org.deploymentobjects.core.application.ScriptedCommand;
import org.deploymentobjects.core.domain.model.environment.Node;
import org.deploymentobjects.core.domain.model.environment.Node.Type;
import org.deploymentobjects.core.domain.model.execution.Step;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch;
import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;

public class JschDispatchTest {

	private static String host = "localhost";

	@Test
	public void listDirectory() throws Exception {
		Logger jschLogger = new Logger() {
			public boolean isEnabled(int level) {
				return true;
			}

			public void log(int level, String message) {
				System.out.println(message);
			}
		};
		JSch.setLogger(jschLogger);
		String username = System.getProperty("user.name");
		JschDispatch jsch = new JschDispatch(username);
		String commandStr = "/bin/ls /bin/ls"; // list the 'ls' command. Should
		// work on every *nix AFAIK
		Node node = new Node(host);
		ScriptedCommand command = new ScriptedCommand(commandStr);
		Step step = new Step(command, node);
		jsch.dispatch(step);
		Assert.assertEquals("/bin/ls\n", step.getOutput());
	}
}
