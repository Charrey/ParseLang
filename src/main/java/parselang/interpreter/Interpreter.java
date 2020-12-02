package parselang.interpreter;

import parselang.interpreter.data.*;
import parselang.parser.data.*;
import parselang.util.DeclarationTree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static parselang.parser.ParseRuleStorage.*;

/**
 * Parselang interpreter
 */
public class Interpreter {


    private PLData runList(ASTElemList list, Map<String, ASTElem>  paramAssignments) {
        PLList result = new PLList();
        list.forEach(astElem -> {
            if (astElem instanceof ASTElemList) {
                result.add(runList((ASTElemList) astElem, paramAssignments));
            } else if (astElem instanceof AST) {
                result.add(run((AST) astElem, paramAssignments));
            }
        });
        return result;
    }


    /**
     * Interprets an entire AST.
     * @param tree AST to be interpreted
     * @return The return value of the program
     */
    public PLData run(AST tree) {
        assertNoGeneratedOrigin(tree);
        return run(tree, new HashMap<>());
    }

    private void assertNoGeneratedOrigin(ASTElem tree) {
        if (tree instanceof AST) {
            assert ((AST) tree).getRule() == null || ((AST) tree).getRule().getOrigin().getRHS().stream().noneMatch(node -> node instanceof NonTerminal && ((NonTerminal) node).wasGeneratedByStar());
            ((AST) tree).getChildren().forEach(this::assertNoGeneratedOrigin);
        } else if (tree instanceof ASTElemList) {
            ((ASTElemList) tree).forEach(this::assertNoGeneratedOrigin);
        }
    }

