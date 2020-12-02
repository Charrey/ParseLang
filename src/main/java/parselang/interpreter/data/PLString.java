package parselang.interpreter.data;

import java.util.List;
import java.util.Objects;

/**
 * Class representing a string in ParseLang
 */
public class PLString extends PLData {

    final String contents;

    /**
     * Creates a new string with an initial value
     * @param contents initial value
     */
    public PLString(String contents) {
        this.contents = contents;
    }

    /**
     * Creates a new string with the value of multiple strings concatenated
     * @param together strings to concatenate and to initialise this with
     */
    public PLString(List<PLString> together) {
        StringBuilder sb = new StringBuilder();
        together.forEach(plString -> sb.append(plString.getContents()));
        contents = sb.toString();
    }

    /**
     * Initializes this string with the string representation of some value in ParseLang
     * @param base whose string representation to initialize with
     */
    public PLString(PLData base) {
        super();
        contents = base.toString();
    }

    private String getContents() {
        return contents;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return contents;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLString plString = (PLString) o;
        return contents.equals(plString.contents);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(contents);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String classString() {
        return "string";
    }
}
