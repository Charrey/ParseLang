package parselang.parser;

import parselang.parser.data.AST;

public class ParseResult {

    private final String original;
    private AST tree;

    /**
     * Saves a result of a parse attempt
     * @param original string being parsed
     * @param tree resulting tree from the parse attempt
     */
    public ParseResult(String original, AST tree) {
        this.original = original;
        this.tree = tree;
    }

    /**
     * Returns the part of the string that is not yet parsed
     * @return the not yet parsed part of the string
     */
    public String getRemaining() {
        if (tree.getParsedTo() == original.length()) {
            return "";
        } else {
            return original.substring(tree.getParsedTo());
        }
    }

    /**
     * Returns the AST that was parsed
     * @return the AST
     */
    public AST getTree() {
        return tree;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "<\"" +  (getRemaining().replaceAll("(\r\n)|(\n)", "\\\\n")).replaceAll("\"", "\\\\\"") + "\", \n" + tree + ">";
    }

    /**
     * Returns the index of the first character of the string that is not yet parsed.
     * @return the index
     */
    public int getRemainingIndex() {
        return tree.getParsedTo();
    }

    /**
     * Sets the tree of this parse result to something else
     * @param tree the new tree
     */
    public void setTree(AST tree) {
        this.tree = tree;
    }
}
