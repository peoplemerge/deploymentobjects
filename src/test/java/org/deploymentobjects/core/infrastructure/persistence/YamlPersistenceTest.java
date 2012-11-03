package org.deploymentobjects.core.infrastructure.persistence;
/************************************************************************
** 
** Copyright (C) 2011 Dave Thomas, PeopleMerge.
** All rights reserved.
** Contact: opensource@peoplemerge.com.
**
** This file is part of the NGDS language.
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**    http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
**  
** Other Uses
** Alternatively, this file may be used in accordance with the terms and
** conditions contained in a signed written agreement between you and the 
** copyright owner.
************************************************************************/
import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;



public class YamlPersistenceTest {

	
	YamlPersistence persistence = new YamlPersistence();
	
	@Test
	public void saveString() throws Exception{
		persistence.save(new Composite("key", "value"));
		Assert.assertEquals("{key: value}\n",persistence.toString());
	}
	
	@Test 
	public void retrieveString() throws Exception{
		persistence.setContents("{key: value}\n");
		Composite value = persistence.retrieve("key");
		Assert.assertEquals("value",value.getValue());
		
	}
	
	@Test 
	public void savesPersist() throws Exception{
		File file = File.createTempFile("yamlpersistenttestcase", ".yaml");
		file.deleteOnExit();
		String filename = file.getPath();
		persistence = new YamlPersistence(filename);
		persistence.save(new Composite("key", "value"));
		String contents = FileUtils.readFileToString(file);
		Assert.assertEquals("{key: value}\n", contents);
	}
	
	
	
}
