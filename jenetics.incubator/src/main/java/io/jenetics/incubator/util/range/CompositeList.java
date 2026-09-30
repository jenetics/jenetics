package io.jenetics.incubator.util.range;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return null;
	}

	@Override
	public boolean add(T t) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public T get(int index) {
		return null;
	}

	@Override
	public T set(int index, T element) {
		return null;
	}

	@Override
	public void add(int index, T element) {

	}

	@Override
	public T remove(int index) {
		return null;
	}

	@Override
	public int indexOf(Object o) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return null;
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return List.of();
	}
}
