package parselang.intermediate.dataholders;

import parselang.parser.ParseRuleStorage;
import parselang.parser.data.ParseRule;

import java.util.List;

public class DeclUse extends Argument{

    private final Integer ruleID; //name where declaration is
    private final ArgumentList args;
    private final String constant;

    public ParseRule actualRule(ParseRuleStorage storage) {
        return storage.getRuleByID(ruleID);
    }

    public DeclUse(int ruleID, ArgumentList args) {
        this.ruleID = ruleID;
        this.args = args;
        this.constant = null;
    }

    public DeclUse(String terminalContent) {
        this.constant = terminalContent;
        this.ruleID = null;
        this.args = ArgumentList.EMPTY_LIST_RHS;
    }

    public int getRuleID() {
        return ruleID;
    }

    public ArgumentList getArgs() {
        return args;
    }

    public String objectNotation() {
        return "rule" + ruleID;
    }

    public boolean isTerminal() {
        return constant != null;
    }
}
