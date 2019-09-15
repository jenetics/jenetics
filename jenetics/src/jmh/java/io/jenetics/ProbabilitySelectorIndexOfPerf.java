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
package io.jenetics;

import static io.jenetics.ProbabilitySelector.incremental;
import static io.jenetics.internal.math.base.normalize;

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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ProbabilitySelectorIndexOfPerf {

	@State(Scope.Benchmark)
	public static class Array {
		double[] array10 = random(new double[10]);
		double[] array15 = random(new double[15]);
		double[] array20 = random(new double[20]);
		double[] array40 = random(new double[40]);
		double[] array80 = random(new double[80]);
		double[] array160 = random(new double[160]);
		double[] array250 = random(new double[250]);
		double[] array320 = random(new double[320]);
		double[] array1000 = random(new double[1000]);
		double[] array10000 = random(new double[10000]);
	}

	private static double[] random(final double[] array) {
		final Random random = new Random();
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextGaussian() + 1.1;
		}
		return incremental(normalize(array));
	}

	// indexOf

	@Benchmark
	public int binaryIndexOf10(final Array array) {
		return ProbabilitySelector.indexOf(array.array10, 0.5);
	}

	@Benchmark
	public int binaryIndexOf15(final Array array) {
		return ProbabilitySelector.indexOf(array.array15, 0.5);
	}

	@Benchmark
	public int binaryIndexOf20(final Array array) {
		return ProbabilitySelector.indexOf(array.array20, 0.5);
	}

	@Benchmark
	public int binaryIndexOf40(final Array array) {
		return ProbabilitySelector.indexOf(array.array40, 0.5);
	}

	@Benchmark
	public int binaryIndexOf80(final Array array) {
		return ProbabilitySelector.indexOf(array.array80, 0.5);
	}

	@Benchmark
	public int binaryIndexOf160(final Array array) {
		return ProbabilitySelector.indexOf(array.array160, 0.5);
	}

	@Benchmark
	public int binaryIndexOf250(final Array array) {
		return ProbabilitySelector.indexOf(array.array250, 0.5);
	}

	// serialIndexOf

	@Benchmark
	public int serialIndexOf10(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array10, 0.5);
	}

	@Benchmark
	public int serialIndexOf15(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array15, 0.5);
	}

	@Benchmark
	public int serialIndexOf20(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array20, 0.5);
	}

	@Benchmark
	public int serialIndexOf40(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array40, 0.5);
	}

	@Benchmark
	public int serialIndexOf80(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array80, 0.5);
	}

	@Benchmark
	public int serialIndexOf160(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array160, 0.5);
	}

	@Benchmark
	public int serialIndexOf250(final Array array) {
		return ProbabilitySelector.indexOfSerial(array.array250, 0.5);
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + ProbabilitySelectorIndexOfPerf.class.getSimpleName() + ".*")
			.warmupIterations(9)
			.measurementIterations(14)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
