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
package org.deploymentobjects.core.infrastructure.configuration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.Template;
import org.deploymentobjects.core.VelocityTemplate;
import org.deploymentobjects.core.domain.model.configuration.NamingService;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Node;

/**
 * Called templated hosts file because it just writes a simple template. This
 * implementation uses SortedSet which preserves the natural ordering of the
 * hostnames.
 * 
 * @author dave
 * 
 */

//TODO Replace this strategy with puppet mechanism
public class TemplateHostsFile implements NamingService {

	private Template template = new VelocityTemplate();
	private File hostsFile;
	private String templateFile = "templates/clients/hosts.tmpl";

	public void add(Environment e) {

	}

	public void update(EnvironmentRepository repo) {
		String allHosts = "";
		for (Environment env : repo.getAll()) {
			for (Node node : env.getNodes()) {
				allHosts += node.getIp() + " " + node.getHostname() + "\n";
			}
		}
		Map<String, Object> hostsString = new TreeMap<String, Object>();
		hostsString.put("dynamichosts", allHosts);
		String output = template.encode(templateFile, hostsString);
		try {
			FileUtils.writeStringToFile(hostsFile, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public TemplateHostsFile() {
		hostsFile = new File("/etc/hosts");
	}

	public TemplateHostsFile(File hostsFile) {
		this.hostsFile = hostsFile;
	}

	public TemplateHostsFile(File hostsFile, String templateToUse) {
		this.hostsFile = hostsFile;
		this.templateFile = templateToUse;
	}

}
