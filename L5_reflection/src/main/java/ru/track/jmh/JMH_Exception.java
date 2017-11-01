package ru.track.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**

 */
@BenchmarkMode({Mode.AverageTime})
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class JMH_Exception {

    @Param({"0", "1", "10", "100"})
    int depth;

    @Fork(1)
    @Benchmark
    public Object testThrow() {
        try {
            return recursive(depth);
        } catch (Exception e) {
            return e;
        }
    }

    Object recursive(int depth) throws Exception {
        if (depth == 0) {
            throw new Exception();
        }
        return recursive(depth - 1);

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_Exception.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
