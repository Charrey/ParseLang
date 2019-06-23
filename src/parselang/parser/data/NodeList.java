package parselang.parser.data;

import java.util.*;

public class NodeList extends ASTElem {

    private List<ASTElem> nodeList = new ArrayList<>();

    public int size() {
        return nodeList.size();
    }

    public boolean isEmpty() {
        return nodeList.isEmpty();
    }

    public boolean contains(ASTElem n) {
        return nodeList.contains(n);
    }

    public Iterator iterator() {
        return nodeList.iterator();
    }

    public ASTElem[] toArray() {
        return nodeList.toArray(new ASTElem[0]);
    }

    public boolean add(ASTElem n) {
        return nodeList.add(n);
    }

    public boolean remove(ASTElem n) {
        return nodeList.remove(n);
    }

    public boolean addAll(Collection<ASTElem> c) {
        return nodeList.addAll(c);
    }

    public boolean addAll(int index, Collection<ASTElem> c) {
        return nodeList.addAll(index, c);
    }

    public void clear() {
        nodeList.clear();
    }

    public boolean equals(NodeList other) {
        return nodeList.equals(other.nodeList);
    }

    public int hashCode() {
        return nodeList.hashCode();
    }

    public ASTElem get(int index) {
        return nodeList.get(index);
    }

    public ASTElem set(int index, ASTElem n) {
        return nodeList.set(index, n);
    }

    public void add(int index, ASTElem n) {
        nodeList.add(index, n);
    }

    public ASTElem remove(int index) {
        return nodeList.remove(index);
    }

    public int indexOf(ASTElem n) {
        return nodeList.indexOf(n);
    }

    public int lastIndexOf(ASTElem n) {
        return nodeList.lastIndexOf(n);
    }

    public ListIterator listIterator() {
        return nodeList.listIterator();
    }

    public ListIterator listIterator(int index) {
        return nodeList.listIterator(index);
    }

    public List subList(int fromIndex, int toIndex) {
        return nodeList.subList(fromIndex, toIndex);
    }

    public boolean retainAll(Collection<ASTElem> c) {
        return nodeList.retainAll(c);
    }

    public boolean removeAll(Collection<ASTElem> c) {
        return nodeList.removeAll(c);
    }

    public boolean containsAll(Collection<ASTElem> c) {
        return nodeList.containsAll(c);
    }

    public ASTElem[] toArray(ASTElem[] a) {
        return nodeList.toArray(a);
    }

    public void addAll(NodeList tempResultList) {
        nodeList.addAll(tempResultList.nodeList);
    }

    public List<ASTElem> asList() {
        return nodeList;
    }

    public String pp(int indent) {
        String prefix = new String(new char[indent]).replace("\0", "\t");

        StringBuilder sb = new StringBuilder(prefix).append("[");
        for (ASTElem n : nodeList) {
            sb.append("\n").append(n.pp(indent + 1));
        }
        return sb.append(prefix).append("\n]").toString();
    }

    @Override
    String parseString() {
        StringBuilder sb = new StringBuilder();
        for (ASTElem child : nodeList) {
            sb.append(child.parseString());
        }
        return sb.toString();
    }
}
