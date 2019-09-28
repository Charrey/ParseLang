package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.parser.data.Terminal;

import java.util.*;

public interface FirstPlusCalculator {

    public void updateFirstPlus(Map<NonTerminal, Map<Character, LinkedHashSet<ParseRule>>> init, Map<NonTerminal, List<ParseRule>> rules, Map<Node, Set<Character>> first, Map<Node, Set<Character>> follow, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals);

}
