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
package org.jenetics.tool.evaluation;

import static java.io.File.createTempFile;
import static java.lang.String.format;
import static java.nio.file.Files.deleteIfExists;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.internal.util.Args;

import org.jenetics.tool.trial.Gnuplot;
import org.jenetics.tool.trial.IO;
import org.jenetics.tool.trial.Params;
import org.jenetics.tool.trial.SampleSummary;
import org.jenetics.tool.trial.TrialMeter;

/**
 * Helper class for creating Gnuplot diagrams from result files.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.7
 * @since 3.4
 */
public class Diagram {

	/**
	 * The available Gnuplot templates.
	 */
	public static enum Template {

		/**
		 * Template for execution time termination diagrams.
		 */
		EXECUTION_TIME("execution_time_termination"),

		GENERATION_POPULATION_SIZE("generation_population_size"),

		/**
		 * Template for fitness threshold termination diagrams.
		 */
		FITNESS_THRESHOLD("fitness_threshold_termination"),

		/**
		 * Template for fitness threshold termination diagrams.
		 */
		FITNESS_CONVERGENCE("fitness_convergence_termination"),

		/**
		 * Template for fixed generation termination diagrams.
		 */
		FIXED_GENERATION("fixed_generation_termination"),

		/**
		 * Template for steady fitness termination diagrams,
		 */
		STEADY_FITNESS("steady_fitness_termination"),

		/**
		 * Template for comparing different selectors.
		 */
		SELECTOR_COMPARISON("selector_comparison"),

		POPULATION_SIZE("population_size");

		private final String _name;
		private final String _path;

		private Template(final String name) {
			_name = requireNonNull(name);
			_path = "/org/jenetics/tool/evaluation/" +
				requireNonNull(name) + ".gp";
		}

		public String getName() {
			return _name;
		}

		/**
		 * Return the template content as string.
		 *
		 * @return the template content
		 */
		public String content() {
			try (InputStream stream = Diagram.class.getResourceAsStream(_path)) {
				return IO.toText(stream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * Create a performance diagram.
	 *
	 * @param input the input data
	 * @param template the Gnuplot template to use
	 * @param params the diagram parameters (x-axis)
	 * @param output the output file
	 * @param summary the first summary data
	 * @param summaries the rest of the summary data
	 * @throws IOException if the diagram generation fails
	 * @throws NullPointerException of one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the {@code params}, {@code generation}
	 *         and {@code fitness} doesn't have the same parameter count
	 */
	public static void create(
		final Path input,
		final Template template,
		final Params<?> params,
		final Path output,
		final SampleSummary summary,
		final SampleSummary... summaries
	)
		throws IOException
	{
		final Stream<SampleSummary> summaryStream = Stream.concat(
			Stream.of(summary), Stream.of(summaries)
		);
		summaryStream.forEach(s -> {
			if (params.size() != s.parameterCount()) {
				throw new IllegalArgumentException(format(
					"Parameters have different size: %d", params.size()
				));
			}
		});

		final Path templatePath = tempPath();
		try {
			IO.write(template.content(), templatePath);

			final Path dataPath = tempPath();
			try {
				final String data = IntStream.range(0, params.size())
					.mapToObj(i -> toLineString(i, params, summary, summaries))
					.collect(Collectors.joining("\n"));
				IO.write(data, dataPath);

				final Gnuplot gnuplot = new Gnuplot(templatePath);
				gnuplot.setEnv(params(input));
				gnuplot.create(dataPath, output);
			} finally {
				deleteIfExists(dataPath);
			}
		} finally {
			deleteIfExists(templatePath);
		}
	}

	private static Path tempPath() throws IOException {
		return createTempFile("__diagram_template__", "__").toPath();
	}

	private static String toLineString(
		final int index,
		final Params<?> params,
		final SampleSummary summary,
		final SampleSummary... summaries
	) {
		return concat(concat(
				Stream.of(params.get(index).toString().split(":")),
				DoubleStream.of(summary.getPoints().get(index).toArray())
					.mapToObj(Double::toString)),
				Stream.of(summaries)
					.flatMapToDouble(s -> DoubleStream.of(s.getPoints().get(index).toArray()))
					.mapToObj(Double::toString))
			.collect(Collectors.joining(" "));
	}

	public static void main(final String[] arguments) throws Exception {
		final Args args = Args.of(arguments);

		final Path input = args.arg("input")
			.map(Paths::get)
			.map(Path::toAbsolutePath)
			.get();

		final String[] samples = args.arg("samples")
			.map(s -> s.split(","))
			.orElse(new String[]{"Generation", "Fitness"});

		final TrialMeter<Integer> trial = TrialMeter.read(input);
		final Params<Integer> params = trial.getParams();
		final SampleSummary summary = trial.getData(samples[0]).summary();
		final SampleSummary[] summaries = Arrays.stream(samples, 1, samples.length)
			.map(s -> trial.getData(s).summary())
			.toArray(SampleSummary[]::new);

		create(
			input,
			template(input),
			params,
			output(input),
			summary,
			summaries
		);
	}

	private static Template template(final Path path) {
		final String name = path.getFileName().toString()
			.split("-")[1]
			.split("\\.")[0];

		return Arrays.stream(Template.values())
			.filter(t -> t.getName().equals(name))
			.findFirst().get();
	}

	private static Map<String, String> params(final Path path) {
		System.out.println(path.getFileName());
		final List<String> parts = param(path.getFileName().toString())
			.flatMap(p -> Stream.of(p.split("@")))
			.collect(Collectors.toList());

		final Map<String, String> params = new HashMap<>();
		for (int i = 0; i < parts.size(); ++i) {
			final String key = format("PARAM_%s", i);
			params.put(key, parts.get(i));
		}

		return params;
	}

	private static Stream<String> param(final String name) {
		final String[] parts = name.split("-");
		return parts.length == 3
			? Stream.of(parts[2].split("\\.")[0])
			: Stream.empty();
	}

	private static Path output(final Path path) {
		final String name = path.getFileName().toString().split("\\.")[0];
		return Paths.get(path.getParent().toString(), name + ".svg");
	}

}
