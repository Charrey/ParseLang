package parselang.parser.parsers;

import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.ASTElem;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;

import java.util.List;
import java.util.logging.Logger;

public abstract class Parser {

    public abstract List<Node> extractNodes(String originalString, ASTElem nodeContainer);


    abstract ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException;

    public ParseResult readFile(ParseRuleStorage storage, String fileContents, NonTerminal toplevel) throws ParseErrorException {
        ParseResult parsed = parse(fileContents, toplevel, storage, toplevel);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(fileContents, fileContents.length() - remainingLength);
        }
        return parsed;
    }

    Logger log = Logger.getLogger(RecursiveParser.class.getName());

}
