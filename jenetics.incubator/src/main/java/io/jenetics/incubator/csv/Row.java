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
import java.util.Map;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class Row {

	private static final Map<Class<?>, Function<String, ?>> CONVERTERS = Map.ofEntries(
		Map.entry(boolean.class, Boolean::parseBoolean),
		Map.entry(Boolean.class, Boolean::parseBoolean),
		Map.entry(byte.class, Byte::parseByte),
		Map.entry(Byte.class, Byte::parseByte),
		Map.entry(short.class, Short::parseShort),
		Map.entry(Short.class, Short::parseShort),
		Map.entry(int.class, Integer::parseInt),
		Map.entry(Integer.class, Integer::parseInt),
		Map.entry(long.class, Long::parseLong),
		Map.entry(Long.class, Long::parseLong),
		Map.entry(float.class, Float::parseFloat),
		Map.entry(Float.class, Float::parseFloat),
		Map.entry(double.class, Double::parseDouble),
		Map.entry(Double.class, Double::parseDouble),
		Map.entry(BigInteger.class, BigInteger::new),
		Map.entry(BigDecimal.class, BigDecimal::new),
		Map.entry(LocalDate.class, LocalDate::parse),
		Map.entry(LocalTime.class, LocalTime::parse),
		Map.entry(LocalDateTime.class, LocalDateTime::parse),
		Map.entry(OffsetTime.class, OffsetTime::parse),
		Map.entry(OffsetDateTime.class, OffsetDateTime::parse)
	);

	private final String[] columns;

	public Row(final String[] columns) {
		this.columns = requireNonNull(columns);
	}

	public boolean isEmpty(final int index) {
		return columns[index] == null || columns[index].isEmpty();
	}

	public String stringAt(final int index) {
		return columns[index];
	}

	public Byte byteAt(final int index) {
		return isEmpty(index) ? null : Byte.parseByte(columns[index]);
	}

	public byte byteAt(final int index, final byte defaultValue) {
		return isEmpty(index) ? defaultValue : Byte.parseByte(columns[index]);
	}

	public Short shortAt(final int index) {
		return isEmpty(index) ? null : Short.parseShort(columns[index]);
	}

	public short shortAt(final int index, final short defaultValue) {
		return isEmpty(index) ? defaultValue : Short.parseShort(columns[index]);
	}

	public Integer intAt(final int index) {
		return isEmpty(index) ? null : Integer.parseInt(columns[index]);
	}

	public int intAt(final int index, final int defaultValue) {
		return isEmpty(index) ? defaultValue : Integer.parseInt(columns[index]);
	}

	public Long longAt(final int index) {
		return isEmpty(index) ? null : Long.parseLong(columns[index]);
	}

	public long longAt(final int index, final long defaultValue) {
		return isEmpty(index) ? defaultValue : Long.parseLong(columns[index]);
	}

	public Float floatAt(final int index) {
		return isEmpty(index) ? null : Float.parseFloat(columns[index]);
	}

	public float floatAt(final int index, final float defaultValue) {
		return isEmpty(index) ? defaultValue : Float.parseFloat(columns[index]);
	}

	public Double doubleAt(final int index) {
		return isEmpty(index) ? null : Double.parseDouble(columns[index]);
	}

	public double doubleAt(final int index, final double defaultValue) {
		return isEmpty(index) ? defaultValue : Double.parseDouble(columns[index]);
	}

	public BigInteger bigIntegerAt(final int index) {
		return isEmpty(index) ? null : new BigInteger(columns[index]);
	}

	public BigDecimal bigDecimalAt(final int index) {
		return isEmpty(index) ? null : new BigDecimal(columns[index]);
	}

	public LocalDate localDateAt(final int index) {
		return isEmpty(index) ? null : LocalDate.parse(columns[index]);
	}

	public LocalTime localTimeAt(final int index) {
		return isEmpty(index) ? null : LocalTime.parse(columns[index]);
	}

	public LocalDateTime localDateTimeAt(final int index) {
		return isEmpty(index) ? null : LocalDateTime.parse(columns[index]);
	}

	public OffsetTime offsetTimeAt(final int index) {
		return isEmpty(index) ? null : OffsetTime.parse(columns[index]);
	}

	public OffsetDateTime offsetDateTimeAt(final int index) {
		return isEmpty(index) ? null : OffsetDateTime.parse(columns[index]);
	}

	public <T> T objectAt(final int index, final Class<T> type) {
		requireNonNull(type);
		if (isEmpty(index)) {
			return null;
		}

		final Function<String, ?> converter = CONVERTERS.get(type);
		if (converter == null) {
			throw new IllegalArgumentException(
				"Unsupported type '%s' at index %d."
					.formatted(type.getName(), index)
			);
		}

		final String value = stringAt(index);
		return type.cast(converter.apply(value));
	}

}
