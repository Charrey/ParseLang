package system.parseLangV1;

import org.junit.Test;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestErrorHandling extends ParseLangV1TestCase {

    @Test
    public void testUnfishedProgram() throws IOException {
        try {
            testFile("faulty/forgotclosebracket.plang");
            fail();
        } catch (ParseErrorException e) {
            assertEquals("No alternative at index (2:15) at EOF", e.getMessage());
        }
    }

    @Test
    public void testTypo() throws IOException {
        try {
            testFile("faulty/typo.plang");
            fail();
        } catch (ParseErrorException e) {
            assertEquals("No alternative at index (4:13) at +", e.getMessage());
        }
    }
}
