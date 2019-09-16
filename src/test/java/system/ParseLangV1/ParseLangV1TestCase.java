package system.ParseLangV1;

import org.junit.Before;
import parselang.filereader.FileReader;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseRuleStorage;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import java.io.File;
import java.io.IOException;

public abstract class ParseLangV1TestCase {

    private FileReader fileReader;
    ParseRuleStorage storage;
    Parser parser;


    String readString(String path) throws IOException {
        return fileReader.readStringFromFile(new File(this.getClass().getResource(path).getFile()).getAbsolutePath());
    }

    @Before
    public void setup() {
        fileReader = new FileReader();
        storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1());
        parser = new RecursiveParser();
    }


}
