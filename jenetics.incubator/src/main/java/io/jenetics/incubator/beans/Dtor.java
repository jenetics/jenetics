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

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This functional interface deconstructs an object of type {@code S} to its
 * components of type {@code T}.
 *
 * @param <S> the type of the deconstructed object
 * @param <T> the type of the object's component type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Dtor<S, T> {

	/**
	 * Extracts the sub-elements from the given {@code source} object.
	 *
	 * @param source the source object
	 * @return the stream of deconstructed object components
	 */
	Stream<T> unapply(final S source);

	/**
	 * Create a new {@code Extractor} by filtering the source object. If the
	 * given {@code predicate} doesn't match, the created extractor will always
	 * return an empty {@code Stream}.
	 *
	 * @param predicate the source object {@code predicate}
	 * @return a new {@link Dtor} which filters on the source object
	 * @throws NullPointerException if the given {@code predicate} is {@code null}
	 */
	default Dtor<S, T> sourceFilter(final Predicate<? super S> predicate) {
		requireNonNull(predicate);
		return source -> predicate.test(source) ? unapply(source) : Stream.empty();
	}

	/**
	 * Create a new {@code Extractor} by filtering the sub-elements. If the
	 * given {@code predicate} doesn't match, the created extractor will filter
	 * these sub-elements
	 *
	 * @param predicate the sub-element {@code predicate}
	 * @return a new {@code Extractor} which filters on the sub-elements
	 * @throws NullPointerException if the given {@code predicate} is {@code null}
	 */
	default Dtor<S, T> targetFilter(final Predicate<? super T> predicate) {
		requireNonNull(predicate);
		return source -> unapply(source).filter(predicate);
	}

}
