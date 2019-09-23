package parselang.parser.parsers;

import javafx.util.Pair;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.TreeFixer;
import parselang.parser.data.*;
import parselang.parser.exceptions.ParseErrorException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static parselang.parser.ParseRuleStorage.*;

public class RecursiveParser extends Parser{

    private int farthestParse;
    private TreeFixer treeFixer = new TreeFixer();

    @Override
    public synchronized ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException {
        farthestParse = 0;
        ParseResult res =  parse(originalString, 0, toParseTo, storage, toplevel);
        res.setTree((AST) treeFixer.fix(res.getTree()));
        if (res.getRemainingIndex() < originalString.length()) {
            throw new ParseErrorException(originalString, farthestParse);
        }
        return res;
    }

    private ParseResult parse(String originalString, int notYetParsed, Node toParseTo, ParseRuleStorage storage, NonTerminal toplevel) throws ParseErrorException  {
        if (originalString.length() < notYetParsed) {
            throw new ParseErrorException(originalString, notYetParsed);
        }
        System.out.println(originalString.substring(notYetParsed).replace("\n", "").replace("\r", "") + "                                      " + toParseTo);

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
            throw new ParseErrorException(originalString, notYetParsed);
        } else if (toParseTo instanceof Terminal) {
            return parseTerminal(originalString, notYetParsed, (Terminal) toParseTo);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private void updateGrammar(String originalString, AST tree, ParseRuleStorage storage, NonTerminal toplevel) {
        String newNonTerminal = ((AST)tree.getChild(0)).subString(originalString);
        Direction insertion = ((AST)tree.getChild(2)).subString(originalString).equals("<") ? Direction.LEFT : Direction.RIGHT;
        String superNonTerminal = ((AST)tree.getChild(4)).subString(originalString);
        List<Node> retrieveNodes = extractNodes(originalString, ((AST)tree.getChild(7)));

        ParseRule ruleToAdd1 = new ParseRule(superNonTerminal).addRhs(nonTerm(newNonTerminal));
        ParseRule ruleToAdd2 = new ParseRule(newNonTerminal).addRhs(retrieveNodes.toArray(new Node[0]));
        Set<Pair<ParseRule, Direction>> rulesToAdd = new HashSet<>();
        rulesToAdd.add(new Pair<>(ruleToAdd1, insertion));
        rulesToAdd.add(new Pair<>(ruleToAdd2, Direction.RIGHT));

        storage.addCustomRules(rulesToAdd, toplevel);
    }

    public List<Node> extractNodes(String originalString, ASTElem nodeContainer) {
        if (nodeContainer instanceof AST) {
            return extractNodesFromAST(originalString, (AST) nodeContainer);
        } else if (nodeContainer instanceof ASTElemList) {
            return extractNodesFromASTElemList(originalString, (ASTElemList) nodeContainer);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    private List<Node> extractNodesFromASTElemList(String originalString, ASTElemList nodeContainer) {
        List<List<Node>> nodesOfList = nodeContainer.stream().map(astElem -> extractNodes(originalString, astElem)).collect(Collectors.toList());
        Stream<Node> stream = Stream.of();
        for (List<Node> nodesList : nodesOfList)
            stream = Stream.concat(stream, nodesList.stream());
        return stream.collect(Collectors.toList());
    }
    private List<Node> extractNodesFromAST(String originalString, AST nodeContainer) {
        if (nodeContainer.getRoot() instanceof Terminal) {
            return Collections.emptyList();
        }
        String rootName = ((NonTerminal) nodeContainer.getRoot()).getName();
        if (rootName.equals("Token")) {
            List<Node> token = extractNodesFromTokenChild(originalString, (AST) nodeContainer.getChild(0));
            if (((AST)nodeContainer.getChild(1)).getChildren().size() == 1) {
                return  Collections.singletonList(star(token));
            } else {
                assert ((AST)nodeContainer.getChild(1)).getChildren().size() == 0;
                return token;
            }
        } else if (rootName.contains("Token")) {
            List<List<Node>> nodesOfChildren = nodeContainer.getChildren().stream().map(ast -> extractNodes(originalString, ast)).collect(Collectors.toList());
            Stream<Node> stream = Stream.of();
            for (List<Node> nodesList : nodesOfChildren)
                stream = Stream.concat(stream, nodesList.stream());
            return stream.collect(Collectors.toList());
        } else {
            return new LinkedList<>();
        }
    }

    private List<Node> extractNodesFromTokenChild(String originalString, AST tokenChild) {
        String type = ((NonTerminal)tokenChild.getRoot()).getName();
        switch (type) {
            case "StringLiteral":
                String stringLiteral = tokenChild.subString(originalString);
                stringLiteral = stringLiteral.substring(1, stringLiteral.length()-1); //remove quotation marks
                return Collections.singletonList(term(stringLiteral));
            case "NonTerminal":
                return Collections.singletonList(nonTerm(tokenChild.subString(originalString)));
            case "BracketToken":
                List<Node> innerTokens = extractNodes(originalString, tokenChild);
                return innerTokens;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }


    private ParseResult parseTerminal(String originalString, int notYetParsed, Terminal toParseTo) throws ParseErrorException {
        int size = toParseTo.getValue().length();
        if (originalString.length() <= notYetParsed || (originalString.charAt(notYetParsed) == toParseTo.getValue().charAt(0) && originalString.substring(notYetParsed).startsWith(toParseTo.getValue()))) {
            AST tree = new AST(toParseTo);
            tree.setParsed(originalString, notYetParsed, notYetParsed + size);
            farthestParse = Math.max(farthestParse, notYetParsed + size);
            return new ParseResult(originalString, notYetParsed + size, tree);
        } else {
            throw new ParseErrorException(originalString, notYetParsed);
        }
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
