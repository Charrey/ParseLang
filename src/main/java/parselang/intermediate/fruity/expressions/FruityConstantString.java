package parselang.intermediate.fruity.expressions;

import parselang.util.Sanitizer;

import java.util.Collections;
import java.util.Set;

public class FruityConstantString extends FruityConstant {


    private final String content;

    public FruityConstantString(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return Sanitizer.makeStringConstant(content);
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.emptySet();
    }
}
