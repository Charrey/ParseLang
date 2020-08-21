import parselang.languages.ParseLangV1;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.UndefinedNontermException;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.parser.parsers.Parser;
import parselang.parser.parsers.RecursiveParser;

import static parselang.interpreter.Interpreter.run;
import static parselang.parser.ParseRuleStorage.nonTerm;

public class Main {

    public static void main(String[] args) throws UndefinedNontermException, ParseErrorException {

        String program =
                        "NewSafeSpecial < Nothing =  '}' {'}'}\n" +
                        "NewSafeSpecial < Nothing =  '{' {'{'}\n" +
                        "NewSafeSpecial < Nothing =  '(' {'('}\n" +
                        "NewSafeSpecial < Nothing =  ')' {')'}\n" +
                        "NewSafeSpecial < Nothing =  ';' {';'}\n" +
                        "NewSafeSpecial < Nothing =  '+' {'+'}\n" +
                        "NewSafeSpecial < Nothing =  '*' {'*'}\n" +
                        "NewSafeSpecial < Nothing =  '/' {'/'}\n" +
                        "NewSafeSpecial < Nothing =  '-' {'-'}\n" +
                        "NewSafeSpecial < Nothing =  '!' {'!'}\n" +
                        "NewSafeChar < Nothing = UpperOrLowerCase a {a}\n" +
                        "NewSafeChar < Nothing = Number a {a}\n" +
                        "NewSafeChar < Nothing = NewSafeSpecial a {a}\n" +
                        "NewStringLiteral < SimpleExpression = '\"' NewSafeChar* a '\"' {a}" +

                        "\"3afoo\"";


        System.out.println("Program:");
        System.out.println(program);
        Parser parser = new RecursiveParser();
        parser.setVerbosity(1);
        ParseRuleStorage storage = new ParseRuleStorage();
        storage.prepare(new ParseLangV1(), new NonTerminal("HighLevel"));
        ParseResult result = parser.readFile(storage, program, nonTerm("HighLevel"));
        System.out.println(run(program, result.getTree()));
    }

}
