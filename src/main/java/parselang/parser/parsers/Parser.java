package parselang.parser.parsers;

import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.ASTElem;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.TimedClass;

import java.util.List;
import java.util.logging.Logger;

public abstract class Parser extends TimedClass {

    protected int verbosity = 1;


    abstract ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException;

    public ParseResult readFile(ParseRuleStorage storage, String fileContents, NonTerminal toplevel) throws ParseErrorException {
        ParseResult parsed = parse(fileContents, toplevel, storage, toplevel);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(fileContents, fileContents.length() - remainingLength);
        }
        return parsed;
    }

    public void setVerbosity(int level) {
        this.verbosity = level;
    }
}
