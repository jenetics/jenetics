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
package io.jenetics.incubator.metamodel.type;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.metamodel.internal.Reflect.isElementType;
import static io.jenetics.incubator.metamodel.internal.Reflect.toRawType;

import java.lang.constant.Constable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Base interface used for matching {@link Type} objects.
 * {@snippet class="ReflectSnippets" region="PropertyType"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface MetaModelType
	permits ElementType, StructType, CollectionType
{

	/**
	 * Return the underlying property type.
	 *
	 * @return the underlying property type
	 */
	Type type();

	/**
	 * Creates a property type from the given {@code type}.
	 *
	 * @param type the java type
	 * @return the converted property type
	 */
	static MetaModelType of(final Type type) {
		requireNonNull(type);

		// 0) Check for ElementType.
		if (type instanceof Class<?> cls &&
			Constable.class.isAssignableFrom(cls))
		{
			return new ElementType(cls);
		}

		// 1) Check for ArrayType.
		if (type instanceof Class<?> arrayType && arrayType.isArray()) {
			if (arrayType.componentType().isPrimitive()) {
				return new ElementType(arrayType);
			} else {
				return new ArrayType(
					arrayType,
					arrayType.getComponentType()
				);
			}
		}

		// 2) Check for OptionalType.
		if (type instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> optionalType &&
			Optional.class.isAssignableFrom(optionalType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				toRawType(typeArguments[0]) != null)
			{
				return new OptionalType(toRawType(typeArguments[0]) );
			}
		}
		if (type instanceof Class<?> optionalType &&
			Optional.class.isAssignableFrom(optionalType))
		{
			return new OptionalType(Object.class);
		}

		// 3) Check for ListType.
		if (type instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				toRawType(typeArguments[0]) != null)
			{
				return new ListType(listType, toRawType(typeArguments[0]) );
			}
		}
		if (type instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return new ListType(listType, Object.class);
		}

		// 4) Check for SetType.
		if (type instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> setType &&
			Set.class.isAssignableFrom(setType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				toRawType(typeArguments[0]) != null)
			{
				return new SetType(setType, toRawType(typeArguments[0]) );
			}
		}
		if (type instanceof Class<?> setType &&
			Set.class.isAssignableFrom(setType))
		{
			return new SetType(setType, Object.class);
		}

		// 5) Check for MapType.
		if (type instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> mapType &&
			Map.class.isAssignableFrom(mapType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 2 &&
				toRawType(typeArguments[0]) != null &&
				toRawType(typeArguments[1]) != null)
			{
				return new MapType(
					mapType,
					toRawType(typeArguments[0]),
					toRawType(typeArguments[1])
				);
			}
		}
		if (type instanceof Class<?> mapType &&
			Map.class.isAssignableFrom(mapType))
		{
			return new MapType(mapType, Object.class, Object.class);
		}

		// 6) Check for RecordType.
		if (type instanceof Class<?> cls && cls.isRecord()) {
			return new RecordType(cls);
		}

		final Class<?> rawType = toRawType(type);

		// 7) Check for ElementType.
		if (rawType != null && isElementType(rawType)) {
			return new ElementType(rawType);
		}

		// 8) Rest must be BeanType
		if (rawType != null) {
			return new BeanType(rawType);
		}

		throw new IllegalArgumentException("Unknown type '%s'.".formatted(type));
	}

}
