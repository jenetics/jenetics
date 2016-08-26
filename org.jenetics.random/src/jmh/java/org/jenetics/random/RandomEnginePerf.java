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
		public int nextIntRange() {
			return random.nextInt(1000);
		}

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

	public static class RandomPerf extends Base {
		{random = new Random();}
	}

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

/*
# Run complete. Total time: 00:17:48

Benchmark                                             Mode  Cnt    Score    Error   Units
RandomEnginePerf.KISS32RandomPerf.nextDouble         thrpt   25   62.649 ±  0.609  ops/us
RandomEnginePerf.KISS32RandomPerf.nextFloat          thrpt   25   83.771 ±  1.236  ops/us
RandomEnginePerf.KISS32RandomPerf.nextInt            thrpt   25   98.252 ±  1.363  ops/us
RandomEnginePerf.KISS32RandomPerf.nextIntRange       thrpt   25   73.940 ±  1.823  ops/us
RandomEnginePerf.KISS32RandomPerf.nextLong           thrpt   25   63.252 ±  1.058  ops/us
RandomEnginePerf.KISS64RandomPerf.nextDouble         thrpt   25   69.694 ±  1.054  ops/us
RandomEnginePerf.KISS64RandomPerf.nextFloat          thrpt   25   69.985 ±  0.950  ops/us
RandomEnginePerf.KISS64RandomPerf.nextInt            thrpt   25   71.160 ±  0.934  ops/us
RandomEnginePerf.KISS64RandomPerf.nextIntRange       thrpt   25   64.741 ±  0.112  ops/us
RandomEnginePerf.KISS64RandomPerf.nextLong           thrpt   25   71.287 ±  1.087  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextDouble     thrpt   25  103.442 ±  2.034  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextFloat      thrpt   25   99.569 ±  2.255  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextInt        thrpt   25  123.018 ±  1.048  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextIntRange   thrpt   25   86.797 ±  1.104  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextLong       thrpt   25  128.519 ±  1.640  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextDouble       thrpt   25  138.632 ±  4.595  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextFloat        thrpt   25  195.515 ±  9.852  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextInt          thrpt   25  223.810 ±  4.986  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextIntRange     thrpt   25  141.575 ±  5.608  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextLong         thrpt   25  178.120 ±  4.621  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextDouble       thrpt   25  169.504 ±  1.592  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextFloat        thrpt   25  169.685 ±  2.354  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextInt          thrpt   25  207.302 ±  2.249  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextIntRange     thrpt   25  131.044 ±  1.668  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextLong         thrpt   25  224.148 ±  3.217  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextDouble    thrpt   25  122.182 ±  4.778  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextFloat     thrpt   25  128.571 ±  2.232  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextInt       thrpt   25  134.841 ± 13.211  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextIntRange  thrpt   25  103.187 ±  4.550  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextLong      thrpt   25  159.145 ±  2.036  ops/us
*/

/*
# Run complete. Total time: 00:20:43

Benchmark                                             Mode  Cnt    Score    Error   Units
RandomEnginePerf.KISS32RandomPerf.nextDouble         thrpt   25   61.791 ±  1.090  ops/us
RandomEnginePerf.KISS32RandomPerf.nextFloat          thrpt   25   84.113 ±  0.768  ops/us
RandomEnginePerf.KISS32RandomPerf.nextInt            thrpt   25   98.324 ±  1.254  ops/us
RandomEnginePerf.KISS32RandomPerf.nextIntRange       thrpt   25   74.940 ±  1.740  ops/us
RandomEnginePerf.KISS32RandomPerf.nextLong           thrpt   25   64.801 ±  1.327  ops/us
RandomEnginePerf.KISS64RandomPerf.nextDouble         thrpt   25   70.179 ±  0.962  ops/us
RandomEnginePerf.KISS64RandomPerf.nextFloat          thrpt   25   70.489 ±  0.871  ops/us
RandomEnginePerf.KISS64RandomPerf.nextInt            thrpt   25   71.264 ±  0.836  ops/us
RandomEnginePerf.KISS64RandomPerf.nextIntRange       thrpt   25   64.231 ±  0.278  ops/us
RandomEnginePerf.KISS64RandomPerf.nextLong           thrpt   25   71.908 ±  0.961  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextDouble     thrpt   25  106.935 ±  0.562  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextFloat      thrpt   25  100.884 ±  4.835  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextInt        thrpt   25   84.537 ± 16.616  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextIntRange   thrpt   25   88.029 ±  0.749  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextLong       thrpt   25  128.609 ±  0.975  ops/us
RandomEnginePerf.RandomPerf.nextDouble               thrpt   25   31.999 ±  0.066  ops/us
RandomEnginePerf.RandomPerf.nextFloat                thrpt   25   64.164 ±  0.141  ops/us
RandomEnginePerf.RandomPerf.nextInt                  thrpt   25   64.190 ±  0.101  ops/us
RandomEnginePerf.RandomPerf.nextIntRange             thrpt   25   63.736 ±  0.225  ops/us
RandomEnginePerf.RandomPerf.nextLong                 thrpt   25   31.993 ±  0.063  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextDouble       thrpt   25  141.136 ±  2.134  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextFloat        thrpt   25  178.039 ±  1.723  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextInt          thrpt   25  229.018 ±  3.053  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextIntRange     thrpt   25  142.176 ±  6.704  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextLong         thrpt   25  178.649 ±  2.741  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextDouble       thrpt   25  169.775 ±  1.850  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextFloat        thrpt   25  169.776 ±  1.879  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextInt          thrpt   25  205.252 ±  2.181  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextIntRange     thrpt   25  132.792 ±  0.934  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextLong         thrpt   25  225.306 ±  2.258  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextDouble    thrpt   25  126.312 ±  1.381  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextFloat     thrpt   25  129.921 ±  1.407  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextInt       thrpt   25  166.202 ±  2.425  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextIntRange  thrpt   25  113.317 ±  0.713  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextLong      thrpt   25  161.369 ±  0.578  ops/us
*/
