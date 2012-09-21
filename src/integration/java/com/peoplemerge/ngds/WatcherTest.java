package com.peoplemerge.ngds;

import static org.mockito.Mockito.mock;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class WatcherTest {


	HostWatcher.NodeAppears callback = mock(HostWatcher.NodeAppears.class);

	
	@Test
	public void testProcess() throws Exception{
		// TODO when the watcher is called, that means there is a new node that
		// has been created.  We need to get it's IP back to CreateEnvironmentCommand
		
		ZookeeperRepository repo = new ZookeeperRepository("localhost:2181");
		HostWatcher watcher = new HostWatcher(callback, repo.getZookeeper());
		
		
	}
}
