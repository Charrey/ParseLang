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
        String contentsToExecute = readString("general/emptyfile.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals("", result.getParsed());
    }

    @Test
    public void testEmpty100Times() throws IOException, ParseErrorException {
        String contentsToExecute = readString("general/test100empty.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testEmptyDecl() throws IOException, ParseErrorException {
        String contentsToExecute = readString("general/emptydecl.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }


}
