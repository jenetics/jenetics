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

import static java.lang.String.format;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a sample point used for the symbolic regression task. It consists
 * of an argument array and a result value. The sample point is comparable
 * according its {@link #result()} value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Sample implements Comparable<Sample>, Serializable {

	private static final long serialVersionUID = 1L;

	private final double[] _args;
	private final double _result;

	/**
	 * Create a new sample point with the given argument array and result value.
	 *
	 * @param args the arguments of the sample point
	 * @param result the result value
	 * @throws IllegalArgumentException if the argument array is empty
	 * @throws NullPointerException if the argument array is {@code null}
	 */
	private Sample(final double[] args, final double result) {
		if (args.length == 0) {
			throw new IllegalArgumentException("Argument array must not be zero.");
		}

		_args = args;
		_result = result;
	}

	/**
	 * Return the dimensionality of the sample point arguments.
	 *
	 * @return the arity of the sample point
	 */
	public int arity() {
		return _args.length;
	}

	/**
	 * Return the argument array.
	 *
	 * @implNote
	 * For performance reasons, the returned array is not copied. Changing the
	 * array will lead to unpredictable results.
	 *
	 * @return the argument array
	 */
	public double[] args() {
		return _args;
	}

	/**
	 * Return the result of the sample point.
	 *
	 * @return the result of the sample point
	 */
	public double result() {
		return _result;
	}

	@Override
	public int compareTo(final Sample sample) {
		return Double.compare(_result, sample._result);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_args) + 31*Double.hashCode(_result) + 37;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Sample &&
			Arrays.equals(_args, ((Sample)obj)._args) &&
			Double.compare(_result, ((Sample)obj)._result) == 0;
	}

	@Override
	public String toString() {
		return format("%s -> %f", Arrays.toString(_args), _result);
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new sample point from the given argument array and sample result.
	 * It represents the function arguments with the function value:
	 * {@code f: args -> result}
	 *
	 * @implNote
	 * For performance reasons, the returned array is not copied. Changing the
	 * argument array after creating the {@code Sample} point will lead to
	 * unpredictable results.
	 *
	 * @param args the arguments
	 * @param result the sample point result
	 * @return a new sample point
	 * @throws IllegalArgumentException if the argument array is empty
	 * @throws NullPointerException if the argument array is {@code null}
	 */
	public static Sample of(final double[] args, final double result) {
		return new Sample(args, result);
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
	public static Sample of(final double x, final double y) {
		return of(new double[]{x}, y);
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
	public static Sample of(final double x1, final double x2, final double y) {
		return of(new double[]{x1, x2}, y);
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
	public static Sample of(
		final double x1,
		final double x2,
		final double x3,
		final double y
	) {
		return of(new double[]{x1, x2, x3}, y);
	}

}
