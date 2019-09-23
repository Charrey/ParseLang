package system.parseLangV1;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestStar extends ParseLangV1TestCase {


    @Test
    public void testNumberLiteral() throws IOException, ParseErrorException {
        String contentsToExecute = readString("general/emptydecl.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute, new NonTerminal("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
        System.out.println(result.getTree());
    }

}
