package parselang.parser.parsers;


import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.TreeFixer;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.DeclarationTree;
import parselang.util.Pair;

import java.util.ArrayDeque;
import java.util.Deque;

import static parselang.parser.ParseRuleStorage.nonTerm;
import static parselang.parser.ParseRuleStorage.term;

public class RecursiveParser extends Parser{

    private int farthestParse;
    private final TreeFixer treeFixer = new TreeFixer();

    private final MaxSizeDoubleMap<Integer, Node, ParseResult> memo           = new MaxSizeDoubleMap<>(1000);
    private final MaxSizeDoubleMap<Integer, ParseRule, Integer> recursionDepth     = new MaxSizeDoubleMap<>(-1);

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
        if (memo.contains(notYetParsed, toParseTo)) {
            return memo.get(notYetParsed, toParseTo);
        }
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
                    } else if (toParseTo.equals(nonTerm("Variable"))) {
                        addParameter(originalString, res.getTree(), storage, toplevel);
                    }
                    memo.add(notYetParsed, toParseTo, res);
                    return res;
                } catch (ParseErrorException ignored) {
                }
            }
            throw new ParseErrorException();
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(originalString, notYetParsed, (Terminal) toParseTo);
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private void addParameter(String originalString, AST tree, ParseRuleStorage storage, NonTerminal toplevel) {
        boolean lazy = ((AST)tree.getLastChild()).getChildren().size() == 1;
        ParseRule ruleToAdd;
        if (lazy) {
            ruleToAdd = new ParseRule("ParameterName").addRhs(term(originalString.substring(tree.getParsedFrom(), tree.getParsedTo() - 1)));
        } else {
            ruleToAdd = new ParseRule("ParameterName").addRhs(term(tree.subString(originalString)));
        }
        storage.addTemporary(ruleToAdd, toplevel);
    }

    private void updateGrammar(String originalString, AST declaration, ParseRuleStorage storage, NonTerminal toplevel) {
        DeclarationTree declTree = new DeclarationTree(originalString, declaration);
        ParseRule inheritanceRule = new ParseRule(declTree.superNonTerminal).addRhs(nonTerm(declTree.name));
        ParseRule ruleToAdd2 = new ParseRule(declTree.name).addRhs(declTree.retrievedNodes.toArray(new Node[0]));
        storage.addCustomRules(declaration, new Pair<>(inheritanceRule, declTree.direction), new Pair<>(ruleToAdd2, Direction.RIGHT), toplevel);
        storage.purgeTemporary("Parameter", toplevel);
    }



    private ParseResult parseTerminal(String originalString, int notYetParsed, Terminal toParseTo) throws ParseErrorException {
        if (memo.contains(notYetParsed, toParseTo)) {
            return memo.get(notYetParsed, toParseTo);
        }
        int size = toParseTo.getValue().length();
        if (originalString.length() <= notYetParsed || (originalString.charAt(notYetParsed) == toParseTo.getValue().charAt(0) && subStringStartsWith(originalString, notYetParsed, toParseTo.getValue()))) {
            AST tree = new AST(toParseTo);
            tree.setParsed(originalString, notYetParsed, notYetParsed + size);
            farthestParse = Math.max(farthestParse, notYetParsed + size);
            ParseResult res = new ParseResult(originalString, notYetParsed + size, tree);
            memo.add(notYetParsed, toParseTo, res);
            return res;
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
        if (recursionDepth.contains(notYetParsed, ruleToTry)) {
            throw new UnsupportedOperationException("yaay");
        }
        recursionDepth.computeIfAbsent(notYetParsed, ruleToTry, a -> 0);
        recursionDepth.update(notYetParsed, ruleToTry, integer -> integer + 1);
        //System.out.println(recursionDepth.get(notYetParsed, ruleToTry));
        int newlyParsed = notYetParsed;
        AST ast = new AST(ruleToTry.getLHS(), storage);
        Deque<Node> toTry = new ArrayDeque<>(ruleToTry.getRHS());
        try {
            while (!toTry.isEmpty()) {
                Node node = toTry.pop();
                if (node instanceof NonTerminal || node instanceof Terminal) {
                    ParseResult subResult = parse(originalString, newlyParsed, node, storage, toplevel);
                    newlyParsed = subResult.getRemainingIndex();
                    ast.addChild(subResult.getTree());
                } else if (node instanceof BoundNonTerminal) {
                    toTry.push(((BoundNonTerminal) node).getContent());
                    //throw new UnsupportedOperationException();
                }
            }
        } finally {
            recursionDepth.update(notYetParsed, ruleToTry, integer -> integer - 1);
        }
        ast.setParsed(originalString, notYetParsed, newlyParsed);
        ParseResult res = new ParseResult(originalString, newlyParsed, ast, ruleToTry);
        memo.add(notYetParsed, ruleToTry.getLHS(), res);
        return res;
    }


}
