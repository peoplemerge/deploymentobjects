import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.junit.Test;

import com.peoplemerge.ngds.NgdsLexer;
import com.peoplemerge.ngds.NgdsParser;
import com.peoplemerge.ngds.Result;

public class LangTest {
	@Test
	public void createSentence() throws RecognitionException {
		String sentence = "Create a new environment called development using 1 small nodes from dom0.";
		Result result = parse(sentence);
	}

	@Test
	public void deploySentence() throws RecognitionException {
		String sentence = "Deploy latest infrastructure code from version control to the testing environment.";
		Result result = parse(sentence);
		result.getCommand();
	}

	private Result parse(String sentence) throws RecognitionException {
		CharStream stringStream = new ANTLRStringStream(sentence);
		NgdsLexer lexer = new NgdsLexer(stringStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		NgdsParser parser = new NgdsParser(tokenStream);
		Result result = parser.program().result;
		return result;
	}
}
