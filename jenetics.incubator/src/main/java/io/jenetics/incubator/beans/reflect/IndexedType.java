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

/**
 * Represents indexed types. An indexed type is a container where its elements
 * are accessible via index. Such types are arrays and lists.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface IndexedType
	extends PropertyType
	permits ArrayType, ListType, OptionalType
{

	/**
	 * Return the container type, e.g., Array or List.
	 *
	 * @return the container type
	 */
	Class<?> type();

	/**
	 * Return the container element type.
	 *
	 * @return the container element type
	 */
	Class<?> componentType();

	/**
	 * Returns the length of the given indexed object, as an {@code int}.
	 *
	 * @param object the indexed type
	 * @return the length of the array
	 * @throws NullPointerException if the specified object is {@code null}
	 */
	int size(final Object object);

	/**
	 * Returns the value of the indexed object at the given index.
	 *
	 * @param object the indexed type
	 * @param index the index
	 * @return the value of the indexed object at the given index
	 * @throws NullPointerException if the specified object is {@code null}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * ({@code index < 0 || index >= size()})
	 */
	Object get(final Object object, final int index);

	/**
	 * Sets the value of the indexed object at the given index.
	 *
	 * @param object the indexed object
	 * @param index the index
	 * @param value the new value of the indexed object
	 * @throws NullPointerException if the specified object argument is
	 * {@code null}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * ({@code index < 0 || index >= size()})
	 */
	void set(final Object object, final int index, final Object value);

	/**
	 * Return {@code true} if {@code this} type is mutable.
	 *
	 * @return {@code true} if {@code this} type is mutable
	 */
	default boolean isMutable() {
		return true;
	}

}
