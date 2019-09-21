package system.ParseLangV1;

import org.junit.Before;
import org.junit.Test;
import parselang.filereader.FileReader;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestGeneral extends ParseLangV1TestCase {

    @Test
    public void testEmpty() throws IOException, ParseErrorException {
        test("general/emptyfile.plang");
    }

    @Test
    public void testEmpty3Times() throws IOException, ParseErrorException {
        test("general/test3empty.plang");
    }

    @Test
    public void testEmptyDecl() throws IOException, ParseErrorException {
        test("general/emptydecl.plang");
    }


}
