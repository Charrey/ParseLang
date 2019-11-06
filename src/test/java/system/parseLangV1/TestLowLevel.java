package system.parseLangV1;

import org.junit.Test;
import parselang.intermediate.ASTWriter;
import parselang.intermediate.dataholders.Program;
import parselang.parser.ParseResult;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.Sanitizer;
import parselang.writer.FruityWriter;
import system.ParseLangTestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static parselang.parser.ParseRuleStorage.nonTerm;

public class TestLowLevel extends ParseLangV1TestCase {

    @Test
    public void testPrint() throws IOException, ParseErrorException {
        String contentsToExecute = readResource("expressions/print.plang");
        ParseResult result = testString(contentsToExecute, nonTerm("HighLevel"));
        ASTWriter intermediateWriter = new ASTWriter();
        Program intermediate = intermediateWriter.write("TestProgram", contentsToExecute, result.getTree(), storage);
        String res = new FruityWriter().writeToString(intermediate, storage);
        System.out.println(res);
    }

    @Test
    public void testTryCatch() throws IOException, ParseErrorException {
        parser.setVerbosity(1);
        String contentsToExecute = readResource("expressions/try_catch.plang");
        ParseResult result = testString(contentsToExecute, nonTerm("HighLevel"));
        ASTWriter intermediateWriter = new ASTWriter();
        Program intermediate = intermediateWriter.write("TestProgram", contentsToExecute, result.getTree(), storage);
        String res = new FruityWriter().writeToString(intermediate, storage);
        System.out.println(res);
    }
}
