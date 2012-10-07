package com.peoplemerge.ngds;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;


public class TemplateHostsFileTest {

	
	/*
	@Test
	public void createHostsUsingOneEnvironment() throws Exception{
		Environment environment = new Environment("testhost");
		environment.addNode(new Node("testhost1", "192.16.0.2"));
		File actual = File.createTempFile("hosts", "");
		TemplateHostsFile hostsFile = new TemplateHostsFile(actual);
		hostsFile.update(environment);
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("192.168.0.2 testhost1\n", contents);
	}
	*/

	@Test
	public void createHostsUsingAllEnvironmentsInRepository() throws Exception{
		EnvironmentRepository repo = mock(EnvironmentRepository.class);
		File actual = File.createTempFile("hosts", "");
		TemplateHostsFile hostsFile = new TemplateHostsFile(actual, "test-hostsfile.tmpl");
		Environment env1 = new Environment("env1");
		env1.addNode(new Node("env1host1", "192.168.0.10"));
		env1.addNode(new Node("env1host2", "192.168.0.11"));
		Environment env2 = new Environment("env2");
		env2.addNode(new Node("env2host1", "192.168.0.20"));
	
		List<Environment> environments = new LinkedList<Environment>();
		environments.add(env1);
		environments.add(env2);
		
		when(repo.getAll()).thenReturn(environments);

		hostsFile.update(repo);
		//Write HostsFileTest?
		String contents = FileUtils.readFileToString(actual);
		Assert.assertEquals("192.168.0.10 env1host1\n192.168.0.11 env1host2\n192.168.0.20 env2host1\n", contents);		
	}
	
	@Ignore
	@Test
	public void includeStaticEntry(){
		Persistence repo = new YamlPersistence();
		TemplateHostsFile hostsFile = new TemplateHostsFile();
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("127.0.0.1 localhost\n192.168.0.10 host1env1\n", contents);		
	}
	
	/*
	@Test
	public void savesCreateCallsToFile() throws Exception{
		File actual = File.createTempFile("hosts", "");
		actual.deleteOnExit();
		TemplateHostsFile hostsFile = new TemplateHostsFile(actual, "test-hostsfile.tmpl");
		hostsFile.add("env1host1", "192.168.0.10");
		hostsFile.add("env1host2", "192.168.0.11");
		hostsFile.add("env2host1", "192.168.0.20");
		hostsFile.commit();
		//Write HostsFileTest?
		String contents = FileUtils.readFileToString(actual);

		Assert.assertEquals("192.168.0.10 env1host1\n192.168.0.11 env1host2\n192.168.0.20 env2host1\n", contents);		
	}
	*/

	

	
}
