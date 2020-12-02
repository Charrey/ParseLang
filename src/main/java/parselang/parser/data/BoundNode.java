package parselang.parser.data;

/**
 * Node wrapper that binds it to a parameter
 */
public class BoundNode extends Node {

    private final String name;
    private final Node nonTerm;
    private final boolean lazy;

    /**
     * @inheritDoc
     */
    @Override
    public Node copy() {
        return new BoundNode(nonTerm.copy(), name, lazy);
    }

    /**
     * Creates a new node that is bound to a parameter
     * @param node node that is bound
     * @param name name of the parameter it is bound to
     * @param lazy whether the parameter should be lazily evaluated
     */
    public BoundNode(Node node, String name, boolean lazy) {
        this.nonTerm = node;
        this.name = name;
        this.lazy = lazy;
    }

    /**
     * Returns the node that is bound by a variable.
     * @return the node
     */
    public Node getContent() {
        return nonTerm;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "(" + nonTerm + " --> " + name + (lazy ? " (lazy) " : "") + ")";
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BoundNode && ((BoundNode)obj).getName().equals(name) && nonTerm.equals(((BoundNode)obj).getContent()) && lazy==((BoundNode) obj).lazy);

    }

    /**
     * Returns the parameter name
     * @return the parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return name.hashCode() + 3*nonTerm.hashCode();
    }

    /**
     * Returns whether the parameter is lazy
     * @return true iff the parameter is lazy
     */
    public boolean isLazy() {
        return lazy;
    }



}
