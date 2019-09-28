package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.*;

import java.util.*;

public class NaiveFirstCalculator implements FirstCalculator {

    @Override
    public void updateFirst(Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) throws UndefinedNontermException {
        for (Terminal term : terminals) {
            first.put(term, Collections.singleton(term.getValue().charAt(0)));
        }
        for (NonTerminal nt : nonTerminals) {
            first.put(nt, new HashSet<>());
        }
        for (NonTerminal nt : nonTerminals) {
            for (ParseRule rule : rules.get(nt)) {
                if (rule.getRHS().size() > 0 && rule.getRHS().get(0) instanceof Terminal) {
                    first.get(nt).add((((Terminal) rule.getRHS().get(0)).getValue().charAt(0)));
                }
            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (NonTerminal nt : nonTerminals) {
                for (ParseRule rule : rules.get(nt)) {
                    if (rule.getRHS().isEmpty()) {
                        if (first.get(nt).add(null)) {
                            changed = true;
                        }
                        continue;
                    }
                    for (Node rhsElem : rule.getRHS()) {
                        if (rhsElem instanceof BoundNonTerminal) {
                            continue;
                        }

                        if (!first.containsKey(rhsElem)) {
                            throw new UndefinedNontermException(rhsElem);
                        }

                        if (first.get(nt).addAll(first.get(rhsElem))) {
                            changed = true;
                        }
                        if (!first.get(rhsElem).contains(null)) {
                            break;
                        }
                        if (first.get(nt).add(null)) {
                            changed = true;
                        }
                    }
                }
            }
        }
    }
}
