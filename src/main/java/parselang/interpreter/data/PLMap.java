package parselang.interpreter.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PLMap {

    private Map<PLData, PLData> content = new HashMap<>();

    public void put(PLData key, PLData value) {
        content.put(key, value);
    }

    public PLData get(PLData key) {
        return content.get(key);
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
}
