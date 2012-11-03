package org.deploymentobjects.core.infrastructure.execution;


import junit.framework.Assert;

import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.DispatchEvent;
import org.deploymentobjects.core.domain.model.execution.Script;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep.DispatchEventType;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
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
		EventStore eventStore = new InMemoryEventStore();
		EventPublisher publisher = new EventPublisher(eventStore);

		JschDispatch jsch = new JschDispatch(publisher, username);
		
		// list the 'ls' command. Should work on every *nix AFAIK
		String commandStr = "/bin/ls /bin/ls"; 

		Script command = new Script(commandStr); 
		DispatchEvent event = new DispatchEvent(DispatchEventType.DISPATCH_REQUESTED, jsch, command,
		new Host("localhost"));
		jsch.dispatch(event);
		
		System.out.println(eventStore);
		Assert.assertTrue(eventStore.toString().contains(
				JschDispatch.DispatchEventType.JSCH_DISPATCH_ALL_HOSTS_COMPLETED.toString()));
		
		// PROBLEM : how do we check the event history?  There's no Environment available to this event.
		
		/*
		Assert.assertEquals(true, completed.isSuccessful());
		Assert.assertEquals("/bin/ls\n", completed.getOutput());
		*/
	}
	
	//TODO test nonzero retval and failure conditions!
	
}
