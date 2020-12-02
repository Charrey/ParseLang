package parselang.interpreter.data;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Class representing integer numbers in ParseLang
 */
public class PLInteger extends PLData{

    private BigInteger content;

    /**
     * Parses an integer from a string
     * @param stringValue String representation of an integer
     * @throws NumberFormatException stringValue is not a valid representation of an integer.
     */
    public PLInteger(PLString stringValue) {
        content = new BigInteger(stringValue.contents);
    }

    /**
     * Creates a new integer with value zero.
     */
    public PLInteger() {
        content = new BigInteger("0");
    }

    /**
     * Creates a new integer and initializes it to a value
     * @param bigInteger initial value of the integer
     */
    public PLInteger(BigInteger bigInteger) {
        this.content = bigInteger;
    }

    /**
     * Returns the BigInteger value of this integer
     * @return the BigInteger value
     */
    public BigInteger get() {
        return content;
    }

    /**
     * Sets the content of this mutible integer to a new value
     * @param content new value
     */
    public void set(BigInteger content) {
        this.content = content;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return content.toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLInteger plInteger = (PLInteger) o;
        return content.equals(plInteger.content);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String classString() {
        return "integer";
    }
}
