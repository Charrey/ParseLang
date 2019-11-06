package parselang.intermediate.fruity;

import parselang.parser.ParseRuleStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FruityProgram {

    private List<FruityFunction> functions = new LinkedList<>();
    private FruityFunction main = new FruityFunction();

    public FruityProgram() {

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (FruityFunction ff : functions) {
            sb.append(ff.toString());
        }
        sb.append(main.toString());
        return sb.toString();
    }

    public void addFunction(FruityFunction fruityFunction) {
        functions.add(fruityFunction);
    }

    public List<FruityFunction> getFunctions() {
        return functions;
    }

    public void addFunctions(Set<FruityFunction> fruityFunctions) {
        functions.addAll(fruityFunctions);
    }
}
