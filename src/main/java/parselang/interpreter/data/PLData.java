package parselang.interpreter.data;

public abstract class PLData implements ParameterValue {

    /**
     * Provides a string that gives an indication of the type (in a user friendly way)
     * @return the string representing the class
     */
    public abstract String classString();
}
