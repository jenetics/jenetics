package io.jenetics.incubator.util.range;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * A list <em>view</em> over the given list of lists.
 *
 * @param lists the underlying lists
 * @param <T> the list element type
 */
public record CompositeList<T>(List<List<T>> lists) implements List<T> {

	public CompositeList {
		lists = lists.stream()
			.filter(not(List::isEmpty))
			.toList();
	}

	@Override
	public int size() {
		int size = 0;
		for (var list : lists) {
			size += list.size();
		}
		return size;
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
		return listIterator();
	}

	@Override
	public Object[] toArray() {
		final var result = new Object[size()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = get(i);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1> T1[] toArray(T1[] a) {
		final int size = size();
		final T1[] result = a.length >= size
			? a
			: Arrays.copyOf(a, size);

		for (int i = 0; i < size; ++i) {
			result[i] = (T1)get(i);
		}
		if (result.length > size) {
			result[size] = null;
		}

		return result;
	}

	@Override
	public boolean add(T t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return c.stream().allMatch(this::contains);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
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
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
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
			private int cursor = index;
			private int last = -1;

			@Override
			public boolean hasNext() {
				return cursor < size();
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				last = cursor;
				return get(cursor++);
			}

			@Override
			public boolean hasPrevious() {
				return cursor > 0;
			}

			@Override
			public T previous() {
				if (!hasPrevious()) {
					throw new NoSuchElementException();
				}
				last = --cursor;
				return get(cursor);
			}

			@Override
			public int nextIndex() {
				return cursor;
			}

			@Override
			public int previousIndex() {
				return cursor - 1;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(final T element) {
				if (last < 0) {
					throw new IllegalStateException();
				}
				CompositeList.this.set(last, element);
			}

			@Override
			public void add(final T element) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		Objects.checkFromToIndex(fromIndex, toIndex, size());
		return new ListRange<>(this, Range.INTEGER.dense(fromIndex, toIndex));
	}

	@Override
	public int hashCode() {
		int hash = 1;
		for (var element : this) {
			hash = 31*hash + (element == null ? 0 : element.hashCode());
		}
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof List<?>)) {
			return false;
		}

		final var other = (List<?>)obj;
		if (other.size() != size()) {
			return false;
		}

		final var left = iterator();
		final var right = other.iterator();
		while (left.hasNext()) {
			if (!Objects.equals(left.next(), right.next())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return stream()
			.map(Objects::toString)
			.collect(Collectors.joining(", ", "[", ""));
	}

}
