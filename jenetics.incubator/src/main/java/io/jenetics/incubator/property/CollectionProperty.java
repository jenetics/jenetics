package io.jenetics.incubator.property;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed interface CollectionProperty
	extends Iterable<Object>, Property
	permits ArrayProperty, ListProperty, MapProperty, SetProperty
{

	int size();

	Object get(final int index);

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
				return get(i);
			}
		};
	}

	default Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
