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
		private final T _value;
		private Node<T> _next;

		Node(final T value, final Node<T> next) {
			_value = value;
			_next = next;
		}
	}

	private Node<T> _head;
	private Node<T> _tail;

	void enqueue(final T value) {
		if (_head == null) {
			_head = _tail = new Node<>(value, null);
		} else {
			_tail._next = new Node<>(value, null);
			_tail = _tail._next;
		}
	}

	T dequeue() {
		if (_head == null) {
			throw new NoSuchElementException("No more elements.");
		}

		final T result = _head._value;
		final Node<T> head = _head;
		_head = _head._next;
		if (_head == null) {
			_tail = null;
		} else {
			head._next = null;
		}

		return result;
	}

	T firstObject() {
		if (_head == null) {
			throw new NoSuchElementException("No more elements.");
		}
		return _head._value;
	}

	boolean isEmpty() {
		return _head == null;
	}

}
