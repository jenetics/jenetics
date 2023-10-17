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

import java.lang.reflect.Array;

/**
 * Trait which represents an array type.
 *
 * @param type the array type
 * @param componentType the array component type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public record ArrayType(
	Class<?> type,
	Class<?> componentType
)
	implements IndexedType
{

	public ArrayType {
		if (!type.isArray()) {
			throw new IllegalArgumentException("Not an array type: " + type);
		}
	}

	@Override
	public int size(Object object) {
		return Array.getLength(object);
	}

	@Override
	public Object get(Object object, int index) {
		return Array.get(object, index);
	}

	@Override
	public void set(Object object, int index, Object value) {
		Array.set(object, index, value);
	}

//	/**
//	 * Return an {@code ArrayType} instance if the given {@code type} is an
//	 * array class.
//	 * {@snippet lang = "java":
//	 * final Type type = null; // @replace substring='null' replacement="..."
//	 * if (ArrayType.of(type) instanceof ArrayType at) {
//	 *     System.out.println(at);
//	 * }
//	 * }
//     *
//     * @param type the type object
//     * @return an {@code ArrayType} if the given {@code type} is an array type,
//     * or {@code null}
//     */
//    public static IndexedType of(final Type type) {
//        if (type instanceof Class<?> arrayType && arrayType.isArray()) {
//            return new ArrayType(
//                arrayType,
//                arrayType.getComponentType()
//            );
//        }  {
//            return null;
//        }
//    }

}
