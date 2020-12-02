package parselang.interpreter.data;

/**
 * Class representing null in ParseLang
 */
public class PLNull extends PLData {

    private static final PLNull instance = new PLNull();

    private PLNull(){}

    /**
     * Returns the null object
     * @return the null object
     */
    public static PLNull get() {
        return instance;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "->Null!<-";
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String classString() {
        return "null";
    }
}
