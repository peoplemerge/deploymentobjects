package com.peoplemerge.ngds;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NodeTest {

	@Test
	public void isEquals(){
		Node first = new Node("first", "127.0.0.1");
		Node copy = new Node("first", "127.0.0.1");
		assertEquals(first, copy);
	}
}
