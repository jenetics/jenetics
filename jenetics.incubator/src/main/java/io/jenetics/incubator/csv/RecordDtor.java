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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

public interface RecordDtor<T> {

	String[] unapply(T record);

	static <T extends Record> RecordDtor<T>
	of(final Class<T> type, final Formatter formatter) {
		requireNonNull(formatter);
		final RecordComponent[] components = type.getRecordComponents();

		return record -> {
			final String[] values = new String[components.length];
			for (int i = 0; i < components.length; ++i) {
				final var component = get(components[i], record);
				values[i] = formatter.format(component);
			}
			return values;
		};
	}

	static <T extends Record> RecordDtor<T> of(final Class<T> type) {
		return of(type, Formatter.DEFAULT);
	}

	private static Object get(RecordComponent component, Object record) {
		try {
			return component.getAccessor().invoke(record);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}

}
