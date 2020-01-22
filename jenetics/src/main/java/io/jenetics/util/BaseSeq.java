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
package io.jenetics.util;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.internal.collection.BaseSeqIterator;
import io.jenetics.internal.collection.BaseSeqSpliterator;

/**
 * General base interface for a ordered, fixed sized, object sequence.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 5.2
 * @version 5.2
 */
public interface BaseSeq<T> extends Iterable<T> {

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= size()).
	 */
	public T get(final int index);

	/**
	 * Return the length of this sequence. Once the sequence is created, the
	 * length can't be changed.
	 *
	 * @return the length of this sequence.
	 */
	public int length();

	@Override
	public default Iterator<T> iterator() {
		return listIterator();
	}

	public default ListIterator<T> listIterator() {
		return new BaseSeqIterator<>(this);
	}

	/**
	 * Returns a sequential Stream with this sequence as its source.
	 *
	 * @return a sequential Stream over the elements in this sequence
	 */
	public default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public default Spliterator<T> spliterator() {
		return new BaseSeqSpliterator<T>(this);
	}

}
