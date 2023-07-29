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
import io.jenetics.incubator.beans.description.IndexedValue;
import io.jenetics.incubator.beans.description.SingleValue;
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

			if (description.value() instanceof SingleValue desc) {
				final var path = object.path().append(description.name());
				final var value = desc.getter().get(object.value());

				final Property property;
				if (isArrayType(desc.value())) {
					property = new ArrayProperty(path, toValue(enclosing, value, desc));
				} else if (isListType(desc.value())) {
					property = new ListProperty(path, toValue(enclosing, value, desc));
				} else {
					property = new SimpleProperty(path, toValue(enclosing, value, desc));
				}

				return Stream.of(property);
			} else if (description.value() instanceof IndexedValue desc) {
				final var path = description.path().element() instanceof Path.Index
					? object.path()
					: object.path().append(new Path.Name(description.name()));

				final int size = desc.size().get(enclosing);

				return IntStream.range(0, size).mapToObj(i -> {
					final var value = desc.getter().get(enclosing, i);

					return new IndexProperty(
						path.append(new Path.Index(i)),
						i,
						new Mutable(
							enclosing,
							value,
							value != null ? value.getClass() : toClass(desc.value()),
							o -> desc.getter().get(o, i),
							(o, v) -> desc.setter().orElseThrow().set(o, i, v)
						)
					);
				});
			}

			return Stream.empty();
		});
	}

	private static Value toValue(
		final Object enclosing,
		final Object value,
		final SingleValue description
	) {
		if (description.setter().isPresent()) {
			return new Mutable(
				enclosing,
				value,
				toClass(description.value()),
				description.getter(),
				description.setter().orElseThrow()
			);
		} else {
			return new Immutable(
				enclosing,
				value,
				toClass(description.value())
			);
		}
	}

	private static Class<?> toClass(final Type type) {
		if (type instanceof ParameterizedType pt) {
			return (Class<?>)pt.getRawType();
		} else {
			return (Class<?>)type;
		}
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
