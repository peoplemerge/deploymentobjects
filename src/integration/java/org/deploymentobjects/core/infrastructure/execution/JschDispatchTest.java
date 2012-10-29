package org.deploymentobjects.core.infrastructure.execution;


import java.util.Date;

import junit.framework.Assert;

import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.Script;
import org.deploymentobjects.core.domain.model.execution.DispatchEvent.Completed;
import org.deploymentobjects.core.domain.model.execution.DispatchEvent.Requested;
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
		// this should work if the user has set a private key to "ssh localhost"
		// without a password.
		String username = System.getProperty("user.name");
		JschDispatch jsch = new JschDispatch(username);
		String commandStr = "/bin/ls /bin/ls"; // list the 'ls' command. Should
		// work on every *nix AFAIK
		Host node = new Host(host);
		Script command = new Script(commandStr); 
		Requested requested = new Requested(new Date(), jsch, command, node);
		Completed completed = jsch.dispatch(requested);
		Assert.assertEquals(true, completed.isSuccessful());
		Assert.assertEquals("/bin/ls\n", completed.getOutput());
	}
	
	//TODO test nonzero retval and failure conditions
	
}
