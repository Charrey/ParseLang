package parselang.intermediate;

import java.util.LinkedList;
import java.util.List;

public class Declaration {

    public static final Declaration SENTENCE = new Declaration();
    public static final Declaration EMPTY = new Declaration();



    private String name;
    private Declaration superDeclaration;
    private boolean left;
    private List<Parameter> params = new LinkedList<>();
    private List<Sentence> sentences;

    public void setName(String name) {
        this.name = name;
    }


    public void setSuper(Declaration superDecl) {
        superDeclaration = superDecl;
    }

    public void addParam(TerminalParameter parameter) {
        params.add(parameter);
    }
}
