import junit.framework.Assert;

import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;
import com.peoplemerge.ngds.JschDispatch;
import com.peoplemerge.ngds.Node;
import com.peoplemerge.ngds.ScriptedCommand;
import com.peoplemerge.ngds.Step;

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
