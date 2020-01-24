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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.util.Objects;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 4.0
 */
abstract class VariableChromosome<G extends Gene<?, G>>
	extends AbstractChromosome<G>
{
	private static final long serialVersionUID = 1L;

	private final IntRange _lengthRange;

	/**
	 * Create a new {@code VariableChromosome} from the given {@code genes}
	 * and the allowed length range of the chromosome.
	 *
	 * @param genes the genes that form the chromosome.
	 * @param lengthRange the allowed length range of the chromosome
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty or doesn't match with the allowed length range.
	 * @throws IllegalArgumentException if the minimum or maximum of the range
	 *         is smaller or equal zero
	 * @throws IllegalArgumentException if the given range size is zero
	 */
	VariableChromosome(
		final ISeq<? extends G> genes,
		final IntRange lengthRange
	) {
		super(genes);
		_lengthRange = checkLengthRange(lengthRange, genes.size());
	}

	static IntRange checkLengthRange(final IntRange lengthRange, final int length) {
		requireNonNull(lengthRange);
		if (lengthRange.min() <= 0) {
			throw new IllegalArgumentException(format(
				"Minimum length must be positive: %d", lengthRange.min()
			));
		}
		if (lengthRange.max() <= 0) {
			throw new IllegalArgumentException(format(
				"Maximum length must be positive: %d", lengthRange.max()
			));
		}
		if (lengthRange.size() <= 0) {
			throw new IllegalArgumentException(format(
				"Maximal length must be positive: %d", lengthRange.size()
			));
		}
		if (length < lengthRange.min() ||
			length >= lengthRange.max())
		{
			throw new IllegalArgumentException(format(
				"Number of genes (%d) not within the allowed range: %s",
				length,
				lengthRange
			));
		}

		return lengthRange;
	}

	/**
	 * Return the allowed length range of the chromosome. The minimum value of
	 * the range is included and the maximum value is excluded.
	 *
	 * @return the allowed length range of the chromosome
	 */
	public IntRange lengthRange() {
		return _lengthRange;
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), hash(_lengthRange));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj != null &&
			getClass() == obj.getClass() &&
			Objects.equals(_lengthRange, ((VariableChromosome)obj)._lengthRange) &&
			super.equals(obj);
	}

}
