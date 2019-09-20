package system.ParseLangV1;

import org.junit.Test;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

public class TestErrorHandling extends ParseLangV1TestCase {

    @Test
    public void testForgotBracket() throws IOException, ParseErrorException {
        test("faulty/forgotclosebracket.plang");
    }
}
