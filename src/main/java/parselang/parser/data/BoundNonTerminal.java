package parselang.parser.data;

public class BoundNonTerminal extends Node {

    private final String name;
    private final Node nonTerm;
    private final boolean lazy;

    @Override
    public Node copy() {
        return new BoundNonTerminal(nonTerm.copy(), name, lazy);
    }

    public BoundNonTerminal(Node nonTerm, String name, boolean lazy) {
        this.nonTerm = nonTerm;
        this.name = name;
        this.lazy = lazy;
    }

    public Node getContent() {
        return nonTerm;
    }

    public String toString() {
        return "(" + nonTerm + " --> " + name + (lazy ? " (lazy) " : "") + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BoundNonTerminal && ((BoundNonTerminal)obj).getName().equals(name) && nonTerm.equals(((BoundNonTerminal)obj).getContent()) && lazy==((BoundNonTerminal) obj).lazy);

    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 3*nonTerm.hashCode();
    }

    public boolean isLazy() {
        return lazy;
    }



}
