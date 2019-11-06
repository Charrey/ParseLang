package parselang.intermediate.fruity;

import parselang.intermediate.fruity.expressions.FruityExpression;
import parselang.intermediate.fruity.parameters.FruityParameter;

import java.util.Collections;
import java.util.Set;

public class FruityEvaluate extends FruityExpression {

    private final FruityParameter param;

    public FruityEvaluate(FruityParameter param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "evaluate(" + param + ")";
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.emptySet();
    }
}
