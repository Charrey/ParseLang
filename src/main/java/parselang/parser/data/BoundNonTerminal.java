package parselang.parser.data;

public class BoundNonTerminal extends Node {

    private final String name;
    private final NonTerminal nonTerm;

    public BoundNonTerminal(NonTerminal nonTerm, String name) {
        this.nonTerm = nonTerm;
        this.name = name;
    }

    public NonTerminal getContent() {
        return nonTerm;
    }

    public String toString() {
        return "(" + nonTerm + " --> " + name + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BoundNonTerminal && ((BoundNonTerminal)obj).getName().equals(name) && nonTerm.equals(((BoundNonTerminal)obj).getContent()));

    }

    private String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 3*nonTerm.hashCode();
    }
}
