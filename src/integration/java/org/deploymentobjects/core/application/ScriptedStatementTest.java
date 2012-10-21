package org.deploymentobjects.core.application;
import junit.framework.Assert;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.deploymentobjects.core.DeploymentObjectsLexer;
import org.deploymentobjects.core.DeploymentObjectsParser;
import org.deploymentobjects.core.application.ScriptedCommand;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Program;
import org.deploymentobjects.core.domain.model.execution.Step;
import org.junit.Test;


public class ScriptedStatementTest {
	
	@Test
	public void testStatement() throws Exception{
		String sentence = "On host localhost run:<<EOF\n/bin/ls /bin/ls\nEOF";
		CharStream stringStream = new ANTLRStringStream(sentence);
		DeploymentObjectsLexer lexer = new DeploymentObjectsLexer(stringStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		DeploymentObjectsParser parser = new DeploymentObjectsParser(tokenStream);
		Program program = parser.program().program;
		
		Step step = program.getSteps().get(0);
		Assert.assertTrue(step.getCommand() instanceof ScriptedCommand);
		ScriptedCommand toRun = (ScriptedCommand) step.getCommand();	
		ExitCode returned = toRun.execute();
		
	
		//TODO Complete this test
		Assert.assertEquals(ExitCode.FAILURE, returned);
		
	}
	
	
}
