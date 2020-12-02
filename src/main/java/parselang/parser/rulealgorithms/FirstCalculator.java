package parselang.parser.rulealgorithms;

import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.parser.data.Terminal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that calculates the FIRST set of an LL(1) parser.
 */
public abstract class FirstCalculator {

    /**
     * Calculates the FIRST set of an LL(1) parser.
     * @param rules All parse rules sorted by left hand side
     * @param terminals List of all terminals
     * @param nonTerminals List of all nonterminals
     */
    public abstract Map<Node, Set<Character>> computeFirst(Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals);
}
