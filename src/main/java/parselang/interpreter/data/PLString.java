package parselang.interpreter.data;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PLString extends PLData {

    final String contents;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLString plString = (PLString) o;
        return contents.equals(plString.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents);
    }

    @Override
    public String classString() {
        return "string";
    }
}
