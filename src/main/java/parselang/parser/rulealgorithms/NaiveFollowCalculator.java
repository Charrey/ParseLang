package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.ParseRule;
import parselang.parser.data.Terminal;

import java.util.*;

public class NaiveFollowCalculator implements FollowCalculator {
    @Override
    public void updateFollow(Map<Node, Set<Character>> follow, Node startSymbol, Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) {
        for (NonTerminal nt : nonTerminals) {
            follow.put(nt, new HashSet<>());
        }
        follow.get(startSymbol).add(null);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (NonTerminal nt : nonTerminals) {
                for (ParseRule rule : rules.get(nt)) {
                    if (rule.getRHS().isEmpty()) {
                        continue;
                    }
                    Node lastRhs = rule.getRHS().get(rule.getRHS().size()-1);
                    if (lastRhs instanceof NonTerminal) {
                        if (follow.get(lastRhs).addAll(follow.get(nt))) {
                            changed = true;
                        }
                    }
                    Set<Character> toAdd = new HashSet<>();
                    for (int i = rule.getRHS().size() - 2; i >= 0 ; i--) {
                        if (!(rule.getRHS().get(i) instanceof  NonTerminal)) {
                            toAdd = new HashSet<>();
                            continue;
                        }
                        toAdd.addAll(first.get(rule.getRHS().get(i + 1)));
                        if (follow.get(rule.getRHS().get(i)).addAll(toAdd)) {
                            changed = true;
                        }
                        if (!first.get(rule.getRHS().get(i)).contains(null)) {
                            toAdd = new HashSet<>();
                        }
                    }
                }
            }
        }
    }
}
