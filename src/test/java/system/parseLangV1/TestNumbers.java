package system.parseLangV1;

import org.junit.Test;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestNumbers extends ParseLangV1TestCase{

    @Test
    public void testNumberLiteral() throws IOException, ParseErrorException {
        testFile("expressions/number1.plang");
    }

    @Test
    public void testPlusExpression() throws IOException, ParseErrorException {
        testFile("expressions/number2.plang");
    }

    @Test
    public void testPlusExpressionWhitespace() throws IOException, ParseErrorException {
        testFile("expressions/number3.plang");
    }
}
