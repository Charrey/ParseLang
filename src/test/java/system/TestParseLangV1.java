package system;

import org.junit.Before;
import org.junit.Test;
import parselang.filereader.FileReader;
import parselang.interpreter.Interpreter;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.Parser;
import parselang.parser.exceptions.ParseErrorException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TestParseLangV1 {

    private FileReader fileReader;

    private String readString(String path) throws IOException {
        return fileReader.readStringFromFile(new File(this.getClass().getResource(path).getFile()).getAbsolutePath());
    }


    @Before
    public void setup() {
        fileReader = new FileReader();
    }

    @Test
    public void testEmpty() throws IOException, ParseErrorException {
        String contentsToExecute = readString("emptyfile.plang");
        ParseRuleStorage storage = new ParseRuleStorage();
        Parser parser = new Parser();
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals("", result.getParsed());
    }

    @Test
    public void testEmpty100Times() throws IOException, ParseErrorException {
        String contentsToExecute = readString("test100empty.plang");
        ParseRuleStorage storage = new ParseRuleStorage();
        Parser parser = new Parser();
        ParseResult result = parser.readFile(storage, contentsToExecute);
        //System.out.println(result);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testEmptyDecl() throws IOException, ParseErrorException {
        String contentsToExecute = readString("emptydecl.plang");
        ParseRuleStorage storage = new ParseRuleStorage();
        Parser parser = new Parser();
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testReturnNumber1() throws IOException, ParseErrorException {
        String contentsToExecute = readString("number1.plang");
        ParseRuleStorage storage = new ParseRuleStorage();
        Parser parser = new Parser();
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Test
    public void testReturnNumber2() throws IOException, ParseErrorException {
        String contentsToExecute = readString("number2.plang");
        ParseRuleStorage storage = new ParseRuleStorage();
        Parser parser = new Parser();
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }
}
