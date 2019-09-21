package system.ParseLangV1;

import org.junit.Test;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

public class TestBooleans extends ParseLangV1TestCase {

    @Test
    public void testBooleanLiterals() throws IOException, ParseErrorException {
        testFile("expressions/boolean1.plang");
    }
}
