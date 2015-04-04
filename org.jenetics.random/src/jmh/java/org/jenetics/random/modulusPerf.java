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
package org.jenetics.random;

import java.util.Random;
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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class modulusPerf {

	private final long[] numbers = new Random().longs().limit(1000).toArray();

	@Benchmark
	@OperationsPerInvocation(1000)
	public long systemModulus() {
		long result = 0;
		for (int i = 0; i < numbers.length; ++i) {
			result = numbers[i]%OxFFFFFFFB.VALUE;
		}

		return result;
	}

	@Benchmark
	@OperationsPerInvocation(1000)
	public long optimizedModulus() {
		long result = 0;
		for (int i = 0; i < numbers.length; ++i) {
			result = OxFFFFFFFB.mod(numbers[i]);
		}

		return result;
	}

	@Benchmark
	//@OperationsPerInvocation(1000)
	public long systemAdd() {
		long result = 0;
		for (int i = 0; i < numbers.length - 1; ++i) {
			result = (numbers[i]%OxFFFFFFFB.VALUE + numbers[i + 1]%OxFFFFFFFB.VALUE)%OxFFFFFFFB.VALUE;
		}

		return result;
	}

	@Benchmark
	//@OperationsPerInvocation(1000)
	public long optimizedAdd() {
		long result = 0;
		for (int i = 0; i < numbers.length - 1; ++i) {
			result = OxFFFFFFFB.add(numbers[i], numbers[i + 1]);
		}

		return result;
	}

	private static long modulus(final long x) {
		return x >= 0 ? modp(x) : -modp(x);
	}
	private static final long MASK = (1L << 32) - 1L;
	private static long modp(final long x) {
		long k = x;
		while (k > MASK) k = (k&MASK) + (k >>> 32);
		return k == MASK ? 0 : k;
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + modulusPerf.class.getSimpleName() + ".*")
			.warmupIterations(2)
			.measurementIterations(10)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}
}
