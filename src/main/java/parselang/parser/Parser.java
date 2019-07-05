package parselang.parser;

import parselang.languages.ParseLangV1;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

import java.util.logging.Logger;

public class Parser {

    Logger log = Logger.getLogger(Parser.class.getName());


    public ParseResult readFile(ParseRuleStorage storage, String fileContents) throws ParseErrorException {
        //storage.prepare(new ParseLangV1());
        ParseResult parsed = parse(fileContents, 0, new NonTerminal("HighLevel"), storage);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(fileContents, fileContents.length() - remainingLength);
        }
        return parsed;
    }

    public AST readCommand(ParseRuleStorage storage, String command) throws ParseErrorException {
        ParseResult parsed = parse(command, 0, new NonTerminal("Expression"), storage);
        System.out.println(parsed);
        return parsed.getTree();
    }

//    public void readHighLevel(ParseRuleStorage storage, String highLevelContents) throws ParseErrorException {
//
//    }

    private ParseResult parse(String originalString, int notYetParsed, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException  {
         if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
            for (ParseRule ruleToTry : storage.getByNonTerminal(toParseToNT, notYetParsed == originalString.length() ? null : originalString.charAt(notYetParsed))) {
                try {
                    return parseWithRule(originalString, notYetParsed, ruleToTry, storage);
                } catch (ParseErrorException ignored) {}
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
        if (originalString.charAt(notYetParsed) == toParseTo.getValue().charAt(0)) {
            AST tree = new AST(toParseTo);
            tree.setParsed(toParseTo.getValue());
            return new ParseResult(originalString, notYetParsed + toParseTo.getValue().length(), tree);
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
        String parsed = originalString.substring(notYetParsed, newlyParsed);
        res.setParsed(parsed);
        return new ParseResult(parsed, newlyParsed, res, ruleToTry);
    }
}
