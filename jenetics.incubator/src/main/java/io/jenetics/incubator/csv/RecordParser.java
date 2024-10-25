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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Parser function for parsing a {@link ColumnsRow} to an object of type {@code T}.
 *
 * @param <T> the target type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface RecordParser<T> extends Function<Row, T> {

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
	T parse(final Row value);

	@Override
	default T apply(final Row row) {
		return parse(row);
	}

	/**
	 * Creates a new row-parser for the given record {@code type}.
	 *
	 * @param type the record type
	 * @return a new row-parser for the given record {@code type}
	 * @param <T> the record type
	 */
	static <T extends Record> RecordParser<T> of(final Class<T> type) {
		final RecordComponent[] components = type.getRecordComponents();
		final Constructor<T> ctor = ctor(type);

		return row -> {
			final Object[] values = new Object[components.length];
			for (int i = 0; i < components.length; ++i) {
				values[i] = row.objectAt(i, components[i].getType());
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
