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
 * A rectangular outline. Width and height are measured in character cells and
 * include the border cells.
 *
 * @param x the left coordinate
 * @param y the top coordinate
 * @param width the width in character cells, at least two
 * @param height the height in character cells, at least two
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public record Rectangle(int x, int y, int width, int height) {
	public Rectangle {
		if (width < 2 || height < 2) {
			throw new IllegalArgumentException(
				"Rectangle dimensions must be at least two: %dx%d."
					.formatted(width, height)
			);
		}
	}

	public Rectangle(int width, int height) {
		this(0, 0, width, height);
	}
}
