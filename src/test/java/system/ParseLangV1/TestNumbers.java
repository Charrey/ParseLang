package system.ParseLangV1;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestNumbers extends ParseLangV1TestCase{

    @Test
    public void testNumberLiteral() throws IOException, ParseErrorException {
        test("expressions/number1.plang");
    }

    @Test
    public void testPlusExpression() throws IOException, ParseErrorException {
        test("expressions/number2.plang");
    }

    @Test
    public void testPlusExpressionWhitespace() throws IOException, ParseErrorException {
        test("expressions/number3.plang");
    }
}
