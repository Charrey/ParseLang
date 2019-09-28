package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.parser.data.Terminal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FirstCalculator {


    public void updateFirst(Map<Node, Set<Character>> init, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) throws UndefinedNontermException;
}
