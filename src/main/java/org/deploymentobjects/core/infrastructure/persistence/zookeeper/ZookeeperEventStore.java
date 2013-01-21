package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.shared.DomainEvent;
import org.deploymentobjects.core.domain.shared.EventHistory;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.Composite;

public class ZookeeperEventStore extends ZookeeperRepository implements
		EventStore {

	private ZookeeperPersistence persistence;

	public ZookeeperEventStore(ZookeeperPersistence persistence) {
		this.persistence = persistence;
	}

	private Job job;

	// TODO Remove this hack, it's just here to get the UI working. All Events
	// should have factories and serializers.
	public class UnknownEvent extends DomainEvent<UnknownEvent> {
		private String id;
		private String toString;

		public UnknownEvent(String id, String toString) {
			this.id = id;
			this.toString = toString;
		}

		@Override
		public String getId() {
			return id;
		}

		public String toString() {
			return toString;
		}

		public boolean sameEventAs(UnknownEvent other) {
			return false;
		}
	}
	


	@Override
	public EventHistory lookup(String jobName) {
		EventHistory history = new EventHistory();
		//history.events.add(new UnknownEvent("dummy", "dummy"));
		System.out.println("jobname: " + jobName);
		String key = jobsKey + "/"+ jobName;
		Composite envComposite;
		try {
			envComposite = persistence.retrieve(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return history;
		}
		for (Composite composite : envComposite.getChildren()) {
			UnknownEvent event = new UnknownEvent(composite.getKey(), composite
					.getValue());
			history.events.add(event);
		}
		return history;
	}

	private String jobsKey = "jobs";

	@Override
	public void store(DomainEvent<?> event) {
		if (job == null) {
			System.out.println("Don't know what job to attache this event to: "
					+ event);
		} else {
			Composite eventComposite = new Composite(jobsKey + "/"
					+ job.getId() + "/" + event.getId(), event.toString());
			try {
				persistence.save(eventComposite);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// TODO this is a hack, need to fix it. The Events themselves should
	// encapsulate the job, environment, etc.
	public void setJob(Job notMyBestJob) {
		this.job = notMyBestJob;
		Composite eventComposite = new Composite(jobsKey + "/" + job.getId(),
				job.toString());
		try {
			persistence.save(eventComposite);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
