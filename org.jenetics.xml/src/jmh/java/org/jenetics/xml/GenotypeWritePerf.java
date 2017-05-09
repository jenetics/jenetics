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
package org.jenetics.xml;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

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
import org.jenetics.util.IO;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class GenotypeWritePerf {

	public final Writer<Genotype<DoubleGene>> writer = Writers.Genotype
		.writer(Writers.DoubleChromosome.writer());

	@State(Scope.Thread)
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
	public Object object(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.object.write(state.genotype, out);
		return out.toByteArray();
	}

	@Benchmark
	public Object jaxb(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.jaxb.write(state.genotype, out);

		return out.toByteArray();
	}

	@Benchmark
	public Object stream(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
			writer.write(state.genotype, xml);
		}

		return out.toByteArray();
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + GenotypeWritePerf.class.getSimpleName() + ".*")
			.warmupIterations(10)
			.measurementIterations(15)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}

/*
# Run complete. Total time: 00:12:33

Benchmark                 (chromosomeCount)  Mode  Cnt        Score        Error  Units
GenotypeWritePerf.jaxb                    1  avgt   15       44.528 ±      0.630  us/op
GenotypeWritePerf.jaxb                   10  avgt   15      435.334 ±      5.573  us/op
GenotypeWritePerf.jaxb                  100  avgt   15     4436.904 ±     97.704  us/op
GenotypeWritePerf.jaxb                 1000  avgt   15    46180.033 ±    705.693  us/op
GenotypeWritePerf.jaxb                10000  avgt   15   469439.712 ±   5947.615  us/op
GenotypeWritePerf.jaxb               100000  avgt   15  5205345.300 ± 434537.458  us/op
GenotypeWritePerf.object                  1  avgt   15        5.283 ±      0.070  us/op
GenotypeWritePerf.object                 10  avgt   15       18.274 ±      0.253  us/op
GenotypeWritePerf.object                100  avgt   15      141.953 ±      3.564  us/op
GenotypeWritePerf.object               1000  avgt   15     1989.419 ±     37.496  us/op
GenotypeWritePerf.object              10000  avgt   15    23284.665 ±    216.587  us/op
GenotypeWritePerf.object             100000  avgt   15   304091.647 ± 122564.251  us/op
GenotypeWritePerf.stream                  1  avgt   15       63.584 ±      0.666  us/op
GenotypeWritePerf.stream                 10  avgt   15      595.413 ±     16.380  us/op
GenotypeWritePerf.stream                100  avgt   15     5872.970 ±     79.351  us/op
GenotypeWritePerf.stream               1000  avgt   15    60282.170 ±    777.615  us/op
GenotypeWritePerf.stream              10000  avgt   15   605028.098 ±   9447.686  us/op
GenotypeWritePerf.stream             100000  avgt   15  6825602.710 ± 812527.632  us/op
*/
