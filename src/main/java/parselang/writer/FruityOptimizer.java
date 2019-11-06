package parselang.writer;

import parselang.intermediate.fruity.FruityFunction;
import parselang.intermediate.fruity.FruityProgram;
import parselang.intermediate.fruity.expressions.*;
import parselang.intermediate.fruity.sentences.*;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.ParseRule;

import java.util.*;

import static parselang.parser.ParseRuleStorage.term;

public class FruityOptimizer {



    public static FruityProgram optimize(FruityProgram program, ParseRuleStorage storage) {
        program = replaceIntegers(program, storage);
        program = removeUnused(program, storage);
        return program;
    }

    public static FruityProgram removeUnused(FruityProgram program, ParseRuleStorage storage) {
        for (FruityFunction function : program.getFunctions()) {
            Set<Integer> seen = new HashSet<>();
            for (int i = function.getSentences().size() - 1; i >= 0; i--) {
                FruitySentence sentence = function.getSentences().get(i);
                Optional<Integer> writes = sentence.getWrites();
                if (writes.isPresent() && !seen.contains(writes.get())) {
                    function.getSentences().remove(i);
                } else {
                    Set<Integer> reads = sentence.getReads();
                    seen.addAll(reads);
                }
            }
        }
        return program;
    }


    static Map<Integer, FruityExpression> ruleCallToConstant;
    private static FruityProgram replaceIntegers(FruityProgram program, ParseRuleStorage storage) {
        if (ruleCallToConstant == null) {
            fillRuleToIntegerMap(storage);
        }
        for(FruityFunction fun : program.getFunctions()) {
            for (FruitySentence sen : fun.getSentences()) {
                if (sen instanceof FruityAssignment) {
                    FruityExpression source = ((FruityAssignment) sen).source();
                    if (source instanceof FruityCall) {
                        if (ruleCallToConstant.containsKey(((FruityCall) source).getRule())) {
                            ((FruityAssignment) sen).setSource(ruleCallToConstant.get(((FruityCall) source).getRule()));
                        } else {
                            int id = ((FruityCall) source).getRule();
                            System.out.println("Rule is not implemented as constant: " + id + " " + storage.getRuleByID(id));
                        }
                    } else if (source instanceof FruityLambda) {
                        if (ruleCallToConstant.containsKey(((FruityLambda) source).getResult().getRule())) {
                            ((FruityAssignment) sen).setSource(ruleCallToConstant.get(((FruityLambda) source).getResult().getRule()));
                        } else {
                            int id = ((FruityLambda) source).getResult().getRule();
                            System.out.println("Rule is not implemented as constant: " + id + " " + storage.getRuleByID(id));
                        }
                    }
                }
            }
        }
        return program;
    }

    private static void fillRuleToIntegerMap(ParseRuleStorage storage) {
        ruleCallToConstant = new HashMap<>();
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("Number").addRhs(term("0"))), new FruityConstantInteger(0));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("1"))), new FruityConstantInteger(1));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("2"))), new FruityConstantInteger(2));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("3"))), new FruityConstantInteger(3));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("4"))), new FruityConstantInteger(4));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("5"))), new FruityConstantInteger(5));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("6"))), new FruityConstantInteger(6));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("7"))), new FruityConstantInteger(7));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("8"))), new FruityConstantInteger(8));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("NonZeroNumber").addRhs(term("9"))), new FruityConstantInteger(9));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("WhiteSpace").addRhs(term(" "))), new FruityConstantString(" "));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("WhiteSpace").addRhs(term("\t"))), new FruityConstantString("\\t"));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("WhiteSpace").addRhs(term("\r"))), new FruityConstantString("\\r"));
        ruleCallToConstant.put(storage.getIDbyRule(new ParseRule("WhiteSpace").addRhs(term("\n"))), new FruityConstantString("\\n"));
    }


}
