import parselang.languages.ParseLangV1;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.UndefinedNontermException;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static parselang.interpreter.Interpreter.run;
import static parselang.parser.ParseRuleStorage.nonTerm;

public class Main {

    public static void main(String[] args) throws UndefinedNontermException, IOException {
        //runDemo("examples/doublequotes.plang");
        //runDemo("examples/test.plang");
        //runDemo("examples/gcd.plang");
        runDemo("examples/demo.plang");
        //runDemo("examples/concat.plang");
    }

    private static void runDemo(String path) throws IOException, UndefinedNontermException {
        String program = new String(Files.readAllBytes(Paths.get(path)));
        System.out.println("----------------------------------------------------------------");
        System.out.println(program);
        System.out.println("----------------------------------------------------------------");
        Parser parser = new RecursiveParser();
        parser.setVerbosity(0);
        ParseRuleStorage storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1(), new NonTerminal("HighLevel"));
        try {
            ParseResult result = parser.readFile(storage, program, nonTerm("HighLevel"));
            System.out.println("> " + run(program, result.getTree()));
        } catch (Exception e) {
            System.out.println("> " + e.getMessage());
            e.printStackTrace();
        }
    }

}
