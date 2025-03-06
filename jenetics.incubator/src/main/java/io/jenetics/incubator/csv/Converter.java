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
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Converter class for converting strings to an object of the given type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
public final class Converter {

	private static final Map<Class<?>, Function<String, ?>> DEFAULT_CONVERTERS =
		Map.ofEntries(
			Map.entry(String.class, Function.identity()),
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
			Map.entry(OffsetDateTime.class, OffsetDateTime::parse),
			Map.entry(Year.class, Year::parse),
			Map.entry(MonthDay.class, MonthDay::parse),
			Map.entry(URI.class, URI::create)
		);

	/**
	 * The default converter with the default converter functions.
	 */
	public static final Converter DEFAULT = new Converter(DEFAULT_CONVERTERS);

	private final Map<Class<?>, ? extends Function<? super String, ?>> converters;

	private Converter(
		final Map<
			Class<?>,
			? extends Function<? super String, ?>
		> converters
	) {
		this.converters = Map.copyOf(converters);
	}

	/**
	 * Return the set of the supported conversion types.
	 *
	 * @return the set of the supported conversion types
	 */
	public Set<Class<?>> supportedTypes() {
		return converters.keySet();
	}

	/**
	 * Checks whether the given {@code type} is supported for conversion.
	 *
	 * @param type the conversion target type to check
	 * @return {@code true} if a string can be converted to the give {@code type},
	 *         {@code false} otherwise
	 */
	public boolean isSupported(final Class<?> type) {
		requireNonNull(type);
		return converters.containsKey(type);
	}

	/**
	 * Convert the given string {@code value} to the desired {@code type}. If
	 * the given input {@code value} is {@code null}, the converter method
	 * also returns {@code null}.
	 *
	 * @param value the string value to convert
	 * @param type the target type
	 * @return the converted string value
	 * @param <T> the target type
	 * @throws UnsupportedOperationException if the conversion target {@code type}
	 *         is not supported
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the registered converter function.
	 */
	public <T> T convert(final String value, final Class<T> type) {
		requireNonNull(type);
		if (!isSupported(type)) {
			throw new UnsupportedOperationException(
				"Can't convert the '%s' to type '%s'."
					.formatted(value, type.getName())
			);
		}

		if (value == null || value.isEmpty()) {
			return null;
		}

		@SuppressWarnings("unchecked")
		final T result = (T)converters.get(type).apply(value);
		return result;
	}

	/**
	 * Return a {@code Converter} builder with the currently defined converter
	 * functions. The returned builder lets override converters.
	 *
	 * @return a builder with the currently defined converter functions
	 */
	public Builder toBuilder() {
		return new Builder(converters);
	}

	/**
	 * Return a new, empty builder.
	 *
	 * @return a new, empty builder
	 */
	public static Builder builder() {
		return new Builder(Map.of());
	}

	/**
	 * The converter builder class.
	 */
	public static final class Builder {
		private final Map<Class<?>, Function<? super String, ?>>
			converters = new HashMap<>();

		private Builder(
			final Map<
				Class<?>,
				? extends Function<? super String, ?>
			> converters
		) {
			this.converters.putAll(converters);
		}

		/**
		 * Registers a converter function for a given type.
		 *
		 * @param type the target type
		 * @param converter the converter function
		 * @return {@code this} builder for method chaining
		 * @param <T> the target type
		 */
		public <T> Builder
		add(Class<T> type, Function<? super String, ? extends T> converter) {
			requireNonNull(type);
			requireNonNull(converter);

			converters.put(type, converter);
			return this;
		}

		public Converter build() {
			return new Converter(converters);
		}
	}

}
