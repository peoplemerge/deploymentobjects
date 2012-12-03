/************************************************************************
 ** 
 ** Copyright (C) 2011 Dave Thomas, PeopleMerge.
 ** All rights reserved.
 ** Contact: opensource@peoplemerge.com.
 **
 ** This file is part of the NGDS language.
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **    http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 **  
 ** Other Uses
 ** Alternatively, this file may be used in accordance with the terms and
 ** conditions contained in a signed written agreement between you and the 
 ** copyright owner.
 ************************************************************************/
package org.deploymentobjects.core.infrastructure.persistence.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.Persistence;

public class ZookeeperPersistence implements Persistence, Watcher {

	private ZooKeeper zk;
	private String rootZnode = "/ngds";
	private boolean recurse = true;
	public class ZookeeperPersistenceException extends PersistenceException{
		public ZookeeperPersistenceException(Exception e){
			super(e);
		}
	}

	/*
	 * no good reason to enable these methods yet 
	 * 
	 * 
	 * public boolean isRecursive() {
	 * return recurse; }
	 * 
	 * public void setRecursive(boolean recurse) { this.recurse = recurse; }
	 */

	public ZooKeeper getZookeeper() {
		return zk;
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO handle connection/disconnection
		System.err.println(event.toString());
	}

	public ZookeeperPersistence(String zookeeperConnectString)
			throws IOException {
		zk = new ZooKeeper(zookeeperConnectString, 3000, this);
	}

	public Composite retrieve(String key) throws ZookeeperPersistenceException{
		Stat stat = null;
		String path = rootZnode + "/" + key;
		try {
			byte[] bytearray = zk.getData(path, false, stat);
			String data = new String(bytearray);
			Composite retval = new Composite(key, data);
			if (recurse) {
				List<String> children = zk.getChildren(path, false, stat);
				for (String childname : children) {
					Composite child = retrieve(key + "/" + childname);
					retval.addChild(child);
				}
			}
			return retval;
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		}
		//return null;
	}

	public void save(Composite element) throws ZookeeperPersistenceException {
		try {
			Stat stat = zk.exists(rootZnode + "/" + element.getKey(), false);
			if (stat == null) {
				zk.create(rootZnode + "/" + element.getKey(), element
						.getValue().getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			} else {
				int version = stat.getVersion();
				zk.setData(rootZnode + "/" + element.getKey(), element
						.getValue().getBytes(), version);
			}
			if (recurse) {
				for (Composite child : element.getChildren()) {
					save(child);
				}
			}
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		}

	}

	public void watchData(String key, Watcher observer) throws ZookeeperPersistenceException {
		try {/*
			 * zk.create(rootZnode + "/" +
			 * key,"".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			 */
			byte[] dataBytes = zk
					.getData(rootZnode + "/" + key, observer, null);
			String data = new String(dataBytes);
			System.err.println(data);
		} catch (KeeperException e) {
			// TODO Here we are swallowing the exceptions. FIXME!
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		}

	}

	public List<String> watchChildren(Composite composite, Watcher observer) throws ZookeeperPersistenceException {
		try {/*
			 * zk.create(rootZnode + "/" +
			 * key,"".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			 */
			return zk.getChildren(rootZnode + "/" + composite.getKey(),
					observer);
		} catch (KeeperException e) {
			// TODO Here we are swallowing the exceptions. FIXME!
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		}
		//return new ArrayList<String>();

	}

	public void delete(Composite toDelete) throws ZookeeperPersistenceException {
		try {
			if (recurse) {
				// annihilate all ancestors
				for (Composite child : toDelete.getChildren()) {
					delete(child);
				}
			} else {
				// TODO throw exception if there are any children
			}
			zk.delete(rootZnode + "/" + toDelete.getKey(), -1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new ZookeeperPersistenceException(e));
		}
	}

}
