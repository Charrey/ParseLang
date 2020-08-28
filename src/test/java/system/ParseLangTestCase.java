package system;

import parselang.filereader.FileReader;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.Parser;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public abstract class ParseLangTestCase {

    protected FileReader fileReader;
    protected ParseRuleStorage storage;
    protected Parser parser;


    protected String readResource(String path) throws IOException {
        return fileReader.readStringFromFile(new File(this.getClass().getResource(path).getFile()).getAbsolutePath());
    }

    protected void testResource(String file, NonTerminal toplevel) throws IOException, ParseErrorException {
        String contentsToExecute = readResource(file);
        testString(contentsToExecute, toplevel);
    }

    protected ParseResult testString(String program, NonTerminal toplevel) throws ParseErrorException {
        ParseResult result = parser.readFile(storage, program, toplevel);
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        return result;
    }



}
