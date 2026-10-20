package io.jenetics.incubator.util.range;

import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A list <em>view</em> over the given list of lists.
 *
 * @param <T> the list element type
 */
final class CompositeList<T> extends AbstractList<T> {

	private final List<? extends List<T>> lists;
	private final int[] sizes;

	CompositeList(final List<? extends List<T>> lists) {
		this.lists = requireNonNull(lists);
		this.sizes = lists.stream().mapToInt(List::size).toArray();
	}

	@Override
	public int size() {
		return lists.stream().mapToInt(List::size).sum();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return lists.stream().anyMatch(list -> list.contains(o));
	}

	@Override
	public Iterator<T> iterator() {
		return stream().iterator();
	}

	@Override
	public T get(int index) {
		Objects.checkIndex(index, size());

		int offset = index;
		for (var list : lists) {
			if (offset < list.size()) {
				return list.get(offset);
			}
			offset -= list.size();
		}
		throw new AssertionError("Unreachable");
	}

	@Override
	public T set(int index, T element) {
		Objects.checkIndex(index, size());

		int offset = index;
		for (var list : lists) {
			if (offset < list.size()) {
				return list.set(offset, element);
			}
			offset -= list.size();
		}
		throw new AssertionError("Unreachable");
	}

	@Override
	public int indexOf(Object o) {
		int offset = 0;
		for (var list : lists) {
			final int index = list.indexOf(o);
			if (index >= 0) {
				return offset + index;
			}
			offset += list.size();
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int offset = size();
		for (int i = lists.size(); --i >= 0;) {
			final var list = lists.get(i);
			offset -= list.size();
			final int index = list.lastIndexOf(o);
			if (index >= 0) {
				return offset + index;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException(
				"Index: %d, size: %d".formatted(index, size())
			);
		}

		return new ListIterator<>() {
			private final IndexCursor crs = new IndexCursor(sizes);
			{ crs.pos(index); }

			private int last1 = -1;
			private int last2 = -1;

			@Override
			public boolean hasNext() {
				return crs.pos() < crs.size();
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				last1 = crs.index1();
				last2 = crs.index2();
				crs.next();
				return lists.get(last1).get(last2);
			}

			@Override
			public boolean hasPrevious() {
				return crs.pos() > 0;
			}

			@Override
			public T previous() {
				if (!hasPrevious()) {
					throw new NoSuchElementException();
				}

				crs.prev();
				last1 = crs.index1();
				last2 = crs.index2();
				return lists.get(last1).get(last2);
			}

			@Override
			public int nextIndex() {
				return crs.pos();
			}

			@Override
			public int previousIndex() {
				return crs.pos() - 1;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(final T element) {
				if (last1 < 0) {
					throw new IllegalStateException();
				}
				lists.get(last1).set(last2, element);
			}

			@Override
			public void add(final T element) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public Stream<T> stream() {
		return lists.stream().flatMap(List::stream);
	}

}
