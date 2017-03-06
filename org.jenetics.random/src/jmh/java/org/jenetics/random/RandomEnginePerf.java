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

	public static class KISS32RandomPerf extends Base {
		{random = new KISS32Random();}
	}

	public static class KISS64RandomPerf extends Base {
		{random = new KISS64Random();}
	}

	public static class LCG64ShiftRandomPerf extends Base {
		{random = new LCG64ShiftRandom();}
	}

	public static class MT19937_32RandomPerf extends Base {
		{random = new MT19937_32Random();}
	}

	public static class MT19937_64RandomPerf extends Base {
		{random = new MT19937_64Random();}
	}

	public static class XOR32ShiftRandomPerf extends Base {
		{random = new XOR32ShiftRandom();}
	}

	public static class XOR64ShiftRandomPerf extends Base {
		{random = new XOR64ShiftRandom();}
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
			.measurementIterations(30)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}


}

/*
# Run complete. Total time: 00:36:55

Benchmark                                             Mode  Cnt    Score    Error   Units
RandomEnginePerf.KISS32RandomPerf.nextDouble         thrpt   30  103.570 ±  0.916  ops/us
RandomEnginePerf.KISS32RandomPerf.nextFloat          thrpt   30  138.115 ±  1.028  ops/us
RandomEnginePerf.KISS32RandomPerf.nextInt            thrpt   30  170.900 ±  2.096  ops/us
RandomEnginePerf.KISS32RandomPerf.nextIntRange       thrpt   30  128.729 ±  0.840  ops/us
RandomEnginePerf.KISS32RandomPerf.nextLong           thrpt   30  117.015 ±  0.864  ops/us
RandomEnginePerf.KISS64RandomPerf.nextDouble         thrpt   30  119.067 ±  0.942  ops/us
RandomEnginePerf.KISS64RandomPerf.nextFloat          thrpt   30  118.952 ±  1.065  ops/us
RandomEnginePerf.KISS64RandomPerf.nextInt            thrpt   30  123.097 ±  0.707  ops/us
RandomEnginePerf.KISS64RandomPerf.nextIntRange       thrpt   30  105.131 ±  1.327  ops/us
RandomEnginePerf.KISS64RandomPerf.nextLong           thrpt   30  125.716 ±  0.924  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextDouble     thrpt   30  181.937 ±  1.790  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextFloat      thrpt   30  175.151 ±  1.928  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextInt        thrpt   30  230.916 ±  4.003  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextIntRange   thrpt   30  149.653 ±  1.570  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextLong       thrpt   30  245.313 ±  2.650  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextDouble     thrpt   30   78.001 ±  1.319  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextFloat      thrpt   30  108.679 ±  1.480  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextInt        thrpt   30  130.030 ±  1.678  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextIntRange   thrpt   30  102.072 ±  1.763  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextLong       thrpt   30   85.609 ±  1.547  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextDouble     thrpt   30  111.420 ±  2.139  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextFloat      thrpt   30  112.836 ±  1.480  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextInt        thrpt   30  135.942 ±  1.218  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextIntRange   thrpt   30  103.395 ±  1.406  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextLong       thrpt   30  137.690 ±  2.588  ops/us
RandomEnginePerf.RandomPerf.nextDouble               thrpt   30   42.881 ±  0.382  ops/us
RandomEnginePerf.RandomPerf.nextFloat                thrpt   30   83.787 ±  0.892  ops/us
RandomEnginePerf.RandomPerf.nextInt                  thrpt   30   85.796 ±  0.811  ops/us
RandomEnginePerf.RandomPerf.nextIntRange             thrpt   30   84.139 ±  0.741  ops/us
RandomEnginePerf.RandomPerf.nextLong                 thrpt   30   40.212 ±  0.899  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextDouble       thrpt   30  228.859 ±  1.529  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextFloat        thrpt   30  274.058 ±  2.441  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextInt          thrpt   30  344.055 ±  4.329  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextIntRange     thrpt   30  234.598 ± 14.671  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextLong         thrpt   30  298.199 ±  4.963  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextDouble       thrpt   30  267.836 ±  2.806  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextFloat        thrpt   30  272.977 ±  2.425  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextInt          thrpt   30  332.301 ±  2.535  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextIntRange     thrpt   30  227.367 ±  3.256  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextLong         thrpt   30  344.666 ±  6.690  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextDouble    thrpt   30  213.452 ±  1.526  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextFloat     thrpt   30  207.892 ±  1.817  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextInt       thrpt   30  260.693 ±  3.843  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextIntRange  thrpt   30  202.895 ±  1.665  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextLong      thrpt   30  249.672 ±  3.049  ops/us
RandomEnginePerf.XOR32ShiftRandomPerf.nextDouble     thrpt   30  112.938 ±  0.730  ops/us
RandomEnginePerf.XOR32ShiftRandomPerf.nextFloat      thrpt   30  151.404 ±  1.878  ops/us
RandomEnginePerf.XOR32ShiftRandomPerf.nextInt        thrpt   30  209.063 ±  1.834  ops/us
RandomEnginePerf.XOR32ShiftRandomPerf.nextIntRange   thrpt   30  154.317 ±  1.198  ops/us
RandomEnginePerf.XOR32ShiftRandomPerf.nextLong       thrpt   30  131.908 ±  2.180  ops/us
RandomEnginePerf.XOR64ShiftRandomPerf.nextDouble     thrpt   30  158.106 ±  0.943  ops/us
RandomEnginePerf.XOR64ShiftRandomPerf.nextFloat      thrpt   30  154.839 ±  2.629  ops/us
RandomEnginePerf.XOR64ShiftRandomPerf.nextInt        thrpt   30  209.014 ±  1.249  ops/us
RandomEnginePerf.XOR64ShiftRandomPerf.nextIntRange   thrpt   30  141.018 ±  1.132  ops/us
RandomEnginePerf.XOR64ShiftRandomPerf.nextLong       thrpt   30  210.128 ±  1.786  ops/us

*/
