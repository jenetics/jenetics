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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Java 8
 * Benchmark                               Mode  Cnt         Score        Error  Units
 * ProxySorterPerf.java_index_sort_10      avgt    7        42.444 ±      0.246  ns/op
 * ProxySorterPerf.java_index_sort_100     avgt    7       647.321 ±      7.265  ns/op
 * ProxySorterPerf.java_index_sort_1000    avgt    7     13272.546 ±    104.921  ns/op
 * ProxySorterPerf.java_index_sort_10000   avgt    7    499394.146 ±   3966.725  ns/op
 * ProxySorterPerf.java_index_sort_100000  avgt    7   6193328.099 ±  43234.161  ns/op
 * ProxySorterPerf.old_index_sort_10       avgt    7        70.987 ±      0.377  ns/op
 * ProxySorterPerf.old_index_sort_100      avgt    7      2739.078 ±     48.139  ns/op
 * ProxySorterPerf.old_index_sort_1000     avgt    7     74404.780 ±    351.720  ns/op
 * ProxySorterPerf.old_index_sort_10000    avgt    7   1038772.749 ±  15819.550  ns/op
 * ProxySorterPerf.old_index_sort_100000   avgt    7  16679849.508 ± 413979.681  ns/op
 * ProxySorterPerf.tim_sort_10             avgt    7       131.607 ±      0.817  ns/op
 * ProxySorterPerf.tim_sort_100            avgt    7      1846.389 ±     60.137  ns/op
 * ProxySorterPerf.tim_sort_1000           avgt    7     68894.592 ±    646.854  ns/op
 * ProxySorterPerf.tim_sort_10000          avgt    7    929895.036 ±   3997.038  ns/op
 * ProxySorterPerf.tim_sort_100000         avgt    7  12202155.149 ± 100955.707  ns/op
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
 * @version 5.1
 * @since 5.1
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ProxySorterPerf {

	@State(Scope.Benchmark)
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


	/* *************************************************************************
	 * ProxySorter
	 * ************************************************************************/

	@Benchmark
	public int[] tim_sort_10(final Array array) {
		return ProxySorter.sort(array.array_10);
	}

	@Benchmark
	public int[] tim_sort_100(final Array array) {
		return ProxySorter.sort(array.array_100);
	}

	@Benchmark
	public int[] tim_sort_1000(final Array array) {
		return ProxySorter.sort(array.array_1000);
	}

	@Benchmark
	public int[] tim_sort_10000(final Array array) {
		return ProxySorter.sort(array.array_10000);
	}

	@Benchmark
	public int[] tim_sort_100000(final Array array) {
		return ProxySorter.sort(array.array_100000);
	}

	/* *************************************************************************
	 * IndexSorter
	 * ************************************************************************/

	@Benchmark
	public int[] old_index_sort_10(final Array array) {
		return IndexSorter.sort(array.array_10);
	}

	@Benchmark
	public int[] old_index_sort_100(final Array array) {
		return IndexSorter.sort(array.array_100);
	}

	@Benchmark
	public int[] old_index_sort_1000(final Array array) {
		return IndexSorter.sort(array.array_1000);
	}

	@Benchmark
	public int[] old_index_sort_10000(final Array array) {
		return IndexSorter.sort(array.array_10000);
	}

	@Benchmark
	public int[] old_index_sort_100000(final Array array) {
		return IndexSorter.sort(array.array_100000);
	}

	/* *************************************************************************
	 * IndexSorter
	 * ************************************************************************/

	@Benchmark
	public int[] java_index_sort_10(final Array array) {
		int[] a = array.array_10.clone();
		Arrays.sort(a);
		return a;
	}

	@Benchmark
	public int[] java_index_sort_100(final Array array) {
		int[] a = array.array_100.clone();
		Arrays.sort(a);
		return a;
	}

	@Benchmark
	public int[] java_index_sort_1000(final Array array) {
		int[] a = array.array_1000.clone();
		Arrays.sort(a);
		return a;
	}

	@Benchmark
	public int[] java_index_sort_10000(final Array array) {
		int[] a = array.array_10000.clone();
		Arrays.sort(a);
		return a;
	}

	@Benchmark
	public int[] java_index_sort_100000(final Array array) {
		int[] a = array.array_100000.clone();
		Arrays.sort(a);
		return a;
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + ProxySorterPerf.class.getSimpleName() + ".*")
			.warmupIterations(4)
			.measurementIterations(7)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
