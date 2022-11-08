package io.jenetics.incubator.property;

import java.util.Collection;
import java.util.stream.Stream;

enum PropertyFlattener implements Extractor<Property, Object> {

	INSTANCE;

	@Override
	@SuppressWarnings("unchecked")
	public Stream<Object> extract(final Property source) {
		return source.value() instanceof Collection<?> coll
			? (Stream<Object>)coll.stream()
			: Stream.empty();
	}

}