    private PLData run(AST tree, Map<String, ASTElem>  paramAssignments) {
        if (tree.getRoot() instanceof NonTerminal) {
            return runNonTerminal(tree, paramAssignments);
        } else if (tree.getRoot() instanceof Terminal) {
            return runTerminal(tree);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private final Map<ParseRule, AST> declarations = new HashMap<>();

    private void addDeclarationAsFunction(AST declaration) {
        DeclarationTree declTree = new DeclarationTree(declaration);
        ParseRule rule = new ParseRule(declTree.getName()).addRhs(declTree.getRetrievedNodes().toArray(new Node[0]));
        AST declarationContent = (AST) declaration.getChild(11);
        declarations.put(rule, declarationContent);
    }

    private PLData runTerminal(AST tree) {
        return new PLString(((Terminal) tree.getRoot()).getValue());
    }

    private PLData runNonTerminal(AST tree, Map<String, ASTElem>  paramAssignments) {
        assertNoGeneratedOrigin(tree);
        PLData toReturn;
        if (declarations.containsKey(tree.getRule().getOrigin())) {
            Map<String, ASTElem> parametersToAdd = new HashMap<>();
            AST toExecute = (AST) declarations.get(tree.getRule().getOrigin()).copy();
            for (int i = 0; i < tree.getRule().getRHS().size(); i++) {
                Node ithNode = tree.getRule().getRHS().get(i);
                if (ithNode instanceof BoundNode && !((BoundNode) ithNode).isLazy()) {
                    String name = ((BoundNode) ithNode).getName();
                    PLData value;
                    if (tree.getChild(i) instanceof AST) {
                        value = run((AST) tree.getChild(i), paramAssignments);
                    } else {
                        value = runList( (ASTElemList) tree.getChild(i), paramAssignments);
                    }
                    replaceParameters(toExecute, name, value);
                }
            }
            for (int i = 0; i < tree.getRule().getRHS().size(); i++) {
                Node ithNode = tree.getRule().getRHS().get(i);
                if (ithNode instanceof BoundNode && ((BoundNode) ithNode).isLazy()) {
                    ASTElem value = tree.getChildren().get(i).copy();
                    replaceByMap(value, paramAssignments);
                    parametersToAdd.put(((BoundNode) ithNode).getName(), value);
                }
            }
            toReturn = run(toExecute, parametersToAdd);
        } else {
            switch (((NonTerminal) (tree.getRoot())).getName()) {
                case "HighLevel":
                    toReturn = processHighLevel(tree, paramAssignments);
                    break;
                case "Expression":
                    toReturn =  processExpression(tree, paramAssignments);
                    break;
                case "ComparitiveExpression":
                    toReturn =  processComparitiveExpression(tree, paramAssignments);
                    break;
                case "AdditiveExpression":
                    toReturn =  processAdditiveExpression(tree, paramAssignments);
                    break;
                case "MultiplicativeExpression":
                    toReturn =  processMultiplicativeExpression(tree, paramAssignments);
                    break;
                case "SimpleExpression":
                    toReturn =  processSimpleExpression(tree, paramAssignments);
                    break;
                case "DeclarationContent":
                    toReturn =  processDeclarationContent(tree, paramAssignments);
                    break;
                case "DelimitedSentence":
                    toReturn =  processDelimitedSentence(tree, paramAssignments);
                    break;
                case "Sentence":
                    toReturn =  processSentence(tree, paramAssignments);
                    break;
                case "StringLiteral":
                    toReturn =  processStringLiteral(tree, paramAssignments);
                    break;
                case "ListLiteral":
                    toReturn =  processListLiteral(tree, paramAssignments);
                    break;
                case "NumberLiteral":
                    toReturn =  processNumberLiteral(tree, paramAssignments);
                    break;
                case "OptionalDecimalPlaces":
                    toReturn =  processOptionalDecimalPlaces(tree, paramAssignments);
                    break;
                case "Data":
                    toReturn = processData(tree, paramAssignments);
                    break;
                case "OptionalAssignment":
                    toReturn = processOptionalAssignment(tree, paramAssignments);
                    break;
                case "SingleExpression":
                    toReturn = processSingleExpression(tree, paramAssignments);
                    break;
                case "BooleanLiteral":
                    toReturn = processBooleanLiteral(tree);
                    break;
                case "NonZeroNumber":
                    toReturn = processNonZeroNumber(tree);
                    break;
                case "SafeChar":
                case "Number":
                case "UpperOrLowerCase":
                case "UpperOrLowerCaseOrNumber":
                case "SafeSpecial":
                case "UpperCase":
                case "LowerCase":
                case "WhiteSpace":
                case "RegisteredNonTerminal":
                    toReturn =  processSimpleRule(tree, paramAssignments);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        assert toReturn != null;
        return toReturn;
    }

    private void replaceByMap(ASTElem value, Map<String, ASTElem> variableAssignments) {
        if (value instanceof AST) {
            if (((AST) value).getRoot().equals(nonTerm("ParameterName")) && variableAssignments.containsKey(value.parseString())) {
                ((AST) value).overrideSemantics(variableAssignments.get(value.parseString()));
            } else {
                ((AST) value).getChildren().forEach(astElem -> replaceByMap(astElem, variableAssignments));
            }
        } else {
            ((ASTElemList)value).forEach(astElem -> replaceByMap(astElem, variableAssignments));
        }
    }

    private PLData processNonZeroNumber(AST tree) {
        return new PLInteger(new BigInteger(tree.parseString()));
    }

    private void replaceParameters(ASTElem toExecute, String name, PLData value) {
        if (toExecute instanceof AST) {
            if (((AST) toExecute).getRoot().equals(nonTerm("ParameterName")) && toExecute.parseString().equals(name)) {
                ((AST)toExecute).overrideSemantics(value);
            } else {
                ((AST) toExecute).getChildren().forEach(astElem -> replaceParameters(astElem, name, value));
            }
        } else {
            ((ASTElemList)toExecute).forEach(astElem -> replaceParameters(astElem, name, value));
        }
    }

    private PLData processBooleanLiteral(AST tree) {
        if (tree.getRule().getOrigin().equals(new ParseRule("BooleanLiteral").addRhs(term("true")))) {
            return PLBoolean.getTrue();
        } else if (tree.getRule().getOrigin().equals(new ParseRule("BooleanLiteral").addRhs(term("false")))) {
            return PLBoolean.getFalse();
        }
        throw new UnsupportedOperationException();
    }

    private PLData processSingleExpression(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("SingleExpression").addRhs(bound(nonTerm("SimpleExpression"), "e", false), bound(star(term("["), ws(), nonTerm("Expression"), ws(), term("]")), "e2", false)))) {
            PLData toReturn = run((AST) tree.getChild(0), paramAssignments);
            ASTElemList indexations = (ASTElemList) tree.getChild(1);
            for (ASTElem indexation : indexations) {
                PLData index = run((AST) ((ASTElemList) indexation).get(2), paramAssignments);
                if (toReturn instanceof PLIndexable) {
                    toReturn = ((PLIndexable)toReturn).get(index);
                }else {
                    throw new IllegalArgumentException(toReturn.getClass().getName() + " cannot be indexed!");
                }
            }
            return toReturn;
        }
        throw new UnsupportedOperationException();
    }

    private PLData processListLiteral(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("ListLiteral").addRhs(
                term("["),
                ws(),
                bound(nonTerm("Expression"), "e", false),
                ws(),
                bound(star(term(","), ws(), nonTerm("Expression"), ws()), "e2", false),
                term("]")))) {
            PLList toReturn = new PLList();
            PLData firstElement = run((AST) tree.getChild(2), paramAssignments);
            toReturn.add(firstElement);
            ASTElemList others = (ASTElemList) tree.getChild(4);
            for (ASTElem other : others) {
                AST expression = (AST) ((ASTElemList) other).get(2);
                toReturn.add(run(expression, paramAssignments));
            }
            return toReturn;
        } else if (tree.getRule().getOrigin().equals(new ParseRule("ListLiteral").addRhs(term("["), ws(), term("]"), ws()))) {
            return new PLList();
        }
        throw new UnsupportedOperationException();
    }

