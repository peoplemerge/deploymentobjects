package org.deploymentobjects.core.infrastructure.configuration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.Node;
import org.deploymentobjects.core.domain.model.environment.Role;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.junit.Test;


public class PuppetTest {

	private ConfigurationManagement puppet = new Puppet(new Node("localhost"));

	@Test
	public void testWriteHostsFile() throws Exception {

		URL expectedUrl = this.getClass().getClassLoader().getResource(
				"expected-hosts.pp");
		File expectedPp = new File(expectedUrl.getFile());
		String expected = FileUtils.readFileToString(expectedPp);

		Puppet puppet = new Puppet(new Node("puppetmaster1", "peoplemerge.com",
				"192.168.10.137"));
		/*
		 * File actualPp = File.createTempFile("test", "ks");
		 * actualPp.deleteOnExit(); String actual =
		 * FileUtils.readFileToString(actualPp);
		 */

		// TODO: abstract the puppetmaster too, remove it from template. It's in
		// the output from getHostsPp(...)
		Environment environment = new Environment("refactor5test");
		Role web = new Role("web");
		Role db = new Role("db");
		Node refactor5test1 = new Node("refactor5test1", "peoplemerge.com",
				"192.168.10.146", web);
		Node refactor5test2 = new Node("refactor5test2", "peoplemerge.com",
				"192.168.10.147", web);
		Node refactor5test3 = new Node("refactor5test3", "peoplemerge.com",
				"192.168.10.148", db);
		environment.addNode(refactor5test1);
		environment.addNode(refactor5test2);
		environment.addNode(refactor5test3);
		List<Environment> environments = new ArrayList<Environment>();
		environments.add(environment);
		String actual = puppet.getHostsPp(environments);
		assertEquals(expected, actual);

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

		Puppet puppet = new Puppet(new Node("puppetmaster1", "peoplemerge.com",
				"192.168.10.137"));
		/*
		 * File actualPp = File.createTempFile("test", "ks");
		 * actualPp.deleteOnExit(); String actual =
		 * FileUtils.readFileToString(actualPp);
		 */

		// TODO: abstract the puppetmaster too, remove it from template. It's in
		// the output from getHostsPp(...)
		Environment environment = new Environment("refactor5test");
		Role web = new Role("web");
		Role db = new Role("db");
		Node refactor5test1 = new Node("refactor5test1", "peoplemerge.com",
				"192.168.10.146", web);
		Node refactor5test2 = new Node("refactor5test2", "peoplemerge.com",
				"192.168.10.147", web);
		Node refactor5test3 = new Node("refactor5test3", "peoplemerge.com",
				"192.168.10.148", db);
		environment.addNode(refactor5test1);
		environment.addNode(refactor5test2);
		environment.addNode(refactor5test3);
		List<Environment> environments = new ArrayList<Environment>();
		environments.add(environment);
		String actual = puppet.getSitePp(environments);
		assertEquals(expected, actual);

	}

}
