package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.*;

import java.util.*;
import java.util.stream.Collectors;

public class NaiveFirstPlusCalculator implements FirstPlusCalculator {
    @Override
    public void updateFirstPlus(Map<NonTerminal, Map<Character, LinkedHashSet<ParseRule>>> rulesPlus, Map<NonTerminal, List<ParseRule>> rules, Map<Node, Set<Character>> first, Map<Node, Set<Character>> follow, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) {
        for (NonTerminal nonTerminal : nonTerminals) {
            rulesPlus.put(nonTerminal, new HashMap<>());
        }
        for (NonTerminal nonTerminal : nonTerminals) {
            for (ParseRule rule : rules.get(nonTerminal)) {
                Set<Character> firstOfRhs = firstOfList(rule.getRHS(), first);
                for (Character character : firstOfRhs) {
                    rulesPlus.get(nonTerminal).computeIfAbsent(character, character1 -> new LinkedHashSet<>());
                    rulesPlus.get(nonTerminal).get(character).add(rule);
                    rulesPlus.get(nonTerminal).get(character).addAll(rulesPlus.get(nonTerminal).getOrDefault(null, new LinkedHashSet<>()));
                }
                if (firstOfRhs.contains(null)) {
                    for (Character character : follow.get(nonTerminal)) {
                        rulesPlus.get(nonTerminal).computeIfAbsent(character, character1 -> new LinkedHashSet<>());
                        rulesPlus.get(nonTerminal).get(character).add(rule);
                    }
                }
            }
        }
        for (NonTerminal nonTerminal : nonTerminals) {
            if (rulesPlus.get(nonTerminal).containsKey(null)) {
                for (Map.Entry<Character, LinkedHashSet<ParseRule>> rule : rulesPlus.get(nonTerminal).entrySet()) {
                    if (rule.getKey() != null) {
                        rule.getValue().addAll(rulesPlus.get(nonTerminal).get(null));
                    }
                }
            }
        }
    }

    private Set<Character> firstOfList(List<Node> list, Map<Node, Set<Character>> first) {
        list = list.stream().map(node -> {
            if (node instanceof BoundNonTerminal) {
                node = ((BoundNonTerminal) node).getContent();
            }
            return node;
        }).collect(Collectors.toList());
        if (list.isEmpty()) {
            return Collections.singleton(null);
        } else if (!first.get(list.get(0)).contains(null)) {
            return first.get(list.get(0));
        } else {
            Set<Character> firstOfTail = new HashSet<>(firstOfList(list.subList(1, list.size()), first));
            firstOfTail.addAll(first.get(list.get(0)));
            return firstOfTail;
        }
    }
}
