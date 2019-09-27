package parselang.parser.data;

public class BoundNonTerminal extends Node {

    private final String name;
    private final NonTerminal nonTerm;
    private final boolean lazy;

    public BoundNonTerminal(NonTerminal nonTerm, String name, boolean lazy) {
        this.nonTerm = nonTerm;
        this.name = name;
        this.lazy = lazy;
    }

    public NonTerminal getContent() {
        return nonTerm;
    }

    public String toString() {
        return "(" + nonTerm + " --> " + name + (lazy ? " (lazy) " : "") + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BoundNonTerminal && ((BoundNonTerminal)obj).getName().equals(name) && nonTerm.equals(((BoundNonTerminal)obj).getContent()) && lazy==((BoundNonTerminal) obj).lazy);

    }

    private String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 3*nonTerm.hashCode();
    }
}
