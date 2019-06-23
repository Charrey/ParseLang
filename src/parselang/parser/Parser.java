package parselang.parser;

import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Parser {

    Logger log = Logger.getLogger(Parser.class.getName());


    public void readFile(ParseRuleStorage storage, String fileContents) {
        //TODO
    }

    public AST readCommand(ParseRuleStorage storage, String command) {
        //TODO
        return null;
    }

    public void readHighLevel(ParseRuleStorage storage, String highLevelContents) throws ParseErrorException {
        storage.setDefaults();
        storage.calculateFirst();
        storage.calculateFollow();
        storage.calculateFirstPlus();
        storage.removeLeftRecursion();
        ParseResult parsed = parse(highLevelContents, new NonTerminal("HighLevel"), storage);
        System.out.println(parsed);
    }

    private ParseResult parse(String toParse, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException  {
        //System.out.println(toParseTo + "\t" + toParse.replaceAll("(\r\n)|(\n)", "\\\\n"));
        if (toParseTo instanceof StarNode) {
            return parseStarNode(toParse, (StarNode) toParseTo, storage);
        } else if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
            List<ParseRule> applicableRules = new LinkedList<>(storage.getByNonTerminal(toParseToNT));
            while (!applicableRules.isEmpty()) {
                ParseRule ruleToTry = applicableRules.remove(0);
                try {
                    return parseWithRule(toParse, ruleToTry, storage);
                } catch (ParseErrorException ignored) {}
            }
            throw new ParseErrorException();
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(toParse, (Terminal) toParseTo);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private ParseResult parseTerminal(String toParse, Terminal toParseTo) throws ParseErrorException {
        int size = toParseTo.getValue().length();
        if (toParse.startsWith(toParseTo.getValue())) {
            return new ParseResult(toParse.substring(size), new AST(toParseTo));
        } else {
            throw new ParseErrorException();
        }
    }

    private ParseResult parseWithRule(String toParse, ParseRule ruleToTry, ParseRuleStorage storage) throws ParseErrorException {
        String remaining = toParse;
        AST res = new AST(ruleToTry.getLHS(), storage);

        for (Node node : ruleToTry.getRHS()) {
            if (node instanceof NonTerminal || node instanceof Terminal) {
                ParseResult subResult = parse(remaining, node, storage);
                remaining = subResult.getRemaining();
                res.addChild(subResult.getTree());
            } else if (node instanceof StarNode) {
                ParseResult subResult = parseStarNode(remaining, (StarNode) node, storage);
                remaining = subResult.getRemaining();
                res.addChild(subResult.getTree());
            }
        }
        return new ParseResult(remaining, res, ruleToTry);
    }

    private ParseResult parseStarNode(String inputString, StarNode node, ParseRuleStorage storage) {
        String remaining = inputString;
        NodeList resultList = new NodeList();
        List<Node> contents = node.contents();

        while (true) {
            String stringCopy = remaining;
            NodeList tempResultList = new NodeList();
            try {
                for (Node subItem : contents) {
                    ParseResult parseAttempt = parse(stringCopy, subItem, storage);
                    stringCopy = parseAttempt.getRemaining();
                    tempResultList.add(parseAttempt.getTree());
                }
                if (remaining.equals(stringCopy)) {
                    return new ParseResult(remaining, resultList);
                }
                remaining = stringCopy;
                resultList.addAll(tempResultList);
            } catch (ParseErrorException e) {
                return new ParseResult(remaining, resultList);
            }

        }
    }


//    private void evaluateParseAssignments() throws ParseErrorException {
//        while (!parseAssignments.isEmpty()) {
//            ParseAssignment firstAssignment = parseAssignments.pop();
//            if (firstAssignment.fail()) {
//                throw new ParseErrorException();
//            } else {
//                List<ParseAssignment> requirements = firstAssignment.getRequirements();
//                parseAssignments.push(firstAssignment);
//                for (ParseAssignment toAdd : requirements) {
//                    parseAssignments.push(toAdd);
//                }
//            }
//        }
//    }
}
