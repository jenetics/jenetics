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
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.4
 */
public abstract class ArraySeq<T>
	implements
		Seq<T>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	public final Array<T> array;

	public ArraySeq(final Array<T> array) {
		this.array = requireNonNull(array, "Array must not be null.");
	}

	@Override
	public final T get(final int index) {
		array.checkIndex(index);
		return array.get(index);
	}

	@SuppressWarnings("unchecked")
	final Array<T> __append(final Iterable<? extends T> values) {
		requireNonNull(values);
		return values instanceof ArraySeq<?>
			? array.append(((ArraySeq<T>)values).array)
			: array.append(values);
	}

	@SuppressWarnings("unchecked")
	final Array<T> __prepend(final Iterable<? extends T> values) {
		requireNonNull(values);
		return values instanceof ArraySeq<?>
			? ((ArraySeq<T>)values).array.append(array)
			: array.prepend(values);
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ArrayIterator<>(array);
	}

	@Override
	public Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public Stream<T> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}

	@Override
	public Spliterator<T> spliterator() {
		return new ArraySpliterator<T>(array);
	}

	@Override
	public void forEach(final Consumer<? super T> consumer) {
		requireNonNull(consumer, "The consumer must not be null.");

		for (int i = 0; i < array.length(); ++i) {
			consumer.accept(array.get(i));
		}
	}

	@Override
	public boolean forAll(final Predicate<? super T> predicate) {
		requireNonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = 0; i < array.length() && valid; ++i) {
			valid = predicate.test(array.get(i));
		}
		return valid;
	}

	@Override
	public int indexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		array.checkIndex(start, end);
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = start; i < end && index == -1; ++i) {
			if (predicate.test(array.get(i))) {
				index = i;
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
		array.checkIndex(start, end);
		requireNonNull(predicate, "Predicate must not be null.");

		int index = -1;

		for (int i = end; --i >= start && index == -1;) {
			if (predicate.test(array.get(i))) {
				index = i;
			}
		}

		return index;
	}

	@Override
	public int length() {
		return array.length();
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
