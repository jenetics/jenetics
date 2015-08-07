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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import org.jenetics.internal.collection.ArrayISeq;
import org.jenetics.internal.collection.ArrayMSeq;
import org.jenetics.internal.collection.Array;
import org.jenetics.internal.util.bit;
import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version !__version__!
 */
final class BitGeneMSeq extends ArrayMSeq<BitGene> {

	private static final long serialVersionUID = 1L;

	// Primary constructor.
	private BitGeneMSeq(final Array<BitGene> array) {
		super(array);
		assert array.store() instanceof BitGeneStore;
	}

	@Override
	public BitGeneMSeq copy() {
		return new BitGeneMSeq(array.copy());
	}

	@Override
	public BitGeneISeq toISeq() {
		return new BitGeneISeq(array.seal());
	}

	static BitGeneMSeq of(final byte[] genes, final int length) {
		return new BitGeneMSeq(Array.of(BitGeneStore.of(genes, length)));
	}

	static BitGeneMSeq of(final Array<BitGene> array) {
		return new BitGeneMSeq(array);
	}

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version !__version__!
 */
final class BitGeneISeq extends ArrayISeq<BitGene> {
	private static final long serialVersionUID = 1L;

	// Primary constructor.
	BitGeneISeq(final Array<BitGene> array) {
		super(array);
		assert array.store() instanceof BitGeneStore;
	}

	void copyTo(final byte[] array) {
		final BitGeneStore store = (BitGeneStore)this.array.store();
		System.arraycopy(store.array, 0, array, 0, store.array.length);
	}

	@Override
	public BitGeneMSeq copy() {
		return BitGeneMSeq.of(array.copy());
	}

	static BitGeneISeq of(final byte[] genes, final int length) {
		return new BitGeneISeq(Array.of(BitGeneStore.of(genes, length)).seal());
	}

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version !__version__!
 */
final class BitGeneStore implements Array.Store<BitGene>, Serializable {
	private static final long serialVersionUID = 1L;

	final byte[] array;
	final int length;

	// Primary constructor.
	private BitGeneStore(final byte[] array, final int length) {
		this.array = requireNonNull(array);
		this.length = require.nonNegative(length);
	}

	@Override
	public BitGene get(final int index) {
		return BitGene.of(bit.get(array, index));
	}

	@Override
	public void set(final int index, final BitGene value) {
		bit.set(array, index, value.booleanValue());
	}

	@Override
	public BitGeneStore copy(final int from, final int until) {
		return new BitGeneStore(bit.copy(array, from, until), until - from);
	}

	@Override
	public int length() {
		return length;
	}


	static BitGeneStore of(final byte[] array, final int length) {
		return new BitGeneStore(array, length);
	}

	static BitGeneStore ofLength(final int length) {
		return new BitGeneStore(bit.newArray(length), length);
	}

}
