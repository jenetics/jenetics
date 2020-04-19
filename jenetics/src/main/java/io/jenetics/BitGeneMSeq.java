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
package io.jenetics;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Comparator;

import io.jenetics.internal.collection.Array;
import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.ArrayMSeq;
import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 6.0
 */
final class BitGeneMSeq extends ArrayMSeq<BitGene> {

	private static final long serialVersionUID = 1L;

	// Primary constructor.
	private BitGeneMSeq(final Array<BitGene> array) {
		super(array);
		assert array.store() instanceof BitGeneStore;
	}

	@Override
	public void swap(final int i, final int j) {
		array.checkIndex(i);
		array.checkIndex(j);
		array.copyIfSealed();

		final byte[] bytes = ((BitGeneStore)array.store()).array;
		final boolean temp = Bits.get(bytes, i);
		Bits.set(bytes, i, Bits.get(bytes, j));
		Bits.set(bytes, j, temp);
	}

	@Override
	public void swap(
		final int start, final int end,
		final MSeq<BitGene> other, final int otherStart
	) {
		if (other instanceof BitGeneMSeq) {
			checkIndex(start, end, otherStart, other.length());
			final var otherMSeq = (BitGeneMSeq)other;
			final var thisStore = (BitGeneStore)array.store();
			final var otherStore = (BitGeneStore)otherMSeq.array.store();

			array.copyIfSealed();
			otherMSeq.array.copyIfSealed();
			thisStore.swap(start, end, otherStore, otherStart);
		} else {
			super.swap(start, end, other, otherStart);
		}
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.4
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

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.4
 */
final class BitGeneStore implements Array.Store<BitGene>, Serializable {
	private static final long serialVersionUID = 1L;

	final byte[] array;
	final int length;

	// Primary constructor.
	private BitGeneStore(final byte[] array, final int length) {
		this.array = requireNonNull(array);
		this.length = Requires.nonNegative(length);
	}

	@Override
	public BitGene get(final int index) {
		return BitGene.of(Bits.get(array, index));
	}

	@Override
	public void sort(
		final int from, final int until, final Comparator<? super BitGene> comparator
	) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(final int index, final BitGene value) {
		Bits.set(array, index, value.booleanValue());
	}

	void swap(
		final int start, final int end,
		final BitGeneStore other, final int otherStart
	) {
		Bits.swap(array, start, end, other.array, otherStart);
	}

	@Override
	public BitGeneStore copy(final int from, final int until) {
		return new BitGeneStore(Bits.copy(array, from, until), until - from);
	}

	@Override
	public BitGeneStore newInstance(final int length) {
		return ofLength(length);
	}

	@Override
	public int length() {
		return length;
	}


	static BitGeneStore of(final byte[] array, final int length) {
		return new BitGeneStore(array, length);
	}

	static BitGeneStore ofLength(final int length) {
		return new BitGeneStore(Bits.newArray(length), length);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.BIT_GENE_STORE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(length, out);
		writeInt(array.length, out);
		out.write(array);
	}

	static BitGeneStore read(final DataInput in) throws IOException {
		final int length = readInt(in);
		final byte[] array = new byte[readInt(in)];

		return new BitGeneStore(array, length);
	}

}
