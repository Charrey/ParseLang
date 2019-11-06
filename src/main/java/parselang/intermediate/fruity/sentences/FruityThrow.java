package parselang.intermediate.fruity.sentences;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityThrow extends FruitySentence {

    private final FruityThrowType target;

    public FruityThrow(FruityThrowType variable) {
        this.target = variable;
    }

    @Override
    public String toString() {
        return "throw " + target;
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
