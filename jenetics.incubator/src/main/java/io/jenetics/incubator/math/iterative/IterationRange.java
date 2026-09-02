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
package io.jenetics.incubator.math.iterative;

/**
 * The iteration range of an iterative algorithm.
 *
 * @param min the minimal iteration count, inclusively
 * @param max the maximal iteration count, inclusively
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record IterationRange(long min, long max) {

	/**
	 * Create a new iteration range.
	 *
	 * @param min the minimal iteration count, inclusively
	 * @param max the maximal iteration count, inclusively
	 * @throws IllegalArgumentException if {@code min <= 0} or {@code min > max}
	 */
	public IterationRange {
		if (min <= 0) {
			throw new IllegalArgumentException(
				"Minimum iteration count must be greater than zero: " + min
			);
		}
		if (min > max) {
			throw new IllegalArgumentException(
				"Maximum iteration count must be less than minimal: %s > %s"
					.formatted(min, max)
			);
		}
	}
}
