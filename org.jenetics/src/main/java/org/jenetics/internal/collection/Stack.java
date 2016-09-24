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
package org.jenetics.internal.collection;

import java.util.function.Consumer;

/**
 * Minimal implementation of stack data-structure. {@code Null} values are not
 * permitted, but not checked.
 *
 * <pre>{@code
 * final Stack<Integer> stack = new Stack<>();
 * for (int i = 0; i &lt; 10; ++i) {
 *     stack.push(i);
 * }
 *
 * for (Integer i = stack.pop(); i != null; i = stack.pop()) {
 *     System.out.println(i);
 * }
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 2.0
 */
public final class Stack<T> {

	private Node<T> _tail = null;

	public int length = 0;

	public void push(final T value) {
		_tail = new Node<>(value, _tail);
		++length;
	}

	public T pop() {
		T value = null;
		if (_tail != null) {
			value = _tail._value;
			_tail = _tail._previous;
			--length;
		}

		return value;
	}

	public void popAll(final Consumer<T> consumer) {
		for (T element = pop(); element != null; element = pop()) {
			consumer.accept(element);
		}
	}

	public void clear() {
		_tail = null;
		length = 0;
	}

	private static final class Node<T> {
		final T _value;
		final Node<T> _previous;

		Node(final T value, final Node<T> previous) {
			_value = value;
			_previous = previous;
		}
	}
}
