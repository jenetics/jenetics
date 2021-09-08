package io.jenetics.internal.math;

import java.util.concurrent.TimeUnit;

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
@Warmup(iterations = 2)
@BenchmarkMode(Mode.AverageTime)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CombinatoricsPerf {

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		@Param({
			"50/1",
			"50/2",
			"50/3",
			"50/5",
			"50/10",
			"50/15",
			"50/20",
			"50/25",
			"50/30",
			"50/35",
			"50/40",
			"50/45",
			"50/49",
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
	public int[] subset(final BenchmarkState state) {
		return Combinatorics.subset(state.n, state.k);
	}

}

/*
Benchmark                  (nk)   Mode  Cnt   Score   Error   Units
CombinatoricsPerf.subset   50/1  thrpt    5  14.153 ± 0.088  ops/us
CombinatoricsPerf.subset   50/2  thrpt    5   9.135 ± 0.257  ops/us
CombinatoricsPerf.subset   50/3  thrpt    5   6.380 ± 0.131  ops/us
CombinatoricsPerf.subset   50/5  thrpt    5   3.702 ± 0.027  ops/us
CombinatoricsPerf.subset  50/10  thrpt    5   1.774 ± 0.032  ops/us
CombinatoricsPerf.subset  50/15  thrpt    5   1.144 ± 0.011  ops/us
CombinatoricsPerf.subset  50/20  thrpt    5   0.815 ± 0.013  ops/us
CombinatoricsPerf.subset  50/25  thrpt    5   0.687 ± 0.007  ops/us
CombinatoricsPerf.subset  50/30  thrpt    5   0.567 ± 0.007  ops/us
CombinatoricsPerf.subset  50/35  thrpt    5   0.439 ± 0.004  ops/us
CombinatoricsPerf.subset  50/40  thrpt    5   0.369 ± 0.008  ops/us
CombinatoricsPerf.subset  50/45  thrpt    5   0.304 ± 0.013  ops/us
CombinatoricsPerf.subset  50/49  thrpt    5   0.235 ± 0.003  ops/us
 */
