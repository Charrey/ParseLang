package parselang.parser.data;

public class AST extends ASTElem {

    private final Node root;
    private NodeList children;

    public AST(Node root) {
        this.root = root;
    }

    public boolean isLeaf() {
        return root instanceof Terminal;
    }

    public void addChild(ASTElem elem) {
        if (!isLeaf()) {
            children.add(elem);
        } else {
            System.err.println("Warning! Attempting to add child to leaf AST node");
        }
    }
}
