package parselang.intermediate.fruity.parameters;

import parselang.intermediate.fruity.expressions.FruityExpression;

import java.util.Collections;
import java.util.Set;

public class FruityParameter extends FruityExpression {

    private final String boundName;

    public FruityParameter(String boundName) {
        this.boundName = boundName;
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return "o_" + boundName;
    }
}
