package ru.track.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
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
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Fork(1)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class JMH_Exception {


//    @Benchmark
//    public void testCreate(Blackhole bh) {
//        bh.consume(new Exception());
//    }

    @Param({"0", "1", "5", "10", "100"})
    int depth;

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
                .threads(4)
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(2)
                .build();

        new Runner(opt).run();
    }
}
