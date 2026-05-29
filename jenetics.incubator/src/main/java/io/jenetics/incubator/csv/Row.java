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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Abstracts the typed access to the values of a CSV row.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
public interface Row extends Iterable<Object> {

	/**
	 * Return the column at the given index.
	 *
	 * @param index the column index
	 * @return the column at the given index
	 */
	Object at(int index);

	/**
	 * Return the number of columns
	 *
	 * @return the number of columns
	 */
	int size();

	@Override
	default Iterator<Object> iterator() {
		return new Iterator<>() {
			private int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor != size();
			}

			@Override
			public Object next() {
				final int i = cursor;
				if (cursor >= size()) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				return at(i);
			}
		};
	}


	/* *************************************************************************
	 * Default implementations of basic string conversions.
	 * ************************************************************************/

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
	 *         the exception thrown by the registered parser function.
	 */
	default <T> T at(int index, Class<T> type) {
		return StringFormats.DEFAULT.parse(stringAt(index), type);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default String stringAt(int index) {
		return StringFormats.DEFAULT.format(at(index));
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default boolean booleanAt(int index, boolean defaultValue) {
		final var value = at(index, Boolean.class);
		return value != null ? value : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Boolean booleanAt(int index) {
		return at(index, Boolean.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default byte byteAt(int index, byte defaultValue) {
		final var value = at(index, Byte.class);
		return value != null ? value : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Byte byteAt(int index) {
		return at(index, Byte.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default short shortAt(int index, short defaultValue) {
		final var value = at(index, Short.class);
		return value != null ? value : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Short shortAt(int index) {
		return at(index, Short.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default int intAt(int index, int defaultValue) {
		final var value = at(index, Integer.class);
		return value != null ? value : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Integer intAt(int index) {
		return at(index, Integer.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default long longAt(int index, long defaultValue) {
		final var value = at(index, Long.class);
		return value != null ? value : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Long longAt(int index) {
		return at(index, Long.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default float floatAt(int index, float defaultValue) {
		final var value = at(index, Float.class);
		return value != null ? value : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Float floatAt(int index) {
		return at(index, Float.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @param defaultValue the value returned if the value at the given
	 *         {@code index} is empty
	 * @return the value at the given {@code index}, or the {@code defaultValue}
	 *         if the value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default double doubleAt(int index, double defaultValue) {
		final var value = stringAt(index);
		return value != null ? Double.parseDouble(value) : defaultValue;
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default Double doubleAt(int index) {
		return at(index, Double.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default BigInteger bigIntegerAt(int index) {
		return at(index, BigInteger.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default BigDecimal bigDecimalAt(int index) {
		return at(index, BigDecimal.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default LocalTime localTimeAt(int index) {
		return at(index, LocalTime.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default LocalDate localDateAt(int index) {
		return at(index, LocalDate.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default LocalDateTime localDateTimeAt(int index) {
		return at(index, LocalDateTime.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default OffsetTime offsetTimeAt(int index) {
		return at(index, OffsetTime.class);
	}

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index the row {@code index} of the value
	 * @return the value at the given {@code index}, or {@code null} if the
	 *         value at the given {@code index} is empty
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default OffsetDateTime offsetDateTimeAt(int index) {
		return at(index, OffsetDateTime.class);
	}

	/**
	 * Return a new {@code Row} object from the given {@code columns} and
	 * string {@code parser}.
	 *
	 * @param columns the columns of the row
	 * @param format the string parser
	 * @return a new {@code Row} object
	 */
	static Row of(final String[] columns, final StringFormat format) {
		record Columns(String[] columns, StringFormat format) implements Row {
			Columns {
				requireNonNull(columns);
				requireNonNull(format);
			}
			@Override
			public Object at(int index) {
				return columns[index];
			}
			@Override
			public int size() {
				return columns.length;
			}
			@Override
			public <T> T at(int index, Class<T> type) {
				return format.parse(columns[index], type);
			}
			@Override
			public String stringAt(int index) {
				return columns[index];
			}
			@Override
			public int hashCode() {
				return Arrays.hashCode(columns);
			}
			@Override
			public boolean equals(final Object obj) {
				return obj instanceof Columns row &&
					Arrays.equals(columns, row.columns);
			}
			@Override
			public String toString() {
				return Arrays.toString(columns);
			}
		}

		return new Columns(columns, format);
	}

	/**
	 * Return a new {@code Row} object from the given {@code columns} and
	 * using the {@link StringFormats#DEFAULT} string parser.
	 *
	 * @param columns the columns of the row
	 * @return a new {@code Row} object
	 */
	static Row of(final String... columns) {
		return Row.of(columns, StringFormats.DEFAULT);
	}

	static Row of(final Object[] components, final StringFormat format) {
		record Components(Object[] components, StringFormat format) implements Row {
			Components {
				requireNonNull(components);
				requireNonNull(format);
			}

			@Override
			public Object at(int index) {
				return components[index];
			}
			@Override
			public String stringAt(int index) {
				return format.format(components[index]);
			}
			@Override
			public int size() {
				return components.length;
			}
			@Override
			public <T> T at(int index, Class<T> type) {
				return format.parse(stringAt(index), type);
			}
			@Override
			public int hashCode() {
				return Arrays.hashCode(components);
			}
			@Override
			public boolean equals(final Object obj) {
				return obj instanceof Components row &&
					Arrays.equals(components, row.components);
			}
			@Override
			public String toString() {
				return Arrays.toString(components);
			}
		}

		return new Components(components, format);
	}

	static Row of(final Object... components) {
		return Row.of(components, StringFormats.DEFAULT);
	}

}
