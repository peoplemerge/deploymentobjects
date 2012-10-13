package com.peoplemerge.ngds;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Composite {

	private String key;
	private String value;
	private List<Composite> children = new LinkedList<Composite>();

	public Composite(String key, String value) {
		this.key = key;
		this.value = value;
	} 
	
	public Composite(String key, String value, List<Composite> children) {
		this.key = key;
		this.value = value;
		this.children = children;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<Composite> getChildren() {
		return children;
	}
	public void addChild(Composite child) {
		this.children.add(child);
	}
	
	@Override
	public String toString(){
		if(children == null || children.size() == 0){
			return key + ": " + value;
		}
		return  key + ": " + value + "... " + children;
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
		Composite rhs = (Composite) obj;
		EqualsBuilder builder = new EqualsBuilder() //TODO consider adding .appendSuper(super.equals(obj)) as in node
		.append(key, rhs.key)
		.append(value, rhs.value)
		.append(children,
				rhs.children);
		return builder.isEquals();
	}
	
	@Override
	   public int hashCode() {

	     return new HashCodeBuilder(274535, 128537).
	       append(key).
	       append(value).
	       append(children).
	       toHashCode();
	   }

	public Composite getChild(String toFind) {
		for(Composite child : children){
			if(child.getKey().equals(toFind)){
				return child;
			}
		}
		return null;
	}
	
}
