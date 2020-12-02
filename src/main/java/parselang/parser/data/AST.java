package parselang.parser.data;

import parselang.interpreter.data.ParameterValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract syntax tree of a parsed object
 */
public class AST extends ASTElem  {

    private final String originalString;
    private ParseRule ruleApplied;
    private final Node root;

    private int parsedFrom;
    private int parsedTo;


    private final List<ASTElem> children = new LinkedList<>();



    /**
     * @inheritDoc
     */
    @Override
    public ASTElem copy() {
        AST res = new AST(root.copy(), originalString);
        res.ruleApplied = ruleApplied == null ? null : ruleApplied.copy();
        res.parsedFrom = parsedFrom;
        res.parsedTo = parsedTo;
        children.forEach(x -> res.children.add(x.copy()));
        res.overriddenSemantics = overriddenSemantics;
        return res;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AST ast = (AST) o;
        return parsedFrom == ast.parsedFrom &&
                parsedTo == ast.parsedTo &&
                Objects.equals(ruleApplied, ast.ruleApplied) &&
                root.equals(ast.root) &&
                children.equals(ast.children);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(ruleApplied, root, parsedFrom, parsedTo, children);
    }

    /**
     * Returns the rule used at this point in the parse tree
     * @return the rule
     */
    public ParseRule getRule() {
        return ruleApplied;
    }

    /**
     * Creates a new AST with a nonterminal as tree root
     * @param root tree root
     */
    public AST(Node root, String originalString) {
        this.root = root;
        this.originalString = originalString;
    }

    /**
     * Sets which part of the original string is represented by this parse tree.
     * @param from From index (inclusive)
     * @param to To index (exclusive)
     */
    public void setParsed(int from, int to) {
        parsedFrom = from;
        parsedTo = to;
    }

    /**
     * Sets the rule used at the root of this tree.
     * @param rule the rule
     */
    public void setRuleApplied(ParseRule rule) {
        ruleApplied = rule;
    }

    private boolean isLeaf() {
        return root instanceof Terminal;
    }

    /**
     * Appends a child to this tree
     * @param elem child to add
     */
    public void addChild(ASTElem elem) {
        if (!isLeaf()) {
            children.add(elem);
        } else {
            System.err.println("Warning! Attempting to add child to leaf AST node");
        }
    }

    /**
     * Pretty prints the AST
     */
    @Override
    public String toString() {
        return pp(0);
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }


    /**
     * Pretty prints this AST to a String
     * @param indent Number of tabs used as global indentation
     * @return String representation of this AST.
     */
    @Override
    protected String pp(int indent) {
        String prefix = new String(new char[indent]).replace("\0", "\t");

        StringBuilder sb = new StringBuilder(prefix);

        String line = padRight(root.toString(), 1000 - (indent*4));
        if (ruleApplied != null) {
            line += ruleApplied.toString();
        }
        sb.append(line);


        for (ASTElem i : children) {
            sb.append("\n").append(i.pp(indent + 1));
        }
        return sb.toString();
    }

    /**
     * Slow method of obtaining the string represented by this AST. Use substring() whenever possible for minimum overhead.
     * @return The string
     */
    public String parseString() {
        return this.originalString.substring(parsedFrom, parsedTo);
    }

    /**
     * Returns the root node of this tree (e.g. a nonterminal or terminal)
     * @return the root node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Returns a list of child nodes of the root node
     * @return child nodes
     */
    public List<ASTElem> getChildren() {
        return children;
    }

    /**
     * Returns a specific child node of this tree denoted by an index
     * @param i index
     * @return child at that index
     */
    public ASTElem getChild(int i) {
        return children.get(i);
    }

    /**
     * Replaces a child node in this tree with a new one.
     * @param i index
     * @param newChild new child
     */
    public void setChild(int i, ASTElem newChild) {
        children.set(i, newChild);
    }

    /**
     * Returns the last child of this tree.
     * @return last child
     */
    public ASTElem getLastChild() {
        return children.get(children.size()-1);
    }


    /**
     * Returns the start index (inclusive) in the original parsed string that is represented by this AST.
     * @return start index
     */
    public int getParsedTo() {
        return parsedTo;
    }

    /**
     * Returns the end index (exclusive) in the original parsed string that is represented by this AST.
     * @return start index
     */
    public int getParsedFrom() {
        return parsedFrom;
    }


    private ParameterValue overriddenSemantics = null;

    /**
     * Overrides the semantics of this AST (used in interpreter)
     */
    public void overrideSemantics(ParameterValue value) {
        overriddenSemantics = value;
    }

    /**
     * Returns whether the AST has overridden semantics (used in interpreter)
     * @return whether the AST has overridden semantics
     */
    public boolean hasOverriddenSemantics() {
        return overriddenSemantics != null;
    }

    /**
     * Returns the ovverridden semantics (used in interpreter)
     * @return overridden semantics
     */
    public ParameterValue getOverriddenSemantics() {
        return overriddenSemantics;
    }
}
