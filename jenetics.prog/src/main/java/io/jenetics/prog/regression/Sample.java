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

/**
 * Represents a sample point used for the symbolic regression task. It consists
 * of an argument array and a result value. The sample point is comparable
 * according its {@link #result()} value.
 *
 * @implSpec
 * The dimensionality of the sample point must be at least one, which means
 * {@code arity() >= 1}.
 *
 * @param <T> the sample type
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public interface Sample<T> {

	/**
	 * Return the dimensionality of the sample point arguments.
	 *
	 * @return the arity of the sample point
	 */
	int arity();

	/**
	 * Return the argument value with the given {@code index}.
	 *
	 * @see #arity()
	 *
	 * @param index the argument index
	 * @return the argument value with the given {@code index}
	 * @throws ArrayIndexOutOfBoundsException if the given {@code index} is not
	 *         within the given range {@code [0, arity)}
	 */
	T argAt(final int index);

	/**
	 * Return the result of the sample point.
	 *
	 * @return the result of the sample point
	 */
	T result();


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new sample point from the given argument and sample result. It
	 * represents the function arguments with the function value:
	 * {@code f: sample[0:sample.length-1] -> sample[sample.length-1]}. The last
	 * array element contains the result, and the first n-1 elements are function
	 * arguments.
	 *
	 * @param <T> the sample type
	 * @param sample the sample point result
	 * @return a new sample point
	 * @throws IllegalArgumentException if the argument array is empty
	 * @throws NullPointerException if the argument array is {@code null}
	 */
	static <T> Sample<T> of(final T[] sample) {
		return new ObjectSample<>(sample.clone());
	}

	/**
	 * Create a new sample point from the given argument and sample result. It
	 * represents the function arguments with the function value:
	 * {@code f: x -> y}
	 *
	 * @param x the argument
	 * @param y the sample point result
	 * @return a new sample point
	 */
	static Sample<Double> ofDouble(final double x, final double y) {
		return new DoubleSample(x, y);
	}

	/**
	 * Create a new sample point from the given argument and sample result. It
	 * represents the function arguments with the function value:
	 * {@code f: (x1, x2) -> y}
	 *
	 * @param x1 the first argument
	 * @param x2 the second argument
	 * @param y the sample point result
	 * @return a new sample point
	 */
	static Sample<Double> ofDouble(
		final double x1,
		final double x2,
		final double y
	) {
		return new DoubleSample(x1, x2, y);
	}

	/**
	 * Create a new sample point from the given argument and sample result. It
	 * represents the function arguments with the function value:
	 * {@code f: (x1, x2, x3) -> y}
	 *
	 * @param x1 the first argument
	 * @param x2 the second argument
	 * @param x3 the second argument
	 * @param y the sample point result
	 * @return a new sample point
	 */
	static Sample<Double> ofDouble(
		final double x1,
		final double x2,
		final double x3,
		final double y
	) {
		return new DoubleSample(x1, x2, x3, y);
	}

}
