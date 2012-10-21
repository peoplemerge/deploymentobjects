package org.deploymentobjects.com;
import junit.framework.Assert;

import org.deploymentobjects.core.JschDispatch;
import org.deploymentobjects.core.Node;
import org.deploymentobjects.core.ScriptedCommand;
import org.deploymentobjects.core.Step;
import org.deploymentobjects.core.Node.Type;
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
