package org.deploymentobjects.core.application;

import static org.mockito.Mockito.mock;

import java.io.File;

import org.deploymentobjects.core.domain.model.configuration.NamingService;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.HostPool;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartTemplateService;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.slf4j.Logger;


public class MockBuilder {
	
	EnvironmentRepository repo = mock(EnvironmentRepository.class);
	Dispatchable dispatch = mock(Dispatchable.class);
	HostPool pool = mock(HostPool.class);
	NamingService namingService = mock(NamingService.class);
	Logger logger = mock(Logger.class);
	EventStore eventStore = mock(EventStore.class);

	public CreateEnvironmentCommand.Builder builder(int numNodes) throws Exception {
		File tempFile = File.createTempFile("test", "ks");
		tempFile.deleteOnExit();
		String tempDir = new File(tempFile.getParent()).getAbsolutePath();
		KickstartTemplateService kickstartServer = new KickstartTemplateService(tempDir,
				new NfsMount(),new Puppet(new Host("puppetmaster1", "peoplemerge.com", "192.168.10.137")));

		
		CreateEnvironmentCommand.Builder createCommandBuilder = new CreateEnvironmentCommand.Builder(
				"test", repo);
		createCommandBuilder.withNodes(numNodes, Host.Type.SMALL, pool);
		createCommandBuilder.withDispatch(dispatch);
		createCommandBuilder.withKickstartServer(kickstartServer);
		createCommandBuilder.withNamingService(namingService);
		createCommandBuilder.withEventStore(eventStore);
		return createCommandBuilder;
	}

}
