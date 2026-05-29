package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Components extends Iterable<Object> {

	/**
	 * Return the column at the given index.
	 *
	 * @param index the column index
	 * @return the column at the given index
	 */
	Object at(int index);

	/**
	 * Return the number of columns
	 *
	 * @return the number of columns
	 */
	int size();

	@Override
	default Iterator<Object> iterator() {
		return new Iterator<>() {
			private int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor != size();
			}

			@Override
			public Object next() {
				final int i = cursor;
				if (cursor >= size()) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				return at(i);
			}
		};
	}

	static Components of(final Object... components) {
		requireNonNull(components);

		return new Components() {
			@Override
			public Object at(int index) {
				return components[index];
			}

			@Override
			public int size() {
				return components.length;
			}
		};
	}

}
