package parselang.util;

import parselang.parser.data.AST;
import parselang.parser.data.Direction;
import parselang.parser.data.Node;

import java.util.Collections;
import java.util.List;

public class DeclarationTree {

    public final String name;
    public final Direction direction;
    public final String superNonTerminal;
    public final List<Node> retrievedNodes;

    public DeclarationTree(String originalString, AST declaration) {
        this.name = ((AST)declaration.getChild(0)).subString(originalString);
        this.direction = ((AST)declaration.getChild(2)).subString(originalString).equals("<") ? Direction.LEFT : Direction.RIGHT;
        this.superNonTerminal = ((AST)declaration.getChild(4)).subString(originalString);
        NodeExtractor nodeExtractor = new NodeExtractor();
        this.retrievedNodes = Collections.unmodifiableList(nodeExtractor.extractNodes(originalString, declaration.getChild(7)));
    }
}
