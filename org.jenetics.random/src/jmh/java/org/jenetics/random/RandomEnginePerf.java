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
# Run complete. Total time: 00:30:12

Benchmark                                             Mode  Cnt    Score   Error   Units
RandomEnginePerf.KISS32RandomPerf.nextDouble         thrpt   30  116.714 ± 0.383  ops/us
RandomEnginePerf.KISS32RandomPerf.nextFloat          thrpt   30  158.301 ± 0.386  ops/us
RandomEnginePerf.KISS32RandomPerf.nextInt            thrpt   30  194.571 ± 2.499  ops/us
RandomEnginePerf.KISS32RandomPerf.nextIntRange       thrpt   30  146.216 ± 0.995  ops/us
RandomEnginePerf.KISS32RandomPerf.nextLong           thrpt   30  135.141 ± 0.875  ops/us
RandomEnginePerf.KISS64RandomPerf.nextDouble         thrpt   30  132.698 ± 0.966  ops/us
RandomEnginePerf.KISS64RandomPerf.nextFloat          thrpt   30  134.564 ± 0.893  ops/us
RandomEnginePerf.KISS64RandomPerf.nextInt            thrpt   30  138.735 ± 0.663  ops/us
RandomEnginePerf.KISS64RandomPerf.nextIntRange       thrpt   30  118.423 ± 0.748  ops/us
RandomEnginePerf.KISS64RandomPerf.nextLong           thrpt   30  142.344 ± 0.563  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextDouble     thrpt   30  206.123 ± 0.552  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextFloat      thrpt   30  199.399 ± 0.393  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextInt        thrpt   30  264.727 ± 2.683  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextIntRange   thrpt   30  169.448 ± 1.228  ops/us
RandomEnginePerf.LCG64ShiftRandomPerf.nextLong       thrpt   30  278.635 ± 1.256  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextDouble     thrpt   30   88.522 ± 0.802  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextFloat      thrpt   30  126.057 ± 0.869  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextInt        thrpt   30  147.657 ± 1.032  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextIntRange   thrpt   30  115.891 ± 0.582  ops/us
RandomEnginePerf.MT19937_32RandomPerf.nextLong       thrpt   30   97.796 ± 0.310  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextDouble     thrpt   30  127.831 ± 1.210  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextFloat      thrpt   30  128.200 ± 0.581  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextInt        thrpt   30  153.233 ± 0.781  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextIntRange   thrpt   30  117.296 ± 1.122  ops/us
RandomEnginePerf.MT19937_64RandomPerf.nextLong       thrpt   30  157.719 ± 1.001  ops/us
RandomEnginePerf.RandomPerf.nextDouble               thrpt   30   48.438 ± 0.321  ops/us
RandomEnginePerf.RandomPerf.nextFloat                thrpt   30   95.325 ± 0.509  ops/us
RandomEnginePerf.RandomPerf.nextInt                  thrpt   30   97.217 ± 0.272  ops/us
RandomEnginePerf.RandomPerf.nextIntRange             thrpt   30   96.856 ± 0.918  ops/us
RandomEnginePerf.RandomPerf.nextLong                 thrpt   30   48.126 ± 0.364  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextDouble       thrpt   30  259.161 ± 0.693  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextFloat        thrpt   30  332.504 ± 1.370  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextInt          thrpt   30  389.586 ± 1.416  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextIntRange     thrpt   30  271.965 ± 7.329  ops/us
RandomEnginePerf.SimpleRandom32Perf.nextLong         thrpt   30  335.994 ± 4.940  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextDouble       thrpt   30  309.965 ± 1.976  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextFloat        thrpt   30  312.811 ± 3.011  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextInt          thrpt   30  377.539 ± 1.524  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextIntRange     thrpt   30  256.810 ± 0.950  ops/us
RandomEnginePerf.SimpleRandom64Perf.nextLong         thrpt   30  390.168 ± 1.929  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextDouble    thrpt   30  241.195 ± 0.650  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextFloat     thrpt   30  235.079 ± 0.722  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextInt       thrpt   30  295.362 ± 1.196  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextIntRange  thrpt   30  230.225 ± 0.574  ops/us
RandomEnginePerf.ThreadLocalRandomPerf.nextLong      thrpt   30  283.781 ± 0.476  ops/us
*/
