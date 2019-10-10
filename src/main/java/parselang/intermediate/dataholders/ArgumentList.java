package parselang.intermediate.dataholders;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ArgumentList extends Argument implements Iterable<Argument> {

    public static final ArgumentList EMPTY_LIST_RHS = new ArgumentList(Nature.RHS);
    public static final ArgumentList EMPTY_LIST_STAR = new ArgumentList(Nature.STAR);


    public final Nature nature;


    private List<Argument> arguments;

    private ArgumentList(Nature nature){
        this.nature = nature;
        this.arguments = Collections.emptyList();
    }

    public ArgumentList(List<Argument> args, Nature nature) {
        this.arguments = args;
        this.nature = nature;
    }

    @Override
    public Iterator<Argument> iterator() {
        return arguments.iterator();
    }

    public Stream<Argument> stream() {
        return arguments.stream();
    }

    public boolean isEmpty() {
        return arguments.isEmpty();
    }

    public enum Nature {
        RHS, STAR
    }
}
