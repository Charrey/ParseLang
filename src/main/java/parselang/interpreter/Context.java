package parselang.interpreter;

import parselang.interpreter.data.PLMap;
import parselang.interpreter.data.ParameterValue;

import java.util.Map;

public class Context {
    public Map<String, ParameterValue> variableAssignments;

    public Context(Map<String, ParameterValue> variableAssignments) {
        this.variableAssignments = variableAssignments;
    }

}
