package io.jenetics.tool.measurement;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import io.jenetics.tool.measurement.Table.Row;

/**
 * Represents a two-dimensional collection of elements of type {@code T}.
 *
 * @param <T> the element type
 */
public interface Table<T> extends Iterable<Table.Row<T>> {

	/**
	 * Represents a table row.
	 *
	 * @param <T> the element type
	 */
	public static interface Row<T> extends Iterable<T> {
		public int size();
		public T get(final int index);

		@Override
		public default Iterator<T> iterator() {
			return new Iterator<T>() {
				private int _cursor = 0;
				private int _lastElement = -1;

				@Override
				public boolean hasNext() {
					return _cursor != size();
				}

				@Override
				public T next() {
					final int i = _cursor;
					if (_cursor >= size()) {
						throw new NoSuchElementException();
					}

					_cursor = i + 1;
					return get(_lastElement = i);
				}
			};
		}

		public default Object[] toArray() {
			final Object[] array = new Object[size()];
			for (int i = 0; i < array.length; ++i) {
				array[i] = get(i);
			}
			return array;
		}

		@SafeVarargs
		static <T> Row<T> of(final T... row) {
			requireNonNull(row);
			if (row.length == 0) {
				throw new IllegalArgumentException();
			}

			return new ArrayRow<>(row);
		}

	}

	/**
	 * Return the (optional) header line. If the returned list is empty, no
	 * header line has been defined.
	 *
	 * @return the header line, if available
	 */
	public List<String> header();

	public int size();

	public Row<T> get(final int index);

	@Override
	public default Iterator<Row<T>> iterator() {
		return new Iterator<Row<T>>() {
			private int _cursor = 0;
			private int _lastElement = -1;

			@Override
			public boolean hasNext() {
				return _cursor != size();
			}

			@Override
			public Row<T> next() {
				final int i = _cursor;
				if (_cursor >= size()) {
					throw new NoSuchElementException();
				}

				_cursor = i + 1;
				return get(_lastElement = i);
			}
		};
	}

	public void add(final Row<T> row);


//	public void write(final OutputStream out) throws IOException {
//		final OutputStreamWriter osw = new OutputStreamWriter(out);
//		final BufferedWriter writer = new BufferedWriter(osw);
//
//		writer.write(toString(_header));
//		writer.write("\n");
//
//		for (String[] row : _rows) {
//			writer.write(toString(asList(row)));
//			writer.write("\n");
//		}
//
//		writer.flush();
//	}
//
//	private static String toString(final List<String> row) {
//		return String.join(", ", row);
//	}
//
//	public static Table read(final InputStream in) throws IOException {
//		return null;
//	}
//
//
//	public static Table of(final String... header) {
//		return new Table(asList(header));
//	}

}

final class ArrayTable<T> implements Table<T> {

	private final List<String> _header;
	private final List<Row<T>> _rows = new ArrayList<>();

	ArrayTable(final List<String> header) {
		_header = unmodifiableList(new ArrayList<>(header));
	}

	@Override
	public List<String> header() {
		return _header;
	}

	@Override
	public int size() {
		return _rows.size();
	}

	@Override
	public Row<T> get(final int index) {
		return _rows.get(index);
	}

	@Override
	public void add(final Row<T> row) {
		_rows.add(new ArrayRow<>(row.toArray()));
	}
}

final class ArrayRow<T> implements Row<T> {
	private final Object[] _row;

	ArrayRow(final Object[] row) {
		_row = requireNonNull(row);
	}

	@Override
	public int size() {
		return _row.length;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T)_row[index];
	}

}
