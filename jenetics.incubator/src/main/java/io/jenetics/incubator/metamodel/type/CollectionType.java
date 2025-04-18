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

/**
 * Represents collection types. Collection types have a size and are abel to
 * iterate over its element. The iterator elements are from instances of
 * {@link #componentType()}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public sealed interface CollectionType
	extends MetaModelType
	permits IndexedType, SetType, MapType
{

	/**
	 * Return the component type, e.g., Array, List, Set or Optional.
	 *
	 * @return the container type
	 */
	@Override
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
	 * @param object the sized type
	 * @return the length of the sized object
	 * @throws NullPointerException if the specified object is {@code null}
	 */
	int size(final Object object);

	/**
	 * Return an iterable object for the collection {@code object}.
	 *
	 * @param object the collection object
	 * @return the iterable for the collection object.
	 */
	Iterable<Object> iterable(final Object object);

	/**
	 * Return {@code true} if {@code this} type is mutable.
	 *
	 * @return {@code true} if {@code this} type is mutable
	 */
	default boolean isMutable() {
		return false;
	}

}
