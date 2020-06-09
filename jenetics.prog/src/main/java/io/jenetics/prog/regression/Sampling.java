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
 * This interface represents a set of sample points, which can be evaluated with
 * a given evolved <em>program</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
@FunctionalInterface
public interface Sampling<T> {

	/**
	 * This class represents the result of a sample calculation, which contains
	 * the array of calculated values and a corresponding array with expected
	 * sample values. This two arrays can then be used for calculating the
	 * error between modeled regression function and actual sample values.
	 *
	 * @param <T> the sample result  type
	 */
	final class Result<T> {
		private final T[] _calculated;
		private final T[] _expected;

		private Result(final T[] calculated, final T[] expected) {
			_calculated = requireNonNull(calculated);
			_expected = requireNonNull(expected);
		}

		/**
		 * Return the the calculated result values.
		 *
		 * @return the the calculated result values
		 */
		public T[] calculated() {
			return _calculated;
		}

		/**
		 * Return the expected sample result values.
		 *
		 * @return the expected sample result values
		 */
		public T[] expected() {
			return _expected;
		}

		/**
		 * Create a new sampling result object.
		 *
		 * @param calculated the calculated values
		 * @param expected the expected sample values
		 * @param <T> the sample type
		 * @return a new sampling result object
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <T> Result<T> of(final T[] calculated, final T[] expected) {
			return new Result<>(calculated.clone(), expected.clone());
		}
	}

	/**
	 * Evaluates the given {@code program} tree with its sample points. The
	 * returned result object may be {@code null} if no sample point has been
	 * added to the <em>sampling</em> when calling the {@code eval} method.
	 *
	 * @param program the program to evaluate
	 * @return the evaluated sample result. May be {@code null} if the sampling
	 *         is empty and contains no sample points.
	 */
	Result<T> eval(final Tree<? extends Op<T>, ?> program);

}
