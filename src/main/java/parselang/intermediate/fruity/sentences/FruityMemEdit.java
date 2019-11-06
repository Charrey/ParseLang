package parselang.intermediate.fruity.sentences;

import parselang.intermediate.fruity.expressions.FruityConstant;
import parselang.parser.data.NonTerminal;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityMemEdit extends FruitySentence {

    private final int value;
    private final NonTerminal container;
    private final FruityConstant key;

    public FruityMemEdit(NonTerminal container, FruityConstant key, int value) {
        this.container = container;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return container + "[" + key + "] = " + "var" + value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.singleton(value);
    }

    @Override
    public Optional<Integer> getWrites() {
        return Optional.empty();
    }
}
