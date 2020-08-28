package parselang.util;

import parselang.parser.data.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static parselang.parser.ParseRuleStorage.*;

public class NodeExtractor {

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
        if (!(nodeContainer.getRoot() instanceof NonTerminal)) {
            return Collections.emptyList();
        }
        String rootName = ((NonTerminal) nodeContainer.getRoot()).getName();
        if (rootName.equals("Token")) {
            AST firstChild = (AST) nodeContainer.getChild(0);
            NonTerminal firstChildRoot = (NonTerminal) firstChild.getRoot();
            List<Node> token = extractNodesFromTokenChild(originalString, firstChild);
            List<Node> res;
            AST potentialStarNode = (AST) nodeContainer.getChild(1);
            boolean hasStar = potentialStarNode.getChildren().size() == 1;
            res = hasStar ? Collections.singletonList(star(token)) : token; //todo: star nodes should be able to be bound
            if (firstChildRoot.getName().equals("Terminal")) {
                return res;
            } else if (firstChildRoot.getName().equals("NonTerminal")) {
                AST potentialVariable = ((AST)nodeContainer.getChild(3));
                if (potentialVariable.getChildren().size() > 0) {
                    AST variable = (AST) potentialVariable.getChild(0);
                    boolean lazy = isLazy(variable);
                    String name = variable.subString(originalString);
                    String trimmedname = lazy ? name.substring(0, name.length()-1) : name;
                    return res.stream().map((Function<Node, Node>) node -> bound(node, trimmedname, lazy)).collect(Collectors.toList());
                } else {
                    return res;
                }
            } else if (firstChildRoot.getName().equals("StringLiteral")) {
                return res;
            } else if (firstChildRoot.getName().equals("BracketToken")){
                AST potentialVariable = ((AST)nodeContainer.getChild(3));
                if (potentialVariable.getChildren().size() > 0) {
                    AST variable = (AST) potentialVariable.getChild(0);
                    boolean lazy = isLazy(variable);
                    String name = variable.subString(originalString);
                    String trimmedname = lazy ? name.substring(0, name.length()-1) : name;
                    return res.stream().map((Function<Node, Node>) node -> bound(node, trimmedname, lazy)).collect(Collectors.toList());
                } else {
                    return res;
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else if (rootName.contains("Token")) {
            List<Node> res = new LinkedList<>();
            for (ASTElem child : nodeContainer.getChildren()) {
                List<Node> subNodes = extractNodes(originalString, child);
                res.addAll(subNodes);
            }
            return res;
        } else {
            return new LinkedList<>();
        }
    }

    private boolean isLazy(AST variable) {
        assert variable.getRoot().equals(nonTerm("Variable"));
        AST potentialLazy = (AST) variable.getChild(2);
        return potentialLazy.getChildren().size() > 0;
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
                return extractNodes(originalString, tokenChild);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
