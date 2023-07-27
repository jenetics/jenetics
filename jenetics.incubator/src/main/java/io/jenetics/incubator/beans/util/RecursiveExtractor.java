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
package io.jenetics.incubator.beans.util;

import static java.util.Objects.requireNonNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Implementation of a <em>recursive</em> extractor. The recursive extractor
 * can be used with an arbitrary <em>flat</em> extractor.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class RecursiveExtractor<S, T> implements Extractor<S, T> {


	private final Extractor<S, T> extractor;
	private final Function<T, S> mapper;

	/**
	 * Create a new <em>recursive</em> extractor from the given parameters.
	 *
	 * @param extractor the <em>direct</em> extractor
	 * @param mapper mapper function from the target type back to the source type
	 */
	@SuppressWarnings("unchecked")
	public RecursiveExtractor(
		final Extractor<? super S, ? extends T> extractor,
		final Function<? super T, ? extends S> mapper
	) {
		this.extractor = (Extractor<S, T>)requireNonNull(extractor);
		this.mapper = (Function<T, S>)requireNonNull(mapper);
	}

	@Override
	public Stream<T> extract(final S source) {
		final Map<Object, Object> visited = new IdentityHashMap<>();
		return stream(source, visited);
	}

	private Stream<T> stream(
		final S object,
		final Map<Object, Object> visited
	) {
		if (object == null) {
			return Stream.empty();
		}

		final boolean exists = visited.containsKey(object);
		if (!exists) {
			visited.put(object, "");
		}

		return !exists
			? new PreOrderIterator<>(object, extractor, mapper).stream()
			: Stream.empty();
	}
}
