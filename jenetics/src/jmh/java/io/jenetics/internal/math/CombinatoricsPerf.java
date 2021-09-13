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
@Warmup(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CombinatoricsPerf {

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		@Param({
			"50/1",
			"50/2",
			"50/3",
			"50/4",
			"50/6",
			"50/8",
			"50/10",
			"50/12",
			"50/14",
			"50/16",
			"50/18",
			"50/20",
			"50/22",
			"50/24",
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
		return Subset.next(state.n, state.k);
	}

}

/*
Benchmark                  (nk)  Mode  Cnt     Score    Error  Units
CombinatoricsPerf.subset   50/1  avgt    5    71.078 ±  5.437  ns/op
CombinatoricsPerf.subset   50/2  avgt    5   112.112 ±  5.298  ns/op
CombinatoricsPerf.subset   50/3  avgt    5   159.361 ±  4.176  ns/op
CombinatoricsPerf.subset   50/4  avgt    5   213.608 ±  5.658  ns/op
CombinatoricsPerf.subset   50/6  avgt    5   327.797 ±  5.308  ns/op
CombinatoricsPerf.subset   50/8  avgt    5   450.547 ±  6.201  ns/op
CombinatoricsPerf.subset  50/10  avgt    5   565.340 ±  6.385  ns/op
CombinatoricsPerf.subset  50/12  avgt    5   668.205 ± 10.326  ns/op
CombinatoricsPerf.subset  50/14  avgt    5   835.440 ± 20.314  ns/op
CombinatoricsPerf.subset  50/16  avgt    5   915.044 ± 20.749  ns/op
CombinatoricsPerf.subset  50/18  avgt    5  1046.269 ±  8.506  ns/op
CombinatoricsPerf.subset  50/20  avgt    5  1183.285 ± 18.118  ns/op
CombinatoricsPerf.subset  50/22  avgt    5  1323.500 ± 31.883  ns/op
CombinatoricsPerf.subset  50/24  avgt    5  1395.401 ± 22.897  ns/op
CombinatoricsPerf.subset  50/26  avgt    5  1735.639 ± 24.399  ns/op
CombinatoricsPerf.subset  50/28  avgt    5  1659.776 ± 34.502  ns/op
CombinatoricsPerf.subset  50/30  avgt    5  1514.722 ± 19.607  ns/op
CombinatoricsPerf.subset  50/32  avgt    5  1369.287 ± 20.932  ns/op
CombinatoricsPerf.subset  50/34  avgt    5  1263.780 ± 16.305  ns/op
CombinatoricsPerf.subset  50/36  avgt    5  1142.473 ± 17.030  ns/op
CombinatoricsPerf.subset  50/38  avgt    5   975.531 ±  8.941  ns/op
CombinatoricsPerf.subset  50/40  avgt    5   852.359 ± 12.621  ns/op
CombinatoricsPerf.subset  50/42  avgt    5   726.852 ±  9.770  ns/op
CombinatoricsPerf.subset  50/44  avgt    5   595.421 ± 12.033  ns/op
CombinatoricsPerf.subset  50/46  avgt    5   458.843 ±  8.613  ns/op
CombinatoricsPerf.subset  50/48  avgt    5   315.020 ±  6.122  ns/op
CombinatoricsPerf.subset  50/49  avgt    5   231.994 ±  5.446  ns/op
 */

/*
Benchmark                  (nk)  Mode  Cnt     Score     Error  Units
CombinatoricsPerf.subset   50/1  avgt    5    77.463 ±   1.074  ns/op
CombinatoricsPerf.subset   50/2  avgt    5   117.074 ±   3.234  ns/op
CombinatoricsPerf.subset   50/3  avgt    5   172.015 ±   2.781  ns/op
CombinatoricsPerf.subset   50/4  avgt    5   238.575 ±  12.577  ns/op
CombinatoricsPerf.subset   50/6  avgt    5   368.074 ±  11.813  ns/op
CombinatoricsPerf.subset   50/8  avgt    5   486.440 ±   8.302  ns/op
CombinatoricsPerf.subset  50/10  avgt    5   616.736 ±  28.068  ns/op
CombinatoricsPerf.subset  50/12  avgt    5   735.692 ±  11.639  ns/op
CombinatoricsPerf.subset  50/14  avgt    5   883.986 ±  21.073  ns/op
CombinatoricsPerf.subset  50/16  avgt    5  1013.184 ±  29.040  ns/op
CombinatoricsPerf.subset  50/18  avgt    5  1272.428 ±  37.367  ns/op
CombinatoricsPerf.subset  50/20  avgt    5  1430.692 ±  37.085  ns/op
CombinatoricsPerf.subset  50/22  avgt    5  1596.892 ±  68.767  ns/op
CombinatoricsPerf.subset  50/24  avgt    5  1671.377 ±  43.393  ns/op
CombinatoricsPerf.subset  50/26  avgt    5  1876.788 ±  45.864  ns/op
CombinatoricsPerf.subset  50/28  avgt    5  1991.426 ±  44.862  ns/op
CombinatoricsPerf.subset  50/30  avgt    5  2079.981 ±  42.206  ns/op
CombinatoricsPerf.subset  50/32  avgt    5  2280.386 ±  28.103  ns/op
CombinatoricsPerf.subset  50/34  avgt    5  2501.318 ±  37.715  ns/op
CombinatoricsPerf.subset  50/36  avgt    5  2741.034 ±  69.267  ns/op
CombinatoricsPerf.subset  50/38  avgt    5  3023.644 ±  70.023  ns/op
CombinatoricsPerf.subset  50/40  avgt    5  3288.124 ± 203.196  ns/op
CombinatoricsPerf.subset  50/42  avgt    5  3455.089 ±  94.088  ns/op
CombinatoricsPerf.subset  50/44  avgt    5  3782.770 ± 120.549  ns/op
CombinatoricsPerf.subset  50/46  avgt    5  4027.589 ± 286.878  ns/op
CombinatoricsPerf.subset  50/48  avgt    5  4874.910 ± 475.295  ns/op
CombinatoricsPerf.subset  50/49  avgt    5  5174.455 ± 319.814  ns/op
 */

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
