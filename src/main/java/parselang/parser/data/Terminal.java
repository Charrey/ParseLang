package parselang.parser.data;

public class Terminal extends Node {

    private final String value;

    public Terminal(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Terminal)) {
            return false;
        }
        return value.equals(((Terminal) obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return value;
    }

    @Override
    public Node copy() {
        return new Terminal(value);
    }
}
