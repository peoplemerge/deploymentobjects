package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence.ZookeeperPersistenceException;
import org.junit.Ignore;
import org.junit.Test;

public class ZookeeperEnvironmentRepositoryTest {

	private ZookeeperPersistence mock = mock(ZookeeperPersistence.class);
	private EventStore store = mock(EventStore.class);
	private EventPublisher publisher = new EventPublisher(store);

	private EnvironmentRepository repo =  ZookeeperEnvironmentRepository.factory(
			mock, publisher);

	@Test
	public void testLookupByName() throws ZookeeperPersistenceException {
		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		Host expected = new Host(hostname, ip);
		expected.setIp(ip);
		Environment expectedEnv = new Environment(environmentName);
		expectedEnv.addHost(expected);

		Composite environment = new Composite(
				"environments/" + environmentName, hostname);
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		environment.addChild(roles);

		Composite host = new Composite("hosts/" + hostname, ip);
		String envkey = "environments/" + environmentName;
		when(mock.retrieve(envkey)).thenReturn(environment);
		String hostkey = "hosts/" + hostname;
		when(mock.retrieve(hostkey)).thenReturn(host);
		Environment actualEnv = repo.lookupByName(environmentName);
		assertEquals(actualEnv.getHosts().size(), 1);
		Host actual = actualEnv.getHosts().get(0);
		assertEquals(expected, actual);
		assertEquals(expectedEnv, actualEnv);
		// TODO verifyNoMoreInteractions(mock);

	}

	@Test
	public void testLookupWithDomainname() throws ZookeeperPersistenceException {
		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		String domainnameStr = "example.com";
		Host expected = new Host(hostname, ip);
		expected.setDomainname(domainnameStr);

		Environment expectedEnv = new Environment(environmentName);
		expectedEnv.addHost(expected);

		Composite environment = new Composite(
				"environments/" + environmentName, hostname);
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		environment.addChild(roles);

		Composite host = new Composite("hosts/" + hostname, ip);
		Composite domainName = new Composite("hosts/" + hostname
				+ "/domainname", domainnameStr);
		host.addChild(domainName);
		String envkey = "environments/" + environmentName;
		when(mock.retrieve(envkey)).thenReturn(environment);
		String hostkey = "hosts/" + hostname;
		when(mock.retrieve(hostkey)).thenReturn(host);
		Environment actualEnv = repo.lookupByName(environmentName);
		assertEquals(actualEnv.getHosts().size(), 1);
		Host actual = actualEnv.getHosts().get(0);
		assertEquals(expected, actual);
		assertEquals(expectedEnv, actualEnv);
		// TODO verifyNoMoreInteractions(mock);

	}

	@Test
	public void testLookupWithRole() throws ZookeeperPersistenceException {
		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		String roleName = "testrole";
		Host expected = new Host(hostname, ip, new Role(roleName));
		Environment expectedEnv = new Environment(environmentName);
		expectedEnv.addHost(expected);

		Composite host = new Composite("hosts/" + hostname, ip);

		Composite environment = new Composite(
				"environments/" + environmentName, "");
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		Composite testrole = new Composite("environments/" + environmentName
				+ "/roles/" + roleName, hostname);
		roles.addChild(testrole);
		environment.addChild(roles);

		String envkey = "environments/" + environmentName;
		when(mock.retrieve(envkey)).thenReturn(environment);

		String hostkey = "hosts/" + hostname;
		when(mock.retrieve(hostkey)).thenReturn(host);
		Environment actualEnv = repo.lookupByName(environmentName);
		assertEquals(actualEnv.getHosts().size(), 1);
		Host actual = actualEnv.getHosts().get(0);
		assertEquals(expected, actual);
		assertEquals(expectedEnv, actualEnv);
		// TODO verifyNoMoreInteractions(mock);
	}

	// TODO test for multiple roles

