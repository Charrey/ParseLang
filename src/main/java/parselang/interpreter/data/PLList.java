package parselang.interpreter.data;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

public class PLList extends PLData {

    private LinkedList<PLData> content = new LinkedList<>();

    public void add(PLData item) {
        content.add(item);
    }

    @Override
    public String toString() {
        return content.toString();
    }

    public void forEach(Consumer<PLData> consumer) {
        content.forEach(consumer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLList plList = (PLList) o;
        return content.equals(plList.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
