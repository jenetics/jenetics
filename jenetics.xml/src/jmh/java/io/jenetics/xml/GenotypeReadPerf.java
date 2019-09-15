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

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
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

import io.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import io.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import io.jenetics.xml.stream.Reader;
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
public class GenotypeReadPerf {

	public static final Writer<Genotype<DoubleGene>> writer = Writers.Genotype
		.writer(Writers.DoubleChromosome.writer());

	public static final Reader<Genotype<DoubleGene>> reader = Readers.Genotype
		.reader(Readers.DoubleChromosome.reader());

	@State(Scope.Benchmark)
	public static class IOState {

		@Param({"1", "10", "100", "1000", "10000", "100000"})
		public int chromosomeCount;

		public Genotype<DoubleGene> genotype;

		public byte[] objectData;
		public byte[] jaxbData;
		public byte[] streamData;

		@Setup
		public void setup() throws Exception {
			genotype = Genotype.of(
				DoubleChromosome.of(0.0, 1.0, 100),
				chromosomeCount
			);

			objectData = object(genotype);
			streamData = stream(genotype);

			System.out.println(format(
				"Size[chromosomes=%s, object=%s, jaxb=%s, stream=%s]",
				chromosomeCount,
				mib(objectData.length),
				mib(jaxbData.length),
				mib(streamData.length)
			));
		}

		private static String mib(final int size) {
			return format("%.4f MiB", (double)size/(1024*1024));
		}

		public static byte[] object(final Genotype<DoubleGene> gt) throws Exception {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			IO.object.write(gt, out);
			return out.toByteArray();
		}

		private static byte[] stream(final Genotype<DoubleGene> gt)
			throws Exception
		{
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
				writer.write(xml, gt);
			}
			return out.toByteArray();
		}

	}

	@Benchmark
	public Object object(final IOState state) throws Exception {
		final ByteArrayInputStream in = new ByteArrayInputStream(state.objectData);
		return IO.object.read(in);
	}

	@Benchmark
	public Object stream(final IOState state) throws Exception {
		final ByteArrayInputStream in = new ByteArrayInputStream(state.streamData);
		try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
			xml.next();
			return reader.read(xml);
		}
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(GenotypeReadPerf.class.getSimpleName())
			.warmupIterations(10)
			.measurementIterations(25)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}

/*
Size[chromosomes=1, object=0.0017 MiB, jaxb=0.0045 MiB, stream=0.0035 MiB]
Size[chromosomes=10, object=0.0090 MiB, jaxb=0.0439 MiB, stream=0.0346 MiB]
Size[chromosomes=100, object=0.0812 MiB, jaxb=0.4379 MiB, stream=0.3459 MiB]
Size[chromosomes=1000, object=0.8039 MiB, jaxb=4.3772 MiB, stream=3.4578 MiB]
Size[chromosomes=10000, object=8.0309 MiB, jaxb=43.7730 MiB, stream=34.5795 MiB]
Size[chromosomes=100000, object=80.3003 MiB, jaxb=437.7283 MiB, stream=345.7940 MiB]

# Run complete. Total time: 00:23:17

Benchmark                (chromosomeCount)  Mode  Cnt         Score        Error  Units
GenotypeReadPerf.jaxb                    1  avgt   25       168.178 ±     43.804  us/op
GenotypeReadPerf.jaxb                   10  avgt   25       932.813 ±    103.739  us/op
GenotypeReadPerf.jaxb                  100  avgt   25      9102.482 ±    841.143  us/op
GenotypeReadPerf.jaxb                 1000  avgt   25     93216.813 ±   9814.003  us/op
GenotypeReadPerf.jaxb                10000  avgt   25    901538.952 ±  52950.669  us/op
GenotypeReadPerf.jaxb               100000  avgt   25   9050957.940 ± 403521.551  us/op
GenotypeReadPerf.object                  1  avgt   25        43.193 ±      0.371  us/op
GenotypeReadPerf.object                 10  avgt   25        65.423 ±      0.748  us/op
GenotypeReadPerf.object                100  avgt   25       279.793 ±      2.188  us/op
GenotypeReadPerf.object               1000  avgt   25      1886.943 ±     16.895  us/op
GenotypeReadPerf.object              10000  avgt   25     18830.820 ±    268.658  us/op
GenotypeReadPerf.object             100000  avgt   25    273564.198 ±  36077.894  us/op
GenotypeReadPerf.stream                  1  avgt   25       106.167 ±      0.967  us/op
GenotypeReadPerf.stream                 10  avgt   25       899.680 ±     11.792  us/op
GenotypeReadPerf.stream                100  avgt   25      9100.424 ±     79.534  us/op
GenotypeReadPerf.stream               1000  avgt   25     88677.488 ±    817.563  us/op
GenotypeReadPerf.stream              10000  avgt   25    911031.414 ±   7758.277  us/op
GenotypeReadPerf.stream             100000  avgt   25  10284911.531 ± 667552.440  us/op
*/
