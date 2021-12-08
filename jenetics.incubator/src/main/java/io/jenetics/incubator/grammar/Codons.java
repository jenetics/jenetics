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
package io.jenetics.incubator.grammar;

import java.util.concurrent.atomic.AtomicInteger;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.internal.util.Bits;
import io.jenetics.util.BaseSeq;

/**
 * Represents a mapping of a chromosome to a symbol index.
 */
public final class Codons implements SymbolIndex {

	private final int[] _values;
	private final AtomicInteger _pos = new AtomicInteger(0);

	/**
	 * Create a new codons object from the given {@code int[]} array.
	 *
	 * @param values int symbol index mapping array of the codons object
	 * @throws IllegalArgumentException if the given {@code values} array is
	 *         empty
	 */
	private Codons(final int[] values) {
		if (values.length == 0) {
			throw new IllegalArgumentException("Index mappings must not be empty.");
		}
		_values = values.clone();
	}

	@Override
	public int next(final int bound) {
		if (bound < 1) {
			throw new IllegalArgumentException(
				"Bound is smaller than 1: " + bound
			);
		}

		final int index = _pos.getAndUpdate(x -> (x + 1)%_values.length);
		return _values[index]%bound;
	}

	/**
	 * Creates a new <em>codons</em> object from the given bit-genes. The
	 * genes is split into 8-bit chunks and converted into an unsigned
	 * {@code int[]} array.
	 *
	 * @param genes the genes used for creating the codons object
	 * @return a new <em>codons</em> object
	 */
	public static Codons ofBitGenes(final BaseSeq<BitGene> genes) {
		if (genes instanceof BitChromosome ch) {
			return ofBytes(ch.toByteArray());
		} else {
			return ofBytes(toByteArray(genes));
		}
	}

	private static Codons ofBytes(final byte[] bytes) {
		final var values = new int[bytes.length];
		for (int i = 0; i < values.length; ++i) {
			values[i] = Byte.toUnsignedInt(bytes[i]);
		}

		return new Codons(values);
	}

	static byte[] toByteArray(final BaseSeq<BitGene> genes) {
		final byte[] bytes = Bits.newArray(genes.length());
		for (int i = 0; i < genes.length(); ++i) {
			if (genes.get(i).booleanValue()) {
				Bits.set(bytes, i);
			}
		}

		return bytes;
	}

	/**
	 * Creates a new <em>codons</em> object from the given int-genes.
	 *
	 * @param genes the genes used for creating the codons object
	 * @return a new <em>codons</em> object
	 */
	public static Codons ofIntegerGenes(final BaseSeq<IntegerGene> genes) {
		if (genes instanceof IntegerChromosome ich) {
			return new Codons(ich.toArray());
		} else {
			final var values = new int[genes.length()];
			for (int i = 0; i < genes.length(); ++i) {
				values[i] = genes.get(i).intValue();
			}
			return new Codons(values);
		}
	}

	/**
	 * Create a new codons object from the given {@code int[]} array.
	 *
	 * @param values int symbol index mapping array of the codons object
	 * @return a new codons object from the given {@code int[]} array
	 * @throws IllegalArgumentException if the given {@code values} array is
	 *         empty
	 */
	public static Codons ofIntArray(final int[] values) {
		return new Codons(values);
	}

}
