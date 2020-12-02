package parselang.parser.data;

import parselang.interpreter.data.ParameterValue;

/**
 * Any object that represents some parsed text
 */
public abstract class ASTElem implements ParameterValue {

    /**
     * Returns a pretty printed string representation of this AST element
     * @param indent global indentation level (in # of tabs)
     * @return pretty string
     */
    protected abstract String pp(int indent);

    /**
     * Returns the string represented by this AST element
     * @return the string
     */
    public abstract String parseString();

    /**
     * Returns a deep copy of this AST element
     * @return the string
     */
    public abstract ASTElem copy();
}
