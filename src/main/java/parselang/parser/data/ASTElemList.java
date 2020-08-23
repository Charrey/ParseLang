package parselang.parser.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTElemList extends ASTElem implements Iterable<ASTElem> {

    private List<ASTElem> nodeList = new ArrayList<>();

    public ASTElemList() {
    }

    public ASTElemList(List<ASTElem> subList) {
        nodeList = subList;
    }

    public String pp(int indent) {
        String prefix = new String(new char[indent]).replace("\0", "\t");

        StringBuilder sb = new StringBuilder(prefix).append("[");
        for (ASTElem n : nodeList) {
            sb.append("\n").append(n.pp(indent + 1));
        }
        return sb.append("\n").append(prefix).append("]").toString();
    }

    @Override
    public String parseString() {
        StringBuilder sb = new StringBuilder();
        for (ASTElem child : nodeList) {
            sb.append(child.parseString());
        }
        return sb.toString();
    }

    @Override
    public Iterator<ASTElem> iterator() {
        return nodeList.iterator();
    }

    public void add(ASTElem child) {
        nodeList.add(child);
    }

    public int size() {
        return nodeList.size();
    }


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

    public void prepend(ASTElem listToAdd) {
        nodeList.add(0, listToAdd);
    }

    public ASTElem get(int i) {
        return nodeList.get(i);
    }

    public Stream<ASTElem> stream() {
        return nodeList.stream();
    }
}
