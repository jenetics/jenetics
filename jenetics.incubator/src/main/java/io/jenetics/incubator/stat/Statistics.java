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
package io.jenetics.incubator.stat;

/**
 * Holds the statistical values as produced by the
 * {@link java.util.DoubleSummaryStatistics} class.
 *
 * @see java.util.DoubleSummaryStatistics
 *
 * @param count the number of samples
 * @param min the minimum sample value
 * @param max the maximum sample value
 * @param sum the sum of the sample values
 * @param mean the average of the sample values
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Statistics(
	long count,
	double min,
	double max,
	double sum,
	double mean
) {
	public static final Statistics EMPTY = new Statistics(0, 0, 0, 0, 0);
}
