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
package io.jenetics.incubator.property;

import static java.util.Collections.emptyIterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents an array property.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ArrayProperty extends CollectionProperty {

	ArrayProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject, path, value);
	}

	@Override
	public Object[] value() {
		return (Object[])value;
	}

	@Override
	public int size() {
		return value != null ? value().length : 0;
	}

	@Override
	public Object get(final int index) {
		if (value == null) {
			throw new IndexOutOfBoundsException("Array is null.");
		}

		return value()[index];
	}

	@Override
	public Iterator<Object> iterator() {
		return value != null
			? Arrays.asList(value()).iterator()
			: emptyIterator();
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
