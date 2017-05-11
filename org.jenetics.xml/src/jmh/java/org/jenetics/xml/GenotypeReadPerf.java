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

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.util.IO;
import org.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
			jaxbData = jaxb(genotype);
			streamData = stream(genotype);
		}

		public static byte[] object(final Genotype<DoubleGene> gt) throws Exception {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			IO.object.write(gt, out);
			return out.toByteArray();
		}

		public static byte[] jaxb(final Genotype<DoubleGene> gt) throws Exception {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			IO.jaxb.write(gt, out);
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
	public Object jaxb(final IOState state) throws Exception {
		final ByteArrayInputStream in = new ByteArrayInputStream(state.jaxbData);
		return IO.jaxb.read(in);
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
