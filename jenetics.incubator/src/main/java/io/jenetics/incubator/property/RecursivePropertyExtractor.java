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
package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import io.jenetics.incubator.property.Property.Path;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class RecursivePropertyExtractor implements Extractor<PathObject, Property> {

	private final Extractor<? super PathObject, ? extends Property> properties;
	private final Extractor<? super Property, ?> flattener;

	RecursivePropertyExtractor(
		final Extractor<PathObject, Property> properties,
		final Extractor<? super Property, ?> flattener
	) {
		this.properties = requireNonNull(properties);
		this.flattener = requireNonNull(flattener);
	}

	RecursivePropertyExtractor(
		final Extractor<PathObject, Property> properties
	) {
		this(properties, RecursivePropertyExtractor::flatten);
	}

	RecursivePropertyExtractor() {
		this(PropertyExtractor.DEFAULT);
	}

	private static Stream<Object> flatten(final Property property) {
		return property instanceof CollectionProperty coll
			? coll.stream()
			: Stream.empty();
	}

	@Override
	public Stream<Property> extract(final PathObject source) {
		final Map<Object, Object> visited = new IdentityHashMap<>();
		return stream(source, visited);
	}

	private Stream<Property> stream(
		final PathObject object,
		final Map<Object, Object> visited
	) {
		if (object == null) {
			return Stream.empty();
		}

		final boolean exists;
		synchronized(visited) {
			if (!(exists = visited.containsKey(object))) {
				visited.put(object, "");
			}
		}

		if (exists) {
			return Stream.empty();
		} else {
			final var it = new PreOrderIterator<>(
				object,
				properties,
				property -> new PathObject(property.path(), property.value())
			);

			return it.stream()
				.flatMap(prop -> Stream
					.concat(Stream.of(prop), flatten(prop, visited)));
		}
	}

	private Stream<Property> flatten(
		final Property property,
		final Map<Object, Object> visited
	) {
		final var index = new AtomicInteger();

		return flattener.extract(property)
			.flatMap(ele -> {
				final Path path = property.path()
					.indexed(index.getAndIncrement());

				return stream(new PathObject(path, ele), visited);
			});
	}

}
