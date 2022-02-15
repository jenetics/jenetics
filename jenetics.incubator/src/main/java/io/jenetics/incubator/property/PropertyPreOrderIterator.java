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

import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.jenetics.incubator.property.Property.Path;

/**
 * Preorder property iterator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class PropertyPreOrderIterator implements Iterator<Property> {

	private final Property.Reader reader;
	private final Deque<Iterator<Property>> deque = new ArrayDeque<>();

	PropertyPreOrderIterator(
		final Path basePath,
		final Object object,
		final Property.Reader reader
	) {
		this.reader = requireNonNull(reader);
		deque.push(reader.read(basePath, object).iterator());
	}

	@Override
	public boolean hasNext() {
		final Iterator<Property> peek = deque.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public Property next() {
		final Iterator<Property> it = deque.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final Property node = it.next();
		if (!it.hasNext()) {
			deque.pop();
		}

		final Iterator<Property> children = reader
			.read(node.path(), node.value())
			.iterator();
		if (children.hasNext()) {
			deque.push(children);
		}

		return node;
	}

}
