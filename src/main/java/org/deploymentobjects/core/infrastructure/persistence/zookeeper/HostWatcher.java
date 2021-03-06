package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence.ZookeeperPersistenceException;

public class HostWatcher implements Watcher{

	public interface HostAppears{
		/**
		 * Callback to calling method
		 * @param hostname
		 * @param ip
		 */
		public void nodeAppears(Host appeared);

	}
	
	private HostAppears callback;
	private ZookeeperPersistence zk;
	
	public HostWatcher(HostAppears callback, ZookeeperPersistence zk){
		this.callback = callback;
		this.zk = zk;
		watchHosts();
	}
	private List<String> watchHosts(){
		try {
			return zk.watchChildren(new Composite("hosts", ""), this);
		} catch (ZookeeperPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<String>();
		}
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
				Composite composite;
				try {
					composite = zk.retrieve(path);
				} catch (ZookeeperPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				Host equivalent = new Host(composite);
				callback.nodeAppears(equivalent);
			}
		}
	}

}
