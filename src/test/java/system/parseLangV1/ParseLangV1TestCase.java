package system.parseLangV1;

import org.junit.Before;
import parselang.filereader.FileReader;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseRuleStorage;
import parselang.parser.UndefinedNontermException;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.RecursiveParser;
import system.ParseLangTestCase;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public abstract class ParseLangV1TestCase extends ParseLangTestCase {

    @Before
    public void setup() throws UndefinedNontermException {
        fileReader = new FileReader();
        storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1(), new NonTerminal("HighLevel"));
        parser = new RecursiveParser();
        parser.setVerbosity(0);
    }

    void testFile(String file) throws IOException, ParseErrorException {
        super.testFile(file, new NonTerminal("HighLevel"));
    }


}
