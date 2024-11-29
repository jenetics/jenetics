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

import io.jenetics.stat.Sampler;
import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
record RangedDistribution(Distribution distribution, DoubleRange range)
	implements Distribution
{
	@Override
	public Sampler sampler() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cdf cdf() {
		final var origin = distribution.cdf();

		return x -> {
			if (x < range.min()) {
				return 0;
			}
			if (x >= range.max()) {
				return 1;
			}

			return origin.apply(x);
		};
	}

	@Override
	public Pdf pdf() {
		return distribution.pdf();
	}
}
