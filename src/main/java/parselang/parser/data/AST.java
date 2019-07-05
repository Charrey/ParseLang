package parselang.parser.data;

import parselang.parser.ParseRuleStorage;
import parselang.types.Value;

import java.util.LinkedList;
import java.util.List;

public class AST extends ASTElem implements Evaluator {

    private final ParseRuleStorage storage;
    private ParseRule ruleApplied;
    private final Node root;
    private String parsed;
    private List<ASTElem> children = new LinkedList<>();

    public AST(Terminal root) {
        this(root, null);
    }

    public ParseRule getRule() {
        return ruleApplied;
    }

    public AST(Node root, ParseRuleStorage storage) {
        this.root = root;
        this.storage = storage;
    }

    public void setParsed(String parsed) {
        this.parsed = parsed;
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

    @Override
    public Value evaluate() {
        return null;
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

    protected String parseString() {
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
}
