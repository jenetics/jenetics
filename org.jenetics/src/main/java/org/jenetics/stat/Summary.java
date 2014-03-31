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
package org.jenetics.stat;

import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

/**
 * Reporting interface for basic univariate statistics.
 *
 * [code]
 * final Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
 * final Summary<Integer> summary = numbers.collect(Summary.collector());
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-14 $</em>
 * @since @__version__@
 */
public interface Summary<N extends Number & Comparable<? super N>> {

	/**
	 * Return the number of accumulated/collected samples.
	 *
	 * @return the number of accumulated/collected samples.
	 */
	public long getSampleCount();

	/**
	 * Return the minimum value.
	 *
	 * @return the minimum value. Is {@code null} if the sample count is zero.
	 */
	public N getMin();

	/**
	 * Return the maximum value.
	 *
	 * @return the maximum value. Is {@code null} if the sample count is zero.
	 */
	public N getMax();

	/**
	 * Returns the sum of the values that have been added to <i>Univariate</i>.
	 * The sum is calculated using the
	 * <a href="http://en.wikipedia.org/wiki/Kahan_summation_algorithm">
	 * Kahan summation algorithm</a>.
	 *
	 * @return the sum of the values.
	 */
	public double getSum();

	/**
	 * Returns the arithmetic <a href="http://www.xycoon.com/arithmetic_mean.htm">
	 * mean</a> of the available values
	 *
	 * @return the mean or Double.NaN if no values have been added.
	 */
	public double getMean();

	/**
	 * Returns the (sample) variance of the accumulated values.
	 *
	 * <p>This method returns the bias-corrected sample variance (using
	 * {@code n - 1} in the denominator).
	 *
	 * @return the variance, Double.NaN if no values have been added or 0.0 for
	 *         a single accumulated value.
	 */
	public double getVariance();

	/**
	 * Returns the skewness of the available values. Skewness is a measure of
	 * the asymmetry of a given distribution.
	 *
	 * @return the skewness, Double.NaN if no values have been added or 0.0 for
	 *         a value set &lt;=2.
	 */
	public double getSkewness();

	/**
	 * Returns the Kurtosis of the available values. Kurtosis is a measure of
	 * the <i>peakedness</i> of a distribution
	 *
	 * @return the kurtosis, Double.NaN if no values have been added, or 0.0 for
	 *         a value set &lt;=3.
	 */
	public double getKurtosis();

	/**
	 * Return a {@link java.util.stream.Collector} which can be used to
	 * <i>collect</i> statistical summary information of a given number
	 * {@link java.util.stream.Stream}.
	 *
	 * [code]
	 * final Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	 * final Summary<Integer> summary = numbers.collect(Summary.collector());
	 * [/code]
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance">
	 *      Algorithms for calculating variance.</a>
	 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
	 *      Computing Higher-Order Moments Online</a>
	 *
	 * @param <N> the number type to collect.
	 * @return a statistical summary information collector.
	 */
	public static <N extends Number & Comparable<? super N>>
	Collector<N, ?, Summary<N>> collector() {
		return Collector.<N, CollectibleSummary<N>, Summary<N>>of(
			CollectibleSummary::new,
			CollectibleSummary::accumulate,
			CollectibleSummary::combine,
			s -> s,
			Characteristics.IDENTITY_FINISH
		);
	}

}

