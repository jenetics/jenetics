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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public class ToGnuplotResult {

	public static void main(final String[] args) throws Exception {
		final Path file = Paths.get("/home/fwilhelm/Workspace/Development/Projects/Jenetics/io.jenetics.xml/src/jmh/java/org/jenetics/xml/GenotypeReadPerf.java");//Paths.get(args[0]);
		final String prefix = "GenotypeReadPerf";//args[1];

		final Result result = new Result("jaxb", "object", "stream");
		Files.readAllLines(file).stream()
			.filter(line -> line.startsWith(prefix))
			.map(ToGnuplotResult::split)
			.forEach(result::put);

		System.out.println(result);
	}

	private static String[] split(final String line) {
		final String[] p = line.split("\\s+");
		return new String[]{p[0], p[1], p[4], p[6]};
	}

	private static final class Result {
		final String[] header;

		final Map<Integer, double[]> data = new HashMap<>();

		Result(final String... header) {
			this.header = header;
		}

		void put(final String[] line) {
			final String name = line[0];
			final int count = Integer.parseInt(line[1]);
			final double[] row = data.computeIfAbsent(count, c -> new double[header.length*2]);
			col(name).ifPresent(i -> {
				row[i*2] = Double.parseDouble(line[2]);
				row[i*2 + 1] = Double.parseDouble(line[3]);
			});
		}

		private OptionalInt col(final String name) {
			return IntStream.range(0, header.length)
				.filter(i -> name.endsWith(header[i]))
				.findFirst();
		}

		@Override
		public String toString() {
			final StringBuilder out = new StringBuilder();
			final List<Map.Entry<Integer, double[]>> sortedData =
				new ArrayList<>(data.entrySet());

			sortedData.sort(Comparator.comparing(Map.Entry::getKey));

			out.append("#Chromosomes").append("\t");
			for (String h : header) {
				out.append(format("Score[%s]", h)).append("\t");
				out.append(format("Error[%s]", h)).append("\t");
			}
			out.append("\n");
			for (Map.Entry<Integer, double[]> row : sortedData) {
				out.append(row.getKey()).append("\t");
				for (int i = 0; i < row.getValue().length; ++i) {
					out.append(row.getValue()[i]).append("\t");
				}
				out.append("\n");
			}

			return out.toString();
		}
	}

}
