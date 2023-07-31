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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Filters {

	private Filters() {
	}

    public static Predicate<String> ofRegex(final String pattern) {
        final var regex = Pattern.compile(pattern);

        return string -> regex.matcher(string).matches();
    }

    public static Predicate<String> ofGlob(final String pattern) {
        final var regex = Pattern.compile(
            "^" +
                Pattern.quote(pattern)
                    .replace("*", "\\E.*\\Q")
                    .replace("?", "\\E.\\Q") +
                "$"
        );

        return string -> regex.matcher(string).matches();
    }

    public static <A, B> Predicate<B> filtering(
        final Function<? super B, ? extends A> extractor,
        final Predicate<? super A> filters
    ) {
        return value -> filters.test(extractor.apply(value));
    }

    public static <A, B, C> Predicate<C> filtering(
        final Function<? super C, ? extends B> extractor1,
        final Function<? super B, ? extends A> extractor2,
        final Predicate<? super A> filters
    ) {
        return value -> filters.test(
            extractor2
                .compose(extractor1)
                .apply(value)
        );
    }

    public static <A, B, C, D> Predicate<D> filtering(
        final Function<? super D, ? extends C> extractor1,
        final Function<? super C, ? extends B> extractor2,
        final Function<? super B, ? extends A> extractor3,
        final Predicate<? super A> filters
    ) {
        return value -> filters.test(
            extractor3
                .compose(extractor2)
                .compose(extractor1)
                .apply(value)
        );
    }

    public static <A, B, C, D, E> Predicate<E> filtering(
        final Function<? super E, ? extends D> extractor1,
        final Function<? super D, ? extends C> extractor2,
        final Function<? super C, ? extends B> extractor3,
        final Function<? super B, ? extends A> extractor4,
        final Predicate<? super A> filters
    ) {
        return value -> filters.test(
            extractor4
                .compose(extractor3)
                .compose(extractor2)
                .compose(extractor1)
                .apply(value)
        );
    }

}
