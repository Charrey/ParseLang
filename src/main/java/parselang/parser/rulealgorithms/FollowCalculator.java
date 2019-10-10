package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.parser.data.Terminal;
import parselang.util.TimedClass;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FollowCalculator extends TimedClass {

    public abstract void updateFollow(Map<Node, Set<Character>> follow, Node startSymbol, Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals);

}
