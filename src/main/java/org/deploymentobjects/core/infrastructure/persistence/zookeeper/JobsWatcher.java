package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.EventWatcher.EventAppears;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence.ZookeeperPersistenceException;

public class JobsWatcher implements Watcher{

	public interface JobAppears{
		/**
		 * Callback to calling method
		 * @param hostname
		 * @param ip
		 */
		public void jobAppears(Composite appeared);

	}
	
	private JobAppears callback;
	private ZookeeperPersistence zk;
	
	public JobsWatcher(JobAppears callback, ZookeeperPersistence zk){
		this.callback = callback;
		this.zk = zk;
		List<String> existing = watchJob();
		System.out.println("existing jobs: " +existing);
		previousJobs.addAll(existing);
	}
	private List<String> watchJob(){
		try {
			return zk.watchChildren(new Composite("jobs", ""), this);
		} catch (ZookeeperPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
	
	private List<String> previousJobs = new ArrayList<String>();
	
	public List<String> getPreviousJobs() {
		return previousJobs;
	}
	@Override
	public synchronized void process(WatchedEvent event) {
		System.out.println("JobWatcher got WatchedEvent: " + event);
		watchJob();
		List<String> allChildren = watchJob();
		for(String childEvent : allChildren){
			if(!previousJobs.contains(childEvent)){
				previousJobs.add(childEvent);
				String path = "jobs" + "/" + childEvent;
				Composite composite;
				try {
					composite = zk.retrieve(path);
				} catch (ZookeeperPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				// TODO map JobEvent from this Composite.  Serialization and deserialization needs to be done
				//Job equivalent = new Job(composite);
				callback.jobAppears(composite);
			}
		}
	}

}
