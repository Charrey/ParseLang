package parselang.intermediate.dataholders;

public class ConstantString extends Argument {

    private final String content;

    public ConstantString(String subString) {
        super();
        this.content = subString;
    }

    public String getContent() {
        return content;
    }
}
