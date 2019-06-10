package parselang.parser.data;

public class Terminal extends Node {

    private final String value;

    public Terminal(String value) {
        this.value = value;
    }
}
