package com.peoplemerge.ngds;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;

public class ZookeeperEnvironmentRepositoryTest {

	private ZookeeperPersistence mock = mock(ZookeeperPersistence.class);
	private EnvironmentRepository repo = new ZookeeperEnvironmentRepository(
			mock);

	@Test
	public void testLookupByName() {
		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		Node expected = new Node(hostname, ip);
		expected.setIp(ip);
		Environment expectedEnv = new Environment(environmentName);
		expectedEnv.addNode(expected);

		Composite environment = new Composite(environmentName, hostname);
		Composite host = new Composite(hostname, ip);
		String envkey = "environments/" + environmentName;
		when(mock.retrieve(envkey)).thenReturn(environment);
		String hostkey = "hosts/" + hostname;
		when(mock.retrieve(hostkey)).thenReturn(host);
		Environment actualEnv = repo.lookupByName(environmentName);
		assertEquals(actualEnv.getNodes().size(), 1);
		Node actual = actualEnv.getNodes().get(0);
		assertEquals(expected, actual);
		assertEquals(expectedEnv, actualEnv);
		//TODO verifyNoMoreInteractions(mock);

	}

	@Test
	public void testGetAll() {
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
			Node first1 = new Node(first1Str, first1ipStr);
			Node first2 = new Node(first2Str, first2ipStr);
			first.addNode(first1);
			first.addNode(first2);

			Environment second = new Environment(secondStr);
			Node second1 = new Node(second1Str, second1ipStr);
			second.addNode(second1);

			expected.add(first);
			expected.add(second);
		}
		// Composite root = new Composite("root","");
		Composite environments = new Composite("environments", "");
		{
			Composite first = new Composite(firstStr, first1Str + ", "
					+ first2Str);
			Composite second = new Composite(secondStr, second1Str);
			environments.addChild(first);
			environments.addChild(second);
		}

		Composite first1Host = new Composite(first1Str, first1ipStr);
		Composite first2Host = new Composite(first2Str, first2ipStr);
		Composite second1Host = new Composite(second1Str, second1ipStr);

		when(mock.retrieve("environments")).thenReturn(environments);
		when(mock.retrieve("hosts/" + first1Str)).thenReturn(first1Host);
		when(mock.retrieve("hosts/" + first2Str)).thenReturn(first2Host);
		when(mock.retrieve("hosts/" + second1Str)).thenReturn(second1Host);

		List<Environment> actual = repo.getAll();
		assertEquals(expected, actual);
		//TODO verifyNoMoreInteractions(mock);

	}

	@Test
	public void testSave() {

		String environmentName = "testenv";
		String hostname = "testenv1";
		String ip = "192.168.0.5";
		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, hostname);
		Composite expectedHost = new Composite("hosts/" + hostname, ip);

		Node node = new Node(hostname, ip);
		Environment env = new Environment(environmentName);
		env.addNode(node);

		repo.save(env);

		verify(mock, times(1)).save(eq(expectedHost));
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verifyNoMoreInteractions(mock);

	}

	@Test
	public void testSaveWithoutIp() {

		String environmentName = "testenv";
		String hostname = "testenv1";
		Composite expectedEnvironment = new Composite("environments/"
				+ environmentName, hostname);
		Composite expectedHost = new Composite("hosts/" + hostname, "");

		// In this test, node is different... no IP
		Node node = new Node(hostname);
		Environment env = new Environment(environmentName);
		env.addNode(node);

		repo.save(env);
		
		// don't save a host without the IP
		verify(mock, never()).save(eq(expectedHost));
		// but do save the environment
		verify(mock, times(1)).save(eq(expectedEnvironment));
		verifyNoMoreInteractions(mock);
	}
	
	static boolean isDone = false;

	@Test
	public void zookeeperNodeAppears() throws Exception{
		// When a node gets created on the dom0, after rebooting, will
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
		env.addNode(new Node(nodeName1));
		env.addNode(new Node(nodeName2));
		
		Runnable blockThread = new Runnable() {
			public void run() {
				try {
					repo.blockUntilProvisioned(env);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				isDone = true;
			}

		};
		executor.execute(blockThread);
		Thread.sleep(500);

		String ip1 = "192.168.0.155";
		repo.nodeAppears(new Node(nodeName1, ip1));
		// The hosts file should be called but not yet committed
		Thread.sleep(1000);
		assertTrue(!isDone);
		
		String ip2 = "192.168.0.156";
		repo.nodeAppears(new Node(nodeName2, ip2));
		Thread.sleep(1000);
		assertTrue(isDone);

	}
	
	
	@Test
	@Ignore
	public void testDelete() {
	}

}
