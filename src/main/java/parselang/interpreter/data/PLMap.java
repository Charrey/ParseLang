package parselang.interpreter.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PLMap extends PLData {

    private final Map<PLData, PLData> content = new HashMap<>();

    public void put(PLData key, PLData value) {
        content.put(key, value);
    }

    public PLData get(PLData key) {
        if (!content.containsKey(key)) {
            throw new IllegalArgumentException("Map " + this + " does not contain key " + key);
        } else {
            return content.get(key);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLMap plMap = (PLMap) o;
        return content.equals(plMap.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String classString() {
        return "map";
    }

    public boolean containsKey(PLData key) {
        return content.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        List<String> list = content.entrySet().stream().map(entry -> entry.getKey() + " : " + entry.getValue()).collect(Collectors.toList());
        sb.append(String.join(", ", list));
        sb.append("}");
        return sb.toString();
    }
}
