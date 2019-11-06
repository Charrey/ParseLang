package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.*;

import java.util.*;

public class NaiveFollowCalculator extends FollowCalculator {
    @Override
    public void updateFollow(Map<Node, Set<Character>> follow, Node startSymbol, Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) {
        start();
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

                    List<Node> rhscopy = new ArrayList<>(rule.getRHS());
                    Collections.reverse(rhscopy);
                    Deque<Node> toView = new ArrayDeque<>(rhscopy);

                    while (!toView.isEmpty()) {
                        Node last = toView.pop();
                        if (last instanceof BoundNonTerminal) {
                            toView.push(((BoundNonTerminal) last).getContent());
                            continue;
                        }
                        if (!(toView.peek() instanceof  NonTerminal)) {
                            toAdd = new HashSet<>();
                            continue;
                        }
                        toAdd.addAll(first.get(last));
                        if (follow.get(toView.peek()).addAll(toAdd)) {
                            changed = true;
                        }
                        if (!first.get(toView.peek()).contains(null)) {
                            toAdd = new HashSet<>();
                        }
                    }
                }
            }
        }
        stop();
    }
}
