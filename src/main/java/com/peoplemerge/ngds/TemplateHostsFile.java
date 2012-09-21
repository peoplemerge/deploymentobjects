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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
/**
 * Called templated hosts file because it just writes a simple template.
 * This implementation uses SortedSet which preserves the natural ordering
 * of the hostnames.
 * @author dave
 *
 */
public class TemplateHostsFile implements NamingService {

	private Template template = new VelocityTemplate();
	private File hostsFile;
	private String templateFile = "templates/clients/hosts.tmpl";
	private List<String> values = new LinkedList<String>();
	
	public void addAll(ResourceStateRepository repo){}
	
	public void addAll(Environment env){}
	public TemplateHostsFile(){
		hostsFile = new File("/etc/hosts");
	}
	
	public TemplateHostsFile(File hostsFile){
		this.hostsFile = hostsFile;
	}

	public TemplateHostsFile(File hostsFile, String templateToUse) {
		this.hostsFile = hostsFile;
		this.templateFile = templateToUse;
	}

	@Override
	public synchronized void add(String host, String ip) {
		values.add(ip + " " + host + "\n");
		
	}
	
	@Override
	public void commit() {
		String allHosts = "";
		for(String line : values){
			allHosts += line;
		}
		Map<String, Object> hostsString = new TreeMap<String,Object>();
		hostsString.put("dynamichosts", allHosts);
		String output = template.encode(templateFile, hostsString);
		try {
			FileUtils.writeStringToFile(hostsFile, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
