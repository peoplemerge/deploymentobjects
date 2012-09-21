package com.peoplemerge.ngds;

import static org.mockito.Mockito.*;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class ZookeeperIntegrationTest {

	// assume local zookeeper is running on port 2181 - TODO process to launch the zk server
	// connect to local zookeeper repo
	public ZookeeperIntegrationTest() throws IOException{
		repo = new ZookeeperRepository("ino:2181");
	}
	
	ZookeeperRepository repo;
	
	@Test 
	public void testDataSaved(){
		// TODO consider if key/value is sufficient
		// create an node with path /testing/simple with data "sample data"
		String expected = "sample data";
		String key = "simple";
		repo.save(key, expected);
		String actual = (String) repo.retrieve(key);
		Assert.assertEquals(expected, actual);
		// delete the node
		repo.delete(key);
	}

	class IsWatch extends ArgumentMatcher<WatchedEvent> {
		
		    public boolean matches(Object list) {
		
		        return true;
		
		    }
		
		}

	@Test
	public void testWatcherCalled() throws InterruptedException, KeeperException{
		// create an node with path /testing/watch with data "before"
		String before = "before";
		String key = "watchertest";
		Watcher observer = mock(Watcher.class);
		//Stat stat = repo.getZookeeper().exists("/ngds" + "/" + key, observer);

		repo.save(key, before);
		// add a watcher to the node
		
		repo.watch(key, observer);
		// modify the node's data
		String after = "after";
		repo.save(key, after);
		// delete the node
		repo.delete(key);
		Thread.sleep(2000);
		// verify the watcher has been called
		verify(observer).process(argThat(new IsWatch()));
	}
	
	
	
	
}
