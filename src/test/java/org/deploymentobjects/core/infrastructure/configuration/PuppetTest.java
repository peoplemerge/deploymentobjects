package org.deploymentobjects.core.infrastructure.configuration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.execution.DispatchEvent;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.execution.JschDispatch.DispatchEventType;
import org.junit.Test;


public class PuppetTest {


	private EventStore store = mock(EventStore.class);
	private Dispatchable dispatchable = mock(Dispatchable.class);
	private EventPublisher publisher = new EventPublisher(store);
	private EnvironmentRepository repo = mock(EnvironmentRepository.class);


	@Test
	public void testWriteHostsFile() throws Exception {

		URL expectedUrl = this.getClass().getClassLoader().getResource(
				"expected-hosts.pp");
		File expectedPp = new File(expectedUrl.getFile());
		String expected = FileUtils.readFileToString(expectedPp);

		Puppet puppet = new Puppet(publisher, new Host("puppetmaster1", "peoplemerge.com",
				"192.168.0.7"), dispatchable);


		// TODO: abstract the puppetmaster too, remove it from template. It's in
		// the output from getHostsPp(...)
		Environment environment = buildEnvironment();
		List<Environment> environments = new ArrayList<Environment>();
		environments.add(environment);
		String actual = puppet.getHostsPp(environments);
		assertEquals(expected, actual);

	}

	private Environment buildEnvironment() {
		Environment environment = new Environment("refactor5test");
		Role web = new Role("web");
		Role db = new Role("db");
		Host refactor5test1 = new Host("refactor5test1", "peoplemerge.com",
				"192.168.0.146", web);
		Host refactor5test2 = new Host("refactor5test2", "peoplemerge.com",
				"192.168.0.147", web);
		Host refactor5test3 = new Host("refactor5test3", "peoplemerge.com",
				"192.168.0.148", db);
		environment.addHost(refactor5test1);
		environment.addHost(refactor5test2);
		environment.addHost(refactor5test3);
		return environment;
	}

	@Test
	public void testApplyRoleToNodeByClass() throws Exception {

		// Push puppet manifests to nfs.
		// On puppetmaster, pull updated manifests from nfs.
		// On all client nodes, run puppet client to apply updates.

		URL expectedUrl = this.getClass().getClassLoader().getResource(
				"expected-site.pp");
		File expectedPp = new File(expectedUrl.getFile());
		String expected = FileUtils.readFileToString(expectedPp);

		Puppet puppet = new Puppet(publisher, new Host("puppetmaster1", "peoplemerge.com",
				"192.168.0.6"), dispatchable);

		Environment environment = buildEnvironment();
		List<Environment> environments = new ArrayList<Environment>();
		environments.add(environment);
		String actual = puppet.getSitePp(environments);
		assertEquals(expected, actual);

	}
	@Test
	public void createStep() throws Exception{
		Puppet puppet = new Puppet(publisher, new Host("puppetmaster1", "peoplemerge.com",
		"192.168.0.6"), dispatchable);

		File hostsPp = File.createTempFile("hosts", "pp");
		puppet.hostsPpFile = hostsPp;
		File sitePp = File.createTempFile("site", "pp");
		puppet.sitePpFile = sitePp;
		
		//don't look at what's in the environment because hosts don't have their IPs yet...
		Executable create = puppet.newEnvironment(repo);
		verify(repo, never()).getAll();

		List<Environment> all = new ArrayList<Environment>();
		all.add(buildEnvironment());

		//...do it on execute() instead - that should call repo.getAll()
		when(repo.getAll()).thenReturn(all);		
		ExitCode exitCode = create.execute();
		verify(repo, times(1)).getAll();
		
		
		assertEquals(ExitCode.SUCCESS, exitCode);
		String hostsPpOut = FileUtils.readFileToString(hostsPp);
		String sitePpOut = FileUtils.readFileToString(sitePp);
		
		// Do a quick sanity check that it wrote out the expected files
		assertTrue(hostsPpOut.contains("host {'refactor5test1.peoplemerge.com':"));
		assertTrue(sitePpOut.contains("node 'refactor5test1.peoplemerge.com'"));
		
		// Also verify that it tried to ssh out to do something.
		verify(dispatchable, times(1)).dispatch(any(DispatchEvent.class));
		
	}

}
