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

import static java.lang.Math.min;
import static java.lang.String.format;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0 &mdash; <em>$Date: 2014-10-07 $</em>
 */
public class ArrayProxyMSeq<T, P extends ArrayProxy<T, ?, ?>>
	extends ArrayProxySeq<T, P>
	implements MSeq<T>
{

	private static final long serialVersionUID = 1L;

	public ArrayProxyMSeq(final P proxy) {
		super(proxy);
	}

	@Override
	public MSeq<T> copy() {
		return new ArrayProxyMSeq<>(proxy.copy());
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayProxyMIterator<>(proxy);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ArrayProxyMIterator<>(proxy);
	}

	@Override
	public void set(final int index, final T value) {
		proxy.cloneIfSealed();
		proxy.set(index, value);
	}

	@Override
	public MSeq<T> setAll(final Iterator<? extends T> it) {
		proxy.cloneIfSealed();
		for (int i = proxy.start; i < proxy.end && it.hasNext(); ++i) {
			proxy.__set__(i, it.next());
		}
		return this;
	}

	@Override
	public MSeq<T> setAll(final Iterable<? extends T> values) {
		return setAll(values.iterator());
	}

	@Override
	public MSeq<T> setAll(final T[] values) {
		proxy.cloneIfSealed();
		for (int i = 0, n = min(proxy.length, values.length); i < n; ++i) {
			proxy.__set(i, values[i]);
		}
		return this;
	}

	public MSeq<T> fill(final Supplier<? extends T> supplier) {
		proxy.cloneIfSealed();
		for (int i = proxy.start; i < proxy.end; ++i) {
			proxy.__set__(i, supplier.get());
		}
		return this;
	}

	@Override
	public MSeq<T> shuffle(final Random random) {
		proxy.cloneIfSealed();
		for (int j = length() - 1; j > 0; --j) {
			swap(j, random.nextInt(j + 1));
		}
		return this;
	}

	@Override
	public void swap(final int i, final int j) {
		final T temp = proxy.get(i);
		proxy.__set(i, proxy.get(j));
		proxy.__set(j, temp);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void swap(int start, int end, MSeq<T> other, int otherStart) {
		checkIndex(start, end, otherStart, other.length());

		if (start < end) {
			if (other instanceof ArrayProxyMSeq<?, ?>) {
				__swap(start, end, (ArrayProxyMSeq<T, P>) other, otherStart);
			} else {
				proxy.cloneIfSealed();

				for (int i = (end - start); --i >= 0;) {
					final T temp = proxy.__get(i + start);
					proxy.__set(i + start, other.get(otherStart + i));
					other.set(otherStart + i, temp);
				}
			}
		}
	}

	private void __swap(int start, int end, ArrayProxyMSeq<T, P> other, int otherStart) {
		proxy.swap(start, end, other.proxy, otherStart);
	}

	private void checkIndex(
		final int start, final int end,
		final int otherStart, final int otherLength
	) {
		proxy.checkIndex(start, end);
		if (otherStart < 0 || (otherStart + (end - start)) > otherLength) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %d)",
				otherStart, (otherStart + (end - start))
			));
		}
	}

	@Override
	public MSeq<T> subSeq(final int start, final int end) {
		return new ArrayProxyMSeq<>(proxy.slice(start, end));
	}

	@Override
	public MSeq<T> subSeq(final int start) {
		return new ArrayProxyMSeq<>(proxy.slice(start));
	}

	@Override
	public <B> MSeq<B> map(final Function<? super T, ? extends B> mapper) {
		return new ArrayProxyMSeq<>(proxy.map(mapper));
	}

	@Override
	public ISeq<T> toISeq() {
		return new ArrayProxyISeq<>(proxy.seal());
	}

	@Override
	public List<T> asList() {
		return new ArrayProxyMList<>(proxy);
	}

}
