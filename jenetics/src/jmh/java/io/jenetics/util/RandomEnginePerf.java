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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

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

import io.jenetics.prngine.LCG64ShiftRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RandomEnginePerf {

	@State(Scope.Benchmark)
	public static class Rand {
		RandomGenerator java = new Random();
		RandomGenerator tlrandom = ThreadLocalRandom.current();
		RandomGenerator lcg64shift = new LCG64ShiftRandom();
	}


	@Benchmark
	public int javaNextInt(final Rand random) {
		return random.java.nextInt();
	}

	@Benchmark
	public long javaNextLong(final Rand random) {
		return random.java.nextLong();
	}

	@Benchmark
	public float javaNextFloat(final Rand random) {
		return random.java.nextFloat();
	}

	@Benchmark
	public double javaNextDouble(final Rand random) {
		return random.java.nextDouble();
	}

	@Benchmark
	public int tlrandomNextInt(final Rand random) {
		return random.tlrandom.nextInt();
	}

	@Benchmark
	public long tlrandomNextLong(final Rand random) {
		return random.tlrandom.nextLong();
	}

	@Benchmark
	public float tlrandomNextFloat(final Rand random) {
		return random.tlrandom.nextFloat();
	}

	@Benchmark
	public double tlrandomNextDouble(final Rand random) {
		return random.tlrandom.nextDouble();
	}

	@Benchmark
	public int lcg64shiftNextInt(final Rand random) {
		return random.lcg64shift.nextInt();
	}

	@Benchmark
	public long lcg64shiftNextLong(final Rand random) {
		return random.lcg64shift.nextLong();
	}

	@Benchmark
	public float lcg64shiftNextFloat(final Rand random) {
		return random.lcg64shift.nextFloat();
	}

	@Benchmark
	public double lcg64shiftNextDouble(final Rand random) {
		return random.lcg64shift.nextDouble();
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + RandomEnginePerf.class.getSimpleName() + ".*")
			.warmupIterations(5)
			.measurementIterations(20)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}

/*
# Run complete. Total time: 00:05:03

Benchmark                                           Mode  Cnt    Score   Error   Units
RandomEnginePerf.LCG64ShiftRandomPerf.nextDouble   thrpt   20  178.266 ± 2.396  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextFloat    thrpt   20  175.915 ± 2.555  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextInt      thrpt   20  237.989 ± 3.336  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextLong     thrpt   20  241.323 ± 2.839  ops/us
RandomEnginePerf.RandomPerf.nextDouble             thrpt   20   42.905 ± 0.409  ops/us
RandomEnginePerf.RandomPerf.nextFloat              thrpt   20   86.078 ± 0.496  ops/us
RandomEnginePerf.RandomPerf.nextInt                thrpt   20   87.608 ± 0.884  ops/us
RandomEnginePerf.RandomPerf.nextLong               thrpt   20   43.560 ± 0.287  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextDouble  thrpt   20  208.822 ± 2.666  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextFloat   thrpt   20  208.813 ± 3.790  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextInt     thrpt   20  255.523 ± 2.263  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextLong    thrpt   20  253.980 ± 3.081  ops/us
*/
