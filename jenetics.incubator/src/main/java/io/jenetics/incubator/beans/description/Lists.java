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
package io.jenetics.incubator.beans.description;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Some helper methods for accessing list methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Lists {
	private Lists() {
	}

	/**
	 * Returns the length of the specified list object, as an {@code int}.
	 *
	 * @param list the list
	 * @return the length of the array
	 * @throws NullPointerException if the specified object is {@code null}
	 * @throws IllegalArgumentException If the specified object is not a
	 *         {@link List}
	 */
	static int size(Object list) {
		requireNonNull(list);

		if (list instanceof List<?> l) {
			return l.size();
		} else {
			throw noListValue(list);
		}
	}

	private static IllegalArgumentException noListValue(final Object value) {
		return new IllegalArgumentException(
			"Given object is not a list: %s.".formatted(value)
		);
	}

	/**
	 * Returns the value of the indexed component in the specified list object.
	 *
	 * @param list the list
	 * @param index the index
	 * @return the value of the indexed component in the specified list
	 * @throws NullPointerException if the specified object is {@code null}
	 * @throws IllegalArgumentException If the specified object is not a
	 *         {@link List}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	static Object get(final Object list, final int index) {
		requireNonNull(list);

		if (list instanceof List<?> l) {
			return l.get(index);
		} else {
			throw noListValue(list);
		}
	}

	/**
	 * Sets the value of the indexed component for the specified list
	 * object to the specified new value.
	 *
	 * @param list the list
	 * @param index the index
	 * @param value the new value of the indexed component
	 * @throws NullPointerException if the specified object argument is
	 *         {@code null}
	 * @throws IllegalArgumentException if the specified object argument is not
	 *         a list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	static void set(final Object list, final int index, final Object value) {
		requireNonNull(list);

		if (list instanceof List<?> l) {
            ((List)l).set(index, value);
		} else {
			throw noListValue(list);
		}
	}

}
