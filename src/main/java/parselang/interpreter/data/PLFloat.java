package parselang.interpreter.data;

import java.math.BigDecimal;

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


}
