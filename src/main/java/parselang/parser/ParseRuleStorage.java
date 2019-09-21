package parselang.parser;

import parselang.languages.Language;
import parselang.parser.data.*;

import java.util.*;
import java.util.stream.Collectors;

public class ParseRuleStorage {

    private Map<NonTerminal, List<ParseRule>> rules = new HashMap<>();
    private Map<Node, Set<Character>> first = new HashMap<>();
    private Map<Node, Set<Character>> follow = new HashMap<>();
    private Map<NonTerminal, Map<Character, LinkedHashSet<ParseRule>>> rulesPlus = new HashMap<>();


    public void prepare(Language lang, NonTerminal toplevel) throws UndefinedNontermException {
        setDefaults(lang);
        calculateFirst();
        calculateFollow(toplevel);
        calculateFirstPlus();
        removeLeftRecursion();

    }

    private void addRule(ParseRule rule) {
        addRules(rule.convertStarNodes(), Direction.RIGHT);
    }

    private void addRules(Collection<ParseRule> rules, Direction dir) {
        for (ParseRule rule : rules) {
            NonTerminal nonTerminal = rule.getLHS();
            this.rules.computeIfAbsent(nonTerminal, nonTerminal1 -> new LinkedList<>());
            switch (dir) {
                case LEFT:
                    this.rules.get(nonTerminal).add(0, rule);
                    break;
                case RIGHT:
                    this.rules.get(nonTerminal).add(rule);
            }
        }
    }


    public LinkedHashSet<ParseRule> getByNonTerminal(Node nonTerminal, Character startsWith) {
        if (!(nonTerminal instanceof NonTerminal)) {
            return new LinkedHashSet<>();
        }
        if (((NonTerminal) nonTerminal).getName().contains("Number*")) {
            System.out.println();
        }
        if (rulesPlus.containsKey(nonTerminal)) {
            if (rulesPlus.get(nonTerminal).containsKey(startsWith)) {
                return rulesPlus.get(nonTerminal).get(startsWith);
            }
            return rulesPlus.get(nonTerminal).getOrDefault(null, new LinkedHashSet<>());
        } else {
            System.out.println("Warning! No such rule! => " + ((NonTerminal)nonTerminal).getName() + ", starts with: \"" + startsWith + "\"");
            return new LinkedHashSet<>();
        }
    }

    public static Node ws() {
        return new StarNode(nonTerm("WhiteSpace"));
    }

    public static NonTerminal nonTerm(String name) {
        return new NonTerminal(name);
    }

    public static Terminal term(String name) {
        return new Terminal(name);
    }

    public static Node star(Node... content) {
        return new StarNode(content);
    }

