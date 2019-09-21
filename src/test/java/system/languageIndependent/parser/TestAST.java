package system.languageIndependent.parser;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.data.AST;
import parselang.parser.data.ASTElemList;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static parselang.parser.ParseRuleStorage.nonTerm;
import static parselang.parser.ParseRuleStorage.term;


public class TestAST extends ParseLangTestTestCase {

    @Test
    public void testASTStructure() throws IOException, ParseErrorException {
        String program = "ba";
        ParseResult result = parser.readFile(storage, program, new NonTerminal("Level1"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        AST tree = result.getTree();
        assertEquals(1, tree.getChildren().size());
        assertTrue(tree.getChild(0) instanceof AST);
        AST level2Child = (AST) tree.getChild(0);
        assertEquals(nonTerm("Level2"), level2Child.getRoot());
        assertEquals(2, level2Child.getChildren().size());
        assertTrue( level2Child.getChild(1) instanceof AST);
        assertEquals(term("a"), ((AST)level2Child.getChild(1)).getRoot());
        assertTrue( level2Child.getChild(0) instanceof AST);
        AST level3Child = (AST) level2Child.getChild(0);
        assertEquals(nonTerm("Level3"), level3Child.getRoot());
        assertEquals(2, level3Child.getChildren().size());
        assertTrue( level3Child.getChild(0) instanceof AST);
        assertEquals(term("b"), ((AST)level3Child.getChild(0)).getRoot());
        assertTrue( level3Child.getChild(1) instanceof AST);
        AST level4Child = (AST) level3Child.getChild(1);
        assertEquals(1, level4Child.getChildren().size());
        assertTrue(level4Child.getChild(0) instanceof ASTElemList);
        assertEquals(0, ((ASTElemList) level4Child.getChild(0)).size());
   }

}
