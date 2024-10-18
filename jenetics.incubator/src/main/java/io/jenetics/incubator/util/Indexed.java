/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Pairs a value with an index. The main usage will be iterating collections with
 * the advanced for-loop and also having its index available.
 *
 * @param index the index of the value within the collection
 * @param value the indexed value
 * @param <T> the value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public record Indexed<T>(int index, T value) {

	@SafeVarargs
	public static <T> Iterable<Indexed<T>> enumerate(final T... values) {
		return enumerate(Arrays.asList(values));
	}

	public static <T> Iterable<Indexed<T>>
	enumerate(final Iterable<? extends T> iterable) {
		requireNonNull(iterable);

		return () -> new Iterator<>() {
			private final Iterator<? extends T> iterator = iterable.iterator();
			private int nextIndex;

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Indexed<T> next() {
				return new Indexed<>(nextIndex++, iterator.next());
			}
		};
	}

	@SafeVarargs
	public static <T> Stream<Indexed<T>> stream(final T... values) {
		return stream(Arrays.asList(values));
	}

	public static <T> Stream<Indexed<T>>
	stream(final Iterable<? extends T> iterable) {
		final Iterable<Indexed<T>> indexed = enumerate(iterable);
		return StreamSupport.stream(indexed.spliterator(), false);
	}

}
