package system.parseLangV1;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static parselang.parser.ParseRuleStorage.nonTerm;

public class TestLowLevel extends ParseLangV1TestCase {

    @Test
    public void testPrint() throws IOException, ParseErrorException {
        String contentsToExecute = readResource("expressions/print.plang");
        ParseResult result = testString(contentsToExecute, nonTerm("HighLevel"));
        //ASTWriter intermediateWriter = new ASTWriter();
        //Program intermediate = intermediateWriter.write("TestProgram", contentsToExecute, result.getTree(), storage);
        //String res = new FruityWriter().writeToString(intermediate, storage);
        System.out.println(result);
    }

    @Test
    public void testTryCatch() throws IOException, ParseErrorException {
        parser.setVerbosity(1);
        String contentsToExecute = readResource("expressions/try_catch.plang");
        ParseResult result = testString(contentsToExecute, nonTerm("HighLevel"));
        //ASTWriter intermediateWriter = new ASTWriter();
        //Program intermediate = intermediateWriter.write("TestProgram", contentsToExecute, result.getTree(), storage);
        //String res = new FruityWriter().writeToString(intermediate, storage);
        System.out.println(result);
    }
}
