package parselang.intermediate.fruity.expressions;

import parselang.intermediate.fruity.parameters.FruityParameter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FruityCall extends FruityExpression {

    private final int target;
    private final List<Integer> params;

    public FruityCall(Integer toCall, List<Integer> params) {
        this.target = toCall;
        this.params = params;
    }


    public String toString() {
        return "rule" + target + "(" + params.stream().map((Function<Integer, Object>) integer -> ("var" + integer)).map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }

    public int getRule() {
        return target;
    }

    public List<Integer> getParams() {
        return params;
    }

    @Override
    public Set<Integer> getReads() {
        return new HashSet<>(params);
    }
}
