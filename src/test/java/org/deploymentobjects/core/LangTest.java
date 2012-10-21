package org.deploymentobjects.core;
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
import static junit.framework.Assert.*;

import org.antlr.runtime.RecognitionException;
import org.deploymentobjects.core.application.CreateEnvironmentCommand;
import org.deploymentobjects.core.application.ScriptedCommand;
import org.deploymentobjects.core.domain.model.execution.Executable;
import org.deploymentobjects.core.domain.model.execution.Program;
import org.deploymentobjects.core.domain.model.execution.Step;
import org.junit.Ignore;
import org.junit.Test;


public class LangTest {
	
	@Ignore
	@Test
	public void scriptedSentence() throws RecognitionException {
		String sentence = "On host localhost run:<<EOF\n"
			+ "ls\n"
			+ "EOF\n";
		Program program = Program.factory(sentence);
		
		Step step = program.getSteps().get(0);
		assertTrue(step.getCommand() instanceof ScriptedCommand);
		ScriptedCommand toRun = (ScriptedCommand) step.getCommand();	
		assertEquals("ls",toRun.getBody());
		assertEquals("localhost",step.getNodes().get(0).getHostname());
		assertEquals("on Node[hostname=localhost,domainname=<null>,ip=<null>,type=<null>,provisioned=<null>] run ls", program.display());
	}
	
	private static String ZOOKEEPER_SENTENCE = "Persist with zookeeper with connection string localhost:2181\n";
	
	@Test
	public void createSentence() throws RecognitionException {
		String sentence = ZOOKEEPER_SENTENCE + "Create a new environment called development using 1 small nodes from dom0 xen0";
		//String sentence = "Create a new environment called development using 1 small nodes from dom0 xen0";
		Program program = Program.factory(sentence);
		
		// Perhaps the create environment command should return an environment
		// implements Command<Environment> ?
		// ditch Command pattern?
		Step step = program.getSteps().get(0);
		assertTrue(step.getCommand() instanceof CreateEnvironmentCommand);
		CreateEnvironmentCommand toRun = (CreateEnvironmentCommand) step.getCommand();
		assertTrue(toRun.toString().contains("hostname=development1"));
		assertTrue(toRun.toString().contains("environmentName=development"));
		

		// Reach out to the domO to create the node
		//toRun.execute();
		
		// Assume (default) HostsFileNaming
		//Environment newEnvironment = toRun.execute();

	}
	@Test
	public void createSentenceWithRoles() throws RecognitionException {

		String sentence = ZOOKEEPER_SENTENCE + "Create a new environment called development using 1 small nodes from dom0 xen0 having roles web db";
		Program program = Program.factory(sentence);
		
		Step step = program.getSteps().get(0);
		assertTrue(step.getCommand() instanceof CreateEnvironmentCommand);
		CreateEnvironmentCommand toRun = (CreateEnvironmentCommand) step.getCommand();
		assertTrue(toRun.toString().contains("hostname=developmentwebdb1"));
		assertTrue(toRun.toString().contains("role=web,role=db"));

	}
	@Test
	public void createSentenceWithPuppet() throws RecognitionException {
		String puppet = "Use puppet with puppetmaster on puppetmaster1 peoplemerge.com 192.168.10.250 \n";
		String sentence = puppet + ZOOKEEPER_SENTENCE + "Create a new environment called development using 1 small nodes from dom0 xen0 having roles web db";
		Program program = Program.factory(sentence);
		
		Step step = program.getSteps().get(0);
		assertTrue(step.getCommand() instanceof CreateEnvironmentCommand);
		CreateEnvironmentCommand toRun = (CreateEnvironmentCommand) step.getCommand();
		String programOut = toRun.toString();
		assertTrue(programOut.contains("Puppet"));
		

	}
	
	public void createSentenceWithJsch() throws RecognitionException {
		String jsch = "Use jsch dispatch as user root\n";
		String sentence = jsch + ZOOKEEPER_SENTENCE + "Create a new environment called development using 1 small nodes from dom0 xen0 having roles web db";
		Program program = Program.factory(sentence);
		
		Step step = program.getSteps().get(0);
		assertTrue(step.getCommand() instanceof CreateEnvironmentCommand);
		CreateEnvironmentCommand toRun = (CreateEnvironmentCommand) step.getCommand();
		String programOut = toRun.toString();
		assertTrue(programOut.contains("root"));
		
	}
	@Test
	public void deploySentence() throws RecognitionException {
		String sentence = ZOOKEEPER_SENTENCE + "Deploy latest infrastructure code from version control to the testing environment";
		Program program = Program.factory(sentence);
		Step step = program.getSteps().get(0);
		Executable toRun = step.getCommand();
	}

	
}
