package parselang.parser.data;

import java.util.Arrays;
import java.util.List;

public class StarNode extends Node {

    private final Node[] inner;

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
}
