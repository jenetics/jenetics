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

	int width() {
		return width;
	}

	int height() {
		return height;
	}

	void set(int x, int y, char value) {
		buffer[x][y] = value;
	}

	void print(PrintStream out) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				out.print((char)buffer[x][y]);
			}
			out.println();
		}
	}

	void draw(final Rectangle rectangle) {
		// Horizontal lines.
		for (int i = rectangle.x(); i < rectangle.width(); ++i) {
			set(i, rectangle.y(), BoxDrawing.BOX_CHARS[1]);
			set(i, rectangle.height() - 1, BoxDrawing.BOX_CHARS[1]);
		}

		// Vertical lines.
		for (int i = rectangle.y(); i < rectangle.height(); ++i) {
			set(rectangle.x(), i, BoxDrawing.BOX_CHARS[3]);
			set(rectangle.width() - 1, i, BoxDrawing.BOX_CHARS[3]);
		}

		// Edges.
		set(rectangle.x(), rectangle.y(), BoxDrawing.BOX_CHARS[15]);
		set(rectangle.x(), rectangle.height() - 1, BoxDrawing.BOX_CHARS[23]);
		set(rectangle.width() - 1, rectangle.y(), BoxDrawing.BOX_CHARS[19]);
		set(rectangle.width() - 1, rectangle.height() - 1, BoxDrawing.BOX_CHARS[27]);
	}





	public static void main(String[] args) {
		final var screen = new Screen(80, 20);
		final var rectangle = new Rectangle(2, 2, 76, 16);
		screen.draw(rectangle);

		screen.print(System.out);
	}
}
