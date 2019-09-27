package parselang.parser;

import javafx.util.Pair;
import parselang.languages.Language;
import parselang.parser.data.*;

import java.util.*;
import java.util.stream.Collectors;

public class ParseRuleStorage {

    private Set<Pair<ParseRule, Direction>> addedRules;

    private final Map<NonTerminal, List<ParseRule>> rules = new HashMap<>();
    private final Map<Node, Set<Character>> first = new HashMap<>();
    private final Map<Node, Set<Character>> follow = new HashMap<>();
    private final Map<NonTerminal, Map<Character, LinkedHashSet<ParseRule>>> rulesPlus = new HashMap<>();
    private final Set<NonTerminal> allNonterminals = new HashSet<>();


    public void prepare(Language lang, NonTerminal toplevel) throws UndefinedNontermException {
        addedRules = new HashSet<>();
        setDefaults(lang);
        calculateFirst();
        calculateFollow(toplevel);
        calculateFirstPlus();
    }

    public void addCustomRules(Collection<Pair<ParseRule, Direction>> customRules, NonTerminal toplevel) {
        addedRules.addAll(customRules);
        for (Pair<ParseRule, Direction> rule : customRules) {
            addRule(rule.getKey(), rule.getValue());
        }
        try {
            calculateFirst();
        } catch (UndefinedNontermException e) {
            //This should not happen??
            e.printStackTrace();
        }
        calculateFollow(toplevel);
        calculateFirstPlus();
    }

    private void addRule(ParseRule rule, Direction dir) {
        addRules(rule.convertStarNodes(), dir);
    }

    private void addMissingNonterminals(Collection<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof NonTerminal) {
                allNonterminals.add((NonTerminal) node);
                this.rules.putIfAbsent((NonTerminal) node, new LinkedList<>());
            } else if (node instanceof StarNode) {
                addMissingNonterminals(((StarNode) node).contents());
            } else if (node instanceof BoundNonTerminal) {
                addMissingNonterminals(Collections.singleton(((BoundNonTerminal) node).getContent()));
            }
        }
    }

    private void addRules(Collection<ParseRule> rules, Direction dir) {
        for (ParseRule rule : rules) {
            NonTerminal nonTerminal = rule.getLHS();
            allNonterminals.add(nonTerminal);
            this.rules.computeIfAbsent(nonTerminal, nonTerminal1 -> new LinkedList<>());
            addMissingNonterminals(rule.getRHS());
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

    public static BoundNonTerminal bound(NonTerminal nonTerm, String name, boolean lazy) {
        return new BoundNonTerminal(nonTerm, name, lazy);
    }

    public static Node star(Node... content) {
        return new StarNode(content);
    }

    public static Node star(List<Node> content) {
        return new StarNode(content.toArray(new Node[0]));
    }

    private void setDefaults(Language language) {
        List<ParseRule> originalRules = language.getRules();
        for (ParseRule rule : originalRules) {
            addRule(rule, Direction.RIGHT);
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
        return allNonterminals;
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
        boolean changed = true;
        while (changed) {
           changed = false;
            for (NonTerminal nt : getAllNonTerminals()) {
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
    }

    private Set<Character> firstOfList(List<Node> list) {
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
        boolean changed = true;
        while (changed) {
            changed = false;
            for (NonTerminal nt : getAllNonTerminals()) {
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

    public Set<Pair<ParseRule, Direction>> getAddedRules() {
        return addedRules;
    }
}
