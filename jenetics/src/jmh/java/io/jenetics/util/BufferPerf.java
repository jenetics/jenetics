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
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 5.1
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BufferPerf {


	private int[] data;
	private Buffer<Integer> buffer;

	@State(Scope.Benchmark)
	public static class TestBuffer {
		Buffer<Integer> buffer = createBuffer();
	}

	private static Buffer<Integer> createBuffer() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(1_000);
		new Random().ints(1_500).forEach(buffer::add);
		return buffer;
	}

	@Setup
	public void setup() {
		data = new Random().ints(1_000_000).toArray();
		buffer = Buffer.ofCapacity(1_000);
	}

	@Benchmark
	public Object toSeq(final TestBuffer tb) {
		return tb.buffer.toSeq();
	}

	@Benchmark
	public Object toArray(final TestBuffer tb) {
		return tb.buffer.toArray();
	}

	@Benchmark
	public Object toTypedArray(final TestBuffer tb) {
		return tb.buffer.toArray(Integer[]::new);
	}


	public static void main(final String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + BufferPerf.class.getSimpleName() + ".*")
			.warmupIterations(4)
			.measurementIterations(5)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
