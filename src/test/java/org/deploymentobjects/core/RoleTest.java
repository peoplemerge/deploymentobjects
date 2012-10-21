package org.deploymentobjects.core;

import static org.junit.Assert.*;

import org.deploymentobjects.core.Role;
import org.junit.Test;


public class RoleTest {

	@Test
	public void isEquals(){
		Role first = new Role("first");
		Role copy = new Role("first");
		assertEquals(first, copy);
	}

	@Test
	public void notEquals(){
		Role first = new Role("first");
		Role copy = new Role("other");
		assertFalse(first.equals(copy));
	}

}
