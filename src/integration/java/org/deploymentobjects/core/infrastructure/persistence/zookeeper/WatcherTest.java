package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.junit.Test;

public class WatcherTest {

	HostWatcher.HostAppears callback = mock(HostWatcher.HostAppears.class);

	@Test
	public void testProcess() throws Exception {
		// TODO when the watcher is called, that means there is a new node that
		// has been created. We need to get it's IP back to
		// CreateEnvironmentCommand
		ZookeeperPersistence repo = new ZookeeperPersistence("localhost:2181");
		new HostWatcher(callback, repo);
		String host = "watchertest".intern();
		String ip = "192.168.10.111".intern();
		Composite composite = new Composite("hosts/" + host, ip);
		Host node = new Host(composite);

		try {
			repo.save(composite);
			Thread.sleep(2000);
			verify(callback).nodeAppears(eq(node));
		} finally {
			repo.delete(composite);
		}
	}
}
