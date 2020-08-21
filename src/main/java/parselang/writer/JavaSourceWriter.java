package parselang.writer;

import parselang.intermediate.dataholders.*;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.util.Sanitizer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static parselang.parser.ParseRuleStorage.*;

public class JavaSourceWriter extends Writer {

    private Sanitizer sanitizer = new Sanitizer();
    private Map<ParseRule, String> premadeMethods;


    @Override
    public String writeToString(Program program, ParseRuleStorage storage) {
        StringBuilder sb = new StringBuilder();
        writeImports(sb, program);
        writePackage(sb, program);
        writeClass(sb, program, storage);
        return sb.toString();
    }

    private void writeClass(StringBuilder sb, Program program, ParseRuleStorage storage) {
        sb.append("public class " + program.getName() + " {\n");
        writeStacks(sb, program, storage);
        writeLibMethods(sb, program, storage);
        writeMethods(sb, program);
        writeMain(sb, program);
        sb.append("}\n");
    }

    private Map<ParseRule, String> standardLib = new HashMap<>();
    private void fillStandardLib() {
        standardLib.put(new ParseRule("LowerCase").addRhs(term("a")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("b")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("c")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("d")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("e")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("f")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("g")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("h")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("i")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("j")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("k")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("l")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("m")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("n")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("o")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("p")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("q")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("r")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("s")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("t")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("u")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("v")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("w")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("x")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("y")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("LowerCase").addRhs(term("z")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("A")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("B")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("C")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("D")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("E")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("F")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("G")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("H")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("I")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("J")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("K")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("L")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("M")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("N")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("O")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("P")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("Q")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("R")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("S")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("T")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("U")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("V")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("W")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("X")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("Y")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperCase").addRhs(term("Z")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("Number").addRhs(term("0")), "(Object o) {\n\t\treturn 0;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("1")), "(Object o) {\n\t\treturn 1;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("2")), "(Object o) {\n\t\treturn 2;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("3")), "(Object o) {\n\t\treturn 3;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("4")), "(Object o) {\n\t\treturn 4;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("5")), "(Object o) {\n\t\treturn 5;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("6")), "(Object o) {\n\t\treturn 6;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("7")), "(Object o) {\n\t\treturn 7;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("8")), "(Object o) {\n\t\treturn 8;\n\t}\n");
        standardLib.put(new ParseRule("NonZeroNumber").addRhs(term("9")), "(Object o) {\n\t\treturn 9;\n\t}\n");
        standardLib.put(new ParseRule("WhiteSpace").addRhs(term(" ")), "(Object o) {\n\t\treturn null;\n\t}\n");
        standardLib.put(new ParseRule("WhiteSpace").addRhs(term("\t")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("WhiteSpace").addRhs(term("\n")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("WhiteSpace").addRhs(term("\r")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("Number").addRhs(nonTerm("NonZeroNumber")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperOrLowerCase").addRhs(nonTerm("LowerCase")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperOrLowerCase").addRhs(nonTerm("UpperCase")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperOrLowerCaseOrNumber").addRhs(nonTerm("UpperOrLowerCase")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("UpperOrLowerCaseOrNumber").addRhs(nonTerm("Number")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term(";")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term("}")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term("{")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term("+")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term("*")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term("/")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeSpecial").addRhs(term("-")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeChar").addRhs(nonTerm("UpperOrLowerCase")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeChar").addRhs(nonTerm("Number")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("SafeChar").addRhs(nonTerm("SafeSpecial")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("StringLiteral").addRhs(term("'"), star(nonTerm("SafeChar")), term("'")), "(Object o1, Object o2, Object o3) {\n\t\treturn \"idk debug\";\n\t}\n");
        standardLib.put(new ParseRule("BooleanLiteral").addRhs(term("true")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("BooleanLiteral").addRhs(term("false")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("NumberLiteral").addRhs(term("0")), "(Object o) {\n\t\treturn 0;\n\t}\n");
        standardLib.put(new ParseRule("Comparator").addRhs(term("<")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("Comparator").addRhs(term(">")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("Sentence").addRhs(nonTerm("Expression")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("Sentence").addRhs(term("return"), ws(), nonTerm("Expression")), "(Object o1, Object o2, Object o3) {\n\t\treturn o3;\n\t}\n");
        standardLib.put(new ParseRule("Sentence").addRhs(term("print"), ws(), nonTerm("Expression")), "(Object o1, Object o2, Object o3) {\n\t\tSystem.out.println(o3);\n\t\treturn null;\n\t}\n");
        standardLib.put(new ParseRule("Expression").addRhs(nonTerm("ComparitiveExpression")), "(Object o) {\n\t\treturn o;\n\t}\n");
        standardLib.put(new ParseRule("NumberLiteral").addRhs(nonTerm("NonZeroNumber"), star(nonTerm("Number"))), "(Object o1, Object o2) {\n\t\tInteger res = (Integer) o1;\n\t\tfor (Object i : ((List<Object>)o2)) {\n\t\t\tres = 10*res +  (Integer)i;\n\t\t}\n\t\treturn res;\n\t}\n");
        standardLib.put(new ParseRule("DeclarationContent").addRhs(star(nonTerm("Sentence"), term(";"), ws())), "(Object o) {\n\t\treturn o;\n\t}\n");

        standardLib.put(new ParseRule("ComparitiveExpression").addRhs(nonTerm("AdditiveExpression"), star(nonTerm("Comparator"), ws(), nonTerm("AdditiveExpression")), ws()), "(Object o1, Object o2, Object o3) {\n\tComparable res = (Comparable) o1;\n\tif (((List)o2).size() > 1) {\n\t\tthrow new UnsupportedOperationException();\n\t} else if (((List)o2).size() == 0) {\n\t\treturn o1;\n\t}\n\treturn (res.compareTo(((List)((List)o2).get(0)).get(1)) > 0 && ((List)((List)o2).get(0)).get(0).equals(\">\")) || (res.compareTo(((List)((List)o2).get(0)).get(1)) < 0 && ((List)((List)o2).get(0)).get(0).equals(\"<\"));\n\t}\n");
        standardLib.put(new ParseRule("AdditiveExpression").addRhs(nonTerm("MultiplicativeExpression"), star(term("+"), ws(), nonTerm("MultiplicativeExpression")), ws()), "(Object o1, Object o2, Object o3) {\n\t\tInteger res = (Integer) o1;\n\t\tfor (List<Object> i : ((List<List<Object>>)o2)) {\n\t\t\tres += (Integer)i.get(2);\n\t\t}\n\t\treturn res;\n\t}\n");
        standardLib.put(new ParseRule("MultiplicativeExpression").addRhs(nonTerm("SimpleExpression"), star(term("*"), ws(), nonTerm("SimpleExpression")), ws()), "(Object o1, Object o2, Object o3) {\n\t\tInteger res = (Integer) o1;\n\t\tfor (List<Object> i : ((List<List<Object>>)o2)) {\n\t\t\tres *= (Integer)i.get(2);\n\t\t}\n\t\treturn res;\n\t}\n");

        standardLib.put(new ParseRule("SimpleExpression").addRhs(nonTerm("NumberLiteral"), ws()), "(Object o1, Object o2) {\n\t\treturn o1;\n\t}\n");
        standardLib.put(new ParseRule("SimpleExpression").addRhs(nonTerm("StringLiteral"), ws()), "(Object o1, Object o2) {\n\t\treturn \"FFFFFF\";\n\t}\n");
        standardLib.put(new ParseRule("SimpleExpression").addRhs(nonTerm("BooleanLiteral"), ws()), "(Object o1, Object o2) {\n\t\treturn \"foo123\";\n\t}\n");

        standardLib.put(new ParseRule("BooleanLiteral").addRhs(nonTerm("StringLiteral"), ws()), "(Object o1, Object o2) {\n\t\treturn \"FFFFFF\";\n\t}\n");
    }




    private void writeLibMethods(StringBuilder sb, Program program, ParseRuleStorage storage) {
        fillStandardLib();
        for (int i = 0; i < storage.getIDSpace(); i++) {
            //System.out.println(i);
            final int j = i;
            if (!storage.getAddedRules().contains(storage.getRuleByID(i))) {
                ParseRule rule = storage.getRuleByID(i).getOrigin();
                String toAdd = "";
                if (standardLib.containsKey(rule)) {
                    toAdd = "\t//" + rule.toString() + "\n\tprivate Object rule" + i + standardLib.get(rule) ;
                } else {
                    int o = 1;
                }
                sb.append(toAdd);
            }
        }
    }

    private void writeMethods(StringBuilder sb, Program program) {
        for (Declaration decl : program.getDeclarations()) {
            //add actual rule
            sb.append("\tprivate Object rule" + decl.getRuleID() + "(");
            for (int i = 0; i < decl.getParams().size(); i++) {
                sb.append("Object " + sanitizer.objectName(decl.getParams().get(i)));
                if (i < decl.getParams().size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("){\n");
            sb.append("\t\tstore" + decl.getName() + ".push(new HashMap<>());\n");

            writeContent(decl.getContent(), sb, program);

            sb.append("\t\tstore" + decl.getName() + ".pop();\n");
            sb.append("\t\treturn res;\n");
            sb.append("\t}\n");

            sb.append("\tprivate Object rule" + decl.getInheritedId() + "(Object o){return rule" + decl.getRuleID() + "();}\n\n");

        }
    }

    private void writeContent(DeclUse decl, StringBuilder sb, Program program) {
        ArgumentList arguments = decl.getArgs();
        for (Argument arg : arguments) {
            String functionCall = constructFunctionCall(decl);
            sb.append("\t\tObject res = " + functionCall + ";\n");
        }
    }

    private String constructFunctionCall(Argument arg) {
        if (arg instanceof ArgumentList) {
            if (((ArgumentList) arg).isEmpty()) {
                return "Collections.emptyList()";
            } else if (((ArgumentList) arg).nature == ArgumentList.Nature.RHS) {
                return ((ArgumentList) arg).stream().map(this::constructFunctionCall).collect(Collectors.joining(", "));
            } else if (((ArgumentList) arg).nature == ArgumentList.Nature.STAR) {
                return "Arrays.asList(" + ((ArgumentList) arg).stream().map(this::constructFunctionCall).collect(Collectors.joining(", ")) + ")";
            } else {
                throw new UnsupportedOperationException();
            }
        } else if (arg instanceof DeclUse) {
            if (((DeclUse) arg).isTerminal()) {
                return ((DeclUse) arg).objectNotation();
            } else {
                return ((DeclUse) arg).objectNotation() + "(" + constructFunctionCall(((DeclUse) arg).getArgs()) + ")";
            }
        } else if (arg instanceof ConstantString) {
            return sanitizer.makeStringConstant(((ConstantString) arg).getContent());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void writeMain(StringBuilder sb, Program program) {
        sb.append("\tpublic static void main(String[] args) {\n" +
                "\t\tnew Example().rule113(\"Hellothisismyexp\");\n" +
                "\t}");
    }

    private void writeStacks(StringBuilder sb, Program program, ParseRuleStorage storage) {
        for (NonTerminal i : storage.getAllNonTerminals().stream().filter(nonTerminal -> nonTerminal.getSpecialStatus() == NonTerminal.SpecialStatus.NONE).collect(Collectors.toList())) {
            sb.append("\tprivate Deque<Map<String, Object>> store" + sanitizer.classify(i.getName()) + " = new ArrayDeque<>();\n");
        }
    }

    private void writePackage(StringBuilder sb, Program program) {
    }

    private void writeImports(StringBuilder sb, Program program) {
        sb.append("import java.util.*;\n");
        sb.append("import java.util.stream.Collectors;\n");


    }
}
