package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.deploymentobjects.core.domain.model.environment.Node;
import org.deploymentobjects.core.infrastructure.persistence.Composite;

public class HostWatcher implements Watcher{

	public interface NodeAppears{
		/**
		 * Callback to calling method
		 * @param hostname
		 * @param ip
		 */
		public void nodeAppears(Node appeared);

	}
	
	private NodeAppears callback;
	private ZookeeperPersistence zk;
	
	public HostWatcher(NodeAppears callback, ZookeeperPersistence zk){
		this.callback = callback;
		this.zk = zk;
		watchHosts();
	}
	private List<String> watchHosts(){
		return zk.watchChildren(new Composite("hosts", ""), this);
	}
	
	List<String> previousHosts = new ArrayList<String>();
	
	@Override
	public synchronized void process(WatchedEvent event) {
		watchHosts();
		List<String> allChildren = watchHosts();
		for(String host : allChildren){
			if(!previousHosts.contains(host)){
				previousHosts.add(host);
				String path = "hosts/" + host;
				Composite composite = zk.retrieve(path);
				Node equivalent = new Node(composite);
				callback.nodeAppears(equivalent);
			}
		}
	}

}
