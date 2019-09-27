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
import static parselang.parser.ParseRuleStorage.nonTerm;
import static parselang.parser.ParseRuleStorage.term;

public class TestUpdateGrammar extends ParseLangV1TestCase {

    @Test
    public void testMultipleTokens() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/multipleTokens.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        AST declaration = (AST) ((ASTElemList)((ASTElemList)result.getTree().getChild(0)).get(0)).get(1);
        List<Node> nodesExtracted = parser.extractNodes(program, declaration.getChild(7));
        assertEquals(new Terminal("foo"), nodesExtracted.get(0));
        assertEquals(new Terminal("bar"), nodesExtracted.get(1));
        assertEquals(new NonTerminal("Flop"), nodesExtracted.get(2));
        assertEquals(3, nodesExtracted.size());
    }

    @Test
    public void testStarToken() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/starToken.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        AST declaration = (AST) ((ASTElemList)((ASTElemList)result.getTree().getChild(0)).get(0)).get(1);
        List<Node> nodesExtracted = parser.extractNodes(program, declaration.getChild(7));
        System.out.println(nodesExtracted);
        assertEquals(1, nodesExtracted.size());
        Node node = nodesExtracted.get(0);
        assertTrue(node instanceof StarNode);
        StarNode starNode = (StarNode) node;
        assertEquals(1, starNode.contents().size());

        Node contentOfStar = starNode.contents().get(0);
        assertTrue(contentOfStar instanceof Terminal);
        Terminal terminal = (Terminal) contentOfStar;
        assertEquals("foo", terminal.getValue());
    }

    @Test
    public void testStarTokenMultiple() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/starTokenMultiple.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        AST declaration = (AST) ((ASTElemList)((ASTElemList)result.getTree().getChild(0)).get(0)).get(1);
        List<Node> nodesExtracted = parser.extractNodes(program, declaration.getChild(7));
        System.out.println(nodesExtracted);
        assertEquals(1, nodesExtracted.size());
        Node node = nodesExtracted.get(0);
        assertTrue(node instanceof StarNode);
        StarNode starNode = (StarNode) node;
        assertEquals(2, starNode.children().size());

        assertEquals(term("foo"), starNode.children().get(0));
        assertEquals(term("bar"), starNode.children().get(1));
    }

    @Test
    public void testStarTokenNested() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/starTokenNested.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        AST declaration = (AST) ((ASTElemList)((ASTElemList)result.getTree().getChild(0)).get(0)).get(1);
        List<Node> nodesExtracted = parser.extractNodes(program, declaration.getChild(7));
        System.out.println(nodesExtracted);
        assertEquals(1, nodesExtracted.size());
        Node node = nodesExtracted.get(0);
        assertTrue(node instanceof StarNode);
        StarNode starNode = (StarNode) node;
        assertEquals(2, starNode.children().size());
        assertEquals(term("bar"), starNode.children().get(1));
        assertTrue(starNode.children().get(0) instanceof StarNode);
        StarNode firstChild = (StarNode) starNode.children().get(0);
        assertEquals(1, firstChild.children().size());
        assertEquals(term("foo"), firstChild.children().get(0));
    }

    @Test
    public void testNamedToken() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/namedToken.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());

        Set<Pair<ParseRule, Direction>> addedRules = storage.getAddedRules();
        assertTrue(addedRules.contains(new Pair<>(new ParseRule("Expression").addRhs(nonTerm("FooExpression")), Direction.LEFT)));
    }

    @Test
    public void testLazyToken() throws IOException, ParseErrorException {
        String program = readString("updateGrammar/tokenDelayedExecution.plang");
        ParseResult result = parser.readFile(storage, program, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
    }
}
