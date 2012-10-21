/************************************************************************
 ** 
 ** All rights reserved.
 ** Copyright (C) 2011 Dave Thomas, PeopleMerge.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.deploymentobjects.core.domain.model.environment.provisioning.ProvisioningEvent;
import org.deploymentobjects.core.domain.model.execution.AcceptsCommands;
import org.deploymentobjects.core.domain.shared.Entity;
import org.deploymentobjects.core.infrastructure.persistence.Composite;

public class Node implements AcceptsCommands, Entity<Node> {

	public enum Type {
		SMALL, LARGE, DATABASE;
		public static Type getSmall() {
			return SMALL;
		}
	}

	private Type type;

	private String hostname;

	private String domainname;

	private List<ProvisioningEvent> changes;
	
	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	private NodePool source;

	private Boolean isProvisioned;

	private String ip = null;

	private List<Role> roles = new LinkedList<Role>();

	/**
	 * TODO IP may change over time Ip is nullable to allow for provisioning
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Type getType() {
		return type;
	}

	public NodePool getSource() {
		return source;
	}

	public Node(String hostname) {
		this.hostname = hostname;
	}

	public Node(String hostname, String ip, Role... roles) {
		this.hostname = hostname;
		this.ip = ip;
		isProvisioned = true;
		addRoles(roles);
	}

	public Node(String hostname, String domainname, String ip, Role... roles) {
		this.hostname = hostname;
		this.domainname = domainname;
		this.ip = ip;
		isProvisioned = true;
		addRoles(roles);
	}

	// TODO use builder or factory when constructing nodes
	// This constructor makes sense when provisioning Nodes
	public Node(String hostname, Type type, NodePool source) {
		this.hostname = hostname;
		this.type = type;
		this.source = source;
	}

	// This constructor makes sense when provisioning Nodes
	public Node(String hostname, String domainname, Type type, NodePool pool,
			Role... roles) {
		this.hostname = hostname;
		this.domainname = domainname;
		this.type = type;
		this.source = pool;
		this.roles.addAll(Arrays.asList(roles));
	}

	// This constructor makes sense when using provisioned Nodes
	public Node(String hostname, String ip, Type type, NodePool source) {
		this.hostname = hostname;
		this.ip = ip;
		this.type = type;
		this.source = source;
		isProvisioned = true;
	}

	public Node(Composite composite) {
		// TODO make this a factory, so composite.getKey throws some obvious
		// exceptions
		this.hostname = composite.getKey().replace("hosts/", "");
		this.ip = composite.getValue();
		isProvisioned = true;

	}

	public String getHostname() {
		return hostname;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("hostname",
				hostname).append("domainname", domainname).append("ip", ip)
				.append("type", type).append("provisioned", isProvisioned);
		for (Role role : roles) {
			builder.append("role", role.getName());
		}
		return builder.toString();
	}

	/*
	 * @Override public Job accept(Executable command) { // TODO Auto-generated
	 * method stub return null; }
	 */
	// TODO This is here simply because Environment.getNodes makes sense which
	// shares the interface AcceptsCommands. Think about ISP, break up
	// AcceptsCommands?
	public List<Node> getNodes() {
		List<Node> asList = new ArrayList<Node>();
		asList.add(this);
		return asList;
	}

	// TODO consider a user-definable set of statuses rather than just
	// provisioned. Not adding that now because we want to just do the simplest
	// thing that can possibly work!
	public void setProvisioned() {
		isProvisioned = true;
	}

	// TODO clean this up
	public boolean isProvisioned() {
		if (isProvisioned == null) {
			return false;
		}
		return isProvisioned;
	}

	public void addRole(Role role) {
		roles.add(role);
		role.addNode(this);
	}

	public void addRoles(Role... roles) {
		for (Role role : roles) {
			addRole(role);
		}
	}

	public List<Role> getRoles() {
		return roles;
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
		Node rhs = (Node) obj;
		// TODO invesigate why it fails when .appendSuper(super.equals(obj))
		EqualsBuilder builder = new EqualsBuilder().append(hostname,
				rhs.hostname);
		if (!(ip == null && rhs.ip == null)) {
			builder.append(ip, rhs.ip);
		}
		if (!(type == null && rhs.type == null)) {
			builder.append(type, rhs.type);
		}
		if (!(source == null && rhs.source == null)) {
			builder.append(source, rhs.source);
		}
		if (!(isProvisioned == null && rhs.isProvisioned == null)) {
			builder.append(isProvisioned, rhs.isProvisioned);
		}
		if (!(domainname == null && rhs.domainname == null)) {
			builder.append(domainname, rhs.domainname);
		}
		builder.append(roles, rhs.roles);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1217, 52345).append(type).append(hostname)
				.append(source).append(ip).append(domainname).append(
						isProvisioned).toHashCode();
	}

	@Override
	public boolean sameIdentityAs(Node other) {
		// TODO Auto-generated method stub
		return false;
	}

}
