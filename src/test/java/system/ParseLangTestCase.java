package system;

import org.junit.Before;
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


    protected String readString(String path) throws IOException {
        return fileReader.readStringFromFile(new File(this.getClass().getResource(path).getFile()).getAbsolutePath());
    }

    protected void testFile(String file, NonTerminal toplevel) throws IOException, ParseErrorException {
        String contentsToExecute = readString(file);
        testString(contentsToExecute, toplevel);
    }

    protected void testString(String program, NonTerminal toplevel) throws IOException, ParseErrorException {
        ParseResult result = parser.readFile(storage, program, toplevel);
        assertEquals("", result.getRemaining());
        assertEquals(program, result.getParsed());
        System.out.println(result.getTree());
    }



}
