package parselang.interpreter.data;

/**
 * Class representing a boolean type in the interpreter
 */
public class PLBoolean extends PLData{


    private final boolean value;
    private static final PLBoolean True = new PLBoolean(true);
    private static final PLBoolean False = new PLBoolean(false);

    private PLBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Returns the 'true' boolean value
     * @return true
     */
    public static PLBoolean getTrue() {
        return True;
    }

    /**
     * Returns the 'false' boolean value
     * @return true
     */
    public static PLBoolean getFalse() {
        return False;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String classString() {
        return "boolean";
    }
}
