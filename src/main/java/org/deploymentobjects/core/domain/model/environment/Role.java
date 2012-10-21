package org.deploymentobjects.core.domain.model.environment;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.deploymentobjects.core.domain.model.execution.AcceptsCommands;

public class Role implements AcceptsCommands {

	private final String name;

	private List<Node> nodes = new LinkedList<Node>();

	public List<Node> getNodes() {
		return nodes;
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public Role(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("nodes",nodes).toString();
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
		Role rhs = (Role) obj;
		// TODO invesigate why it fails when .appendSuper(super.equals(obj))
		return new EqualsBuilder().append(name, rhs.name).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(11349, 72341).append(name).toHashCode();
	}

}
