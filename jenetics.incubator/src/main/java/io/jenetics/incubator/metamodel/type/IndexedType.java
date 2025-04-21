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

import java.util.Iterator;
import java.util.NoSuchElementException;

import io.jenetics.incubator.metamodel.access.Curryer;
import io.jenetics.incubator.metamodel.access.IndexedAccessor;
import io.jenetics.incubator.metamodel.access.IndexedGetter;

/**
 * Represents indexed types. An indexed type is a container where its elements
 * are accessible via index. Such types are arrays and lists.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface IndexedType
	extends CollectionType
	permits ArrayType, ListType
{

	/**
	 * Return the element access object.
	 *
	 * @return the element access object
	 */
	Curryer<? extends IndexedAccessor> accessor();

	@Override
	default Curryer<Iterable<Object>> iterable() {
		return collection -> () -> new Iterator<>() {
			private final IndexedGetter getter = accessor().curry(collection).getter();
			private final int size = size().curry(collection).get();

			private int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor != size;
			}

			@Override
			public Object next() {
				final int i = cursor;
				if (cursor >= size) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				return getter.get(i);
			}
		};
	}

}
