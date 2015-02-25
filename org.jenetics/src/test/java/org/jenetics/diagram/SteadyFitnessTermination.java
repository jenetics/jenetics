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
package org.jenetics.diagram;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.jenetics.diagram.CandleStickPoint.toCandleStickPoint;
import static org.jenetics.engine.EvolutionResult.toBestEvolutionResult;
import static org.jenetics.engine.limit.bySteadyFitness;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.Gene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 */
public class SteadyFitnessTermination<G extends Gene<?, G>> {

	private final int _samples;
	private final Engine<G, Double> _engine;

	public SteadyFitnessTermination(
		final Engine<G, Double> engine,
		final int samples
	) {
		_engine = requireNonNull(engine);
		_samples = samples;
	}

	public void write(final File file) throws IOException {
	}

	static void write(final File file, final Object[][] data)
		throws IOException
	{
		final String[][] sdata = Stream.of(data)
			.map(e -> Stream.of(e).map(Objects::toString).toArray(String[]::new))
			.toArray(String[][]::new);

		final int[] width = Stream.of(sdata)
			.map(e -> Stream.of(e).mapToInt(String::length).toArray())
			.collect(toWidth(sdata[0].length));

		final String pattern = IntStream.of(width)
			.mapToObj(i -> format("%%%ds", i))
			.collect(joining("    "));

		try (PrintWriter writer = new PrintWriter(file)) {
			System.out.println("#" + format(pattern, sdata[0]));
			writer.println("#" + format(pattern, sdata[0]));

			Stream.of(sdata).skip(1).forEach(d -> {
				System.out.println(" " + format(pattern, d));
				writer.println(" " + format(pattern, d));
			});
		}

	}

	private static Collector<int[], ?, int[]> toWidth(final int length) {
		return Collector.of(() -> new int[length],
			SteadyFitnessTermination::max,
			SteadyFitnessTermination::max
		);
	}

	private static int[] max(final int[] a, final int[] b) {
		for (int i = 0; i < a.length; ++i) {
			a[i] = Math.max(a[i], b[i]);
		}
		return a;
	}

	Object[] run(final int generation) {
		System.out.println("Generation: " + generation);
		final CandleStickPoint[] result = eval(generation);
		final Object[] data = new Object[11];

		data[0] = generation;

		// Total generation
		data[1] = (int)result[0].median;
		data[2] = (int)result[0].low;
		data[3] = (int)result[0].high;
		data[4] = (int)result[0].min;
		data[5] = (int)result[0].max;

		// Fitness
		data[6] = df(result[1].median);
		data[7] = df(result[1].low);
		data[8] = df(result[1].high);
		data[9] = df(result[1].min);
		data[10] = df(result[1].max);

		return data;
	}

	private static String df(final double value) {
		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(7);
		format.setMinimumFractionDigits(7);

		return format.format(value).replace(",", "");
	}

	private CandleStickPoint[] eval(final int generation) {
		return IntStream.range(0, _samples)
			.mapToObj(i -> toResult(generation))
			.collect(toCandleStickPoint(a -> a._1, a -> a._2));
	}

	private IntDoublePair toResult(final int generation) {
		final EvolutionResult<G, Double> result = _engine.stream()
			.limit(bySteadyFitness(generation))
			.collect(toBestEvolutionResult());

		return IntDoublePair.of(
			(int)result.getTotalGenerations(),
			result.getBestFitness()
		);
	}

}
