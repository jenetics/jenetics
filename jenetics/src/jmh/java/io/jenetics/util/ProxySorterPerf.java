/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Java 8
 * Benchmark                             Mode  Cnt      Score       Error  Units
 * ProxySorterPerf.heap_sort_10          avgt    5    118.336 ±     7.764  ns/op
 * ProxySorterPerf.heap_sort_100         avgt    5   2002.145 ±   158.991  ns/op
 * ProxySorterPerf.heap_sort_1000        avgt    5  72283.465 ± 10795.387  ns/op
 * ProxySorterPerf.java_index_sort_10    avgt    5     10.324 ±     0.407  ns/op
 * ProxySorterPerf.java_index_sort_100   avgt    5    103.452 ±     3.592  ns/op
 * ProxySorterPerf.java_index_sort_1000  avgt    5    343.679 ±    12.527  ns/op
 * ProxySorterPerf.old_index_sort_10     avgt    5     71.901 ±     3.603  ns/op
 * ProxySorterPerf.old_index_sort_100    avgt    5   3005.035 ±   101.993  ns/op
 * ProxySorterPerf.old_index_sort_1000   avgt    5  75792.042 ±  2633.823  ns/op
 * ProxySorterPerf.tim_sort_10           avgt    5     79.485 ±     2.863  ns/op
 * ProxySorterPerf.tim_sort_100          avgt    5   1591.106 ±   162.232  ns/op
 * ProxySorterPerf.tim_sort_1000         avgt    5  52207.042 ±  1775.293  ns/op
 *
 * Java 11
 * Benchmark                             Mode  Cnt      Score      Error  Units
 * ProxySorterPerf.heap_sort_10          avgt    5    138.540 ±   17.232  ns/op
 * ProxySorterPerf.heap_sort_100         avgt    5   2433.890 ±  250.078  ns/op
 * ProxySorterPerf.heap_sort_1000        avgt    5  68613.191 ± 5128.302  ns/op
 * ProxySorterPerf.java_index_sort_10    avgt    5     13.087 ±    0.993  ns/op
 * ProxySorterPerf.java_index_sort_100   avgt    5    100.869 ±    2.555  ns/op
 * ProxySorterPerf.java_index_sort_1000  avgt    5    272.506 ±   20.439  ns/op
 * ProxySorterPerf.old_index_sort_10     avgt    5     63.236 ±    8.195  ns/op
 * ProxySorterPerf.old_index_sort_100    avgt    5   2235.945 ±  135.744  ns/op
 * ProxySorterPerf.old_index_sort_1000   avgt    5  73539.263 ± 8230.531  ns/op
 * ProxySorterPerf.tim_sort_10           avgt    5     47.301 ±    1.298  ns/op
 * ProxySorterPerf.tim_sort_100          avgt    5   1542.740 ±   93.445  ns/op
 * ProxySorterPerf.tim_sort_1000         avgt    5  48419.026 ± 1429.150  ns/op
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ProxySorterPerf {

	@State(Scope.Thread)
	public static class Array {
		int[] array_10 = new Random().ints()
			.limit(10)
			.toArray();

		int[] array_100 = new Random().ints()
			.limit(100)
			.toArray();

		int[] array_1000 = new Random().ints()
			.limit(1000)
			.toArray();

		int[] array_10000 = new Random().ints()
			.limit(10000)
			.toArray();

		int[] array_100000 = new Random().ints()
			.limit(100000)
			.toArray();
	}


	@Benchmark
	public int[] tim_sort_10(final Array array, final Blackhole black) {
		return ProxySorter.sort(array.array_10);
	}

	@Benchmark
	public int[] tim_sort_100(final Array array, final Blackhole black) {
		return ProxySorter.sort(array.array_100);
	}

	@Benchmark
	public int[] tim_sort_1000(final Array array, final Blackhole black) {
		return ProxySorter.sort(array.array_1000);
	}

	@Benchmark
	public int[] old_index_sort_10(final Array array, final Blackhole black) {
		return IndexSorter.sort(array.array_10);
	}

	@Benchmark
	public int[] old_index_sort_100(final Array array, final Blackhole black) {
		return IndexSorter.sort(array.array_100);
	}

	@Benchmark
	public int[] old_index_sort_1000(final Array array, final Blackhole black) {
		return IndexSorter.sort(array.array_1000);
	}

	@Benchmark
	public int[] java_index_sort_10(final Array array, final Blackhole black) {
		Arrays.sort(array.array_10);
		return array.array_10;
	}

	@Benchmark
	public int[] java_index_sort_100(final Array array, final Blackhole black) {
		Arrays.sort(array.array_100);
		return array.array_100;
	}

	@Benchmark
	public int[] java_index_sort_1000(final Array array, final Blackhole black) {
		Arrays.sort(array.array_1000);
		return array.array_1000;
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + ProxySorterPerf.class.getSimpleName() + ".*")
			.warmupIterations(3)
			.measurementIterations(5)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
