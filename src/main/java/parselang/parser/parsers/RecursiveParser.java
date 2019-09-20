package parselang.parser.parsers;

import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

public class RecursiveParser extends Parser{

    private int farthestParse;

    @Override
    public synchronized ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException {
        farthestParse = 0;
        ParseResult res =  parse(originalString, 0, toParseTo, storage);
        if (res.getRemainingIndex() < originalString.length()) {
            throw new ParseErrorException(originalString, farthestParse);
        }
        return res;
    }

    private ParseResult parse(String originalString, int notYetParsed, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException  {
        if (originalString.length() < notYetParsed) {
            throw new ParseErrorException(originalString, notYetParsed);
        }
        System.out.println(originalString.substring(notYetParsed).replace("\n", "").replace("\r", "") + "                                      " + toParseTo);

        if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
             for (ParseRule ruleToTry : storage.getByNonTerminal(toParseToNT, notYetParsed == originalString.length() ? null : originalString.charAt(notYetParsed))) {
                try {
                    return parseWithRule(originalString, notYetParsed, ruleToTry, storage);
                } catch (ParseErrorException ignored) {
                }
            }
            throw new ParseErrorException(originalString, notYetParsed);
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(originalString, notYetParsed, (Terminal) toParseTo);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private ParseResult parseTerminal(String originalString, int notYetParsed, Terminal toParseTo) throws ParseErrorException {
        int size = toParseTo.getValue().length();
        if (originalString.length() <= notYetParsed || originalString.charAt(notYetParsed) == toParseTo.getValue().charAt(0)) {
            AST tree = new AST(toParseTo);
            tree.setParsed(originalString, notYetParsed, notYetParsed + size);
            farthestParse = notYetParsed + size;
            return new ParseResult(originalString, notYetParsed + size, tree);
        } else {
            throw new ParseErrorException(originalString, notYetParsed);
        }
    }

    private ParseResult parseWithRule(String originalString, int notYetParsed, ParseRule ruleToTry, ParseRuleStorage storage) throws ParseErrorException {
        int newlyParsed = notYetParsed;
        AST res = new AST(ruleToTry.getLHS(), storage);
        for (Node node : ruleToTry.getRHS()) {
            if (node instanceof NonTerminal || node instanceof Terminal) {
                ParseResult subResult = parse(originalString, newlyParsed, node, storage);
                newlyParsed = subResult.getRemainingIndex();
                res.addChild(subResult.getTree());
            }
        }
        res.setParsed(originalString, notYetParsed, newlyParsed);
        return new ParseResult(originalString, newlyParsed, res, ruleToTry);
    }


}
