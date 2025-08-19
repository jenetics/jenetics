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
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_DOWN_AND_LEFT;
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_DOWN_AND_RIGHT;
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_HORIZONTAL;
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_UP_AND_LEFT;
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_UP_AND_RIGHT;
import static io.jenetics.distassert.internal.screen.DrawChars.HEAVY_VERTICAL;

import java.io.PrintStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Screen {
	private final int width;
	private final int height;

	private final int[][] buffer;

	public Screen(final int width, final int height) {
		this.width = width;
		this.height = height;
		this.buffer = new int[width][height];
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				buffer[x][y] = ' ';
			}
		}
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public void set(int x, int y, char value) {
		if (x < width && y < height && x >= 0 && y >= 0) {
			buffer[x][y] = value;
		}
	}

	public void print(PrintStream out) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				out.print((char)buffer[x][y]);
			}
			out.println();
		}
	}

	public void draw(final Rectangle rectangle) {
		final int ox = rectangle.x();
		final int oy = rectangle.y();

		// Horizontal lines.
		for (int i = 0; i < rectangle.width(); ++i) {
			set(i + ox, oy, HEAVY_HORIZONTAL);
			set(i + ox, rectangle.height() - 1 + oy, HEAVY_HORIZONTAL);
		}

		// Vertical lines.
		for (int i = 0; i < rectangle.height(); ++i) {
			set(ox, i + oy, HEAVY_VERTICAL);
			set(rectangle.width() - 1 + ox, i + oy, HEAVY_VERTICAL);
		}

		// Edges.
		set(ox, oy, HEAVY_DOWN_AND_RIGHT);
		set(ox, rectangle.height() - 1 + oy, HEAVY_UP_AND_RIGHT);
		set(rectangle.width() - 1 + ox, oy, HEAVY_DOWN_AND_LEFT);
		set(rectangle.width() - 1 + ox, rectangle.height() - 1 + oy, HEAVY_UP_AND_LEFT);
	}

	public void draw(final Bar bar) {
		for  (int y = 0; y < bar.height(); ++y) {
			set(bar.x(), bar.y() - y, BLOCK_CHARS[8]);
		}
	}





	public static void main(String[] args) {
		final var screen = new Screen(80, 20);
		screen.draw(new Rectangle(2, 2, 76, 16));
		screen.draw(new Rectangle(15, 7, 30, 30));
		screen.draw(new Bar(30, 15, 10));
		screen.draw(new Bar(31, 15, 12));

		screen.print(System.out);
	}
}
