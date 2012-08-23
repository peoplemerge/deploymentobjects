package com.peoplemerge.ngds;

/************************************************************************
 ** 
 ** Copyright (C) 2011 Dave Thomas, PeopleMerge. All rights reserved. Contact:
 * opensource@peoplemerge.com.
 ** 
 ** This file is part of the NGDS language.
 ** 
 ** Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 ** 
 ** http://www.apache.org/licenses/LICENSE-2.0
 ** 
 ** Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ** 
 ** Other Uses Alternatively, this file may be used in accordance with the terms
 * and conditions contained in a signed written agreement between you and the
 * copyright owner.
 ************************************************************************/

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.junit.Test;

public class CreateEnvironmentCommandTest {

	ResourceStateRepository mockrepo = mock(ResourceStateRepository.class);
	Dispatchable dispatch = mock(Dispatchable.class);
	NodePool pool = mock(NodePool.class);
	NamingService namingService = mock(NamingService.class);
	// "Create a new environment called development using 1 small nodes from dom0."
	// Step to figure out where to create the node based on the dom0s in the
	// MockRepo
	// Step to use a connection method to connect to the dom0 and issue command
	// to create the node
	// Step to add the node to the naming service
	// Step to add the environment to the MockRepo

	@Test
	public void createOne() throws Exception{

		// TODO the Dom0 abstraction with NodePool should be overloaded or
		// perhaps rethought since we actually want to get the dom0 host from
		// the repository state
		CreateEnvironmentCommand.Builder createCommandBuilder = new CreateEnvironmentCommand.Builder(
				"test", mockrepo);
		createCommandBuilder.withNodes(1, Node.Type.SMALL, pool);
		//TODO the dispatch method should probably be control-inverted.
		createCommandBuilder.withDispatch(dispatch);
		
		CreateEnvironmentCommand command = createCommandBuilder.build();
		// so where is the libvirt / etc command encapsulated to hide 
		Step dummyStep = new Step(new ScriptedCommand("dummy"),pool);
		when(pool.createStep(Node.Type.SMALL)).thenReturn(dummyStep);
		ExitCode exitCode = command.execute();
		assertEquals(ExitCode.SUCCESS, exitCode);
		verify(pool).createStep(Node.Type.SMALL);
		verify(dispatch).dispatch(dummyStep);
		
		verify(mockrepo).save("environments.test", "test1");
		/* need to think more about how this will work outside of a test
		verify(namingService).add("test1", "10.0.1.1");
		verify(namingService).commit();
		*/
	}

	@Test
	public void createMultiple() throws Exception{

		// TODO the Dom0 abstraction with NodePool should be overloaded or
		// perhaps rethought since we actually want to get the dom0 host from
		// the repository state
		CreateEnvironmentCommand.Builder createCommandBuilder = new CreateEnvironmentCommand.Builder(
				"pair", mockrepo);
		createCommandBuilder.withNodes(2, Node.Type.SMALL, pool);
		//TODO the dispatch method should probably be control-inverted.
		createCommandBuilder.withDispatch(dispatch);
		
		CreateEnvironmentCommand command = createCommandBuilder.build();
		// so where is the libvirt / etc command encapsulated to hide 
		Step dummyStep = new Step(new ScriptedCommand("dummy"),pool);
		when(pool.createStep(Node.Type.SMALL)).thenReturn(dummyStep);
		ExitCode exitCode = command.execute();
		assertEquals(ExitCode.SUCCESS, exitCode);

		verify(mockrepo).save("environments.pair", "pair1,pair2");
		
	}


}
