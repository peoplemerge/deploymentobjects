package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import static org.mockito.Mockito.*;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class ZookeeperIntegrationTest {

	// assume local zookeeper is running on port 2181 - TODO process to launch
	// the zk server
	// connect to local zookeeper repo
	public ZookeeperIntegrationTest() throws IOException {
		repo = new ZookeeperPersistence("ino:2181");
	}

	ZookeeperPersistence repo;

	@Test
	public void testDataSaved() {
		// TODO consider if key/value is sufficient
		// create an node with path /testing/simple with data "sample data"
		String expected = "sample data";
		String key = "simple";
		Composite composite = new Composite(key, expected);
		repo.save(composite);
		Composite actual = repo.retrieve(key);
		Assert.assertEquals(expected, actual.getValue());
		// delete the node
		repo.delete(composite);
	}

	

	@Test
	public void testNestedDataSaved() {
		// TODO consider if key/value is sufficient
		// create an node with path /testing/simple with data "sample data"
		String expected = "sample data";
		String key = "parent";
		String childKey = "parent/child";
		Composite composite = new Composite(key, expected);
		Composite inner = new Composite(childKey, expected);
		composite.addChild(inner);
		
		repo.save(composite);
		Composite parent = repo.retrieve(key);
		Assert.assertEquals(expected, parent.getValue());
		Assert.assertEquals(key, parent.getKey());
		
		Composite child = parent.getChildren().get(0);
		Assert.assertEquals(childKey, child.getKey());
		Assert.assertEquals(expected, child.getValue());
		// delete the node
		//repo.delete(inner);
		repo.delete(composite);
	}
	class IsWatch extends ArgumentMatcher<WatchedEvent> {

		public boolean matches(Object list) {

			return true;

		}

	}

	@Test
	public void testWatcherCalledOnDataChange() throws InterruptedException,
			KeeperException {
		// create an node with path /testing/watch with data "before"
		String before = "before";
		String key = "watchertest";
		Composite composite = new Composite(key, before);
		Watcher observer = mock(Watcher.class);
		// Stat stat = repo.getZookeeper().exists("/ngds" + "/" + key,
		// observer);

		repo.save(composite);
		// add a watcher to the node

		repo.watchData(key, observer);
		// modify the node's data
		composite.setValue("after");
		repo.save(composite);
		Thread.sleep(2000);
		// verify the watcher has been called
		verify(observer).process(argThat(new IsWatch()));
		// delete the node
		repo.delete(composite);
	}

	@Test
	public void testWatcherCalledOnChildrenAddedAndSaved()
			throws InterruptedException, KeeperException {
		// create an node with path /testing/watch with data "before"

		String data = "before";
		Composite parent = new Composite("watchertest2", data);
		Composite child = new Composite("watchertest2/child", data);

		Watcher observer = mock(Watcher.class);
		// Stat stat = repo.getZookeeper().exists("/ngds" + "/" + key,
		// observer);

		repo.save(parent);

		// add a watcher to the node
		repo.watchChildren(parent, observer);

		parent.addChild(child);
		repo.save(child);
		Thread.sleep(2000);
		// verify the watcher has been called
		verify(observer).process(argThat(new IsWatch()));
		// delete the nodes
		//repo.delete(child);
		repo.delete(parent);
	}

	@Test
	public void testWatcherCalledOnChildrenAddedParentSaved()
			throws InterruptedException, KeeperException {
		// create an node with path /testing/watch with data "before"

		String data = "before";
		Composite parent = new Composite("watchertest2", data);
		Composite child = new Composite("watchertest2/child", data);

		Watcher observer = mock(Watcher.class);
		// Stat stat = repo.getZookeeper().exists("/ngds" + "/" + key,
		// observer);

		repo.save(parent);

		// add a watcher to the node
		repo.watchChildren(parent, observer);

		parent.addChild(child);
		repo.save(parent);
		Thread.sleep(2000);
		// verify the watcher has been called
		verify(observer).process(argThat(new IsWatch()));
		// delete the nodes
		//repo.delete(child);
		repo.delete(parent);
	}

}
