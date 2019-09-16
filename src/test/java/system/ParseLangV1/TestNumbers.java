package system.ParseLangV1;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestNumbers extends ParseLangV1TestCase{

    @Test
    public void testNumberLiteral() throws IOException, ParseErrorException {
        String contentsToExecute = readString("expressions/number1.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testPlusExpression() throws IOException, ParseErrorException {
        String contentsToExecute = readString("expressions/number2.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testPlusExpressionWhitespace() throws IOException, ParseErrorException {
        String contentsToExecute = readString("expressions/number3.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }
}
