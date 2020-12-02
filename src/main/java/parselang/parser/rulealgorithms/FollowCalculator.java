package parselang.parser.rulealgorithms;

import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that calculates the FOLLOW set of an LL(1) parser.
 */
public abstract class FollowCalculator {

    /**
     * Calculates the FOLLOW set of an LL(1) parser.
     *
     * @param topLevel toplevel nonterminal of the parsing process
     * @param first FIRST set of this LL(1) parser
     * @param rules All parse rules sorted by left hand side
     * @param nonTerminals set of all nonterminals
     * @return FOLLOW set of an LL(1) parser.
     */
    public abstract Map<Node, Set<Character>> computeFollow(Node topLevel, Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<NonTerminal> nonTerminals);

}
