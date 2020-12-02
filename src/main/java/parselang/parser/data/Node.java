package parselang.parser.data;

/**
 * Any terminal, nonterminal, kleene star wrapper or bound wrapper
 */
public abstract class Node {
    /**
     * Returns a deep copy of this node.
     * @return a deep copy
     */
    public abstract Node copy();


    //StarNode
    //Terminal
    //NonTerminal
    //BoundNonTerminal
}
