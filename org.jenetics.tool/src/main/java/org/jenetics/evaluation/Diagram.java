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
package org.jenetics.evaluation;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.tool.trial.Gnuplot;
import org.jenetics.tool.trial.IO;
import org.jenetics.tool.trial.Params;
import org.jenetics.tool.trial.SampleSummary;
import org.jenetics.tool.trial.TrialMeter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Diagram {

	public static enum Template {
		EXECUTION_TIME("execution_time_termination.gp"),
		FITNESS_THRESHOLD("fitness_threshold_termination.gp"),
		FIXED_GENERATION("fixed_generation_termination.gp"),
		STEADY_FITNESS("steady_fitness_termination.gp");

		private static final String BASE = "/org/jenetics/evaluation";

		private final String _path;

		private Template(final String path) {
			_path = requireNonNull(path);
		}

		public String text() {
			final String rsc = BASE + "/" + _path;
			System.out.println(rsc);
			try (InputStream stream = Diagram.class.getResourceAsStream(rsc)) {
				return IO.toText(stream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public static void create(
		final Template template,
		final Params<?> params,
		final SampleSummary generation,
		final SampleSummary fitness,
		final Path output
	)
		throws IOException
	{
		if (params.size() != generation.size() || params.size() != fitness.size()) {
			throw new IllegalArgumentException(format(
				"Parameters have different size: [%s, %s, %s].",
				params.size(), generation.size(), fitness.size()
			));
		}

		final Path templatePath = File
			.createTempFile("__diagram_template__", "__").toPath();
		try {
			IO.write(template.text(), templatePath);

			final Path dataPath = File
				.createTempFile("__diagram_data__", "__").toPath();
			try {
				final String data = IntStream.range(0, params.size())
					.mapToObj(i -> toLineString(i, params, generation, fitness))
					.collect(Collectors.joining("\n"));
				IO.write(data, dataPath);


				final Gnuplot gnuplot = new Gnuplot(templatePath);
				gnuplot.create(dataPath, output);
			} finally {
				Files.deleteIfExists(dataPath);
			}
		} finally {
			Files.deleteIfExists(templatePath);
		}
	}

	private static String toLineString(
		final int index,
		final Params<?> params,
		final SampleSummary generation,
		final SampleSummary fitness
	) {
		return concat(concat(
				Stream.of(params.get().get(index).toString()),
				DoubleStream.of(generation.getPoints().get(index).toArray())
					.mapToObj(Double::toString)),
				DoubleStream.of(fitness.getPoints().get(index).toArray())
					.mapToObj(Double::toString))
			.collect(Collectors.joining(" "));
	}

	public static void main(final String[] args) throws Exception {
		final TrialMeter<Integer> trial = TrialMeter.read(Paths.get(
			"/home/fwilhelm/Workspace/Development/Projects/Jenetics/org.jenetics/src/tool/resources/org/jenetics/trail/",
			"knapsack_steady_fitness-perf.xml"
		));

		final Params<Integer> params = trial.getParams();
		final SampleSummary generation = trial.getDataSet().getSet("Generation").summary();
		final SampleSummary fitness = trial.getDataSet().getSet("Fitness").summary();

		create(
			Template.STEADY_FITNESS,
			params,
			generation,
			fitness,
			Paths.get("/home/fwilhelm/test.svg")
		);
	}

}
