package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.parser.data.Terminal;
import parselang.util.TimedClass;

import java.util.*;

public abstract class FirstCalculator extends TimedClass {


    public abstract void updateFirst(Map<Node, Set<Character>> init, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) throws UndefinedNontermException;
}
