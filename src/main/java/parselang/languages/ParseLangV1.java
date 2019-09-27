package parselang.languages;

import parselang.parser.data.ParseRule;

import java.util.LinkedList;
import java.util.List;

import static parselang.parser.ParseRuleStorage.*;

public class ParseLangV1 implements Language {


    @Override
    public List<ParseRule> getRules() {
        List<ParseRule> rules = new LinkedList<>();
        rules.add(new ParseRule("WhiteSpace").addRhs(term("\t")));
        rules.add(new ParseRule("WhiteSpace").addRhs(term("\n")));
        rules.add(new ParseRule("WhiteSpace").addRhs(term("\r")));
        rules.add(new ParseRule("WhiteSpace").addRhs(term(" ")));

        rules.add(new ParseRule("LowerCase").addRhs(term("a")));
        rules.add(new ParseRule("LowerCase").addRhs(term("b")));
        rules.add(new ParseRule("LowerCase").addRhs(term("c")));
        rules.add(new ParseRule("LowerCase").addRhs(term("d")));
        rules.add(new ParseRule("LowerCase").addRhs(term("e")));
        rules.add(new ParseRule("LowerCase").addRhs(term("f")));
        rules.add(new ParseRule("LowerCase").addRhs(term("g")));
        rules.add(new ParseRule("LowerCase").addRhs(term("h")));
        rules.add(new ParseRule("LowerCase").addRhs(term("i")));
        rules.add(new ParseRule("LowerCase").addRhs(term("j")));
        rules.add(new ParseRule("LowerCase").addRhs(term("k")));
        rules.add(new ParseRule("LowerCase").addRhs(term("l")));
        rules.add(new ParseRule("LowerCase").addRhs(term("m")));
        rules.add(new ParseRule("LowerCase").addRhs(term("n")));
        rules.add(new ParseRule("LowerCase").addRhs(term("o")));
        rules.add(new ParseRule("LowerCase").addRhs(term("p")));
        rules.add(new ParseRule("LowerCase").addRhs(term("q")));
        rules.add(new ParseRule("LowerCase").addRhs(term("r")));
        rules.add(new ParseRule("LowerCase").addRhs(term("s")));
        rules.add(new ParseRule("LowerCase").addRhs(term("t")));
        rules.add(new ParseRule("LowerCase").addRhs(term("u")));
        rules.add(new ParseRule("LowerCase").addRhs(term("v")));
        rules.add(new ParseRule("LowerCase").addRhs(term("w")));
        rules.add(new ParseRule("LowerCase").addRhs(term("x")));
        rules.add(new ParseRule("LowerCase").addRhs(term("y")));
        rules.add(new ParseRule("LowerCase").addRhs(term("z")));

        rules.add(new ParseRule("UpperCase").addRhs(term("A")));
        rules.add(new ParseRule("UpperCase").addRhs(term("B")));
        rules.add(new ParseRule("UpperCase").addRhs(term("C")));
        rules.add(new ParseRule("UpperCase").addRhs(term("D")));
        rules.add(new ParseRule("UpperCase").addRhs(term("E")));
        rules.add(new ParseRule("UpperCase").addRhs(term("F")));
        rules.add(new ParseRule("UpperCase").addRhs(term("G")));
        rules.add(new ParseRule("UpperCase").addRhs(term("H")));
        rules.add(new ParseRule("UpperCase").addRhs(term("I")));
        rules.add(new ParseRule("UpperCase").addRhs(term("J")));
        rules.add(new ParseRule("UpperCase").addRhs(term("K")));
        rules.add(new ParseRule("UpperCase").addRhs(term("L")));
        rules.add(new ParseRule("UpperCase").addRhs(term("M")));
        rules.add(new ParseRule("UpperCase").addRhs(term("N")));
        rules.add(new ParseRule("UpperCase").addRhs(term("O")));
        rules.add(new ParseRule("UpperCase").addRhs(term("P")));
        rules.add(new ParseRule("UpperCase").addRhs(term("Q")));
        rules.add(new ParseRule("UpperCase").addRhs(term("R")));
        rules.add(new ParseRule("UpperCase").addRhs(term("S")));
        rules.add(new ParseRule("UpperCase").addRhs(term("T")));
        rules.add(new ParseRule("UpperCase").addRhs(term("U")));
        rules.add(new ParseRule("UpperCase").addRhs(term("V")));
        rules.add(new ParseRule("UpperCase").addRhs(term("W")));
        rules.add(new ParseRule("UpperCase").addRhs(term("X")));
        rules.add(new ParseRule("UpperCase").addRhs(term("Y")));
        rules.add(new ParseRule("UpperCase").addRhs(term("Z")));

        rules.add(new ParseRule("Number").addRhs(term("0")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("1")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("2")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("3")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("4")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("5")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("6")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("7")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("8")));
        rules.add(new ParseRule("NonZeroNumber").addRhs(term("9")));
        rules.add(new ParseRule("Number").addRhs(nonTerm("NonZeroNumber")));


        rules.add(new ParseRule("UpperOrLowerCase").addRhs(nonTerm("LowerCase")));
        rules.add(new ParseRule("UpperOrLowerCase").addRhs(nonTerm("UpperCase")));

        rules.add(new ParseRule("SafeSpecial").addRhs(term(";")));
        rules.add(new ParseRule("SafeSpecial").addRhs(term("}")));
        rules.add(new ParseRule("SafeSpecial").addRhs(term("{")));
        rules.add(new ParseRule("SafeSpecial").addRhs(term("+")));
        rules.add(new ParseRule("SafeSpecial").addRhs(term("*")));
        rules.add(new ParseRule("SafeSpecial").addRhs(term("/")));
        rules.add(new ParseRule("SafeSpecial").addRhs(term("-")));

        rules.add(new ParseRule("SafeChar").addRhs(nonTerm("UpperOrLowerCase")));
        rules.add(new ParseRule("SafeChar").addRhs(nonTerm("Number")));
        rules.add(new ParseRule("SafeChar").addRhs(nonTerm("SafeSpecial")));

        rules.add(new ParseRule("NonTerminal").addRhs(nonTerm("UpperCase")).addRhs(star(nonTerm("UpperOrLowerCase"))));

        rules.add(new ParseRule("StringLiteral").addRhs(term("'"))
                .addRhs(star(nonTerm("SafeChar"))).addRhs(term("'")));
        rules.add(new ParseRule("Token").addRhs(nonTerm("StringLiteral"), nonTerm("PotentialStar")));
        rules.add(new ParseRule("BracketToken").addRhs(term("(")).addRhs(star(ws(), nonTerm("Token"))).addRhs(term(")")));

        rules.add(new ParseRule("Token").addRhs(nonTerm("BracketToken"), nonTerm("PotentialStar")));
        rules.add(new ParseRule("Token").addRhs(nonTerm("NonTerminal"), nonTerm("PotentialStar"), ws(), nonTerm("PotentialVariable")));
        rules.add(new ParseRule("PotentialVariable").addRhs(nonTerm("Variable")));
        rules.add(new ParseRule("PotentialVariable"));

        rules.add(new ParseRule("Variable").addRhs(nonTerm("LowerCase"), star(nonTerm("UpperOrLowerCase")), nonTerm("PotentialLazy")));
        rules.add(new ParseRule("PotentialLazy").addRhs(term("'")));
        rules.add(new ParseRule("PotentialLazy"));
        rules.add(new ParseRule("PotentialStar").addRhs(term("*")));
        rules.add(new ParseRule("PotentialStar"));

        rules.add(new ParseRule("Comparator").addRhs(term("<")));
        rules.add(new ParseRule("Comparator").addRhs(term(">")));

        rules.add(new ParseRule("Sentence").addRhs(nonTerm("Expression")));

        rules.add(new ParseRule("Sentence").addRhs(term("return"), ws(), nonTerm("Expression")));

        rules.add(new ParseRule("NumberLiteral").addRhs(term("0")));
        rules.add(new ParseRule("NumberLiteral").addRhs(nonTerm("NonZeroNumber"), star(nonTerm("Number"))));

        rules.add(new ParseRule("BooleanLiteral").addRhs(term("true")));
        rules.add(new ParseRule("BooleanLiteral").addRhs(term("false")));

        rules.add(new ParseRule("Expression").addRhs(nonTerm("ComparitiveExpression")));
        rules.add(new ParseRule("ComparitiveExpression").addRhs(nonTerm("AdditiveExpression"), star(nonTerm("Comparator"), ws(), nonTerm("AdditiveExpression")), ws()));
        rules.add(new ParseRule("AdditiveExpression").addRhs(nonTerm("MultiplicativeExpression"), star(term("+"), ws(), nonTerm("MultiplicativeExpression")), ws()));
        rules.add(new ParseRule("MultiplicativeExpression").addRhs(nonTerm("SimpleExpression"), star(term("*"), ws(), nonTerm("SimpleExpression")), ws()));
        rules.add(new ParseRule("SimpleExpression").addRhs(nonTerm("NumberLiteral"), ws()));
        rules.add(new ParseRule("SimpleExpression").addRhs(nonTerm("StringLiteral"), ws()));
        rules.add(new ParseRule("SimpleExpression").addRhs(nonTerm("BooleanLiteral"), ws()));




        //rules.add(new ParseRule("PlusExpression").addRhs(nonTerm("Expression"), ws(), term("+"), ws(), nonTerm("Expression")));
        //rules.add(new ParseRule("PowerExpression").addRhs(nonTerm("Expression"), term("^"), nonTerm("Expression")));
        //rules.add(new ParseRule("Expression").addRhs(nonTerm("PlusExpression")));
        //rules.add(new ParseRule("Expression").addRhs(nonTerm("PowerExpression")));

        rules.add(new ParseRule("Declaration").addRhs(
                nonTerm("NonTerminal"),
                ws(),
                nonTerm("Comparator"),
                ws(),
                nonTerm("NonTerminal"),
                ws(),
                term("="),
                star(ws(), nonTerm("Token")),
                ws(),
                term("{"),
                star(ws(), nonTerm("Sentence"), term(";")),
                ws(),
                term("}")
        ));


        rules.add(new ParseRule("HighLevel").addRhs(
                star(
                        ws(),
                        nonTerm("Declaration")
                ),
                ws())
        );
        return rules;
    }
}
