package parselang.interpreter.data;

import java.math.BigInteger;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLInteger plInteger = (PLInteger) o;
        return content.equals(plInteger.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
