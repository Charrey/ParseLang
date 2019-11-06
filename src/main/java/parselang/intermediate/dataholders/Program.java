package parselang.intermediate.dataholders;

import parselang.parser.data.NonTerminal;

import java.util.*;

public class Program {

    private final String name;

    public Program(String name) {
        this.name = name;
    }

    private List<Declaration> declarations = new LinkedList<>();

    public void addDeclaration(Declaration decl) {
        declarations.add(decl);
    }

    public String getName() {
        return name;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }
}
