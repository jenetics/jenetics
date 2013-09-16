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
package org.jenetics.internal.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-09-16 $</em>
 */
public final class SeqListIteratorAdapter<T>
	extends SeqIteratorAdapter<T>
	implements ListIterator<T>
{

	public SeqListIteratorAdapter(final MSeq<T> seq) {
		super(seq);
	}

	@Override
	public void set(final T value) {
		final MSeq<T> array = (MSeq<T>)_seq;
		array.set(_pos, value);
	}

	@Override
	public int nextIndex() {
		return _pos;
	}

	@Override
	public boolean hasPrevious() {
		return _pos > 0;
	}

	@Override
	public T previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return _seq.get(--_pos);
	}

	@Override
	public int previousIndex() {
		return _pos - 1;
	}

	@Override
	public void add(final T o) {
		throw new UnsupportedOperationException("Can't change array size.");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Can't change array size.");
	}
}