    private PLData processSentence(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("Sentence").addRhs(nonTerm("DelimitedSentence"), ws(), term(";")))) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        throw new UnsupportedOperationException();
    }

    private PLData processOptionalAssignment(AST tree, Map<String, ASTElem> paramAssignments) {
        if (tree.getChildren().isEmpty()) {
            return PLNull.get();
        } else {
            return run((AST) tree.getChild(2), paramAssignments);
        }
    }

    private final PLMap data = new PLMap();

    private PLData processData(AST tree, Map<String, ASTElem> paramAssignments) {
        PLData current = data;
        ASTElemList astKeys = ((ASTElemList)tree.getChild(1));
        boolean isAssignment = ((AST)tree.getChild(3)).getChildren().size() > 0;
        for (int i = 0; i < astKeys.size() - (isAssignment ? 1 : 0); i++) {
            AST expressionAST = (AST) ((ASTElemList) astKeys.get(i)).get(1);
            PLData expressionResult = run(expressionAST, paramAssignments);
            if (!(current instanceof PLIndexable)) {
                throw new IllegalArgumentException(current.classString() + " cannot be indexed.");
            } else {
                current = ((PLIndexable)current).get(expressionResult);
            }
        }
        if (isAssignment) {
            if (!(current instanceof PLIndexable)) {
                throw new IllegalArgumentException(current.classString() + " cannot be indexed.");
            } else {
                PLData lastKey = run((AST) ((ASTElemList) astKeys.get(astKeys.size() - 1)).get(1), paramAssignments);
                PLData value = run((AST) ((AST)tree.getChild(3)).getChild(2), paramAssignments);
                ((PLIndexable)current).set(lastKey, value);
            }
        }
        return current;
    }

    private PLData processOptionalDecimalPlaces(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("OptionalDecimalPlaces"))) {
            return PLNull.get();
        } else if (tree.getRule().getOrigin().equals(new ParseRule("OptionalDecimalPlaces").addRhs(term("."), bound(star(nonTerm("Number")), "e", false)))) {
            PLInteger res = new PLInteger();
            ASTElemList decimals = (ASTElemList) tree.getChild(1);
            decimals.forEach(astElem -> res.set(res.get().multiply(new BigInteger("10")).add(new PLInteger((PLString) run((AST) astElem, paramAssignments)).get())));
            return res;
        }
        throw new UnsupportedOperationException();
    }

    private PLData processNumberLiteral(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return new PLInteger((PLString) run((AST) tree.getChild(0), paramAssignments));
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("NumberLiteral").addRhs(nonTerm("OptionalMinus"), bound(nonTerm("NonZeroNumber"), "e", false), bound(star(nonTerm("Number")), "e2", false), nonTerm("OptionalDecimalPlaces")))) {
            PLInteger before = (PLInteger) run((AST) tree.getChild(1), paramAssignments);
            AST optionalMinus = (AST) tree.getChild(0);
            if (optionalMinus.getChildren().size() == 1) {
                before.set(before.get().negate());
            }

            ASTElemList additionalDecimals = (ASTElemList) tree.getChild(2);
            additionalDecimals.forEach(astElem -> {
                PLData intermediateResult = run((AST)astElem, paramAssignments);
                PLInteger toAdd;
                if (intermediateResult instanceof PLString) {
                    toAdd = new PLInteger((PLString) intermediateResult);
                } else if (intermediateResult instanceof PLInteger) {
                    toAdd = (PLInteger) intermediateResult;
                } else {
                    throw new UnsupportedOperationException("Type not recognised here");
                }
                before.set(before.get().multiply(new BigInteger("10")).add(toAdd.get()));
            });
            Object after = run((AST) tree.getChild(3), paramAssignments);
            if (after instanceof PLNull) {
                return before;
            } else {
                PLFloat beforeFloat = new PLFloat(before);
                PLFloat afterFloat = new PLFloat((PLInteger) after);
                while (afterFloat.get().compareTo(new BigDecimal("1")) >= 0) {
                    afterFloat.set(afterFloat.get().divide(new BigDecimal("10"), PLFloat.SCALE, RoundingMode.HALF_UP));
                }
                beforeFloat.add(afterFloat);
                return beforeFloat;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processSimpleRule(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (!tree.getRule().getRHS().isEmpty()) {
            throw new UnsupportedOperationException();
        } else {
            return PLNull.get();
        }
    }

    private PLData processStringLiteral(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("StringLiteral").addRhs(term("'")).addRhs(star(nonTerm("SafeChar"))).addRhs(term("'")))) {
            ASTElemList characters = (ASTElemList) tree.getChild(1);
            List<PLString> together = new ArrayList<>(characters.size());
            for (ASTElem character : characters) {
                together.add(new PLString(run((AST) character, paramAssignments)));
            }
            return new PLString(together);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processDelimitedSentence(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("DeclarationContent").addRhs(bound(nonTerm("DelimitedSentence"), "e", true), ws()))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processDeclarationContent(AST tree, Map<String, ASTElem> paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            if (tree.getChild(0) instanceof ASTElemList && ((ASTElemList)tree.getChild(0)).size() == 0) {
                return PLNull.get();
            }
            if (tree.getChild(0) instanceof AST) {
                return run((AST) tree.getChild(0), paramAssignments);
            } else if (tree.getChild(0) instanceof ASTElemList) {
                PLData lastSentenceReturn = null;
                for (ASTElem astElem : ((ASTElemList) tree.getChild(0))) {
                    lastSentenceReturn = run((AST) ((ASTElemList)astElem).get(0), paramAssignments);
                }
                return lastSentenceReturn;
            }
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("DeclarationContent").addRhs(bound(nonTerm("DelimitedSentence"), "e", true), ws()))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("DeclarationContent").addRhs(bound(nonTerm("Sentence"), "e", true), ws(), bound(star(nonTerm("Sentence"), ws()), "e2", true)))) {
            AST firstSentence = (AST) tree.getChild(0);
            PLData output = run(firstSentence, paramAssignments);
            ASTElemList otherSentences = (ASTElemList) tree.getChild(2);
            for (ASTElem otherSentence : otherSentences) {
                output = run((AST)((ASTElemList)otherSentence).get(0), paramAssignments);
            }
            return output;
        }
        assertNoGeneratedOrigin(tree);
        throw new UnsupportedOperationException();
    }

    private PLData processSimpleExpression(AST tree, Map<String, ASTElem>  paramAssignments) {
        if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("StringLiteral"), "e", false)))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("NumberLiteral"), "e", false)))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(term("("), ws(), bound(nonTerm("Expression"), "e", false), ws(), term(")")))) {
            return run((AST) tree.getChild(2), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("ParameterName"), "e", false), ws()))) {
            if (((AST)(tree.getChild(0))).hasOverriddenSemantics()) {
                ParameterValue overridden = ((AST)(tree.getChild(0))).getOverriddenSemantics();
                if (overridden instanceof PLData) {
                    return (PLData) overridden;
                } else {
                    return run((AST) overridden, paramAssignments);
                }
            }
            String parameterName = ((Terminal)((AST)((AST)tree.getChild(0)).getChild(0)).getRoot()).getValue();
            assert paramAssignments.containsKey(parameterName) : "Variable assignments " + paramAssignments + " does not contain " + parameterName;
            ParameterValue value = paramAssignments.get(parameterName);
            if (value instanceof ASTElemList) {
               return runList((ASTElemList) value, paramAssignments);
            } else if (value instanceof AST) {
                return run((AST) value, paramAssignments);
            } else {
                throw new UnsupportedOperationException();
            }
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(term("~concat"), ws(), term("("), ws(), bound(nonTerm("Expression"), "e", false), ws(), term(")"), ws()))) {
            PLData expression = run((AST) tree.getChild(4), paramAssignments);
            if (expression instanceof PLList) {
                List<PLString> toConcat = new LinkedList<>();
                ((PLList) expression).forEach(plData -> toConcat.add(new PLString(plData)));
                return new PLString(toConcat);
            } else {
                return new PLString(expression);
            }
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(term("~if"), ws(), term("("), ws(), bound(nonTerm("Expression"), "e", false), ws(), term(","), ws(), bound(nonTerm("Expression"), "e2", false), ws(), term(","), ws(), bound(nonTerm("Expression"), "e3", false), ws(), term(")"), ws()))) {
            PLData test = run((AST) tree.getChild(4), paramAssignments);
            if (!(test instanceof PLBoolean)) {
                throw new IllegalArgumentException("If can only be used with a boolean test! Actual type: " + test.getClass());
            } else if (test.equals(PLBoolean.getTrue())){
                return run((AST) tree.getChild(8), paramAssignments);
            } else {
                return run((AST) tree.getChild(12), paramAssignments);
            }
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("Data"), "e", false), ws()))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("ListLiteral"), "e", false), ws()))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("BooleanLiteral"), "e", false), ws()))) {
            return run((AST) tree.getChild(0), paramAssignments);
        } else if (tree.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(term("~map")))) {
            return new PLMap();
        } else if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        throw new UnsupportedOperationException();
    }

    private boolean isSimpleRule(ParseRule rule) {
        return rule.getRHS().size() == 1;
    }

    private PLData processMultiplicativeExpression(AST tree, Map<String, ASTElem>  paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("MultiplicativeExpression").addRhs(bound(nonTerm("SingleExpression"), "e", false), bound(star(nonTerm("TimesDivisionOrModulo"), ws(), nonTerm("SingleExpression")), "e2", false), ws()))) {
            PLData base = run((AST) tree.getChild(0), paramAssignments);
            if (((ASTElemList)tree.getChild(1)).size() == 0) {
                return base;
            } else {
                final boolean[] isFloat = {base instanceof PLFloat};
                List<String> operators = new LinkedList<>(); //0=*, 1=/, 2=%
                List<PLData> rest = new LinkedList<>();
                ASTElemList factors = (ASTElemList) tree.getChild(1);
                factors.forEach(astElem -> {
                    PLData get = run((AST) ((ASTElemList)astElem).get(2), paramAssignments);
                    rest.add(get);
                    operators.add(((ASTElemList) astElem).get(0).parseString());
                    isFloat[0] = isFloat[0] || get instanceof PLFloat;
                });
                if (isFloat[0]) {
                    PLFloat baseFloat = base instanceof PLFloat ? (PLFloat) base : new PLFloat((PLInteger) base);
                    for (int i = 0; i < rest.size(); i++) {
                        PLData other = rest.get(i);
                        PLFloat otherFloat = other instanceof  PLFloat ? (PLFloat) other : new PLFloat((PLInteger) other);
                        switch (operators.get(i)) {
                            case "*":
                                baseFloat.set(baseFloat.get().multiply(otherFloat.get()));
                                break;
                            case "/":
                                baseFloat.set(baseFloat.get().divide(otherFloat.get(), PLFloat.SCALE, RoundingMode.HALF_DOWN));
                                break;
                            case "%":
                                baseFloat.set(baseFloat.get().remainder(otherFloat.get()));
                                break;
                            default:
                                throw new UnsupportedOperationException();
                        }
                    }
                    return baseFloat;
                } else {
                    PLInteger baseInt = (PLInteger) base;
                    for (int i = 0; i < rest.size(); i++) {
                        PLInteger otherInt = (PLInteger) rest.get(i);
                        switch (operators.get(i)) {
                            case "*":
                                baseInt.set(baseInt.get().multiply(otherInt.get()));
                                break;
                            case "/":
                                baseInt.set(baseInt.get().divide(otherInt.get()));
                                break;
                            case "%":
                                baseInt.set(baseInt.get().remainder(otherInt.get()));
                                break;
                            default:
                                throw new UnsupportedOperationException();
                        }
                    }
                    return baseInt;
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processAdditiveExpression(AST tree, Map<String, ASTElem>  paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("AdditiveExpression").addRhs(bound(nonTerm("MultiplicativeExpression"), "e", false), bound(star(nonTerm("PlusOrMinus"), ws(), nonTerm("MultiplicativeExpression")), "e2", false), ws()))) {
            PLData base = run((AST) tree.getChild(0), paramAssignments);
            if (((ASTElemList)tree.getChild(1)).size() == 0) {
                return base;
            } else {
                final int[] type = {base instanceof PLFloat ? 1 : (base instanceof PLString ? 2 : 0)}; //int=0, float=1, string=2
                List<Boolean> addition = new LinkedList<>();
                List<PLData> rest = new LinkedList<>();
                final boolean[] containsMinus = {false};
                ASTElemList terms = (ASTElemList) tree.getChild(1);
                terms.forEach(astElem -> {
                    PLData get = run((AST) ((ASTElemList)astElem).get(2), paramAssignments);
                    boolean isAddition = ((ASTElemList)astElem).get(0).parseString().equals("+");
                    addition.add(isAddition);
                    containsMinus[0] = containsMinus[0] || !isAddition;
                    rest.add(get);
                    if (get instanceof PLFloat && type[0] < 1) {
                        type[0] = 1;
                    } else if (get instanceof PLString && type[0] < 2) {
                        type[0] = 2;
                    }
                });
                if (type[0] == 1) {
                    PLFloat baseFloat = base instanceof PLFloat ? (PLFloat) base : new PLFloat((PLInteger) base);
                    for (int i = 0; i < rest.size(); i++) {
                        PLFloat otherFloat = rest.get(i) instanceof  PLFloat ? (PLFloat) rest.get(i) : new PLFloat((PLInteger) rest.get(i));
                        if (addition.get(i)) {
                            baseFloat.set(baseFloat.get().add(otherFloat.get()));
                        } else {
                            baseFloat.set(baseFloat.get().subtract(otherFloat.get()));
                        }
                    }
                    return baseFloat;
                } else if (type[0] == 0) {
                    PLInteger baseInt = (PLInteger) base;
                    for (int i = 0; i < rest.size(); i++) {
                        PLInteger otherInt = (PLInteger) rest.get(i);
                        if (addition.get(i)) {
                            baseInt.set(baseInt.get().add(otherInt.get()));
                        } else {
                            baseInt.set(baseInt.get().subtract(otherInt.get()));
                        }
                    }
                    return baseInt;
                } else {
                    if (containsMinus[0]) {
                        throw new IllegalArgumentException("Cannot use a String in subtraction");
                    }
                    List<PLString> asStrings = new LinkedList<>();
                    asStrings.add(new PLString(base));
                    rest.forEach(other -> asStrings.add(new PLString(other)));
                    return new PLString(asStrings);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processComparitiveExpression(AST tree, Map<String, ASTElem>  paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("ComparitiveExpression").addRhs(bound(nonTerm("AdditiveExpression"), "e", false), bound(star(nonTerm("Comparator"), ws(), nonTerm("AdditiveExpression")), "e2", false), ws()))) {
            PLData reference = run((AST) tree.getChild(0), paramAssignments);
            ASTElemList others = (ASTElemList) tree.getChild(1);
            if (others.size() == 0) {
                return reference;
            } else {
                for (ASTElem other : others) {
                    PLData toCompareTo = run((AST) ((ASTElemList) other).get(2), paramAssignments);
                    String referenceString = reference.toString();
                    switch (((ASTElemList) other).get(0).parseString()) {
                        case "==":
                            if (!reference.equals(toCompareTo)) {
                                return PLBoolean.getFalse();
                            }
                            break;
                        case "!=":
                            if (reference.equals(toCompareTo)) {
                                return PLBoolean.getFalse();
                            }
                            break;
                        case ">=":
                            if (reference instanceof PLString && toCompareTo instanceof PLString) {
                                if (referenceString.compareTo(toCompareTo.toString()) <= 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else if ((reference instanceof PLInteger || reference instanceof PLFloat) &&
                                    (toCompareTo instanceof PLInteger || toCompareTo instanceof PLFloat)) {
                                PLFloat referenceAsFloat = reference instanceof PLFloat ? (PLFloat) reference : new PLFloat((PLInteger) reference);
                                PLFloat toCompareToAsFloat = toCompareTo instanceof PLFloat ? (PLFloat) toCompareTo : new PLFloat((PLInteger) toCompareTo);
                                if (referenceAsFloat.get().compareTo(toCompareToAsFloat.get()) < 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else {
                                throw new IllegalArgumentException("Incompatible types for >: " + reference.classString() + ", " + toCompareTo.classString());
                            }
                            break;
                        case "<=":
                            if (reference instanceof PLString && toCompareTo instanceof PLString) {
                                if (referenceString.compareTo(toCompareTo.toString()) <= 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else if ((reference instanceof PLInteger || reference instanceof PLFloat) &&
                                    (toCompareTo instanceof PLInteger || toCompareTo instanceof PLFloat)) {
                                PLFloat referenceAsFloat = reference instanceof PLFloat ? (PLFloat) reference : new PLFloat((PLInteger) reference);
                                PLFloat toCompareToAsFloat = toCompareTo instanceof PLFloat ? (PLFloat) toCompareTo : new PLFloat((PLInteger) toCompareTo);
                                if (referenceAsFloat.get().compareTo(toCompareToAsFloat.get()) > 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else {
                                throw new IllegalArgumentException("Incompatible types for >: " + reference.classString() + ", " + toCompareTo.classString());
                            }
                            break;
                        case ">":
                            if (reference instanceof PLString && toCompareTo instanceof PLString) {
                                if (referenceString.compareTo(toCompareTo.toString()) <= 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else if ((reference instanceof PLInteger || reference instanceof PLFloat) &&
                                    (toCompareTo instanceof PLInteger || toCompareTo instanceof PLFloat)) {
                                PLFloat referenceAsFloat = reference instanceof PLFloat ? (PLFloat) reference : new PLFloat((PLInteger) reference);
                                PLFloat toCompareToAsFloat = toCompareTo instanceof PLFloat ? (PLFloat) toCompareTo : new PLFloat((PLInteger) toCompareTo);
                                if (referenceAsFloat.get().compareTo(toCompareToAsFloat.get()) <= 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else {
                                throw new IllegalArgumentException("Incompatible types for >: " + reference.classString() + ", " + toCompareTo.classString());
                            }
                            break;
                        case "<":
                            if (reference instanceof PLString && toCompareTo instanceof PLString) {
                                if (referenceString.compareTo(toCompareTo.toString()) >= 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else if ((reference instanceof PLInteger || reference instanceof PLFloat) &&
                                    (toCompareTo instanceof PLInteger || toCompareTo instanceof PLFloat)) {
                                PLFloat referenceAsFloat = reference instanceof PLFloat ? (PLFloat) reference : new PLFloat((PLInteger) reference);
                                PLFloat toCompareToAsFloat = toCompareTo instanceof PLFloat ? (PLFloat) toCompareTo : new PLFloat((PLInteger) toCompareTo);
                                if (referenceAsFloat.get().compareTo(toCompareToAsFloat.get()) >= 0) {
                                    return PLBoolean.getFalse();
                                }
                            } else {
                                throw new IllegalArgumentException("Incompatible types for >: " + reference.classString() + ", " + toCompareTo.classString());
                            }
                            break;
                    }
                    reference = toCompareTo;
                }
                return PLBoolean.getTrue();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processExpression(AST tree, Map<String, ASTElem>  paramAssignments) {
        if (isSimpleRule(tree.getRule())) {
            return run((AST) tree.getChild(0), paramAssignments);
        }
        if (tree.getRule().getOrigin().equals(new ParseRule("Expression").addRhs(bound(nonTerm("ComparitiveExpression"), "e", false)))) {
            if (tree.getChildren().size() == 1) {
                return run((AST) tree.getChild(0), paramAssignments);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private PLData processHighLevel(AST tree, Map<String, ASTElem>  paramAssignments) {
        ASTElemList declarationList = (ASTElemList) tree.getChild(0);
        for (ASTElem astElem : declarationList) {
            AST declaration = (AST) ((ASTElemList) astElem).get(1);
            addDeclarationAsFunction(declaration);
        }
        AST optionalExpression = (AST) tree.getChild(2);
        if (optionalExpression.getChildren().size() == 0) {
            return null;
        } else {
            return run((AST) optionalExpression.getChild(0), paramAssignments);
        }
    }
}
