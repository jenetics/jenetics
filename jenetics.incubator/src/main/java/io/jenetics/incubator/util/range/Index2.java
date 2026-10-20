package io.jenetics.incubator.util.range;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents a 2-dimensional index with the given sizes on the y-axis. The
 * length of the sizes array is also the valid size in x-axis.
 * Empty rows are skipped. Instances are immutable and always denote a valid
 * position; an empty grid cannot be indexed.
 */
public final class Index2 {

	private final int[] sizes;
	private final int size;

	private final int x;
	private final int y;

	/**
	 * Create an index at the given coordinates, copying the supplied row sizes.
	 *
	 * @param x the row coordinate
	 * @param y the coordinate within the row
	 * @param sizes the row sizes
	 * @throws IllegalArgumentException if a size is negative, the grid is empty,
	 *         or its total size exceeds {@link Integer#MAX_VALUE}
	 * @throws IndexOutOfBoundsException if the coordinates are outside the grid
	 * @throws NullPointerException if {@code sizes} is {@code null}
	 */
	public Index2(final int x, final int y, final int[] sizes) {
		this.sizes = sizes.clone();
		long total = 0;
		for (int length : this.sizes) {
			if (length < 0) {
				throw new IllegalArgumentException("Negative row size: " + length);
			}
			total += length;
		}
		if (total == 0 || total > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Invalid grid size: " + total);
		}
		Objects.checkIndex(x, this.sizes.length);
		Objects.checkIndex(y, this.sizes[x]);
		this.x = x;
		this.y = y;
		this.size = (int)total;
	}

	/**
	 * Create an index at the first position of the first non-empty row.
	 *
	 * @param sizes the row sizes
	 * @throws IllegalArgumentException if a size is negative, the grid is empty,
	 *         or its total size exceeds {@link Integer#MAX_VALUE}
	 * @throws NullPointerException if {@code sizes} is {@code null}
	 */
	public Index2(final int[] sizes) {
		this(firstRow(sizes), 0, sizes);
	}

	private static int firstRow(final int[] sizes) {
		int row = 0;
		while (row < sizes.length && sizes[row] == 0) {
			++row;
		}
		return row;
	}

	// Derived positions share the privately owned, validated row sizes.
	private Index2(final int x, final int y, final int[] sizes, final int size) {
		this.x = x;
		this.y = y;
		this.sizes = sizes;
		this.size = size;
	}

	/**
	 * Return the size of the index curser. This is the number of valid indexes.
	 *
	 * @return the curser size
	 */
	public int size() {
		return size;
	}

	/**
	 * Return the current first index value.
	 *
	 * @return the current first index value
	 */
	public int x() {
		return x;
	}

	/**
	 * Return the current second index value.
	 *
	 * @return the current second index value
	 */
	public int y() {
		return y;
	}

	/**
	 * Return the zero-based rank of the index in lexicographical order.
	 *
	 * @return the rank of the 2-dimensional index
	 */
	public int rank() {
		int rank = y;
		for (int row = 0; row < x; ++row) {
			rank += sizes[row];
		}
		return rank;
	}

	/**
	 * Return a new index object with the same row sizes and the given rank.
	 *
	 * @see #rank()
	 *
	 * @param rank the rank
	 * @return a new index with the given rank
	 * @throws IndexOutOfBoundsException if {@code rank < 0 || rank >= size()}
	 */
	public Index2 rank(final int rank) {
		Objects.checkIndex(rank, size);
		int row = 0;
		int column = rank;
		while (column >= sizes[row]) {
			column -= sizes[row++];
		}
		return new Index2(row, column, sizes, size);
	}

	/**
	 * Return the next index, in lexicographical order.
	 *
	 * @return the next index
	 * @throws NoSuchElementException if this is the last position
	 */
	public Index2 next() {
		if (y + 1 < sizes[x]) {
			return new Index2(x, y + 1, sizes, size);
		}
		int row = x + 1;
		while (row < sizes.length && sizes[row] == 0) {
			++row;
		}
		if (row == sizes.length) {
			throw new NoSuchElementException("Already at the last index.");
		}
		return new Index2(row, 0, sizes, size);
	}

	/**
	 * Return the previous index, in lexicographical order.
	 *
	 * @return the previous index
	 * @throws NoSuchElementException if this is the first position
	 */
	public Index2 prev() {
		if (y > 0) {
			return new Index2(x, y - 1, sizes, size);
		}
		int row = x - 1;
		while (row >= 0 && sizes[row] == 0) {
			--row;
		}
		if (row < 0) {
			throw new NoSuchElementException("Already at the first index.");
		}
		return new Index2(row, sizes[row] - 1, sizes, size);
	}

}
