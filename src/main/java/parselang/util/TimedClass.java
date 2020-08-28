package parselang.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;

public abstract class TimedClass {

    private final Set<Long> measurements = new HashSet<>();
    private long startTime;

    protected void start() {
        startTime = System.nanoTime();
    }

    protected void stop() {
        measurements.add(System.nanoTime() - startTime);
    }

    public double getTotalTime() {
        return measurements.stream().reduce(0L, Long::sum).doubleValue() / 1000000000.0;
    }

    public double getMeanTime() {
        return measurements.stream().mapToLong(value -> value).average().orElse(-1) / 1000000000.0;
    }

}
