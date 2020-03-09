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
package io.jenetics.prog.regression;

import static java.util.Objects.requireNonNull;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Sampling<T> {

	public static final class Result<T> {
		private final T[] _calculated;
		private final T[] _expected;

		private Result(final T[] calculated, final T[] expected) {
			_calculated = requireNonNull(calculated);
			_expected = requireNonNull(expected);
		}

		public T[] calculated() {
			return _calculated;
		}

		public T[] expected() {
			return _expected;
		}

		public static <T> Result<T> of(final T[] calculated, final T[] expected) {
			return new Result<>(calculated, expected);
		}

	}

	Result<T> eval(final Tree<? extends Op<T>, ?> program);

}
