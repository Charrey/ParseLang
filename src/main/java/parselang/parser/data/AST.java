package parselang.parser.data;

import parselang.parser.ParseRuleStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AST extends ASTElem  {

    private ParseRule ruleApplied;
    private Node root;

    private int parsedFrom;
    private int parsedTo;


    private final List<ASTElem> children = new LinkedList<>();

    @Override
    public ASTElem copy() {
        AST res = new AST(root.copy());
        res.ruleApplied = ruleApplied == null ? null : ruleApplied.copy();
        res.parsedFrom = parsedFrom;
        res.parsedTo = parsedTo;
        children.forEach(x -> res.children.add(x.copy()));
        return res;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(ruleApplied, root, parsedFrom, parsedTo, children);
    }

    public ParseRule getRule() {
        return ruleApplied;
    }

    public AST(Node root) {
        this.root = root;
    }

    public void setParsed(int from, int to) {
        parsedFrom = from;
        parsedTo = to;
    }

    public void setRuleApplied(ParseRule rule) {
        ruleApplied = rule;
    }

    private boolean isLeaf() {
        return root instanceof Terminal;
    }

    public void addChild(ASTElem elem) {
        if (!isLeaf()) {
            children.add(elem);
        } else {
            System.err.println("Warning! Attempting to add child to leaf AST node");
        }
    }

    public String toString() {
        return pp(0);
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }



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

    public String parseString() {
        if (isLeaf()) {
            return ((Terminal)root).getValue();
        } else {
            StringBuilder sb = new StringBuilder();
            for (ASTElem node : children) {
                sb.append(node.parseString());
            }
            return sb.toString();
        }
    }



    public Node getRoot() {
        return root;
    }

    public List<ASTElem> getChildren() {
        return children;
    }

    public ASTElem getChild(int i) {
        return children.get(i);
    }

    public void setChild(int i, ASTElem newChild) {
        children.set(i, newChild);
    }

    public ASTElem getLastChild() {
        return children.get(children.size()-1);
    }

    public String subString(String originalString) {
        return originalString.substring(parsedFrom, parsedTo);
    }

    public int getParsedTo() {
        return parsedTo;
    }

    public int getParsedFrom() {
        return parsedFrom;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
}
