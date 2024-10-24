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
package io.jenetics.incubator.math;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, jvmArgs = {"-server", "-Xms1024M", "-Xmx1024M"})
@Threads(1)
@Warmup(iterations = 5)
@Measurement(iterations = 15)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ErfPerf {

	private double[] data;

	@Setup
	public void init() {
		final var random = RandomGenerator.getDefault();
		data = random.doubles(-100, 100)
			.limit(100)
			.toArray();
	}

	@Benchmark
	public void apacheErfc(final Blackhole bh) {
		double result = 0;
		for (var x : data) {
			result += org.apache.commons.math3.special.Erf.erfc(x);
		}
		bh.consume(result);
	}

	@Benchmark
	public void erfc(final Blackhole bh) {
		double result = 0;
		for (var x : data) {
			result += Erf.erfc(x);
		}
		bh.consume(result);
	}

}

/*
# JMH version: 1.36
# VM version: JDK 21, OpenJDK 64-Bit Server VM, 21+35-2513
# VM invoker: /home/fwilhelm/bin/jdk21/bin/java
# VM options: -server -Xms1024M -Xmx1024M
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 5 iterations, 10 s each
# Measurement: 15 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op

Benchmark            Mode  Cnt  Score    Error  Units
ErfcPerf.apacheErfc  avgt   15  6.441 ± 0.081  us/op
ErfcPerf.erfc        avgt   15  0.988 ± 0.017  us/op
 */
