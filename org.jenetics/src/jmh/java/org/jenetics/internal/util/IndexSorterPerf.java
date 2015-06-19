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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 3.0
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class IndexSorterPerf {

	private double[] array20 = new Random().doubles(20).toArray();
	private double[] array40 = new Random().doubles(40).toArray();
	private double[] array80 = new Random().doubles(80).toArray();
	private double[] array160 = new Random().doubles(160).toArray();
	private double[] array250 = new Random().doubles(250).toArray();
	private double[] array320 = new Random().doubles(320).toArray();


	@Setup(Level.Iteration)
	public void shuffle() {
		array.shuffle(array20);
		array.shuffle(array40);
		array.shuffle(array80);
		array.shuffle(array160);
		array.shuffle(array250);
		array.shuffle(array320);
	}

	//@Benchmark
	public int insertionSort20() {
		return IndexSorter.INSERTION_SORTER.sort(array20, IndexSorter.indexes(20))[0];
	}

	//@Benchmark
	public int insertionSort40() {
		return IndexSorter.INSERTION_SORTER.sort(array40, IndexSorter.indexes(40))[0];
	}

	@Benchmark
	public int insertionSort80() {
		return IndexSorter.INSERTION_SORTER.sort(array80, IndexSorter.indexes(80))[0];
	}

	@Benchmark
	public int insertionSort160() {
		return IndexSorter.INSERTION_SORTER.sort(array160, IndexSorter.indexes(160))[0];
	}

	@Benchmark
	public int insertionSort250() {
		return IndexSorter.INSERTION_SORTER.sort(array250, IndexSorter.indexes(250))[0];
	}

	@Benchmark
	public int insertionSort320() {
		return IndexSorter.INSERTION_SORTER.sort(array320, IndexSorter.indexes(320))[0];
	}

	//@Benchmark
	public int heapSort20() {
		return IndexSorter.HEAP_SORTER.sort(array20, IndexSorter.indexes(20))[0];
	}

	//@Benchmark
	public int heapSort40() {
		return IndexSorter.HEAP_SORTER.sort(array40, IndexSorter.indexes(40))[0];
	}

	@Benchmark
	public int heapSort80() {
		return IndexSorter.HEAP_SORTER.sort(array80, IndexSorter.indexes(80))[0];
	}

	@Benchmark
	public int heapSort160() {
		return IndexSorter.HEAP_SORTER.sort(array160, IndexSorter.indexes(160))[0];
	}

	@Benchmark
	public int heapSort250() {
		return IndexSorter.HEAP_SORTER.sort(array250, IndexSorter.indexes(250))[0];
	}

	@Benchmark
	public int heapSort320() {
		return IndexSorter.HEAP_SORTER.sort(array320, IndexSorter.indexes(320))[0];
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + IndexSorterPerf.class.getSimpleName() + ".*")
			.warmupIterations(7)
			.measurementIterations(14)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
