package parselang.intermediate.dataholders;

import parselang.parser.data.Node;

import java.util.LinkedList;
import java.util.List;

public class Declaration extends Argument {

    public Declaration(String name, int newRule, int inheritedRule, String superDeclaration, List<Node> params, DeclUse content) {
        this.name = name;
        this.ruleID = newRule;
        this.inheritedRule = inheritedRule;
        this.superDeclaration = superDeclaration;
        this.params = params;
        this.content = content;
    }

    private String name;
    private int ruleID;
    private int inheritedRule;
    private String superDeclaration;
    private List<Node> params = new LinkedList<Node>();
    private DeclUse content;

    public void setName(String name) {
        this.name = name;
    }

    public void setSuper(String superDecl) {
        superDeclaration = superDecl;
    }

    public void addParam(Node parameter) {
        params.add(parameter);
    }

    public int getRuleID() {
        return ruleID;
    }

    public String getName() {
        return name;
    }

    public List<Node> getParams() {
        return params;
    }

    public DeclUse getContent() {
        return content;
    }

    public int getInheritedId() {
        return inheritedRule;
    }
}
