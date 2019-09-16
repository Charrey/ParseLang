package system;

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

public class TestParseLangV1 {

    private FileReader fileReader;

    private String readString(String path) throws IOException {
        return fileReader.readStringFromFile(new File(this.getClass().getResource(path).getFile()).getAbsolutePath());
    }


    ParseRuleStorage storage;
    Parser parser;

    @Before
    public void setup() {
        fileReader = new FileReader();
        storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1());
        parser = new RecursiveParser();
    }

    @Test
    public void testEmpty() throws IOException, ParseErrorException {
        String contentsToExecute = readString("emptyfile.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals("", result.getParsed());
    }

    @Test
    public void testEmpty100Times() throws IOException, ParseErrorException {
        String contentsToExecute = readString("test100empty.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testEmptyDecl() throws IOException, ParseErrorException {
        String contentsToExecute = readString("emptydecl.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testReturnNumber1() throws IOException, ParseErrorException {
        String contentsToExecute = readString("number1.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testReturnNumber2() throws IOException, ParseErrorException {
        String contentsToExecute = readString("number2.plang");
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }
}
