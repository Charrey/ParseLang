package parselang.interpreter;

import parselang.filereader.FileReader;
import parselang.languages.ParseLangV1;
import parselang.parser.UndefinedNontermException;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.ParseRuleStorage;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import java.io.IOException;

public class Interpreter {

    private static FileReader fileReader = new FileReader();
    private static Parser parser = new RecursiveParser();
    private static ParseRuleStorage storage = new ParseRuleStorage();

    public static void main(String[] args) throws IOException, ParseErrorException, UndefinedNontermException {
        String fileName = args[0];
        String command = args[1];
        String contentsToExecute = fileReader.readStringFromFile(fileName);
        //Path rootPath = fileReader.seekRoot(fileName);

        //String highLevelContents = fileReader.readRootFile(rootPath);

        //parser.readHighLevel(storage, highLevelContents);
        storage.prepare(new ParseLangV1());
        parser.readFile(storage, contentsToExecute);

        //AST parsedCommand = parser.readCommand(storage, command);
        //System.out.println(parsedCommand.evaluate());
    }
}
