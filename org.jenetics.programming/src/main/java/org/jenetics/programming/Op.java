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
package org.jenetics.programming;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Op<T> extends Function<T[], T> {

	public int arity();

	public default String name() {
		return "Op";
	}

	public static <T> Op<T> of(final String name, final Function<T[], T> function, final int arity) {
		requireNonNull(function);
		if (arity < 0) {
			throw new IllegalArgumentException("Arity smaller than zero: " + arity);
		}

		return new Op<T>() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public T apply(final T[] value) {
				return function.apply(value);
			}

			@Override
			public int arity() {
				return arity;
			}

			@Override
			public String toString() {
				return name();
			}
		};
	}

	public static <T> Op<T> ofScalar(final Function<T, T> function) {
		return null;
	}

	public static final class Default<Object> implements Op<Object> {
		@Override
		public Object apply(final Object[] value) {
			return null;
		}
		@Override
		public int arity() {
			return 0;
		}
	}
}
