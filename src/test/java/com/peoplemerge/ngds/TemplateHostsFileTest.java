package com.peoplemerge.ngds;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;


public class TemplateHostsFileTest {

	@Ignore
	@Test
	public void createHostsUsingOneEnvironment() throws Exception{
		Environment environment = new Environment();
		File actual = File.createTempFile("hosts", "");
		TemplateHostsFile hostsFile = new TemplateHostsFile(actual);
		hostsFile.addAll(environment);
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("192.168.0.2 testhost\n", contents);
	}
	

	@Ignore
	@Test
	public void createHostsUsingAllEnvironmentsInRepository() throws Exception{
		ResourceStateRepository repo = new YamlRepository();
		File actual = File.createTempFile("hosts", "");
		TemplateHostsFile hostsFile = new TemplateHostsFile(actual);
		hostsFile.addAll(repo);
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("192.168.0.10 host1env1\n192.168.0.11 host2env1\n192.168.0.20 host1env2\n", contents);		
	}
	
	@Ignore
	@Test
	public void includeStaticEntry(){
		ResourceStateRepository repo = new YamlRepository();
		TemplateHostsFile hostsFile = new TemplateHostsFile();
		//Write HostsFileTest?
		String contents = hostsFile.toString();
		Assert.assertEquals("127.0.0.1 localhost\n192.168.0.10 host1env1\n", contents);		
	}
	
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
	

	

	
}
