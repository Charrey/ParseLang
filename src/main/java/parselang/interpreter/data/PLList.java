package parselang.interpreter.data;

import java.util.LinkedList;

public class PLList extends PLData {

    private LinkedList<PLData> content = new LinkedList<>();

    public void add(PLData item) {
        content.add(item);
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
