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

public class Dom0 extends Node implements NodePool {

	
	private ControlsMachines controller;
	private Storage storage;
	private String userAt = "";

	private synchronized ControlsMachines getController(){
		if(controller == null){
			controller = new LibvirtAdapter("qemu+ssh://"+ getHostname() +"/system?socket=/var/run/libvirt/libvirt-sock");
		}
		return controller;
	}
	
	public Dom0(String userName, String hostname, Storage storage) {
		super(hostname);
		userAt = userName + "@";
		this.storage = storage;
	}
	
	//TODO consider pushing up Storage constructor
	public Dom0(String hostname, Storage storage) {
		super(hostname);
		this.storage = storage;
	}

	public String getCreateCommand(){
		return "";
	}

	// TODO encapsulate better.  This domain class is anemic!
	@Override
	public Step createStep(Type type, String name) {
		//TODO Critical - this is a big hack hardcoding these commands here.  They should really come from the grammar.
		ScriptedCommand command = new ScriptedCommand("/mnt/media/software/kickstart/launch.sh " + name + " " + storage.getMountPoint());
		Step step  = new Step(command, this);
		return step;
	}
	// TODO need to rethink how this interface is used
	@Override
	public boolean pollForDomainToStart(String vm, int pollMs, int timeoutMs) {	
		return getController().pollForDomainToStart(vm, pollMs, timeoutMs);
	}

	@Override
	public boolean pollForDomainToStop(String vm, int pollMs, int timeoutMs) {
		return getController().pollForDomainToStop(vm, pollMs, timeoutMs);
	}

	@Override
	public boolean startHost(String vm) {
		return getController().startHost(vm);
	}

	@Override
	public boolean stopHost(String vm) {
		return getController().stopHost(vm);

	}
	
	@Override
	public String toString(){
		return userAt + super.toString();
	}
	
	
	
}
