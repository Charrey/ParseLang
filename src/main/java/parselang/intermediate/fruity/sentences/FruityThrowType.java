package parselang.intermediate.fruity.sentences;

public class FruityThrowType {


    public static int counter = 0;
    private final int id;

    public static final FruityThrowType RETURN = getNext();

    public FruityThrowType(int id) {
        this.id = id;
    }
    private  FruityThrowType() {
        this.id = -1;
        throw new RuntimeException("DONT USE THIS CONSTRUCTOR");
    }
    public static FruityThrowType getNext() {
        return new FruityThrowType(counter++);
    }

    @Override
    public String toString() {
        return "(type " + id + ")";
    }
}
