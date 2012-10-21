package org.deploymentobjects.core;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.deploymentobjects.core.Node;
import org.deploymentobjects.core.Role;
import org.junit.Test;


public class NodeTest {

	@Test
	public void isEquals(){
		Node first = new Node("first", "127.0.0.1");
		Node copy = new Node("first", "127.0.0.1");
		assertEquals(first, copy);
	}
	
	@Test
	public void isEqualsRole(){
		
		Node first = new Node("first", "127.0.0.1", new Role("role1"));
		Node copy = new Node("first", "127.0.0.1", new Role("role1"));
		assertEquals(first, copy);
	}

	@Test
	public void notEqualsRole(){
		
		Node first = new Node("first", "127.0.0.1", new Role("role1"));
		Node copy = new Node("first", "127.0.0.1", new Role("role2"));
		assertFalse(first.equals(copy));
	}
}
