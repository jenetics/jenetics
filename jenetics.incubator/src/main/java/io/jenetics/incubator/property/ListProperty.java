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

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a list property.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ListProperty extends CollectionProperty {

	ListProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject, path, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> value() {
		return (List<Object>)value;
	}

	@Override
	public int size() {
		return value != null ? value().size() : 0;
	}

	public Object get(final int index) {
		if (value == null) {
			throw new IndexOutOfBoundsException("List is null.");
		}
		return value().get(index);
	}

	@Override
	public Iterator<Object> iterator() {
		return value != null ? value().iterator() : emptyIterator();
	}

	@Override
	public Stream<Object> stream() {
		return value().stream();
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
