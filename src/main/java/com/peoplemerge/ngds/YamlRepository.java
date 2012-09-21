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
package com.peoplemerge.ngds;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public class YamlRepository implements ResourceStateRepository {

	private Yaml yaml = new Yaml();
	private String contents = "";
	private File yamlFile;
	
	public YamlRepository(){
		contents = yaml.dump(new HashMap());
	}

	public YamlRepository(String location) throws IOException{
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

	public String retrieve(String key) throws IOException {
		if(yamlFile != null){
			contents = FileUtils.readFileToString(yamlFile);
		}
		Map map = (Map) yaml.load(contents);
		return  map.get(key).toString();
	}

	@Override
	public synchronized void save(String key, String element) throws IOException {
		if(yamlFile != null){
			contents = FileUtils.readFileToString(yamlFile);
		}
		Map map = (Map) yaml.load(contents);
		map.put(key, element);
		contents = yaml.dump(map);
		if(yamlFile != null){
			FileUtils.writeStringToFile(yamlFile, contents);
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
