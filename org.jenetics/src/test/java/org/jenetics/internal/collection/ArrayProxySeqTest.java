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

import java.util.function.Function;

import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;
import org.jenetics.util.SeqTestBase;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-04-21 $</em>
 */
public class ArrayProxySeqTest extends SeqTestBase {

	private static final class ArrayProxySeqImpl<T, P extends ArrayProxy<T, ?, ?>>
		extends ArrayProxySeq<T, P>
	{
		private static final long serialVersionUID = 1L;

		ArrayProxySeqImpl(final P proxy) {
			super(proxy);
		}

		@Override
		public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
			final ObjectArrayProxy<B> p = new ObjectArrayProxy<>(proxy.length);
			for (int i = 0; i < p.length; ++i) {
				p.array[i] = mapper.apply(proxy.__get(i));
			}
			return new ArrayProxyISeq<>(p);
		}

		@Override
		public ISeq<T> subSeq(final int start) {
			return new ArrayProxyISeq<>(proxy.slice(start));
		}

		@Override
		public ISeq<T> subSeq(int start, int end) {
			return new ArrayProxyISeq<>(proxy.slice(start, end));
		}

	}

	@Override
	protected Seq<Integer> newSeq(final int length) {
		final ObjectArrayProxy<Integer> impl = new ObjectArrayProxy<>(length);
		for (int i = 0; i < length; ++i) {
			impl.array[i] = i;
		}
		return new ArrayProxySeqImpl<>(impl);
	}


}
