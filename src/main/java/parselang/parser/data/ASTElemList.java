package parselang.parser.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * List of elements that can represent sections of parsed strings
 */
public class ASTElemList extends ASTElem implements Iterable<ASTElem> {

    private final List<ASTElem> nodeList = new ArrayList<>();

    /**
     * Default constructor
     */
    public ASTElemList() {
    }

    /**
     * @inheritDoc
     */
    @Override
    public String pp(int indent) {
        String prefix = new String(new char[indent]).replace("\0", "\t");

        StringBuilder sb = new StringBuilder(prefix).append("[");
        for (ASTElem n : nodeList) {
            sb.append("\n").append(n.pp(indent + 1));
        }
        return sb.append("\n").append(prefix).append("]").toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ASTElemList astElems = (ASTElemList) o;
        return nodeList.equals(astElems.nodeList);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(nodeList);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String parseString() {
        StringBuilder sb = new StringBuilder();
        for (ASTElem child : nodeList) {
            sb.append(child.parseString());
        }
        return sb.toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public ASTElem copy() {
        ASTElemList res = new ASTElemList();
        nodeList.forEach(x -> res.nodeList.add(x.copy()));
        return res;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Iterator<ASTElem> iterator() {
        return nodeList.iterator();
    }

    /**
     * Adds a new AST element to this list
     * @param child AST element to add
     */
    public void add(ASTElem child) {
        nodeList.add(child);
    }

    /**
     * Returns the size of this AST element list
     * @return the size
     */
    public int size() {
        return nodeList.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        String a = nodeList.stream().map(astElem -> {
            if (astElem instanceof AST) {
                return ((AST) astElem).getRoot().toString();
            } else if (astElem instanceof ASTElemList) {
                return "(list with " + ((ASTElemList)astElem).size() + " elements)";
            }
            return "ERROR";
        }).collect(Collectors.joining(","));
        return "[" + a + "]";
    }

    /**
     * Adds an AST element to the front of this list
     * @param listToAdd AST element to prepend
     */
    public void prepend(ASTElem listToAdd) {
        nodeList.add(0, listToAdd);
    }

    /**
     * Retrieves an AST element from this list
     * @param i index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public ASTElem get(int i) {
        return nodeList.get(i);
    }

    /**
     * Converts this list of AST elements to a stream of AST elements
     * @return the stream
     */
    public Stream<ASTElem> stream() {
        return nodeList.stream();
    }
}
