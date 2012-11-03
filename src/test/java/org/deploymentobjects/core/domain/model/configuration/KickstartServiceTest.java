package org.deploymentobjects.core.domain.model.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartTemplateService;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.junit.Test;

public class KickstartServiceTest {

	@Test
	public void testWriteKickstartFile() throws Exception {
		EventStore eventStore = new InMemoryEventStore();
		EventPublisher publisher = new EventPublisher(eventStore);
		Dispatchable dispatch = mock(Dispatchable.class);

		String tempDir = new File(File.createTempFile("test", ".ks")
				.getParent()).getAbsolutePath();
		Storage storage = new NfsMount();
		String hostname = "test1";
		Host host = new Host(hostname, "peoplemerge.com","192.168.10.101");
		Environment environment = new Environment("test");
		
		environment.add(host);
		KickstartTemplateService server = KickstartTemplateService.factory(
				publisher, environment, tempDir, storage, new Puppet(publisher,
						new Host("puppetmaster1", "peoplemerge.com",
								"192.168.10.137"), dispatch));
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("hostname", hostname);
		BlockingEventStep buildStepFor = server.buildStepFor(environment, host);
		ExitCode returned = buildStepFor.execute();
		String out = FileUtils.readFileToString(new File(tempDir + '/'
				+ hostname + ".ks"));

		URL expectedUrl = this.getClass().getClassLoader().getResource(
				"expected-test1.ks");
		File expectedPp = new File(expectedUrl.getFile());
		String expected = FileUtils.readFileToString(expectedPp);

		assertEquals(expected, out);
		assertTrue(returned.isSuccessful());
	}

}
