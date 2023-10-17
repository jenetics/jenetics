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
import java.util.List;

/**
 * Trait which represents a {@code List} type.
 *
 * @param type the list type
 * @param componentType the list component type
 */
public record ListType(Class<?> type,
                       Class<?> componentType) implements IndexedType {

	public ListType {
		if (!List.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Not a list type: " + type);
		}
	}

	@Override
	public int size(final Object object) {
		return object instanceof List<?> list
			? list.size()
			: raise(new IllegalArgumentException("Not a list: " + object));
	}

	@Override
	public Object get(final Object object, final int index) {
		return object instanceof List<?> list
			? list.get(index)
			: raise(new IllegalArgumentException("Not a list: " + object));
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void set(Object object, int index, Object value) {
		if (object instanceof List list) {
			list.set(index, value);
		} else {
			throw new IllegalArgumentException("Not a list: " + object);
		}
	}

	/**
	 * Return a {@code ListType} instance if the given {@code type} is a
	 * {@code List} class.
	 * {@snippet lang = "java":
	 * final Type type = null; // @replace substring='null' replacement="..."
	 * if (ListType.of(type) instanceof ListType lt) {
	 *     System.out.println(lt);
	 * }
	 * }
     *
     * @param type the type object
     * @return an {@code ListType} if the given {@code type} is a list type, or
     * {@code null}
     */
    public static IndexedType of(final Type type) {
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

        return null;
    }
}
