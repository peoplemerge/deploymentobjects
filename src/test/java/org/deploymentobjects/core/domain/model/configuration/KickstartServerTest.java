package org.deploymentobjects.core.domain.model.configuration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartTemplateService;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.junit.Test;

public class KickstartServerTest {

	@Test
	public void testWriteKickstartFile() throws Exception{
		String tempDir = new File(File.createTempFile("test", ".ks").getParent()).getAbsolutePath();
		Storage storage = mock(Storage.class);
		KickstartTemplateService server = new KickstartTemplateService(tempDir, storage, new Puppet(new Host("puppetmaster1.peoplemerge.com", "192.168.10.137")));
		String hostname = "kstest";
		Host host = new Host(hostname);
		Map<String, Object> vars = new HashMap<String,Object>();
		vars.put("hostname", hostname);
		
		HostProvisioningEvent.Requested requested = new HostProvisioningEvent.Requested(host, HostProvisioningEvent.EventType.KICKSTART); 

		HostProvisioningEvent.Completed returned = server.writeKickstartFile(requested);
		FileUtils.
		assertTrue();
		assertTrue(returned.isSuccessful());
	}

}
