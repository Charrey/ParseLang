package parselang.interpreter.data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Class representing floating point numbers in ParseLang
 */
public class PLFloat extends PLData {

    /**
     * Decimals behind the floating point, i.e. accuracy
     */
    public static final int SCALE = 20;
    private BigDecimal content;

    /**
     * Creates a floating point number with the value of an integer
     * @param integer integer to create a floating point number of
     */
    public PLFloat(PLInteger integer) {
        content = new BigDecimal(integer.get());
    }

    /**
     * Gets the content as a BigDecimal
     * @return the BigDecimal value of this floating point number
     */
    public BigDecimal get() {
        return content;
    }

    /**
     * Sets the value of this floating point number
     * @param newValue new value
     */
    public void set(BigDecimal newValue) {
        content = newValue;
    }

    /**
     * Adds a floating point number to this one.
     * @param other floating point number to add
     */
    public void add(PLFloat other) {
        content = content.add(other.get());
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        String toReturn = content.toString();
        return !toReturn.contains(".") ? toReturn + ".0" : toReturn;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLFloat plFloat = (PLFloat) o;
        return content.equals(plFloat.content);
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
        return "float";
    }
}
