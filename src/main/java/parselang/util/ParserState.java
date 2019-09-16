package parselang.util;

import parselang.parser.data.AST;
import parselang.parser.data.Node;
import parselang.parser.data.ParseRule;

import java.util.Collection;

public class ParserState {

    private final Node toBeParsed;
    private final int notYetParsed;
    private final AST parseTree;
    private final Collection<ParseRule> applicableRules;
    private int ruleApplied;

    public ParserState(Node toBeParsed, int notYetParsed, AST parseTree, Collection<ParseRule> applicableRules) {
        this.toBeParsed = toBeParsed;
        this.notYetParsed = notYetParsed;
        this.parseTree = parseTree;
        this.applicableRules = applicableRules;
    }

    public int getNotYetParsed() {
        return notYetParsed;
    }

    public AST getParseTree() {
        return parseTree;
    }

    public Node getToBeParsed() {
        return toBeParsed;
    }

    public Collection<ParseRule> rules() {
        return applicableRules;
    }
}
