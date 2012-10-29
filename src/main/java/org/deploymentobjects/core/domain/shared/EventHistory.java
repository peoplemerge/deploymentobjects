package org.deploymentobjects.core.domain.shared;

import java.util.ArrayList;
import java.util.List;

public class EventHistory {
	public List<DomainEvent<?>> events = new ArrayList<DomainEvent<?>>();

	public String toString() {
		String retval = "\n";
		for (DomainEvent<?> event : events) {
			retval += event.toString() + "\n";
		}
		return retval;
	}
}
