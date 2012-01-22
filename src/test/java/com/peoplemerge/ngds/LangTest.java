package com.peoplemerge.ngds;
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
import junit.framework.Assert;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.junit.Test;

import com.peoplemerge.ngds.Executable;
import com.peoplemerge.ngds.CreateEnvironmentCommand;
import com.peoplemerge.ngds.NgdsLexer;
import com.peoplemerge.ngds.NgdsParser;
import com.peoplemerge.ngds.Program;
import com.peoplemerge.ngds.ScriptedCommand;
import com.peoplemerge.ngds.Step;

public class LangTest {
	
	
	@Test
	public void scriptedSentence() throws RecognitionException {
		String sentence = "On host localhost run:<<__EOF__\n"
			+ "ls\n"
			+ "__EOF__";
		Program program = parse(sentence);
		
		Step step = program.getSteps().get(0);
		Assert.assertTrue(step.getCommand() instanceof ScriptedCommand);
		ScriptedCommand toRun = (ScriptedCommand) step.getCommand();	
		Assert.assertEquals("ls",toRun.getBody());
		Assert.assertEquals("localhost",step.getNode().getHostname());
		Assert.assertEquals("on localhost run ls", program.display());
	}
	
	@Test
	public void createSentence() throws RecognitionException {
		String sentence = "Create a new environment called development using 1 small nodes from dom0.";
		Program program = parse(sentence);
		
		// Perhaps the create environment command should return an environment
		// implements Command<Environment> ?
		// ditch Command pattern?
		Step step = program.getSteps().get(0);
		Assert.assertTrue(step.getCommand() instanceof CreateEnvironmentCommand);
		CreateEnvironmentCommand toRun = (CreateEnvironmentCommand) step.getCommand();		
		
		// Reach out to the domO to create the node
		
		// Assume (default) HostsFileNaming
		//Environment newEnvironment = toRun.execute();
		
	}

	@Test
	public void deploySentence() throws RecognitionException {
		String sentence = "Deploy latest infrastructure code from version control to the testing environment.";
		Program program = parse(sentence);
		Step step = program.getSteps().get(0);
		Executable toRun = step.getCommand();
	}

	private Program parse(String sentence) throws RecognitionException {
		CharStream stringStream = new ANTLRStringStream(sentence);
		NgdsLexer lexer = new NgdsLexer(stringStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		NgdsParser parser = new NgdsParser(tokenStream);
		Program result = parser.program().program;
		return result;
	}
}
