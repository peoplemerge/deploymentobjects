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
package com.peoplemerge.ngds;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JschDispatch implements Dispatchable {

	private JSch jsch = new JSch();
	private Session session;
	private String userName;

	public JschDispatch(String userName) {
		this.userName = userName;
	}

	public ExitCode dispatch(Step step) throws Exception {
		// Should use the runner, right?
		ExitCode retval = ExitCode.SUCCESS;
		for (Node node : step.getNodes()) {
			session = jsch.getSession(userName, node.getHostname());
			jsch.addIdentity(System.getProperty("user.home") + "/.ssh/id_rsa");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();
			Channel channel = session.openChannel("exec");
			// TODO toString? fix this interface.
			((ChannelExec) channel).setCommand(step.getCommand().toString());
			// TODO how to integration test?
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
				// TODO This exit code should probably be returned.
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					if (channel.getExitStatus() != 0) {
						retval = ExitCode.FAILURE;
					}
					break;
				}
				try {
					Thread.sleep(100);
				} catch (Exception ee) {
					// TODO - think HARD what to do here
					System.out.println(ee.toString());
					break;
				}
			}
			channel.disconnect();
			session.disconnect();
			if (step.getOutput() != null) {
				step.setOutput(step.getOutput() + "\n" + output);
			} else {
				step.setOutput(output);
			}
		}
		return retval;
	}

}
