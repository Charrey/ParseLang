package parselang.interpreter;

import parselang.filereader.FileReader;
import parselang.parser.data.AST;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.ParseRuleStorage;
import parselang.parser.Parser;

import java.io.IOException;
import java.nio.file.Path;

public class Interpreter {

    private static FileReader fileReader = new FileReader();
    private static Parser parser = new Parser();
    private static ParseRuleStorage storage = new ParseRuleStorage();

    public static void main(String[] args) throws IOException, ParseErrorException {
        String fileName = args[0];
        String command = args[1];
        String contentsToExecute = fileReader.readStringFromFile(fileName);
        //Path rootPath = fileReader.seekRoot(fileName);

        //String highLevelContents = fileReader.readRootFile(rootPath);

        //parser.readHighLevel(storage, highLevelContents);
        parser.readFile(storage, contentsToExecute);

        //AST parsedCommand = parser.readCommand(storage, command);
        //System.out.println(parsedCommand.evaluate());
    }
}
