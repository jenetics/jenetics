package io.jenetics.incubator.property;

import java.util.function.Predicate;
import java.util.stream.Stream;

@FunctionalInterface
public interface Extractor<S, T> {

	Stream<T> extract(final S source);

	default Extractor<S, T> filter(final Predicate<? super T> predicate) {
		return source -> extract(source).filter(predicate);
	}

}
