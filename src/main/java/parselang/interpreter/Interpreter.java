package parselang.interpreter;

import parselang.interpreter.data.*;
import parselang.parser.data.*;
import parselang.util.DeclarationTree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static parselang.parser.ParseRuleStorage.*;

public class Interpreter {

    private static PLData runList(String originalString, ASTElemList list) {
        PLList result = new PLList();
        list.forEach(astElem -> {
            if (astElem instanceof ASTElemList) {
                result.add(runList(originalString, (ASTElemList) astElem));
            } else if (astElem instanceof AST) {
                result.add(run(originalString, (AST) astElem));
            }
        });
        return result;
    }


    public static PLData run(String originalString, AST result) {
        if (result.getRoot() instanceof NonTerminal) {
            return runNonTerminal(originalString, result);
        } else if (result.getRoot() instanceof Terminal) {
            return runTerminal(result);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static Map<ParseRule, Function<Map<String, Object>, PLData>> declarations = new HashMap<>();
    private static Map<String, Object> variableAssignments = new HashMap<>();
    private static Map<NonTerminal, Deque<PLMap>> data = new HashMap<>();

    private static void addDeclarationAsFunction(String originalString, AST declaration) {
        DeclarationTree declTree = new DeclarationTree(originalString, declaration);
        ParseRule rule = new ParseRule(declTree.name).addRhs(declTree.retrievedNodes.toArray(new Node[0]));
        AST declarationContent = (AST) declaration.getChild(11);
        declarations.put(rule, stringObjectMap -> {
            variableAssignments = stringObjectMap;
            return run(originalString, declarationContent);
        });
    }

    private static PLData runTerminal(AST result) {
        return new PLString(((Terminal) result.getRoot()).getValue());
    }

    private static Deque<Map<String, ParameterValue>> parameterBindings = new LinkedList<>();

    private static PLData runNonTerminal(String originalString, AST result) {
        data.computeIfAbsent((NonTerminal) result.getRoot(), nonTerminal -> new LinkedList<>());
        data.get(result.getRoot()).offerFirst(new PLMap());
        PLData toReturn;
        if (declarations.containsKey(result.getRule().getOrigin())) {
            parameterBindings.offerFirst(new HashMap<>());
            for (int i = 0; i < result.getRule().getRHS().size(); i++) {
                Node ithNode = result.getRule().getRHS().get(i);
                if (ithNode instanceof  BoundNonTerminal) {
                    if (((BoundNonTerminal) ithNode).isLazy()) {
                        parameterBindings.peekFirst().put(((BoundNonTerminal) ithNode).getName(), result.getChildren().get(i));
                    } else {
                        PLData parameterValue;
                        if (result.getChild(i) instanceof AST) {
                            parameterValue = run(originalString, (AST) result.getChild(i));
                        } else {
                            parameterValue = runList(originalString, (ASTElemList) result.getChild(i));
                        }
                        parameterBindings.peekFirst().put(((BoundNonTerminal) ithNode).getName(), parameterValue);
                    }
                }
            }
            toReturn = declarations.get(result.getRule().getOrigin()).apply(variableAssignments);
            parameterBindings.pollFirst();
        } else {
            switch (((NonTerminal) (result.getRoot())).getName()) {
                case "HighLevel":
                    toReturn = processHighLevel(originalString, result);
                    break;
                case "Expression":
                    toReturn =  processExpression(originalString, result);
                    break;
                case "ComparitiveExpression":
                    toReturn =  processComparitiveExpression(originalString, result);
                    break;
                case "AdditiveExpression":
                    toReturn =  processAdditiveExpression(originalString, result);
                    break;
                case "MultiplicativeExpression":
                    toReturn =  processMultiplicativeExpression(originalString, result);
                    break;
                case "SimpleExpression":
                    toReturn =  processSimpleExpression(originalString, result);
                    break;
                case "DeclarationContent":
                    toReturn =  processDeclarationContent(originalString, result);
                    break;
                case "DelimitedSentence":
                    toReturn =  processDelimitedSentence(originalString, result);
                    break;
                case "Sentence":
                    toReturn =  processSentence(originalString, result);
                    break;
                case "StringLiteral":
                    toReturn =  processStringLiteral(originalString, result);
                    break;
                case "ListLiteral":
                    toReturn =  processListLiteral(originalString, result);
                    break;
                case "NumberLiteral":
                    toReturn =  processNumberLiteral(originalString, result);
                    break;
                case "OptionalDecimalPlaces":
                    toReturn =  processOptionalDecimalPlaces(originalString, result);
                    break;
                case "Data":
                    toReturn = processData(originalString, result);
                    break;
                case "OptionalAssignment":
                    toReturn = processOptionalAssignment(originalString, result);
                    break;
                case "NonZeroNumber":
                case "SafeChar":
                case "Number":
                case "UpperOrLowerCase":
                case "SafeSpecial":
                case "LowerCase":
                case "RegisteredNonTerminal":
                    toReturn =  processSimpleRule(originalString, result);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        data.get(result.getRoot()).pollFirst();
        if (data.get(result.getRoot()).isEmpty()) {
            data.remove(result.getRoot());
        }
        assert toReturn != null;
        return toReturn;
    }

    private static PLData processListLiteral(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("ListLiteral").addRhs(
                term("["),
                ws(),
                bound(nonTerm("Expression"), "e", false),
                ws(),
                bound(star(term(","), ws(), nonTerm("Expression"), ws()), "e2", false),
                term("]")))) {
            PLList toReturn = new PLList();
            PLData firstElement = run(originalString, (AST) result.getChild(2));
            toReturn.add(firstElement);
            ASTElemList others = (ASTElemList) result.getChild(4);
            for (ASTElem other : others) {
                AST expression = (AST) ((ASTElemList) other).get(2);
                toReturn.add(run(originalString, expression));
            }
            return toReturn;
        }
        throw new UnsupportedOperationException();
    }

    private static PLData processSentence(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("Sentence").addRhs(nonTerm("DelimitedSentence"), ws(), term(";")))) {
            return run(originalString, (AST) result.getChild(0));
        }
        throw new UnsupportedOperationException();
    }

