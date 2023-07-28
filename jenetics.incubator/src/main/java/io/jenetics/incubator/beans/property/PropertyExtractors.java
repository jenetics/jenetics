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
package io.jenetics.incubator.beans.property;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.description.DescriptionExtractors;
import io.jenetics.incubator.beans.description.IndexedDescription;
import io.jenetics.incubator.beans.description.SimpleDescription;
import io.jenetics.incubator.beans.util.Extractor;
import io.jenetics.incubator.beans.util.PreOrderIterator;

/**
 * Contains functionality for extracting the properties from a given bean object.
 * The higher level functionality is implemented in the {@link Properties} class.
 *
 * @see Properties
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PropertyExtractors {

	/**
	 * Property extractor object, which extracts the direct (first level)
	 * properties of the input object.
	 */
	public static final Extractor<PathValue<Object>, Property>
		DIRECT =
		PropertyExtractors::extract;

	/**
	 * Property extractor object, which extracts the all properties of the input
	 * object, recursively.
	 */
	public static final Extractor<PathValue<Object>, Property>
		RECURSIVE =
		PreOrderIterator.extractor(
			DIRECT,
			property -> new PathValue<>(property.path(), property.value()),
			PathValue::value
		);

	private PropertyExtractors() {
	}

	private static Stream<Property> extract(final PathValue<Object> object) {
		if (object == null || object.value() == null) {
			return Stream.empty();
		}

		final var type = new PathValue<Type>(object.value().getClass());
		final var descriptions = DescriptionExtractors.DIRECT.extract(type);

		return descriptions.flatMap(description -> {
			final var enclosing = object.value();

			if (description instanceof SimpleDescription desc) {
				final var path = object.path().append(desc.name());
				final var value = desc.getter().apply(object.value());

				final Property property;
				if (isArrayType(desc.type())) {
					property = new ArrayProperty(desc, enclosing, path, value);
				} else if (isListType(desc.type())) {
					property = new ListProperty(desc, enclosing, path, value);
				} else {
					property = new SimpleProperty(desc, enclosing, path, value);
				}

				return Stream.of(property);
			} else if (description instanceof IndexedDescription desc) {
				final var path = desc.path().element() instanceof Path.Index
					? object.path()
					: object.path().append(new Path.Name(desc.name()));

				final var list = object.value();
				final int size = desc.size().apply(list);

				return IntStream.range(0, size).mapToObj(i -> {
					final var value = desc.getter().apply(list, i);

					return new IndexProperty(
						desc,
						enclosing,
						path.append(new Path.Index(i)),
						i,
						value
					);
				});
			}

			return Stream.empty();
		});
	}

	private static boolean isArrayType(final Type type) {
		return type instanceof Class<?> cls &&
			cls.isArray() &&
			!cls.getComponentType().isPrimitive();
	}

	private static boolean isListType(final Type type) {
		if (type instanceof Class<?> cls && List.class.isAssignableFrom(cls)) {
			return true;
		}
        return type instanceof ParameterizedType pt &&
	        pt.getRawType() instanceof Class<?> cls &&
	        List.class.isAssignableFrom(cls);
    }

}
