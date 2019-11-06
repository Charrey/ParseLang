package parselang.intermediate.fruity.expressions;

import java.util.Collections;
import java.util.Set;

public class FruityConstantInteger extends FruityConstant {

    private final Integer value;

    public FruityConstantInteger(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.emptySet();
    }
}
