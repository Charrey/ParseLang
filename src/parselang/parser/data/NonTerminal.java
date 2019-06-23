package parselang.parser.data;

import java.util.concurrent.atomic.AtomicInteger;

public final class NonTerminal extends Node {

    private final String name;

    public NonTerminal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object other) {
        if (!(other instanceof NonTerminal)) {
            return false;
        }
        return name.equals(((NonTerminal) other).getName());
    }

    public int hashCode() {
        return name.hashCode();
    }


    private static AtomicInteger IDCOUNTER = new AtomicInteger(0);
    public static NonTerminal getNext() {
        return new NonTerminal("_NONTERMINAL_" + IDCOUNTER.getAndIncrement());
    }
}
