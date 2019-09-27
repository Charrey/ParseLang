package system.parseLangV1;

import javafx.util.Pair;
import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static parselang.parser.ParseRuleStorage.*;

public class TestUpdateGrammar extends ParseLangV1TestCase {

    @Test
    public void testMultipleTokens() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/multipleTokens.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("FooExpression").addRhs(term("foo"), term("bar"), nonTerm("Flop")), Direction.RIGHT)));
    }

    @Test
    public void testStarToken() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/starToken.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("FooExpression").addRhs(star(term("foo"))), Direction.RIGHT)));
    }

    @Test
    public void testStarTokenMultiple() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/starTokenMultiple.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("FooExpression").addRhs(star(term("foo"), term("bar"))), Direction.RIGHT)));
    }

    @Test
    public void testStarTokenNested() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/starTokenNested.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("FooExpression").addRhs(star(star(term("foo")), term("bar"))), Direction.RIGHT)));
    }

    @Test
    public void testNamedToken() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/namedToken.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("FooExpression").addRhs(bound(nonTerm("SimpleExpression"), "exp", false)), Direction.RIGHT)));
    }

    @Test
    public void testLazyToken() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/tokenDelayedExecution.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("FooExpression").addRhs(bound(nonTerm("SimpleExpression"), "exp", true)), Direction.RIGHT)));
    }
}
