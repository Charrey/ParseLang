package parselang.parser.data;

import java.util.*;

public class ParseRule {

    private final NonTerminal lhs;
    private List<Node> rhs = new LinkedList<>();
    private Evaluator evaluator;

    private ParseRule(NonTerminal lhs) {
        this.lhs = lhs;
    }

    public ParseRule(String lhs) {
        this.lhs = new NonTerminal(lhs);
    }

    public NonTerminal getLHS() {
        return lhs;
    }

    public void setRhs(List<Node> rhs) {
        this.rhs = rhs;
    }

    public ParseRule addRhs(Node node) {
        rhs.add(node);
        return this;
    }

    public ParseRule addRhs(Node... nodes) {
        rhs.addAll(Arrays.asList(nodes));
        return this;
    }

    public List<Node> getRHS() {
        return rhs;
    }

    private void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(lhs.toString());
        sb.append(" = ");
        for (Node node : rhs) {
            sb.append(node).append(" ");
        }
        return sb.toString();
    }

    public List<ParseRule> convertStarNodes() {
        List<ParseRule> res = new ArrayList<>();
        ParseRule copy = this.copy();
        for (Node rhsNode : getRHS()) {
            if (rhsNode instanceof StarNode) {
                NonTerminal replacement = new NonTerminal("(" + rhsNode.toString() + ")");//NonTerminal.getNext();
                copy.replaceRHSNodes(rhsNode, replacement);
                ParseRule recursiveReplacement = new ParseRule(replacement);
                for (Node starContent : ((StarNode) rhsNode).contents()) {
                    recursiveReplacement.addRhs(starContent);
                }
                recursiveReplacement.addRhs(replacement);
                res.addAll(recursiveReplacement.convertStarNodes());
                res.add(new ParseRule(replacement));
            }
        }
        res.add(copy);
        return res;

    }

    private void replaceRHSNodes(Node rhsNode, NonTerminal replacement) {
        for (int i = 0; i < rhs.size(); i++) {
            if (rhs.get(i).equals(rhsNode)) {
                rhs.set(i, replacement);
            }
        }
    }

    private ParseRule copy() {
        ParseRule res = new ParseRule(this.lhs);
        res.addRhs(this.rhs.toArray(new Node[0]));
        res.setEvaluator(this.evaluator);
        return res;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ParseRule)) {
            return false;
        }
        if (!lhs.equals(((ParseRule) other).lhs)) {
            return false;
        }
        if (!rhs.equals(((ParseRule) other).rhs)) {
            return false;
        }

        return Objects.equals(evaluator, ((ParseRule) other).evaluator);
    }
}
