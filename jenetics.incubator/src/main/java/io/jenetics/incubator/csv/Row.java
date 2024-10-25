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

/**
 * Abstracts the typed access to the values of a CSV row.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Row {

	/**
	 * Return the of row values.
	 *
	 * @return the number of row values
	 */
	int size();

	/**
	 * Checks if the value at the given {@code index} is empty.
	 *
	 * @param index the column index
	 * @return {@code true} if the value at the given {@code index} is empty,
	 *         {@code false} otherwise
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	boolean isEmptyAt(int index);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	String stringAt(int index);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	byte byteAt(int index, byte defaultValue);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	short shortAt(int index, short defaultValue);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	int intAt(int index, int defaultValue);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	long longAt(int index, long defaultValue);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	float floatAt(int index, float defaultValue);

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	double doubleAt(int index, double defaultValue);

	/**
	 * Return the value at the given {@code index} and tries to convert it into
	 * the given {@code type}.
	 *
	 * @param index the row {@code index} of the value
	 * @param type the target type
	 * @param <T> the target type
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 * @throws UnsupportedOperationException if the conversion target
	 *         {@code type} is not supported
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the registered converter function.
	 */
	<T> T objectAt(int index, Class<T> type);

}
