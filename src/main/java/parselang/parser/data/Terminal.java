package parselang.parser.data;

/**
 * A terminal in the parse process
 */
public class Terminal extends Node {

    private final String value;

    /**
     * Creates a new terminal
     * @param value text this terminal parses as
     */
    public Terminal(String value) {
        this.value = value;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Terminal)) {
            return false;
        }
        return value.equals(((Terminal) obj).value);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns the text this terminal parses as
     * @return the text
     */
    public String getValue() {
        return value;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Node copy() {
        return new Terminal(value);
    }
}
