package parselang.parser.data;

import java.util.Arrays;
import java.util.List;

/**
 * Node wrapper that indicates it may be repeatedly greedily parsed
 */
public class StarNode extends Node {

    private final Node[] inner;

    /**
     * @inheritDoc
     */
    @Override
    public Node copy() {
        return new StarNode(Arrays.stream(inner).map(Node::copy).toArray(Node[]::new));
    }

    /**
     * Creates a new kleene star node from a sequence of nodes
     * @param inner nodes
     */
    public StarNode(Node... inner) {
        this.inner = inner;
    }

    /**
     * Returns all nodes contained within this kleene star
     * @return all nodes within
     */
    public List<Node> contents() {
        return Arrays.asList(inner);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        if (inner.length == 1) {
            return inner[0].toString() + "*";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < inner.length; i++) {
                sb.append(inner[i].toString());
                if (i < inner.length - 1) {
                    sb.append(" ");
                } else {
                    sb.append(")*");
                }
            }
            return sb.toString();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof StarNode && (obj == this || Arrays.equals(((StarNode) obj).inner, inner));
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(inner);
    }


}
