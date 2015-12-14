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
package org.jenetics.trial;

import static java.lang.String.format;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Results {

	private final ISeq<String> _columns;
	private final ISeq<List<Double>> _values;

	public Results(final ISeq<String> columns) {
		_columns = requireNonNull(columns);
		_values = columns.map(c -> new ArrayList<>());
	}

	public void append(final int column, final double value) {
		_values.get(column).add(value);
	}

	public int size(final int column) {
		return _values.get(column).size();
	}

	public int rows() {
		return _values.stream()
			.mapToInt(List::size)
			.max()
			.orElse(0);
	}

	public Statistics statistics() {
		return null;
	}

	public void write(final File file) throws IOException {
		final File parent = file.getParentFile() != null
			? file.getParentFile()
			: file;

		final File temp = new File(parent, format("__%s", file.getName()));
		try {
			Files.write(temp.toPath(), toCSV().getBytes());
			Files.move(temp.toPath(), file.toPath(), ATOMIC_MOVE);
		} finally {
			Files.deleteIfExists(temp.toPath());
		}
	}

	private String toCSV() {
		final StringBuilder out = new StringBuilder();
		out.append(_columns.toString(","));

		for (int i = 0, n = rows(); i < n; ++i) {
			final int row = i;
			final String line = _values.stream()
				.map(rows -> rows.size() < row ? rows.get(row) : null)
				.map(col -> col != null ? col.toString() : "")
				.collect(Collectors.joining(","));

			out.append(line);
			out.append("\n");
		}

		return out.toString();
	}

	public static Results read(final File file) throws IOException {
		final List<String> lines = Files.readAllLines(file.toPath()).stream()
			.flatMap(line -> line.isEmpty() ? Stream.empty() : Stream.of(line))
			.collect(Collectors.toList());

		if (lines.isEmpty()) {
			throw new IOException(format("File '%s' is empty.", file));
		}

		final Results results = new Results(ISeq.of(lines));
		lines.subList(1, lines.size()).stream()
			.map(line -> line.split(","))
			.map(line -> Stream.of(line)
				.map(Results::toDouble)
				.toArray(Double[]::new))
			.forEach(results::append);

		return results;
	}

	private static Double toDouble(final String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void append(final Double[] values) {
		for (int i = 0; i < values.length; ++i) {
			if (values[i] != null) {
				append(i, values[i]);
			}
		}
	}

}
