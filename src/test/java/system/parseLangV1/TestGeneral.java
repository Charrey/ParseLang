package system.parseLangV1;

import org.junit.Test;
import parselang.parser.exceptions.ParseErrorException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestGeneral extends ParseLangV1TestCase {

    @Test
    public void testEmpty() throws IOException, ParseErrorException {
        testFile("general/emptyfile.plang");
    }

    @Test
    public void testEmpty3Times() throws IOException, ParseErrorException {
        testFile("general/test3empty.plang");
    }

    @Test
    public void testEmptyDecl() throws IOException, ParseErrorException {
        testFile("general/emptydecl.plang");
    }


}
