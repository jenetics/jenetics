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
import java.util.concurrent.ThreadLocalRandom;
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RandomEnginePerf {

	@State(Scope.Benchmark)
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public static abstract class Base {

		public Random random;

		@Benchmark
		public int nextInt() {
			return random.nextInt();
		}

		@Benchmark
		public long nextLong() {
			return random.nextLong();
		}

		@Benchmark
		public float nextFloat() {
			return random.nextFloat();
		}

		@Benchmark
		public double nextDouble() {
			return random.nextDouble();
		}

		/*
		@Benchmark
		public double nextGaussian() {
			return random.nextGaussian();
		}
		*/
	}

	public static class LCG64ShiftRandomPerf extends Base {
		{random = new LCG64ShiftRandom();}
	}

	public static class KISS32RandomPerf extends Base {
		{random = new KISS32Random();}
	}

	public static class KISS64RandomPerf extends Base {
		{random = new KISS64Random();}
	}

	public static class SimpleRandom64Perf extends Base {{
		random = new Random64() {
			long _x = 0;
			@Override
			public long nextLong() {
				return ++_x;
			}
		};
	}}

	public static class SimpleRandom32Perf extends Base {{
		random = new Random32() {
			int _x = 0;
			@Override
			public int nextInt() {
				return ++_x;
			}
		};
	}}

	public static class ThreadLocalRandomPerf extends Base {
		{random = ThreadLocalRandom.current();}
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + RandomEnginePerf.class.getSimpleName() + ".*")
			.warmupIterations(10)
			.measurementIterations(25)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}


}

/*
Benchmark                                           Mode  Cnt    Score   Error   Units
RandomEnginePerf.KISS32RandomPerf.nextDouble       thrpt   25  108.091 ± 2.641  ops/us
RandomEnginePerf.KISS32RandomPerf.nextFloat        thrpt   25  147.615 ± 1.138  ops/us
RandomEnginePerf.KISS32RandomPerf.nextInt          thrpt   25  177.378 ± 1.310  ops/us
RandomEnginePerf.KISS32RandomPerf.nextLong         thrpt   25  121.050 ± 1.200  ops/us
RandomEnginePerf.KISS64RandomPerf.nextDouble       thrpt   25  120.396 ± 0.992  ops/us
RandomEnginePerf.KISS64RandomPerf.nextFloat        thrpt   25  119.311 ± 1.279  ops/us
RandomEnginePerf.KISS64RandomPerf.nextInt          thrpt   25  122.467 ± 1.302  ops/us
RandomEnginePerf.KISS64RandomPerf.nextLong         thrpt   25  125.671 ± 1.190  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextDouble   thrpt   25  184.582 ± 2.896  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextFloat    thrpt   25  175.405 ± 2.061  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextInt      thrpt   25  240.889 ± 3.444  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextLong     thrpt   25  249.070 ± 4.884  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextDouble     thrpt   25  230.891 ± 1.524  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextFloat      thrpt   25  342.979 ± 5.605  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextInt        thrpt   25  349.939 ± 6.643  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextLong       thrpt   25  308.032 ± 5.982  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextDouble     thrpt   25  274.501 ± 8.201  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextFloat      thrpt   25  287.653 ± 3.588  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextInt        thrpt   25  339.068 ± 2.026  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextLong       thrpt   25  350.953 ± 1.788  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextDouble  thrpt   25  213.438 ± 3.422  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextFloat   thrpt   25  211.616 ± 2.311  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextInt     thrpt   25  251.721 ± 3.953  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextLong    thrpt   25  261.436 ± 2.990  ops/us
*/
