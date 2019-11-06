package parselang.intermediate.fruity.sentences;

import parselang.intermediate.fruity.expressions.FruityExpression;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityAssignment extends FruitySentence {

    private static int variableCounter = 0;
    private FruityExpression expression;
    private final Integer writesTo;

    public FruityAssignment(Integer assignment_to, FruityExpression exp) {
        this.writesTo = assignment_to;
        this.expression = exp;
    }

    public static Integer newVariable() {
        return variableCounter++;
    }

    public String toString() {
        return "var" + writesTo + " := " + expression;
    }

    public FruityExpression source() {
        return expression;
    }

    public Integer target() {
        return writesTo;
    }


    public void setSource(FruityExpression expression) {
        this.expression = expression;
    }

    @Override
    public Set<Integer> getReads() {
        return expression.getReads();
    }

    @Override
    public Optional<Integer> getWrites() {
        return Optional.of(writesTo);
    }
}
