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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Interface for calculating object equality.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-06-30 $</em>
 */
public interface Equality<T> {

	/**
	 * Perform additional object equality tests.
	 *
	 * @param equality the object equality test
	 * @return the overall object equality.
	 */
	public boolean test(final Predicate<T> equality);

	/**
	 * Test if objects are from the same type.
	 *
	 * @return {@code true} if the tested object are from the same type,
	 *         {@code false} otherwise.
	 */
	public default boolean test() {
		return test(o -> true);
	}

	/**
	 * Create a new {@code Equality} object for testing object equality.
	 *
	 * @param self the {@code this} object to test; must not be {@code null}
	 * @param other the {@code other} object to test; maybe {@code null}
	 * @param <T> the object type
	 * @return the {@code Equality} object for equality testing
	 * @throws java.lang.NullPointerException if the {@code self} parameter is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Equality<T> of(final T self, final Object other) {
		Objects.requireNonNull(self);
		return self == other ?
			p -> true :
			(other == null || self.getClass() != other.getClass()) ?
				p -> false : p -> p.test((T)other);
	}

}
