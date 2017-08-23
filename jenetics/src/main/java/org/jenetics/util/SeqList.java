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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
class SeqList<T>
	extends AbstractList<T>
	implements
		RandomAccess,
		Serializable
{
	private static final long serialVersionUID = 1L;

	public final Seq<T> seq;

	SeqList(final Seq<T> seq) {
		this.seq = requireNonNull(seq, "Seq must not be null.");
	}

	@Override
	public T get(final int index) {
		return seq.get(index);
	}

	@Override
	public int size() {
		return seq.length();
	}

	@Override
	public int indexOf(final Object element) {
		int index = -1;
		if (element == null) {
			for (int i = 0; i < seq.length() && index == -1; ++i) {
				if (seq.get(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = 0; i < seq.length() && index == -1; ++i) {
				if (element.equals(seq.get(i))) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public int lastIndexOf(final Object element) {
		int index = -1;
		if (element == null) {
			for (int i = seq.length(); --i >= 0 && index == -1;) {
				if (seq.get(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = seq.length(); --i >= 0 && index == -1;) {
				if (element.equals(seq.get(i))) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[size()];
		for (int i = size(); --i >= 0;) {
			array[i] = this.seq.get(i);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < size()) {
			final E[] copy = (E[])java.lang.reflect.Array.newInstance(
				array.getClass().getComponentType(), size()
			);
			for (int i = size(); --i >= 0;) {
				copy[i] = (E) this.seq.get(i);
			}

			return copy;
		}

		for (int i = size(); --i >= 0;) {
			array[i] = (E) this.seq.get(i);
		}
		return array;
	}

}
