package parselang.parser;

import parselang.parser.data.AST;
import parselang.parser.data.ParseRule;

public class ParseResult {

    private final int notYetParsed;
    private final String original;
    private AST tree;




    public ParseResult(String original, int notYetParsed, AST tree) {
        this.original = original;
        this.notYetParsed = notYetParsed;
        this.tree = tree;
    }

    public ParseResult(String original, int notYetParsed, AST tree, ParseRule ruleApplied) {
        this.original = original;
        this.notYetParsed = notYetParsed;
        this.tree = tree;
        this.tree.setRuleApplied(ruleApplied);
    }

    public String getRemaining() {
        if (notYetParsed == original.length()) {
            return "";
        } else {
            return original.substring(notYetParsed);
        }
    }

    public AST getTree() {
        return tree;
    }

    public String toString() {
        return "<\"" +  (getRemaining().replaceAll("(\r\n)|(\n)", "\\\\n")).replaceAll("\"", "\\\\\"") + "\", \n" + tree + ">";
    }

    public String getParsed() {
        return original.substring(0, notYetParsed);
    }

    public int getRemainingIndex() {
        return notYetParsed;
    }

    public void setTree(AST tree) {
        this.tree = tree;
    }
}
