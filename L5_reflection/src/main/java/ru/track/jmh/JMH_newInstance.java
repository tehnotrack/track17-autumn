package ru.track.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
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
public class JMH_newInstance {

    static class Box {

    }

    static class BoxV2 {
        int val;

        public BoxV2(int val) {
            this.val = val;
        }
    }

    @Benchmark
    public void testNew(Blackhole bh) {
        bh.consume(new Box());
    }

    @Benchmark
    public void testNewInstance(Blackhole bh) {
        try {
            bh.consume(Box.class.newInstance());
        } catch (Exception e) {

        }
    }

    @Benchmark
    public void testNewV2(Blackhole bh) {
        bh.consume(new BoxV2(1));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMH_newInstance.class.getSimpleName())
                .threads(4)
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(4)
                .build();

        new Runner(opt).run();
    }
}
