package system.ParseLangV1;

import org.junit.Before;
import parselang.filereader.FileReader;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.UndefinedNontermException;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public abstract class ParseLangV1TestCase {

    private FileReader fileReader;
    ParseRuleStorage storage;
    Parser parser;


    String readString(String path) throws IOException {
        return fileReader.readStringFromFile(new File(this.getClass().getResource(path).getFile()).getAbsolutePath());
    }

    void test(String file) throws IOException, ParseErrorException {
        String contentsToExecute = readString(file);
        ParseResult result = parser.readFile(storage, contentsToExecute);
        assertEquals("", result.getRemaining());
        assertEquals(contentsToExecute, result.getParsed());
    }

    @Before
    public void setup() throws UndefinedNontermException {
        fileReader = new FileReader();
        storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1());
        parser = new RecursiveParser();
    }


}