	@Test
	public void testLookupWithMultipleRoles() throws ZookeeperPersistenceException {
		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		String roleName = "testrole1";
		String roleName2 = "testrole2";
		Host expected = new Host(hostname, ip, new Role(roleName), new Role(
				roleName2));
		Environment expectedEnv = new Environment(environmentName);
		expectedEnv.addHost(expected);

		Composite environment = new Composite(
				"environments/" + environmentName, hostname);
		Composite host = new Composite("hosts/" + hostname, ip);
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		Composite testrole = new Composite("environments/" + environmentName
				+ "/roles/" + roleName, hostname);
		Composite testrole2 = new Composite("environments/" + environmentName
				+ "/roles/" + roleName2, hostname);
		roles.addChild(testrole);
		roles.addChild(testrole2);
		environment.addChild(roles);

		String envkey = "environments/" + environmentName;
		when(mock.retrieve(envkey)).thenReturn(environment);
		String hostkey = "hosts/" + hostname;
		when(mock.retrieve(hostkey)).thenReturn(host);
		Environment actualEnv = repo.lookupByName(environmentName);
		assertEquals(actualEnv.getHosts().size(), 1);
		Host actual = actualEnv.getHosts().get(0);
		assertEquals(expected, actual);
		assertEquals(expectedEnv, actualEnv);
		// TODO verifyNoMoreInteractions(mock);
	}

	@Test
	public void testGetAll() throws ZookeeperPersistenceException {
		String firstStr = "first";
		String first1Str = "first1";
		String first1ipStr = "192.168.0.11";
		String first2Str = "first2";
		String first2ipStr = "192.168.0.12";
		String secondStr = "second";
		String second1Str = "second1";
		String second1ipStr = "192.168.0.13";
		List<Environment> expected = new LinkedList<Environment>();
		{
			Environment first = new Environment(firstStr);
			Role firstRole = new Role(firstStr);
			Host first1 = new Host(first1Str, first1ipStr, firstRole);
			Host first2 = new Host(first2Str, first2ipStr, firstRole);
			first.addHost(first1);
			first.addHost(first2);

			Environment second = new Environment(secondStr);
			Role secondRole = new Role(secondStr);
			Host second1 = new Host(second1Str, second1ipStr, secondRole);
			second.addHost(second1);

			expected.add(first);
			expected.add(second);
		}
		// Composite root = new Composite("root","");
		Composite environments = new Composite("environments", "");
		{
			Composite first = new Composite("environments/" + firstStr, "");
			Composite firstRoles = new Composite("environments/" + firstStr
					+ "/roles", "");
			Composite firstRole = new Composite("environments/" + firstStr
					+ "/roles/" + firstStr, first1Str + ", " + first2Str);
			firstRoles.addChild(firstRole);
			first.addChild(firstRoles);

			Composite second = new Composite("environments/" + secondStr, "");
			Composite secondRoles = new Composite("environments/" + secondStr
					+ "/roles", "");
			Composite secondRole = new Composite("environments/" + secondStr
					+ "/roles/" + secondStr, second1Str);
			secondRoles.addChild(secondRole);
			second.addChild(secondRoles);

			environments.addChild(first);
			environments.addChild(second);
		}

		Composite first1Host = new Composite("hosts/" + first1Str, first1ipStr);
		Composite first2Host = new Composite("hosts/" + first2Str, first2ipStr);
		Composite second1Host = new Composite("hosts/" + second1Str,
				second1ipStr);

		when(mock.retrieve("environments")).thenReturn(environments);
		when(mock.retrieve("hosts/" + first1Str)).thenReturn(first1Host);
		when(mock.retrieve("hosts/" + first2Str)).thenReturn(first2Host);
		when(mock.retrieve("hosts/" + second1Str)).thenReturn(second1Host);

		List<Environment> actual = repo.getAll();
		assertEquals(expected, actual);
		// TODO verifyNoMoreInteractions(mock);

	}

	@Test
	public void testSave() throws ZookeeperPersistenceException {

		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";

		Host node = new Host(hostname, ip);
		Environment env = new Environment(environmentName);
		env.addHost(node);

		repo.save(env);

		
		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, hostname);
		Composite hasNoRoles = new Composite("environments/" + environmentName
				+ "/roles", "");
		expectedEnvironment.addChild(hasNoRoles);
		Composite expectedHost = new Composite("hosts/" + hostname, ip);

