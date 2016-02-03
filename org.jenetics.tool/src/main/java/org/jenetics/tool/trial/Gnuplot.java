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
package org.jenetics.tool.trial;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for creating <i>Gnuplot</i> graphs.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class Gnuplot {

	private static final String DATA_NAME = "data";
	private static final String OUTPUT_NAME = "output";

	private final Path _template;

	private final Map<String, String> _parameters = new HashMap<>();

	/**
	 * Create a new {@code Gnuplot} object with the given <i>template</i> path.
	 *
	 * @param template the Gnuplot template path
	 */
	public Gnuplot(final Path template) {
		_template = requireNonNull(template);
	}

	/**
	 * Generate the Gnuplot graph.
	 *
	 * @param data the input data file
	 * @param output the output path of the graph
	 * @throws IOException if the Gnuplot generation fails
	 */
	public void create(final Path data, final Path output)
		throws IOException
	{
		_parameters.put(DATA_NAME, data.toString());
		_parameters.put(OUTPUT_NAME, output.toString());

		final Process process = new ProcessBuilder()
			.command(command())
			.start();

		System.out.println(IO.toText(process.getErrorStream()));
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private List<String> command() {
		final String params = _parameters.entrySet().stream()
			.map(Gnuplot::toParamString)
			.collect(Collectors.joining("; "));

		return Arrays.asList("gnuplot", "-e", params, _template.toString());
	}

	private static String toParamString(final Map.Entry<String, String> entry) {
		return format("%s='%s'", entry.getKey(), entry.getValue());
	}

	public static void main(final String[] args)
		throws IOException, InterruptedException
	{
		final String base = "/home/fwilhelm/Workspace/Development/Projects/" +
			"Jenetics/org.jenetics/src/tool/resources/org/jenetics/trial/";

		final Path data = Paths.get(base, "knapsack_execution_time.dat");
		final Path output = Paths.get(base, "knapsack_execution_time.svg");

		final Gnuplot gnuplot = new Gnuplot(Paths.get(base, "sub_fitness.gp"));
		gnuplot.create(data, output);
	}

}
