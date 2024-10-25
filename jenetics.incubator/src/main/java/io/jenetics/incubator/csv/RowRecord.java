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

/**
 * Combines the column values with the convert used for converting the strings
 * into concrete data types.
 *
 * @param columns the underlying column values of the row
 * @param converter the used column value converter
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record RowRecord(String[] columns, Converter converter) {

	public RowRecord {
		requireNonNull(columns);
		requireNonNull(converter);
	}

	/**
	 * Wraps the given column values with the {@link Converter#DEFAULT} converter.
	 *
	 * @param columns the row values
	 */
	public RowRecord(String[] columns) {
		this(columns, Converter.DEFAULT);
	}

	/**
	 * Return the of row values.
	 *
	 * @return the number of row values
	 */
	public int size() {
		return columns.length;
	}

	/**
	 * Checks if the value at the given {@code index} is empty.
	 *
	 * @param index the column index
	 * @return {@code true} if the value at the given {@code index} is empty,
	 *         {@code false} otherwise
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public boolean isEmptyAt(final int index) {
		return columns[index] == null || columns[index].isEmpty();
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public String stringAt(final int index) {
		return columns[index];
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public byte byteAt(final int index, final byte defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Byte.class));
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public short shortAt(final int index, final short defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Short.class));
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public int intAt(final int index, final int defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Integer.class));
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public long longAt(final int index, final long defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Long.class));
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public float floatAt(final int index, final float defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Float.class));
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	public double doubleAt(final int index, final double defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Double.class));
	}

	/**
	 * Return the value at the given {@code index} and tries to convert it into
	 * the given {@code type}.
	 *
	 * @param index the row {@code index} of the value
	 * @param type the target type
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @param <T> the target type
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 * @throws UnsupportedOperationException if the conversion target {@code type}
	 *         is not supported
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the registered converter function.
	 */
	public <T> T objectAt(final int index, final Class<T> type) {
		requireNonNull(type);
		if (isEmptyAt(index)) {
			return null;
		}

		return converter.convert(columns[index], type);
	}

}
