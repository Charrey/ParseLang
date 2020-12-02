package parselang.interpreter.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class representing lists in ParseLang
 */
public class PLList extends PLData implements PLIndexable {

    private final ArrayList<PLData> content = new ArrayList<>();

    public void add(PLData item) {
        content.add(item);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return content.toString();
    }

    /**
     * Perform an operation for each list item in order.
     * @param consumer operation to perform
     * @throws NullPointerException if the specified operation is null
     */
    public void forEach(Consumer<PLData> consumer) {
        content.forEach(consumer);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLList plList = (PLList) o;
        return content.equals(plList.content);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String classString() {
        return "list";
    }

    /**
     * @inheritDoc
     */
    @Override
    public PLData get(PLData index) {
        if (!(index instanceof PLInteger)) {
            throw new IllegalArgumentException("List index must be integer!");
        } else {
            if (((PLInteger) index).get().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new IllegalArgumentException("List index must be smaller than " + Integer.MAX_VALUE + "!");
            }
            return content.get(((PLInteger) index).get().intValue());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(PLData index, PLData value) {
        if (index instanceof PLInteger) {
            PLInteger indexInteger = (PLInteger) index;
            if (indexInteger.get().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new IllegalArgumentException("List index must be smaller than " + Integer.MAX_VALUE + "!");
            }
            if (indexInteger.get().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new IllegalArgumentException("List index cannot be negative!");
            }
            while (indexInteger.get().compareTo(BigInteger.valueOf(content.size())) >= 0) {
                content.add(PLNull.get());
            }
            content.set(indexInteger.get().intValue(), value);
        } else {
            throw new IllegalArgumentException("List can only be indexed with integer!");
        }
    }
}
