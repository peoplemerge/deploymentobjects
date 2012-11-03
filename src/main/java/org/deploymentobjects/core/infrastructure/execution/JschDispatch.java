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
package org.deploymentobjects.core.infrastructure.execution;

import java.io.InputStream;

import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.execution.DispatchEvent;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.DomainEvent.EventType;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JschDispatch implements Dispatchable {

	private JSch jsch = new JSch();
	private Session session;
	private String userName;
	private EventPublisher publisher;

	public JschDispatch(EventPublisher publisher, String userName) {
		this.userName = userName;
		this.publisher = publisher;
	}

	public enum DispatchEventType implements EventType {
		JSCH_DISPATCH_REQUESTED,JSCH_DISPATCH_INTERRUPTED,JSCH_DISPATCH_HOST_COMPLETED, JSCH_DISPATCH_FAILED, JSCH_DISPATCH_NONZERO_EXIT, JSCH_DISPATCH_ALL_HOSTS_COMPLETED;
	}
	
	@Override
	public void dispatch(DispatchEvent event) {
	 String allOutput = "";

		for (Host node : event.target.getHosts()) {
			
			try{
			
			session = jsch.getSession(userName, node.getHostname());
			jsch.addIdentity(System.getProperty("user.home") + "/.ssh/id_rsa");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(event.getContents());
			InputStream in = channel.getInputStream();
			
			InputStream ext = channel.getExtInputStream();
			

			channel.connect();
			byte[] tmp = new byte[1024];
			String output = "";
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					output += new String(tmp, 0, i);
				}
				// TODO output and error streams are being combined.
				while (ext.available() > 0) {
					int i = ext.read(tmp, 0, 1024);
					if (i < 0)
						break;
					output += new String(tmp, 0, i);
				}
				// TODO This exit code should probably be better handled.  Think about how...
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					
					if (channel.getExitStatus() != 0) {
						publisher.publish(DispatchEvent.fromEvent(event, 
								DispatchEventType.JSCH_DISPATCH_NONZERO_EXIT).addOutput(output));
					}
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException ee) {
					// TODO - think HARD what to do here
					//
					// Will probably need access to something that lets us notify of interruption
					// for example, return DomainEvent instead?
//					retval.setSuccessful(false);
	//				System.out.println(ee.toString());
					publisher.publish(DispatchEvent.fromEvent(event, 
							DispatchEventType.JSCH_DISPATCH_INTERRUPTED).addOutput(output));
					break;
				}
			}
			channel.disconnect();
			session.disconnect();
			publisher.publish(DispatchEvent.fromEvent(event, 
					DispatchEventType.JSCH_DISPATCH_HOST_COMPLETED).addOutput(output));
			allOutput += output;
			}catch(Exception e){
				publisher.publish(DispatchEvent.fromEvent(event, 
						DispatchEventType.JSCH_DISPATCH_FAILED).addOutput(e.toString()));
			}
			
		}
		publisher.publish(DispatchEvent.fromEvent(event, 
				DispatchEventType.JSCH_DISPATCH_ALL_HOSTS_COMPLETED).addOutput(allOutput));
	}




}
