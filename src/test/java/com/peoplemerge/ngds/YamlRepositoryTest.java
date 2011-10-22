package com.peoplemerge.ngds;
import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;


public class YamlRepositoryTest {

	
	YamlRepository repo = new YamlRepository();
	
	@Test
	public void saveString() throws Exception{
		repo.save("key", "value");
		Assert.assertEquals("{key: value}\n",repo.toString());
	}
	
	@Test 
	public void retrieveString() throws Exception{
		repo.setContents("{key: value}\n");
		String value = (String) repo.retrieve("key");
		Assert.assertEquals("value",value);
		
	}
	
	@Test 
	public void savesPersist() throws Exception{
		File file = File.createTempFile("yamlpersistenttestcase", "yaml");
		file.deleteOnExit();
		String filename = file.getPath();
		repo = new YamlRepository(filename);
		repo.save("key", "value");
		String contents = FileUtils.readFileToString(file);
		Assert.assertEquals("{key: value}\n", contents);
	}
	
	
	
}