    void setDefaults(Language language) {
        for (ParseRule rule : language.getRules()) {
            addRule(rule);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<NonTerminal> nonTerminalDomain = rules.keySet().stream().sorted(Comparator.comparing(NonTerminal::getName)).collect(Collectors.toList());
        for (NonTerminal i : nonTerminalDomain) {
            sb.append(i.getName()).append(" =\n");
            for (int p = 0; p < rules.get(i).size(); p++) {
                List<Node> rhs = rules.get(i).get(p).getRHS();
                sb.append("\t");
                for (int j = 0; j < rhs.size()-1; j++) {
                    sb.append(rhs.get(j)).append(" ");
                }
                if (!rhs.isEmpty()) {
                    sb.append(rhs.get(rhs.size() - 1));
                }
                if (p != rules.get(i).size() - 1) {
                    sb.append(" |");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    void removeLeftRecursion() {
        //TODO
        System.err.println("To do: remove left recursion here");
    }

    private Set<Terminal> getAllTerminals() {
        Set<Terminal> res = new HashSet<>();
        for (List<ParseRule> rulesForNonTerminal : rules.values()) {
            for (ParseRule rule : rulesForNonTerminal) {
                for (Node token : rule.getRHS()) {
                    if (token instanceof Terminal) {
                        res.add((Terminal) token);
                    }
                }
            }
        }
        return res;
    }

    private Set<NonTerminal> getAllNonTerminals() {
        return rules.keySet();
    }

    void calculateFirst() throws UndefinedNontermException {
        for (Terminal term : getAllTerminals()) {
            first.put(term, Collections.singleton(term.getValue().charAt(0)));
        }
        for (NonTerminal nt : getAllNonTerminals()) {
            first.put(nt, new HashSet<>());
        }
        for (NonTerminal nt : getAllNonTerminals()) {
            for (ParseRule rule : rules.get(nt)) {
                if (rule.getRHS().size() > 0 && rule.getRHS().get(0) instanceof Terminal) {
                    first.get(nt).add((((Terminal) rule.getRHS().get(0)).getValue().charAt(0)));
                }
            }
        }
        HashMap<Node, Set<Character>> firstCopy = null;
        while (!first.equals(firstCopy)) {
            firstCopy = deepCopy(first);
            for (NonTerminal nt : getAllNonTerminals()) {
                for (ParseRule rule : rules.get(nt)) {
                    if (rule.getRHS().isEmpty()) {
                        first.get(nt).add(null);
                        continue;
                    }
                    for (Node rhsElem : rule.getRHS()) {

                        if (!first.containsKey(rhsElem)) {
                            throw new UndefinedNontermException(rhsElem);
                        }

                        first.get(nt).addAll(first.get(rhsElem));
                        if (!first.get(rhsElem).contains(null)) {
                            break;
                        }
                        first.get(nt).add(null);
                    }
                }
            }
        }
    }

    private HashMap<Node, Set<Character>> deepCopy(Map<Node, Set<Character>> source) {
        HashMap<Node, Set<Character>> res = new HashMap<>();
        for (Map.Entry<Node, Set<Character>> entry : source.entrySet()) {
            res.put(entry.getKey(), new HashSet<>());
            res.get(entry.getKey()).addAll(entry.getValue());
        }
        return res;
    }



    void calculateFirstPlus() {
        for (NonTerminal nonTerminal : getAllNonTerminals()) {
            rulesPlus.put(nonTerminal, new HashMap<>());
        }
        for (NonTerminal nonTerminal : getAllNonTerminals()) {
            for (ParseRule rule : rules.get(nonTerminal)) {
                Set<Character> firstOfRhs = firstOfList(rule.getRHS());
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

        for (NonTerminal nonTerminal : getAllNonTerminals()) {
            if (rulesPlus.get(nonTerminal).containsKey(null)) {
                for (Map.Entry<Character, LinkedHashSet<ParseRule>> rule : rulesPlus.get(nonTerminal).entrySet()) {
                    if (rule.getKey() != null) {
                        rule.getValue().addAll(rulesPlus.get(nonTerminal).get(null));
                    }
                }
            }
        }
        System.out.println();
    }

    private Set<Character> firstOfList(List<Node> list) {
        if (list.isEmpty()) {
            return Collections.singleton(null);
        } else if (!first.get(list.get(0)).contains(null)) {
            return first.get(list.get(0));
        } else {
            Set<Character> firstOfTail = new HashSet<>(firstOfList(list.subList(1, list.size())));
            firstOfTail.addAll(first.get(list.get(0)));
            return firstOfTail;
        }
    }

    void calculateFollow(NonTerminal startSymbol) {
        for (NonTerminal nt : getAllNonTerminals()) {
            follow.put(nt, new HashSet<>());
        }
        follow.get(startSymbol).add(null);
        HashMap<Node, Set<Character>> followCopy = null;
        while (!follow.equals(followCopy)) {
            followCopy = deepCopy(follow);
            for (NonTerminal nt : getAllNonTerminals()) {
                for (ParseRule rule : rules.get(nt)) {
                    if (rule.getRHS().isEmpty()) {
                        continue;
                    }
                    Node lastRhs = rule.getRHS().get(rule.getRHS().size()-1);
                    if (lastRhs instanceof NonTerminal) {
                        follow.get(lastRhs).addAll(follow.get(nt));
                    }
                    Set<Character> toAdd = new HashSet<>();
                    for (int i = rule.getRHS().size() - 2; i >= 0 ; i--) {
                        if (!(rule.getRHS().get(i) instanceof  NonTerminal)) {
                            toAdd = new HashSet<>();
                            continue;
                        }
                        toAdd.addAll(first.get(rule.getRHS().get(i + 1)));
                        follow.get(rule.getRHS().get(i)).addAll(toAdd);
                        if (!first.get(rule.getRHS().get(i)).contains(null)) {
                            toAdd = new HashSet<>();
                        }
                    }
                }
            }
        }
    }
}
