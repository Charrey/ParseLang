package parselang.parser;

import parselang.parser.data.*;

import java.util.*;
import java.util.stream.Collectors;

public class ParseRuleStorage {

    private Map<NonTerminal, List<ParseRule>> rules = new HashMap<>();
    private Map<Node, Set<Character>> first = new HashMap<>();
    private Map<NonTerminal, Set<Character>> follow = new HashMap<>();
    private Map<ParseRule, Set<Character>> firstplus = new HashMap<>();


    private void addRule(ParseRule rule) {
        addRules(rule.convertStarNodes(), Direction.RIGHT);
    }

    private void addRules(Collection<ParseRule> rules, Direction dir) {
        for (ParseRule rule : rules) {
            NonTerminal nonTerminal = rule.getLHS();
            this.rules.computeIfAbsent(nonTerminal, nonTerminal1 -> new LinkedList<>());
            switch (dir) {
                case LEFT:
                    this.rules.get(nonTerminal).add(0, rule);
                    break;
                case RIGHT:
                    this.rules.get(nonTerminal).add(rule);
            }
        }
    }


    public void addRule(ParseRule rule, Direction dir) {
        addRules(rule.convertStarNodes(), dir);
    }

    List<ParseRule> getByNonTerminal(NonTerminal nonTerminal) {
        if (rules.containsKey(nonTerminal)) {
            return rules.get(nonTerminal);
        } else {
            System.out.println("Warning! No such rule! => " + nonTerminal.getName());
            return new LinkedList<>();
        }
    }

    private static Node ws() {
        return new StarNode(nonTerm("WhiteSpace"));
    }

    private static NonTerminal nonTerm(String name) {
        return new NonTerminal(name);
    }

    private static Terminal term(String name) {
        return new Terminal(name);
    }

    private static Node star(Node... content) {
        return new StarNode(content);
    }

