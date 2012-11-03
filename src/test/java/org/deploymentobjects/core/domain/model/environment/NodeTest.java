package org.deploymentobjects.core.domain.model.environment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;


public class NodeTest {

	@Test
	public void isEquals(){
		Host first = new Host("first", "127.0.0.1");
		Host copy = new Host("first", "127.0.0.1");
		assertEquals(first, copy);
	}
	
	@Test
	public void isEqualsRole(){
		
		Host first = new Host("first", "127.0.0.1", new Role("role1"));
		Host copy = new Host("first", "127.0.0.1", new Role("role1"));
		assertEquals(first, copy);
	}

	@Test
	public void notEqualsRole(){
		
		Host first = new Host("first", "127.0.0.1", new Role("role1"));
		Host copy = new Host("first", "127.0.0.1", new Role("role2"));
		assertFalse(first.equals(copy));
	}
}
