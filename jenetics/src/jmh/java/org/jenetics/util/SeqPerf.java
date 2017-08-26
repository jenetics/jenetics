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
package org.jenetics.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.jenetics.internal.util.IntRef;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 3.0
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SeqPerf {

	static final int SIZE = 1000;

	private final Random random = new Random();
	private final int index = ThreadLocalRandom.current().nextInt(SIZE);

	// The native integer array for performance testing
	private final Integer[] array = new Integer[SIZE]; {
		for (int i = 0; i < SIZE; ++i) {
			array[i] = i;
		}
	}

	// The ArrayList for performance testing
	private final ArrayList<Integer> arrayList = new ArrayList<>(SIZE); {
		for (int i = 0; i < SIZE; ++i) {
			arrayList.add(i);
		}
	}

	// The MSeq for performance testing
	private final MSeq<Integer> mseq = MSeq.ofLength(SIZE); {
		for (int i = 0; i < SIZE; ++i) {
			mseq.set(i, i);
		}
	}

	/* *************************************************************************
	 * Native array performance tests.
	 **************************************************************************/

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int array_get() {
		int sum = 0;
		for (int i = 0; i < SIZE; ++i) {
			sum += array[i];
		}
		return sum;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int array_set() {
		final Integer value = random.nextInt();
		for (int i = 0; i < SIZE; ++i) {
			array[i] = value;
		}
		return value;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int array_forLoop() {
		int sum = 0;
		for (Integer i : array) {
			sum += i;
		}
		return sum;
	}


	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int array_copy() {
		return array.clone()[0].hashCode();
	}


	/* *************************************************************************
	 * ArrayList performance tests.
	 **************************************************************************/

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int arrayList_get() {
		int sum = 0;
		for (int i = 0; i < SIZE; ++i) {
			sum += arrayList.get(i);
		}
		return sum;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int arrayList_set() {
		final Integer value = random.nextInt();
		for (int i = 0; i < SIZE; ++i) {
			arrayList.set(i, value);
		}
		return value;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int arrayList_forLoop() {
		int sum = 0;
		for (Integer i : arrayList) {
			sum += i;
		}
		return sum;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int arrayList_forEachLoop() {
		final IntRef sum = new IntRef();
		arrayList.forEach(i -> sum.value += i);
		return sum.value;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int arrayList_contains() {
		int count = 0;
		for (int i = 0; i < SIZE; ++i) {
			count += arrayList.contains(i) ? 1 : 0;
		}
		return count;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	@SuppressWarnings("unchecked")
	public int arrayList_copy() {
		return ((List<Integer>)arrayList.clone()).get(0);
	}

	/* *************************************************************************
	 * MSeq performance tests.
	 **************************************************************************/

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int mseq_get() {
		int sum = 0;
		for (int i = 0; i < SIZE; ++i) {
			sum += mseq.get(i);
		}
		return sum;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int mseq_set() {
		final Integer value = random.nextInt();
		for (int i = 0; i < SIZE; ++i) {
			mseq.set(i, value);
		}
		return value;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int mseq_forLoop() {
		int sum = 0;
		for (Integer i : mseq) {
			sum += i;
		}
		return sum;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int mseq_forEachLoop() {
		final IntRef sum = new IntRef();
		mseq.forEach(i -> sum.value += i);
		return sum.value;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int mseq_contains() {
		int count = 0;
		for (int i = 0; i < SIZE; ++i) {
			count += mseq.contains(i) ? 1 : 0;
		}
		return count;
	}

	@OperationsPerInvocation(SIZE)
	@Benchmark
	public int mseq_copy() {
		return mseq.copy().get(0);
	}



	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + SeqPerf.class.getSimpleName() + ".*")
			.warmupIterations(3)
			.measurementIterations(5)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
