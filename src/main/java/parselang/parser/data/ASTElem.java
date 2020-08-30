package parselang.parser.data;

import parselang.interpreter.data.ParameterValue;

public abstract class ASTElem implements ParameterValue {

    protected abstract String pp(int indent);

    public abstract String parseString();


    public abstract ASTElem copy();
}
