package parselang.parser.parsers;

import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.AST;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;

import java.util.logging.Logger;

public abstract class Parser {

    abstract ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException;

    public ParseResult readFile(ParseRuleStorage storage, String fileContents) throws ParseErrorException {
        ParseResult parsed = parse(fileContents, new NonTerminal("HighLevel"), storage);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(fileContents, fileContents.length() - remainingLength);
        }
        return parsed;
    }

    public AST readCommand(ParseRuleStorage storage, String command) throws ParseErrorException {
        ParseResult parsed = parse(command, new NonTerminal("Expression"), storage);
        return parsed.getTree();
    }

    Logger log = Logger.getLogger(RecursiveParser.class.getName());

}