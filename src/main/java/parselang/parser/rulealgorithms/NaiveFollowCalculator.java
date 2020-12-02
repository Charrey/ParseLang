package parselang.parser.rulealgorithms;

import parselang.parser.data.*;

import java.util.*;

/**
 * Default FOLLOW calculator
 */
public class NaiveFollowCalculator extends FollowCalculator {


    /**
     * @inheritDoc
     */
    @Override
    public Map<Node, Set<Character>> computeFollow(Node topLevel, Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<NonTerminal> nonTerminals) {
        Map<Node, Set<Character>> follow = new HashMap<>();
        for (NonTerminal nt : nonTerminals) {
            follow.put(nt, new HashSet<>());
        }
        follow.get(topLevel).add(null);
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
                        if (last instanceof BoundNode) {
                            toView.push(((BoundNode) last).getContent());
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
        return follow;
    }
}
