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

/**
 * Parser function for parsing a {@code String} line to a {@code String[]} array.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface ColumnsParser extends Function<String, String[]> {

	/**
	 * Parses the {@code value} to a {@code String[]} array.
	 *
	 * @param value the value to parse
	 * @return the parsed value
	 * @throws UnsupportedOperationException if the conversion target uses an
	 *         unsupported target type
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the <em>primitive</em> converter functions.
	 */
	String[] parse(String value);

	@Override
	default String[] apply(final String line) {
		return parse(line);
	}

}
