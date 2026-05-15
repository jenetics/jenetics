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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

/**
 * Abstracts the typed access to the values of a CSV row.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
public interface Row {

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
	 * Return the number of columns
	 *
	 * @return the number of columns
	 */
	int size();

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
	default <T> T at(int index, Class<T> type) {
		switch (type) {
			case Integer.class _-> (T)Integer.valueOf(index);
			default: return null;
		}
	}

	/* *************************************************************************
	 * Default implementations of basic string conversions.
	 * ************************************************************************/

	/**
	 * Checks if the value at the given {@code index} is empty.
	 *
	 * @param index the column index
	 * @return {@code true} if the value at the given {@code index} is empty,
	 *         {@code false} otherwise
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	default boolean isEmptyAt(int index) {
		return stringAt(index) == null;
	}

	default boolean booleanAt(int index, boolean defaultValue) {
		final var value = at(index, Boolean.class);
		return value != null ? value : defaultValue;
	}

	default Boolean booleanAt(int index) {
		return at(index, Boolean.class);
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
	default byte byteAt(int index, byte defaultValue) {
		final var value = at(index, Byte.class);
		return value != null ? value : defaultValue;
	}

	default Byte byteAt(int index) {
		return at(index, Byte.class);
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
	default short shortAt(int index, short defaultValue) {
		final var value = at(index, Short.class);
		return value != null ? value : defaultValue;
	}

	default Short shortAt(int index) {
		return at(index, Short.class);
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
	default int intAt(int index, int defaultValue) {
		final var value = at(index, Integer.class);
		return value != null ? value : defaultValue;
	}

	default Integer intAt(int index) {
		return at(index, Integer.class);
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
	default long longAt(int index, long defaultValue) {
		final var value = at(index, Long.class);
		return value != null ? value : defaultValue;
	}

	default Long longAt(int index) {
		return at(index, Long.class);
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
	default float floatAt(int index, float defaultValue) {
		final var value = at(index, Float.class);
		return value != null ? value : defaultValue;
	}

	default Float floatAt(int index) {
		return at(index, Float.class);
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
	default double doubleAt(int index, double defaultValue) {
		final var value = stringAt(index);
		return value != null ? Double.parseDouble(value) : defaultValue;
	}

	default Double doubleAt(int index) {
		return at(index, Double.class);
	}

	default BigInteger bigIntegerAt(int index) {
		return at(index, BigInteger.class);
	}

	default BigDecimal bigDecimalAt(int index) {
		return at(index, BigDecimal.class);
	}

	default LocalTime localTimeAt(int index) {
		return at(index, LocalTime.class);
	}

	default LocalDate localDateAt(int index) {
		return at(index, LocalDate.class);
	}

	default LocalDateTime localDateTimeAt(int index) {
		return at(index, LocalDateTime.class);
	}

	default OffsetTime offsetTimeAt(int index) {
		return at(index, OffsetTime.class);
	}

	default OffsetDateTime offsetDateTimeAt(int index) {
		return at(index, OffsetDateTime.class);
	}

	/**
	 * Return a new {@code Row} object from the given {@code columns} and
	 * type {@code converter}.
	 *
	 * @param columns the columns of the row
	 * @param converter the type converter
	 * @return a new {@code Row} object
	 */
	static Row of(final String[] columns, final Converter converter) {
		return new ColumnsRow(columns, converter);
	}

}
