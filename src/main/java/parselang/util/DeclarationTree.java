package parselang.util;

import parselang.parser.data.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static parselang.parser.ParseRuleStorage.*;

/**
 * Utility class for extracting a parse rule from a declaration
 */
public class DeclarationTree {

    private final String name;
    private final Direction direction;
    private final String superNonTerminal;
    private final List<Node> retrievedNodes;

    /**
     * Creates a new DeclarationTree, performing all calculations.
     * @param declaration declaration to extract nodes from
     */
    public DeclarationTree(AST declaration) {
        this.name = declaration.getChild(0).parseString();
        this.direction = declaration.getChild(2).parseString().equals("<") ? Direction.LEFT : Direction.RIGHT;
        this.superNonTerminal = declaration.getChild(4).parseString();
        this.retrievedNodes = Collections.unmodifiableList(extractNodes(declaration.getChild(7)));
    }

    /**
     * Provides the name of the NonTerminal that this new rule extends
     * @return the name
     */
    public String getSuperNonTerminal() {
        return superNonTerminal;
    }

    /**
     * Provides the name of the NonTerminal of the left hand side of this rule
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of nodes for the right hand side of the rule
     * @return a list of nodes
     */
    public List<Node> getRetrievedNodes() {
        return retrievedNodes;
    }

    /**
     * Direction to which the rule was requested to be added
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    private List<Node> extractNodes(ASTElem nodeContainer) {
        if (nodeContainer instanceof AST) {
            return extractNodesFromAST((AST) nodeContainer);
        } else if (nodeContainer instanceof ASTElemList) {
            return extractNodesFromASTElemList((ASTElemList) nodeContainer);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private List<Node> extractNodesFromASTElemList(ASTElemList nodeContainer) {
        List<List<Node>> nodesOfList = nodeContainer.stream().map(astElem -> extractNodes(astElem)).collect(Collectors.toList());
        Stream<Node> stream = Stream.of();
        for (List<Node> nodesList : nodesOfList)
            stream = Stream.concat(stream, nodesList.stream());
        return stream.collect(Collectors.toList());
    }

    private List<Node> extractNodesFromAST(AST nodeContainer) {
        if (!(nodeContainer.getRoot() instanceof NonTerminal)) {
            return Collections.emptyList();
        }
        String rootName = ((NonTerminal) nodeContainer.getRoot()).getName();
        if (rootName.equals("Token")) {
            AST firstChild = (AST) nodeContainer.getChild(0);
            NonTerminal firstChildRoot = (NonTerminal) firstChild.getRoot();
            List<Node> token = extractNodesFromTokenChild(firstChild);
            List<Node> res;
            AST potentialStarNode = (AST) nodeContainer.getChild(1);
            boolean hasStar = potentialStarNode.getChildren().size() == 1;
            res = hasStar ? Collections.singletonList(star(token)) : token; //todo: star nodes should be able to be bound
            switch (firstChildRoot.getName()) {
                case "Terminal":
                case "StringLiteral":
                    return res;
                case "NonTerminal":
                case "BracketToken": {
                    AST potentialVariable = ((AST) nodeContainer.getChild(3));
                    if (potentialVariable.getChildren().size() > 0) {
                        AST variable = (AST) potentialVariable.getChild(0);
                        boolean lazy = isLazy(variable);
                        String name = variable.parseString();
                        String trimmedname = lazy ? name.substring(0, name.length() - 1) : name;
                        return res.stream().map((Function<Node, Node>) node -> bound(node, trimmedname, lazy)).collect(Collectors.toList());
                    } else {
                        return res;
                    }
                }
                default:
                    throw new UnsupportedOperationException();
            }
        } else if (rootName.contains("Token")) {
            List<Node> res = new LinkedList<>();
            for (ASTElem child : nodeContainer.getChildren()) {
                List<Node> subNodes = extractNodes(child);
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

    private List<Node> extractNodesFromTokenChild(AST tokenChild) {
        String type = ((NonTerminal)tokenChild.getRoot()).getName();
        switch (type) {
            case "StringLiteral":
                String stringLiteral = tokenChild.parseString();
                stringLiteral = stringLiteral.substring(1, stringLiteral.length()-1); //remove quotation marks
                return Collections.singletonList(term(stringLiteral));
            case "NonTerminal":
                return Collections.singletonList(nonTerm(tokenChild.parseString()));
            case "BracketToken":
                return extractNodes(tokenChild);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
