package parselang.parser.parsers;

import javafx.util.Pair;
import parselang.parser.ParseResult;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.AST;
import parselang.parser.data.Node;
import parselang.parser.data.NonTerminal;
import parselang.parser.data.Terminal;
import parselang.parser.exceptions.ParseErrorException;
import parselang.util.ParserState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class StackParser extends Parser {

    private final Deque<Pair<ParserState, Integer>> stack;
    private final Map<Pair<ParserState, Integer>, ParseResult> cache;

    private ParserState currentState;
    private Integer ruleApplied;
    private ParseRuleStorage storage;
    private String originalString;

    public StackParser() {
        this.cache = new HashMap<>();
        this.stack = new ArrayDeque<>();
    }

    @Override
    ParseResult parse(String originalString, Node toParseTo, ParseRuleStorage storage) throws ParseErrorException {
        this.originalString = originalString;
        this.storage = storage;
        currentState = getParserState(0, toParseTo);
        stack.push(new Pair<>(currentState, -1));

        boolean fail = false;
        while (!stack.isEmpty()) {
            Pair<ParserState, Integer> treeItem = stack.pop();
            currentState = treeItem.getKey();
            ruleApplied = treeItem.getValue();
            int nextRuleToTry = ++ruleApplied;
            if (nextRuleToTry >= currentState.rules().size()) {
                fail = true;
            } else {
                System.out.println();
            }



        }

        return new ParseResult("", 0, new AST(new Terminal(":(")));
    }

    private ParserState getParserState(int index, Node toParseTo) {
        return new ParserState(toParseTo, index, new AST(toParseTo), storage.getByNonTerminal(toParseTo, charAtPos(index)));
    }

    private Character charAtPos(int index) {
        return index == originalString.length() ? null : originalString.charAt(index);
    }
}
