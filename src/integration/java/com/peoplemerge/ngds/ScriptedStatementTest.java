package com.peoplemerge.ngds;
import junit.framework.Assert;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.junit.Test;

import com.peoplemerge.ngds.ExitCode;
import com.peoplemerge.ngds.NgdsLexer;
import com.peoplemerge.ngds.NgdsParser;
import com.peoplemerge.ngds.Program;
import com.peoplemerge.ngds.ScriptedCommand;
import com.peoplemerge.ngds.Step;

public class ScriptedStatementTest {
	
	@Test
	public void testStatement() throws Exception{
		String sentence = "On host localhost run:<<EOF\n/bin/ls /bin/ls\nEOF";
		CharStream stringStream = new ANTLRStringStream(sentence);
		NgdsLexer lexer = new NgdsLexer(stringStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		NgdsParser parser = new NgdsParser(tokenStream);
		Program program = parser.program().program;
		
		Step step = program.getSteps().get(0);
		Assert.assertTrue(step.getCommand() instanceof ScriptedCommand);
		ScriptedCommand toRun = (ScriptedCommand) step.getCommand();	
		ExitCode returned = toRun.execute();
		
	
		//TODO Complete this test
		Assert.assertEquals(ExitCode.FAILURE, returned);
		
	}
	
	
}
