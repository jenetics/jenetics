package io.jenetics.internal.math;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Fork(value = 1, warmups = 3)
@Warmup(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SubsetPerf {

	private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		@Param({
			"50/1",
			"50/2",
			"50/3",
			"50/4",
			"50/5",
			"50/6",
			"50/7",
			"50/8",
			"50/10",
			"50/12",
			"50/14",
			"50/16",
			"50/18",
			"50/20",
			"50/22",
			"50/24",
			"50/25",
			"50/26",
			"50/28",
			"50/30",
			"50/32",
			"50/34",
			"50/36",
			"50/38",
			"50/40",
			"50/42",
			"50/44",
			"50/46",
			"50/48",
			"50/49",
			"50/50",
		})
		public String nk;

		public int n;
		public int k;

		@Setup(Level.Trial)
		public void setUp() {
			final var split = nk.split("/");
			n = Integer.parseInt(split[0]);
			k = Integer.parseInt(split[1]);
		}
	}

	@Benchmark
	public int[] next(final BenchmarkState state) {
		return Subset.next(state.n, state.k, RANDOM);
	}

}

/*
java version "17" 2021-09-14 LTS
Java(TM) SE Runtime Environment (build 17+35-LTS-2724)
Java HotSpot(TM) 64-Bit Server VM (build 17+35-LTS-2724, mixed mode, sharing)

# Run complete. Total time: 02:45:50

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark         (nk)  Mode  Cnt     Score    Error  Units
SubsetPerf.next   50/1  avgt    5    50.502 ±  0.687  ns/op
SubsetPerf.next   50/2  avgt    5   106.731 ±  1.755  ns/op
SubsetPerf.next   50/3  avgt    5   155.513 ±  3.640  ns/op
SubsetPerf.next   50/4  avgt    5   212.522 ±  5.185  ns/op
SubsetPerf.next   50/5  avgt    5   269.629 ±  7.994  ns/op
SubsetPerf.next   50/6  avgt    5   327.883 ±  4.325  ns/op
SubsetPerf.next   50/7  avgt    5   378.605 ±  6.056  ns/op
SubsetPerf.next   50/8  avgt    5   465.662 ± 14.724  ns/op
SubsetPerf.next  50/10  avgt    5   555.227 ± 10.723  ns/op
SubsetPerf.next  50/12  avgt    5   663.116 ± 12.286  ns/op
SubsetPerf.next  50/14  avgt    5   816.092 ± 23.762  ns/op
SubsetPerf.next  50/16  avgt    5   916.325 ± 12.634  ns/op
SubsetPerf.next  50/18  avgt    5  1069.262 ± 22.072  ns/op
SubsetPerf.next  50/20  avgt    5  1184.548 ± 19.884  ns/op
SubsetPerf.next  50/22  avgt    5  1334.913 ± 19.082  ns/op
SubsetPerf.next  50/24  avgt    5  1405.646 ± 13.377  ns/op
SubsetPerf.next  50/25  avgt    5  1471.694 ± 22.492  ns/op
SubsetPerf.next  50/26  avgt    5  1759.169 ± 10.882  ns/op
SubsetPerf.next  50/28  avgt    5  1710.919 ± 67.558  ns/op
SubsetPerf.next  50/30  avgt    5  1538.644 ± 25.889  ns/op
SubsetPerf.next  50/32  avgt    5  1400.157 ± 17.602  ns/op
SubsetPerf.next  50/34  avgt    5  1282.592 ± 15.006  ns/op
SubsetPerf.next  50/36  avgt    5  1159.408 ± 15.712  ns/op
SubsetPerf.next  50/38  avgt    5   988.325 ± 23.613  ns/op
SubsetPerf.next  50/40  avgt    5   877.788 ± 16.343  ns/op
SubsetPerf.next  50/42  avgt    5   742.816 ± 17.206  ns/op
SubsetPerf.next  50/44  avgt    5   578.599 ± 13.985  ns/op
SubsetPerf.next  50/46  avgt    5   440.788 ± 17.018  ns/op
SubsetPerf.next  50/48  avgt    5   301.153 ±  6.875  ns/op
SubsetPerf.next  50/49  avgt    5   220.953 ±  4.837  ns/op
SubsetPerf.next  50/50  avgt    5    58.437 ±  0.658  ns/op
 */
