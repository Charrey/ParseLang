package parselang.parser;


import parselang.languages.Language;
import parselang.parser.data.*;
import parselang.parser.rulealgorithms.*;

import java.util.*;
import java.util.stream.Collectors;

public class ParseRuleStorage {



    private final Map<NonTerminal, List<ParseRule>> rules = new HashMap<>();
    private Map<NonTerminal, Map<Character, TreeSet<ParseRule>>> firstPlus = new HashMap<>();
    private final Set<NonTerminal> allNonterminals = new HashSet<>();

    private final FirstCalculator firstCalc = new NaiveFirstCalculator();
    private final FollowCalculator followCalc = new NaiveFollowCalculator();
    private final FirstPlusCalculator firstPlusCalc = new NaiveFirstPlusCalculator();
    private NonTerminal toplevel;


    /**
     * Prepares the parse rule storage with the standard rule set of a language
     * @param lang language to use
     * @param toplevel top level nonterminal of this language
     */
    public void prepare(Language lang, NonTerminal toplevel) {
        this.toplevel = toplevel;
        setDefaults(lang);
        calculateFirstPlus(toplevel);
    }

    /**
     * Adds the result of a user declaration to this parserule storage
     * @param inheritedRule inherited rule to add
     * @param inheritedRuleDirection whether to add it to the front or back of the rules to be used by the packrat parser
     * @param addedRule rule that was added by the user
     */
    public void addCustomRules(ParseRule inheritedRule, Direction inheritedRuleDirection, ParseRule addedRule) {
        addRule(inheritedRule, inheritedRuleDirection);
        addRule(addedRule, Direction.RIGHT);
        calculateFirstPlus(toplevel);
    }

    private List<ParseRule> addRule(ParseRule rule, Direction dir) {
        List<ParseRule> rules = rule.convertStarNodes();
        addRules(rules, dir);
        return rules;
    }

    private void addMissingNonterminals(Collection<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof NonTerminal) {
                allNonterminals.add((NonTerminal) node);
                this.rules.putIfAbsent((NonTerminal) node, new LinkedList<>());
            } else if (node instanceof StarNode) {
                addMissingNonterminals(((StarNode) node).contents());
            } else if (node instanceof BoundNode) {
                addMissingNonterminals(Collections.singleton(((BoundNode) node).getContent()));
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

    /**
     * Returns an ordered collection of applicable rules to parse a nonterminal with a single character lookahead.
     * @param nonTerminal nonterminal to parse to
     * @param startsWith a single character lookahead
     * @return an ordered collection of rules to try
     */
    public Collection<ParseRule> getByNonTerminal(Node nonTerminal, Character startsWith) {
        if (!(nonTerminal instanceof NonTerminal)) {
            return Collections.emptyList();
        }
        if (firstPlus.containsKey(nonTerminal)) {
            if (firstPlus.get(nonTerminal).containsKey(startsWith)) {
                return firstPlus.get(nonTerminal).get(startsWith);
            }
            return firstPlus.get(nonTerminal).getOrDefault(null, new TreeSet<>(Comparator.comparingInt(value -> rules.get(nonTerminal).indexOf(value))));
        } else {
            System.out.println("Warning! No such rule! => " + ((NonTerminal)nonTerminal).getName() + ", starts with: \"" + startsWith + "\"");
            return Collections.emptyList();
        }
    }

    /**
     * Shorthand for WhiteSpace*
     * @return a node representing WhiteSpace*
     */
    public static Node ws() {
        return new StarNode(nonTerm("WhiteSpace"));
    }

    /**
     * Shorthand for new NonTerminal()
     * @param name name of a nonterminal
     * @return a nonterminal with that name
     */
    public static NonTerminal nonTerm(String name) {
        return new NonTerminal(name, false);
    }

    /**
     * Shorthand for new Terminal()
     * @param name value of a terminal
     * @return a terminal with that value
     */
    public static Terminal term(String name) {
        return new Terminal(name);
    }

    /**
     * Shorthand for new BoundNode()
     * @param node node to bind
     * @param name name of parameter
     * @param lazy whether the parameter is lazy
     * @return a bound node
     */
    public static BoundNode bound(Node node, String name, boolean lazy) {
        return new BoundNode(node, name, lazy);
    }

    /**
     * Shorthand for new StarNode()
     * @param content nodes affected by the kleene star
     * @return a star node
     */
    public static Node star(Node... content) {
        return new StarNode(content);
    }

    /**
     * Shorthand for new StarNode()
     * @param content nodes affected by the kleene star
     * @return a star node
     */
    public static Node star(List<Node> content) {
        return new StarNode(content.toArray(new Node[0]));
    }


    private void setDefaults(Language language) {
        List<ParseRule> originalRules = language.getRules();
        for (ParseRule rule : originalRules) {
            addRule(rule, Direction.RIGHT);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
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

    /**
     * Returns all nonterminals occurring in this parse rule storage
     * @return all nonterminals
     */
    public Set<NonTerminal> getAllNonTerminals() {
        return allNonterminals;
    }


    private void calculateFirstPlus(NonTerminal topLevel) {
        Map<Node, Set<Character>> first = firstCalc.computeFirst(rules, getAllTerminals(), getAllNonTerminals());
        Map<Node, Set<Character>> follow = followCalc.computeFollow(topLevel, first, rules, getAllNonTerminals());
        firstPlus = firstPlusCalc.computeFirstPlus(rules, first, follow, getAllNonTerminals());
    }


    final Set<ParseRule> parameterNameRules = new HashSet<>();

    /**
     * Add a rule for a parameter name such that it is parsed as such
     * @param parameterName name of the parameter
     */
    public void addParameter(String parameterName) {
        parameterNameRules.addAll(addRule(new ParseRule("ParameterName").addRhs(term(parameterName)), Direction.RIGHT));
        calculateFirstPlus(toplevel);
    }

    private final Set<NonTerminal> registered = new HashSet<>();

    /**
     * Registers a new nonterminal as being present somewhere in this storage
     * @param nonTerminal the nonterminal to register
     */
    public void registerNonTerminal(NonTerminal nonTerminal) {
        if (!registered.contains(nonTerminal)) {
            registered.add(nonTerminal);
            addRule(new ParseRule("RegisteredNonTerminal").addRhs(term(nonTerminal.getName())), Direction.LEFT);
            calculateFirstPlus(toplevel);
        }
    }

    /**
     * Clears all rules added for parameter names
     */
    public void removeParameters() {
        parameterNameRules.forEach(x -> rules.get(x.getLHS()).remove(x));
        parameterNameRules.clear();
        calculateFirstPlus(toplevel);
    }
}
