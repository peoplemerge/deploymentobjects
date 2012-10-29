package org.deploymentobjects.core.application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.junit.Test;


public class CreateEnvironmentScenarioTest {

	@Test
	public void provisionUsingEventStore() throws Exception {
		MockBuilder mockery = new MockBuilder();
		CreateEnvironmentCommand command = mockery.builder(1).build();
		// Command should create a job
		Job job = command.create();
		// Job should have steps expected to run when job is started.
		Executable firstStep = job.getSteps().get(0);
		// The first step is to write a kickstart file.
		WriteKickstartStep kickstartStep = (WriteKickstartStep) firstStep;
		ExitCode kickstartExit = kickstartStep.execute();
		// Steps should return ExitCode.
		assertEquals(ExitCode.SUCCESS, kickstartExit);
		// When executed, steps send events.
		verify(mockery.eventStore, times(1)).store(
				any(HostProvisioningEvent.Requested.class));
		verify(mockery.eventStore, times(1)).store(
				any(HostProvisioningEvent.Completed.class));

		// Steps can block (using local latch at first) until something calls
		// back

	}

	@Test
	public void provisionUsingEventHandlers() throws Exception {
		MockBuilder mockery = new MockBuilder();
		CreateEnvironmentCommand command = mockery.builder(1).build();
		// Command should create a job
		Job job = command.create();
		// Job should have steps expected to run when job is started.
		Executable firstStep = job.getSteps().get(0);
		// The first step is to write a kickstart file.
		WriteKickstartStep kickstartStep = (WriteKickstartStep) firstStep;
		// Step execution should ensure a kickstartRequest listener is attached
		// to the event store.

		// Step execution should add a listener for a kickstartCompleted event

		// Step execution should write the an event to the event store, and
		// block until kickstartCompleted event received.

		// The store will save it, and pass the processing to the
		// KickstartService.

		ExitCode kickstartExit = kickstartStep.execute();
		// Steps should return ExitCode.
		assertEquals(ExitCode.SUCCESS, kickstartExit);
		// When executed, steps send events.
		verify(mockery.eventStore, times(1)).store(
				any(HostProvisioningEvent.Requested.class));
		verify(mockery.eventStore, times(1)).store(
				any(HostProvisioningEvent.Completed.class));

		// Steps can block (using local latch at first) until something calls
		// back

	}

}
