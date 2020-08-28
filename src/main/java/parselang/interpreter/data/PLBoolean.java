package parselang.interpreter.data;

public class PLBoolean extends PLData{


    private final boolean value;

    private static final PLBoolean True = new PLBoolean(true);
    private static final PLBoolean False = new PLBoolean(false);

    private PLBoolean(boolean value) {
        this.value = value;
    }

    public static PLBoolean getTrue() {
        return True;
    }

    public static PLBoolean getFalse() {
        return False;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public String classString() {
        return "boolean";
    }
}
