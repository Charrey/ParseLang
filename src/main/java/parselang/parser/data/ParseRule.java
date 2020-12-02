package parselang.parser.data;

import java.util.*;

import static parselang.parser.ParseRuleStorage.bound;
import static parselang.parser.ParseRuleStorage.nonTerm;

/**
 * A parse rule used by the Parse Lang parser
 */
public class ParseRule {

    private final NonTerminal lhs;
    private final List<Node> rhs = new LinkedList<>();
    private ParseRule origin = this;

    /**
     * Creates a new parse rule from a nonterminal
     * @param lhs Left hand side of this rule
     */
    public ParseRule(NonTerminal lhs) {
        this.lhs = lhs;
    }

    /**
     * Creates a new parse rule from a string representing a nonterminal
      * @param lhs Left hand side of this rule
     */
    public ParseRule(String lhs) {
        this.lhs = new NonTerminal(lhs, false);
    }

    /**
     * Returns the left hand side of this rule
     * @return left hand side
     */
    public NonTerminal getLHS() {
        return lhs;
    }

    /**
     * Appends a node to the right hand side of this rule
     * @param node node to aappend
     * @return this object (for chaining)
     */
    public ParseRule addRhs(Node node) {
        rhs.add(node);
        return this;
    }

    /**
     * Appends a sequence of nodes to the right hand side of this rule
     * @param nodes nodes to aappend
     * @return this object (for chaining)
     */
    public ParseRule addRhs(Node... nodes) {
        rhs.addAll(Arrays.asList(nodes));
        return this;
    }

    /**
     * Returns the right hand side of this rule
     * @return the right hand side.
     */
    public List<Node> getRHS() {
        return Collections.unmodifiableList(rhs);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(lhs.toString());
        sb.append(" = ");
        for (Node node : rhs) {
            sb.append(replaceSpecials(node.toString())).append(" ");
        }
        return sb.toString();
    }

    private static String replaceSpecials(String in) {
        return in.replaceAll("\t", "\\\\t").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
    }

    /**
     * Returns the rule from which this one was generated (or this object if it was not generated).
     * @return the original rule
     */
    public ParseRule getOrigin() {
        ParseRule oldPR = null;
        ParseRule newPR = origin;
        while (!newPR.equals(oldPR)) {
            oldPR = newPR;
            newPR = oldPR.origin;
        }
        return newPR;
    }

    /**
     * converts this rule in an equivalent list of rules that do not contain kleene star nodes.
     * @return an equivalent list of rules without kleene star
     */
    public List<ParseRule> convertStarNodes() {
        List<ParseRule> res = new ArrayList<>();
        ParseRule copy = this.copy();
        copy.origin = this;
        Deque<Node> toConsider = new ArrayDeque<>(getRHS());
        while (!toConsider.isEmpty()) {
            Node rhsNode = toConsider.pop();
            if (rhsNode instanceof StarNode) {
                NonTerminal replacement = new NonTerminal("(" + rhsNode.toString() + ")", true);//NonTerminal.getNext();
                copy.replaceRHSNodes(rhsNode, replacement);
                ParseRule recursiveReplacement = new ParseRule(replacement);
                recursiveReplacement.origin = this;
                for (Node starContent : ((StarNode) rhsNode).contents()) {
                    recursiveReplacement.addRhs(starContent);
                }
                recursiveReplacement.addRhs(replacement);
                res.addAll(recursiveReplacement.convertStarNodes());
                ParseRule inheritanceRule = new ParseRule(replacement);
                inheritanceRule.origin = this;
                res.add(inheritanceRule);
            } else if (rhsNode instanceof BoundNode) {
                List<ParseRule> test = new ParseRule("_").addRhs(((BoundNode) rhsNode).getContent()).convertStarNodes();
                for (ParseRule generated : test) {
                    if (generated.getLHS().equals(nonTerm("_"))) {
                        assert generated.getRHS().size() == 1; //Since bound nodes are of only one node, this should also just be one.
                        copy.replaceRHSNodes(rhsNode, bound(generated.getRHS().get(0), ((BoundNode) rhsNode).getName(), ((BoundNode) rhsNode).isLazy()));
                    } else {
                        res.add(generated);
                    }
                }
                toConsider.push(((BoundNode) rhsNode).getContent());
            }
        }
        res.add(copy);
        return res;

    }

    private void replaceRHSNodes(Node rhsNode, Node replacement) {
        for (int i = 0; i < rhs.size(); i++) {
            if (rhs.get(i).equals(rhsNode)) {
                rhs.set(i, replacement);
            }
        }
    }

    ParseRule copy() {
        ParseRule res = new ParseRule((NonTerminal) this.lhs.copy());
        res.addRhs(this.rhs.stream().map(Node::copy).toArray(Node[]::new));
        if (origin == this) {
            res.origin = res;
        } else {
            res.origin = origin.copy();
        }
        assert this.equals(res);
        return res;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return lhs.hashCode() + 3*rhs.hashCode();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ParseRule)) {
            return false;
        }
        if (!lhs.equals(((ParseRule) other).lhs)) {
            return false;
        }
        return rhs.equals(((ParseRule) other).rhs);
    }

}
