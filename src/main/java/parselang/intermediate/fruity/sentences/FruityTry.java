package parselang.intermediate.fruity.sentences;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityTry extends FruitySentence {


    private final FruityThrowType target;

    public FruityTry(FruityThrowType target) {
        this.target = target;
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
