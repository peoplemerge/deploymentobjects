package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence.ZookeeperPersistenceException;

public class EventWatcher implements Watcher{

	public interface EventAppears{
		/**
		 * Callback to calling method
		 * @param hostname
		 * @param ip
		 */
		public void eventAppears(Composite appeared);

	}
	
	private EventAppears callback;
	private ZookeeperPersistence zk;
	private String jobName;
	
	public EventWatcher(EventAppears callback, ZookeeperPersistence zk, String jobName){
		this.callback = callback;
		this.zk = zk;
		this.jobName = jobName;
		List<String> existing = watchJob();
		previousEvents.addAll(existing);
	}
	private List<String> watchJob(){
		try {
			return zk.watchChildren(new Composite("jobs/" + jobName, ""), this);
		} catch (ZookeeperPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
	
	List<String> previousEvents = new ArrayList<String>();
	
	@Override
	public synchronized void process(WatchedEvent event) {
		System.out.println("EventWatcher got WatchedEvent: " + event);
		watchJob();
		List<String> allChildren = watchJob();
		for(String childEvent : allChildren){
			if(!previousEvents.contains(childEvent)){
				previousEvents.add(childEvent);
				String path = "jobs/" + jobName + "/" + childEvent;
				Composite composite;
				try {
					composite = zk.retrieve(path);
				} catch (ZookeeperPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				// TODO map DomainEvent from this Composite.  Serialization and deserialization needs to be done
				//Host equivalent = new Host(composite);
				callback.eventAppears(composite);
			}
		}
	}

}