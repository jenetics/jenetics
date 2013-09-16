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

import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.RandomAccess;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-09-16 $</em>
 */
final public class SeqListAdapter<T> extends AbstractList<T> implements RandomAccess {
	private final Seq<T> _seq;

	public SeqListAdapter(final Seq<T> seq) {
		_seq = requireNonNull(seq, "Seq must not be null.");
	}

	@Override
	public T get(final int index) {
		return _seq.get(index);
	}

	@Override
	public int size() {
		return _seq.length();
	}

	@Override
	public int indexOf(final Object element) {
		return _seq.indexOf(element);
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public Object[] toArray() {
		return _seq.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < _seq.length()) {
			final E[] copy = (E[])java.lang.reflect.Array.newInstance(
				array.getClass().getComponentType(), _seq.length()
			);
			for (int i = 0; i < _seq.length(); ++i) {
				copy[i] = (E)_seq.get(i);
			}

			return copy;
		}

		for (int i = 0, n = _seq.length(); i < n; ++i) {
			array[i] = (E)_seq.get(i);
		}
		return array;
	}

}
