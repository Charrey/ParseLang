package parselang.interpreter.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

public class PLList extends PLData implements PLIndexable {

    private final ArrayList<PLData> content = new ArrayList<>();

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

    private PLData get(BigInteger bigInteger) {
        if (bigInteger.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("List index must be smaller than " + Integer.MAX_VALUE + "!");
        }
        return content.get(bigInteger.intValue());
    }

    @Override
    public String classString() {
        return "list";
    }


    public void set(PLInteger index, PLData value) {
        if (index.get().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("List index must be smaller than " + Integer.MAX_VALUE + "!");
        }
        if (index.get().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("List index cannot be negative!");
        }
        while (index.get().compareTo(BigInteger.valueOf(content.size())) >= 0) {
            content.add(PLNull.get());
        }
        content.set(index.get().intValue(), value);

    }

    @Override
    public PLData get(PLData key) {
        if (!(key instanceof PLInteger)) {
            throw new IllegalArgumentException("List index must be integer!");
        } else {
            return get(((PLInteger)key).get());
        }
    }

    @Override
    public void set(PLData key, PLData value) {
        if (key instanceof PLInteger) {
            set((PLInteger)key, value);
        } else {
            throw new IllegalArgumentException("List can only be indexed with integer!");
        }
    }
}
