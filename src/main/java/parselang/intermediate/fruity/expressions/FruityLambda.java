package parselang.intermediate.fruity.expressions;

import parselang.intermediate.fruity.expressions.FruityExpression;

import java.util.Set;

public class FruityLambda extends FruityExpression {

    private final FruityCall result;

    public FruityLambda(FruityCall result) {
        this.result = result;
    }

    public String toString() {
        return "x --> " + result;
    }

    public FruityCall getResult() {
        return result;
    }

    @Override
    public Set<Integer> getReads() {
        return result.getReads();
    }
}
