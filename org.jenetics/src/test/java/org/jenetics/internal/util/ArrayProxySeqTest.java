/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.internal.util;

import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;
import org.jenetics.util.SeqTestBase;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-29 $</em>
 */
public class ArrayProxySeqTest extends SeqTestBase {

	private static final class ArrayProxySeqImpl<T> extends ArrayProxySeq<T> {

		ArrayProxySeqImpl(final ArrayProxy<T> proxy) {
			super(proxy);
		}

		@Override
		public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
			final ArrayProxyImpl<B> proxy = new ArrayProxyImpl<>(_proxy._length);
			for (int i = 0; i < proxy._length; ++i) {
				proxy._array[i] = mapper.apply(_proxy.uncheckedGet(i));
			}
			return new ArrayProxyISeq<>(proxy);
		}

		@Override
		public ISeq<T> subSeq(final int start) {
			return new ArrayProxyISeq<>(_proxy.sub(start));
		}

		@Override
		public ISeq<T> subSeq(int start, int end) {
			return new ArrayProxyISeq<>(_proxy.sub(start, end));
		}

	}

	@Override
	protected Seq<Integer> newSeq(final int length) {
		final ArrayProxyImpl<Integer> impl = new ArrayProxyImpl<>(length);
		for (int i = 0; i < length; ++i) {
			impl._array[i] = i;
		}
		return new ArrayProxySeqImpl<>(impl);
	}


}