		verify(mock, times(1)).save(eq(expectedHost));
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verifyNoMoreInteractions(mock);

	}

	@Test
	public void testSaveWithRoles() throws ZookeeperPersistenceException {

		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		String roleName = "testenvrole";

		Host node = new Host(hostname, ip, new Role(roleName));
		Environment env = new Environment(environmentName);
		env.addHost(node);

		repo.save(env);


		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, "");
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		Composite role = new Composite("environments/" + environmentName
				+ "/roles/" + roleName, hostname);
		roles.addChild(role);
		expectedEnvironment.addChild(roles);
		Composite expectedHost = new Composite("hosts/" + hostname, ip);

		verify(mock, times(1)).save(eq(expectedHost));
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verifyNoMoreInteractions(mock);

	}

	@Test
	public void testSaveWithoutIp() throws ZookeeperPersistenceException {

		String environmentName = "testenv";
		String hostname = "testenv1";
		// In this test, node is different... no IP
		Host node = new Host(hostname);
		Environment env = new Environment(environmentName);
		env.addHost(node);

		repo.save(env);

		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, hostname);
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		expectedEnvironment.addChild(roles);
		Composite expectedHost = new Composite("hosts/" + hostname, "");

		// don't save a host without the IP
		verify(mock, never()).save(eq(expectedHost));
		// but do save the environment
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verifyNoMoreInteractions(mock);
	}

	@Test
	public void testSaveWithDomainName() throws ZookeeperPersistenceException {

		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.1.232";
		String domainname = "example.com";

		Host node = new Host(hostname, ip);
		node.setDomainname(domainname);
		Environment env = new Environment(environmentName);
		env.addHost(node);

		repo.save(env);

		
		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, hostname);
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		expectedEnvironment.addChild(roles);
		Composite expectedHost = new Composite("hosts/" + hostname, ip);
		Composite expectedDomainname = new Composite("hosts/" + hostname
				+ "/domainname", domainname);
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verify(mock, times(1)).save(eq(expectedHost));
		verify(mock, times(1)).save(eq(expectedDomainname));
		verifyNoMoreInteractions(mock);
	}

	@Test
	public void testDontSaveDomainnameWithoutIp() throws ZookeeperPersistenceException {

		String environmentName = "testenv";
		String hostname = "testenv1";
		String domainname = "example.com";

		// In this test, node is different... no IP
		Host node = new Host(hostname);
		node.setDomainname(domainname);
		Environment env = new Environment(environmentName);
		env.addHost(node);

		repo.save(env);

		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, hostname);
		Composite roles = new Composite("environments/" + environmentName
				+ "/roles", "");
		expectedEnvironment.addChild(roles);
		Composite expectedHost = new Composite("hosts/" + hostname, "");
		Composite expectedDomainname = new Composite("hosts/" + hostname
				+ "/domainname", domainname);

		// don't save a host without the IP
		verify(mock, never()).save(eq(expectedHost));
		// but do save the environment
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verify(mock, never()).save(eq(expectedDomainname));
		
		verifyNoMoreInteractions(mock);
	}

	
	static boolean isDone = false;

	@Test
	public void zookeeperNodeAppears() throws Exception {
		// When a node gets created on the dom0, after rebooting, it will
		// create the zookeeper nodes /ngds/hosts/hostname the ip of the
		// system that has just booted in the data of the node. We want a
		// watcher to be called allowing the processing to continue.

		// There needs to be a barrier implemented so as nodes are being
		// created, execution stops. Alternatively, we could have the
		// asynchronous notifications continue the execution. That would
		// increase coupling between the methods but mean this could run
		// single-threaded. I suspect it will make command execution failover
		// easier in the future.
		ExecutorService executor = Executors.newSingleThreadExecutor();
		final Environment env = new Environment("test");
		String nodeName1 = "test1";
		String nodeName2 = "test2";
		env.addHost(new Host(nodeName1));
		env.addHost(new Host(nodeName2));

		final Executable blockingStep = repo.buildStepToBlockUntilProvisioned(env);

		Runnable blockThread = new Runnable() {
			public void run() {
				blockingStep.execute();
				isDone = true;
			}

		};
		executor.execute(blockThread);
		Thread.sleep(500);

		String ip1 = "192.168.0.155";
		repo.nodeAppears(new Host(nodeName1, ip1));
		// The hosts file should be called but not yet committed
		Thread.sleep(1000);
		assertTrue(!isDone);

		String ip2 = "192.168.0.156";
		repo.nodeAppears(new Host(nodeName2, ip2));
		Thread.sleep(1000);
		assertTrue(isDone);

	}
	
	@Test
	@Ignore
	public void testDelete() {
		//TODO write this code
	}

}
