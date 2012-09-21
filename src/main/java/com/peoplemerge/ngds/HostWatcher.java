package com.peoplemerge.ngds;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class HostWatcher implements Watcher{

	public interface NodeAppears{
		/**
		 * Callback to calling method
		 * @param hostname
		 * @param ip
		 */
		public void nodeAppears(String host, String ip);

	}
	
	private NodeAppears callback;
	private ZooKeeper zk;
	
	public HostWatcher(NodeAppears callback, ZooKeeper zk){
		this.callback = callback;
		this.zk = zk;
	}
	
	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		
	}

}
