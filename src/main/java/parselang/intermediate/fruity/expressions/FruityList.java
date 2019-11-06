package parselang.intermediate.fruity.expressions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FruityList extends FruityConstant implements Iterable<Integer> {

    private final List<Integer> content;

    public FruityList(List<Integer> content) {
        this.content = content;
    }

    public String toString() {
        Stream<String> strings = content.stream().map(Object::toString).map(s -> "var" + s);
        return "[" + strings.collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public Iterator<Integer> iterator() {
        return content.iterator();
    }

    public List<Integer> getContent() {
        return content;
    }

    @Override
    public Set<Integer> getReads() {
        return new HashSet<>(content);
    }
}
