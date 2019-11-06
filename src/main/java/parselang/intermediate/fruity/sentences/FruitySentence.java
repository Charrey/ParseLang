package parselang.intermediate.fruity.sentences;

import java.util.Optional;
import java.util.Set;

public abstract class FruitySentence {

    public abstract Set<Integer> getReads();
    public abstract Optional<Integer> getWrites();

}
