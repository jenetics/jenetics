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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix.util;

import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Queue<T> {

	private static final class Node<T> {
		T _object;
		Node<T> _next;

		Node(final T object, final Node<T> next) {
			_object = object;
			_next = next;
		}
	}

	Node<T> head;
	Node<T> tail;

	public void enqueue(final T value) {
		if (head == null) {
			head = tail = new Node<>(value, null);
		} else {
			tail._next = new Node<>(value, null);
			tail = tail._next;
		}
	}

	public T dequeue() {
		if (head == null) {
			throw new NoSuchElementException("No more elements.");
		}

		final T result = head._object;
		final Node<T> oldHead = head;
		head = head._next;
		if (head == null) {
			tail = null;
		} else {
			oldHead._next = null;
		}

		return result;
	}

	public T firstObject() {
		if (head == null) {
			throw new NoSuchElementException("No more elements.");
		}
		return head._object;
	}

	public boolean isEmpty() {
		return head == null;
	}

}
