package parselang.parser;

import parselang.parser.data.AST;
import parselang.parser.data.ASTElem;
import parselang.parser.data.ParseRule;

public class ParseResult {

    private String remaining;
    private ASTElem tree;


    ParseResult(String remaining, ASTElem tree) {
        this.remaining = remaining;
        this.tree = tree;
    }

    ParseResult(String remaining, ASTElem tree, ParseRule ruleApplied) {
        this.remaining = remaining;
        this.tree = tree;
        ((AST)this.tree).setRuleApplied(ruleApplied);
    }

    String getRemaining() {
        return remaining;
    }

    ASTElem getTree() {
        return tree;
    }

    public String toString() {
        return "<\"" +  (remaining.replaceAll("(\r\n)|(\n)", "\\\\n")).replaceAll("\"", "\\\\\"") + "\", \n" + tree + ">";
    }
}
