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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEnvironmentCommand implements Executable, HostWatcher.NodeAppears {

	private List<Node> nodes = new ArrayList<Node>();
	private String name;
	private ResourceStateRepository repo;
	private Dispatchable dispatchable;
	private KickstartServer kickstartServer;
	private Storage storage;
	private NamingService namingService;
	
	private CreateEnvironmentCommand(){}
	public static class Builder {

		//"Create a new environment called development using 1 small nodes from dom0."
		CreateEnvironmentCommand command = new CreateEnvironmentCommand();

		public Builder(String environmentName, ResourceStateRepository repo){
			command.name = environmentName;
			command.repo = repo;
		}
		
		public Builder withNodes(int quantity, Node.Type type, NodePool pool){
			for(int i = 1; i <= quantity; i++){
				Node node = new Node(command.name + i, type, pool);
				command.nodes.add(node);
			}
			return this;
		}
		
		public Builder withDispatch(Dispatchable d){
			command.dispatchable = d;
			return this;
		}
		
		public Builder withKickstartServer(KickstartServer kickstartServer){
			command.kickstartServer = kickstartServer;
			return this;
		}


		public Builder withNamingService(NamingService namingService) {
			command.namingService = namingService;
			return this;
		}

		public CreateEnvironmentCommand build(){
			if(command.dispatchable == null){
				command.dispatchable = new JschDispatch(System.getProperty("user.name"));
			}
			if(command.kickstartServer == null){
				command.kickstartServer = new KickstartServer("/mnt/media/software/kickstart", new NfsMount());
			}
			if(command.namingService == null){
				command.namingService = new TemplateHostsFile();
			}
			return command;
		}
		
	}
	
	@Override
	public ExitCode execute(){
		for (Node node : nodes){
			Map<String, Object> vars = new HashMap<String,Object>();
			try {
				kickstartServer.writeKickstartFile(node.getHostname());
			} catch (Exception e) {
				return ExitCode.FAILURE;
			}
		}
		for (Node node : nodes){
			Step step = node.getSource().createStep(node.getType(), node.getHostname());
			try {
				dispatchable.dispatch(step);
			} catch (Exception e) {
				//TODO fail-fast behavior, alternatives would be desirable for users. 
				// Consider rollback
				// Add finer controls than "failure"
				return ExitCode.FAILURE;
			}
		}
		String nodeNames = "";
		for(int i = 0; i < nodes.size(); i++){
			if (i != 0){
				nodeNames += ",";
			}
			nodeNames += nodes.get(i);
		}
		try {
			repo.save("environments." + name, nodeNames);
		} catch (Exception e) {
			return ExitCode.FAILURE;
		}

		return ExitCode.SUCCESS;
		
	}
	
	@Override
	public synchronized void nodeAppears(String host, String ip){
		boolean hasRemaining = false;
		for(Node node: nodes){
			if(node.getHostname().equals(host)){
				namingService.add(host, ip);
				node.setProvisioned();
			}else{
				if(!node.isProvisioned()){
					hasRemaining = true;
				}
			}
		}
		if(!hasRemaining){
			namingService.commit();
		}
	}

}
