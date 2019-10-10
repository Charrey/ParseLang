package parselang.parser.parsers;

import javafx.util.Pair;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.TreeFixer;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.DeclarationTree;
import parselang.util.NodeExtractor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static parselang.parser.ParseRuleStorage.*;

public class RecursiveParser extends Parser{

    private int farthestParse;
    private final TreeFixer treeFixer = new TreeFixer();
    private final NodeExtractor nodeExtractor = new NodeExtractor();

    @Override
    public synchronized ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException {
        start();
        farthestParse = 0;
        try {
            ParseResult res = parse(originalString, 0, toParseTo, storage, toplevel);
            res.setTree((AST) treeFixer.fix(res.getTree()));
            if (res.getRemainingIndex() < originalString.length()) {
                throw new ParseErrorException(originalString, farthestParse);
            }
            stop();
            return res;
        } catch (ParseErrorException e) {
            stop();
            throw new ParseErrorException(originalString, farthestParse);
        }
    }

    private ParseResult parse(String originalString, int notYetParsed, Node toParseTo, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException  {
        if (originalString.length() < notYetParsed) {
            throw new ParseErrorException();
        }
        if (verbosity >= 1) {
            System.out.println(originalString.substring(notYetParsed).replace("\n", "").replace("\r", "") + "                                      " + toParseTo);
        }
        if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
             for (ParseRule ruleToTry : storage.getByNonTerminal(toParseToNT, notYetParsed == originalString.length() ? null : originalString.charAt(notYetParsed))) {
                try {
                    ParseResult res =  parseWithRule(originalString, notYetParsed, ruleToTry, storage, toplevel);
                    if (toParseTo.equals(nonTerm("Declaration"))) {
                        updateGrammar(originalString, res.getTree(), storage, toplevel);
                    }
                    return res;
                } catch (ParseErrorException ignored) {
                }
            }
            throw new ParseErrorException();
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(originalString, notYetParsed, (Terminal) toParseTo);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private void updateGrammar(String originalString, AST declaration, ParseRuleStorage storage, NonTerminal toplevel) {
        DeclarationTree declTree = new DeclarationTree(originalString, declaration);
        ParseRule inheritanceRule = new ParseRule(declTree.superNonTerminal).addRhs(nonTerm(declTree.name));
        ParseRule ruleToAdd2 = new ParseRule(declTree.name).addRhs(declTree.retrievedNodes.toArray(new Node[0]));
        storage.addCustomRules(declaration, new Pair<>(inheritanceRule, declTree.direction), new Pair<>(ruleToAdd2, Direction.RIGHT), toplevel);
    }



    private ParseResult parseTerminal(String originalString, int notYetParsed, Terminal toParseTo) throws ParseErrorException {
        int size = toParseTo.getValue().length();
        if (originalString.length() <= notYetParsed || (originalString.charAt(notYetParsed) == toParseTo.getValue().charAt(0) && subStringStartsWith(originalString, notYetParsed, toParseTo.getValue()))) {
            AST tree = new AST(toParseTo);
            tree.setParsed(originalString, notYetParsed, notYetParsed + size);
            farthestParse = Math.max(farthestParse, notYetParsed + size);
            return new ParseResult(originalString, notYetParsed + size, tree);
        } else {
            throw new ParseErrorException();
        }
    }

    private boolean subStringStartsWith(String originalString, int notYetParsed, String value) {
        //return originalString.substring(notYetParsed).startsWith(value);
        for (int i = 0; i < value.length(); i++) {
            if (originalString.charAt(i + notYetParsed) != value.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private ParseResult parseWithRule(String originalString, int notYetParsed, ParseRule ruleToTry, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException {
        int newlyParsed = notYetParsed;
        AST res = new AST(ruleToTry.getLHS(), storage);
        for (Node node : ruleToTry.getRHS()) {
            if (node instanceof NonTerminal || node instanceof Terminal) {
                ParseResult subResult = parse(originalString, newlyParsed, node, storage, toplevel);
                newlyParsed = subResult.getRemainingIndex();
                res.addChild(subResult.getTree());
            }
        }
        res.setParsed(originalString, notYetParsed, newlyParsed);
        return new ParseResult(originalString, newlyParsed, res, ruleToTry);
    }


}
