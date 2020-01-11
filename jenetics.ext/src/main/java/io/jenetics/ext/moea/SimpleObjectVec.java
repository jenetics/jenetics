package io.jenetics.ext.moea;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Comparator;

final class SimpleObjectVec<T> implements Vec<T[]> {
	private final T[] _data;
	private final Comparator<? super T> _comparator;
	private final ElementDistance<T[]> _distance;

	SimpleObjectVec(
		final T[] data,
		final Comparator<? super T> comparator,
		final ElementDistance<T[]> distance
	) {
		Vecs.checkVecLength(data.length);
		_data = data;
		_comparator = requireNonNull(comparator);
		_distance = requireNonNull(distance);
	}

	@Override
	public T[] data() {
		return _data;
	}

	@Override
	public int length() {
		return _data.length;
	}

	@Override
	public ElementComparator<T[]> comparator() {
		return (u, v, i) -> _comparator.compare(u[i], v[i]);
	}

	@Override
	public ElementDistance<T[]> distance() {
		return _distance;
	}

	@Override
	public Comparator<T[]> dominance() {
		return (u, v) -> Vec.dominance(u, v, _comparator);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_data);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof SimpleObjectVec &&
			Arrays.equals(((SimpleObjectVec)obj)._data, _data);
	}

	@Override
	public String toString() {
		return Arrays.toString(_data);
	}

}
