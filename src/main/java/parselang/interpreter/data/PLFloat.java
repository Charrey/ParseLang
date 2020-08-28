package parselang.interpreter.data;

import java.math.BigDecimal;
import java.util.Objects;

public class PLFloat extends PLData {

    public static final int SCALE = 20;
    private BigDecimal content;

    public PLFloat(PLInteger after) {
        content = new BigDecimal(after.get());
    }

    public BigDecimal get() {
        return content;
    }

    public void set(BigDecimal newValue) {
        content = newValue;
    }

    public void add(PLFloat afterFloat) {
        content = content.add(afterFloat.get());
    }

    @Override
    public String toString() {
        String toReturn = content.toString();
        return !toReturn.contains(".") ? toReturn + ".0" : toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLFloat plFloat = (PLFloat) o;
        return content.equals(plFloat.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String classString() {
        return "float";
    }
}
