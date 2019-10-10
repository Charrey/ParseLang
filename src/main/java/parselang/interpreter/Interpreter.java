package parselang.interpreter;

import parselang.intermediate.IntermediateWriter;
import parselang.intermediate.dataholders.Program;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.UndefinedNontermException;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;
import parselang.util.Sanitizer;
import parselang.writer.JavaSourceWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static parselang.parser.ParseRuleStorage.nonTerm;

public class Interpreter {

    public static void main(String[] args) throws UndefinedNontermException, IOException, ParseErrorException {
        System.out.println("Working on it! Run tests instead :)");
        byte[] encoded = Files.readAllBytes(Paths.get(args[0]));
        String program = new String(encoded, Charset.defaultCharset());
        Parser parser = new RecursiveParser();
        parser.setVerbosity(0);
        ParseRuleStorage storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1(), new NonTerminal("HighLevel"));
        ParseResult result = parser.readFile(storage, program, nonTerm("HighLevel"));
        IntermediateWriter intermediateWriter = new IntermediateWriter();
        Program intermediate = intermediateWriter.write(new Sanitizer().classify(Paths.get(args[0]).toFile().getName().split("\\.")[0]), program, result.getTree(), storage);
        new JavaSourceWriter().writeToFile(intermediate, new File("src/main/java"), storage);

    }
}
