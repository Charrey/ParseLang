package parselang.parser.rulealgorithms;

import parselang.parser.UndefinedNontermException;
import parselang.parser.data.*;

import java.util.*;
import java.util.function.Function;

public class NaiveFirstCalculator extends FirstCalculator {

    @Override
    public void updateFirst(Map<Node, Set<Character>> first, Map<NonTerminal, List<ParseRule>> rules, Collection<Terminal> terminals, Collection<NonTerminal> nonTerminals) throws UndefinedNontermException {
        start();
        for (Terminal term : terminals) {
            first.computeIfAbsent(term, node -> Collections.singleton(term.getValue().charAt(0)));
        }
        for (NonTerminal nt : nonTerminals) {
            first.computeIfAbsent(nt, node -> new HashSet<>());
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
                            continue; //TODO: fix
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
        stop();
    }
}
