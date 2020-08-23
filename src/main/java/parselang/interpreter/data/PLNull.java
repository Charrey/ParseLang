package parselang.interpreter.data;

public class PLNull extends PLData {

    private static PLNull instance = new PLNull();

    private PLNull(){}

    public static PLNull get() {
        return instance;
    }

    @Override
    public String toString() {
        return "->Null!<-";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
