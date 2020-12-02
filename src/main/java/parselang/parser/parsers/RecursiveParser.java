package parselang.parser.parsers;


import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.TreeFixer;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.DeclarationTree;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import static parselang.parser.ParseRuleStorage.*;

/**
 * Packrat parser implementation that uses recursion to evaluate nonterminals in the right hand side of parse rules
 */
public class RecursiveParser extends Parser{

    private int farthestParse;
    private final TreeFixer treeFixer = new TreeFixer();
    private final MaxSizeDoubleMap<Integer, Node, ParseResult> memo = new MaxSizeDoubleMap<>(1000);

    /**
     * @inheritDoc
     */
    @Override
    public synchronized ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException {
        farthestParse = 0;
        try {
            ParseResult res = parse(originalString, 0, toParseTo, storage);
            res.setTree((AST) treeFixer.fix(res.getTree()));
            if (res.getRemainingIndex() < originalString.length()) {
                throw new ParseErrorException(originalString, farthestParse);
            }
            return res;
        } catch (ParseErrorException e) {
            e.printStackTrace();
            throw new ParseErrorException(originalString, farthestParse);
        }
    }

    private ParseResult parse(String originalString, int notYetParsed, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException  {
        if (memo.contains(notYetParsed, toParseTo)) {
            return memo.get(notYetParsed, toParseTo);
        }
        if (originalString.length() < notYetParsed) {
            throw new ParseErrorException();
        }
        if (verbosity >= 1) {
            System.out.println(toParseTo + " ".repeat(100 - toParseTo.toString().length()) + originalString.substring(notYetParsed).replace("\n", "").replace("\r", ""));
        }
        if (toParseTo instanceof NonTerminal) {
            NonTerminal toParseToNT = (NonTerminal) toParseTo;
            storage.registerNonTerminal(toParseToNT);
            Collection<ParseRule> rulesToTry = storage.getByNonTerminal(toParseToNT, notYetParsed == originalString.length() ? null : originalString.charAt(notYetParsed));
             for (ParseRule ruleToTry : rulesToTry) {
                try {
                    ParseResult res =  parseWithRule(originalString, notYetParsed, ruleToTry, storage);
                    if (toParseTo.equals(nonTerm("Variable"))) {
                        addParameter(originalString, res.getTree(), storage);
                    } else if (toParseTo.equals(nonTerm("NonTerminal"))) {
                        addNonTerminalName(res.getTree(), storage);
                    } else if (toParseToNT.getName().equals("Declaration")) {
                        storage.removeParameters();
                    }
                    memo.put(notYetParsed, toParseTo, res);
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

    private void addParameter(String originalString, AST tree, ParseRuleStorage storage) {
        boolean lazy = ((AST)tree.getLastChild()).getChildren().size() == 1;
        String ruleToAdd = lazy ? originalString.substring(tree.getParsedFrom(), tree.getParsedTo() - 1) : tree.parseString();
        storage.addParameter(ruleToAdd);
    }

    private void addNonTerminalName(AST tree, ParseRuleStorage storage) {
        storage.registerNonTerminal(new NonTerminal(tree.parseString(), false));
    }

    private void updateGrammar(AST declaration, ParseRuleStorage storage) {
        DeclarationTree declTree = new DeclarationTree(declaration);
        ParseRule inheritanceRule = new ParseRule(declTree.getSuperNonTerminal()).addRhs(nonTerm(declTree.getName()));
        ParseRule ruleToAdd2 = new ParseRule(declTree.getName()).addRhs(declTree.getRetrievedNodes().toArray(new Node[0]));
        storage.addCustomRules(inheritanceRule, declTree.getDirection(), ruleToAdd2);
    }

    private ParseResult parseTerminal(String originalString, int notYetParsed, Terminal toParseTo) throws ParseErrorException {
        if (memo.contains(notYetParsed, toParseTo)) {
            return memo.get(notYetParsed, toParseTo);
        }
        int size = toParseTo.getValue().length();
        if (originalString.length() <= notYetParsed || (originalString.charAt(notYetParsed) == toParseTo.getValue().charAt(0) && subStringStartsWith(originalString, notYetParsed, toParseTo.getValue()))) {
            AST tree = new AST(toParseTo, originalString);
            tree.setParsed(notYetParsed, notYetParsed + size);
            farthestParse = Math.max(farthestParse, notYetParsed + size);
            ParseResult res = new ParseResult(originalString, tree);
            memo.put(notYetParsed, toParseTo, res);
            return res;
        } else {
            throw new ParseErrorException();
        }
    }

    private boolean subStringStartsWith(String originalString, int notYetParsed, String value) {
        for (int i = 0; i < value.length(); i++) {
            if (originalString.charAt(i + notYetParsed) != value.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private ParseResult parseWithRule(String originalString, int notYetParsed, ParseRule ruleToTry, ParseRuleStorage storage) throws ParseErrorException {
        int newlyParsed = notYetParsed;
        AST ast = new AST(ruleToTry.getLHS(), originalString);
        Deque<Node> toTry = new ArrayDeque<>(ruleToTry.getRHS());
        while (!toTry.isEmpty()) {
            Node node = toTry.pop();
            if (node instanceof NonTerminal && ((NonTerminal) node).getName().equals("DeclarationContent") && ruleToTry.getOrigin().equals(new ParseRule("Declaration").addRhs(nonTerm("NonTerminal"), ws(), nonTerm("GTorLT"), ws(), nonTerm("NonTerminal"), ws(), term("="), star(ws(), nonTerm("Token")), ws(), term("{"), ws(), nonTerm("DeclarationContent"), ws(), term("}")))) {
                updateGrammar(ast, storage);
            }
            if (node instanceof NonTerminal || node instanceof Terminal) {
                ParseResult subResult = parse(originalString, newlyParsed, node, storage);
                newlyParsed = subResult.getRemainingIndex();
                ast.addChild(subResult.getTree());
            } else if (node instanceof BoundNode) {
                toTry.push(((BoundNode) node).getContent());
            }
        }
        ast.setParsed(notYetParsed, newlyParsed);
        ast.setRuleApplied(ruleToTry);
        ParseResult res = new ParseResult(originalString, ast);
        memo.put(notYetParsed, ruleToTry.getLHS(), res);
        return res;
    }
}
