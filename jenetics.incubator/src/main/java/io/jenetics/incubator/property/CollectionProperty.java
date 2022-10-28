package io.jenetics.incubator.property;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed interface CollectionProperty<T>
	extends ReadonlyProperty, Iterable<T>
	permits ArrayProperty, ListProperty, MapProperty, SetProperty
{

	Class<T> elementType();

	int size();

	T get(final int index);

	@Override
	default Iterator<T> iterator() {
		return new Iterator<>() {
			private int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor != size();
			}

			@Override
			public T next() {
				final int i = cursor;
				if (cursor >= size()) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				return get(i);
			}
		};
	}

	default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
