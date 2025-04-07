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

import java.util.random.RandomGenerator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class StatSnippets {
	private StatSnippets() {
	}

	static final class HistogramSnippets {

		void creation() {
			// @start region="Histogram.builder"
			// The range of the observed values.
			final Interval interval = new Interval(-5, 5);

			// The value source.
			final var random = RandomGenerator.getDefault();

			// Building the histogram
			final Histogram observation = Histogram.Builder.of(interval, 20)
				.build(samples -> {
					for (int i = 0; i < 1_000_000; ++i) {
						samples.add(random.nextGaussian());
					}
				});
			// @end
		}

	}

}
