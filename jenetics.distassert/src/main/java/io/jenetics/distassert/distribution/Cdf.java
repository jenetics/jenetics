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
package io.jenetics.distassert.distribution;

import io.jenetics.distassert.observation.Interval;

/**
 * The cumulative distribution function.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Cumulative_distribution_function">
 *     Wikipedia: CDF</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
@FunctionalInterface
public interface Cdf {

	/**
	 * Calculates the cumulative distribution value for the given input.
	 *
	 * @param value the value to calculate the cumulative distribution value for
	 * @return the cumulative value
	 */
	double apply(double value);

	/**
	 * Return the probability of the CDF for the given {@code interval}.
	 *
	 * @param interval the interval
	 * @return the probability for the given {@code interval}
	 */
	default double probability(final Interval interval) {
		return apply(interval.max()) - apply(interval.min());
	}

}
