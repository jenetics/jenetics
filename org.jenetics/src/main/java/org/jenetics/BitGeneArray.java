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
package org.jenetics;

import org.jenetics.internal.collection.ArrayProxyISeq;
import org.jenetics.internal.collection.ArrayProxyMSeq;
import org.jenetics.internal.collection.Array;
import org.jenetics.internal.util.bit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0
 */
final class BitGeneArray extends ArrayProxyMSeq<BitGene> {

	private static final long serialVersionUID = 1L;

	BitGeneArray(final Array<BitGene> array) {
		super(array);
	}

	@Override
	public BitGeneArray copy() {
		return new BitGeneArray(array.copy());
	}

	@Override
	public BitGeneISeq toISeq() {
		return new BitGeneISeq(array.seal());
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.4
	 * @version 1.4
	 */
	static final class BitGeneISeq extends ArrayProxyISeq<BitGene> {
		private static final long serialVersionUID = 1L;

		public BitGeneISeq(final Array<BitGene> array) {
			super(array);
		}

		void copyTo(final byte[] array) {
			for (int i = 0; i < length(); ++i) {
				bit.set(array, i, get(i).booleanValue());
			}
			//System.arraycopy(this.array, 0, array, 0, this.array.array.length);
		}

		@Override
		public BitGeneArray copy() {
			return new BitGeneArray(array.copy());
		}

	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.4
	 * @version 3.0
	 */
	static final class BitGeneStore implements Array.Store<BitGene> {
		private static final long serialVersionUID = 1L;

		private final byte[] _array;
		private final int _length;

		BitGeneStore(final byte[] array, final int length) {
			_array = array;
			_length = length;
		}

		BitGeneStore(final int length) {
			this(bit.newArray(length), length);
		}

		@Override
		public BitGene get(final int index) {
			return BitGene.of(bit.get(_array, index));
		}

		@Override
		public void set(final int index, final BitGene value) {
			bit.set(_array, index, value.booleanValue());
		}

		@Override
		public BitGeneStore copy(final int from, final int until) {
			return new BitGeneStore(bit.copy(_array, from, until), until - from);
		}

		@Override
		public int length() {
			return _length;
		}

	}

}
