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
package org.deploymentobjects.core.domain.model.environment;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.deploymentobjects.core.domain.shared.DomainEvent;
/**
 * Aggregate root
 * @author dave
 *
 */
public class Environment {

	private String name;

	private List<DomainEvent<?>> changes;

	public Environment(String name) {
		this.name = name;
	}

	private List<Host> hosts = new LinkedList<Host>();
	
	public Role lookupRoleByName(String roleName){
		for(Host node : hosts){
			for(Role role : node.getRoles()){
				if(role.getName().equals(roleName)){
					return role;
				}
			}
		}
		return null;
	}

	//TODO delete me
	public void add(Host host){
		addHost(host);
	}
	
	public void addHost(Host node) {
		hosts.add(node);
	}

	public List<Host> getHosts() {
		return hosts;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name + ": " + hosts;
	}
	
	public boolean containsHostNamed(String nodeName){
		for(Host node : hosts){
			if(node.getHostname().equals(nodeName)){
				return true;
			}
		}
		return false;
	}
	
	public Host getHostByName(String nodeName){
		for(Host node : hosts){
			if(node.getHostname().equals(nodeName)){
				return node;
			}
		}
		return null;
	}
	
	public void apply(DomainEvent<?> event){
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Environment rhs = (Environment) obj;
		EqualsBuilder builder = new EqualsBuilder(); // TODO consider adding
		// .appendSuper(super.equals(obj)) - similar
		// to Node.equals
		builder.append(name, rhs.name);
		builder.append(hosts, rhs.hosts);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder(3459, 14237).append(name).append(hosts)
				.toHashCode();
	}
}
