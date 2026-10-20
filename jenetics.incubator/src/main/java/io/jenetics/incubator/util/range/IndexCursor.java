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
package io.jenetics.incubator.util.range;

import java.util.NoSuchElementException;

import io.jenetics.internal.util.Arrays;

final class IndexCursor {
	private final int[] sizes;
	private final int size;

	private int index1;
	private int index2 = -1;
	private int pos = -1;

	IndexCursor(final int... sizes) {
		this.sizes = sizes;
		this.size = Arrays.sum(sizes);
	}

	/**
	 * Return the size of the index curser. This is the number of valid indexes.
	 *
	 * @return the curser size
	 */
	int size() {
		return size;
	}

	/**
	 * Return the current first index value.
	 *
	 * @return the current first index value
	 * @throws NoSuchElementException if the cursor position is before the fist
	 *         index
	 */
	int index1() {
		if (pos < 0) {
			throw new NoSuchElementException("Invalid index position: " + pos);
		}
		return index1;
	}

	/**
	 * Return the current second index value.
	 *
	 * @return the current second index value
	 * @throws NoSuchElementException if the cursor position is before the fist
	 *         index
	 */
	int index2() {
		if (pos < 0) {
			throw new NoSuchElementException("Invalid index position: " + pos);
		}
		return index2;
	}

	/**
	 * Return the current index position. {@code -1} is returned if the curser
	 * is positioned before the first index.
	 *
	 * @return the current curser position
	 */
	int pos() {
		return pos;
	}

	/**
	 * Moves the index cursor position to the given value. If {@code pos < 0},
	 * the position is et before the first index.
	 *
	 * @param pos the new cursor position
	 * @throws IndexOutOfBoundsException if {@code pos >= size}
	 */
	void pos(final int pos) {
		if (pos > size) {
			throw new IndexOutOfBoundsException(
				"Cursor out of bounds: %s > %s.".formatted(pos, size)
			);
		}

		if (pos < 0) {
			index1 = 0;
			index2 = -1;
			this.pos = -1;
		} else {
			int r = pos;
			int i1 = 0;

			while (r >= sizes[i1]) {
				r -= sizes[i1++];
			}

			index1 = i1;
			index2 = r;
			this.pos = pos;
		}
	}

	/**
	 * Moves the index cursor to the next position. The index of the cursor is
	 * initially positioned before the first index.
	 *
	 * @return {@code true} if the new index is valid; {@code false} if there
	 *         are no more indexes
	 */
	boolean next() {
		if (size == 0 || pos == size) {
			return false;
		}

		++pos;
		int i1 = index1;
		int i2 = index2;

		if (++i2 < sizes[i1]) {
			index2 = i2;
			return true;
		}
		do {
			++i1;
		} while (i1 < sizes.length && sizes[i1] == 0);
		if (i1 < sizes.length) {
			index1 = i1;
			index2 = 0;
			return true;
		}

		return false;
	}

	/**
	 * Moves the index cursor to the previous position. The index of the cursor
	 * is initially positioned before the first index.
	 *
	 * @return {@code true} if the new index is valid; {@code false} if there
	 *         are no more indexes
	 */
	boolean prev() {
		if (size == 0 || pos <= 0) {
			return false;
		}

		--pos;
		int i1 = index1;
		int i2 = index2;

		if (--i2 >= 0) {
			index2 = i2;
			//--pos;
			return true;
		}
		do {
			--i1;
		} while (i1 >= 0 && sizes[i1] == 0);
		if (i1 >= 0) {
			index1 = i1;
			index2 = sizes[i1] - 1;
			//--pos;
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "[%d, %d]".formatted(index1, index2);
	}

}
