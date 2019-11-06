package parselang.intermediate.fruity.sentences;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FruityPrint extends FruitySentence {

    private final int toPrint;

    public FruityPrint(int toPrint) {
        this.toPrint = toPrint;
    }

    public int getSource() {
        return toPrint;
    }

    @Override
    public String toString() {
        return "print var" + toPrint;
    }

    @Override
    public Set<Integer> getReads() {
        return Collections.singleton(toPrint);
    }

    @Override
    public Optional<Integer> getWrites() {
        return Optional.empty();
    }
}