    void setDefaults() {
        addRule(new ParseRule("WhiteSpace").addRhs(term("\t")));
        addRule(new ParseRule("WhiteSpace").addRhs(term("\n")));
        addRule(new ParseRule("WhiteSpace").addRhs(term("\r")));
        addRule(new ParseRule("WhiteSpace").addRhs(term(" ")));

        addRule(new ParseRule("LowerCase").addRhs(term("a")));
        addRule(new ParseRule("LowerCase").addRhs(term("b")));
        addRule(new ParseRule("LowerCase").addRhs(term("c")));
        addRule(new ParseRule("LowerCase").addRhs(term("d")));
        addRule(new ParseRule("LowerCase").addRhs(term("e")));
        addRule(new ParseRule("LowerCase").addRhs(term("f")));
        addRule(new ParseRule("LowerCase").addRhs(term("g")));
        addRule(new ParseRule("LowerCase").addRhs(term("h")));
        addRule(new ParseRule("LowerCase").addRhs(term("i")));
        addRule(new ParseRule("LowerCase").addRhs(term("j")));
        addRule(new ParseRule("LowerCase").addRhs(term("k")));
        addRule(new ParseRule("LowerCase").addRhs(term("l")));
        addRule(new ParseRule("LowerCase").addRhs(term("m")));
        addRule(new ParseRule("LowerCase").addRhs(term("n")));
        addRule(new ParseRule("LowerCase").addRhs(term("o")));
        addRule(new ParseRule("LowerCase").addRhs(term("p")));
        addRule(new ParseRule("LowerCase").addRhs(term("q")));
        addRule(new ParseRule("LowerCase").addRhs(term("r")));
        addRule(new ParseRule("LowerCase").addRhs(term("s")));
        addRule(new ParseRule("LowerCase").addRhs(term("t")));
        addRule(new ParseRule("LowerCase").addRhs(term("u")));
        addRule(new ParseRule("LowerCase").addRhs(term("v")));
        addRule(new ParseRule("LowerCase").addRhs(term("w")));
        addRule(new ParseRule("LowerCase").addRhs(term("x")));
        addRule(new ParseRule("LowerCase").addRhs(term("y")));
        addRule(new ParseRule("LowerCase").addRhs(term("z")));

        addRule(new ParseRule("UpperCase").addRhs(term("A")));
        addRule(new ParseRule("UpperCase").addRhs(term("B")));
        addRule(new ParseRule("UpperCase").addRhs(term("C")));
        addRule(new ParseRule("UpperCase").addRhs(term("D")));
        addRule(new ParseRule("UpperCase").addRhs(term("E")));
        addRule(new ParseRule("UpperCase").addRhs(term("F")));
        addRule(new ParseRule("UpperCase").addRhs(term("G")));
        addRule(new ParseRule("UpperCase").addRhs(term("H")));
        addRule(new ParseRule("UpperCase").addRhs(term("I")));
        addRule(new ParseRule("UpperCase").addRhs(term("J")));
        addRule(new ParseRule("UpperCase").addRhs(term("K")));
        addRule(new ParseRule("UpperCase").addRhs(term("L")));
        addRule(new ParseRule("UpperCase").addRhs(term("M")));
        addRule(new ParseRule("UpperCase").addRhs(term("N")));
        addRule(new ParseRule("UpperCase").addRhs(term("O")));
        addRule(new ParseRule("UpperCase").addRhs(term("P")));
        addRule(new ParseRule("UpperCase").addRhs(term("Q")));
        addRule(new ParseRule("UpperCase").addRhs(term("R")));
        addRule(new ParseRule("UpperCase").addRhs(term("S")));
        addRule(new ParseRule("UpperCase").addRhs(term("T")));
        addRule(new ParseRule("UpperCase").addRhs(term("U")));
        addRule(new ParseRule("UpperCase").addRhs(term("V")));
        addRule(new ParseRule("UpperCase").addRhs(term("W")));
        addRule(new ParseRule("UpperCase").addRhs(term("X")));
        addRule(new ParseRule("UpperCase").addRhs(term("Y")));
        addRule(new ParseRule("UpperCase").addRhs(term("Z")));

        addRule(new ParseRule("Number").addRhs(term("0")));
        addRule(new ParseRule("Number").addRhs(term("1")));
        addRule(new ParseRule("Number").addRhs(term("2")));
        addRule(new ParseRule("Number").addRhs(term("3")));
        addRule(new ParseRule("Number").addRhs(term("4")));
        addRule(new ParseRule("Number").addRhs(term("5")));
        addRule(new ParseRule("Number").addRhs(term("6")));
        addRule(new ParseRule("Number").addRhs(term("7")));
        addRule(new ParseRule("Number").addRhs(term("8")));
        addRule(new ParseRule("Number").addRhs(term("9")));

        addRule(new ParseRule("UpperOrLowerCase").addRhs(nonTerm("LowerCase")));
        addRule(new ParseRule("UpperOrLowerCase").addRhs(nonTerm("UpperCase")));

        addRule(new ParseRule("SafeSpecial").addRhs(term(";")));
        addRule(new ParseRule("SafeSpecial").addRhs(term("}")));
        addRule(new ParseRule("SafeSpecial").addRhs(term("{")));
        addRule(new ParseRule("SafeSpecial").addRhs(term("+")));
        addRule(new ParseRule("SafeSpecial").addRhs(term("*")));
        addRule(new ParseRule("SafeSpecial").addRhs(term("/")));
        addRule(new ParseRule("SafeSpecial").addRhs(term("-")));

        addRule(new ParseRule("SafeChar").addRhs(nonTerm("UpperOrLowerCase")));
        addRule(new ParseRule("SafeChar").addRhs(nonTerm("Number")));
        addRule(new ParseRule("SafeChar").addRhs(nonTerm("SafeSpecial")));

        addRule(new ParseRule("NonTerminal").addRhs(nonTerm("UpperCase")).addRhs(star(nonTerm("UpperOrLowerCase"))));

        addRule(new ParseRule("StringLiteral").addRhs(term("'"))
                .addRhs(star(nonTerm("SafeChar"))).addRhs(term("'")));


        addRule(new ParseRule("StarToken").addRhs(nonTerm("StringLiteral")).addRhs(term("*")));
        addRule(new ParseRule("StarToken").addRhs(nonTerm("NonTerminal")).addRhs(term("*")));


        addRule(new ParseRule("Token").addRhs(nonTerm("StringLiteral")));
        addRule(new ParseRule("BracketToken").addRhs(term("(")).addRhs(star(ws(), nonTerm("Token"))).addRhs(term(")")));
        addRule(new ParseRule("StarToken").addRhs(nonTerm("BracketToken")).addRhs(term("*")));

        addRule(new ParseRule("Token").addRhs(nonTerm("StarToken")));
        addRule(new ParseRule("Token").addRhs(nonTerm("BracketToken")));
        addRule(new ParseRule("Token").addRhs(nonTerm("NonTerminal")));


        addRule(new ParseRule("Sentence").addRhs(
            nonTerm("NonTerminal"),
            ws(),
            term("="),
            star(
                ws(),
                nonTerm("Token")
            ),
            term(";")
        ));


        addRule(new ParseRule("HighLevel").addRhs(
                star(
                        ws(),
                        nonTerm("Sentence")
                ),
                ws())
        );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<NonTerminal> nonTerminalDomain = rules.keySet().stream().sorted(Comparator.comparing(NonTerminal::getName)).collect(Collectors.toList());
        for (NonTerminal i : nonTerminalDomain) {
            sb.append(i.getName()).append(" =\n");
            for (int p = 0; p < rules.get(i).size(); p++) {
                List<Node> rhs = rules.get(i).get(p).getRHS();
                sb.append("\t");
                for (int j = 0; j < rhs.size()-1; j++) {
                    sb.append(rhs.get(j)).append(" ");
                }
                sb.append(rhs.get(rhs.size()-1));
                if (p != rules.get(i).size() - 1) {
                    sb.append(" |");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    void removeLeftRecursion() {
        //TODO
        System.err.println("To do: remove left recursion here");
    }

//    private Set<Terminal> getAllTerminals() {
//        Set<Terminal> res = new HashSet<>();
//        for (List<ParseRule> rulesForNonTerminal : rules.values()) {
//            for (ParseRule rule : rulesForNonTerminal) {
//                for (Node token : rule.getRHS()) {
//                    if (token instanceof Terminal) {
//                        res.add((Terminal) token);
//                    }
//                }
//            }
//        }
//        return res;
//    }

//    private Set<NonTerminal> getAllNonTerminals() {
//        return rules.keySet();
//    }

    void calculateFirst() {
//        first = new HashMap<>();
//        Set<Terminal> terminals = getAllTerminals();
//        for (Terminal term : terminals) {
//            first.put(term, Collections.singleton(term.getValue().charAt(0)));
//        }
//        Set<NonTerminal> nonTerminals = getAllNonTerminals();
//        for (NonTerminal nonTerminal : nonTerminals) {
//            first.put(nonTerminal, new HashSet<>());
//        }
//        HashMap<Node, Set<Character>> old = null;
//        while (!first.equals(old)) {
//             old = deepCopy(first);
//            for (NonTerminal nonTerminal : nonTerminals) {
//                for (ParseRule rule : rules.get(nonTerminal)) {
//                    first.get(nonTerminal).addAll(getFirstOfSequence(rule.getRHS()));
//                }
//            }
//        }
        System.err.println("To do: calculate first");
        //TODO
    }

//    private HashMap<Node, Set<Character>> deepCopy(Map<Node, Set<Character>> source) {
//        HashMap<Node, Set<Character>> res = new HashMap<>();
//        for (Map.Entry<Node, Set<Character>> entry : source.entrySet()) {
//            res.put(entry.getKey(), new HashSet<>());
//            res.get(entry.getKey()).addAll(entry.getValue());
//        }
//        return res;
//    }

//    private Set<Character> getFirstOfSequence(List<Node> sequence) {
//        Set<Character> res = new HashSet<>();
//        for (Node node : sequence) {
//            if (node instanceof Terminal || node instanceof NonTerminal) {
//                res.addAll(first.get(node));
//                if (!first.get(node).contains(null)) {
//                    break;
//                }
//            } else if (node instanceof StarNode) {
//                res.add(null);
//                res.addAll(getFirstOfSequence(((StarNode) node).contents()));
//            }
//        }
//        return res;
//    }



    void calculateFirstPlus() {
        System.err.println("To do: calculate firstplus");
        //TODO
    }

    void calculateFollow() {
        System.err.println("To do: calculate follow");
        //TODO
    }
}
