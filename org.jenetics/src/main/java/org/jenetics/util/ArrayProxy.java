package org.jenetics.util;

import java.util.Iterator;
import java.util.List;

abstract class ArrayProxy<T, P extends ArrayProxy<T, P>> {

	abstract void set(final int index, final T value);

	abstract T get(final int index);

	abstract void uncheckedSet(final int index, final T value);

	abstract T uncheckedGet(final int index);

	abstract void cloneIfSealed();

	abstract P seal();

	abstract int length();

}

class AbstractSeq<T> implements Seq<T> {

	final ArrayProxy<T, ?> _proxy;

	AbstractSeq(final ArrayProxy<T, ?> proxy) {
		_proxy = proxy;
	}

	@Override
	public T get(final int index) {
		return _proxy.get(index);
	}

	@Override
	public int length() {
		return _proxy.length();
	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}


	@Override
	public <B> Iterator<B> iterator(Function<? super T, ? extends B> mapper) {
		return null;
	}

	@Override
	@Deprecated
	public <R> void foreach(Function<? super T, ? extends R> function) {
		forEach(function);
	}

	@Override
	public <R> void forEach(Function<? super T, ? extends R> function) {
	}

	@Override
	@Deprecated
	public boolean forall(Function<? super T, Boolean> predicate) {
		return forAll(predicate);
	}

	@Override
	public boolean forAll(Function<? super T, Boolean> predicate) {
		return false;
	}

	@Override
	public boolean contains(Object element) {
		return false;
	}

	@Override
	public int indexOf(Object element) {
		return 0;
	}

	@Override
	public int indexOf(Object element, int start) {
		return 0;
	}

	@Override
	public int indexOf(Object element, int start, int end) {
		return 0;
	}

	@Override
	public int indexWhere(Function<? super T, Boolean> predicate) {
		return 0;
	}

	@Override
	public int indexWhere(Function<? super T, Boolean> predicate, int start) {
		return 0;
	}

	@Override
	public int indexWhere(Function<? super T, Boolean> predicate, int start,
			int end) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object element) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object element, int end) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object element, int start, int end) {
		return 0;
	}

	@Override
	public int lastIndexWhere(Function<? super T, Boolean> predicate) {
		return 0;
	}

	@Override
	public int lastIndexWhere(Function<? super T, Boolean> predicate, int end) {
		return 0;
	}

	@Override
	public int lastIndexWhere(Function<? super T, Boolean> predicate,
			int start, int end) {
		return 0;
	}

	@Override
	public List<T> asList() {
		return null;
	}

	@Override
	public <B> Seq<B> map(Function<? super T, ? extends B> mapper) {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public T[] toArray(T[] array) {
		return null;
	}

	@Override
	public Seq<T> subSeq(int start) {
		return null;
	}

	@Override
	public Seq<T> subSeq(int start, int end) {
		return null;
	}

	@Override
	public String toString(String prefix, String separator, String suffix) {
		return null;
	}

	@Override
	public String toString(String separator) {
		return null;
	}

}