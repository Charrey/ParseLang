package demo;

import org.junit.Test;
import parselang.parser.ParseResult;
import parselang.parser.exceptions.ParseErrorException;
import system.parseLangV1.ParseLangV1TestCase;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static parselang.parser.ParseRuleStorage.nonTerm;

public class TestDemo extends ParseLangV1TestCase {

    @Test
    public void testDemo() throws IOException, ParseErrorException {
        String program = readString("bad.plang");
        ParseResult result = parser.readFile(storage, program, nonTerm("HighLevel"));
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        System.out.println(result.getTree());
    }
}
