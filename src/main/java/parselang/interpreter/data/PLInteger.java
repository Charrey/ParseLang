package parselang.interpreter.data;

import java.math.BigInteger;

public class PLInteger extends PLData{

    private BigInteger content;

    public PLInteger(PLString stringValue) {
        content = new BigInteger(stringValue.contents);
    }

    public PLInteger() {
        content = new BigInteger("0");
    }

    public BigInteger get() {
        return content;
    }

    public void set(BigInteger content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
