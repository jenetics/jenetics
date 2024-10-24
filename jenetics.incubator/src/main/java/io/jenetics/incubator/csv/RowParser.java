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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public interface RowParser<T> extends Function<Row, T> {

	T parse(final Row row);

	@Override
	default T apply(final Row row) {
		return parse(row);
	}

	default ColumnParser<T> with(final Map<Class<?>, Function<String, ?>> converters) {
		return columns -> parse(Row.of(converters, columns));
	}

	default ColumnParser<T> columns() {
		return with(Map.of());
	}

	default ColumnParser<T> compos(Function<? super String[], ? extends Row> fn) {
		return columns -> parse(fn.apply(columns));
	}

	static <T extends Record> RowParser<T> of(final Class<T> type) {
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
