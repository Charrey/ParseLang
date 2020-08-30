package parselang.interpreter.data;

public interface PLIndexable {

    PLData get(PLData key);

    void set(PLData key, PLData value);
}
