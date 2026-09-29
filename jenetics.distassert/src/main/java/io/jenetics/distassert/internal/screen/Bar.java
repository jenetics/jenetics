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
package io.jenetics.distassert.internal.screen;

/**
 * A vertical filled bar. The coordinate {@code (x, y)} denotes its inclusive
 * bottom cell and the bar grows towards decreasing y-values.
 *
 * @param x the horizontal coordinate
 * @param y the inclusive bottom coordinate
 * @param width the bar width in character cells
 * @param height the number of completely filled character rows
 * @param fraction the filled fraction of the additional top row, in the range
 *        {@code [0, 1)}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public record Bar(int x, int y, int width, int height, double fraction) {
	public Bar {
		if (width < 1 || height < 0 ||
			!Double.isFinite(fraction) || fraction < 0 || fraction >= 1)
		{
			throw new IllegalArgumentException(
				"Invalid bar dimensions: %dx%d, fraction %s."
					.formatted(width, height, fraction)
			);
		}
	}

	public Bar(final int x, final int y, final int width, final int height) {
		this(x, y, width, height, 0);
	}

	public Bar(final int x, final int y, final int height) {
		this(x, y, 1, height, 0);
	}
}
