package org.deploymentobjects.core.application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.deploymentobjects.core.domain.model.configuration.ConfigurationManagement;
import org.deploymentobjects.core.domain.model.configuration.NamingService;
import org.deploymentobjects.core.domain.model.configuration.NfsMount;
import org.deploymentobjects.core.domain.model.environment.Environment;
import org.deploymentobjects.core.domain.model.environment.EnvironmentEvent;
import org.deploymentobjects.core.domain.model.environment.EnvironmentRepository;
import org.deploymentobjects.core.domain.model.environment.Host;
import org.deploymentobjects.core.domain.model.environment.HostPool;
import org.deploymentobjects.core.domain.model.environment.provisioning.KickstartTemplateService;
import org.deploymentobjects.core.domain.model.execution.BlockingEventStep;
import org.deploymentobjects.core.domain.model.execution.DispatchEvent;
import org.deploymentobjects.core.domain.model.execution.Dispatchable;
import org.deploymentobjects.core.domain.model.execution.DispatchableStep;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.shared.EventPublisher;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.configuration.Puppet;
import org.deploymentobjects.core.infrastructure.persistence.InMemoryEventStore;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;


public class CreateEnvironmentScenarioTest {

	EnvironmentRepository repo = mock(EnvironmentRepository.class);
	Dispatchable dispatch = mock(Dispatchable.class);
	HostPool pool = mock(HostPool.class);
	NamingService namingService = mock(NamingService.class);
	Logger logger = mock(Logger.class);
	EventStore eventStore = new InMemoryEventStore();
	EventPublisher publisher = new EventPublisher(eventStore);
	ConfigurationManagement configMgt = mock(ConfigurationManagement.class);

	public CreateEnvironmentCommand.Builder builder(int numNodes) throws Exception {
		File tempFile = File.createTempFile("test", "ks");
		tempFile.deleteOnExit();
		String tempDir = new File(tempFile.getParent()).getAbsolutePath();
		Environment environment = new Environment("test");
		Host host = new Host("test1", "peoplemerge.com","192.168.0.111");
		environment.add(host);
		KickstartTemplateService kickstartServer = KickstartTemplateService.factory(publisher, environment, tempDir,
				new NfsMount("192.168.0.4", "/media"),new Puppet(publisher, new Host("puppetmaster1", "peoplemerge.com", "192.168.0.7"),dispatch));
		BlockingEventStep fake = mock(BlockingEventStep.class);
		when(fake.execute()).thenReturn(ExitCode.SUCCESS);
		when(pool.createStep(any(Host.Type.class), any(Host.class))).thenReturn(fake);
		when(pool.buildStepForHostToStop(any(Environment.class), any(Host.class))).thenReturn(fake);
		when(pool.buildStepForStartingHost(any(Environment.class), any(Host.class))).thenReturn(fake);
//		Executable hostRestarted = repo.buildStepToBlockUntilProvisioned(environment);
		when(repo.buildStepToBlockUntilProvisioned(any(Environment.class))).thenReturn(fake);
		when(namingService.buildStepToUpdate(any(EventPublisher.class), any(EnvironmentRepository.class))).thenReturn(fake);
		//			Executable configMgtStep = configurationManagement.postCompleteStep(host);
		DispatchableStep dispatchFake = mock(DispatchableStep.class);
		when(dispatchFake.execute()).thenReturn(ExitCode.SUCCESS);
		when(configMgt.postCompleteStep(any(Host.class))).thenReturn(dispatchFake);
		when(configMgt.newEnvironment(any(EnvironmentRepository.class))).thenReturn(dispatchFake);
		when(configMgt.nodeProvisioned(any(Host.class))).thenReturn(dispatchFake);
		

		CreateEnvironmentCommand.Builder createCommandBuilder = new CreateEnvironmentCommand.Builder(
				"test", "peoplemerge.com", repo, publisher);
		createCommandBuilder.withNodes(numNodes, Host.Type.SMALL, pool);
		createCommandBuilder.withDispatch(dispatch);
		createCommandBuilder.withKickstartServer(kickstartServer);
		createCommandBuilder.withNamingService(namingService);
		createCommandBuilder.withEventStore(eventStore);
		createCommandBuilder.withConfigurationManagement(configMgt);
		return createCommandBuilder;
	}

	//TODO treemap.  this needs to be tested
	@Ignore
	@Test
	public void provisionUsingEventHandlers() throws Exception {

		CreateEnvironmentCommand command = builder(1).build();
		// Command should create a job
		Job job = command.create();
		// Job should have steps expected to run when job is started.
		//Executable firstStep = job.getStep();
		ExitCode jobExit = job.execute();
		// The first step is to write a kickstart file.
		//WriteKickstartStep kickstartStep = (WriteKickstartStep) firstStep;
		// Step execution should ensure a kickstartRequest listener is attached
		// to the event store.

		// Step execution should add a listener for a kickstartCompleted event

		// Step execution should write the an event to the event store, and
		// block until kickstartCompleted event received.

		// The store will save it, and pass the processing to the
		// KickstartService.

		//ExitCode kickstartExit = kickstartStep.execute();
		// Steps should return ExitCode.
		assertEquals(ExitCode.SUCCESS, jobExit);
		// When executed, steps send events.
		verify(eventStore, atLeastOnce()).store(
				any(EnvironmentEvent.class));
		verify(eventStore, atLeastOnce()).store(
				any(DispatchEvent.class));

		// Steps can block (using local latch at first) until something calls
		// back

	}

}
