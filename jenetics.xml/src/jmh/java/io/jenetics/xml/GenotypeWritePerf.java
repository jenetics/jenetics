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
package io.jenetics.xml;

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

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.util.IO;

import io.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import io.jenetics.xml.stream.Writer;
import io.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
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
	public Object object(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.object.write(state.genotype, out);
		return out.toByteArray();
	}

	@Benchmark
	public Object stream(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
			writer.write(xml, state.genotype);
		}

		return out.toByteArray();
	}

	@Benchmark
	public Object emptyStream(final IOState state) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (AutoCloseableXMLStreamWriter xml = new EmptyXMLStreamWriter(out)) {
			writer.write(xml, state.genotype);
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
# Run complete. Total time: 00:22:27

Benchmark                      (chromosomeCount)  Mode  Cnt        Score        Error  Units
GenotypeWritePerf.emptyStream                  1  avgt   25       32.393 ±      0.467  us/op
GenotypeWritePerf.emptyStream                 10  avgt   25      315.596 ±      4.421  us/op
GenotypeWritePerf.emptyStream                100  avgt   25     3162.672 ±     40.256  us/op
GenotypeWritePerf.emptyStream               1000  avgt   25    32254.042 ±    222.167  us/op
GenotypeWritePerf.emptyStream              10000  avgt   25   326237.190 ±   6900.882  us/op
GenotypeWritePerf.emptyStream             100000  avgt   25  3230708.933 ±  17768.231  us/op
GenotypeWritePerf.jaxb                         1  avgt   25       46.237 ±      0.564  us/op
GenotypeWritePerf.jaxb                        10  avgt   25      448.078 ±      5.265  us/op
GenotypeWritePerf.jaxb                       100  avgt   25     4477.686 ±     62.613  us/op
GenotypeWritePerf.jaxb                      1000  avgt   25    45964.522 ±    552.452  us/op
GenotypeWritePerf.jaxb                     10000  avgt   25   477826.242 ±  12626.502  us/op
GenotypeWritePerf.jaxb                    100000  avgt   25  5146163.050 ± 318853.258  us/op
GenotypeWritePerf.object                       1  avgt   25        5.464 ±      0.115  us/op
GenotypeWritePerf.object                      10  avgt   25       18.431 ±      0.159  us/op
GenotypeWritePerf.object                     100  avgt   25      140.085 ±      1.799  us/op
GenotypeWritePerf.object                    1000  avgt   25     1975.590 ±     25.421  us/op
GenotypeWritePerf.object                   10000  avgt   25    23747.132 ±    380.077  us/op
GenotypeWritePerf.object                  100000  avgt   25   329312.176 ± 219264.291  us/op
GenotypeWritePerf.stream                       1  avgt   25       66.248 ±      0.967  us/op
GenotypeWritePerf.stream                      10  avgt   25      593.448 ±      7.709  us/op
GenotypeWritePerf.stream                     100  avgt   25     5938.576 ±     77.691  us/op
GenotypeWritePerf.stream                    1000  avgt   25    58722.709 ±    520.807  us/op
GenotypeWritePerf.stream                   10000  avgt   25   614540.820 ±   7481.422  us/op
GenotypeWritePerf.stream                  100000  avgt   25  6975361.258 ± 554799.213  us/op
*/
