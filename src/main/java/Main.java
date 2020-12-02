import parselang.interpreter.Interpreter;
import parselang.languages.ParseLangV1;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.NonTerminal;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static parselang.parser.ParseRuleStorage.nonTerm;

public class Main {

    public static void main(String[] args) throws IOException {
        runDemo("examples/doublequotes.plang");
        runDemo("examples/forloop.plang");
        runDemo("examples/madness.plang");
        runDemo("examples/gcd.plang");
        runDemo("examples/concat.plang");
    }

    private static void runDemo(String path) throws IOException {
        String program = new String(Files.readAllBytes(Paths.get(path)));
        System.out.println("Interpreting program \"" + Paths.get(path).getFileName().toFile().getName() + "\"...");
        Parser parser = new RecursiveParser();
        parser.setVerbosity(0);
        ParseRuleStorage storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1(), new NonTerminal("HighLevel", false));
        try {
            ParseResult result = parser.readFile(program, storage, nonTerm("HighLevel"));
            System.out.println("> " + new Interpreter().run(result.getTree()));
        } catch (Exception e) {
            System.out.println("> " + e.getMessage());
            e.printStackTrace();
        }
    }

}
