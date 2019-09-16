package parselang.parser;

import parselang.parser.data.Node;

public class UndefinedNontermException extends Exception {

    public UndefinedNontermException(Node rhsElem) {
        super("Usage of undefined nonterminal " + rhsElem + ".");
    }
}
