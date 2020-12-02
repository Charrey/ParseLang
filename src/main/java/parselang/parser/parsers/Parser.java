package parselang.parser.parsers;

import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.exceptions.ParseErrorException;

/**
 * Literally, a parser. Has methods that provide parsing functionality
 */
public abstract class Parser {

    protected int verbosity = 1;

    /**
     * Parses a String as far as possible.
     * @param originalString String to parse
     * @param toParseTo node that is next to be parsed
     * @param storage storage of parse rules that should be used
     * @return an object that contains an AST and information on how successful the parse was
     * @throws ParseErrorException thrown when the string could not (with packrat techniques) be parsed into the nonterminal.
     */
    abstract ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException;

    /**
     * Parses a String completely or throws an exception for the user indicating what went wrong.
     * @param originalString String to parse
     * @param storage storage of parse rules that should be used
     * @param toplevel nonterminal that represents the entire string to parse
     * @return an object that contains an AST and information on how successful the parse was
     * @throws ParseErrorException thrown when the string could not (with packrat techniques) be parsed into the toplevel nonterminal.
     */
    public ParseResult readFile(String originalString, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException {
        ParseResult parsed = parse(originalString, toplevel, storage);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(originalString, originalString.length() - remainingLength);
        }
        return parsed;
    }

    /**
     * Sets the verbosity level of this parser. 0 is off, 1 is everything.
     * @param level verbosity level
     */
    public void setVerbosity(int level) {
        this.verbosity = level;
    }
}
