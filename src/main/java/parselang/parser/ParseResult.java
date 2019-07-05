package parselang.parser;

import parselang.parser.data.AST;
import parselang.parser.data.ParseRule;

public class ParseResult {

    private String parsed;
    private String remaining;
    private AST tree;




    ParseResult(String parsed, String remaining, AST tree) {
        this.parsed = parsed;
        this.remaining = remaining;
        this.tree = tree;
    }

    ParseResult(String parsed, String remaining, AST tree, ParseRule ruleApplied) {
        this.parsed = parsed;
        this.remaining = remaining;
        this.tree = tree;
        this.tree.setRuleApplied(ruleApplied);
    }

    public String getRemaining() {
        return remaining;
    }

    AST getTree() {
        return tree;
    }

    public String toString() {
        return "<\"" +  (remaining.replaceAll("(\r\n)|(\n)", "\\\\n")).replaceAll("\"", "\\\\\"") + "\", \n" + tree + ">";
    }

    public String getParsed() {
        return parsed;
    }
}
