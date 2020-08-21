package parselang.intermediate.fruity;

import parselang.intermediate.fruity.expressions.FruityConstant;
import parselang.intermediate.fruity.expressions.FruityConstantString;
import parselang.intermediate.fruity.parameters.FruityParameter;
import parselang.intermediate.fruity.sentences.FruityAssignment;
import parselang.intermediate.fruity.sentences.FruityPrint;
import parselang.intermediate.fruity.sentences.FruityReturn;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;

import java.util.*;
import java.util.stream.Collectors;

import static parselang.parser.ParseRuleStorage.*;

public class FruityLibrary {

    private static Map<ParseRule, FruityFunction> content;

    private static void init() {
        if (content == null) {
            content = new HashMap<>();
            ParseRule rule;

            rule = new ParseRule("Number").addRhs(bound(nonTerm("NonZeroNumber"), "e", false));
            content.put(rule, identity(nonTerm("Number"), 1, 1, rule.toString()));

            rule = new ParseRule("WhiteSpace").addRhs(term("\t"));
            content.put(rule, constant(nonTerm("WhiteSpace"), new FruityConstantString("\t"), rule.toString()));

            rule = new ParseRule("WhiteSpace").addRhs(term("\n"));
            content.put(rule, constant(nonTerm("WhiteSpace"), new FruityConstantString("\n"), rule.toString()));

            rule = new ParseRule("WhiteSpace").addRhs(term("\r"));
            content.put(rule, constant(nonTerm("WhiteSpace"), new FruityConstantString("\r"), rule.toString()));

            rule = new ParseRule("").addRhs(term("\r"));
            content.put(rule, constant(nonTerm("WhiteSpace"), new FruityConstantString("\r"), rule.toString()));

            rule = new ParseRule("SimpleExpression").addRhs(bound(nonTerm("NumberLiteral"), "e", false), ws());
            content.put(rule, identity(nonTerm("SimpleExpression"), 1, 1, rule.toString()));

            rule = new ParseRule("Sentence").addRhs(term("print"), ws(), bound(nonTerm("Expression"), "e", false));
            FruityFunction printFun = new FruityFunction();
            printFun.addParam(new FruityParameter("toPrint"));
            printFun.setComment(rule.toString());
            int target = FruityAssignment.newVariable();
            printFun.addSentence(new FruityAssignment(target, new FruityEvaluate(new FruityParameter("toPrint"))));
            printFun.addSentence(new FruityPrint(target));
            printFun.addSentence(new FruityReturn());
            content.put(rule, printFun);
        }
    }

    private static FruityFunction constant(NonTerminal container, FruityConstant value, String comment) {
        FruityFunction res = new FruityFunction();
        int assignParam = FruityAssignment.newVariable();
        res.addSentence(new FruityAssignment(assignParam, value));
        res.addSentence(new FruityReturn(assignParam));
        res.setComment(comment);
        return res;
    }

    private static FruityFunction identity(NonTerminal container, int inputs, int returns, String comment) {
        FruityFunction res = new FruityFunction();
        for (int param = 0; param < inputs; param++) {
            res.addParam(new FruityParameter("o" + (param + 1)));
        }
        int assignParam = FruityAssignment.newVariable();
        res.addSentence(new FruityAssignment(assignParam, new FruityEvaluate(res.getParam(returns - 1))));
        res.addSentence(new FruityReturn(assignParam));
        res.setComment(comment);
        return res;
    }

    public static Set<FruityFunction> get(Set<Integer> rulesUsed, ParseRuleStorage storage) {
        init();
        List<Integer> ruleIDs = new LinkedList<>(rulesUsed);
        ruleIDs.sort(Comparator.naturalOrder());
        List<ParseRule> rules = rulesUsed.stream().map(storage::getRuleByID).collect(Collectors.toList());

        Set<FruityFunction> res = new HashSet<>();
        for (int rule = 0; rule < rules.size(); rule++) {
            if (content.containsKey(rules.get(rule))) {
                FruityFunction function = content.get(rules.get(rule));
                function.setName("rule" + ruleIDs.get(rule));
                res.add(function);
            } else {
                System.err.println("The library rule " + ruleIDs.get(rule) + " has no definition:\n" + rules.get(rule) );
            }
        }
        return res;//throw new UnsupportedOperationException();
    }
}
