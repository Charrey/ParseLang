package parselang.parser.data;

import java.util.List;

public class ParseRule {

    private final NonTerminal lhs;
    private List<Node> rhs;

    public ParseRule(NonTerminal lhs) {
        this.lhs = lhs;
    }
}
