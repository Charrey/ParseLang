package parselang.intermediate.fruity.sentences;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityReturn extends FruitySentence {

    private int variable = -1;

    public FruityReturn() { }

    public FruityReturn(int var) {
        this.variable = var;
    }

    @Override
    public Set<Integer> getReads() {
        if (variable == -1) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(variable);
        }
    }

    @Override
    public Optional<Integer> getWrites() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "return" + (variable == -1 ? "" : " var" + variable);
    }
}
