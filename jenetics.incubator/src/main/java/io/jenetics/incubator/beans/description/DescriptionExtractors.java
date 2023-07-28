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

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.util.Extractor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Methods for extracting <em>static</em> property {@link Description} objects,
 * directly accessible from a given data type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class DescriptionExtractors {

	private record IndexedType(Type type, Type componentType) {
	}

	/**
	 * Descriptor extractor object, which extracts the direct (first level)
	 * properties of the input type.
	 */
	public static final Extractor<Class<?>, Description>
		DIRECT =
		DescriptionExtractors::extract;

	private DescriptionExtractors() {
	}

	private static Stream<Description> extract(final Class<?> type) {
		if (type == null) {
			return Stream.empty();
		}

		final var descriptions = new ArrayList<Description>();

		toArrayDescription(new PathValue<>(type)).ifPresent(descriptions::add);
		toListDescription(new PathValue<>(type)).ifPresent(descriptions::add);

		if (type.isRecord()) {
			for (var component : type.getRecordComponents()) {
				descriptions.add(toDescription(component));
			}
		} else {
			try {
				final PropertyDescriptor[] descriptors = Introspector
					.getBeanInfo(type)
					.getPropertyDescriptors();

				for (var descriptor : descriptors) {
					if (descriptor.getReadMethod() != null &&
						descriptor.getReadMethod().getReturnType() != Class.class)
					{
						descriptions.add(toDescription(descriptor));
					}
				}
			} catch (IntrospectionException e) {
				throw new IllegalArgumentException(
					"Can't introspect class '%s'.".formatted(type.getName()),
					e
				);
			}
		}

		descriptions.sort(Comparator.naturalOrder());
		return descriptions.stream();
	}

	private static SimpleDescription
	toDescription(final RecordComponent component) {
		return new SimpleDescription(
			Path.of(component.getName()),
			component.getType(),
			component.getDeclaringRecord(),
			Methods.toGetter(component.getAccessor()),
			null
		);
	}

	private static Description
	toDescription(final PropertyDescriptor descriptor) {
		//final Type returnType = descriptor.getReadMethod().getGenericReturnType();

		return new SimpleDescription(
			Path.of(descriptor.getName()),
			descriptor.getPropertyType(),
			descriptor.getReadMethod().getDeclaringClass(),
			Methods.toGetter(descriptor.getReadMethod()),
			Methods.toSetter(descriptor.getWriteMethod())
		);

		// Check if the return type is an array.
		/*
		if (returnType instanceof Class<?> arrayType && arrayType.isArray()) {
			return new IndexedDescription(
				descriptor.getName(),
				arrayType.getComponentType(),
				arrayType,
				Methods.toGetter(descriptor.getReadMethod()),
				Array::getLength, Array::get, Array::set
			);
		}
		 */

		// Check if the return type is a list.
		/*
		if (returnType instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				typeArguments[0] instanceof Class<?> componentType)
			{
				return new IndexedDescription(
					descriptor.getName(),
					componentType,
					List.class,
					Methods.toGetter(descriptor.getReadMethod()),
					Lists::size, Lists::get, Lists::set
				);
			}
		}
		 */
	}

	private static Stream<Description> extract0(final PathValue<Type> type) {
		return Stream.empty();
	}

	private static Optional<Description>
	toArrayDescription(final PathValue<? extends Type> type) {
		if (type.value() instanceof Class<?> arrayType &&
			arrayType.isArray() && !arrayType.componentType().isPrimitive())
		{
			return Optional.of(
				new IndexedDescription(
					type.path(),
					arrayType.getComponentType(),
					arrayType,
					object -> object,
					Array::getLength, Array::get, Array::set
				)
			);
		} else {
			return Optional.empty();
		}
	}

	private static Optional<Description>
	toListDescription(final PathValue<? extends Type> type) {
		if (type.value() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return Optional.of(
				new IndexedDescription(
					type.path(),
					Object.class,
					List.class,
					object -> object,
					Lists::size, Lists::get, Lists::set
				)
			);
		} else {
			return Optional.empty();
		}
	}

}
