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
 * Benchmark                            Mode  Cnt      Score      Error  Units
 * ProxySorterPerf.heap_sort_10         avgt    5    115.528 ±    0.492  ns/op
 * ProxySorterPerf.heap_sort_100        avgt    5   1897.558 ±   23.744  ns/op
 * ProxySorterPerf.heap_sort_1000       avgt    5  67898.955 ±  321.160  ns/op
 * ProxySorterPerf.old_index_sort_10    avgt    5     76.479 ±    1.021  ns/op
 * ProxySorterPerf.old_index_sort_100   avgt    5   2765.186 ±   55.576  ns/op
 * ProxySorterPerf.old_index_sort_1000  avgt    5  75128.993 ± 1665.751  ns/op
 * ProxySorterPerf.tim_sort_10          avgt    5     74.174 ±    0.854  ns/op
 * ProxySorterPerf.tim_sort_100         avgt    5   1573.028 ±    4.969  ns/op
 * ProxySorterPerf.tim_sort_1000        avgt    5  46181.516 ±  594.498  ns/op
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
		return TimProxySorter.INSTANCE.sort(array.array_10);
	}

	@Benchmark
	public int[] tim_sort_100(final Array array, final Blackhole black) {
		return TimProxySorter.INSTANCE.sort(array.array_100);
	}

	@Benchmark
	public int[] tim_sort_1000(final Array array, final Blackhole black) {
		return TimProxySorter.INSTANCE.sort(array.array_1000);
	}

	@Benchmark
	public int[] heap_sort_10(final Array array, final Blackhole black) {
		return HeapProxySorter.INSTANCE.sort(array.array_10);
	}

	@Benchmark
	public int[] heap_sort_100(final Array array, final Blackhole black) {
		return HeapProxySorter.INSTANCE.sort(array.array_100);
	}

	@Benchmark
	public int[] heap_sort_1000(final Array array, final Blackhole black) {
		return HeapProxySorter.INSTANCE.sort(array.array_1000);
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
