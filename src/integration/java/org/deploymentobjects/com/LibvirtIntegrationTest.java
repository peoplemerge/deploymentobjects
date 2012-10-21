package org.deploymentobjects.com;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.deploymentobjects.core.ControlsMachines;
import org.deploymentobjects.core.JschDispatch;
import org.deploymentobjects.core.LibvirtAdapter;
import org.deploymentobjects.core.Node;
import org.deploymentobjects.core.ScriptedCommand;
import org.deploymentobjects.core.Step;
import org.junit.Assert;
import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;

public class LibvirtIntegrationTest {

	ExecutorService executor = Executors.newSingleThreadExecutor();

	Logger jschLogger = new Logger() {
		public boolean isEnabled(int level) {
			return true;
		}

		public void log(int level, String message) {
			System.out.println(message);
		}
	};

	private Step executeRemote(String commandStr) {
		ScriptedCommand command = new ScriptedCommand(commandStr);
		Step step = new Step(command, node);
		try {
			jsch.dispatch(step);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return step;
	}

	Runnable starter = new Runnable() {
		public void run() {
			String commandStr = "/usr/bin/virsh start " + vm;
			executeRemote(commandStr);

		}

	};
	Runnable stopper = new Runnable() {
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // sleep 10 secs
			String commandStr = "/usr/bin/virsh destroy " + vm; // list the 'ls'
			// command.
			// Should
			executeRemote(commandStr);
		}
	};
	JschDispatch jsch;
	String host = "ino";
	String vm = "test40";
	Node node;

	public LibvirtIntegrationTest() {
		JSch.setLogger(jschLogger);
		String username = System.getProperty("user.name");
		jsch = new JschDispatch(username);
		node = new Node(host);
	}

	@Test
	public void testConnection() throws Exception {
		ControlsMachines lv = new LibvirtAdapter(
				"qemu+ssh://ino/system?socket=/var/run/libvirt/libvirt-sock");
		executor.execute(starter);
		boolean retval = lv.pollForDomainToStart(vm, 1000, 60000);
		Assert.assertTrue(retval == true);
		executor.execute(stopper);

		retval = lv.pollForDomainToStop(vm, 1000, 60000);
		Assert.assertTrue(retval == true);

	}

	@Test
	public void startStop() throws Exception {
		String commandStr = "/usr/bin/virsh list";
		Step step; 
		step = executeRemote(commandStr);
		Assert.assertTrue(!step.getOutput().contains(vm));
		ControlsMachines lv = new LibvirtAdapter(
				"qemu+ssh://ino/system?socket=/var/run/libvirt/libvirt-sock");
		boolean retval = lv.startHost(vm);
		Assert.assertTrue(retval == true);
		step = executeRemote(commandStr);
		Assert.assertTrue(step.getOutput().contains(vm));
		System.out.println("Stopped VM");
		retval = lv.stopHost(vm);
		Assert.assertTrue(retval == true);
		step = executeRemote(commandStr);
		Assert.assertTrue(!step.getOutput().contains(vm));
	}

}
