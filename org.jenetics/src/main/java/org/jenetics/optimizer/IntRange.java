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
package org.jenetics.optimizer;

import static java.lang.String.format;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IntRange {

	private final int _min;
	private final int _max;

	private IntRange(final int min, final int max) {
		if (min >= max) {
			throw new IllegalArgumentException(format(
				"Max is smaller than min: %d >= %d.", min, max
			));
		}
		_min = min;
		_max = max;
	}

	public int intValue(final double p) {
		require.probability(p);
		final double prob = Double.compare(p, 1.0) == 0 ? Math.nextDown(p) : p;

		return (int)(prob*(_max - _min) + _min);
	}

	static IntRange of(final int min, final int max) {
		return new IntRange(min, max);
	}

	static IntRange of(final int max) {
		return new IntRange(0, max);
	}

}
