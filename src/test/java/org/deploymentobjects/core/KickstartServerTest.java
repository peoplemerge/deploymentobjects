package org.deploymentobjects.core;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.deploymentobjects.core.KickstartServer;
import org.deploymentobjects.core.Node;
import org.deploymentobjects.core.Puppet;
import org.deploymentobjects.core.Storage;
import org.junit.Test;


import static org.mockito.Mockito.*;

public class KickstartServerTest {

	@Test
	public void testWriteKickstartFile() throws Exception{
		String tempDir = new File(File.createTempFile("test", ".ks").getParent()).getAbsolutePath();
		Storage storage = mock(Storage.class);
		KickstartServer server = new KickstartServer(tempDir, storage, new Puppet(new Node("puppetmaster1.peoplemerge.com", "192.168.10.137")));
		String hostname = "kstest";
		Map<String, Object> vars = new HashMap<String,Object>();
		vars.put("hostname", hostname);
		
		server.writeKickstartFile(hostname);
		
	}

}
