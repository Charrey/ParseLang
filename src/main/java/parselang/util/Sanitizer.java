package parselang.util;

import parselang.parser.data.*;

import java.util.function.Function;
import java.util.stream.Collectors;

public class Sanitizer {

    public String classify(String in) {
        String res = in.replaceAll("[^a-zA-Z]", "");
        res = res.substring(0, 1).toUpperCase() + res.substring(1);
        return res;
    }

    public String objectify(String in) {
        String res = in.replaceAll("[^a-zA-Z]", "");
        res = res.substring(0, 1).toLowerCase() + res.substring(1);
        return res;
    }

    public String objectName(Node node) {
        if (node instanceof Terminal) {
            return objectify(((Terminal) node).getValue());
        } else if (node instanceof NonTerminal) {
            return objectify(((NonTerminal) node).getName());
        } else if (node instanceof BoundNonTerminal) {
            return objectify(((BoundNonTerminal)node).getName());
        } else if (node instanceof StarNode) {
            return ((StarNode) node).contents().stream().map(new Function<Node, String>() {
                @Override
                public String apply(Node node) {
                    return objectName(node);
                }
            }).collect(Collectors.joining("_and_")) + "_star";
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static String makeStringConstant(String stringContent) {
        return "\"" + replaceSpecials(stringContent) + "\"";
    }

    public static String replaceSpecials(String in) {
        return in.replaceAll("\t", "\\\\t").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
    }
}
