package parselang.interpreter;

import parselang.filereader.FileReader;
import parselang.parser.ParseRuleStorage;
import parselang.parser.Parser;

import java.io.IOException;
import java.nio.file.Path;

public class Interpreter {

    private static FileReader fileReader = new FileReader();
    private static Parser parser = new Parser();
    private static ParseRuleStorage storage;

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String contentsToExecute = fileReader.readStringFromFile(fileName);
        Path rootPath = fileReader.seekRoot(fileName);
        String highLevelContents = fileReader.readRootFile(rootPath);
        parser.readFile(storage, highLevelContents);
    }
}
