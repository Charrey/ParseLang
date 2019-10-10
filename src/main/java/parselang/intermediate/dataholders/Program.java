package parselang.intermediate.dataholders;

import parselang.parser.data.NonTerminal;

import java.util.*;

public class Program {

    private final Set<NonTerminal> nonTerminals;
    private final String name;

    public Program(String name, Set<NonTerminal> nonTerminals) {
        this.name = name;
        this.nonTerminals = nonTerminals;
    }

    private List<Declaration> declarations = new LinkedList<>();
    private Map<Integer, Declaration> ruleIdToDeclaration = new HashMap<>();

    public void addDeclaration(Declaration decl) {
        ruleIdToDeclaration.put(decl.getRuleID(), decl);
        declarations.add(decl);
    }

    public String getName() {
        return name;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }
}
