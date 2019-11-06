package parselang.intermediate.fruity.expressions;

import parselang.intermediate.fruity.sentences.FruitySentence;
import parselang.parser.data.NonTerminal;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityPush extends FruitySentence {

    private final NonTerminal target;

    public FruityPush(NonTerminal target) {
        this.target = target;
    }

    public String toString() {
        return "push " + target;
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.emptySet();
    }

    @Override
    public Optional<Integer> getWrites() {
        return Optional.empty();
    }
}
