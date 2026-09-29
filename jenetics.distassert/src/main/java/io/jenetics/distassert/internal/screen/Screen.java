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

import static io.jenetics.distassert.internal.screen.DrawChars.BLOCK_CHARS;
import static io.jenetics.distassert.internal.screen.DrawChars.FULL_BLOCK;
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_STROKE_CHARS;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_STROKE_CHARS;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * A mutable character-cell drawing surface. Its origin is at the upper-left
 * corner, with the x-axis pointing right and the y-axis pointing down. Drawing
 * outside the screen is silently clipped.
 * <p>
 * Line strokes are composed according to their directions. Drawing crossing
 * horizontal and vertical strokes therefore creates the corresponding junction
 * character. Direct character writes and filled primitives use painter order
 * and replace previously drawn content.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public class Screen {
	/**
	 * The stroke weight used for drawing line primitives.
	 */
	public enum Stroke {
		LIGHT,
		HEAVY
	}

	private static final int UP = 1;
	private static final int RIGHT = 1 << 1;
	private static final int DOWN = 1 << 2;
	private static final int LEFT = 1 << 3;

	private final int width;
	private final int height;

	private final char[][] buffer;
	private final byte[][] strokes;
	private final byte[][] strokeWeights;

	/**
	 * Create a screen with the given dimensions.
	 *
	 * @param width the screen width in character cells
	 * @param height the screen height in character cells
	 * @throws IllegalArgumentException if a dimension is smaller than one
	 */
	public Screen(final int width, final int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException(
				"Screen dimensions must be positive: %dx%d."
					.formatted(width, height)
			);
		}

		this.width = width;
		this.height = height;
		this.buffer = new char[height][width];
		this.strokes = new byte[height][width];
		this.strokeWeights = new byte[height][width];
		for (var row : buffer) {
			Arrays.fill(row, ' ');
		}
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	/**
	 * Set a screen cell to the given value. This operation replaces existing
	 * content, including composed line strokes.
	 *
	 * @param x the horizontal cell coordinate
	 * @param y the vertical cell coordinate
	 * @param value the character value
	 */
	public void set(final int x, final int y, final char value) {
		if (contains(x, y)) {
			buffer[y][x] = value;
			strokes[y][x] = 0;
			strokeWeights[y][x] = 0;
		}
	}

	private boolean contains(final int x, final int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private void stroke(
		final int x,
		final int y,
		final int directions,
		final Stroke weight
	) {
		if (contains(x, y)) {
			final int composed = strokes[y][x] | directions;
			final int composedWeight = Math.max(
				strokeWeights[y][x],
				weight.ordinal() + 1
			);
			strokes[y][x] = (byte)composed;
			strokeWeights[y][x] = (byte)composedWeight;
			buffer[y][x] = composedWeight == 1
				? LIGHT_STROKE_CHARS[composed]
				: HEAVY_STROKE_CHARS[composed];
		}
	}

	/**
	 * Print the screen content to the given output stream.
	 *
	 * @param out the output stream
	 * @throws NullPointerException if {@code out} is {@code null}
	 */
	public void print(final PrintStream out) {
		requireNonNull(out);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				out.print(buffer[y][x]);
			}
			out.println();
		}
	}

	/**
	 * Draw the outline of the given rectangle. Rectangle strokes are composed
	 * with strokes already present on the screen.
	 *
	 * @param rectangle the rectangle to draw
	 * @throws NullPointerException if {@code rectangle} is {@code null}
	 */
	public void draw(final Rectangle rectangle) {
		draw(rectangle, Stroke.HEAVY);
	}

	/**
	 * Draw the outline of the given rectangle with the requested stroke weight.
	 * Rectangle strokes are composed with strokes already present on the screen.
	 * If light and heavy strokes intersect, the resulting cell is rendered with
	 * heavy strokes.
	 *
	 * @param rectangle the rectangle to draw
	 * @param stroke the stroke weight
	 * @throws NullPointerException if an argument is {@code null}
	 */
	public void draw(final Rectangle rectangle, final Stroke stroke) {
		requireNonNull(rectangle);
		requireNonNull(stroke);
		final int ox = rectangle.x();
		final int oy = rectangle.y();
		final int right = ox + rectangle.width() - 1;
		final int bottom = oy + rectangle.height() - 1;

		for (int x = ox + 1; x < right; ++x) {
			stroke(x, oy, LEFT | RIGHT, stroke);
			stroke(x, bottom, LEFT | RIGHT, stroke);
		}

		for (int y = oy + 1; y < bottom; ++y) {
			stroke(ox, y, UP | DOWN, stroke);
			stroke(right, y, UP | DOWN, stroke);
		}

		stroke(ox, oy, RIGHT | DOWN, stroke);
		stroke(right, oy, DOWN | LEFT, stroke);
		stroke(ox, bottom, UP | RIGHT, stroke);
		stroke(right, bottom, UP | LEFT, stroke);
	}

	/**
	 * Draw the given filled bar. A bar uses painter order and replaces existing
	 * content. It includes its origin cell and grows towards decreasing y-values.
	 *
	 * @param bar the bar to draw
	 * @throws NullPointerException if {@code bar} is {@code null}
	 */
	public void draw(final Bar bar) {
		requireNonNull(bar);
		for (int x = 0; x < bar.width(); ++x) {
			for (int y = 0; y < bar.height(); ++y) {
				set(bar.x() + x, bar.y() - y, FULL_BLOCK);
			}
			if (bar.fraction() > 0) {
				set(
					bar.x() + x,
					bar.y() - bar.height(),
					partialBlock(bar.fraction())
				);
			}
		}
	}

	private static char partialBlock(final double fraction) {
		final int level = Math.clamp((int)Math.round(fraction*8), 1, 7);
		return BLOCK_CHARS[level];
	}

	/**
	 * Draw the given text. Text uses painter order and replaces existing content.
	 *
	 * @param text the text to draw
	 * @throws NullPointerException if {@code text} is {@code null}
	 */
	public void draw(final Text text) {
		requireNonNull(text);
		for (int i = 0; i < text.value().length(); ++i) {
			set(text.x() + i, text.y(), text.value().charAt(i));
		}
	}

	public static void main() {
		final var screen = new Screen(80, 20);
		screen.draw(new Rectangle(2, 2, 76, 16));
		screen.draw(new Rectangle(15, 7, 30, 30));
		screen.draw(new Bar(30, 15, 10));
		screen.draw(new Bar(31, 15, 12));

		screen.print(System.out);
	}
}
