package system.trees;

import org.junit.Before;
import parselang.filereader.FileReader;
import parselang.languages.ParseLangTest;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseRuleStorage;
import parselang.parser.UndefinedNontermException;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.RecursiveParser;
import system.ParseLangTestCase;

import java.io.IOException;

public abstract class ParseLangTreesTestCase extends ParseLangTestCase {

    @Before
    public void setup() throws UndefinedNontermException {
        fileReader = new FileReader();
        storage = new ParseRuleStorage();
        storage.prepare(new ParseLangTest(), new NonTerminal("Level1"));
        parser = new RecursiveParser();
    }

    void test(String file) throws IOException, ParseErrorException {
        super.testFile(file, new NonTerminal("Level1"));
    }

}
