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
package io.jenetics.incubator.beans.reflect;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.beans.reflect.Reflect.isElementType;
import static io.jenetics.incubator.beans.reflect.Reflect.toRawType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * Base interface used for matching {@link Type} objects.
 * <p>
 * {@snippet lang = "java":
 * final Type type = null; // @replace substring='null' replacement="..."
 * if (ArrayType.of(type) instanceof ArrayType at) {
 *     System.out.println(at);
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface PropertyType
	permits ElementType, StructType, IndexedType
{

	static PropertyType of(final Type type) {
		requireNonNull(type);

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

		// 3) Check for OptionalType.
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

		// 4) Check for ListType.
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

		// 2) Check for RecordType.
		if (type instanceof Class<?> cls && cls.isRecord()) {
			return new RecordType(cls);
		}

		final Class<?> rawType = toRawType(type);

		// 6) Check for ElementType.
		if (rawType != null) {
			if (isElementType(rawType)) {
				return new ElementType(rawType);
			}
		}

		// 5) Check for BeanType
		if (rawType != null) {
			return new BeanType(rawType);
		}

		throw new IllegalArgumentException("Unknown type '%s'.".formatted(type));
	}

}
