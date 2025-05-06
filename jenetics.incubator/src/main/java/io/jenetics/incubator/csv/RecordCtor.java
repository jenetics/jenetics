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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.stream.Stream;

/**
 * Constructor function for constructing a record of type {@code T} from a
 * CSV {@code String[]} row.
 *
 * @param <T> the record type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
@FunctionalInterface
public interface RecordCtor<T> {

	/**
	 * Constructs a new record of type {@code T} from the given {@code String[]}
	 * array row.
	 *
	 * @param row the CSV row
	 * @return the constructed record
	 * @throws UnsupportedOperationException if the conversion target uses an
	 *         unsupported target type
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the <em>primitive</em> converter functions.
	 */
	T apply(final String[] row);

	/**
	 * Creates a new record constructor for the given record {@code type} and
	 * {@code converter}.
	 *
	 * @param type the record type
	 * @param converter the converter to use for converting the string
	 *        columns of the CSV row
	 * @return a new row-parser for the given record {@code type}
	 * @param <T> the record type
	 */
	static <T extends Record> RecordCtor<T>
	of(final Class<T> type, final Converter converter) {
		requireNonNull(type);
		requireNonNull(converter);

		final RecordComponent[] components = type.getRecordComponents();
		final Constructor<T> ctor = ctor(type);

		return row -> {
			final int length = Math.min(components.length, row.length);
			final Object[] values = new Object[components.length];
			for (int i = 0; i < length; ++i) {
				values[i] = converter.convert(row[i], components[i].getType());
			}
			return create(ctor, values);
		};
	}

	private static <T extends Record> Constructor<T> ctor(final Class<T> type) {
		final Class<?>[] columnTypes = Stream.of(type.getRecordComponents())
			.map(RecordComponent::getType)
			.toArray(Class<?>[]::new);

		try {
			return type.getDeclaredConstructor(columnTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
				"Canonical record constructor must be available.", e
			);
		}
	}

	private static <T> T create(final Constructor<T> ctor, final Object[] args) {
		try {
			return ctor.newInstance(args);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException rte) {
				throw rte;
			} else if (e.getCause() instanceof Error error) {
				throw error;
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
