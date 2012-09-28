package com.peoplemerge.ngds;

import static org.mockito.Mockito.*;

import org.junit.Test;

public class WatcherTest {

	HostWatcher.NodeAppears callback = mock(HostWatcher.NodeAppears.class);

	@Test
	public void testProcess() throws Exception {
		// TODO when the watcher is called, that means there is a new node that
		// has been created. We need to get it's IP back to
		// CreateEnvironmentCommand
		ZookeeperRepository repo = new ZookeeperRepository("localhost:2181");
		HostWatcher watcher = new HostWatcher(callback, repo);
		String host = "watchertest".intern();
		String ip = "192.168.10.111".intern();
		try {
			repo.save("hosts/" + host, ip);
			Thread.sleep(2000);
			verify(callback).nodeAppears(eq(host), eq(ip));
		} finally {
			repo.delete("hosts/watchertest");
		}
	}
}
