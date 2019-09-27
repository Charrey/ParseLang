package system.parseLangV1;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.data.AST;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.Analytics;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static parselang.parser.ParseRuleStorage.nonTerm;

public class StressTest extends ParseLangV1TestCase {

    private Analytics analytics = new Analytics();

    @Test
    public void testManyDecls() throws IOException, ParseErrorException {
        String program = readString("stresstesting/manyDecls.plang");
        long start = System.currentTimeMillis();
        parser.setVerbosity(0);
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        long millis = System.currentTimeMillis() - start;
        Set<AST> declarations = analytics.searchByRoot(result.getTree(), nonTerm("Declaration"));
        assertEquals(200, declarations.size());
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        System.out.println(millis/1000.0 + " seconds");
        System.out.println(millis/(1000.0 * declarations.size()) + " seconds per declaration");

    }

    @Test
    public void testComplexDecl() throws IOException, ParseErrorException {
        String program = readString("stresstesting/ComplexDecl.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
    }

}
