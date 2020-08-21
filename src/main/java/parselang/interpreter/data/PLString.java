package parselang.interpreter.data;

import java.util.List;
import java.util.function.Consumer;

public class PLString extends PLData {

    String contents;

    public PLString(String contents) {
        this.contents = contents;
    }

    public PLString(List<PLString> together) {
        StringBuilder sb = new StringBuilder();
        together.forEach(plString -> sb.append(plString.getContents()));
        contents = sb.toString();
    }

    public PLString(PLData base) {
        super();
        contents = base.toString();
    }

    private String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return contents;
    }
}
