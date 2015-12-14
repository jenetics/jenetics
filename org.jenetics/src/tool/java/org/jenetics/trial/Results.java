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

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public Statistics statistics() {
		return null;
	}

	public void write(final File file) throws IOException {
		final File parent = file.getParentFile() != null
			? file.getParentFile()
			: file;
	}

	public static Results read(final File file) throws IOException {
		return null;
	}

}
