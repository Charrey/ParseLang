package parselang.parser;

import parselang.languages.ParseLangV1;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Parser {

    Logger log = Logger.getLogger(Parser.class.getName());


    public void readFile(ParseRuleStorage storage, String fileContents) throws ParseErrorException {
        storage.setDefaults(new ParseLangV1());
        storage.calculateFirst();
        storage.calculateFollow(new NonTerminal("HighLevel"));
        storage.calculateFirstPlus();
        storage.removeLeftRecursion();
        ParseResult parsed = parse(fileContents, 0, fileContents, new NonTerminal("HighLevel"), storage);
        int remainingLength = parsed.getRemaining().length();
        if (remainingLength > 0) {
            throw new ParseErrorException(fileContents, fileContents.length() - remainingLength);
        }
        System.out.println(parsed);
    }

    public AST readCommand(ParseRuleStorage storage, String command) throws ParseErrorException {
        ParseResult parsed = parse(command, 0, command, new NonTerminal("Expression"), storage);
        System.out.println(parsed);
        return parsed.getTree();
    }

//    public void readHighLevel(ParseRuleStorage storage, String highLevelContents) throws ParseErrorException {
//
//    }

    private ParseResult parse(String originalString, int index, String toParse, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException  {
         if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
            List<ParseRule> applicableRules = new LinkedList<>(storage.getByNonTerminal(toParseToNT, toParse.isEmpty()? null : toParse.charAt(0)));
            while (!applicableRules.isEmpty()) {
                ParseRule ruleToTry = applicableRules.remove(0);
                try {
                    return parseWithRule(originalString, index, toParse, ruleToTry, storage);
                } catch (ParseErrorException ignored) {}
            }
            throw new ParseErrorException(originalString, index);
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(originalString, index, toParse, (Terminal) toParseTo);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private ParseResult parseTerminal(String originalString, int index, String toParse, Terminal toParseTo) throws ParseErrorException {
        int size = toParseTo.getValue().length();
        if (toParse.startsWith(toParseTo.getValue())) {
            AST tree = new AST(toParseTo);
            tree.setParsed(toParse.substring(0, size));
            return new ParseResult(toParse.substring(0, size), toParse.substring(size), tree);
        } else {
            throw new ParseErrorException(originalString, index);
        }
    }

    private ParseResult parseWithRule(String originalString, int index, String toParse, ParseRule ruleToTry, ParseRuleStorage storage) throws ParseErrorException {
        String remaining = toParse;
        int newindex = index;
        AST res = new AST(ruleToTry.getLHS(), storage);
        for (Node node : ruleToTry.getRHS()) {
            if (node instanceof NonTerminal || node instanceof Terminal) {
                ParseResult subResult = parse(originalString, newindex, remaining, node, storage);
                int oldlength = remaining.length();
                remaining = subResult.getRemaining();
                res.addChild(subResult.getTree());
                newindex += oldlength - remaining.length();
            }
        }
        res.setParsed(toParse.substring(0, toParse.length()-remaining.length()));
        return new ParseResult(toParse.substring(0, toParse.length()-remaining.length()), remaining, res, ruleToTry);
    }
}
