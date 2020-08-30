package parselang.parser.data;

import java.util.Arrays;
import java.util.List;

public class StarNode extends Node {

    private final Node[] inner;

    @Override
    public Node copy() {
        return new StarNode(Arrays.stream(inner).map(Node::copy).toArray(Node[]::new));
    }

    public StarNode(Node... inner) {
        this.inner = inner;
    }

    public List<Node> contents() {
        return Arrays.asList(inner);
    }

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StarNode && (obj == this || Arrays.equals(((StarNode) obj).inner, inner));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(inner);
    }


}
