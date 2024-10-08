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

import java.util.List;

import io.jenetics.ext.util.CsvSupport;

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

	/**
	 * Create a new sample point from the given argument and sample result. It
	 * represents the function arguments with the function value:
	 * {@code f: (x1, x2, x3, xn-1) -> y}
	 *
	 * @since 8.1
	 *
	 * @param values the sample data
	 * @return a new sample point
	 */
	static Sample<Double> ofDouble(final double... values) {
		return new DoubleSample(values);
	}

	/**
	 * Parses the given CSV string into a list of double sample points. The
	 * following example shows how to create a list of sample points from a CSV
	 * string.
	 * {@snippet lang=java:
	 * static final List<Sample<Double>> SAMPLES = Sample.parseDoubles("""
	 *      1.0, -8.0000
	 *      0.9, -6.2460
	 *      0.8, -4.7680
	 *      0.7, -3.5420
	 *      0.6, -2.5440
	 *      0.5, -1.7500
	 *      0.4, -1.1360
	 *      0.3, -0.6780
	 *      0.2, -0.3520
	 *      0.1, -0.1340
	 *      0.0,  0.0000
	 *      0.1,  0.0740
	 *      0.2,  0.1120
	 *      0.3,  0.1380
	 *      0.4,  0.1760
	 *      0.5,  0.2500
	 *      0.6,  0.3840
	 *      0.7,  0.6020
	 *      0.8,  0.9280
	 *      0.9,  1.3860
	 *      1.0,  2.0000
	 *      """
	 *  );
	 * }
	 *
	 * @since 8.1
	 *
	 * @param csv the CSV string to parse
	 * @return the parsed double samples
	 */
	static List<Sample<Double>> parseDoubles(final CharSequence csv) {
		return CsvSupport.parseDoubles(csv).stream()
			.map(Sample::ofDouble)
			.toList();
	}

}
