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
package io.jenetics.internal.util;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import io.jenetics.internal.collection.BitArray;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class BitArrayPerf {

	BitArray array;

	@Setup
	public void setup() {
		array = BitArray.ofLength(1000);
	}

	@Benchmark
	public byte[] naiveBitArray() {
		byte[] bytes = Bits.newArray(array.length());
		for (int i = 0, n = array.length(); i < n; ++i) {
			Bits.set(bytes, i, array.get(i));
		}
		return bytes;
	}

	@Benchmark
	public byte[] toBitArray() {
		return array.toByteArray();
	}

	@Benchmark
	public int bitArrayHashCode() {
		return array.hashCode();
	}

	@Benchmark
	public int byteArrayHashCode() {
		return Arrays.hashCode(array.toByteArray());
	}

	@Benchmark
	public int bigIntegerHashCode() {
		return array.toBigInteger().hashCode();
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + BitArrayPerf.class.getSimpleName() + ".*")
			.warmupIterations(4)
			.measurementIterations(7)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}

/*
Benchmark                        Mode  Cnt     Score    Error  Units
BitArrayPerf.bigIntegerHashCode  avgt   15   140.946 ±  1.040  ns/op
BitArrayPerf.bitArrayHashCode    avgt   15  1406.953 ± 10.515  ns/op
BitArrayPerf.byteArrayHashCode   avgt   15   137.089 ±  1.635  ns/op
BitArrayPerf.naiveBitArray       avgt   15  2687.904 ± 37.137  ns/op
BitArrayPerf.toBitArray          avgt   15    30.531 ±  0.407  ns/op

 */
