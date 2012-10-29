package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import org.deploymentobjects.core.domain.shared.DomainEvent;
import org.deploymentobjects.core.domain.shared.EventHistory;
import org.deploymentobjects.core.domain.shared.EventStore;

public class ZookeeperEventRepository  extends ZookeeperRepository implements EventStore{

	@Override
	public EventHistory lookup(String jobName) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void store(DomainEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

}
