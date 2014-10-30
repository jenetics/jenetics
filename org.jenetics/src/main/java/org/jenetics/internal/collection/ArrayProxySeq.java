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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0 &mdash; <em>$Date: 2014-09-11 $</em>
 */
public abstract class ArrayProxySeq<T, P extends ArrayProxy<T, ?, ?>>
	implements
		Seq<T>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	public final P proxy;

	public ArrayProxySeq(final P proxy) {
		this.proxy = requireNonNull(proxy, "ArrayProxy must not be null.");
	}

	@Override
	public final T get(final int index) {
		return proxy.get(index);
	}

	@Override
	public Stream<T> stream() {
		return proxy.stream();
	}

	@Override
	public Stream<T> parallelStream() {
		return proxy.parallelStream();
	}

	@Override
	public Spliterator<T> spliterator() {
		return proxy.spliterator();
	}

	@Override
	public void forEach(final Consumer<? super T> consumer) {
		requireNonNull(consumer, "The consumer must not be null.");

		for (int i = proxy.start; i < proxy.end; ++i) {
			consumer.accept(proxy.__get__(i));
		}
	}

	@Override
	public boolean forAll(final Predicate<? super T> predicate) {
		requireNonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = proxy.start; i < proxy.end && valid; ++i) {
			valid = predicate.test(proxy.__get__(i));
		}
		return valid;
	}

	@Override
	public int indexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		proxy.checkIndex(start, end);
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = start + proxy.start, n = end + proxy.start;
				i < n && index == -1; ++i)
		{
			if (predicate.test(proxy.__get__(i))) {
				index = i - proxy.start;
			}
		}

		return index;
	}

	@Override
	public int lastIndexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		proxy.checkIndex(start, end);
		requireNonNull(predicate, "Predicate must not be null.");

		int index = -1;

		for (int i = end + proxy.start;
			--i >= start + proxy.start && index == -1;)
		{
			if (predicate.test(proxy.__get__(i))) {
				index = i - proxy.start;
			}
		}

		return index;
	}

	@Override
	public int length() {
		return proxy.length;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayProxyIterator<>(proxy);
	}

	public ListIterator<T> listIterator() {
		return new ArrayProxyIterator<>(proxy);
	}

	@Override
	public List<T> asList() {
		return new ArrayProxyList<>(proxy);
	}

	@Override
	public Object[] toArray() {
		return asList().toArray();
	}

	@Override
	public T[] toArray(final T[] array) {
		return asList().toArray(array);
	}

	@Override
	public String toString() {
		return toString("[", ",", "]");
	}

	@Override
	public int hashCode() {
		return Seq.hashCode(this);
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof Seq<?>)) {
			return false;
		}

		final Seq<?> seq = (Seq<?>)object;
		return Seq.equals(this, seq);
	}

}
