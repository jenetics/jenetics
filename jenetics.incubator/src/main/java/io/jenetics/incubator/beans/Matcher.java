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
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Matcher<T> {

    boolean matches(final T value);

    static Matcher<String> ofRegex(final String pattern) {
        final var regex = Pattern.compile(pattern);

        return string -> regex.matcher(string).matches();
    }

    static Matcher<String> ofGlob(final String pattern) {
        final var regex = Pattern.compile(
            "^" +
                Pattern.quote(pattern)
                    .replace("*", "\\E.*\\Q")
                    .replace("?", "\\E.\\Q") +
                "$"
        );

        return string -> regex.matcher(string).matches();
    }

    static <A, B> Matcher<B> matching(
        final Function<? super B, ? extends A> extractor,
        final Matcher<? super A> matcher
    ) {
        return value -> matcher.matches(extractor.apply(value));
    }

    static <A, B, C> Matcher<C> matching(
        final Function<? super C, ? extends B> extractor1,
        final Function<? super B, ? extends A> extractor2,
        final Matcher<? super A> matcher
    ) {
        return value -> matcher.matches(
            extractor2
                .compose(extractor1)
                .apply(value)
        );
    }

    static <A, B, C, D> Matcher<D> matching(
        final Function<? super D, ? extends C> extractor1,
        final Function<? super C, ? extends B> extractor2,
        final Function<? super B, ? extends A> extractor3,
        final Matcher<? super A> matcher
    ) {
        return value -> matcher.matches(
            extractor3
                .compose(extractor2)
                .compose(extractor1)
                .apply(value)
        );
    }

    static <A, B, C, D, E> Matcher<E> matching(
        final Function<? super E, ? extends D> extractor1,
        final Function<? super D, ? extends C> extractor2,
        final Function<? super C, ? extends B> extractor3,
        final Function<? super B, ? extends A> extractor4,
        final Matcher<? super A> matcher
    ) {
        return value -> matcher.matches(
            extractor4
                .compose(extractor3)
                .compose(extractor2)
                .compose(extractor1)
                .apply(value)
        );
    }

}
