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
package io.jenetics.incubator.csv;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Parser function for parsing a {@code String[]} array to a {@link Row}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface RowParser extends Function<String[], Row> {

	/**
	 * Row column-parser with the <em>default</em> {@link Converter#DEFAULT}
	 * converter.
	 */
	RowParser DEFAULT = ColumnsRow::new;

	/**
	 * Parses the {@code value} to an object of type {@code T}.
	 *
	 * @param value the value to parse
	 * @return the parsed value
	 * @throws UnsupportedOperationException if the conversion target uses an
	 *         unsupported target type
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the <em>primitive</em> converter functions.
	 */
	Row parse(final String[] value);

	@Override
	default Row apply(final String[] columns) {
		return parse(columns);
	}

	/**
	 * Create a {@link ColumnsRow} column-parser using the given {@code converter}.
	 *
	 * @param converter the converter used for the {@link ColumnsRow} object
	 * @return a {@link ColumnsRow} column-parser using the given {@code converter}
	 */
	static RowParser of(final Converter converter) {
		requireNonNull(converter);
		return value -> new ColumnsRow(value, converter);
	}

}
