package com.peoplemerge.ngds;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;


public class HostsFileNamingTest {

	@Ignore
	@Test
	public void createHostsUsingOneEnvironment(){
		Environment environment = new Environment();
		HostsFile hostsFile = new HostsFile(environment);
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("192.168.0.2 testhost\n", contents);
	}
	

	@Ignore
	@Test
	public void createHostsUsingAllEnvironmentsInRepository(){
		ResourceStateRepository repo = new YamlRepository();
		HostsFile hostsFile = new HostsFile(repo);
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("192.168.0.10 host1env1\n192.168.0.11 host2env1\n192.168.0.20 host1env2\n", contents);		
	}
	
	@Ignore
	@Test
	public void includeStaticEntry(){
		ResourceStateRepository repo = new YamlRepository();
		HostsFile hostsFile = new HostsFile(repo);
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("127.0.0.1 localhost\n192.168.0.10 host1env1\n", contents);		
	}
	
	//add hostsfile for major OSes
	

	
}
