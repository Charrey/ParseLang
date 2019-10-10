package system.parseLangV1;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.data.AST;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.Analytics;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static parselang.parser.ParseRuleStorage.nonTerm;

public class StressTest extends ParseLangV1TestCase {

    private Analytics analytics = new Analytics();

    @Test
    public void testManyDecls() throws IOException, ParseErrorException {
        String program = readResource("stresstesting/manyDecls.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        List<AST> declarations = analytics.searchByRoot(result.getTree(), nonTerm("Declaration"));
        assertEquals(200, declarations.size());
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        System.out.println(parser.getTotalTime() + " seconds parsing");
        System.out.println(parser.getTotalTime()/declarations.size() + " seconds parsing per declaration");
        System.out.println(storage.firstCalc.getTotalTime() + " seconds calculating first (" + (100.0*storage.firstCalc.getTotalTime()/parser.getTotalTime()) + "%)");
        System.out.println(storage.firstCalc.getTotalTime()/declarations.size() + " seconds calculating first per declaration");
        System.out.println(storage.followCalc.getTotalTime() + " seconds calculating follow (" + (100.0*storage.followCalc.getTotalTime()/parser.getTotalTime()) + "%)");
        System.out.println(storage.followCalc.getTotalTime()/declarations.size()  + " seconds calculating follow per declaration");
        System.out.println(storage.firstPlusCalc.getTotalTime() + " seconds calculating firstPlus (" + (100.0*storage.firstPlusCalc.getTotalTime()/parser.getTotalTime()) + "%)");
        System.out.println(storage.firstPlusCalc.getTotalTime()/declarations.size() + " seconds calculating firstPlus per declaration");

    }

    @Test
    public void testComplexDecl() throws IOException, ParseErrorException {
        String program = readResource("stresstesting/ComplexDecl.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        List<AST> declarations = analytics.searchByRoot(result.getTree(), nonTerm("Declaration"));
        assertEquals(1, declarations.size());
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        System.out.println(parser.getTotalTime() + " seconds parsing");
        System.out.println(parser.getTotalTime()/declarations.size() + " seconds parsing per declaration");
        System.out.println(storage.firstCalc.getTotalTime() + " seconds calculating first (" + (100.0*storage.firstCalc.getTotalTime()/parser.getTotalTime()) + "%)");
        System.out.println(storage.firstCalc.getTotalTime()/declarations.size() + " seconds calculating first per declaration");
        System.out.println(storage.followCalc.getTotalTime() + " seconds calculating follow (" + (100.0*storage.followCalc.getTotalTime()/parser.getTotalTime()) + "%)");
        System.out.println(storage.followCalc.getTotalTime()/declarations.size()  + " seconds calculating follow per declaration");
        System.out.println(storage.firstPlusCalc.getTotalTime() + " seconds calculating firstPlus (" + (100.0*storage.firstPlusCalc.getTotalTime()/parser.getTotalTime()) + "%)");
        System.out.println(storage.firstPlusCalc.getTotalTime()/declarations.size() + " seconds calculating firstPlus per declaration");
    }

}
