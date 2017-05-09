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

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.util.IO;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GenotypesXMLPerf {

	final Genotype<DoubleGene> gt = Genotype
		.of(DoubleChromosome.of(0.0, 1.0, 100), 10000);

	final Writer<Genotype<DoubleGene>> writer = Writers.Genotype
		.writer(Writers.DoubleChromosome.writer());

	@Benchmark
	public Object object() throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.object.write(gt, out);

		return out.toByteArray();
	}

	@Benchmark
	public Object jaxb() throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.jaxb.write(gt, out);

		return out.toByteArray();
	}

	@Benchmark
	public Object stream() throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
			writer.write(gt, xml);
		}

		return out.toByteArray();
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
			.include(".*" + GenotypesXMLPerf.class.getSimpleName() + ".*")
			.warmupIterations(10)
			.measurementIterations(15)
			.threads(1)
			.forks(1)
			.build();

		new Runner(opt).run();
	}

}
