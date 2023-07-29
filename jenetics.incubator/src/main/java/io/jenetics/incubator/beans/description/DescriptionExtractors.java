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
package io.jenetics.incubator.beans.description;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathEntry;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.Extractor;

/**
 * Methods for extracting <em>static</em> property {@link Description} objects,
 * directly accessible from a given data type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class DescriptionExtractors {

	/**
	 * Descriptor extractor object, which extracts the direct (first level)
	 * properties of the input type.
	 */
	public static final Extractor<PathValue<Type>, Description>
		DIRECT =
		DescriptionExtractors::extract;

	private DescriptionExtractors() {
	}

	private static Stream<Description> extract(final PathValue<? extends Type> type) {
		if (type == null || type.value() == null) {
			return Stream.empty();
		}

		final var descriptions = new ArrayList<Description>();

		toArrayDescription(type).ifPresent(descriptions::add);
		toListDescription(type).ifPresent(descriptions::add);
		toDescriptions(type).forEach(descriptions::add);

		descriptions.sort(Comparator.comparing(PathEntry::name));
		return descriptions.stream();
	}

	private static Description
	toDescription(final Path path, final PropertyDescriptor descriptor) {
		return new Description(
			path.append(descriptor.getName()),
			new SingleValue(
				descriptor.getReadMethod().getDeclaringClass(),
				descriptor.getReadMethod().getGenericReturnType(),
				Methods.toGetter(descriptor.getReadMethod()),
				Methods.toSetter(descriptor.getWriteMethod())
			)
		);
	}

	private static Description
	toDescription(final Path path, final RecordComponent component) {
		return new Description(
			path.append(component.getName()),
			new SingleValue(
				component.getDeclaringRecord(),
				component.getAccessor().getGenericReturnType(),
				Methods.toGetter(component.getAccessor()),
				null
			)
		);
	}

	private static Optional<Description>
	toArrayDescription(final PathValue<? extends Type> type) {
		if (type.value() instanceof Class<?> arrayType &&
			arrayType.isArray() && !arrayType.componentType().isPrimitive())
		{
			return Optional.of(
				new Description(
					type.path().append(new Path.Index(0)),
					new IndexedValue(
						arrayType.getComponentType(),
						arrayType,
						Array::getLength, Array::get, Array::set
					)
				)
			);
		} else {
			return Optional.empty();
		}
	}

	private static Optional<Description>
	toListDescription(final PathValue<? extends Type> type) {
		 if (type.value() instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				typeArguments[0] instanceof Class<?> componentType)
			{
				return Optional.of(
					new Description(
						type.path().append(new Path.Index(0)),
						new IndexedValue(
							listType,
							componentType,
							Lists::size, Lists::get, Lists::set
						)
					)
				);
			}
		}

		if (type.value() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return Optional.of(
				new Description(
					type.path().append(new Path.Index(0)),
					new IndexedValue(
						listType,
						Object.class,
						Lists::size, Lists::get, Lists::set
					)
				)
			);
		}

		return Optional.empty();
	}

	private static Stream<Description>
	toDescriptions(final PathValue<? extends Type> type) {
		if (type.value() instanceof Class<?> cls && cls.isRecord()) {
			return Stream.of(cls.getRecordComponents())
				.filter(d -> d.getAccessor().getReturnType() != Class.class)
				.map(c -> toDescription(type.path(), c));
		} else if (type.value() instanceof Class<?> cls) {
			try {
				final PropertyDescriptor[] descriptors = Introspector
					.getBeanInfo(cls)
					.getPropertyDescriptors();

				return Stream.of(descriptors)
					.filter(d -> d.getReadMethod() != null)
					.filter(d -> d.getReadMethod().getReturnType() != Class.class)
					.map(d -> toDescription(type.path(), d));
			} catch (IntrospectionException e) {
				throw new IllegalArgumentException(
					"Can't introspect class '%s'.".formatted(type.value()),
					e
				);
			}
		} else {
			return Stream.of();
		}
	}


}
