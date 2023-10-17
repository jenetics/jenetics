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

import static io.jenetics.incubator.beans.reflect.Reflect.raise;
import static io.jenetics.incubator.beans.reflect.Reflect.toRawType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Trait which represents an {@code Optional} type.
 *
 * @param componentType the optional component type
 */
public record OptionalType(Class<?> componentType) implements IndexedType {

	@Override
	public Class<?> type() {
		return Optional.class;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public int size(Object object) {
		return object instanceof Optional<?> optional ? optional.isPresent()
			? 1 : 0 : raise(new IllegalArgumentException("Not an Optional: " + object));
	}

	@Override
	public Object get(Object object, int index) {
		return object instanceof Optional<?> optional
			? optional.orElseThrow()
			: raise(new IllegalArgumentException("Not an Optional: " + object));
	}

	@Override
	public void set(Object object, int index, Object value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a {@code OptionalType} instance if the given {@code type} is a
	 * {@code Optional} class.
	 * {@snippet lang = "java":
	 * final Type type = null; // @replace substring='null' replacement="..."
	 * if (OptionalType.of(type) instanceof OptionalType ot) {
	 *     System.out.println(ot);
	 * }
	 * }
     *
     * @param type the type object
     * @return an {@code OptionalType} if the given {@code type} is an optional
     * type, or {@code null}
     */
    public static IndexedType of(final Type type) {
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

        return null;
    }
}
