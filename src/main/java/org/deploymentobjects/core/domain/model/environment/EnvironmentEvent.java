package org.deploymentobjects.core.domain.model.environment;

import org.deploymentobjects.core.domain.shared.DomainEvent;

public class EnvironmentEvent extends DomainEvent<EnvironmentEvent> {
	public final Environment environment;

	public final EventType type;
	private Host host;
	private String command;

	private EnvironmentEvent(EventType type, Environment environment) {
		this.environment = environment;
		this.type = type;
	}

	public String getCommand() {
		return command;
	}

	public Host getHost() {
		return host;
	}

	public static class Builder {
		private EnvironmentEvent event;

		public Builder(EventType type, Environment environment) {
			event = new EnvironmentEvent(type, environment);
		}

		public Builder withHost(Host host) {
			event.host = host;
			return this;
		}

		public Builder withCommand(String command) {
			event.command = command;
			return this;
		}

		public EnvironmentEvent build() {
			return event;
		}
	}

	public boolean sameEventAs(EnvironmentEvent event) {
		if (event.environment.equals(environment) && event.type == type) {
			if (event.host != null && host != null) {
				return event.host.equals(host);
			}
			return true;
		}
		return false;
	}

	public String toString() {
		String hostStr = (host == null) ? "" : " host " + host.getHostname();
		String commandStr = (command == null) ? "" : " command " + command;
		return this.getClass().getSimpleName() + " " + type + hostStr
				+ commandStr + " " + environment + " " + occurred;
	}

	public boolean equals(Object o){
		if(o instanceof EnvironmentEvent){
			EnvironmentEvent event = (EnvironmentEvent) o;
			return sameEventAs(event);
		}
		return false;
	}
}
