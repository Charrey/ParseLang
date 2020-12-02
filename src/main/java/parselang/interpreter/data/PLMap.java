package parselang.interpreter.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class representing maps in ParseLang
 */
public class PLMap extends PLData implements PLIndexable {

    private final Map<PLData, PLData> content = new HashMap<>();

    /**
     * @inheritDoc
     */
    @Override
    public PLData get(PLData index) {
        if (!content.containsKey(index)) {
            set(index, new PLMap());
        }
        return content.get(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(PLData key, PLData value) {
        content.put(key, value);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLMap plMap = (PLMap) o;
        return content.equals(plMap.content);
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
        return "map";
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        List<String> list = content.entrySet().stream().map(entry -> entry.getKey() + " : " + entry.getValue()).collect(Collectors.toList());
        sb.append(String.join(", ", list));
        sb.append("}");
        return sb.toString();
    }
}
