package org.deploymentobjects.core.domain.model.configuration;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.deploymentobjects.core.domain.model.configuration.Storage;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartServer;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.junit.Test;


import static org.mockito.Mockito.*;

public class KickstartServerTest {

	@Test
	public void testWriteKickstartFile() throws Exception{
		String tempDir = new File(File.createTempFile("test", ".ks").getParent()).getAbsolutePath();
		Storage storage = mock(Storage.class);
		KickstartServer server = new KickstartServer(tempDir, storage, new Puppet(new Host("puppetmaster1.peoplemerge.com", "192.168.10.137")));
		String hostname = "kstest";
		Map<String, Object> vars = new HashMap<String,Object>();
		vars.put("hostname", hostname);
		
		server.writeKickstartFile(hostname);
		
	}

}
