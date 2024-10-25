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

import java.util.stream.IntStream;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Projection {
	private Projection() {
	}

	static CsvSupport.ColumnIndexes of(final Class<? extends Record> type) {
		final var components = type.getRecordComponents();
		final int[] projection = IntStream.range(0, components.length)
			.map(i -> {
				final var index = components[i].getAnnotation(Index.class);
				if (index != null) {
					return index.value();
				} else {
					return i;
				}
			})
			.toArray();

		return new CsvSupport.ColumnIndexes(projection);
	}

}
