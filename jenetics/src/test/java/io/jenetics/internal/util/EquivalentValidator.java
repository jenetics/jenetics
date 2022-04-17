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
package io.jenetics.internal.util;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.testng.Assert;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public interface EquivalentValidator<A, B> {

	Function<A, B> from();

	BiFunction<A, B, A> to();

	default void verify(final A value) {
		final B object = from().apply(value);
		final A reconstructed = to().apply(value, object);
		assertThat(reconstructed).isEqualTo(value);
	}

	public static <A, B> EquivalentValidator<A, B> of(
		final Function<A, B> from,
		final BiFunction<A, B, A> to
	) {
		requireNonNull(from);
		requireNonNull(to);

		return new EquivalentValidator<A, B>() {
			@Override
			public Function<A, B> from() {
				return from;
			}

			@Override
			public BiFunction<A, B, A> to() {
				return to;
			}
		};
	}

}
