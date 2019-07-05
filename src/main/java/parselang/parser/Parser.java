package parselang.parser;

import parselang.languages.ParseLangV1;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

import java.util.logging.Logger;

public class Parser {

    Logger log = Logger.getLogger(Parser.class.getName());


    public ParseResult readFile(ParseRuleStorage storage, String fileContents) throws ParseErrorException {
        storage.setDefaults(new ParseLangV1());
        storage.calculateFirst();
        storage.calculateFollow(new NonTerminal("HighLevel"));
        storage.calculateFirstPlus();
        storage.removeLeftRecursion();
        ParseResult parsed = parse(fileContents, fileContents, new NonTerminal("HighLevel"), storage);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(fileContents, fileContents.length() - remainingLength);
        }
        return parsed;
    }

    public AST readCommand(ParseRuleStorage storage, String command) throws ParseErrorException {
        ParseResult parsed = parse(command, command, new NonTerminal("Expression"), storage);
        System.out.println(parsed);
        return parsed.getTree();
    }

//    public void readHighLevel(ParseRuleStorage storage, String highLevelContents) throws ParseErrorException {
//
//    }

    private ParseResult parse(String originalString, String toParse, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException  {
         if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
            for (ParseRule ruleToTry : storage.getByNonTerminal(toParseToNT, toParse.isEmpty()? null : toParse.charAt(0))) {
                try {
                    return parseWithRule(originalString, toParse, ruleToTry, storage);
                } catch (ParseErrorException ignored) {}
            }

            throw new ParseErrorException(originalString, originalString.length() - toParse.length());
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(originalString, toParse, (Terminal) toParseTo);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private ParseResult parseTerminal(String originalString, String toParse, Terminal toParseTo) throws ParseErrorException {
        int size = toParseTo.getValue().length();
        if (toParse.startsWith(toParseTo.getValue())) {
            AST tree = new AST(toParseTo);
            tree.setParsed(toParse.substring(0, size));
            return new ParseResult(toParse.substring(0, size), toParse.substring(size), tree);
        } else {
            throw new ParseErrorException(originalString, originalString.length() - toParse.length());
        }
    }

    private ParseResult parseWithRule(String originalString, String toParse, ParseRule ruleToTry, ParseRuleStorage storage) throws ParseErrorException {
        String remaining = toParse;
        AST res = new AST(ruleToTry.getLHS(), storage);
        for (Node node : ruleToTry.getRHS()) {
            if (node instanceof NonTerminal || node instanceof Terminal) {
                ParseResult subResult = parse(originalString, remaining, node, storage);
                remaining = subResult.getRemaining();
                res.addChild(subResult.getTree());
            }
        }
        String parsed = toParse.substring(0, toParse.length() - remaining.length());
        res.setParsed(parsed);
        return new ParseResult(parsed, remaining, res, ruleToTry);
    }
}
