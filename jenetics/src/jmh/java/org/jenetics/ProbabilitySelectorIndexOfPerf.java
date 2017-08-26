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
package org.jenetics;

import static org.jenetics.ProbabilitySelector.incremental;
import static org.jenetics.internal.math.arithmetic.normalize;

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
public class ProbabilitySelectorIndexOfPerf {

	private final double[] array10 = random(new double[10]);
	private final double[] array15 = random(new double[15]);
	private final double[] array20 = random(new double[20]);
	private final double[] array40 = random(new double[40]);
	private final double[] array80 = random(new double[80]);
	private final double[] array160 = random(new double[160]);
	private final double[] array250 = random(new double[250]);
	private final double[] array320 = random(new double[320]);
	private final double[] array1000 = random(new double[1000]);
	private final double[] array10000 = random(new double[10000]);



	private static double[] random(final double[] array) {
		final Random random = new Random();
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextGaussian() + 1.1;
		}
		return incremental(normalize(array));
	}

	@Setup(Level.Iteration)
	public void shuffle() {
		random(array10);
		random(array15);
		random(array20);
		random(array40);
		random(array80);
		random(array160);
		random(array250);
		random(array320);
		random(array1000);
	}

	// indexOf

	@Benchmark
	public int binaryIndexOf10() {
		return ProbabilitySelector.indexOf(array10, 0.5);
	}

	@Benchmark
	public int binaryIndexOf15() {
		return ProbabilitySelector.indexOf(array15, 0.5);
	}

	@Benchmark
	public int binaryIndexOf20() {
		return ProbabilitySelector.indexOf(array20, 0.5);
	}

	@Benchmark
	public int binaryIndexOf40() {
		return ProbabilitySelector.indexOf(array40, 0.5);
	}

	@Benchmark
	public int binaryIndexOf80() {
		return ProbabilitySelector.indexOf(array80, 0.5);
	}

	@Benchmark
	public int binaryIndexOf160() {
		return ProbabilitySelector.indexOf(array160, 0.5);
	}

	@Benchmark
	public int binaryIndexOf250() {
		return ProbabilitySelector.indexOf(array250, 0.5);
	}

//	@Benchmark
//	public int binaryIndexOf320() {
//		return ProbabilitySelector.indexOf(array320, 0.5);
//	}
//
//	@Benchmark
//	public int binaryIndexOf1000() {
//		return ProbabilitySelector.indexOf(array1000, 0.5);
//	}
//
//	@Benchmark
//	public int binaryIndexOf10000() {
//		return ProbabilitySelector.indexOf(array10000, 0.5);
//	}

	// serialIndexOf

	@Benchmark
	public int serialIndexOf10() {
		return ProbabilitySelector.indexOfSerial(array10, 0.5);
	}

	@Benchmark
	public int serialIndexOf15() {
		return ProbabilitySelector.indexOfSerial(array15, 0.5);
	}

	@Benchmark
	public int serialIndexOf20() {
		return ProbabilitySelector.indexOfSerial(array20, 0.5);
	}

	@Benchmark
	public int serialIndexOf40() {
		return ProbabilitySelector.indexOfSerial(array40, 0.5);
	}

	@Benchmark
	public int serialIndexOf80() {
		return ProbabilitySelector.indexOfSerial(array80, 0.5);
	}

	@Benchmark
	public int serialIndexOf160() {
		return ProbabilitySelector.indexOfSerial(array160, 0.5);
	}

	@Benchmark
	public int serialIndexOf250() {
		return ProbabilitySelector.indexOfSerial(array250, 0.5);
	}

//	@Benchmark
//	public int serialIndexOf320() {
//		return ProbabilitySelector.indexOfSerial(array320, 0.5);
//	}
//
//	@Benchmark
//	public int serialIndexOf1000() {
//		return ProbabilitySelector.indexOfSerial(array1000, 0.5);
//	}
//
//	@Benchmark
//	public int serialIndexOf10000() {
//		return ProbabilitySelector.indexOfSerial(array10000, 0.5);
//	}

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