    private static PLData processOptionalAssignment(String originalString, AST result) {
        if (result.getChildren().isEmpty()) {
            return PLNull.get();
        } else {
            return run(originalString, (AST) result.getChild(2));
        }
    }

    private static PLData processData(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("Data").addRhs(nonTerm("RegisteredNonTerminal"), term("["), bound(nonTerm("Expression"), "e", false), term("]"), ws(), nonTerm("OptionalAssignment")))) {
            PLString whichMap = (PLString) run(originalString, (AST) result.getChild(0));
            PLData optionalAssignment = run(originalString, (AST) result.getChild(5));
            PLData key = run(originalString, (AST) result.getChild(2));
            boolean isAssignment = !(optionalAssignment instanceof PLNull);
            if (isAssignment) {
                data.get(nonTerm(whichMap.toString())).peekFirst().put(key, optionalAssignment);
                return optionalAssignment;
            } else {
                return data.get(nonTerm(whichMap.toString())).peekFirst().get(key);
            }
        }
        throw new UnsupportedOperationException();
    }


    private static PLData processOptionalDecimalPlaces(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("OptionalDecimalPlaces"))) {
            return PLNull.get();
        } else if (result.getRule().getOrigin().equals(new ParseRule("OptionalDecimalPlaces").addRhs(term("."), bound(star(nonTerm("Number")), "e", false)))) {
            PLInteger res = new PLInteger();
            ASTElemList decimals = (ASTElemList) result.getChild(1);
            decimals.forEach(astElem -> res.set(res.get().multiply(new BigInteger("10")).add(new PLInteger((PLString) run(originalString, (AST) astElem)).get())));
            return res;
        }
        throw new UnsupportedOperationException();
    }


    private static PLData processNumberLiteral(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("NumberLiteral").addRhs(nonTerm("OptionalMinus"), bound(nonTerm("NonZeroNumber"), "e", false), bound(star(nonTerm("Number")), "e2", false), nonTerm("OptionalDecimalPlaces")))) {
            PLInteger before = new PLInteger((PLString) run(originalString, (AST) result.getChild(1)));
            AST optionalMinus = (AST) result.getChild(0);
            if (optionalMinus.getChildren().size() == 1) {
                before.set(before.get().negate());
            }

            ASTElemList additionalDecimals = (ASTElemList) result.getChild(2);
            additionalDecimals.forEach(astElem -> {
                PLInteger toAdd = new PLInteger((PLString) run(originalString, (AST)astElem));
                before.set(before.get().multiply(new BigInteger("10")).add(toAdd.get()));
            });
            Object after = run(originalString, (AST) result.getChild(3));
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

    private static PLData processSimpleRule(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        } else if (!result.getRule().getRHS().isEmpty()) {
            throw new UnsupportedOperationException();
        } else {
            return PLNull.get();
        }
    }

    private static PLData processStringLiteral(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("StringLiteral").addRhs(term("'")).addRhs(star(nonTerm("SafeChar"))).addRhs(term("'")))) {
            ASTElemList characters = (ASTElemList) result.getChild(1);
            List<PLString> together = new ArrayList<>(characters.size());
            for (ASTElem character : characters) {
                together.add((PLString) run(originalString, (AST) character));
            }
            return new PLString(together);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static PLData processDelimitedSentence(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().equals(new ParseRule("DeclarationContent").addRhs(bound(nonTerm("DelimitedSentence"), "e", true), nonTerm("(WhiteSpace*)")))) {
            return run(originalString, (AST) result.getChild(0));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static PLData processDeclarationContent(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            if (result.getChild(0) instanceof ASTElemList && ((ASTElemList)result.getChild(0)).size() == 0) {
                return PLNull.get();
            }
            if (result.getChild(0) instanceof AST) {
                return run(originalString, (AST) result.getChild(0));
            } else if (result.getChild(0) instanceof ASTElemList) {
                PLData lastSentenceReturn = null;
                for (ASTElem astElem : ((ASTElemList) result.getChild(0))) {
                    lastSentenceReturn = run(originalString, (AST) ((ASTElemList)astElem).get(0));
                }
                return lastSentenceReturn;
            }
        }
        if (result.getRule().getOrigin().equals(new ParseRule("DeclarationContent").addRhs(bound(nonTerm("DelimitedSentence"), "e", true), ws()))) {
            return run(originalString, (AST) result.getChild(0));
        } else if (result.getRule().getOrigin().equals(new ParseRule("DeclarationContent").addRhs(bound(nonTerm("Sentence"), "e", true), ws(), bound(star(nonTerm("Sentence"), ws()), "e2", true)))) {
            AST firstSentence = (AST) result.getChild(0);
            PLData output = run(originalString, firstSentence);
            ASTElemList otherSentences = (ASTElemList) result.getChild(2);
            for (ASTElem otherSentence : otherSentences) {
                output = run(originalString, (AST)((ASTElemList)otherSentence).get(0));
            }
            return output;
        }
        throw new UnsupportedOperationException();
    }

    private static PLData processSimpleExpression(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("StringLiteral"), "e", false), nonTerm("(WhiteSpace*)")))) {
            return run(originalString, (AST) result.getChild(0));
        } else if (result.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("NumberLiteral"), "e", false), ws()))) {
            return run(originalString, (AST) result.getChild(0));
        } else if (result.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(term("("), ws(), bound(nonTerm("Expression"), "e", false), ws(), term(")")))) {
            return run(originalString, (AST) result.getChild(2));
        } else if (result.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("ParameterName"), "e", false), ws()))) {
            String parameterName = ((Terminal)((AST)((AST)result.getChild(0)).getChild(0)).getRoot()).getValue();
            ParameterValue value = parameterBindings.peekFirst().get(parameterName);
            if (value instanceof PLData) {
                return (PLData) value;
            } else if (value instanceof ASTElemList) {
               return runList(originalString, (ASTElemList) value);
            } else if (value instanceof AST) {
                return run(originalString, (AST) value);
            } else {
                throw new UnsupportedOperationException();
            }
        } else if (result.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(term("~concat"), ws(), term("("), ws(), bound(nonTerm("Expression"), "e", false), ws(), term(")"), ws()))) {
            PLData expression = run(originalString, (AST) result.getChild(4));
            if (expression instanceof PLList) {
                List<PLString> toConcat = new LinkedList<>();
                ((PLList)expression).forEach(plData -> toConcat.add(new PLString(plData)));
                return new PLString(toConcat);
            } else {
                return new PLString(expression);
            }
        } else if (result.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("Data"), "e", false), ws()))) {
            return run(originalString, (AST) result.getChild(0));
        } else if (result.getRule().getOrigin().equals(new ParseRule("SimpleExpression").addRhs(bound(nonTerm("ListLiteral"), "e", false), ws()))) {
            return run(originalString, (AST) result.getChild(0));
        }
        throw new UnsupportedOperationException();
    }

    private static boolean isSimpleRule(ParseRule rule) {
        return rule.getRHS().size() == 1;
    }

    private static PLData processMultiplicativeExpression(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().equals(new ParseRule("MultiplicativeExpression").addRhs(bound(nonTerm("SimpleExpression"), "e", false),
                bound(
                        nonTerm("((* WhiteSpace* SimpleExpression)*)"),
                        "e2", false
                ), nonTerm("(WhiteSpace*)")))) {
            PLData base = run(originalString, (AST) result.getChild(0));
            if (((ASTElemList)result.getChild(1)).size() == 0) {
                return base;
            } else {
                final boolean[] isFloat = {base instanceof PLFloat};
                List<PLData> rest = new LinkedList<>();
                ASTElemList factors = (ASTElemList) result.getChild(1);
                factors.forEach(astElem -> {
                    PLData get = run(originalString, (AST) ((ASTElemList)astElem).get(2));
                    rest.add(get);
                    isFloat[0] = isFloat[0] || get instanceof PLFloat;
                });
                if (isFloat[0]) {
                    PLFloat baseFloat = base instanceof PLFloat ? (PLFloat) base : new PLFloat((PLInteger) base);
                    rest.forEach(other -> {
                        PLFloat otherFloat = other instanceof  PLFloat ? (PLFloat) other : new PLFloat((PLInteger) other);
                        baseFloat.set(baseFloat.get().multiply(otherFloat.get()));
                    });
                    return baseFloat;
                } else {
                    PLInteger baseInt = (PLInteger) base;
                    rest.forEach(other -> {
                        PLInteger otherInt = (PLInteger) other;
                        baseInt.set(baseInt.get().multiply(otherInt.get()));
                    });
                    return baseInt;
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static PLData processAdditiveExpression(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().getOrigin().equals(new ParseRule("AdditiveExpression").addRhs(bound(nonTerm("MultiplicativeExpression"), "e", false), bound(star(nonTerm("PlusOrMinus"), ws(), nonTerm("MultiplicativeExpression")), "e2", false), ws()))) {
            PLData base = run(originalString, (AST) result.getChild(0));
            if (((ASTElemList)result.getChild(1)).size() == 0) {
                return base;
            } else {
                final int[] type = {base instanceof PLFloat ? 1 : (base instanceof PLString ? 2 : 0)}; //int=0, float=1, string=2
                List<Boolean> addition = new LinkedList<>();
                List<PLData> rest = new LinkedList<>();
                final boolean[] containsMinus = {false};
                ASTElemList terms = (ASTElemList) result.getChild(1);
                terms.forEach(astElem -> {
                    PLData get = run(originalString, (AST) ((ASTElemList)astElem).get(2));
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
                    rest.forEach(other -> {

                    });
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

    private static PLData processComparitiveExpression(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().equals(new ParseRule("ComparitiveExpression").addRhs(bound(nonTerm("AdditiveExpression"), "e", false),
                bound(
                        nonTerm("((Comparator WhiteSpace* AdditiveExpression)*)"),
                        "e2", false
                ), nonTerm("(WhiteSpace*)")))) {
            if (((ASTElemList)result.getChild(1)).size() == 0) {
                return run(originalString, (AST) result.getChild(0));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static PLData processExpression(String originalString, AST result) {
        if (isSimpleRule(result.getRule())) {
            return run(originalString, (AST) result.getChild(0));
        }
        if (result.getRule().equals(new ParseRule("Expression").addRhs(new BoundNonTerminal(new NonTerminal("ComparitiveExpression"), "e", false)))) {
            if (result.getChildren().size() == 1) {
                return run(originalString, (AST) result.getChild(0));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static PLData processHighLevel(String originalString, AST result) {
        ASTElemList declarationList = (ASTElemList) result.getChild(0);
        for (ASTElem astElem : declarationList) {
            AST declaration = (AST) ((ASTElemList) astElem).get(1);
            addDeclarationAsFunction(originalString, declaration);
        }
        AST optionalExpression = (AST) result.getChild(2);
        if (optionalExpression.getChildren().size() == 0) {
            return null;
        } else {
            return run(originalString, (AST) optionalExpression.getChild(0) );
        }
    }
}
