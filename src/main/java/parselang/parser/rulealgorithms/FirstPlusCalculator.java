package parselang.parser.rulealgorithms;

import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;

import java.util.*;

/**
 * Class that calculates the FIRST+ set of an LL(1) parser.
 */
public abstract class FirstPlusCalculator {

    /**
     * Calculates the FIRST+ set of an LL(1) parser.
     *
     * @param rules All parse rules sorted by left hand side
     * @param first FIRST set of this LL(1) parser
     * @param follow FOLLOW set of this LL(1) parser
     * @param nonTerminals set of all nonterminals
     * @return FIRST+ set of an LL(1) parser.
     */
    public abstract Map<NonTerminal, Map<Character, TreeSet<ParseRule>>> computeFirstPlus(Map<NonTerminal, List<ParseRule>> rules, Map<Node, Set<Character>> first, Map<Node, Set<Character>> follow, Collection<NonTerminal> nonTerminals);

}
