package parselang.interpreter.data;

/**
 * Class for any type that can be indexed (e.g. maps and lists)
 */
public interface PLIndexable {

    /**
     * Returns the data at a given index
     * @param index index
     * @return the data at that index
     */
    PLData get(PLData index);

    /**
     * Sets the data at a given index
     * @param index index
     * @param value new value
     */
    void set(PLData index, PLData value);
}
