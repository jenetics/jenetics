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

import java.io.Serializable;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

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

	private final double[] _sample;

	/**
	 * Create a new sample point with the given argument array and result value.
	 *
	 * @param sample the arguments of the sample point
	 * @throws IllegalArgumentException if the argument array is empty
	 * @throws NullPointerException if the argument array is {@code null}
	 */
	private Sample(final double[] sample) {
		if (sample.length < 2) {
			throw new IllegalArgumentException(format(
				"Argument sample must contain at least two values: %s",
				sample.length
			));
		}

		_sample = requireNonNull(sample);
	}

	/**
	 * Return the dimensionality of the sample point arguments.
	 *
	 * @return the arity of the sample point
	 */
	public int arity() {
		return _sample.length - 1;
	}

	public double argAt(final int index) {
		if (index < 0 || index >= arity()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return _sample[index];
	}

	public double[] args() {
		return Arrays.copyOfRange(_sample, 0, _sample.length - 1);
	}

	/**
	 * Return the result of the sample point.
	 *
	 * @return the result of the sample point
	 */
	public double result() {
		return _sample[_sample.length - 1];
	}

	@Override
	public int compareTo(final Sample sample) {
		return Double.compare(result(), sample.result());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_sample);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Sample &&
			Arrays.equals(_sample, ((Sample)obj)._sample);
	}

	@Override
	public String toString() {
		return format("%s -> %f", Arrays.toString(args()), result());
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
	 * @param sample the sample point result
	 * @return a new sample point
	 * @throws IllegalArgumentException if the argument array is empty
	 * @throws NullPointerException if the argument array is {@code null}
	 */
	public static Sample of(final double[] sample) {
		return new Sample(sample.clone());
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
		return new Sample(new double[]{x, x});
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
		return new Sample(new double[]{x1, x2, y});
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
		return new Sample(new double[]{x1, x2, x3, y});
	}

}
