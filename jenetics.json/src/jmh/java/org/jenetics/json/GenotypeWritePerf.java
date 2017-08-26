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
package org.jenetics.json;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import com.google.gson.stream.JsonWriter;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.json.stream.Writer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class GenotypeWritePerf {

	public static final Writer<Genotype<DoubleGene>> writer = Writers.Genotype
		.writer(Writers.DoubleChromosome.writer());

	@State(Scope.Benchmark)
	public static class IOState {

		@Param({"1", "10", "100", "1000", "10000", "100000"})
		public int chromosomeCount;

		public Genotype<DoubleGene> genotype;

		@Setup
		public void setup() {
			genotype = Genotype.of(
				DoubleChromosome.of(0.0, 1.0, 100),
				chromosomeCount
			);
		}

	}

	@Benchmark
	public Object stream(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (final JsonWriter json = new JsonWriter(new OutputStreamWriter(out))) {
			writer.write(json, state.genotype);
		}

		return out.toByteArray();
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(GenotypeWritePerf.class.getSimpleName())
			.warmupIterations(10)
			.measurementIterations(25)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}

/*
# Run complete. Total time: 00:06:01

Benchmark                 (chromosomeCount)  Mode  Cnt        Score        Error  Units
GenotypeWritePerf.stream                  1  avgt   25       42.725 ±      0.396  us/op
GenotypeWritePerf.stream                 10  avgt   25      419.298 ±      8.067  us/op
GenotypeWritePerf.stream                100  avgt   25     4256.276 ±     67.198  us/op
GenotypeWritePerf.stream               1000  avgt   25    42838.930 ±    612.493  us/op
GenotypeWritePerf.stream              10000  avgt   25   438458.559 ±   4428.120  us/op
GenotypeWritePerf.stream             100000  avgt   25  4787233.831 ± 410710.510  us/op
*/
