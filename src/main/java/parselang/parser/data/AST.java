package parselang.parser.data;

import parselang.parser.ParseRuleStorage;

import java.util.LinkedList;
import java.util.List;

public class AST extends ASTElem  {

    private ParseRule ruleApplied;
    private final Node root;

    private int parsedFrom;
    private int parsedTo;


    private final List<ASTElem> children = new LinkedList<>();

    public AST(Node root) {
        this(root, null);
    }

    public ParseRule getRule() {
        return ruleApplied;
    }

    public AST(Node root, ParseRuleStorage storage) {
        this.root = root;
    }

    public void setParsed(String original, int from, int to) {
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
}
