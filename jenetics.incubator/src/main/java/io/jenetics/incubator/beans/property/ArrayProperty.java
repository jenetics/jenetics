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
package io.jenetics.incubator.beans.property;

import static java.util.Collections.emptyIterator;

import java.util.Arrays;
import java.util.Iterator;

import io.jenetics.incubator.beans.Path;

/**
 * Represents an array property.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class ArrayProperty extends IndexedProperty {

	ArrayProperty(final Path path, final Value value) {
		super(path, value);
	}

	/**
	 * Return the array values as {@code Object[]} array.
	 *
	 * @return the array values
	 */
	public Object[] array() {
		return (Object[])value().value();
	}

	@Override
	public int size() {
		return array() != null ? array().length : 0;
	}

	@Override
	public Object get(final int index) {
		if (array() == null) {
			throw new IndexOutOfBoundsException("Array is null.");
		}

		return array()[index];
	}

	@Override
	public Iterator<Object> iterator() {
		return array() != null
			? Arrays.asList(array()).iterator()
			: emptyIterator();
	}

}
