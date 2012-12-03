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
package org.deploymentobjects.core.infrastructure.persistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public class YamlPersistence implements Persistence{

	private class YamlPersistenceException extends PersistenceException{
		public YamlPersistenceException(Exception e){
			super(e);
		}
	}
	
	private Yaml yaml = new Yaml();
	private String contents = "";
	private File yamlFile;
	
	public YamlPersistence(){
		contents = yaml.dump(new HashMap());
	}

	public YamlPersistence(String location) throws IOException{
		yamlFile = new File(location);
		if(!yamlFile.exists()){
			yamlFile.createNewFile();
		}
		//check file is writeable and nondir
		if(FileUtils.sizeOf(yamlFile) == 0L){
			contents = yaml.dump(new HashMap());
			FileUtils.writeStringToFile(yamlFile, contents);
		}
	}

	public Composite retrieve(String key) throws YamlPersistenceException  {
		if(yamlFile != null){
			try {
				contents = FileUtils.readFileToString(yamlFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new YamlPersistenceException(e);
			}
		}
		Map map = (Map) yaml.load(contents);
		String data =  map.get(key).toString();
		Composite retval = new Composite(key, data);
		return retval;
	}

	public synchronized void save(Composite composite) throws YamlPersistenceException {
		if(yamlFile != null){
			try {
				contents = FileUtils.readFileToString(yamlFile);
			} catch (IOException e) {
				throw new YamlPersistenceException(e);
			}
		}
		Map map = (Map) yaml.load(contents);
		map.put(composite.getKey(), composite.getValue());
		contents = yaml.dump(map);
		if(yamlFile != null){
			try {
				FileUtils.writeStringToFile(yamlFile, contents);
			} catch (IOException e) {
				throw new YamlPersistenceException(e);
			}
		}

	}
	
	//intended for testing only
	void setContents(String contents){
		this.contents = contents;
	}
	
	public String toString(){
		return contents;
	}

}
