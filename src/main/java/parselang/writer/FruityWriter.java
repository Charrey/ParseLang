package parselang.writer;

import javafx.util.Pair;
import parselang.intermediate.dataholders.*;
import parselang.intermediate.fruity.*;
import parselang.intermediate.fruity.expressions.*;
import parselang.intermediate.fruity.parameters.FruityParameter;
import parselang.intermediate.fruity.sentences.*;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.*;

import java.util.*;

import static parselang.parser.ParseRuleStorage.nonTerm;

public class FruityWriter extends Writer{


    @Override
    public String writeToString(Program program, ParseRuleStorage storage) {
        FruityProgram fruityProgram = makeFruity(program, storage);
        return fruityProgram.toString();
    }

    private FruityProgram makeFruity(Program program, ParseRuleStorage storage) {
        FruityProgram res = new FruityProgram();
        for (Declaration decl : program.getDeclarations()) {
            writeFruityMethods(res, decl, storage);
        }
        res = FruityOptimizer.optimize(res, storage);
        Set<Integer> rulesUsed = getRulesUsed(res, storage);
        res.addFunctions(FruityLibrary.get(rulesUsed, storage));
        return FruityOptimizer.optimize(res, storage);
    }

    private Set<Integer> getRulesUsed(FruityProgram program, ParseRuleStorage storage) {
        Set<Integer> res = new HashSet<>();
        for (FruityFunction function : program.getFunctions()) {
            for (FruitySentence sentence : function.getSentences()) {
                if (sentence instanceof FruityAssignment) {
                    FruityExpression expression = ((FruityAssignment) sentence).source();
                    if (expression instanceof FruityCall) {
                        int rule = ((FruityCall) expression).getRule();
                        if (!storage.isAddedRule(rule)) {
                            res.add(rule);
                        }
                    } else if (expression instanceof FruityLambda) {
                        int rule = ((FruityLambda) expression).getResult().getRule();
                        if (!storage.isAddedRule(rule)) {
                            res.add(rule);
                        }
                    }
                }
            }
        }
        return res;
    }

    private Set<Integer> rulesUsed = new HashSet<>();

    private void writeFruityMethods(FruityProgram res, Declaration declaration, ParseRuleStorage storage) {
        FruityFunction fruityFunction = new FruityFunction();
        fruityFunction.setName("rule" + declaration.getRuleID());
        fruityFunction.setComment(storage.getRuleByID(declaration.getRuleID()).toString());
        for (Node param : declaration.getParams()) {
            if (param instanceof BoundNonTerminal) {
                String variableName = ((BoundNonTerminal) param).getName();
                fruityFunction.addParam(new FruityParameter(variableName));
            }
        }
        Pair<Integer, List<FruitySentence>> content = getFruitySentencesForCall(declaration.getContent(), storage, false);
        fruityFunction.addSentences(content.getValue());
        fruityFunction.addSentence(new FruityReturn(content.getKey()));
        res.addFunction(fruityFunction);
    }

    /**
     * Returns FruitSentences that corresponds to a function call.
     * @param content AST defining the function to be called.
     * @param storage Parserulestorage in use
     * @return The sentences and the variable the function call result is in.
     */
    public Pair<Integer, List<FruitySentence>> getFruitySentencesForCall(DeclUse content, ParseRuleStorage storage, boolean lazy) {
        List<FruitySentence> res = new LinkedList<>();
        ParseRule ruleUsed = storage.getRuleByID(content.getRuleID());
        assert content.getArgs().size() == ruleUsed.getRHS().size();
        List<Integer> parameters = new ArrayList<>();
        for (int i = 0; i < ruleUsed.getRHS().size(); i++) {
            Node node = ruleUsed.getRHS().get(i);
            Argument parameter = content.getArgs().get(i);
            if (node instanceof StarNode) {
                Pair<Integer, List<FruitySentence>> starSentences = getFruitySentencesForStar((ArgumentList) parameter, storage, lazy);
                res.addAll(starSentences.getValue());
            } else if (node instanceof BoundNonTerminal) {
                BoundNonTerminal boundNode = (BoundNonTerminal) node;
                Node boundNodeContent = boundNode.getContent();
                if (boundNode.isLazy()) {
                    if (boundNodeContent instanceof StarNode) {
                        Pair<Integer, List<FruitySentence>> fruitySentencesStar = getFruitySentencesForStar(((ArgumentList)parameter), storage, true);
                        res.addAll(fruitySentencesStar.getValue());
                        parameters.add(fruitySentencesStar.getKey());
                    } else if (boundNodeContent instanceof NonTerminal) {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    if (boundNodeContent instanceof StarNode) {
                        Pair<Integer, List<FruitySentence>> starSentences = getFruitySentencesForStar((ArgumentList) parameter, storage, lazy);
                        res.addAll(starSentences.getValue());
                        parameters.add(starSentences.getKey());
                    } else if (boundNodeContent instanceof NonTerminal) {
                        Pair<Integer, List<FruitySentence>> ntSubResult = getFruitySentencesForCall((DeclUse) parameter, storage, lazy);
                        res.addAll(ntSubResult.getValue());
                        parameters.add(ntSubResult.getKey());
                    }
                }
            } else if (node instanceof Terminal) {

            } else if (node instanceof NonTerminal)  {
                Pair<Integer, List<FruitySentence>> ntSubResult = getFruitySentencesForCall((DeclUse) parameter, storage, lazy);
                res.addAll(ntSubResult.getValue());
            } else {
                throw new UnsupportedOperationException();
            }
        }
        int returnVar = FruityAssignment.newVariable();
        if (lazy) {
            res.add(new FruityAssignment(returnVar, new FruityLambda((new FruityCall(content.getRuleID(), parameters)))));
        } else {
            res.add(new FruityAssignment(returnVar, new FruityCall(content.getRuleID(), parameters)));
        }
        return new Pair<>(returnVar, res);
        //throw new RuntimeException();
    }

    private Pair<Integer, List<FruitySentence>> getFruitySentencesForStar(ArgumentList starArgs, ParseRuleStorage storage, boolean lazy) {
        List<FruitySentence> res = new LinkedList<>();
        List<Integer> varsToPutInFruityList = new LinkedList<>();
        for (Argument arg : starArgs) {
            if (arg instanceof ArgumentList) {
                Pair<Integer, List<FruitySentence>> sublist = getFruitySentencesForStar((ArgumentList)arg, storage, lazy);
                res.addAll(sublist.getValue());
                varsToPutInFruityList.add(sublist.getKey());
            } else if (arg instanceof DeclUse) {
                Pair<Integer, List<FruitySentence>> fruitySentencesForListItem = getFruitySentencesForCall((DeclUse) arg, storage, lazy);
                res.addAll(fruitySentencesForListItem.getValue());
                varsToPutInFruityList.add(fruitySentencesForListItem.getKey());
            } else if (arg instanceof ConstantString) {
                int toPutIn = FruityAssignment.newVariable();
                res.add(new FruityAssignment(toPutIn, new FruityConstantString(((ConstantString) arg).getContent())));
            } else {
                throw new UnsupportedOperationException("todo");
            }
        }
        int returnVar = FruityAssignment.newVariable();
        res.add(new FruityAssignment(returnVar, new FruityList(varsToPutInFruityList)));
        return new Pair<>(returnVar, res);
    }

}
