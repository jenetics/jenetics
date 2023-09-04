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
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.jenetics.util.ProxySorter.Comparator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 5.1
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
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
	public int[] java_prox_sort_10(final Array array) {
		return sort(
			array.array_10,
			0, array.array_10.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);
	}

	@Benchmark
	public int[] java_prox_sort_100(final Array array) {
		return sort(
			array.array_100,
			0, array.array_100.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);
	}

	@Benchmark
	public int[] java_prox_sort_1000(final Array array) {
		return sort(
			array.array_1000,
			0, array.array_1000.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);
	}

	@Benchmark
	public int[] java_prox_sort_10000(final Array array) {
		return sort(
			array.array_10000,
			0, array.array_10000.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);
	}

	@Benchmark
	public int[] java_prox_sort_100000(final Array array) {
		return sort(
			array.array_100000,
			0, array.array_100000.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);
	}

	static <T> int[] sort(
		final T array,
		final int from,
		final int to,
		final Comparator<? super T> comparator
	) {
		record Proxy<T>(int index, T array, Comparator<? super T> comparator)
			implements Comparable<Proxy<T>>
		{
			@Override
			public int compareTo(final Proxy<T> o) {
				return comparator.compare(array, index, o.index);
			}
		}

		final var proxies = IntStream.range(from, to)
			.mapToObj(i -> new Proxy<>(i, array, comparator))
			.sorted();

		return proxies
			.mapToInt(Proxy::index)
			.toArray();
	}

}

/* 7.1 / Java 17
Benchmark                              Mode  Cnt         Score        Error  Units
ProxySorterPerf.java_prox_sort_10      avgt   45       323.006 ±      5.525  ns/op
ProxySorterPerf.java_prox_sort_100     avgt   45      4820.464 ±     59.685  ns/op
ProxySorterPerf.java_prox_sort_1000    avgt   45    130113.814 ±   1816.579  ns/op
ProxySorterPerf.java_prox_sort_10000   avgt   45   1859521.307 ±  16841.191  ns/op
ProxySorterPerf.java_prox_sort_100000  avgt   45  26085419.026 ± 327704.002  ns/op
ProxySorterPerf.tim_sort_10            avgt   45        99.720 ±      5.198  ns/op
ProxySorterPerf.tim_sort_100           avgt   45      1718.837 ±     16.663  ns/op
ProxySorterPerf.tim_sort_1000          avgt   45     62573.221 ±    770.409  ns/op
ProxySorterPerf.tim_sort_10000         avgt   45   1002972.444 ±   8302.657  ns/op
ProxySorterPerf.tim_sort_100000        avgt   45  12654231.966 ±  56720.567  ns/op
 */
