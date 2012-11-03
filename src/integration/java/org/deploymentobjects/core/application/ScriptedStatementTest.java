package org.deploymentobjects.core.application;
import junit.framework.Assert;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.deploymentobjects.core.DeploymentObjectsLexer;
import org.deploymentobjects.core.DeploymentObjectsParser;
import org.deploymentobjects.core.domain.model.execution.CreatesJob;
import org.deploymentobjects.core.domain.model.execution.ExitCode;
import org.deploymentobjects.core.domain.model.execution.Job;
import org.deploymentobjects.core.domain.model.execution.Program;
import org.junit.Ignore;
import org.junit.Test;


public class ScriptedStatementTest {
	/*
	
On host localhost run:<<EOF
/bin/ls /bin/ls
EOF	

scripted_statement returns [ScriptedCommand command] : 'On' + 
	 	'host' ID 
	'run:' '<<EOF'
 	body=.* {
 		$command = new ScriptedCommand(getPublisher(), $body.text, new Host($ID.text), dispatchable);
 		
 	} 
 	'EOF';

	
	*/
	@Ignore // TODO parser is ambiguous, fix this
	@Test
	public void testStatement() throws Exception{
		String sentence = "Persist with yaml file /tmp/matilda\nOn host localhost run:<<EOF\n/bin/ls /bin/ls\nEOF";
		CharStream stringStream = new ANTLRStringStream(sentence);
		DeploymentObjectsLexer lexer = new DeploymentObjectsLexer(stringStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		DeploymentObjectsParser parser = new DeploymentObjectsParser(tokenStream);
		Program program = parser.program().program;
		Assert.assertEquals(1, program.getSteps().size());
		CreatesJob step = program.getSteps().get(0);
		Assert.assertTrue(step instanceof ScriptedCommand);
		ScriptedCommand toRun = (ScriptedCommand) step;
		Job job = toRun.create();
		ExitCode returned = job.execute();
		
	
		//TODO Complete this test
		Assert.assertEquals(ExitCode.SUCCESS, returned);
		
	}
	
	
}
