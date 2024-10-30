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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Formatter class for formatting objects of a given type to a string.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Formatter {

	private static final Map<Class<?>, Function<?, String>> DEFAULT_CONVERTERS =
		Map.of(
			Object.class, Objects::toString
		);

	/**
	 * The default converter with the default formatter functions.
	 */
	public static final Formatter DEFAULT = new Formatter(DEFAULT_CONVERTERS);

	private final Map<Class<?>, ? extends Function<?, String>> formatters;

	private Formatter(final Map<Class<?>, ? extends Function<?, String>> formatters) {
		this.formatters = Map.copyOf(formatters);
	}

	/**
	 * Return the set of the supported conversion types.
	 *
	 * @return the set of the supported conversion types
	 */
	public Set<Class<?>> supportedTypes() {
		return formatters.keySet();
	}

	/**
	 * Checks whether the given {@code type} has an explicit formatter defined.
	 *
	 * @param type the formatting source type to check
	 * @return {@code true} if there is an explicit formater for the give
	 *         {@code type}, {@code false} otherwise
	 */
	public boolean isSupported(final Class<?> type) {
		requireNonNull(type);
		return formatters.containsKey(type);
	}

	/**
	 * Formats the given {@code value} to a string. If the given input
	 * {@code value} is {@code null}, the formatter method also returns
	 * {@code null}. If no formatter is defined for the given input type, the
	 * {@link Object#toString()} value is returned.
	 *
	 * @param value the string value to convert
	 * @return the formatted string value
	 * @param <T> the source type
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the registered converter function.
	 */
	@SuppressWarnings("unchecked")
	public <T> String format(final T value) {
		if (value == null) {
			return null;
		}

		var formatter = (Function<T, String>)formatters.get(value.getClass());
		if (formatter == null) {
			formatter = (Function<T, String>)formatters.get(Object.class);
			if (formatter == null) {
				formatter = Object::toString;
			}
		}

		return formatter.apply(value);
	}

	/**
	 * Return a {@code Converter} builder with the currently defined formatter
	 * functions. The returned builder lets override formatters.
	 *
	 * @return a builder with the currently defined formatter functions
	 */
	public Builder toBuilder() {
		return new Builder(formatters);
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
	 * The formatter builder class.
	 */
	public static final class Builder {
		private final Map<Class<?>, Function<?, String>> formatters = new HashMap<>();

		private Builder(final Map<Class<?>, ? extends Function<?, String>> formatters) {
			this.formatters.putAll(formatters);
		}

		/**
		 * Registers a formatter function for a given type.
		 *
		 * @param type the target type
		 * @param formatter the formatter function
		 * @return {@code this} builder for method chaining
		 * @param <T> the target type
		 */
		public <T> Builder
		add(final Class<T> type, final Function<? super T, String> formatter) {
			formatters.put(requireNonNull(type), requireNonNull(formatter));
			return this;
		}

		public Formatter build() {
			return new Formatter(formatters);
		}
	}

}
