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
package io.jenetics.incubator.beans;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Helper methods for creating filter predicates.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Filters {

	private Filters() {
	}

	/**
	 * Creates a predicate that tests if the {@code pattern} matches a given
	 * input string.
	 *
	 * @see Pattern#asMatchPredicate()
	 *
	 * @param pattern the matching regular expression
	 * @return a matching predicate for the given pattern
	 */
    public static Predicate<String> ofRegex(final String pattern) {
        return Pattern.compile(pattern).asMatchPredicate();
    }

	/**
	 * Creates a predicate that tests if the {@code pattern} matches a given
	 * input string.
	 *
	 * @param pattern the matching regular expression
	 * @return a matching predicate for the given pattern
	 */
    public static Predicate<String> ofGlob(final String pattern) {
        return ofRegex(
			"^" +
				Pattern.quote(pattern)
					.replace("*", "\\E.*\\Q")
					.replace("?", "\\E.\\Q") +
				"$"
		);
    }

	/**
	 * Accepts a function that extracts the filter key from a type {@code B},
	 * and returns a {@code Predicate<B>} that tests the key using the specified
	 * {@code filter}.
	 *
	 * @param extractor the key extractor function
	 * @param filter the {@code Predicate} used for testing the key
	 * @return a predicate for testing a key
	 * @param <A> the type of the object to test with the given filter
	 * @param <B> the type of the object to test with the created filter
	 */
    public static <A, B> Predicate<B> filtering(
        final Function<? super B, ? extends A> extractor,
        final Predicate<? super A> filter
    ) {
		requireNonNull(extractor);
		requireNonNull(filter);

        return value -> filter.test(extractor.apply(value));
    }

	/**
	 * Accepts functions that extracts the filter key from a type {@code C},
	 * and returns a {@code Predicate<C>} that tests the key using the specified
	 * {@code filter}.
	 *
	 * @param extractor1 the first extractor function
	 * @param extractor2 the second extractor function
	 * @param filter the {@code Predicate} used for testing the key
	 * @return a predicate for testing a key
	 * @param <A> the type of the object to test with the given filter
	 * @param <B> the first intermediate type
	 * @param <C> the type of the object to test with the created filter
	 */
    public static <A, B, C> Predicate<C> filtering(
        final Function<? super C, ? extends B> extractor1,
        final Function<? super B, ? extends A> extractor2,
        final Predicate<? super A> filter
    ) {
		requireNonNull(extractor1);
		requireNonNull(extractor2);
		requireNonNull(filter);

        return value -> {
			final var v1 = extractor1.apply(value);
			final var v2 = v1 != null ? extractor2.apply(v1) : null;

			return filter.test(v2);
		};
    }

	/**
	 * Accepts functions that extracts the filter key from a type {@code D},
	 * and returns a {@code Predicate<D>} that tests the key using the specified
	 * {@code filter}.
	 *
	 * @param extractor1 the first extractor function
	 * @param extractor2 the second extractor function
	 * @param extractor3 the third extractor function
	 * @param filter the {@code Predicate} used for testing the key
	 * @return a predicate for testing a key
	 * @param <A> the type of the object to test with the given filter
	 * @param <B> the first intermediate type
	 * @param <C> the second intermediate type
	 * @param <D> the type of the object to test with the created filter
	 */
    public static <A, B, C, D> Predicate<D> filtering(
        final Function<? super D, ? extends C> extractor1,
        final Function<? super C, ? extends B> extractor2,
        final Function<? super B, ? extends A> extractor3,
        final Predicate<? super A> filter
    ) {
		requireNonNull(extractor1);
		requireNonNull(extractor2);
		requireNonNull(extractor3);
		requireNonNull(filter);

		return value -> {
			final var v1 = extractor1.apply(value);
			final var v2 = v1 != null ? extractor2.apply(v1) : null;
			final var v3 = v2 != null ? extractor3.apply(v2) : null;

			return filter.test(v3);
		};
    }

	/**
	 * Accepts functions that extracts the filter key from a type {@code E},
	 * and returns a {@code Predicate<E>} that tests the key using the specified
	 * {@code filter}.
	 *
	 * @param extractor1 the first extractor function
	 * @param extractor2 the second extractor function
	 * @param extractor3 the third extractor function
	 * @param extractor4 the forth extractor function
	 * @param filter the {@code Predicate} used for testing the key
	 * @return a predicate for testing a key
	 * @param <A> the type of the object to test with the given filter
	 * @param <B> the first intermediate type
	 * @param <C> the second intermediate type
	 * @param <E> the third intermediate type
	 * @param <D> the type of the object to test with the created filter
	 */
    public static <A, B, C, D, E> Predicate<E> filtering(
        final Function<? super E, ? extends D> extractor1,
        final Function<? super D, ? extends C> extractor2,
        final Function<? super C, ? extends B> extractor3,
        final Function<? super B, ? extends A> extractor4,
        final Predicate<? super A> filter
    ) {
		requireNonNull(extractor1);
		requireNonNull(extractor2);
		requireNonNull(extractor3);
		requireNonNull(extractor4);
		requireNonNull(filter);

		return value -> {
			final var v1 = extractor1.apply(value);
			final var v2 = v1 != null ? extractor2.apply(v1) : null;
			final var v3 = v2 != null ? extractor3.apply(v2) : null;
			final var v4 = v3 != null ? extractor4.apply(v3) : null;

			return filter.test(v4);
		};
    }

}
