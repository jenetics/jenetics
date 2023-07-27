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
package io.jenetics.incubator.beans;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.statical.DescriptionExtractor;
import io.jenetics.incubator.beans.statical.IndexedDescription;
import io.jenetics.incubator.beans.statical.SimpleDescription;
import io.jenetics.incubator.beans.util.Extractor;
import io.jenetics.incubator.beans.util.RecursiveExtractor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PropertyExtractor {

	/**
	 * Property extractor object, which extracts the direct (first level)
	 * properties of the input object.
	 */
	public static final Extractor<PathObject, Property>
		DIRECT =
		PropertyExtractor::extract;

	/**
	 * Property extractor object, which extracts the all properties of the input
	 * object, recursively.
	 */
	public static final Extractor<PathObject, Property>
		RECURSIVE =
		new RecursiveExtractor<>(
			DIRECT,
			property -> new PathObject(property.path(), property.value())
		);


	private PropertyExtractor() {
	}

	private static Stream<Property> extract(final PathObject object) {
		requireNonNull(object);

		if (object.value() != null) {
			return DescriptionExtractor.extract(object.value().getClass())
				.flatMap(description -> {
					final var enclosing = object.value();

					if (description instanceof SimpleDescription desc) {
						final var type = desc.type();
						final var path = object.path().append(desc.name());
						final var value = desc.getter().apply(object.value());

						final Property property;
						if (type.isArray() &&
							!type.getComponentType().isPrimitive())
						{
							property = new ArrayProperty(desc, enclosing, path, value);
						} else if (List.class.isAssignableFrom(type)) {
							property = new ListProperty(desc, enclosing, path, value);
						} else {
							property = new SimpleProperty(desc, enclosing, path, value);
						}
						return Stream.of(property);
					} else if (description instanceof IndexedDescription desc) {
						final var path = desc.name().isEmpty()
							? object.path()
							: object.path().append(new Property.Path.Name(desc.name()));

						final var list = desc.container().apply(object.value());
						int size = desc.size().applyAsInt(list);

						return IntStream.range(0, size).mapToObj(i -> {
							final var value = desc.getter().apply(list, i);

							return new IndexProperty(
								desc,
								enclosing,
								path.append(new Property.Path.Index(i)),
								i,
								value
							);
						});
					}

					return Stream.empty();
				});
		} else {
			return Stream.empty();
		}
	}

}
